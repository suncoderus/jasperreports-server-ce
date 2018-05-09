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

package com.jaspersoft.jasperserver.dto.common;

/**
 * Defines strategy with broken dependencies
 *
 * @author askorodumov
 * @version $Id$
 */
public enum BrokenDependenciesStrategy {
    FAIL("fail"), SKIP("skip"), INCLUDE("include");

    private final String label;

    BrokenDependenciesStrategy(String label) {
        this.label = label;
    }

    public static BrokenDependenciesStrategy parseString(String value) {
        for (BrokenDependenciesStrategy strategy : BrokenDependenciesStrategy.values()) {
            if (strategy.name().equalsIgnoreCase(value)) {
                return strategy;
            }
        }

        return FAIL; // default value
    }

    public String getLabel() {
        return label;
    }
}
