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
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Implementation for {@link ThemeService}.
 *
 * @author asokolnikov
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ThemeServiceImpl implements ThemeService {

    private TenantService tenantService;
    private ConfigurationBean configurationBean;
    private RepositoryService repositoryService;
    private ResourceFactory resourceFactory;

    public String getActiveTheme(ExecutionContext executionContext, String tenantId) {
        Tenant tenant = tenantService.getTenant(executionContext, tenantId);
        if (tenant != null) {
            return tenant.getTheme();
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void setActiveTheme(ExecutionContext executionContext, String tenantId, String themeName) {
        tenantService.setTenantActiveTheme(executionContext, tenantId, themeName);
    }

    public boolean isThemeFolder(ExecutionContext executionContext, String folderUri) {

        String themeFolder = configurationBean.getThemeFolderName() + "/";
        int idx = folderUri.indexOf(themeFolder);
        if (idx < 0) {
            return false;
        }
        // needs to be immediate child
        String themeName = folderUri.substring(idx + themeFolder.length());
        if (themeName.length() == 0 || themeName.contains("/")) {
            return false;
        }

        if (folderUri.startsWith(themeFolder)) {
            // root themes
            return true;
        }

        Tenant tenant = tenantService.getTenantBasedOnRepositoryUri(executionContext, folderUri);
        if (tenant != null) {
            String tenantThemeFolder = (tenant.getTenantFolderUri() + themeFolder).replace("//", "/");
            return folderUri.startsWith(tenantThemeFolder);
        }
        return false;
    }

    public boolean isThemeRootFolder(ExecutionContext executionContext, String folderUri) {

        String themeFolder = configurationBean.getThemeFolderName();
        if (!folderUri.endsWith(themeFolder)) {
            return false;
        }

        if (folderUri.startsWith(themeFolder)) {
            // root themes
            return true;
        }

        Tenant tenant = tenantService.getTenantBasedOnRepositoryUri(executionContext, folderUri);
        if (tenant != null) {
            String tenantThemeFolder = (tenant.getTenantFolderUri() + themeFolder).replace("//", "/");
            return folderUri.equals(tenantThemeFolder);
        }
        return false;
    }

    public boolean isActiveThemeFolder(ExecutionContext executionContext, String folderUri) {
        if (!isThemeFolder(executionContext, folderUri)) {
            return false;
        }
        String themeName = folderUri.substring(folderUri.lastIndexOf("/") + 1);
        Tenant tenant = tenantService.getTenantBasedOnRepositoryUri(executionContext, folderUri);
        if (tenant != null) {
            return themeName.equals(tenant.getTheme());
        }
        try {
            tenant = tenantService.getTenant(executionContext, TenantService.ORGANIZATIONS);
            return themeName.equals(tenant.getTheme());
        } catch (Exception ex) {}
        return false;
    }

    public byte[] getZipedTheme(ExecutionContext executionContext, String folderUri) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        zipThemeFolder(executionContext, folderUri, zos, "");

        zos.close();

        return baos.toByteArray();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void addZippedTheme(ExecutionContext executionContext, String folderUri, String themeName, byte[] themeZip) throws Exception {
        addZippedTheme(executionContext, folderUri, themeName, themeZip, false);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void addZippedTheme(ExecutionContext executionContext, String folderUri, String themeName, byte[] themeZip, boolean overwrite) throws Exception {

        if (!isThemeRootFolder(executionContext, folderUri)) {
            throw new NotThemeRootFolderException();
        }

        String themeUri = folderUri + "/" + themeName;

        if (repositoryService.getFolder(executionContext, themeUri) != null) {
            if (!overwrite) {
                //to notify client-side about folder existing
                throw new ThemeFolderExistsException();
            } else {
                repositoryService.deleteFolder(executionContext, themeUri);
            }
        }
        getOrCreateFolder(executionContext, themeUri);

        ByteArrayInputStream bais = new ByteArrayInputStream(themeZip);
        ZipInputStream zis = new ZipInputStream(bais);

        Set<String> subfolders = new HashSet<String>();

        ZipEntry entry = null;
        while ( (entry = zis.getNextEntry()) != null ) {
            String path = entry.getName();
            String name = path;
            String resFolder = themeUri;

            // find/create subfolders
            if (path.contains("/")) {
                // skip folder entry itself
                if (path.endsWith("/")) {
                    continue;
                }

                name = path.substring(path.lastIndexOf("/") + 1);
                String[] folders = path.split("/");
                for (int i = 0; i < folders.length - 1; i++) {
                    String folder = folders[i];
                    if (folder.length() == 0) {
                        continue;
                    }
                    resFolder += "/" + folder;
                    if (!subfolders.contains(folder)) {
                        Folder f = getOrCreateFolder(executionContext, resFolder);
                        subfolders.add(resFolder);
                    }
                }
            }

            // read data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int read;
            while ( (read = zis.read(buf, 0, 1024)) != -1) {
                baos.write(buf, 0, read);
            }

            // detect type
            String type = (name.toUpperCase().endsWith(".CSS")) ? FileResource.TYPE_CSS : FileResource.TYPE_IMAGE;

            String label = name, desc = name;

            // readExtra (if zip was produced by JasperServer)
            try {
                String jsonStr = new String(entry.getExtra());
                JSONObject json = new JSONObject(jsonStr);
                label = json.getString(LABEL);
                desc = json.getString(DESCRIPTION);
            } catch (Exception ex) {
                // ignore
            }

            // get FileResource from repository or create new if it doesn't exist
            String resourceURI = resFolder + "/" + name;
            FileResource fileResource = (FileResource) repositoryService.getResource(executionContext, resourceURI);
            if (fileResource == null) {
                fileResource = (FileResource) resourceFactory.newResource(executionContext, FileResource.class);
            }

            fileResource.setName(name);
            fileResource.setLabel(label);
            fileResource.setDescription(desc);
            fileResource.setParentFolder(resFolder);
            fileResource.setData(baos.toByteArray());
            fileResource.setFileType(type);

            // store
            repositoryService.saveResource(executionContext, fileResource);
        }

        zis.close();
    }

    protected void zipThemeFolder(ExecutionContext executionContext, String folderUri, ZipOutputStream zos, String basePath) throws IOException {

        FilterCriteria criteria = FilterCriteria.createFilter(FileResource.class);
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(folderUri));
        List resources = repositoryService.loadResourcesList(null, criteria);

        if (resources != null) {
            for (ResourceLookup res : (List<ResourceLookup>) resources) {
                ZipEntry ze = new ZipEntry(basePath + res.getName());
                ze.setTime(res.getUpdateDate().getTime());
                ze.setExtra(readExtraParameters(res).getBytes());
                zos.putNextEntry(ze);

                FileResourceData frd = repositoryService.getResourceData(executionContext, res.getURIString());
                byte[] data = frd.getData();
                zos.write(data);
            }
        }

        List folders = repositoryService.getSubFolders(executionContext, folderUri);
        if (folders != null) {
            for (Folder folder : (List<Folder>) folders) {
                String name = basePath + folder.getName() + "/";
                // Windows Explorer in XP and Vista does not like folder entries,
                // so the following code is commented out.
                // It effectively means we will lose time, label and description for folders
                //ZipEntry ze = new ZipEntry(name);
                //ze.setTime(folder.getUpdateDate().getTime());
                //ze.setExtra(readExtraParameters(folder).getBytes());
                //zos.putNextEntry(ze);

                zipThemeFolder(executionContext, folder.getURIString(), zos, name);
            }
        }

    }

    protected String readExtraParameters(Resource res) {
        JSONObject obj = new JSONObject();
        try {
            obj.put(LABEL, res.getLabel());
            obj.put(DESCRIPTION, res.getDescription());
        } catch (JSONException ex) {}
        return obj.toString();
    }

    protected Folder getOrCreateFolder(ExecutionContext executionContext, String uri) {
        Folder f = repositoryService.getFolder(executionContext, uri);
        if (f == null) {
            int k = uri.lastIndexOf("/");
            String parent = uri.substring(0, k);
            String name = uri.substring(k + 1);
            f = new FolderImpl();
            f.setName(name);
            f.setLabel(name);
            f.setParentFolder(parent);
            repositoryService.saveFolder(executionContext, f);
            f = repositoryService.getFolder(executionContext, uri);
        }
        return f;
    }

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
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

    public ResourceFactory getResourceFactory() {
        return resourceFactory;
    }

    public void setResourceFactory(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }
}
