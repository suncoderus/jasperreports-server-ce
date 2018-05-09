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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientListOfValues;
import com.jaspersoft.jasperserver.dto.resources.ClientListOfValuesItem;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ListOfValuesResourceConverterTest {
    private ListOfValuesResourceConverter converter = new ListOfValuesResourceConverter();

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeHelper.extractClientType(ClientListOfValues.class));
        assertEquals(converter.getServerResourceType(), ListOfValues.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception{
        final String label1 = "testLabel1";
        final String value1 = "testValue1";
        final String label2 = "testLabel2";
        final String value2 = "testValue2";
        final ClientListOfValues clientObject = new ClientListOfValues();
        final ListOfValues serverObject = new ListOfValuesImpl();
        List<ClientListOfValuesItem> items = new ArrayList<ClientListOfValuesItem>(2);
        items.add(new ClientListOfValuesItem(label1, value1));
        items.add(new ClientListOfValuesItem(label2, value2));
        clientObject.setItems(items);
        final ListOfValues result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertNotNull(result);
        final ListOfValuesItem[] values = result.getValues();
        assertNotNull(values);
        assertEquals(values.length, 2);
        for(ListOfValuesItem currentItem : values){
            if(label1.equals(currentItem.getLabel())){
                assertEquals(currentItem.getValue(), value1);
            } else if(label2.equals(currentItem.getLabel())){
                assertEquals(currentItem.getValue(), value2);
            } else {
                // no other labels. Test fails if there is any.
                assertTrue(false);
            }
        }
    }

    @Test
    public void resourceSpecificFieldsToServer_updateItems() throws Exception{
        final String label1 = "testLabel1";
        final String value1 = "testValue1";
        final String label2 = "testLabel2";
        final String value2 = "testValue2";
        final ClientListOfValues clientObject = new ClientListOfValues();
        final ListOfValues serverObject = new ListOfValuesImpl();
        List<ClientListOfValuesItem> items = new ArrayList<ClientListOfValuesItem>(2);
        items.add(new ClientListOfValuesItem(label1, value1));
        items.add(new ClientListOfValuesItem(label2, value2));
        clientObject.setItems(items);
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setLabel("lala");
        item.setValue("ada");
        serverObject.addValue(item);
        final ListOfValues result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertNotNull(result);
        final ListOfValuesItem[] values = result.getValues();
        assertNotNull(values);
        assertEquals(values.length, 2);
        for(ListOfValuesItem currentItem : values){
            if(label1.equals(currentItem.getLabel())){
                assertEquals(currentItem.getValue(), value1);
            } else if(label2.equals(currentItem.getLabel())){
                assertEquals(currentItem.getValue(), value2);
            } else {
                // no other labels. Test fails if there is any.
                assertTrue(false);
            }
        }
    }

    @Test
    public void resourceSpecificFieldsToClient() {
        final String label1 = "testLabel1";
        final String value1 = "testValue1";
        final String label2 = "testLabel2";
        final String value2 = "testValue2";
        final ClientListOfValues clientObject = new ClientListOfValues();
        final ListOfValues serverObject = new ListOfValuesImpl();
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setLabel(label1);
        item.setValue(value1);
        serverObject.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel(label2);
        item.setValue(value2);
        serverObject.addValue(item);
        final ClientListOfValues result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertNotNull(result);
        final List<ClientListOfValuesItem> items = result.getItems();
        assertNotNull(items);
        assertTrue(items.size() == 2);
        for(ClientListOfValuesItem currentItem : items){
            if(label1.equals(currentItem.getLabel())){
                assertEquals(currentItem.getValue(), value1);
            } else if(label2.equals(currentItem.getLabel())){
                assertEquals(currentItem.getValue(), value2);
            } else {
                // no other labels. Test fails if there is any.
                assertTrue(false);
            }
        }
    }




}
