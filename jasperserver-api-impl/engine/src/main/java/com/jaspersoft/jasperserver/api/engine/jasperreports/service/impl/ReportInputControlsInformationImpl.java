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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.io.Serializable;
import java.util.*;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlsInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.impl.QueryParameterInformation;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportInputControlsInformationImpl implements
		ReportInputControlsInformation, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String, ReportInputControlInformation> infos = new HashMap<String, ReportInputControlInformation>();
    private Boolean isDiagnostic = false;
	
	public ReportInputControlsInformationImpl() {
	}
	
	public ReportInputControlInformation getInputControlInformation(String name) {
		return infos.get(name);
	}

    public Map<String, Object> getDefaultValuesMap() {
        Map<String, Object> defaultValues = new HashMap<String, Object>();
        for(String icName: this.getControlNames()) {
            ReportInputControlInformation inputControlInfo = getInputControlInformation(icName);
            Object value = inputControlInfo.getDefaultValue();
            if (value instanceof Collection) {
                /* Value is checked for instanceof before assignment */
                @SuppressWarnings("unchecked")
                final Collection<Object> collection = (Collection<Object>) value;
                List valueList = new ArrayList<Object>(collection);
                value = valueList;
            }

            defaultValues.put(icName, value);
        }
        return defaultValues;
    }

    @Override
    public boolean getDiagnosticProperty() {
        return isDiagnostic;
    }

    @Override
    public void setDiagnosticProperty(boolean isDiagnostic) {
        this.isDiagnostic = isDiagnostic;
    }

    public void setInputControlInformation(String name,
			ReportInputControlInformation info) {
		infos.put(name, info);
	}

	public Set<String> getControlNames() {
		return infos.keySet();
	}

}
