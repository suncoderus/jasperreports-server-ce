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

package com.jaspersoft.jasperserver.dto.common;

import com.jaspersoft.jasperserver.dto.authority.ClientRole;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@XmlRootElement(name = "patchItems")
public class PatchDescriptor {
    private List<PatchItem> items;
    private int version;

    public PatchDescriptor(){}

    public PatchDescriptor(int version) {
        this.version = version;
    }

    public PatchDescriptor(List<PatchItem> items, int version) {
        this.items = items;
        this.version = version;
    }

    public PatchDescriptor(PatchDescriptor other) {
        final List<PatchItem> patchItems = other.getItems();
        if(patchItems != null){
            items = new ArrayList<PatchItem>(other.getItems().size());
            for(PatchItem item : patchItems){
                items.add(new PatchItem(item));
            }
        }
        this.version = other.getVersion();
    }

    @XmlElement(name = "version")
    public int getVersion() {
        return version;
    }

    public PatchDescriptor setVersion(int version) {
        this.version = version;
        return this;
    }

    @XmlElement(name = "patch")
    public List<PatchItem> getItems() {
        return items;
    }

    public PatchDescriptor setItems(List<PatchItem> items) {
        this.items = items;
        return this;
    }

    public PatchDescriptor field(String name, String value){
        if (items == null){
            items = new LinkedList<PatchItem>();
        }

        PatchItem item = new PatchItem();
        item.setField(name);
        item.setValue(value);
        items.add(item);

        return this;
    }

    public PatchDescriptor expression(String expression){
        if (items == null){
            items = new LinkedList<PatchItem>();
        }

        PatchItem item = new PatchItem();
        item.setExpression(expression);
        items.add(item);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatchDescriptor that = (PatchDescriptor) o;

        if (items != null ? !items.equals(that.items) : that.items != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return items != null ? items.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PatchDescriptor{" +
                "items=" + items +
                '}';
    }
}
