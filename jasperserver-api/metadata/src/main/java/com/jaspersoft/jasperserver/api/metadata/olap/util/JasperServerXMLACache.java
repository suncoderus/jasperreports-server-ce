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
package com.jaspersoft.jasperserver.api.metadata.olap.util;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache;

/**
 * Created by IntelliJ IDEA. User: ichan Date: 11/27/12 Time: 2:47 PM
 * 
 */
public class JasperServerXMLACache extends XmlaOlap4jNamedMemoryCache {

	protected static final Log log = LogFactory.getLog(JasperServerXMLACache.class);

    private static String USER = "USER";

    public JasperServerXMLACache() {
		super();
	}

    @Override
    public String setParameters(
            Map<String, String> config,
            Map<String, String> props) {
        if (props.containsKey(XmlaOlap4jNamedMemoryCache.Property.NAME.name()) && config.containsKey(USER)) {
            String refId = props.get(XmlaOlap4jNamedMemoryCache.Property.NAME.name()) + "_" + config.get(USER);
            //Put cache info per authorized user.
            props.put(XmlaOlap4jNamedMemoryCache.Property.NAME.name(), refId);
        }
        return super.setParameters(config, props);
    }

}
