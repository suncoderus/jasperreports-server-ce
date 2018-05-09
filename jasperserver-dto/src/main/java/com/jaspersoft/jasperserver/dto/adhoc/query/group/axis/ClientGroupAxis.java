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
package com.jaspersoft.jasperserver.dto.adhoc.query.group.axis;


import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * @author Andriy Godovanets
 */
public class ClientGroupAxis extends ClientBaseAxis<ClientQueryGroup> {

    public ClientGroupAxis() {
        super();
    }

    public ClientGroupAxis(ClientGroupAxis axis) {
        this();
        if (axis != null && axis.getItems() != null) {
            for (ClientQueryGroup group : axis.getItems()) {
                this.getItems().add(group.deepClone());
            }
        }

    }

    public ClientGroupAxis(Collection<? extends ClientQueryGroup> c) {
        super(c);
    }

    @Valid
    @Override
    public List<ClientQueryGroup> getItems() {
        return super.getItems();
    }

    @Override
    public ClientBaseAxis setItems(List<ClientQueryGroup> items) {
        return super.setItems(items);
    }

    @Override
    public String toString() {
        return "ClientGroupAxis{" +
                "items=" + getItems() +
                '}';
    }
}
