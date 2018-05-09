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

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.ReportUnitImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import net.sf.jasperreports.engine.JRException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.jgroups.util.Util.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ReportUnitResourceValidatorTest {
    @InjectMocks
    private  ReportUnitResourceValidator validator;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private ReportUnit report;

    @BeforeMethod
    public void setUpMethod() throws Exception {
        validator = new ReportUnitResourceValidator();
        MockitoAnnotations.initMocks(this);
        validator = spy(validator);
        report = new ReportUnitImpl();
        report.setLabel("Label");

        final ResourceReference reportReference = new ResourceReference("/test/uri");
        report.setMainReport(reportReference);
    }

    @Test
    public void testValidate() throws Exception {
        final ResourceReference mainReport = report.getMainReport();
        doReturn(true).when(validator).isJrxmlValid(mainReport);
        validator.validate(report);
        verify(validator).isJrxmlValid(mainReport);
    }

    @Test(expectedExceptions = JSValidationException.class)
    public void testValidate_invalidJrxml_exception() throws Exception {
        doReturn(false).when(validator).isJrxmlValid(report.getMainReport());
        validator.validate(report);
    }

    @Test
    public void isJrxmlValid_nonLocalResource() throws JRException {
        final String referenceURI = "/test/uri";
        final byte[] bytes = {1, 2, 3};
        doReturn(new FileResourceData(bytes)).when(repositoryService).getResourceData(null, referenceURI);
        doReturn(null).when(validator).loadJasperDesign(bytes);
        assertTrue(validator.isJrxmlValid(new ResourceReference(referenceURI)));
        verify(validator).loadJasperDesign(bytes);
    }

    @Test
    public void isJrxmlValid_localResourceWithoutData() throws JRException{
        final String referenceURI = "/test/uri";
        final byte[] bytes = {1, 2, 3};
        doReturn(new FileResourceData(bytes)).when(repositoryService).getResourceData(null, referenceURI);
        doReturn(null).when(validator).loadJasperDesign(bytes);
        final FileResourceImpl localResource = new FileResourceImpl();
        localResource.setURIString(referenceURI);
        assertTrue(validator.isJrxmlValid(new ResourceReference(localResource)));
        verify(validator).loadJasperDesign(bytes);
    }

    @Test
    public void isJrxmlValid_localResourceWithData() throws JRException{
        final String referenceURI = "/test/uri";
        final byte[] bytes = {1, 2, 3};
        doReturn(null).when(validator).loadJasperDesign(bytes);
        final FileResourceImpl localResource = new FileResourceImpl();
        localResource.setURIString(referenceURI);
        localResource.setData(bytes);
        assertTrue(validator.isJrxmlValid(new ResourceReference(localResource)));
        verify(validator).loadJasperDesign(bytes);
        verify(repositoryService, never()).getResourceData(null, referenceURI);
    }

    @Test
    public void isJrxmlValid_localResourceWithInvalidData() throws JRException{
        final FileResourceImpl localResource = new FileResourceImpl();
        final byte[] bytes = "invalidJrxml".getBytes();
        localResource.setData(bytes);
        doThrow(new RuntimeException()).when(validator).loadJasperDesign(bytes);
        assertFalse(validator.isJrxmlValid(new ResourceReference(localResource)));
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noJRXML() throws Exception {
        report.setMainReport((FileResource) null);
        doThrow(new RuntimeException("Should not call isJrxmlValid()"))
                .when(validator).isJrxmlValid(any(ResourceReference.class));
        validator.validate(report);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noJRXMLContent() throws Exception {
        final FileResourceImpl fileResource = new FileResourceImpl();
        fileResource.setURIString("/test/uri");
        report.setMainReport(fileResource);
        doThrow(new RuntimeException("Should not call isJrxmlValid()"))
                .when(validator).isJrxmlValid(any(ResourceReference.class));
        validator.validate(report);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void validate_noResourceData_exception() {
        List<ResourceReference> resourceReferences = new ArrayList<ResourceReference>();
        final FileResourceImpl localResource = new FileResourceImpl();
        localResource.setURIString("/test/uri");
        resourceReferences.add(new ResourceReference(localResource));
        report.setResources(resourceReferences);
        doReturn(true).when(validator).isJrxmlValid(any(ResourceReference.class));
        validator.validate(report);
    }

}
