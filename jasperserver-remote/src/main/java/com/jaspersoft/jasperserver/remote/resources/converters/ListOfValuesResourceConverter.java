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
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.dto.resources.ClientListOfValues;
import com.jaspersoft.jasperserver.dto.resources.ClientListOfValuesItem;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class ListOfValuesResourceConverter extends ResourceConverterImpl<ListOfValues, ClientListOfValues> {
    @Override
    protected ListOfValues resourceSpecificFieldsToServer(ClientListOfValues clientObject, ListOfValues resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        for (ListOfValuesItem existing : resultToUpdate.getValues()){
            resultToUpdate.removeValue(existing);
        }

        final List<ClientListOfValuesItem> items = clientObject.getItems();
        if(items != null){
            for(ClientListOfValuesItem currentItem : items){
                ListOfValuesItem serverItem = new ListOfValuesItemImpl();
                serverItem.setLabel(currentItem.getLabel());
                serverItem.setValue(currentItem.getValue());
                resultToUpdate.addValue(serverItem);
            }
        }
        return resultToUpdate;
    }

    @Override
    protected ClientListOfValues resourceSpecificFieldsToClient(ClientListOfValues client, ListOfValues serverObject, ToClientConversionOptions options) {
        final ListOfValuesItem[] values = serverObject.getValues();
        if(values != null && values.length > 0){
            List<ClientListOfValuesItem> clientItems = new ArrayList<ClientListOfValuesItem>(values.length);
            client.setItems(clientItems);
            for(ListOfValuesItem currentItem : values){
                String currentItemValue = currentItem.getValue().toString();
                if (EncryptionEngine.isEncrypted(currentItemValue)) {
                    currentItemValue = null;
                }
                clientItems.add(new ClientListOfValuesItem(currentItem.getLabel(), currentItemValue));
            }
        }
        return client;
    }
}
