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

import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryClause;
import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientList;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ParameterExpressionsMapXmlAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeTimestampRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientFloat;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDouble;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLong;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientShort;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientByte;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigDecimal;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.*;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import com.jaspersoft.jasperserver.dto.adhoc.query.validation.CheckExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.validation.CheckParametersExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.validation.ParameterMap;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.adhoc.query.validation.CheckExpressionContainerValidator.WHERE_EXPRESSION_NOT_VALID;

/**
 * Created by stas on 4/8/15.
 */
@XmlRootElement(name = "where")
public class ClientWhere implements ClientQueryClause {

    @CheckExpressionContainer(message = WHERE_EXPRESSION_NOT_VALID,
            value = {
                    ClientNot.class,
                    ClientAnd.class,
                    ClientOr.class,
                    ClientGreater.class,
                    ClientGreaterOrEqual.class,
                    ClientLess.class,
                    ClientLessOrEqual.class,
                    ClientNotEqual.class,
                    ClientEquals.class,
                    ClientFunction.class,
                    ClientIn.class,
                    ClientList.class
    })
    @Valid
    private ClientExpressionContainer filterExpression;

    @ParameterMap
    @CheckParametersExpressionContainer(
            value = {
                    ClientNull.class,
                    ClientBoolean.class,
                    ClientByte.class,
                    ClientShort.class,
                    ClientInteger.class,
                    ClientLong.class,
                    ClientBigInteger.class,
                    ClientFloat.class,
                    ClientDouble.class,
                    ClientBigDecimal.class,
                    ClientString.class,
                    ClientDate.class,
                    ClientTime.class,
                    ClientTimestamp.class,
                    ClientList.class,
                    ClientRelativeDateRange.class,
                    ClientRelativeTimestampRange.class})
    @Valid
    private Map<String, ClientExpressionContainer> parameters;

    public ClientWhere() {

    }

    public ClientWhere(ClientWhere where) {
        if (where != null) {
            this.setFilterExpression(where.getFilterExpression())
                    .setParameters(
                            where.getParameters() != null ? new HashMap<String, ClientExpressionContainer>(where.getParameters()) : null);
        }
    }

    public ClientWhere(ClientExpressionContainer container) {
        this.filterExpression = container;
    }

    public ClientWhere(ClientExpression filters) {
        if (filters != null) {
            this.filterExpression = new ClientExpressionContainer().setObject(filters);
        }
    }

    public ClientWhere(String expression) {
        if (expression != null) {
            this.filterExpression = new ClientExpressionContainer().setString(expression);
        }
    }

    public ClientWhere(Map<String, ClientExpressionContainer> parameters) {
        this.parameters = parameters;
    }

    public ClientWhere(ClientExpression filters, Map<String, ClientExpressionContainer> parameters) {
        if (filters != null) {
            this.filterExpression = new ClientExpressionContainer().setObject(filters);
        }
        this.parameters = parameters;
    }

    public ClientExpressionContainer getFilterExpression() {
        return filterExpression;
    }

    public ClientWhere setFilterExpression(ClientExpressionContainer filterExpression) {
        this.filterExpression = filterExpression;
        return this;
    }

    @XmlJavaTypeAdapter(ParameterExpressionsMapXmlAdapter.class)
    public Map<String, ClientExpressionContainer> getParameters() {
        return parameters;
    }

    @ParameterMap
    public ClientWhere setParameters(Map<String, ClientExpressionContainer> parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    public void accept(ClientQueryVisitor visitor) {
        if (this.filterExpression != null && this.filterExpression.getObject() != null) {
            this.filterExpression.getObject().accept(visitor);
        }
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientWhere that = (ClientWhere) o;

        if (filterExpression != null ? !filterExpression.equals(that.filterExpression) : that.filterExpression != null)
            return false;
        return !(parameters != null ? !parameters.equals(that.parameters) : that.parameters != null);

    }

    @Override
    public int hashCode() {
        int result = filterExpression != null ? filterExpression.hashCode() : 0;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientWhere{" +
                "filterExpression=" + filterExpression +
                ", parameters=" + parameters +
                '}';
    }
}
