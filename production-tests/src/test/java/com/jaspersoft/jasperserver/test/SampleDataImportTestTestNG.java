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

import com.jaspersoft.jasperserver.export.Parameters;
import java.io.File;

import com.jaspersoft.jasperserver.test.BaseExportTestCaseTestNG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * Additional resources for testing get imported here
 * @author swood
 */
public class SampleDataImportTestTestNG extends BaseExportTestCaseTestNG {

    private static final Log m_logger = LogFactory.getLog(SampleDataImportTestTestNG.class);

    public SampleDataImportTestTestNG(){
        m_logger.info("SampleDataImportTestTestNG => constructor() called");
    }

    @Test()
    public void doDemoSampleImportTest() throws Exception {
        m_logger.info("SampleDataImportTestTestNG => doDemoSampleImportTest() called");

        File importDirFile = new File(TEST_BASE_DIR +
                                        FILE_SEPARATOR +
                                        "test-classes" +
                                        FILE_SEPARATOR +
                                        "exportedResources");

        // Load all folders and ZIPs as individual imports and ZIPs

        File[] files = importDirFile.listFiles();

        if (files != null) {

            for (File f : files) {
                Parameters importParams = createParameters();

                try {
                    if (f.isDirectory()) {
                        importParams.addParameterValue(PARAM_IMPORT_DIR, f.toString());
                        m_logger.info("processing directory: " + f);
                    } else if (f.getName().endsWith(".zip")) {
                        importParams.addParameterValue(PARAM_IMPORT_ZIP, f.toString());
                        m_logger.info("processing zip: " + f);
                    } else {
                        m_logger.info("skipping file: " + f);
                    }

                    if (importParams.getParameterNames().hasNext()) {
                        performImport(importParams);
                    }
                } catch (Exception e) {
                    m_logger.error("Error importing file " + f, e);
                    throw e;
                }
            }
        } else {
            m_logger.warn("no files in: " + importDirFile);
        }

    }

}
