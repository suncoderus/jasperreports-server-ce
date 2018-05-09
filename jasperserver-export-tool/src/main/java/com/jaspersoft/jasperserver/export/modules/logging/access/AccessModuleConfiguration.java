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

import com.jaspersoft.jasperserver.api.logging.access.service.impl.AccessService;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AccessModuleConfiguration {
    private AccessService accessService;
    private ObjectSerializer serializer;
    private String accessEventIndexElement;
    private String accessEventsDirectory;

    public AccessService getAccessService() {
        return accessService;
    }

    public void setAccessService(AccessService accessService) {
        this.accessService = accessService;
    }

    public ObjectSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    public String getAccessEventsDirectory() {
        return accessEventsDirectory;
    }

    public void setAccessEventsDirectory(String accessEventsDirectory) {
        this.accessEventsDirectory = accessEventsDirectory;
    }

    public String getAccessEventIndexElement() {
        return accessEventIndexElement;
    }

    public void setAccessEventIndexElement(String accessEventIndexElement) {
        this.accessEventIndexElement = accessEventIndexElement;
    }
}
