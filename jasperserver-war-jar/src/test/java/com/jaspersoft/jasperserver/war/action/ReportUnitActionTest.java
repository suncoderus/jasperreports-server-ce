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

package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.dto.ReportUnitWrapper;
import org.junit.Test;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Prilukin
 */
public class ReportUnitActionTest extends UnitilsJUnit4 {
    @TestedObject
    private ReportUnitAction reportUnitAction;

    private Mock<RequestContext> requestContextMock;
    private Mock<ParameterMap> requestParametersMock;
    private Mock<MutableAttributeMap> flowScopeMock;
    private Mock<ReportUnitWrapper> reportUnitWrapperMock;
    private Mock<ReportUnit> reportUnitMock;
    private Mock<ResourceReference> resourceReferenceMock;
    private Mock<FileResource> localFileResource;

    @Dummy
    private FileResource fileResource;

    @InjectInto(property = "repository")
    private Mock<RepositoryService> repositoryServiceMock;
    public static final String JRXML_FILE_NAME = "simpleReport.jrxml";

    /*
     * All checkJrxmlResourceUploadedFlag* tests for:
     * Bug 23217 - uploaded jrxml file disappers if user navigate away from Set Up Report page
     */

    @Test
    public void checkJrxmlResourceUploadedFlagWhenDataIsUploadedFromFileSystemAndRUResourceIsLocal() throws Exception {
        setUpCommonBehaviourForUploadJrxmlTests();
        setUpSourceAndLocalness(JasperServerConstImpl.getFieldChoiceFile(), true);
        setUpJrxmlDataAndResourceReferenceURI(new byte[10], null);

        Event event = reportUnitAction.uploadJRXML(requestContextMock.getMock());

        assertEquals(event.getId(), "success");
        flowScopeMock.assertInvoked().put(ReportUnitAction.JRXML_FILE_RESOURCE_ALREADY_UPLOADED, JRXML_FILE_NAME);
        reportUnitWrapperMock.assertInvoked().setJrxmlChanged(true);
    }

    @Test
    public void checkJrxmlResourceUploadedFlagWhenDataIsUploadedFromFileSystemAndRUResourceIsNotLocal()
            throws Exception {
        setUpCommonBehaviourForUploadJrxmlTests();
        setUpSourceAndLocalness(JasperServerConstImpl.getFieldChoiceFile(), false);
        setUpJrxmlDataAndResourceReferenceURI(new byte[10], null);

        Event event = reportUnitAction.uploadJRXML(requestContextMock.getMock());

        assertEquals(event.getId(), "success");
        flowScopeMock.assertInvoked().put(ReportUnitAction.JRXML_FILE_RESOURCE_ALREADY_UPLOADED, JRXML_FILE_NAME);
        reportUnitWrapperMock.assertInvoked().setJrxmlChanged(true);
    }

    @Test
    public void checkJrxmlResourceUploadedFlagWhenDataIsUploadedFromFileSystemAndRUResourceIsLocalNoJrxmlData()
            throws Exception {
        setUpCommonBehaviourForUploadJrxmlTests();
        setUpSourceAndLocalness(JasperServerConstImpl.getFieldChoiceFile(), true);
        setUpJrxmlDataAndResourceReferenceURI(null, null);

        Event event = reportUnitAction.uploadJRXML(requestContextMock.getMock());

        assertEquals(event.getId(), "success");
        flowScopeMock.assertNotInvoked().put(ReportUnitAction.JRXML_FILE_RESOURCE_ALREADY_UPLOADED, null);
        reportUnitWrapperMock.assertNotInvoked().setJrxmlChanged(true);
    }

    @Test
    public void checkJrxmlResourceUploadedFlagWhenDataIsUploadedFromRepositoryAndRUResourceIsLocal()
            throws Exception {
        setUpCommonBehaviourForUploadJrxmlTests();
        setUpSourceAndLocalness("ggg", true);
        setUpJrxmlDataAndResourceReferenceURI(new byte[10], null);

        Event event = reportUnitAction.uploadJRXML(requestContextMock.getMock());

        assertEquals(event.getId(), "success");
        //TODO: check this functionality after our changes
        flowScopeMock.assertInvoked().put(ReportUnitAction.JRXML_FILE_RESOURCE_ALREADY_UPLOADED, "");
        reportUnitWrapperMock.assertInvoked().setJrxmlChanged(true);
    }

    @Test
    public void checkJrxmlResourceUploadedFlagWhenDataIsUploadedFromRepositoryAndRUResourceIsNotLocal()
            throws Exception {
        setUpCommonBehaviourForUploadJrxmlTests();
        setUpSourceAndLocalness("ggg", false);
        setUpJrxmlDataAndResourceReferenceURI(new byte[10], "/other_uri");

        Event event = reportUnitAction.uploadJRXML(requestContextMock.getMock());

        assertEquals(event.getId(), "success");
        flowScopeMock.assertInvoked().put(ReportUnitAction.JRXML_FILE_RESOURCE_ALREADY_UPLOADED, "");
        reportUnitWrapperMock.assertInvoked().setJrxmlChanged(true);
    }

    @Test
    public void checkJrxmlResourceUploadedFlagWhenDataIsUploadedFromRepositoryAndRUResourceIsNotLocalButSameUri()
            throws Exception {
        setUpCommonBehaviourForUploadJrxmlTests();
        setUpSourceAndLocalness("ggg", false);
        setUpJrxmlDataAndResourceReferenceURI(new byte[10], "/uri");

        Event event = reportUnitAction.uploadJRXML(requestContextMock.getMock());

        assertEquals(event.getId(), "success");
        flowScopeMock.assertNotInvoked().put(ReportUnitAction.JRXML_FILE_RESOURCE_ALREADY_UPLOADED, null);
        reportUnitWrapperMock.assertNotInvoked().setJrxmlChanged(true);
    }

    private void setUpSourceAndLocalness(String source, boolean isLocal) {
        reportUnitWrapperMock.returns(source).getSource();
        resourceReferenceMock.returns(isLocal).isLocal();
    }

    private void setUpJrxmlDataAndResourceReferenceURI(byte[] jrxmlData, String resourceReferenceUri) {
        reportUnitWrapperMock.returns(jrxmlData).getJrxmlData();
        resourceReferenceMock.returns(resourceReferenceUri).getReferenceURI();
    }

    private void setUpCommonBehaviourForUploadJrxmlTests() {
        reportUnitWrapperMock.returns(reportUnitMock).getReportUnit();
        reportUnitWrapperMock.returns("/uri").getJrxmlUri();

        // we do not ever want this to return true in this test
        // because this test not intended to cover this branch of uploadJRXML logic
        reportUnitWrapperMock.returns(false).isJrxmlChanged();
        flowScopeMock.returns(reportUnitWrapperMock).get("wrapper");
        requestContextMock.returns(flowScopeMock).getFlowScope();

        requestParametersMock.returns(JRXML_FILE_NAME).get("fileName");
        requestContextMock.returns(requestParametersMock).getRequestParameters();

        reportUnitMock.returns(resourceReferenceMock).getMainReport();
        reportUnitMock.returns("report").getName();
        resourceReferenceMock.returns(localFileResource).getLocalResource();
        repositoryServiceMock.returns(fileResource).newResource(null, FileResource.class);
    }
}
