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

import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.logging.access.beans.AccessEventBean;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AccessEventsImporter extends BaseImporterModule {
    private AccessModuleConfiguration accessModuleConfiguration;
    private AccessEventsImportHandler accessEventsImportHandler;
    private ResourceFactory clientClassFactory;
    private String includeAccessEvents;

    private int logAccessEventsInterval;

    public String getIncludeAccessEvents() {
        return includeAccessEvents;
    }

    public void setIncludeAccessEvents(String includeAccessEvents) {
        this.includeAccessEvents = includeAccessEvents;
    }

    public void setAccessModuleConfiguration(AccessModuleConfiguration accessModuleConfiguration) {
        this.accessModuleConfiguration = accessModuleConfiguration;
    }

    public void setAccessEventsImportHandler(AccessEventsImportHandler accessEventsImportHandler) {
        this.accessEventsImportHandler = accessEventsImportHandler;
    }

    public void setClientClassFactory(ResourceFactory clientClassFactory) {
        this.clientClassFactory = clientClassFactory;
    }

    public void setLogAccessEventsInterval(int logAccessEventsInterval) {
        this.logAccessEventsInterval = logAccessEventsInterval;
    }

    public List<String> process() {
        long count = 0;
        long reallyImported = 0;

        if (!hasParameter(includeAccessEvents)) {
            commandOut.info("Skip access events importing");
            return null;
        }
        for (Iterator it = indexElement.elementIterator(accessModuleConfiguration.getAccessEventIndexElement());
                it.hasNext(); ) {
            Element accessEventElement = (Element) it.next();
            String countString = accessEventElement.getText();
            count = Long.valueOf(countString);
        }

        if (count > 0) {
            for (long i = 1; i <= count; i++) {
                boolean isSaved = process(String.valueOf(i));
                if (isSaved) {
                    reallyImported++;
                }

                if (reallyImported % logAccessEventsInterval == 0) {
                    commandOut.info("Next " + logAccessEventsInterval + " accessEvents has been imported");
                }

            }

            commandOut.info(reallyImported + " accessEvents has been imported successfully");
        }
        return null;
    }

    protected String getAccessEventFileName(String accessEventId) {
        return accessEventId + ".xml";
    }

    protected boolean process(String accessEventId)
    {
            AccessEventBean accessEventBean =
                    (AccessEventBean) deserialize(accessModuleConfiguration.getAccessEventsDirectory(),
                        getAccessEventFileName(accessEventId),
                        accessModuleConfiguration.getSerializer());
        AccessEvent accessEvent = (AccessEvent)clientClassFactory.newObject(AccessEvent.class);
        accessEventBean.copyTo(accessEvent, accessEventsImportHandler, importContext);
        if (accessEvent.getUser() != null && accessEvent.getResource() != null) {
            accessModuleConfiguration.getAccessService().saveEvent(accessEvent);
            return true;
        } else {
            return false;
        }
    }

}
