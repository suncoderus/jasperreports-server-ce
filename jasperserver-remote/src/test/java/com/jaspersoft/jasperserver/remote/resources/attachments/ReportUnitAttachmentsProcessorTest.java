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
package com.jaspersoft.jasperserver.remote.resources.attachments;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.ReportUnitImpl;
import com.jaspersoft.jasperserver.dto.resources.ResourceMultipartConstants;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ReportUnitAttachmentsProcessorTest {
    private ReportUnitAttachmentsProcessor processor = new ReportUnitAttachmentsProcessor();

    @Test
    public void processAttachments_noAttachments_noFiles_nothingHappens() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final ReportUnit reportUnit = new ReportUnitImpl();
        final Resource result = processor.processAttachments(reportUnit, new HashMap<String, InputStream>());
        assertSame(result, reportUnit);
    }

    @Test
    public void processAttachments_jrxml() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final ReportUnit reportUnit = new ReportUnitImpl();
        final FileResourceImpl jrxmlFile = new FileResourceImpl();
        reportUnit.setMainReport(jrxmlFile);
        byte[] bytes = {1, 2, 3};
        final HashMap<String, InputStream> parts = new HashMap<String, InputStream>();
        parts.put(ResourceMultipartConstants.REPORT_JRXML_PART_NAME, new ByteArrayInputStream(bytes));
        final ReportUnit result = processor.processAttachments(reportUnit, parts);
        assertSame(result, reportUnit);
        assertNotNull(result.getMainReport());
        assertSame(result.getMainReport().getLocalResource(), jrxmlFile);
        assertEquals(jrxmlFile.getData(), bytes);
    }

    @Test
    public void processAttachments_files() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final ReportUnit reportUnit = new ReportUnitImpl();
        final ArrayList<ResourceReference> resources = new ArrayList<ResourceReference>();
        reportUnit.setResources(resources);
        FileResourceImpl localResource1 = new FileResourceImpl();
        final String name1 = "name1";
        localResource1.setName(name1);
        resources.add(new ResourceReference(localResource1));
        FileResourceImpl localResource2 = new FileResourceImpl();
        final String name2 = "name2";
        localResource2.setName(name2);
        resources.add(new ResourceReference(localResource2));
        FileResourceImpl localResource3 = new FileResourceImpl();
        final String name3 = "name3";
        localResource3.setName(name3);
        localResource3.setReferenceURI("/test/reference/uri");
        resources.add(new ResourceReference(localResource3));
        FileResourceImpl localResource4 = new FileResourceImpl();
        final String name4 = "name4";
        localResource4.setName(name4);
        resources.add(new ResourceReference(localResource4));
        byte[] bytes1 = {1, 2, 3};
        byte[] bytes2 = {4, 5, 6};
        byte[] bytes4 = {7, 8, 9};
        final HashMap<String, InputStream> parts = new HashMap<String, InputStream>();
        parts.put(ResourceMultipartConstants.REPORT_FILE_PART_NAME_PREFIX + name1, new ByteArrayInputStream(bytes1));
        parts.put(ResourceMultipartConstants.REPORT_FILE_PART_NAME_PREFIX + name2, new ByteArrayInputStream(bytes2));
        parts.put(ResourceMultipartConstants.REPORT_FILE_PART_NAME_PREFIX + name4, new ByteArrayInputStream(bytes4));
        final ReportUnit result = processor.processAttachments(reportUnit, parts);
        assertSame(result, reportUnit);
        final List<ResourceReference> resultResources = result.getResources();
        assertEquals(resultResources.size(), 4);
        assertSame(resultResources.get(0).getLocalResource(), localResource1);
        assertEquals(localResource1.getData(), bytes1);
        assertSame(resultResources.get(1).getLocalResource(), localResource2);
        assertEquals(localResource2.getData(), bytes2);
        assertSame(resultResources.get(2).getLocalResource(), localResource3);
        assertNull(localResource3.getData());
        assertSame(resultResources.get(3).getLocalResource(), localResource4);
        assertEquals(localResource4.getData(), bytes4);
    }
}
