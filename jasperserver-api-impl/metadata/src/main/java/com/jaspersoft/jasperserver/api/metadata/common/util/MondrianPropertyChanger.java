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

package com.jaspersoft.jasperserver.api.metadata.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jaspersoft.jasperserver.api.common.properties.PropertyChangerAdapter;
import mondrian.olap.MondrianProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eigenbase.util.property.Property;

/**
 * @author sbirney (sbirney@users.sourceforge.net)
 * @version $Id: EditMondrianPropertiesAction.java 8410 2007-05-29 23:34:07Z melih $
 */
public class MondrianPropertyChanger extends PropertyChangerAdapter {

    private static final Log log = LogFactory.getLog(MondrianPropertyChanger.class);

    public void setProperty(String key, String val) {
		log.debug("setting mondrian property: " + key + " - " + val);
        if (val != null) {
            MondrianProperties.instance().setProperty(key, val);
        }
	}

	public String getProperty(String key) {
		return MondrianProperties.instance().getProperty(key);
	}

    public Map<String, String> getProperties() {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        for (Object property : MondrianProperties.instance().getPropertyList()) {
            if (property instanceof Property) {
                properties.put(((Property) property).getPath(), ((Property) property).stringValue());
            }
        }
        return properties;
    }
}