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

/**
 * @author fpang
 * @since Oct 10, 2014
 * @version $Id$
 *
 */

package com.jaspersoft.jasperserver.api.common.util.diagnostic;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author fpang
 *
 */
public class DiagnosticSnapshotPropertyHelper
{
	public final static String ATTRIBUTE_IS_DIAG_SNAPSHOT = "isDiagSnapshot";

	public static boolean isDiagSnapshotSet(Map<String, Object> map)
	{
		return (map.get(ATTRIBUTE_IS_DIAG_SNAPSHOT) != null);
	}
	
	public static boolean isDiagSnapshotSet(Properties props)
	{
		String val = props.getProperty(ATTRIBUTE_IS_DIAG_SNAPSHOT);
		return Boolean.valueOf(val);
	}

	public static boolean isDiagSnapshotSet(List<?> attrs) {
		return attrs != null && attrs.contains(ATTRIBUTE_IS_DIAG_SNAPSHOT);
	}

}
