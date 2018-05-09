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

import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Anton Fomin
 * @version $Id$
 */
public class ReportParametersUtilsTest {

    @Test
    public void getValueMapFromInputControlStates() {

        Map<String, String[]> actualValueMap = ReportParametersUtils.getValueMapFromInputControlStates(prepareStates());

        Map<String, String[]> expectedValueMap = new HashMap<String, String[]>();
        expectedValueMap.put("accountType", new String[]{"Consulting"});
        expectedValueMap.put("industry", new String[]{"Communications"});
        expectedValueMap.put("name", new String[]{"BestEngineering, Inc"});
        expectedValueMap.put(null, new String[0]);
        expectedValueMap.put("address", new String[]{"address string"});

        assertReflectionEquals(expectedValueMap, actualValueMap);
    }

    private List<InputControlState> prepareStates() {
        List<InputControlState> expectedStates = new ArrayList<InputControlState>();

        InputControlState accountType = new InputControlState();
        List<InputControlOption> accountTypeOptions = new ArrayList<InputControlOption>();
        accountTypeOptions.add(new InputControlOption("Consulting", "Consulting Label", true));
        accountTypeOptions.add(new InputControlOption("Distribution", "Distribution Label"));
        accountTypeOptions.add(new InputControlOption("Manufactoring", "Manufactoring Label"));
        accountType.setOptions(accountTypeOptions);
        accountType.setId("accountType");
        expectedStates.add(accountType);

        InputControlState industry = new InputControlState();
        List<InputControlOption> industryOptions = new ArrayList<InputControlOption>();
        industryOptions.add(new InputControlOption("Engineering", "Engineering Label"));
        industryOptions.add(new InputControlOption("Machinery", "Machinery Label"));
        industryOptions.add(new InputControlOption("Construction", "Construction Label"));
        industryOptions.add(new InputControlOption("Communications", "Communications Label", true));
        industryOptions.add(new InputControlOption("Telecommunications", "Telecommunications Label"));
        industry.setOptions(industryOptions);
        industry.setId("industry");
        expectedStates.add(industry);

        InputControlState name = new InputControlState();
        List<InputControlOption> nameOptions = new ArrayList<InputControlOption>();
        nameOptions.add(new InputControlOption("EngBureau, Ltd", "EngBureau, Ltd Label"));
        nameOptions.add(new InputControlOption("BestEngineering, Inc", "BestEngineering, Inc Label", true));
        nameOptions.add(new InputControlOption("SuperSoft, LLC", "SuperSoft, LLC Label"));
        nameOptions.add(new InputControlOption("Detwiler-Biltoft Transportation Corp", "Detwiler-Biltoft Transportation Corp Label"));
        nameOptions.add(new InputControlOption("F & M Detwiler Transportation Corp", "F & M Detwiler Transportation Corp Label"));
        nameOptions.add(new InputControlOption("D & D Barrera Transportation, Ltd", "D & D Barrera Transportation, Ltd Label"));
        nameOptions.add(new InputControlOption("Infinity Communication Calls, Ltd", "Infinity Communication Calls, Ltd Label"));
        name.setOptions(nameOptions);
        name.setId("name");
        expectedStates.add(name);

        expectedStates.add(null);

        expectedStates.add(new InputControlState());

        InputControlState address = new InputControlState();
        address.setId("address");
        address.setValue("address string");
        expectedStates.add(address);

        return expectedStates;
    }
}
