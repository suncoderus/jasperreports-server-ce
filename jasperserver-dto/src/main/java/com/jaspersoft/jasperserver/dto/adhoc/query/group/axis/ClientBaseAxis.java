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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Andriy Godovanets
 */
public class ClientBaseAxis<T extends ClientQueryGroup> implements ClientAxis<T> {
    private List<T> items;

    protected ClientBaseAxis() {
        this.items = new ArrayList<T>();
    }

    public ClientBaseAxis(Collection<? extends T> c) {
        this.items = new ArrayList<T>(c);
    }

    @Override
    public T get(String id) {
        if (id == null) {
            return null;
        }
        for(T item : items) {
            if (id.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    @Override
    public T get(int index) {
        return items.get(index);
    }

    public List<T> getItems() {
        return items;
    }

    protected ClientBaseAxis setItems(List<T> items) {
        this.items = items;
        return this;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientBaseAxis<?> that = (ClientBaseAxis<?>) o;

        return getItems() != null ? getItems().equals(that.getItems()) : that.getItems() == null;

    }

    @Override
    public int hashCode() {
        return getItems() != null ? getItems().hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientBaseAxis{");
        sb.append("items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
