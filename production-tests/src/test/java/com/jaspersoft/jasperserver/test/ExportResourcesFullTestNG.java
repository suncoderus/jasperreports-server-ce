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
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.ContextConfiguration;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class ExportResourcesFullTestNG extends BaseServiceSetupTestNG {

    protected static Log m_logger = LogFactory.getLog(ExportResourcesFullTestNG.class);

    public ExportResourcesFullTestNG() {
    }

    @BeforeClass()
    public void onSetUp() throws Exception {
        m_logger.info("onSetUp() called");
    }

    @AfterClass()
    public void onTearDown() throws Exception {
        m_logger.info("onTearDown() called");
    }

    @Test()
    public void doExportEverything() {
        m_logger.info("doExportEverything() called");

        String databaseVendor = getDatabaseProductNameFromProp();

        // set export filename. Like: js-catalog-postgresql-ce.zip, js-catalog-minimal-mysql-ce.zip
       	String outputZipFile = OUTPUT_ZIP_FILE_START + OUTPUT_DASH + databaseVendor
       	                       + OUTPUT_DASH + OUTPUT_EDITION_CE + OUTPUT_DOT + OUTPUT_ZIP_EXT;

        m_logger.info("databaseVendor=" + databaseVendor + ", outputZipFile=" + outputZipFile);
        m_logger.info("databaseVendor=" + databaseVendor + ", outputZipFile=" + outputZipFile);

    	Parameters params = createParameters().addParameterValue(PARAM_EXPORT_ZIP,
                                                      TEST_BASE_DIR + FILE_SEPARATOR + outputZipFile);
    	params.addParameter(PARAM_EVERYTHING);

        m_logger.info("About to export all resources from repository...");
    	performExport(params);
    }
}
