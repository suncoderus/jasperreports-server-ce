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

import com.jaspersoft.jasperserver.api.JSShowOnlyErrorMessage;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlsInformationImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportLoadingService;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.war.cascade.InputControlValidationError;
import com.jaspersoft.jasperserver.war.cascade.InputControlsLogicService;
import com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;
import net.sf.jasperreports.web.servlets.JasperPrintAccessor;
import net.sf.jasperreports.web.servlets.ReportPageStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Vladimir Tsukur
 * @author Anton Fomin
 * @version $Id$
 */
public class ViewReportActionTest extends UnitilsJUnit4 {

    @TestedObject
    private ViewReportAction viewReportAction;

    @InjectInto(property = "auditContext")
    private Mock<AuditContext> auditContextMock;

    @InjectInto(property = "messages")
    private Mock<MessageSource> messageSourceMock;

    @InjectInto(property = "engine")
    private Mock<EngineService> engineServiceMock;

    @InjectInto(property = "reportLoadingService")
    private Mock<ReportLoadingService> reportLoadingServiceMock;

    @InjectInto(property = "inputControlsLogicService")
    private Mock<InputControlsLogicService> inputControlsLogicServiceMock;

    @InjectInto(property = "attributeInputControlsInformation")
    private String attributeInputControlsInformation = "controlsInfo";

    @InjectInto(property = "inputControlsAttrName")
    private String inputControlsAttrName = "controlsAttr";

    @InjectInto(property = "reportUnitAttrName")
    private String reportUnitAttrName = "reportUnitAttrName";

    @InjectInto(property = "jasperPrintAccessor")
    private Mock<SessionObjectSerieAccessor> jasperPrintAccessor;

    @InjectInto(property = "reportContextAccessor")
    private Mock<WebflowReportContextAccessor> reportContextAccessor;

    @InjectInto(property = "uiExceptionRouter")
    private Mock<UIExceptionRouter> uiExceptionRouterMock;

    private Mock<RequestContext> requestContext;

    @Before
    public void setUp() {
        mockMessageSource();
        
        Mock<WebflowReportContext> reportContextMock = MockUnitils.createMock(WebflowReportContext.class);
		reportContextAccessor.returns(reportContextMock).getContext(requestContext.getMock());
    }

    @Test
    public void ensureProperMessageSetForEmptyRyportIfValidationErrorsOccursDuringGettingTypedParams() throws Exception {
        final Map<String, Object> flowScopeParams = map(
                entry("controlsInfo", new ReportInputControlsInformationImpl()),
                entry("reportUnitAttrName", "/uri"),
                entry("controlsAttr", new ArrayList<InputControl>()));
        final Map<String, Object> requestParams = map();
        mockRequestContext(requestParams, flowScopeParams, map());

        inputControlsLogicServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                final ValidationErrorsImpl errors = new ValidationErrorsImpl();
                errors.add(new InputControlValidationError("code", null, "message", "/OrderId", null));
                errors.add(new InputControlValidationError("code", null, "message", "/Country", null));
                throw new InputControlsValidationException(errors);
            }
        }).getTypedParameters(null, null);

        viewReportAction.runReport(requestContext.getMock());

        assertEquals("jasper.report.view.controls.validation.failed: OrderId, Country", requestParams.get(ViewReportAction.ATTRIBUTE_EMPTY_REPORT_MESSAGE));
    }

    @Test
    public void ensureProperMessageSetForEmptyRyportIfValidationErrorsOccursDuringGettingControlStates() throws Exception {
        final Map<String, Object> flowScopeParams = map(
                entry("controlsInfo", new ReportInputControlsInformationImpl()),
                entry("reportUnitAttrName", "/uri"),
                entry("controlsAttr", new ArrayList<InputControl>()));
        final Map<String, Object> requestParams = map();
        mockRequestContext(requestParams, flowScopeParams, map());

        inputControlsLogicServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                InputControlState inputControlState = new InputControlState();
                inputControlState.setError("Error");
                inputControlState.setUri("/OrderId");

                InputControlState inputControlStateCountry = new InputControlState();
                inputControlStateCountry.setError("Error");
                inputControlStateCountry.setUri("/Country");

                return Arrays.asList(inputControlState, inputControlStateCountry);
            }
        }).getValuesForInputControls(null, null, null, false);

        viewReportAction.runReport(requestContext.getMock());

        assertEquals("jasper.report.view.controls.validation.failed: OrderId, Country", requestParams.get(ViewReportAction.ATTRIBUTE_EMPTY_REPORT_MESSAGE));
    }

    @Test
    public void ensureProperMessageSetForEmptyRyportIfNoValidation() throws Exception {
        final Map<String, Object> flowScopeParams = map(
                entry("controlsInfo", new ReportInputControlsInformationImpl()),
                entry("reportUnitAttrName", "/uri"),
                entry("controlsAttr", new ArrayList<InputControl>()));
        final Map<String, Object> requestParams = map();

        mockRequestContext(requestParams, flowScopeParams, map());

        inputControlsLogicServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                return new HashMap();
            }
        }).getTypedParameters(null, null);

        Mock<ReportUnitResult> reportUnitResultMock = MockUnitils.createMock(ReportUnitResult.class);
        Mock<JasperPrintAccessor> jasperPrintAccessorMock = MockUnitils.createMock(JasperPrintAccessor.class);
        Mock<ReportPageStatus> reportPageStatusMock = MockUnitils.createMock(ReportPageStatus.class);
        engineServiceMock.returns(reportUnitResultMock).execute(null, null);
        reportUnitResultMock.returns(jasperPrintAccessorMock).getJasperPrintAccessor();
        jasperPrintAccessorMock.returns(reportPageStatusMock.getMock()).pageStatus(-1, null);

        viewReportAction.runReport(requestContext.getMock());

        assertEquals("jasper.report.view.empty", requestParams.get(ViewReportAction.ATTRIBUTE_EMPTY_REPORT_MESSAGE));
    }

    @Test(expected = JSShowOnlyErrorMessage.class)
    public void ensureUIExceptionRouterIsUsedForRegisteredExceptionTypes() throws Exception {
        final Map<String, Object> flowScopeParams = map(
                entry("controlsInfo", new ReportInputControlsInformationImpl()),
                entry("reportUnitAttrName", "/uri"),
                entry("controlsAttr", new ArrayList<InputControl>()));

        mockRequestContext(map(), flowScopeParams, map());

        inputControlsLogicServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                return new HashMap();
            }
        }).getTypedParameters(null, null);

        Mock<ReportUnitResult> reportUnitResultMock = MockUnitils.createMock(ReportUnitResult.class);
        Mock<JasperPrintAccessor> jasperPrintAccessorMock = MockUnitils.createMock(JasperPrintAccessor.class);
        Mock<ReportPageStatus> reportPageStatusMock = MockUnitils.createMock(ReportPageStatus.class);
        engineServiceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                throw new RuntimeException("testException");
            }
        }).execute(null, null);
        reportUnitResultMock.returns(jasperPrintAccessorMock).getJasperPrintAccessor();
        jasperPrintAccessorMock.returns(reportPageStatusMock.getMock()).pageStatus(-1, null);

        JSShowOnlyErrorMessage uiErrorMessage = new JSShowOnlyErrorMessage("testMessage");
        uiExceptionRouterMock.returns(uiErrorMessage).getUIException(null);

        viewReportAction.runReport(requestContext.getMock());
    }

    @Test
    public void testGetRequestParametersAsJSON() throws IOException {
        Mock<RequestContext> requestContextMock = MockUnitils.createMock(RequestContext.class);
        Map<String, String[]> nativeParameterMap = new LinkedHashMap<String, String[]>();
        nativeParameterMap.put("p1", new String[]{"v1"});
        nativeParameterMap.put("p2", new String[]{"v1", "v2"});

        Mock<ServletRequest> servletRequestMock = MockUnitils.createMock(ServletRequest.class);
        servletRequestMock.returns(nativeParameterMap).getParameterMap();
        Mock<ExternalContext> externalContextMock = MockUnitils.createMock(ExternalContext.class);
        externalContextMock.returns(servletRequestMock.getMock()).getNativeRequest();
        requestContextMock.returns(externalContextMock.getMock()).getExternalContext();

        String actualObject = viewReportAction.getRequestParametersAsJSON(requestContextMock.getMock());

        assertEquals("{\"p1\":[\"v1\"],\"p2\":[\"v1\",\"v2\"]}", actualObject);
    }

    private void mockRequestContext(
            Map<String, Object> requestParams, Map<String, Object> flowScopeParams, final Map<String, Object> sessionParams) {

        Mock<ServletExternalContext> externalContext = MockUnitils.createMock(ServletExternalContext.class);
        Mock<HttpServletRequest> httpServletRequestMock = MockUnitils.createMock(HttpServletRequest.class);
        Mock<HttpSession> httpSessionMock = MockUnitils.createMock(HttpSession.class);
        Mock<FlowExecutionContext> flowExecutionContextMock = MockUnitils.createMock(FlowExecutionContext.class);

        requestContext.returns(setupMutableAttributeMap(flowScopeParams)).getFlowScope();
        requestContext.returns(setupMutableAttributeMap(requestParams)).getRequestScope();
        requestContext.returns(setupMutableAttributeMap(requestParams)).getFlashScope();
        requestContext.returns(externalContext).getExternalContext();
        requestContext.returns(setupParameterMap(requestParams)).getRequestParameters();
        requestContext.returns(flowExecutionContextMock).getFlowExecutionContext();

        externalContext.returns(setupSharedAttributeMap(sessionParams)).getSessionMap();
        externalContext.returns(httpServletRequestMock).getNativeRequest();
        httpServletRequestMock.returns(httpSessionMock).getSession();

        httpSessionMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                return sessionParams.get(proxyInvocation.getArguments().get(0));
            }
        }).getAttribute(null);
    }

    private void mockMessageSource() {
        messageSourceMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                StringBuilder sb = new StringBuilder((String)proxyInvocation.getArguments().get(0));
                if (proxyInvocation.getArguments().get(1) != null) {
                    sb.append(": ");
                    for (Object param: (Object[])proxyInvocation.getArguments().get(1)) {
                        sb.append((String)param);
                    }
                }

                return sb.toString();
            }
        }).getMessage(null, null, null);
    }

    private ParameterMap setupParameterMap(final Map<String, Object> properties) {
        Mock<ParameterMap> parameterMapMock = MockUnitils.createMock(ParameterMap.class);
        parameterMapMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                return properties.get(proxyInvocation.getArguments().get(0));
            }
        }).get(null);

        return parameterMapMock.getMock();
    }

    private MutableAttributeMap setupMutableAttributeMap(final Map<String, Object> properties) {
        Mock<MutableAttributeMap> attributeMapMock = MockUnitils.createMock(MutableAttributeMap.class);
        attributeMapMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                return properties.get(proxyInvocation.getArguments().get(0));
            }
        }).get(null);
        attributeMapMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                final String key = (String)proxyInvocation.getArguments().get(0);
                final Object value = proxyInvocation.getArguments().get(1);
                properties.put(key, value);
                return null;
            }
        }).put(null, null);

        return attributeMapMock.getMock();
    }

    private SharedAttributeMap setupSharedAttributeMap(final Map<String, Object> properties) {
        Mock<SharedAttributeMap> attributeMapMock = MockUnitils.createMock(SharedAttributeMap.class);
        attributeMapMock.performs(new MockBehavior() {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                return properties.get(proxyInvocation.getArguments().get(0));
            }
        }).get(null);

        return attributeMapMock.getMock();
    }

    private Map<String, Object> map(Map.Entry<String, Object>... entries) {
        Map<String, Object> entryMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry: entries) {
            entryMap.put(entry.getKey(), entry.getValue());
        }

        return entryMap;
    }

    private Map.Entry<String, Object> entry(String key, Object value) {
        return new AbstractMap.SimpleEntry<String, Object>(key, value);
    }
}
