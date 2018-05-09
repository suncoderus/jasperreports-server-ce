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

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeGroup;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.mt.beans.TenantBean;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import java.util.*;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: TenantExporter.java 15533 2009-01-14 18:34:04Z lucian $
 */
public class TenantExporter extends BaseExporterModule {
	private static final Log log = LogFactory.getLog(TenantExporter.class);

	protected boolean exportTenants;

	protected Queue<String> tenantIdQueue;
	protected TenantModuleConfiguration moduleConfiguration;

	public void init(ExporterModuleContext moduleContext) {
		super.init(moduleContext);
		
		exportTenants = exportEverything ||
				hasParameter(moduleConfiguration.getTenantsArgument()) ||
				hasParameter(ImportExportServiceImpl.ORGANIZATION);

		
		if (log.isDebugEnabled()) {
			log.debug("Exporting tenants: " + exportTenants);
		}
		
		tenantIdQueue = new LinkedList<String>();
	}

	@Override
	protected boolean isToProcess()	{
		return exportTenants ||
				hasParameter(includeSettingsArg) ||
				hasParameter(includeAttributes);
	}

	public void process() {
		mkdir(moduleConfiguration.getTenantsDirectory());
	
		String rootTenantId = moduleConfiguration.getTenantExportConfiguration().getRootTenantId();
		Tenant rootTenant = getTenantService().getTenant(executionContext, rootTenantId);
		if (rootTenant == null) {
			commandOut.info("Root tenant \"" + rootTenantId + "\" not found, skipping tenants");
		} else {
			processTenant(rootTenant);
		}
	}

	protected void processTenant(Tenant tenant) {
		commandOut.info("Exporting tenant " + tenant.getId()
				+ " (" + tenant.getTenantUri() + ")");

		TenantBean tenantBean = new TenantBean();
		tenantBean.copyFrom(tenant);
		tenantBean.setAttributes(prepareAttributesBeans(getProfileAttributes(tenant)));

		// serialize the tenant to XML
		serialize(tenantBean,
				moduleConfiguration.getTenantsDirectory(),
				getTenantFileName(tenant),
				moduleConfiguration.getTenantSerializer());

		// add an entry to the export index.xml
		addTenantIndexEntry(tenant);

		// add the tenant to the queue to process subtenants
		tenantIdQueue.add(tenant.getId());
	}

	protected String getTenantFileName(Tenant tenant) {
		return tenant.getId() + ".xml";
	}

	protected void addTenantIndexEntry(Tenant tenant) {
		Element indexElement = getIndexElement();
		Element tenantElement = indexElement.addElement(
				moduleConfiguration.getTenantIndexElement());
		tenantElement.addText(tenant.getId());
	}

	public TenantModuleConfiguration getModuleConfiguration() {
		return moduleConfiguration;
	}

	public void setModuleConfiguration(TenantModuleConfiguration moduleConfiguration) {
		this.moduleConfiguration = moduleConfiguration;
	}

	private List<ProfileAttribute> getProfileAttributes(Tenant tenant) {
		AttributesSearchCriteria searchCriteria = new AttributesSearchCriteria.Builder()
				.build();
		Set<String> groups = new HashSet<String>();

		groups.add(ProfileAttributeGroup.CUSTOM.toString());
		if (exportEverything || hasParameter(includeSettingsArg)) {
			groups.add(ProfileAttributeGroup.CUSTOM_SERVER_SETTINGS.toString());
		}

		searchCriteria.setGroups(groups);
		return moduleConfiguration.getAttributeService()
				.getProfileAttributesForPrincipal(executionContext, tenant, searchCriteria).getList();
	}
}
