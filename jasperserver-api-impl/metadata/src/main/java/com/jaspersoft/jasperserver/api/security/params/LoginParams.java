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
 *  DEPRECATED!
 *  Known login parameters and their validation settings.
 */
public enum LoginParams
{
    USERNAME("j_username","AlphaUnderscore","UsernameValue", 100, false),
    PASSWORD("j_password","AlphaUnderscore","AllOkNotSpace", 100, false),
    ORG_ID("orgId","Alpha","AlphaNumUnderscore", 100, false),
    USER_LOCALE("userLocale","Alpha","AlphaUnderscore", 100, false),
    USER_TIMEZONE("userTimezone","Alpha","AlphaUnderscoreForward", 100, false);

    private String paramName;
    private String paramValidationKey;
    private String valueValidationKey;
    private int maxLength;
    private boolean allowNull;

    /*
     * Custom constructor sets the parameter name, the validation key for the param,
     * the validation key for the value.
     */
    private LoginParams(String paramName, String paramValidationKey, String valueValidationKey, int maxLength, boolean allowNull)
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
