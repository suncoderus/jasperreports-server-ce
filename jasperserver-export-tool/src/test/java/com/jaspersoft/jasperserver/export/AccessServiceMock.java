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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;
import com.jaspersoft.jasperserver.api.logging.access.service.impl.AccessService;
import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Zakhar.Tomchenco
 * Date: 7/25/12
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccessServiceMock implements AccessService {

    public void purgeAccessEvents() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<AccessEvent> getAllEvents(int firstResult, int maxResults) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getAccessEventsCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void saveEvent(LoggableEvent loggableEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void saveEvents(List<LoggableEvent> loggableEvents) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
