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
package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValuesInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.QueryParameterDescriptor;

import java.util.List;

/**
 * @author Zakhar Tomchenko
 * @version $Id$
 */
public class QueryParameterInformation implements ReportInputControlInformation {

    private String paramName;
    private Class valueType, nestedType = null;


    public QueryParameterInformation(QueryParameterDescriptor descriptor) throws ClassNotFoundException {
        paramName = descriptor.getParameterName();

        if (descriptor.isCollection()){
            valueType = List.class;
            nestedType = descriptor.getParameterType() == null ? null : Class.forName(descriptor.getParameterType());
        } else {
            valueType = Class.forName(descriptor.getParameterType());
        }
    }

    @Override
    public String getPromptLabel() {
        return paramName;
    }

    @Override
    public String getDescription() {
        return paramName;
    }

    @Override
    public String getParameterName() {
        return paramName;
    }

    @Override
    public Class getValueType() {
        return valueType;
    }

    @Override
    public Class getNestedType() {
        return nestedType;
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public void setDefaultValue(Object value) {

    }

    @Override
    public ReportInputControlValuesInformation getReportInputControlValuesInformation() {
        return null;
    }
}
