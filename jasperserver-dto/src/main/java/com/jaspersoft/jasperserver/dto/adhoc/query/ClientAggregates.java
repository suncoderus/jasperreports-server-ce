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


/**
 * @author Stas Chubar,                                                                                                                                                                                 Andriy G.
 * @version $Id$
 *
 * Provided aggregations
 * TODO: Try to use expression evaluator to generate expr
 */
public enum ClientAggregates {
    COUNT_ALL("CountAll"),
    COUNT_DISTINCT("CountDistinct"),
    MODE("Mode"),
    SUM("Sum"),
    AGGREGATE_FORMULA("AggregateFormula"),
    AVERAGE("Average");

    private String funName;

    ClientAggregates(String funName) {
        this.funName = funName;
    }

    public String getFunName() {
        return funName;
    }

    public static ClientQueryAggregatedField countAll(ClientQueryField field) {
        return aggregate(field, COUNT_ALL.getFunName(), null);
    }

    public static ClientQueryAggregatedField countAll(ClientDataSourceField field) {
        return aggregate(field, COUNT_ALL.getFunName(), null);
    }

    public static ClientQueryAggregatedField sum(String id, ClientQueryField field) {
        return aggregate(id, field, SUM.getFunName(), null);
    }

    public static ClientQueryAggregatedField sum(ClientQueryField field) {
        return aggregate(field, SUM.getFunName(), null);
    }

    public static ClientQueryAggregatedField sum(ClientDataSourceField field) {
        return aggregate(field, SUM.getFunName(), null);
    }

    public static ClientQueryAggregatedField aggregateFormula(ClientDataSourceField field) {
        return aggregate(field, AGGREGATE_FORMULA.getFunName(), null);
    }

    public static ClientQueryAggregatedField aggregateFormula(String field) {
        return aggregate(field).setAggregateFunction(AGGREGATE_FORMULA.getFunName());
    }


    public static ClientQueryAggregatedField countDistinct(ClientQueryField field) {
        return aggregate(field, COUNT_DISTINCT.getFunName(), null);
    }

//    public static ClientQueryAggregatedField mode(ClientQueryField field, Object ... args) {
//        return aggregate(field, MODE.getFunName(), asList(args));
//    }
//
//    public static ClientQueryAggregatedField aggregate(ClientQueryField field, String name) {
//        return aggregate(field, name, (java.util.List) null);
//    }
//

    public static ClientQueryAggregatedField aggregate(ClientQueryField field, String name, String arg) {
        return aggregate(null, field, name, null, arg);
    }

    public static ClientQueryAggregatedField aggregate(String id, ClientQueryField field, String name, String arg) {
        return aggregate(id, field, name, null, arg);
    }

    private static String buildExpression(String field, String functionName, String arg) {
        String expr;
        if (arg == null || arg.isEmpty()) {
            expr = String.format("%s(%s)", functionName, field);
        } else {
            expr = String.format("%s(%s, %s)", functionName, field, arg);
        }
        return expr;
    }

    public static ClientQueryAggregatedField aggregate(String id, ClientQueryField field, String funcName, String expr, String args) {
        String fieldRef = field.getId();
        if (fieldRef == null) {
            fieldRef = field.getFieldName();
        }
        return new ClientQueryAggregatedField()
                .setId(id)
                .setFieldReference(fieldRef)
                .setAggregateFunction(funcName)
                .setExpressionContainer(expr != null ? new ClientExpressionContainer(expr) : null)
                .setAggregateArg(args);
    }

    public static ClientQueryAggregatedField aggregate(ClientDataSourceField field, String name, String arg) {
        return aggregate(field, name, null, arg);
    }


    public static ClientQueryAggregatedField aggregate(ClientDataSourceField field, String funcName, String expr, String args) {
        return aggregate(null, field, funcName, expr, args);
    }

    public static ClientQueryAggregatedField aggregate(String id, ClientDataSourceField field, String funcName, String expr, String args) {
        return new ClientQueryAggregatedField()
                .setDataSourceField(field)
                .setAggregateFunction(funcName)
                .setExpressionContainer(expr != null ? new ClientExpressionContainer(expr) : null)
                .setAggregateArg(args);
    }

    public static ClientQueryAggregatedField aggregate(ClientDataSourceField field) {
        return new ClientQueryAggregatedField()
                .setDataSourceField(field);
    }


    public static ClientQueryAggregatedField aggregate(ClientQueryField field) {
        return new ClientQueryAggregatedField()
                .setFieldReference(field.getId() == null ? field.getFieldName() : field.getId());
    }

    public static ClientQueryAggregatedField custom(ClientQueryField field, String expr) {
        return aggregate(field)
                .setExpressionContainer(expr != null ? new ClientExpressionContainer(expr) : null);
    }

    public static ClientQueryAggregatedField custom(ClientDataSourceField field, String expr) {
        return aggregate(field)
                .setExpressionContainer(expr != null ? new ClientExpressionContainer(expr) : null);
    }

    public static ClientQueryAggregatedField custom(ClientQueryField field, ClientExpression expr) {
        return aggregate(field)
                .setExpressionContainer(expr != null ? new ClientExpressionContainer(expr) : null);
    }

    public static ClientQueryAggregatedField custom(ClientDataSourceField field, ClientExpression expr) {
        return aggregate(field)
                .setExpressionContainer(expr != null ? new ClientExpressionContainer(expr) : null);
    }

    public static ClientQueryAggregatedField custom(String id, ClientExpression expr) {
        return new ClientQueryAggregatedField()
                .setId(id)
                .setExpressionContainer(expr != null ? new ClientExpressionContainer(expr) : null);
    }

    public static ClientQueryAggregatedField aggregate(String field) {
        return new ClientQueryAggregatedField()
                .setFieldReference(field);
    }

}