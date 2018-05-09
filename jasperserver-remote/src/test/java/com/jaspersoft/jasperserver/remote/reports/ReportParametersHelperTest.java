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
package com.jaspersoft.jasperserver.remote.reports;

import com.jaspersoft.jasperserver.dto.reports.ReportParameter;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ReportParametersHelperTest {
    private static final String NAME1 = "name1";
    private static final String VALUE1_1 = "value1_1";
    private static final String VALUE1_2 = "value1_2";
    private static final String VALUE1_3 = "value1_3";
    private static final String NAME2 = "name2";
    private static final String VALUE2_1 = "value2_1";
    private static final String VALUE2_2 = "value2_2";
    private static final String VALUE2_3 = "value2_3";

    @Test
    public void toRawParameters(){
        List<ReportParameter> parametersList = new ArrayList<ReportParameter>();
        ReportParameter parameter = new ReportParameter();
        List<String> values = new LinkedList<String>();
        parameter.setName(NAME1);
        values.add(VALUE1_1);
        values.add(VALUE1_2);
        values.add(VALUE1_3);
        parameter.setValues(values);
        parametersList.add(parameter);
        parameter = new ReportParameter();
        parameter.setName(NAME2);
        values = new LinkedList<String>();
        values.add(VALUE2_1);
        values.add(VALUE2_2);
        values.add(VALUE2_3);
        parameter.setValues(values);
        parametersList.add(parameter);
        final Map<String,String[]> resultMap = ReportParametersHelper.toRawParameters(parametersList);
        assertNotNull(resultMap);
        assertEquals(resultMap.size(), 2);
        for(String currentKey : resultMap.keySet()){
            if(NAME1.equals(currentKey)){
                String[] name1Values = resultMap.get(currentKey);
                assertNotNull(name1Values);
                assertEquals(name1Values.length, 3);
                Set<String> currentValues = new HashSet<String>();
                for(String currentValue : name1Values){
                    currentValues.add(currentValue);
                }
                assertTrue(currentValues.contains(VALUE1_1));
                assertTrue(currentValues.contains(VALUE1_2));
                assertTrue(currentValues.contains(VALUE1_3));
            } else if(NAME2.equals(currentKey)){
                String[] name2Values = resultMap.get(currentKey);
                assertNotNull(name2Values);
                assertEquals(name2Values.length, 3);
                Set<String> currentValues = new HashSet<String>();
                for(String currentValue : name2Values){
                    currentValues.add(currentValue);
                }
                assertTrue(currentValues.contains(VALUE2_1));
                assertTrue(currentValues.contains(VALUE2_2));
                assertTrue(currentValues.contains(VALUE2_3));
            } else {
                // no other parameters
                assertTrue(false);
            }
        }
    }

    @Test
    public void toRawParameters_null(){
        assertNull(ReportParametersHelper.toRawParameters(null));
    }
    @Test
    public void fromRawParameters_null(){
        assertNull(ReportParametersHelper.fromRawParameters(null));
    }

    @Test
    public void fromRawParameters(){
        Map<String, String[]> parametersMap = new HashMap<String, String[]>();
        parametersMap.put(NAME1, new String[] {VALUE1_1, VALUE1_2, VALUE1_3});
        parametersMap.put(NAME2, new String[] {VALUE2_1, VALUE2_2, VALUE2_3});
        final List<ReportParameter> result = ReportParametersHelper.fromRawParameters(parametersMap);
        assertNotNull(result);
        assertEquals(result.size(), 2);
        for(ReportParameter currentParameter : result){
            if(NAME1.equals(currentParameter.getName())){
                final List<String> values = currentParameter.getValues();
                assertNotNull(values);
                assertEquals(values.size(), 3);
                assertTrue(values.contains(VALUE1_1));
                assertTrue(values.contains(VALUE1_2));
                assertTrue(values.contains(VALUE1_3));
            } else if(NAME2.equals(currentParameter.getName())){
                final List<String> values = currentParameter.getValues();
                assertNotNull(values);
                assertEquals(values.size(), 3);
                assertTrue(values.contains(VALUE2_1));
                assertTrue(values.contains(VALUE2_2));
                assertTrue(values.contains(VALUE2_3));
            } else {
                // no other parameters
                assertTrue(false);
            }
        }
    }
}
