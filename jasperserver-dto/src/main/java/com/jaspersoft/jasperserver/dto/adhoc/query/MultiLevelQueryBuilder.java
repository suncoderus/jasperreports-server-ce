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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientQueryGroupBy;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * @author Andriy Godovanets
 * @author Stas Chubar <schubar@tibco.com>
 *
 * @version $Id$
 */
public class MultiLevelQueryBuilder extends ClientQueryBuilder {
    private List<? extends ClientQueryGroup> groups;
    private List<ClientQueryAggregatedField> aggregates;

    public static MultiLevelQueryBuilder select(List<ClientQueryField> fields) {
        MultiLevelQueryBuilder qb = new MultiLevelQueryBuilder();
        qb.setFields(fields);
        return qb;
    }

    public static MultiLevelQueryBuilder select(ClientQueryField... fields) {
        return select(asList(fields));
    }

    public static MultiLevelQueryBuilder select(List<ClientQueryField> fields, List<ClientQueryAggregatedField> aggregates) {
        MultiLevelQueryBuilder qb = select(fields);
        qb.setAggregates(aggregates);
        return qb;
    }

    @Override
    public ClientMultiLevelQuery build() {
        ClientMultiLevelQuery query = new ClientMultiLevelQuery();
        buildCommon(query);

        if (getAggregates() != null) {
            query.getSelect().setAggregations(getAggregates());
        }
        if (getGroups() != null) {
            query.setGroupBy(new ClientQueryGroupBy().setGroups(getGroups()));
        }
        if (getOrder() != null) {
            query.setOrderBy((List<ClientGenericOrder>) getOrder());
        }

        return query;
    }

    @Override
    public MultiLevelQueryBuilder where(ClientExpression filters) {
        return (MultiLevelQueryBuilder)super.where(filters);
    }

    @Override
    public MultiLevelQueryBuilder where(String expression) {
        return (MultiLevelQueryBuilder)super.where(expression);
    }

    @Override
    public MultiLevelQueryBuilder where(Map<String, ClientExpressionContainer> parameters) {
        return (MultiLevelQueryBuilder)super.where(parameters);
    }

    @Override
    public MultiLevelQueryBuilder where(ClientExpression filters, Map<String, ClientExpressionContainer> parameters) {
        return (MultiLevelQueryBuilder)super.where(filters, parameters);
    }

    @Override
    public MultiLevelQueryBuilder orderBy(ClientOrder... order) {
        return (MultiLevelQueryBuilder)super.orderBy(order);
    }

    public MultiLevelQueryBuilder aggregates(ClientQueryAggregatedField... aggregatedFields) {
        setAggregates(asList(aggregatedFields));
        return this;
    }

    public MultiLevelQueryBuilder groupBy(ClientQueryGroup... groups) {
        setGroups(asList(groups));
        return this;
    }

    protected List<? extends ClientQueryGroup> getGroups() {
        return groups;
    }

    protected void setGroups(List<? extends ClientQueryGroup> groups) {
        this.groups = groups;
    }

    protected List<ClientQueryAggregatedField> getAggregates() {
        return aggregates;
    }

    protected void setAggregates(List<ClientQueryAggregatedField> aggregates) {
        this.aggregates = aggregates;
    }
}
