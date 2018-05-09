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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class FileSystemOutput extends BaseExportOutput {
	
	private static final Log log = LogFactory.getLog(FileSystemOutput.class);
	
	private static final CommandOut commandOut = CommandOut.getInstance();

	private final String rootDirName;
	private final PathProcessor pathProcessor;
	
	private File rootDir;

	public FileSystemOutput(String rootDir, PathProcessor pathProcessor, Properties outputProperties) {
		super(outputProperties);
		this.rootDirName = rootDir;
		this.pathProcessor = pathProcessor;
	}

	public String getRootDirName() {
		return rootDirName;
	}

	public void open() {
		rootDir = new File(getRootDirName());
		
		commandOut.debug("Creating directory " + rootDir.getAbsolutePath() + "");
		
		rootDir.mkdirs();
	}
	
	public void close() {
		// nothing
	}

	public void mkdir(String path) {
		File dir = getFile(path);
		dir.mkdirs();
	}

	protected File getFile(String path) {
		String filePath = pathProcessor.processPath(path);
		File dir = new File(rootDir, filePath);
		return dir;
	}

	public OutputStream getFileOutputStream(String path) {
		try {
			File file = getFile(path);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			return out;
		} catch (FileNotFoundException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}		
	}

}
