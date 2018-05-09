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

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ZipOutput.java 19925 2010-12-11 15:06:41Z tmatyashovsky $
 */
public abstract class ZipOutput extends BaseExportOutput {

	private static final Log log = LogFactory.getLog(ZipOutput.class);

	protected static final String ZIP_ENTRY_DIR_SUFFIX = "/";

	private final int level;
	private final PathProcessor pathProcessor;

	protected Set directories;

	protected class EntryOutputStream extends OutputStream {
		public void close() throws IOException {
			zipOut.closeEntry();
		}

		public void flush() throws IOException {
			zipOut.flush();
		}

		public void write(byte[] b, int off, int len) throws IOException {
			zipOut.write(b, off, len);
		}

		public void write(byte[] b) throws IOException {
			zipOut.write(b);
		}

		public void write(int b) throws IOException {
			zipOut.write(b);
		}
	}

	protected ZipOutputStream zipOut;

	protected ZipOutput(int level, PathProcessor pathProcessor, Properties outputProperties) {
		super(outputProperties);
		this.level = level;
		this.pathProcessor = pathProcessor;
	}

    protected  abstract OutputStream getOutputStream() throws IOException;

	public void open() {
        try {
            zipOut = new ZipOutputStream(getOutputStream());
            zipOut.setLevel(level);
        } catch (IOException e) {
            log.error(e);
            throw new JSExceptionWrapper(e);
        }

        directories = new HashSet();
	}

    public void close() throws IOException {
		zipOut.finish();
		zipOut.close();
	}

	public OutputStream getFileOutputStream(String path) throws IOException {
		String zipPath = getZipPath(path);
		ZipEntry fileEntry = new ZipEntry(zipPath);
		zipOut.putNextEntry(fileEntry);
		EntryOutputStream entryOut = new EntryOutputStream();
		return entryOut;
	}

	protected String getZipPath(String path) {
		String zipPath = pathProcessor.processPath(path);
		return zipPath;
	}

	public void mkdir(String path) throws IOException {
		if (directories.add(path)) {
			String zipPath = getZipPath(path);
			ZipEntry dirEntry = new ZipEntry(zipPath + ZIP_ENTRY_DIR_SUFFIX);
			zipOut.putNextEntry(dirEntry);
			zipOut.closeEntry();
		}		
	}

}
