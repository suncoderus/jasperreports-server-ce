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
package com.jaspersoft.jasperserver.remote.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ListItem;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.remote.ServiceException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gtoffoli
 * @version $Id$
 */
@Service
public class ListOfValuesHandler extends RepositoryResourceHandler {

    public Class getResourceType() {
        return ListOfValues.class;
    }

    protected void doGet(Resource resource, ResourceDescriptor descriptor, Map options) throws ServiceException {
        ListOfValues fileResource = (ListOfValues) resource;
        descriptor.setWsType(ResourceDescriptor.TYPE_LOV);
        descriptor.setHasData(false);
        descriptor.setIsReference(false);

        ArrayList list = new ArrayList();
        ListOfValuesItem[] lovis = fileResource.getValues();
        for (int k = 0; k < lovis.length; ++k) {
            ListItem lsitItem = new ListItem(lovis[k].getLabel(), lovis[k].getValue());
            list.add(lsitItem);
        }
        descriptor.setListOfValues(list);
    }

    @Override
    protected void updateResource(Resource resource, ResourceDescriptor descriptor, Map options) {
        super.updateResource(resource, descriptor, options);


        ListOfValues lov = (ListOfValues) resource;
        // Validations should be done in the save method...
        // Remove current items...
        ListOfValuesItem[] items = lov.getValues();
        for (int k = 0; k < items.length; ++k) {
            lov.removeValue(items[k]);
        }
        List list = descriptor.getListOfValues();
        for (int k = 0; k < list.size(); ++k) {
            ListItem litem = (ListItem) list.get(k);
            ListOfValuesItem item = new ListOfValuesItemImpl();
            item.setLabel(litem.getLabel());
            item.setValue(litem.getValue());
            lov.addValue(item);
        }
    }

    
}
