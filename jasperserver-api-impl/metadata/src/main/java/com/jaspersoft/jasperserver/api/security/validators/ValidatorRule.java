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
 * A rule is used to determine whether or not a value is considered valid
 * for the application.
 *
 * @see "securities.properties" - This holds the rules for each parameter. There is a rule for the parameter's
 * structure, and a rule for the value's structure.  If a rule is violated, an exception (ValidationException or
 * IntrusionException is thrown)
 *
 * @see "validation.properties" - Rules follow regular expression patterns.
 */
public interface ValidatorRule
{
    /**
     * Retrieve the PARAMETER validation key.
     *
     * @return the PARAMETER validation key.
     */
    public String getParamValidationKey();

    /**
     * Retrieve the VALUE validation key.
     *
     * @return  the VALUE validation key.
     */
    public String getValueValidationKey();

    /**
     * Retrieve the MAXIMUM LENGTH for the value being validated.
     *
     * @return  the MAXIMUM LENGTH for the value being validated.
     */
    public int getMaxLength();

    /**
     * Retrieve true if null is valid; otherwise, false.
     *
     * @return true if null is valid; otherwise, false.
     */
    public boolean isAllowNull();


    public String getContext();

    /**
     * Is rule a blacklist one (!RULE): when RULE is satisfied, the input is invalid
     * @return
     */
    public boolean isBlacklistRule();
}
