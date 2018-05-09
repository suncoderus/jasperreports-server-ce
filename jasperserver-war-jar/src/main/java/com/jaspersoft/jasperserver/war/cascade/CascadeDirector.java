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
package com.jaspersoft.jasperserver.war.cascade;

import java.io.Serializable;
import java.util.List;

/**
 * CascadeDirector
 * @author jwhang
 * @version $Id$
 * Class to simplify configuration and allow a common access point for future improvements.
 */

public class CascadeDirector implements ControlLogicFacade, Serializable {

    static final long serialVersionUID = 1l;
    private transient Delegator delegator = null;

    //simple pass through initialization.
    public List<EventEnvelope> initialize(String reportUri, List<EventEnvelope> envelopes) throws SecurityException {
		return delegator.initialize(reportUri, envelopes);
    }

    //simple pass through event handling.
    public List<EventEnvelope> handleEvents(String reportUri, List<EventEnvelope> envelopes) throws SecurityException {
        return delegator.handleEvents(reportUri, envelopes);
    }

    //autoPopulation of input controls.
    public List<EventEnvelope> autoPopulate(String reportUri, List<EventEnvelope> envelopes, String lookupKey) throws SecurityException {
        return delegator.autoPopulate(reportUri, envelopes, lookupKey);
    }

    public Delegator getDelegator() {
        return delegator;
    }

    public void setDelegator(Delegator delegator) {
        this.delegator = delegator;
    }

}




