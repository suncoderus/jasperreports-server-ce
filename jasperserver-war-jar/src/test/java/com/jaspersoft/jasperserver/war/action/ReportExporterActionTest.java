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

import com.jaspersoft.jasperserver.api.JSException;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.RequestContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Sergey Prilukin
 * @version $Id:$
 */
public class ReportExporterActionTest extends UnitilsJUnit4 {

    @TestedObject
    private ReportExporterAction reportExporterAction;

    private Mock<RequestContext> requestContextMock;
    private Mock<ParameterMap> parameterMapMock;

    @InjectInto(property = "configuredExporters")
    private Mock<Map> configuredExportersMock;

    @InjectInto(property = "messageSource")
    private Mock<MessageSource> messageSourceMock;

    /**
     * If exported type not supported ensure that
     * JSException is thrown
     *
     * @throws Exception
     */
    @Test(expected = JSException.class)
    public void ensureExceptionThrownWhenExportTypeNotSupported() throws Exception{

        messageSourceMock.returns("Exception message").getMessage(
                ReportExporterAction.EXPORT_TYPE_NOT_SUPPORTED_MESSAGE_KEY, null, null);
        configuredExportersMock.returns(false).containsKey("xml");
        configuredExportersMock.returns(new HashSet()).keySet();

        parameterMapMock.returns("xml").get(ReportExporterAction.OUTPUT);
        requestContextMock.returns(parameterMapMock.getMock()).getRequestParameters();
        reportExporterAction.exportOptions(requestContextMock.getMock());
    }
}
