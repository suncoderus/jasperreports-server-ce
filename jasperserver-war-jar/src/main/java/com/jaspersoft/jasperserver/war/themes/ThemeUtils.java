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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Andrew Sokolnikov
 * Date: 1/12/13
 */
public class ThemeUtils {

    public interface ResourceInfo {
        public String getTenantId();
        public String getPathRelativeToTenant();
        public String getThemeName();
        public String getPathRelativeToTheme();
    }

    private static Log log = LogFactory.getLog(ThemeUtils.class);

    private ConfigurationBean configurationBean;

    /**
     * Pattern to find a tenant and possibly theme that owns the resource
     * Any repository URI has to match the whole pattern, regardless of the tenant and theme belonging, otherwise URI is invalid.
     * If matches, the matcher will find 4 groups
     * 1 : tenantId of the tenant that contains the resource, or null if in root tenant
     * 2 : resource path relative to parent tenant
     * 3 : theme name of a theme that contains the resource, or null if the resource is not a part of a theme
     * 4 : resource path relative to the theme; if (3) is null, (4) becomes equal to (2)
     *
     * We will set the value in the setConfigurationBean because we need "/themes" folder name which is configurable
     */
    private Pattern patternToFindTenantAndThemeByURI;//=Pattern.compile("^(?:/organizations/([^/]+))*((?:/themes/([^/]+))?(/.*))");
    private static final int TENANT_ID_GROUP = 1;
    private static final int TENANT_RELATIVE_URI_GROUP = 2;
    private static final int THEME_NAME_GROUP = 3;
    private static final int THEME_RELATIVE_URI_GROUP = 4;

    public ResourceInfo getResourceInfo(String resourceUri) {
        Matcher m = patternToFindTenantAndThemeByURI.matcher(resourceUri);
        if (m.matches()) {
            final String tenant = m.group(TENANT_ID_GROUP);
            final String pathRelTenant = m.group(TENANT_RELATIVE_URI_GROUP);
            final String theme = m.group(THEME_NAME_GROUP);
            final String pathRelTheme = (theme != null) ? m.group(THEME_RELATIVE_URI_GROUP) : null;

            return new ResourceInfo() {
                @Override
                public String getTenantId() {
                    return tenant;
                }

                @Override
                public String getPathRelativeToTenant() {
                    return pathRelTenant;
                }

                @Override
                public String getThemeName() {
                    return theme;
                }

                @Override
                public String getPathRelativeToTheme() {
                    return pathRelTheme;
                }
            };

        } else {
            log.error("Invalid URI : " + resourceUri);
            throw new JSException("Invalid URI : " + resourceUri);
        }
    }

    public ConfigurationBean getConfigurationBean() {
        return configurationBean;
    }

    public void setConfigurationBean(ConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
        patternToFindTenantAndThemeByURI =
                Pattern.compile("^(?:/" + TenantService.ORGANIZATIONS + "/([^/]+))*((?:" +
                        configurationBean.getThemeFolderName() + "/([^/]+))?(/.*))");
    }
}
