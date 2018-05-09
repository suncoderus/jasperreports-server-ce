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

package com.jaspersoft.jasperserver.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.jaspersoft.jasperserver.export.CommandBean;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.ParametersImpl;

import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class BaseExportTestCaseTestNG extends BaseServiceSetupTestNG {

    protected static Log m_logger = LogFactory.getLog(BaseExportTestCaseTestNG.class);

	protected static final String TEST_BASE_DIR = "target";

	protected static final String EXPORT_COMMAND_BEAN_NAME = "exportCommandBean";
	protected static final String IMPORT_COMMAND_BEAN_NAME = "importCommandBean";

	protected static final String PARAM_EXPORT_DIR = "output-dir";
	protected static final String PARAM_EXPORT_URIS = "uris";
	protected static final String PARAM_EXPORT_REPORT_JOB_URIS = "report-jobs";
	protected static final String PARAM_EXPORT_USERS = "users";
	protected static final String PARAM_EXPORT_ROLES = "roles";

	protected static final String PARAM_IMPORT_DIR = "input-dir";
	protected static final String PARAM_IMPORT_ZIP = "input-zip";
	protected static final String PARAM_IMPORT_PREPEND_PATH = "prepend-path";

	protected static final String FILE_SEPARATOR = System.getProperty("file.separator");
	protected static final Random m_random = new Random(System.currentTimeMillis());

	private List m_exportFolders = new ArrayList();

	public BaseExportTestCaseTestNG(){
        m_logger.info("BaseExportTestCaseTestNG => constructor() called");
    }

	public void onSetUp() throws Exception {
        m_logger.info("BaseExportTestCaseTestNG => onSetUp() called");
		m_exportFolders.clear();
	}

	public void onTearDown() throws Exception {
        m_logger.info("BaseExportTestCaseTestNG => onTearDown() called");
		deleteExportFolders();
	}

	protected void deleteExportFolders() {
		for (Iterator it = m_exportFolders.iterator(); it.hasNext();) {
			String folder = (String) it.next();
			deleteFolder(new File(folder));
		}
	}

	protected void deleteFolder(File folder) {
		if (folder.exists() && folder.isDirectory()) {
			File[] subFiles = folder.listFiles();
			if (subFiles != null) {
				for (int i = 0; i < subFiles.length; i++) {
					File subFile = subFiles[i];
					if (subFile.isFile()) {
                        m_logger.info("BaseExportTestCaseTestNG => deleteFolder() is deleting export file " + folder.getName() + "/" + subFile.getName());
						subFile.delete();
					} else if (subFile.isDirectory()) {
						deleteFolder(subFile);
					}
				}
			}

            m_logger.info("BaseExportTestCaseTestNG => deleteFolder() is deleting export folder " + folder.getName());
			folder.delete();
		}
	}

	protected String createExportDir() {
		String dir = TEST_BASE_DIR + FILE_SEPARATOR + "export_" + m_random.nextInt();
        m_logger.info("BaseExportTestCaseTestNG => createExportDir() is adding export directory " + dir);
		m_exportFolders.add(dir);
		return dir;
	}
}
