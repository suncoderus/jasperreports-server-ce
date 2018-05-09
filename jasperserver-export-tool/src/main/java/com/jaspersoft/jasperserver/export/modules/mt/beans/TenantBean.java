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

package com.jaspersoft.jasperserver.export.modules.mt.beans;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.ProfileAttributeBean;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Voloyda Sabadosh
 * @version $Id$
 */
public class TenantBean {
    private String id = null;
    private String alias = null;
    private String parentId = null;
    private String tenantName = null;
    private String tenantDesc = null;
    private String tenantNote = null;
    private String tenantUri = null;
    private String tenantFolderUri = null;
    private String theme = null;
    private ProfileAttributeBean[] attributes;

    public void copyFrom(Tenant tenant) {
        setId(tenant.getId());
        setAlias(tenant.getAlias());
        setParentId(tenant.getParentId());
        setTenantName(tenant.getTenantName());
        setTenantDesc(tenant.getTenantDesc());
        setTenantNote(tenant.getTenantNote());
        setTenantUri(tenant.getTenantUri());
        setTenantFolderUri(tenant.getTenantFolderUri());
        setTheme(tenant.getTheme());
    }

    public void copyTo(Tenant tenant, ImporterModuleContext context) {
        //Root Tenant from import input
        String sourceTenantId = context.getImportTask()
                .getInputMetadata().getProperty(ImportExportService.ROOT_TENANT_ID);

        if (TenantService.ORGANIZATIONS.equals(sourceTenantId)
                || !StringUtils.equals(sourceTenantId, getId())) {
            tenant.setId(getId());
            tenant.setParentId(getParentId());
            if (getAlias() != null) {
                tenant.setAlias(getAlias());
            }
            tenant.setTenantName(getTenantName());
        }

        tenant.setTenantDesc(getTenantDesc());
        tenant.setTenantNote(getTenantNote());
        tenant.setTenantUri(getTenantUri());
        tenant.setTenantFolderUri(getTenantFolderUri());

        if (context.getImportTask().getParameters().hasParameter(ImportExportService.SKIP_THEMES)) {
            if (!StringUtils.equals(getTheme(), tenant.getTheme())) {
                if (StringUtils.isBlank(tenant.getTheme())) {
                    tenant.setTheme(ImportExportService.DEFAULT_THEME_NAME);
                }
            }
        } else if (getTheme() != null) {
            tenant.setTheme(getTheme());
        }

        if (TenantService.ORGANIZATIONS.equals(sourceTenantId)
                || !StringUtils.equals(sourceTenantId, getId())) {
            if (!context.getNewGeneratedTenantIds().isEmpty()) {
                handleNewTenantIds(tenant, context);
            }
        }
    }

    private void handleNewTenantIds(Tenant tenant, ImporterModuleContext context) {
        //Root Tenant from import input
        String sourceTenantId = context.getImportTask()
                .getInputMetadata().getProperty(ImportExportService.ROOT_TENANT_ID);

        String originalId = tenant.getId();
        tenant.setId(TenantStrHolderPattern.TENANT_ID
                .replaceWithNewTenantIds(context.getNewGeneratedTenantIds(), tenant.getId()));

        if (originalId != null && !originalId.equals(tenant.getId())) {
            if (!originalId.equals(sourceTenantId)) {
                tenant.setAlias(tenant.getId());
                tenant.setTenantName(tenant.getAlias());
            }
        }

        tenant.setParentId(TenantStrHolderPattern.TENANT_ID
                .replaceWithNewTenantIds(context.getNewGeneratedTenantIds(), tenant.getParentId()));
        tenant.setTenantUri(TenantStrHolderPattern.TENANT_URI
                .replaceWithNewTenantIds(context.getNewGeneratedTenantIds(), tenant.getTenantUri()));
        tenant.setTenantFolderUri(TenantStrHolderPattern.TENANT_FOLDER_URI
                .replaceWithNewTenantIds(context.getNewGeneratedTenantIds(), tenant.getTenantFolderUri()));
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantDesc() {
        return tenantDesc;
    }

    public void setTenantDesc(String tenantDesc) {
        this.tenantDesc = tenantDesc;
    }

    public String getTenantNote() {
        return tenantNote;
    }

    public void setTenantNote(String tenantNote) {
        this.tenantNote = tenantNote;
    }

    public String getTenantUri() {
        return tenantUri;
    }

    public void setTenantUri(String tenantUri) {
        this.tenantUri = tenantUri;
    }

    public String getTenantFolderUri() {
        return tenantFolderUri;
    }

    public void setTenantFolderUri(String tenantFolderUri) {
        this.tenantFolderUri = tenantFolderUri;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public ProfileAttributeBean[] getAttributes() {
        return attributes;
    }

    public void setAttributes(ProfileAttributeBean[] attributes) {
        this.attributes = attributes;
    }
}
