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
package com.jaspersoft.jasperserver.remote.customdatasources;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.dto.customdatasources.ClientCustomDataSourceDefinition;
import com.jaspersoft.jasperserver.dto.customdatasources.CustomDataSourcePropertyDefinition;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class CustomDataSourceDefinitionToClientConverterTest {
    private CustomDataSourceDefinitionToClientConverter converter = new CustomDataSourceDefinitionToClientConverter();

    @Test
    public void toClient_propertyDefinitionsNullEmpty_getsNull(){
        final CustomDataSourceDefinition serverObject = new CustomDataSourceDefinition();
        ClientCustomDataSourceDefinition result = converter.toClient(serverObject, null);
        assertNull(result.getPropertyDefinitions());
        serverObject.setPropertyDefinitions(new ArrayList<Map<String, Object>>());
        result = converter.toClient(serverObject, null);
        assertNull(result.getPropertyDefinitions());
    }

    @Test
    public void toClient_queryExecuterMapNullEmpty_getsNull(){
        final CustomDataSourceDefinition serverObject = new CustomDataSourceDefinition();
        ClientCustomDataSourceDefinition result = converter.toClient(serverObject, null);
        assertNull(result.getQueryTypes());
        serverObject.setQueryExecuterMap(new HashMap<String, String>());
        result = converter.toClient(serverObject, null);
        assertNull(result.getQueryTypes());
    }

    @Test
    public void toClient_queryTypes(){
        final CustomDataSourceDefinition serverObject = new CustomDataSourceDefinition();
        final HashMap<String, String> queryExecuterMap = new HashMap<String, String>();
        queryExecuterMap.put("expectedKey1", "test1");
        queryExecuterMap.put("expectedKey2", "test2");
        serverObject.setQueryExecuterMap(queryExecuterMap);
        ClientCustomDataSourceDefinition result = converter.toClient(serverObject, null);
        final List<String> queryTypes = result.getQueryTypes();
        assertNotNull(queryTypes);
        assertEquals(queryTypes.size(), queryExecuterMap.keySet().size());
        assertTrue(queryExecuterMap.keySet().containsAll(queryTypes));
    }

    @Test
    public void toClient_propertyDefinitions_name(){
        final CustomDataSourceDefinition serverObject = new CustomDataSourceDefinition();
        final String expectedDefinitionName = "expectedDefinitionName";
        serverObject.setName(expectedDefinitionName);
        final ArrayList<Map<String, Object>> propertyDefinitions = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> propertyDefinitionMap = new HashMap<String, Object>();
        propertyDefinitionMap.put(CustomDataSourceDefinition.PARAM_NAME, "expectedName1");
        propertyDefinitionMap.put(CustomDataSourceDefinition.PARAM_DEFAULT, "expectedDefault1");
        propertyDefinitions.add(propertyDefinitionMap);
        propertyDefinitionMap = new HashMap<String, Object>();
        propertyDefinitionMap.put(CustomDataSourceDefinition.PARAM_NAME, "expectedName2");
        propertyDefinitionMap.put(CustomDataSourceDefinition.PARAM_DEFAULT, "expectedDefault2");
        propertyDefinitions.add(propertyDefinitionMap);
        propertyDefinitionMap = new HashMap<String, Object>();
        propertyDefinitionMap.put(CustomDataSourceDefinition.PARAM_NAME, "expectedName3");
        propertyDefinitionMap.put(CustomDataSourceDefinition.PARAM_HIDDEN, "true");
        propertyDefinitions.add(propertyDefinitionMap);
        serverObject.setPropertyDefinitions(propertyDefinitions);
        ClientCustomDataSourceDefinition result = converter.toClient(serverObject, null);
        final List<CustomDataSourcePropertyDefinition> clientPropertyDefinitions = result.getPropertyDefinitions();
        assertEquals(result.getName(), expectedDefinitionName);
        assertNotNull(clientPropertyDefinitions);
        assertEquals(clientPropertyDefinitions.size(), 2);
        for(CustomDataSourcePropertyDefinition currentDefinition : clientPropertyDefinitions){
            if(currentDefinition.getName().equals("expectedName1")){
                assertEquals(currentDefinition.getDefaultValue(), "expectedDefault1");
                assertEquals(currentDefinition.getLabel(), expectedDefinitionName + ".properties.expectedName1");
            } else if(currentDefinition.getName().equals("expectedName2")){
                assertEquals(currentDefinition.getDefaultValue(), "expectedDefault2");
                assertEquals(currentDefinition.getLabel(), expectedDefinitionName + ".properties.expectedName2");
            } else {
                fail();
            }
        }
    }

    @Test
    public void toClient_additionalProperties(){
        final CustomDataSourceDefinition serverObject = new CustomDataSourceDefinition();
        final String expectedDefinitionName = "expectedDefinitionName";
        serverObject.setName(expectedDefinitionName);
        final ArrayList<Map<String, Object>> propertyDefinitions = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> propertyDefinitionMap = new HashMap<String, Object>();
        propertyDefinitionMap.put(CustomDataSourceDefinition.PARAM_NAME, "expectedName");
        propertyDefinitionMap.put(CustomDataSourceDefinition.PARAM_DEFAULT, "expectedDefault");
        propertyDefinitionMap.put("customProperty1", "customProperty1Value");
        propertyDefinitionMap.put("customProperty2", "customProperty2Value");
        propertyDefinitionMap.put("customProperty3", "customProperty3Value");
        propertyDefinitions.add(propertyDefinitionMap);
        serverObject.setPropertyDefinitions(propertyDefinitions);
        ClientCustomDataSourceDefinition result = converter.toClient(serverObject, null);
        assertNotNull(result);
        assertEquals(result.getName(), expectedDefinitionName);
        final List<CustomDataSourcePropertyDefinition> clientPropertyDefinitions = result.getPropertyDefinitions();
        assertNotNull(clientPropertyDefinitions);
        assertEquals(clientPropertyDefinitions.size(), 1);
        CustomDataSourcePropertyDefinition property = clientPropertyDefinitions.get(0);
        assertEquals(property.getName(), "expectedName");
        assertEquals(property.getDefaultValue(), "expectedDefault");
        final List<ClientProperty> properties = property.getProperties();
        assertNotNull(properties);
        assertEquals(properties.size(), 3);
        for (ClientProperty currentProperty : properties){
            if("customProperty1".equals(currentProperty.getKey())){
                assertEquals(currentProperty.getValue(), "customProperty1Value");
            } else if("customProperty2".equals(currentProperty.getKey())){
                assertEquals(currentProperty.getValue(), "customProperty2Value");
            } else if("customProperty3".equals(currentProperty.getKey())){
                assertEquals(currentProperty.getValue(), "customProperty3Value");
            } else {
                fail();
            }
        }
    }
}
