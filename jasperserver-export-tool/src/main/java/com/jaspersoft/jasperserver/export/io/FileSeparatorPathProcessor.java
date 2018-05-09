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

import com.jaspersoft.jasperserver.core.util.PathUtils;

/**
 * @author lucian
 *
 */
public class FileSeparatorPathProcessor implements PathProcessor {

	private char fileSeparator;
	
	public FileSeparatorPathProcessor() {
		this.fileSeparator = System.getProperty("file.separator").charAt(0);
	}

	public String processPath(String logicalPath) {
		return replaceSeparators(logicalPath);
	}

	public char getFileSeparator() {
		return fileSeparator;
	}

	public void setFileSeparator(char fileSeparator) {
		this.fileSeparator = fileSeparator;
	}

	protected String replaceSeparators(String path) {
		return path.replace(PathUtils.FILE_SEPARATOR, fileSeparator);
	}
	
}
