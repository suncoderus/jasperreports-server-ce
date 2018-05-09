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
package com.jaspersoft.jasperserver.api.security.params;

/**
 * DEPRECATED!
 * Known login parameters.
 */
public enum SpringParams
{
    FLOW_ID("_flowId", "AlphaUnderscore", "Alpha", 100, false),
    EVENT_ID("_eventId", "AlphaUnderscore", "Alpha", 100, false),
    FLOW_EXEC_KEY("_flowExecutionKey", "AlphaUnderscore", "FlowExecKey", 100, false);

    private String paramName;
    private String paramValidationKey;
    private String valueValidationKey;
    private int maxLength;
    private boolean allowNull;

    /*
    * Custom constructor sets the parameter name, the validation key for the param,
    * and the validation key for the value.
    */
    private SpringParams(String paramName, String paramValidationKey, String valueValidationKey, int maxLength, boolean allowNull)
    {
        this.paramName = paramName;
        this.paramValidationKey = paramValidationKey;
        this.valueValidationKey = valueValidationKey;
        this.maxLength = maxLength;
        this.allowNull = allowNull;
    }

    public String getParamName()
    {
        return this.paramName;
    }

    public String getValueValidationKey()
    {
        return this.valueValidationKey;
    }

    public String getParamValidationKey()
    {
        return this.paramValidationKey;
    }

    public int getMaxLength()
    {
        return this.maxLength;
    }

    public boolean getAllowNull()
    {
        return this.allowNull;
    }
}
