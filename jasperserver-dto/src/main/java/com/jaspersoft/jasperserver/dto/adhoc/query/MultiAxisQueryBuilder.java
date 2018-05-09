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
import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientExpandable;
import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientLevelExpansion;
import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientMemberExpansion;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientMultiAxisGroupBy;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxisEnum;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientLevelAxis;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientMemberExpansion.PATH_SEPARATOR;
import static java.util.Arrays.asList;

/**
 * @author Andriy Godovanets
 * @author Stas Chubar <schubar@tibco.com>

 * @version $Id$
 */
public class MultiAxisQueryBuilder extends ClientQueryBuilder {
    private List<ClientQueryAggregatedField> aggregates;
    private List<? extends ClientQueryLevel> rowLevels;
    private List<? extends ClientQueryLevel> columnLevels;
    private List<ClientExpandable> rowExpansions;
    private List<ClientExpandable> columnExpansions;

    public static MultiAxisQueryBuilder select(List<ClientQueryAggregatedField> aggregates) {
        MultiAxisQueryBuilder qb = new MultiAxisQueryBuilder();
        qb.setAggregates(aggregates);
        return qb;
    }

    public static MultiAxisQueryBuilder select() {
        return select(Collections.<ClientQueryAggregatedField>emptyList());
    }
    public static MultiAxisQueryBuilder select(ClientQueryAggregatedField... aggregates) {
        return select(asList(aggregates));
    }

    public static MultiAxisQueryBuilder select(ClientDataSourceField ... dataSourceFields) {
        List<ClientQueryAggregatedField> aggregates = new ArrayList<ClientQueryAggregatedField>();
        for (ClientDataSourceField dsField : dataSourceFields) {

            aggregates.add(new ClientQueryAggregatedField()
                    .setDataSourceField(dsField));

        }

        return select(aggregates);
    }

    @Override
    public ClientMultiAxisQuery build() {
        ClientMultiAxisQuery query = new ClientMultiAxisQuery();
        buildCommon(query);

        if (getAggregates() != null) {
            query.getSelect().setAggregations(getAggregates());
        }

        if (rowLevels != null || columnLevels != null) {
            ClientMultiAxisGroupBy groupBy = new ClientMultiAxisGroupBy();
            if (rowLevels != null) {
                groupBy.addAxis(ClientGroupAxisEnum.ROWS, new ClientLevelAxis(rowLevels, rowExpansions));
            }
            if (columnLevels != null) {
                groupBy.addAxis(ClientGroupAxisEnum.COLUMNS, new ClientLevelAxis(columnLevels, columnExpansions));
            }
            query.setGroupBy(groupBy);
        }

        if (getOrder() != null) {
            query.setOrderBy(getOrder());
        }

        return query;
    }

    /* Builders */
    public static ClientDataSourceLevel sourceLevel(String levelName, String dimensionName) {
        ClientDataSourceLevel sourceLevel = new ClientDataSourceLevel();
        sourceLevel.setName(levelName);
        sourceLevel.setDimensionName(dimensionName);

        return sourceLevel;
    }

    public MultiAxisQueryBuilder groupByRows(ClientQueryLevel... cityDSLevel) {
        setRowLevels(asList(cityDSLevel));
        return this;
    }

    public MultiAxisQueryBuilder groupByColumns(ClientQueryLevel... cityDSLevel) {
        setColumnLevels(asList(cityDSLevel));
        return this;
    }

    public MultiAxisQueryBuilder expandInRows(ClientExpandable... expansions) {
        setRowExpansions(asList(expansions));
        return this;
    }

    public MultiAxisQueryBuilder expandInRows(String... ids) {
        List<ClientExpandable> expansions = new ArrayList<ClientExpandable>();

        for (String id : ids) {
            expansions.add(expansion(id, true));
        }

        setRowExpansions(expansions);
        return this;
    }

    public MultiAxisQueryBuilder expandInRows(ClientIdentifiable<String>... identifiables) {
        if (identifiables == null) {
            throw new IllegalArgumentException("Specify at least one level to expand");
        }
        List<String> ids = new ArrayList<String>();
        for (ClientIdentifiable<String> identifiable : identifiables) {
            ids.add(identifiable.getId());
        }
        return expandInRows(ids.toArray(new String[ids.size()]));
    }

    public MultiAxisQueryBuilder expandInColumns(ClientExpandable... expansions) {
        setColumnExpansions(asList(expansions));
        return this;
    }

    public MultiAxisQueryBuilder expandInColumns(String... ids) {
        List<ClientExpandable> expansions = new ArrayList<ClientExpandable>();

        for (String id : ids) {
            expansions.add(expansion(id, true));
        }

        setColumnExpansions(expansions);
        return this;
    }

    public MultiAxisQueryBuilder collapseInColumns(String... ids) {
        List<ClientExpandable> expansions = new ArrayList<ClientExpandable>();

        for (String id : ids) {
            expansions.add(expansion(id, false));
        }

        setColumnExpansions(expansions);
        return this;
    }

    public MultiAxisQueryBuilder collapseInRows(String... ids) {
        List<ClientExpandable> expansions = new ArrayList<ClientExpandable>();

        for (String id : ids) {
            expansions.add(expansion(id, false));
        }

        setRowExpansions(expansions);
        return this;
    }

    @Override
    public MultiAxisQueryBuilder orderBy(ClientOrder... order) {
        return (MultiAxisQueryBuilder) super.orderBy(order);
    }

    @Override
    public MultiAxisQueryBuilder limit(Integer limit) {
        return (MultiAxisQueryBuilder) super.limit(limit);
    }

    /* Setters and Getters */

    protected List<ClientQueryAggregatedField> getAggregates() {
        return aggregates;
    }

    protected void setAggregates(List<ClientQueryAggregatedField> aggregates) {
        this.aggregates = aggregates;
    }

    public List<? extends ClientQueryLevel> getColumnLevels() {
        return columnLevels;
    }

    public void setColumnLevels(List<? extends ClientQueryLevel> columnLevels) {
        this.columnLevels = columnLevels;
    }

    public List<? extends ClientQueryLevel> getRowLevels() {
        return rowLevels;
    }

    public void setRowLevels(List<? extends ClientQueryLevel> rowLevels) {
        this.rowLevels = rowLevels;
    }

    public List<? extends ClientExpandable> getColumnExpansions() {
        return columnExpansions;
    }

    public void setColumnExpansions(List<ClientExpandable> columnExpansions) {
        this.columnExpansions = columnExpansions;
    }

    public List<? extends ClientExpandable> getRowExpansions() {
        return rowExpansions;
    }

    public void setRowExpansions(List<ClientExpandable> rowExpansions) {
        this.rowExpansions = rowExpansions;
    }

    /* Helpers */
    @Deprecated
    public static ClientQueryLevel level(String id, ClientDataSourceField dsLevel) {
        return (ClientQueryLevel) new ClientQueryLevel()
                .setId(id)
                .setDataSourceField(dsLevel);
    }

    @Deprecated
    public static ClientQueryLevel level(String id, ClientDataSourceLevel dsLevel) {
        return new ClientQueryLevel()
                .setId(id)
                .setDataSourceField(dsLevel);
    }

    @Deprecated
    public static ClientQueryLevel level(String id, String fieldName, String categorizer) {
        return new ClientQueryLevel()
                .setId(id)
                .setFieldName(fieldName)
                .setCategorizer(categorizer);
    }

    @Deprecated
    public static ClientQueryLevel level(ClientDataSourceField clientLevel) {
        return new ClientQueryLevel()
                .setDataSourceField(clientLevel);
    }

    @Deprecated
    public static ClientQueryLevel level(ClientDataSourceLevel clientLevel) {
        return new ClientQueryLevel()
                .setDataSourceField(clientLevel);
    }

    @Deprecated
    public static ClientQueryLevel allLevel(String fieldName, String dimension) {
        return new ClientQueryLevel.ClientAllLevel(dimension).setFieldName(fieldName);
    }

    @Deprecated
    public static ClientQueryLevel allLevel(String dimension) {
        return new ClientQueryLevel.ClientAllLevel(dimension);
    }


    public static ClientQueryLevel aggRef() {
        return new ClientQueryLevel.ClientLevelAggregationsRef();
    }

    public static ClientLevelExpansion expand(String id) {
        return new ClientLevelExpansion()
                .setLevelReference(id)
                .setExpanded(true);
    }

    public static ClientMemberExpansion expandMember(String id) {
        return new ClientMemberExpansion()
                .setPath(sanitizePathList(asList(id.split(PATH_SEPARATOR))))
                .setExpanded(true);
    }

    public static ClientLevelExpansion expandAggLevel() {
        return new ClientLevelExpansion()
                .setAggregationLevel(true)
                .setExpanded(true);
    }

    public static ClientLevelExpansion collapse(String id) {
        return new ClientLevelExpansion()
                .setLevelReference(id)
                .setExpanded(false);
    }

    public static ClientMemberExpansion collapseMember(String id) {
        return new ClientMemberExpansion()
                .setPath(sanitizePathList(asList(id.split(PATH_SEPARATOR))))
                .setExpanded(false);
    }

    public static ClientExpandable expansion(String id, boolean isExpanded) {
        ClientExpandable expandable = id.startsWith(PATH_SEPARATOR)
                ? new ClientMemberExpansion()
                        .setPath(sanitizePathList(asList(id.split(PATH_SEPARATOR))))

                : new ClientLevelExpansion()
                        .setLevelReference(id);

        return expandable.setExpanded(isExpanded);
    }

    public static List<String> sanitizePathList(List<String> pathSegments) {
        List<String> sanitizedList = new ArrayList<String>();
        for (String pathSegment : pathSegments) {
            if (pathSegment != null && pathSegment.length() > 0) {
                sanitizedList.add(pathSegment);
            }
        }
        return sanitizedList;
    }

}
