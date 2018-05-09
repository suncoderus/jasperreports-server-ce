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
package com.jaspersoft.jasperserver.core.util.validators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.Pattern;

/**
 * Simple RegExp validator
 *
 * @author schubar
 */
public class RegExpValidator<T> implements InputValidator<T> {

    private static final Log log = LogFactory.getLog(RegExpValidator.class);

    private Pattern pattern;

    @Override
    public boolean isValid(T t) {
        if (t == null) {
            return false;
        }

        if (this.getPattern() == null) {
            return true;
        }

        boolean isValid = this.getPattern().matcher(t.toString()).matches();
        if (!isValid && log.isDebugEnabled()) { log.debug(String.format("Email address %s not valid.", t.toString())); }

        return isValid;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
