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
package com.jaspersoft.jasperserver.export.modules.logging.access;

import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.logging.access.beans.AccessEventBean;
import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;

import java.util.List;

import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import org.dom4j.Element;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AccessEventsExporter extends BaseExporterModule {
    private AccessModuleConfiguration accessModuleConfiguration;
    private String includeAccessEvents;
    private int maxEventsPerIteration = 50;

    public void setAccessModuleConfiguration(AccessModuleConfiguration accessModuleConfiguration) {
        this.accessModuleConfiguration = accessModuleConfiguration;
    }

    public void setMaxEventsPerIteration(int maxEventsPerIteration) {
        this.maxEventsPerIteration = maxEventsPerIteration;
    }

    public void setIncludeAccessEvents(String includeAccessEvents) {
        this.includeAccessEvents = includeAccessEvents;
    }

    /**
     * Override parent since we would be able
     * to skip exporting access events even if everything is selected
     */
    @Override
    public boolean toProcess() {
        return exportEverything && isToProcess();
    }

    protected boolean isToProcess() {
        return  (rootTenant == null || rootTenant.getId().equals(TenantService.ORGANIZATIONS)) &&
                hasParameter(includeAccessEvents) &&
                !hasParameter(ImportExportServiceImpl.RESOURCE_TYPES) &&
                !hasParameter(ImportExportServiceImpl.SKIP_SUBORGANIZATIONS) &&
                hasAccessEvents();
    }

    protected boolean hasAccessEvents() {
        return accessModuleConfiguration.getAccessService().getAccessEventsCount() > 0;
    }

    public void process() {
        mkdir(accessModuleConfiguration.getAccessEventsDirectory());

        List<AccessEvent> accessEventsBuffer;

        long counter = 0;
        int firstResult = 0;
        accessEventsBuffer = accessModuleConfiguration.getAccessService().getAllEvents(firstResult, maxEventsPerIteration);
        
        while (!accessEventsBuffer.isEmpty()) {
            commandOut.info("Exporting next " + accessEventsBuffer.size()
                    + " accessEvents");

            for (AccessEvent accessEvent: accessEventsBuffer) {
                counter++;
                processAcessEvent(accessEvent, counter);
            }


            firstResult += accessEventsBuffer.size();
            accessEventsBuffer = accessModuleConfiguration.getAccessService().getAllEvents(firstResult, maxEventsPerIteration); 
        }

        commandOut.info(counter + " accessEvents has been exported successfully");
        addAccessEventIndexEntry(counter);
    }

    protected String getAccessEventIndex(AccessEvent accessEvent, long counter) {
        return String.valueOf(counter);
    }

    protected void addAccessEventIndexEntry(long counter) {
        Element indexElement = getIndexElement();
        Element tenantElement = indexElement.addElement(
                accessModuleConfiguration.getAccessEventIndexElement());
        tenantElement.addText(String.valueOf(counter));
    }

    protected void processAcessEvent(AccessEvent accessEvent, long counter) {
        AccessEventBean accessEventBean = new AccessEventBean();
        accessEventBean.copyFrom(accessEvent);
        serialize(accessEventBean,
                accessModuleConfiguration.getAccessEventsDirectory(),
                getAccessEventIndex(accessEvent, counter) + ".xml",
                accessModuleConfiguration.getSerializer());
    }
}
