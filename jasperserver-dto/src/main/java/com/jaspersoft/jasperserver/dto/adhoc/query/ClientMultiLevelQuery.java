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
package com.jaspersoft.jasperserver.dto.adhoc.query;

import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientQueryGroupBy;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andriy Godovanets
 * @author Stas Chubar <schubar@tibco.com>
 *
 * @version $Id$
 */
@XmlRootElement(name = "multiLevelQuery")
public class ClientMultiLevelQuery extends ClientQuery {
    @Valid
    private ClientQueryGroupBy groupBy;

    @Valid
    private List<ClientGenericOrder> orderBy;

    public ClientMultiLevelQuery() {
    }

    public ClientMultiLevelQuery(ClientMultiLevelQuery query) {
        super(query);
        if (query != null) {
            if (query.getGroupBy() != null) {
                setGroupBy(new ClientQueryGroupBy(query.getGroupBy()));
            }

            if(query.getOrderBy() != null) {
                this.orderBy = new ArrayList<ClientGenericOrder>();
                for (ClientOrder clientOrder : query.getOrderBy()) {
                    if(clientOrder instanceof ClientGenericOrder) {
                        this.orderBy.add(new ClientGenericOrder((ClientGenericOrder) clientOrder));
                    } else {
                        throw new IllegalArgumentException("Unsupported order type: " + clientOrder);
                    }
                }
            }
        }
    }

    public ClientMultiLevelQuery setSelect(ClientSelect select) {
        super.setSelect(select);
        return this;
    }

    @XmlTransient
    public ClientQueryGroupBy getGroupBy() {
        return groupBy;
    }

    public ClientMultiLevelQuery setGroupBy(ClientQueryGroupBy groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    @XmlElementWrapper(name = "groupBy")
    @XmlElements({
            @XmlElement(name = "group", type = ClientQueryGroup.class),
            @XmlElement(name = "allGroup", type = ClientQueryGroup.ClientAllGroup.class)
    })
    public List<? extends ClientQueryGroup> getGroups() {
        return groupBy != null ? groupBy.getGroups() : null;
    }

    public ClientMultiLevelQuery setGroups(List<? extends ClientQueryGroup> groups) {
        ClientQueryGroupBy clientQueryGroupBy = new ClientQueryGroupBy().setGroups(groups);
        setGroupBy(clientQueryGroupBy);
        return this;
    }


    @XmlElementWrapper(name = "orderBy")
    @XmlElement(name = "field")
    @Override
    public List<ClientGenericOrder> getOrderBy() {
        return orderBy;
    }

    public ClientMultiLevelQuery setOrderBy(List<ClientGenericOrder> orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    @XmlTransient
    public List<ClientField> getSelectedFields() {
       return super.getSelectedFields();
    }

}
