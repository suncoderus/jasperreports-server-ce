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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class FileSystemInput extends BaseImportInput {
	
	private static final Log log = LogFactory.getLog(FileSystemInput.class);
	
	private final String rootDirName;
	private final FileSystemInputManager manager;
	private PathProcessor pathProcessor;

	private File rootDir;

	public FileSystemInput(String rootDir, PathProcessor pathProcessor, FileSystemInputManager manager) {
		this.rootDirName = rootDir;
		this.pathProcessor = pathProcessor;
		this.manager = manager;
	}

	public void open() {
		rootDir = new File(rootDirName);
		if (!rootDir.exists() || !rootDir.isDirectory()) {
			throw new JSException("jsexception.import.directory.not.found", new Object[] {rootDirName});
		}
	}

	public void close() {
	}

	public InputStream getFileInputStream(String path) {
		try {
			File file = getFile(path);
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			return in;
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}		
	}

	protected File getFile(String path) {
		String fsPath = pathProcessor.processPath(path);
		File file = new File(rootDir, fsPath);
		return file;
	}

	public boolean fileExists(String path) {
		File file = getFile(path);
		return file.exists() && file.isFile();
	}

	public boolean folderExists(String path) {
		File file = getFile(path);
		return file.exists() && file.isDirectory();
	}

	public void setPathProcessor(PathProcessor pathProcessor) {
		this.pathProcessor = pathProcessor;
	}

	public void propertiesRead(Properties properties) {
		manager.updateInputProperties(this, properties);
	}

}
