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
package com.jaspersoft.jasperserver.export.modules.mt;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.export.ImportTask;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.ProfileAttributeBean;
import com.jaspersoft.jasperserver.export.modules.mt.beans.TenantBean;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceModuleConfiguration;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: TenantImporter.java 16193 2009-03-11 08:18:19Z andy21ca $
 */
public class TenantImporter extends BaseImporterModule
{
	protected static final Log log = LogFactory.getLog(TenantImporter.class);

    protected TenantModuleConfiguration moduleConfiguration;
    protected ResourceModuleConfiguration resourceModuleConfiguration;
    private String updateArg;
    private String includeServerSettings;
    private TenantService tenantServiceUnsecured;
    protected boolean createCopyForNonUniqueTenantIds;

    @Override
    public void init(ImporterModuleContext moduleContext) {
        super.init(moduleContext);
    }

    public List<String> process()
	{
        initProcess();

		for (Iterator it = indexElement.elementIterator(moduleConfiguration.getTenantIndexElement());
				it.hasNext(); ) {
			Element tenantElement = (Element) it.next();
			String tenantId = tenantElement.getText();
            process(tenantId);
		}
        return null;
	}

    protected Boolean isMultitenancyFeatureSupported() {
        /* For CE deciding to make it true or false? */
        return true;
    }

    public TenantBean getTenantBean(String tenantId) {
        return (TenantBean) deserialize(getModuleConfiguration().getTenantsDirectory(),
                getTenantFileName(tenantId),
                getModuleConfiguration().getTenantSerializer());
    }

    private Tenant defineExistingTenant(String tenantId) {
        ImportTask task = importContext.getImportTask();
        TenantService tenantService = getTenantService();
        ExecutionContext executionContext = task.getExecutionContext();

        //Root Tenant from import input
        String sourceTenantId = task.getInputMetadata().getProperty(ImportExportService.ROOT_TENANT_ID);
        //Tenant id we import into
        String destinationTenantId = task.getParameters().getParameterValue(ImportExportServiceImpl.ORGANIZATION);

        // define existingTenant
        Tenant existingTenant;
        if (tenantId.equals(sourceTenantId)
                && StringUtils.isNotBlank(destinationTenantId)
                && !destinationTenantId.equals(TenantService.ORGANIZATIONS)) {

            existingTenant = tenantService.getTenant(executionContext, destinationTenantId);
        } else if (isCreateCopyForNonUniqueTenantIds()
                && StringUtils.isNotBlank(importContext.getNewGeneratedTenantIds().get(tenantId))) {

            existingTenant = null;
        } else {
            existingTenant = tenantService.getTenant(executionContext, tenantId);
        }

        return existingTenant;
    }

    protected Tenant process(String tenantId) {
        Tenant result = null;
        Tenant existingTenant = defineExistingTenant(tenantId);

        if (existingTenant != null) {
            if (hasParameter(getUpdateArg())) {
                TenantBean tenantBean = getTenantBean(tenantId);

                updateTenant(existingTenant, tenantBean);
                result = existingTenant;
            } else {
                commandOut.info("Tenant " + tenantId + " already exists, skipping");
            }
        } else {
            /* Checking if now we have a multi tenant environment. */
            Integer tenantsCount = getTenantService().getNumberOfTenants(executionContext);

            Boolean canImportOrganization = true;

            if (tenantsCount >= 1) {
                /* We already have organizations and at least tenant under it. */
                /* Should check if multitenancy is enabled. */
                if (!isMultitenancyFeatureSupported()) {
                    canImportOrganization = false;
                }
            }

            if (canImportOrganization) {
                if (log.isDebugEnabled()) {
                    log.debug("Deserializing tenant " + tenantId);
                }

                TenantBean tenantBean = getTenantBean(tenantId);

                result = createTenant(tenantBean);
                commandOut.info("Imported tenant " + tenantId);
            } else {
                commandOut.warn("This feature is unavailable under the current license. " +
                        "Please contact support at Jaspersoft.com for help.");
            }
        }

        return result;
    }

    protected String getTenantFileName(String tenantId) {
        return tenantId + ".xml";
    }

    protected TenantService getTenantService() {
        return getModuleConfiguration().getTenantService();
    }

    protected Tenant createTenant(TenantBean tenantBean) {
        Tenant tenant = (Tenant)moduleConfiguration.getObjectMappingFactory().newObject(Tenant.class);
        tenantBean.copyTo(tenant, importContext);
        // In previous versions of JasperServer we did not have tenant alias and theme.
        // So, setting alias with tenant id value and default theme for such versions.
        if (tenant.getAlias() == null) {
            tenant.setAlias(tenant.getId());
        }
        if (tenant.getTheme() == null) {
            tenant.setTheme(getModuleConfiguration().getTenantExportConfiguration().getDefaultThemeName());
        }
        if (putCreatingTenant(tenant, tenantBean)) {
            saveProfileAttributes(moduleConfiguration.getAttributeService(), tenant, tenantBean.getAttributes());
        } else {
            commandOut.warn("TenantImporter.createTenant: " +
                    "Profile attributes were skipped for not created tenant " + tenant.getId());
        }

        return tenant;
    }

    protected boolean putCreatingTenant(Tenant tenant, TenantBean tenantBean) {
        getTenantService().putTenant(executionContext, tenant);
        return true;
    }

    private void updateTenant(Tenant existing, TenantBean newTenant) {
        newTenant.copyTo(existing, importContext);

        getTenantService().putTenant(executionContext, existing);

        ProfileAttributeBean[] profileAttributes = newTenant.getAttributes();
        if (profileAttributes != null &&
                !hasParameter(includeServerSettings) &&
                (newTenant.getId() == null ||
                        newTenant.getId().equals(TenantService.ORGANIZATIONS))) {
            profileAttributes = getCustomProfileAttributes(profileAttributes);
        }

        saveProfileAttributes(moduleConfiguration.getAttributeService(), existing, profileAttributes);

        commandOut.info("Updated tenant " + existing.getId());
    }

    protected ProfileAttributeBean[] getCustomProfileAttributes(ProfileAttributeBean[] profileAttributes) {
        ProfileAttributeService attributeService = moduleConfiguration.getAttributeService();
        List<ProfileAttributeBean> profileAttributesToSave = new ArrayList<ProfileAttributeBean>();
        for (ProfileAttributeBean profileAttributeBean : profileAttributes) {
            // TODO: re-implement without getting changer names, remove "getChangerName" method
            String changerName = attributeService.getChangerName(profileAttributeBean.getName());
            if (changerName == null || changerName.equals("custom")) {
                    profileAttributesToSave.add(profileAttributeBean);
            }
        }

        return profileAttributesToSave.toArray(new ProfileAttributeBean[profileAttributesToSave.size()]);
    }

    public TenantModuleConfiguration getModuleConfiguration() {
        return moduleConfiguration;
    }

    public void setResourceModuleConfiguration(ResourceModuleConfiguration resourceModuleConfiguration) {
        this.resourceModuleConfiguration = resourceModuleConfiguration;
    }

    public void setModuleConfiguration(TenantModuleConfiguration moduleConfiguration) {
        this.moduleConfiguration = moduleConfiguration;
    }

    public String getUpdateArg() {
        return updateArg;
    }

    public void setUpdateArg(String updateArg) {
        this.updateArg = updateArg;
    }

    public void setIncludeServerSettings(String includeServerSettings) {
        this.includeServerSettings = includeServerSettings;
    }

    public TenantService getTenantServiceUnsecured() {
        return tenantServiceUnsecured;
    }

    public void setTenantServiceUnsecured(TenantService tenantServiceUnsecured) {
        this.tenantServiceUnsecured = tenantServiceUnsecured;
    }

    public boolean isCreateCopyForNonUniqueTenantIds() {
        return createCopyForNonUniqueTenantIds;
    }

    public void setCreateCopyForNonUniqueTenantIds(boolean createCopyForNonUniqueTenantIds) {
        this.createCopyForNonUniqueTenantIds = createCopyForNonUniqueTenantIds;
    }
}
