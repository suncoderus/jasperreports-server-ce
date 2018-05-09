/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.war.themes;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.context.Theme;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the shared application theme cache.
 * It resolves themes for ThemeResolver, and provides resource content for theme servlet.
 * It generates a unique UID for each theme. The UID needs to change each time when
 * theme resource gets added / deleted / updated and enforce update recognition from browser side,
 * regardless of whether resource was renewed (date got newer) or rolled back (date got older).  
 * @author asokolnikov
 */
public class ThemeCache {

    private static Random random = new Random();
    private static Log log = LogFactory.getLog(ThemeCache.class);

    private Map<String, String> name2uid;
    private Map<String, String> uid2name;
    private Map<String, HierarchicalTheme> themeMap;
    private Map<String, ThemeResource> resourceMap;
    private ConfigurationBean configurationBean;
    private RepositoryService repositoryService;
    private TenantService tenantService;
    private ThemeUtils themeUtils;
    private int refreshThemeAfterUpdateInSeconds;
    private RefreshThread themeRefreshThread;
    private Stack<String> updatedURIs;
    private ThemePreLoadListener themePreLoadListener;

    public interface ThemePreLoadListener {
        void onThemeLoad(String themeName);
    }
    public ThemeCache() {
        name2uid = new ConcurrentHashMap<String, String>();
        uid2name = new ConcurrentHashMap<String, String>();
        themeMap = new ConcurrentHashMap<String, HierarchicalTheme>();
        resourceMap = new ConcurrentHashMap<String, ThemeResource>();

        themeRefreshThread = new RefreshThread();
        themeRefreshThread.setDaemon(true);
        themeRefreshThread.start();
        updatedURIs = new Stack<String>();
    }

    public ThemeResource getThemeResource(String webLink) {
        ThemeResource themeResource = resourceMap.get(webLink);
        while (themeResource == null) {
            try {
                // link looks like : themeServletPathPrefix + "/" + uid + "/" + relPath
                int firstSlash = webLink.indexOf("/");
                int secondSlash = webLink.indexOf("/", firstSlash + 1);
                String uid = webLink.substring(firstSlash + 1, secondSlash);
                String name = uid2name.get(uid);
                if (name == null) {
                    break;
                }
                HierarchicalTheme theme = themeMap.get(name);
                if (theme == null) {
                    break;
                }
                Theme parentTheme = theme.getParentTheme();
                if (parentTheme == null) {
                    break;
                }
                String newUid = name2uid.get(parentTheme.getName());
                if (newUid == null) {
                    break;
                }
                webLink = webLink.replace(uid, newUid);
                themeResource = resourceMap.get(webLink);
            } catch (Exception ex) {
                log.debug("Cannot resolve theme element for : " + webLink, ex);
                break;
            }
        }
        if (themeResource == null) {
            log.debug("Cannot resolve theme element for : " + webLink);
        }
        return themeResource;
    }

    protected String getTenantQualifiedThemeName(String themeName) {
        if (themeName.contains(configurationBean.getUserNameSeparator())) {
            // already qualified
            return themeName;
        }
        // recreate the full name including org id
        // example : ocean|organization_1
        String tenantQualifiedThemeName = themeName;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth.getPrincipal() instanceof TenantQualified) {
                String tenantId = ((TenantQualified) auth.getPrincipal()).getTenantId();
                if (tenantId != null && tenantId.length() > 0 && !tenantId.equals(TenantService.ORGANIZATIONS)) { //checking that it is not root. since in external authentication we can pass organizations as root
                    tenantQualifiedThemeName += configurationBean.getUserNameSeparator() + tenantId;
                }
            }
        }
        return tenantQualifiedThemeName;
    }

    public HierarchicalTheme getTheme(String themeName) {
        String tenantQualifiedThemeName = getTenantQualifiedThemeName(themeName); //check what is the value in internal
        return getThemeByQualifiedName(tenantQualifiedThemeName);
    }

    public HierarchicalTheme getThemeByQualifiedName(String tenantQualifiedThemeName) {
        HierarchicalTheme theme = themeMap.get(tenantQualifiedThemeName);
        if (theme == null) {
            //Theme is absent in cache - lock theme loading and call preLoadlistener before loading theme
            synchronized (this) {
                theme = themeMap.get(tenantQualifiedThemeName);
                if (theme!=null) {
                    return theme;
                }
                if (themePreLoadListener != null) {
                    themePreLoadListener.onThemeLoad(tenantQualifiedThemeName);
                }
                String tenantQualifiedDefault = getTenantQualifiedThemeName(configurationBean.getThemeDefaultName());
                theme = createTheme(tenantQualifiedThemeName);
                if (!tenantQualifiedDefault.equals(tenantQualifiedThemeName)) {
                    theme.setParentTheme(new HierarchicalThemeProxy(tenantQualifiedDefault, this));
                }
            }
        }
        
        return theme;
    }

    private HierarchicalTheme createTheme(String tenantQualifiedThemeName) {
        String sep = configurationBean.getUserNameSeparator();
        int sepPos = tenantQualifiedThemeName.indexOf(sep);
        String themeName = (sepPos > 0) ? tenantQualifiedThemeName.substring(0, sepPos) : tenantQualifiedThemeName;
        String themeFolder = configurationBean.getThemeFolderName() + "/" + themeName;

        if (sepPos > 0) { // tenant theme
            String tenantId = tenantQualifiedThemeName.substring(sepPos + 1);
            Tenant tenant = tenantService.getTenant(null, tenantId);
            String tenantFolder = tenant.getTenantFolderUri();
            themeFolder = tenantFolder + themeFolder;
        }

        FilterCriteria filterCriteria = FilterCriteria.createFilter(FileResource.class);
        filterCriteria.addFilterElement(
                FilterCriteria.createAncestorFolderFilter(themeFolder)
        );
        ExecutionContext executionContext = JasperServerUtil.getExecutionContext();
        ResourceLookup[] lookups = repositoryService.findResource(executionContext, filterCriteria);

        final String uid = getNewUID();
        ThemeMessageSource themeMessageSource = new ThemeMessageSource();

        if (lookups != null) {
            int k = themeFolder.length() + 1;
            for (int i = 0; i < lookups.length; i++) {
                ResourceLookup rlu = lookups[i];
                String relPath = rlu.getURIString().substring(k);
                Date lastModified = rlu.getUpdateDate();
                FileResourceData frd = repositoryService.getResourceData(executionContext, rlu.getURIString());
                byte[] data = frd.getData();
                ThemeResource themeResource = new ThemeResource(lastModified, data);

                String webLink = configurationBean.getThemeServletPrefix() + "/" + uid + "/" + relPath;
                resourceMap.put(webLink, themeResource);

                themeMessageSource.addMessage(relPath, webLink);
            }
        }

        HierarchicalTheme theme = new RepositoryFolderTheme(tenantQualifiedThemeName, null,
            new AbstractMessageSource() {
                @Override
                protected MessageFormat resolveCode(String code, Locale locale) {
                    return new MessageFormat(configurationBean.getThemeServletPrefix() + "/" + uid + "/" + code);
                }
            }
        );
        
        themeMap.put(tenantQualifiedThemeName, theme);
        uid2name.put(uid, tenantQualifiedThemeName);
        name2uid.put(tenantQualifiedThemeName, uid);

        return theme;
    }

    public boolean isThemeResource(String resourceURI) {
        ThemeUtils.ResourceInfo info = themeUtils.getResourceInfo(resourceURI);
        return info.getThemeName() != null;
    }

    public void onThemeResourceChanged(String resourceURI) {
        // make sure it was actually theme resource
        if (!isThemeResource(resourceURI)) {
            return;
        }

        // Ok, it is under themes
        updatedURIs.push(resourceURI);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, getRefreshThemeAfterUpdateInSeconds());
        themeRefreshThread.setNextRefresh(cal.getTime());
        if (log.isTraceEnabled()) {
            log.trace("ThemeCache.onThemeResourceChanged : " + resourceURI);
        }
    }

    private void refreshThemes() {
        // find out all themes to refresh
        Set<String> themesToUpdate = new HashSet<String>();
        while (!updatedURIs.isEmpty()) {
            String resourceURI = updatedURIs.pop();

            ThemeUtils.ResourceInfo info = themeUtils.getResourceInfo(resourceURI);
            String resTheme = info.getThemeName();
            String resTenant = info.getTenantId();
            String tenantQualifiedTheme = resTheme;
            if (resTenant != null) {
                tenantQualifiedTheme += configurationBean.getUserNameSeparator() + resTenant;
            }

            themesToUpdate.add(tenantQualifiedTheme);
        }

        if (log.isTraceEnabled()) {
            log.trace("ThemeCache.refreshThemes");
        }
        for (String tenantQualifiedTheme : themesToUpdate) {
            HierarchicalTheme theme = themeMap.get(tenantQualifiedTheme);
            if (theme != null) {
                cleanTheme(tenantQualifiedTheme);
            }
            if (log.isTraceEnabled()) {
                log.trace("  " + tenantQualifiedTheme);
            }
        }
    }

    private void cleanTheme(String themeName) {
        String uid = name2uid.get(themeName);
        themeMap.remove(themeName);
        name2uid.remove(themeName);
        if (uid != null) {
            uid2name.remove(uid);
            // clean up the resource map
            String webLinkPrefix = configurationBean.getThemeServletPrefix() + "/" + uid;
            for (Iterator<String> iter = resourceMap.keySet().iterator(); iter.hasNext(); ) {
                String webLink = iter.next();
                if (webLink.startsWith(webLinkPrefix)) {
                    iter.remove();
                }
            }
        }
    }

    private synchronized String getNewUID() {
        return Integer.toHexString(random.nextInt()).toUpperCase();
    }

    class RefreshThread extends Thread {
        private Date nextRefresh = new Date();
        private boolean active = false;
        public void setNextRefresh(Date date) {
            nextRefresh = date;
            active = true;
            this.interrupt();
        }
        public void run() {
            while (true) {
                try {
                    if (active) {
                        if (new Date().after(nextRefresh)) {
                            try {
                                refreshThemes();
                            } catch (Throwable t) {
                                log.error("Cannot refresh themes!", t);
                            }
                            active = false;
                        } else {
                            try {
                                sleep(1000);
                            } catch (Exception ex) {
                                // ignore
                            }
                        }
                    } else {
                        try {
                            synchronized (this) {
                                wait();
                            }
                        } catch (Exception ex) {
                            // ignore
                        }
                    }
                } catch (Throwable t) {
                    // keep the thread alive no mater what
                    try { sleep(1000); } catch (Exception ex) {}
                }
            }
        }
    }

    public ConfigurationBean getConfigurationBean() {
        return configurationBean;
    }

    public void setConfigurationBean(ConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public int getRefreshThemeAfterUpdateInSeconds() {
        return refreshThemeAfterUpdateInSeconds;
    }

    public void setRefreshThemeAfterUpdateInSeconds(int refreshThemeAfterUpdateInSeconds) {
        this.refreshThemeAfterUpdateInSeconds = refreshThemeAfterUpdateInSeconds;
    }

    public ThemeUtils getThemeUtils() {
        return themeUtils;
    }

    public void setThemeUtils(ThemeUtils themeUtils) {
        this.themeUtils = themeUtils;
    }

    public void setThemePreLoadListener(ThemePreLoadListener themePreLoadListener) {
        this.themePreLoadListener = themePreLoadListener;
    }
}
