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
package com.jaspersoft.jasperserver.jsp;

/**
 * @author dlitvak
 * @version $id$
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Handles escaping of characters that could be utilized in XSS attacks in html, javascript
 * and css contexts in the browser.
 *
 * So far < > ( ) ; " ' are escaped.
 */
public class EscapeXssScript {

	private static final Map<Character, String> CHAR_ESCAPE_MAP = new HashMap<Character, String>();

	static {
		// < is temporarily replaced by &lt# versus &lt; because ; is on the lhs of the map.
		// This avoids double escaping of ;.
		CHAR_ESCAPE_MAP.put('<', "&lt#");
		CHAR_ESCAPE_MAP.put('>', "&gt#");
//breaks JRS		CHAR_ESCAPE_MAP.put('/', "&#047;");
//breaks JRS		CHAR_ESCAPE_MAP.put('&', "&amp;");
		CHAR_ESCAPE_MAP.put('(', "&#040#");
		CHAR_ESCAPE_MAP.put(')', "&#041#");
		CHAR_ESCAPE_MAP.put(';', "&#059#");
        CHAR_ESCAPE_MAP.put('"', "&#034#");
        CHAR_ESCAPE_MAP.put('\'', "&#039#");
	}


	/**
	 * Escape characters in the string as  mapped in CHAR_ESCAPE_MAP.
	 *
	 * @param rawStr the string to escape
	 * @return the escaped string
	 */
	public static String escape(String rawStr) {
		if (rawStr == null || rawStr.isEmpty())
			return rawStr;

		//Special case for ; in html chars: &lt;  Based on http://www.lookuptables.com/
		rawStr = rawStr.replaceAll("&([A-Za-z1-4]{2,6});","&$1#");
		//Special case for ; in unicode chars
		rawStr = rawStr.replaceAll("&#([0-9A-Fa-f]{1,10});","&#$1#");

		for (Character c : CHAR_ESCAPE_MAP.keySet())
			rawStr = rawStr.replace(c.toString(), CHAR_ESCAPE_MAP.get(c));

		//Special case for ; in unicode chars
		rawStr = rawStr.replaceAll("&#([0-9A-Fa-f]{1,10})#","&#$1;");
		//Special case for ; in html chars: &lt;   Based on http://www.lookuptables.com/
		rawStr = rawStr.replaceAll("&([A-Za-z1-4]{2,6})#","&$1;");

		return rawStr;
	}
}
