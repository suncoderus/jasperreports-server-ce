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

package com.jaspersoft.jasperserver.export;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Import input metadata.
 * properties contain properties from index.xml file.
 * brokenDependencies contain list of broken dependencies from import input data.
 *
 * @author askorodumov
 * @version $Id$
 */
public class ImportInputMetadata {
    private Map<String, String> properties = new HashMap<String, String>();
    private Set<String> brokenDependencies;

    public Set<String> getBrokenDependencies() {
        return brokenDependencies;
    }

    public void setBrokenDependencies(Set<String> brokenDependencies) {
        this.brokenDependencies = brokenDependencies;
    }

    public Set<String> getPropertyNames() {
        return new HashSet<String>(properties.keySet());
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }
}
