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

import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceField;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientPathOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientTopOrBottomNOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * @author Andriy Godovanets
 * @author Stas Chubar <schubar@tibco.com>
 *
 * @version $Id$
 */
public abstract class ClientQueryBuilder {
    private List<ClientQueryField> fields;
    private ClientExpression filters;
    private String filterExpression;
    private List<ClientOrder> order;
    private Map<String, ClientExpressionContainer> parameters;
    private Integer limit;

    public abstract ClientQuery build();

    @Deprecated
    public static ClientDataSourceField source(String name) {
        return new ClientDataSourceField()
                .setName(name);
    }

    @Deprecated
    public static ClientDataSourceField source(String name, String type) {
        return source(name)
                .setType(type);
    }

    // TODO: we should move all these field, source etc builder methods to some separate ClientFields class
    @Deprecated
    public static ClientQueryField field(ClientDataSourceField field) {
        return new ClientQueryField()
                .setDataSourceField(field);
    }

    @Deprecated
    public static ClientQueryField field(String id, ClientDataSourceField field) {
        return new ClientQueryField()
                .setId(id)
                .setDataSourceField(field);
    }

    @Deprecated
    public static ClientQueryField field(String name) {
        return new ClientQueryField()
                .setFieldName(name);
    }

    @Deprecated
    public static ClientQueryAggregatedField aggregatedField(ClientDataSourceField field, String aggregateExpression) {
        return new ClientQueryAggregatedField()
                .setDataSourceField(field)
                .setExpressionContainer(new ClientExpressionContainer(aggregateExpression));
    }

    @Deprecated
    public static ClientQueryAggregatedField aggregatedField(ClientDataSourceField field, ClientExpression aggregateExpression) {
        return new ClientQueryAggregatedField()
                .setDataSourceField(field)
                .setExpressionContainer(new ClientExpressionContainer(aggregateExpression));
    }

    @Deprecated
    public static ClientQueryAggregatedField aggregatedField(String id, String fieldName, String aggregateFunction) {
        return new ClientQueryAggregatedField()
                .setId(id)
                .setFieldReference(fieldName)
                .setAggregateFunction(aggregateFunction);
    }

    @Deprecated
    public static ClientOrder asc(ClientDataSourceField dsField) {
        return new ClientGenericOrder()
                .setFieldReference(dsField.getName())
                .setAscending(true);
    }

    @Deprecated
    public static ClientOrder asc(ClientQueryGroup queryField) {
        return new ClientGenericOrder()
                .setAscending(true)
                .setFieldReference(queryField.getId());
    }

    @Deprecated
    public static ClientOrder asc(String dsField) {
        return new ClientGenericOrder()
                .setFieldReference(dsField)
                .setAscending(true);
    }

    @Deprecated
    public static ClientOrder desc(ClientDataSourceField dsField) {
        return new ClientGenericOrder()
                .setFieldReference(dsField.getName())
                .setAscending(false);
    }

    @Deprecated
    public static ClientOrder desc(ClientQueryGroup queryField) {
        return new ClientGenericOrder()
                .setFieldReference(queryField.getId())
                .setAscending(false);
    }

    @Deprecated
    public static ClientOrder desc(String dsField) {
        return new ClientGenericOrder()
                .setFieldReference(dsField)
                .setAscending(false);
    }

    @Deprecated
    public static ClientOrder descAggLevel() {
        return new ClientGenericOrder()
                .setAggregation(true)
                .setAscending(false);
    }

    @Deprecated
    public static ClientOrder ascAggLevel() {
        return new ClientGenericOrder()
                .setAggregation(true)
                .setAscending(true);
    }

    @Deprecated
    public static ClientOrder ascByMember(List<String> path) {
        return new ClientPathOrder()
                .setPath(path)
                .setAscending(true);
    }

    @Deprecated
    public static ClientOrder descByMember(List<String> path) {
        return new ClientPathOrder()
                .setPath(path)
                .setAscending(false);
    }

    @Deprecated
    public static ClientTopOrBottomNOrder topN(List<String> path, int limit) {
        return new ClientTopOrBottomNOrder.ClientTopNOrder()
                .setLimit(limit)
                .setPath(path);
    }

    @Deprecated
    public static ClientTopOrBottomNOrder bottomN(List<String> path, int limit) {
        return new ClientTopOrBottomNOrder.ClientBottomNOrder()
                .setLimit(limit)
                .setPath(path);
    }

//    public static ClientQueryAggregatedField aggregatedField(ClientDataSourceField field, ClientFunction aggregateFunction) {
//        ClientQueryField newQueryFiled = new ClientQueryField("generated", field);
//        return new ClientQueryAggregatedField(newQueryFiled, aggregateFunction);
//    }

    @Deprecated
    public static ClientQueryGroup group(String id, String categorizer, ClientDataSourceField field) {
        return new ClientQueryGroup()
                .setDataSourceField(field)
                .setId(id)
                .setCategorizer(categorizer);
    }

    @Deprecated
    public static ClientQueryGroup group(ClientDataSourceField field) {
        return new ClientQueryGroup()
                .setDataSourceField(field);
    }

    @Deprecated
    public static ClientQueryGroup group(String id, ClientDataSourceField field) {
        return new ClientQueryGroup()
                .setId(id)
                .setDataSourceField(field);
    }

    @Deprecated
    public static ClientQueryGroup group(String id, String categorizer) {
        return new ClientQueryGroup()
                .setId(id)
                .setCategorizer(categorizer);
    }

    @Deprecated
    public static ClientQueryGroup allGroup() {
        return new ClientQueryGroup.ClientAllGroup();
    }

    public ClientQueryBuilder where(ClientExpression filters) {
        setFilters(filters);
        return this;
    }

    public ClientQueryBuilder where(String expression) {
        setFilterExpression(expression);
        return this;
    }

    public ClientQueryBuilder where(Map<String, ClientExpressionContainer> parameters) {
        setParameters(parameters);
        return this;
    }

    public ClientQueryBuilder orderBy(ClientOrder... order) {
        setOrder(asList(order));
        return this;
    }

    public ClientQueryBuilder where(ClientExpression filters, Map<String, ClientExpressionContainer> parameters) {
        where(filters);
        setParameters(parameters);
        return this;
    }

    public ClientQueryBuilder limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    protected ClientQuery buildCommon(ClientQuery query) {
        query.setSelect(getFields() != null
                ? new ClientSelect(getFields())
                : new ClientSelect());

        if (getFilterExpression() != null) {
            query.setWhere(new ClientWhere(new ClientExpressionContainer(getFilterExpression())));
        }
        if (getFilters() != null) {
            query.setWhere(new ClientWhere(getFilters()));
        }

        if (getParameters() != null) {
            if (query.getWhere() == null) {
                query.setWhere(new ClientWhere(getParameters()));
            } else {
                query.getWhere().setParameters(getParameters());
            }
        }

        query.setLimit(getLimit());
        return query;
    }

    protected List<ClientQueryField> getFields() {
        return fields;
    }

    protected void setFields(List<ClientQueryField> fields) {
        this.fields = fields;
    }

    protected ClientExpression getFilters() {
        return filters;
    }

    protected void setFilters(ClientExpression filters) {
        this.filters = filters;
    }

    protected String getFilterExpression() {
        return filterExpression;
    }

    protected void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }

    protected Map<String, ClientExpressionContainer> getParameters() {
        return parameters;
    }

    protected void setParameters(Map<String, ClientExpressionContainer> parameters) {
        this.parameters = parameters;
    }

    protected List<? extends ClientOrder> getOrder() {
        return order;
    }

    protected void setOrder(List<ClientOrder> order) {
        this.order = order;
    }

    protected Integer getLimit() {
        return limit;
    }

    protected void setLimit(Integer limit) {
        this.limit = limit;
    }
}
