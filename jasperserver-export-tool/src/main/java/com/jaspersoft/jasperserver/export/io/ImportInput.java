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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public interface ImportInput {
	
	void open() throws IOException;
	
	void close() throws IOException;

	boolean folderExists(String path);

	boolean fileExists(String path);

	boolean fileExists(String parentPath, String path);
	
	InputStream getFileInputStream(String path) throws IOException;
	
	InputStream getFileInputStream(String parentPath, String path) throws IOException;
	
	void propertiesRead(Properties properties);

}
