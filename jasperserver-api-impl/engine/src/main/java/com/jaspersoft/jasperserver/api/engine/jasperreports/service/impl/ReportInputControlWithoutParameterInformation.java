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

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValuesInformation;

/**
 * @author andy21ca
 * @version $Id$
 */
public class ReportInputControlWithoutParameterInformation implements
		ReportInputControlInformation, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String name;
    private final String label;
    private final String description;
    private final ReportInputControlValuesInformation valuesInformation;
    
    private Object defaultValue;
	
    public ReportInputControlWithoutParameterInformation(String name, String label, String description,
    		ReportInputControlValuesInformation valuesInformation) {
		this.name = name;
		this.label = label;
        this.description = description;
		this.valuesInformation = valuesInformation;
	}

	@Override
    public String getPromptLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getParameterName() {
        return name;
    }
    
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    @Override
	public void setDefaultValue(Object value) {
		this.defaultValue = value;
	}
	
    @Override
    public Class getValueType() {
        return null;
    }
    
    @Override
    public Class getNestedType() {
        return null;
    }
    
    @Override
    public ReportInputControlValuesInformation getReportInputControlValuesInformation() {
        return valuesInformation;
    }

}
