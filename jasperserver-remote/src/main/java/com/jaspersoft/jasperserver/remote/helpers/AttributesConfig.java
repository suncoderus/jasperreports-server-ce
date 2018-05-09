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
package com.jaspersoft.jasperserver.remote.helpers;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class AttributesConfig {
    private int maxLengthAttrName = 255;
    private int maxLengthAttrValue = 2000;
    private int maxLengthDescription = 255;

    public int getMaxLengthAttrName() {
        return maxLengthAttrName;
    }

    public void setMaxLengthAttrName(int maxLengthAttrName) {
        this.maxLengthAttrName = maxLengthAttrName;
    }

    public int getMaxLengthAttrValue() {
        return maxLengthAttrValue;
    }

    public void setMaxLengthAttrValue(int maxLengthAttrValue) {
        this.maxLengthAttrValue = maxLengthAttrValue;
    }

    public int getMaxLengthDescription() {
        return maxLengthDescription;
    }

    public void setMaxLengthDescription(int maxLengthDescription) {
        this.maxLengthDescription = maxLengthDescription;
    }
}
