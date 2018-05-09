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
package com.jaspersoft.jasperserver.api.security.validators;

/**
 *
 */
public class ValidatorRuleImpl implements ValidatorRule
{
    private String paramValidationKey;
    private String valueValidationKey;
    private int maxLength;
    private boolean allowNull;
    private String context;
    private boolean isBlacklistRule = false;

    /**
     * A Validatorule instance is not valid unless all the arguments are set.
     *
     * @param paramValidationKey For the request parameter, this is the validation regex key found in validation.properties.
     * @param valueValidationKey For the request parameter's value, this is the validation regex key found in validation.properties.
     * @param maxLength          For the request parameter's value, this is the maximum length that will be allowed.
     * @param allowNull          For the request parameter's value, true means null is a valid possibility.
     * @param context            The context is what will be logged if an Exception is thrown by the ESAPI code.
     *
     * @throws ValidatorRuleException If the constructor is not properly populated.
     */
    public ValidatorRuleImpl(String paramValidationKey, String valueValidationKey, int maxLength, boolean allowNull, String context) {
        if (valueValidationKey != null && valueValidationKey != null && maxLength > 0 && context != null) {
            this.paramValidationKey = paramValidationKey;
            this.valueValidationKey = valueValidationKey;

            if (valueValidationKey.startsWith("!")) {
                this.isBlacklistRule = true;
                this.valueValidationKey = valueValidationKey.substring(1);
            }

            this.maxLength = maxLength;
            this.allowNull = allowNull;
            this.context = context;
        }
        else {
            throw new ValidatorRuleException();
        }

    }

    public ValidatorRuleImpl(ValidatorRule rule) {
        this(rule.getParamValidationKey(), rule.getValueValidationKey(), rule.getMaxLength(),
            rule.isAllowNull(),rule.getContext());
        this.isBlacklistRule = rule.isBlacklistRule();
    }

    /**
     * @inheritDoc
     */
    public String getParamValidationKey()
    {
        return paramValidationKey;
    }

    /**
     * @inheritDoc
     */
    public String getValueValidationKey()
    {
        return valueValidationKey;
    }

    /**
     * @inheritDoc
     */
    public int getMaxLength()
    {
        return maxLength;
    }

    /**
     * @inheritDoc
     */
    public boolean isAllowNull()
    {
        return allowNull;
    }

    /**
     * @inheritDoc
     */
    public String getContext()
    {
        return context;
    }

    /**
     * @inheritDoc
     */
    public boolean isBlacklistRule() {
        return isBlacklistRule;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String SPACER = "][";
        sb.append("["+paramValidationKey).append(SPACER);
        sb.append(valueValidationKey).append(SPACER);
        sb.append(maxLength).append(SPACER);
        sb.append(allowNull).append(SPACER);
        sb.append(context).append(SPACER);
        sb.append(isBlacklistRule ? "blacklist" : "whitelist").append("]");

        return sb.toString();
    }

}
