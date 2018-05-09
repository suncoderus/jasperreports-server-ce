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

package com.jaspersoft.jasperserver.war.cascade.utils;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValuesInformation;

/**
 * Dummy implementation of {@Link ReportInputControlInformation}
 * for tests only.
 */
public class ReportInputControlInformationMock implements ReportInputControlInformation {
    private String promptLabel;
    private String description;
    private String parameterName;
    private Class valueType;
    private Class nestedType;
    private Object defaultValue;
    private ReportInputControlValuesInformation valuesInformation;

    public String getPromptLabel() {
        return promptLabel;
    }

    public void setPromptLabel(String promptLabel) {
        this.promptLabel = promptLabel;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public Class getValueType() {
        return valueType;
    }

    public void setValueType(Class valueType) {
        this.valueType = valueType;
    }

    public Class getNestedType() {
        return nestedType;
    }

    public void setNestedType(Class nestedType) {
        this.nestedType = nestedType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ReportInputControlValuesInformation getReportInputControlValuesInformation() {
        return valuesInformation;
    }

    public void setReportInputControlValuesInformation(ReportInputControlValuesInformation valuesInformation) {
        this.valuesInformation = valuesInformation;
    }
}
