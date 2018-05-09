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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

public class ContentRepositoryTestTestNG extends BaseServiceSetupTestNG
{
    protected static Log m_logger = LogFactory.getLog(ContentRepositoryTestTestNG.class);

	public ContentRepositoryTestTestNG(){
        m_logger.info("ContentRepositoryTestTestNG => constructor() called");
    }

    /**
     *  doCreateFilesTest
     */
    @Test()
	public void doCreateFilesTest() throws Exception
	{
        m_logger.info("ContentRepositoryTestTestNG => doCreateFilesTest() called");

		ContentResource fileResource = (ContentResource) getUnsecureRepositoryService().newResource(null, ContentResource.class);
		fileResource.setName("PdfTestFile");
		fileResource.setLabel("Pdf test file");
		fileResource.setParentFolder("/ContentFiles/pdf");
		InputStream file  = getClass().getResourceAsStream("/FontsReport.pdf");

        // Test the dataContainer used by scheduling - There was a problem with this under DB2
        // This is a small file, so it will fit all in mempory
        fileResource.setDataContainer(createFileDataContainer(file));

		fileResource.setFileType(ContentResource.TYPE_PDF);
		getUnsecureRepositoryService().saveResource(null, fileResource);
        m_logger.info("ContentRepositoryTestTestNG => doCreateFilesTest() created file /ContentFiles/pdf/PdfTestFile");

		ContentResource htmlFile = (ContentResource) getUnsecureRepositoryService().newResource(null, ContentResource.class);;
		htmlFile.setName("HtmlTestFile");
		htmlFile.setLabel("HTML test file with images");
		htmlFile.setParentFolder("/ContentFiles/html");
		file  = getClass().getResourceAsStream("/FirstJasper.html");

        // This is a large file, so it will be in both memory and on the file system

        htmlFile.setDataContainer(createFileDataContainer(file));
		htmlFile.setFileType(ContentResource.TYPE_HTML);

		ArrayList images = new ArrayList();

		ContentResource image = (ContentResource) getUnsecureRepositoryService().newResource(null, ContentResource.class);;
		image.setName("img_0_0_1");
		image.setLabel("img_0_0_1");
		file  = getClass().getResourceAsStream("/FirstJasper.html_files/img_0_0_1");

		// ContentResource.readData stores the data only in memory
		image.readData(file);
		image.setFileType(ContentResource.TYPE_HTML);
		images.add(image);

		image = (ContentResource) getUnsecureRepositoryService().newResource(null, ContentResource.class);
		image.setName("img_0_7_125");
		image.setLabel("img_0_7_125");
		file  = getClass().getResourceAsStream("/FirstJasper.html_files/img_0_7_125");
		image.readData(file);
		image.setFileType(ContentResource.TYPE_HTML);
		images.add(image);

		image = (ContentResource) getUnsecureRepositoryService().newResource(null, ContentResource.class);;
		image.setName("px");
		image.setLabel("px");
		file  = getClass().getResourceAsStream("/FirstJasper.html_files/px");
		image.readData(file);
		image.setFileType(ContentResource.TYPE_HTML);
		images.add(image);

		htmlFile.setResources(images);
		getUnsecureRepositoryService().saveResource(null, htmlFile);
        m_logger.info("ContentRepositoryTestTestNG => doCreateFilesTest() created file /ContentFiles/html/HtmlTestFile");

		htmlFile = (ContentResource) getUnsecureRepositoryService().newResource(null, ContentResource.class);;
		htmlFile.setName("Test");
		htmlFile.setLabel("HTML test file with one image");
		htmlFile.setParentFolder("/ContentFiles/html");
		file  = getClass().getResourceAsStream("/Test.html");
		htmlFile.readData(file);
		htmlFile.setFileType(ContentResource.TYPE_HTML);

		image = (ContentResource) getUnsecureRepositoryService().newResource(null, ContentResource.class);;
		image.setName("image0");
		image.setLabel("image0");
		file  = getClass().getResourceAsStream("/Test.html_files/image0");
		image.readData(file);
		image.setFileType(ContentResource.TYPE_HTML);
		images = new ArrayList();
		images.add(image);

		htmlFile.setResources(images);
		getUnsecureRepositoryService().saveResource(null, htmlFile);
        m_logger.info("ContentRepositoryTestTestNG => doCreateFilesTest() created file /ContentFiles/html/Test");
	}

    /**
     *  doDeleteFilesTest
     */
    @Test(dependsOnMethods = "doCreateFilesTest")
	public void doDeleteFilesTest() throws Exception
	{
        m_logger.info("ContentRepositoryTestTestNG => doDeleteFilesTest() called");

        // note that these files are deleted in the reverse order that they were created
        deleteResource("/ContentFiles/html/Test");
        m_logger.info("ContentRepositoryTestTestNG => doDeleteFilesTest() deleted file /ContentFiles/html/Test");

        deleteResource("/ContentFiles/html/HtmlTestFile");
        m_logger.info("ContentRepositoryTestTestNG => doDeleteFilesTest() deleted file /ContentFiles/html/HtmlTestFile");

        deleteResource("/ContentFiles/pdf/PdfTestFile");
        m_logger.info("ContentRepositoryTestTestNG => doDeleteFilesTest() deleted file /ContentFiles/pdf/PdfTestFile");
    }

    private void deleteResource(String uri) {
        Resource result = getUnsecureRepositoryService().getResource(null, uri);
        assertNotNull(result);
        getUnsecureRepositoryService().deleteResource(null, uri);
    }

	private DataContainer createFileDataContainer(InputStream file) throws Exception {
		DataContainer dataContainer = new FileBufferedDataContainer();
		OutputStream os = dataContainer.getOutputStream();

		byte[] buf = new byte[4000];
		try {
			for (;;) {
				int dataSize = file.read(buf);

				if (dataSize == -1) {
					break;
				}
				os.write(buf, 0, dataSize);
			}
		} finally {
			if (file != null) {
				try {
					file.close();
					os.close();
				} catch (IOException ex) {
				}
			}
		}

		return dataContainer;
	}
}
