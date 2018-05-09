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

package com.jaspersoft.jasperserver.export.io;

/**
 * @author lucian
 *
 */
public class AsciiEscapingPathProcessor implements PathProcessor {
	
	private char escapeChar;
	
	public String processPath(String logicalPath) {
		return escapeChars(logicalPath);
	}
	
	protected String escapeChars(String path) {
		int nameLength = path.length();
		StringBuffer xmlName = new StringBuffer(nameLength + 10);
		for (int i = 0; i < nameLength; ++i)
		{
			char c = path.charAt(i);
			if (toEscape(c))
			{
				appendEscaped(xmlName, c);
			}
			else
			{
				xmlName.append(c);
			}
		}
		return xmlName.toString();
	}

	protected void appendEscaped(StringBuffer xmlName, char c) {
		xmlName.append(escapeChar);
		String hexCode = Integer.toHexString(c);
		switch (hexCode.length()) {
		case 1:
			xmlName.append("000");
			break;
		case 2:
			xmlName.append("00");
			break;
		case 3:
			xmlName.append("0");
			break;
		}
		xmlName.append(hexCode);
	}

	protected boolean toEscape(char c) {
		//escape everything but ASCII
		return c > 127 || c == escapeChar;
	}

	public char getEscapeChar() {
		return escapeChar;
	}

	public void setEscapeChar(char escapeChar) {
		this.escapeChar = escapeChar;
	}

}
