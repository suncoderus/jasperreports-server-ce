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

package com.jaspersoft.jasperserver.war.cascade.handlers;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import org.junit.Test;

import java.util.Map;

public class SampleHandlersTest extends InputControlHandlerBaseTest {

    @Test
    public void sampleTestCase() throws Exception {
        String testCaseName = "sampleTestCase";

        InputControl control = getControl("state", testCaseName);
        ReportInputControlInformation info = getControlInfo("state", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }
}
