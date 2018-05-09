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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public final class NullValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final NullValue singleton = new NullValue();
	
	private NullValue() {
	}
	
	public static NullValue instance() {
		return singleton;
	}
	
	public static boolean isNullValue(Object o) {
		return o instanceof NullValue;
	}

	public static Map replaceWithNullValues(Map map) {
		Map replaced;
		if (map == null) {
			replaced = null;
		} else {
			replaced = new HashMap(map);
			for (Iterator it = replaced.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				if (entry.getValue() == null) {
					entry.setValue(singleton);
				}
			}
		}
		return replaced;
	}

	public static Map restoreNulls(Map map) {
		Map replaced;
		if (map == null) {
			replaced = null;
		} else {
			replaced = new HashMap(map);
			for (Iterator it = replaced.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				if (isNullValue(entry.getValue())) {
					entry.setValue(null);
				}
			}
		}
		return replaced;
	}
	
	public String toString() {
		return "<null value>";
	}
	
}
