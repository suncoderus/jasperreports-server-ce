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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author Stas Chubar (schubar@tibco.com)
 * @version $Id$
 */
public class ValuesQueryBuilder extends ClientQueryBuilder {

    public static ValuesQueryBuilder select(List<ClientQueryField> fields) {
        ValuesQueryBuilder qb = new ValuesQueryBuilder();
        qb.setFields(fields);

        return qb;
    }

    @Override
    public ClientQuery build() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ValuesQueryBuilder where(ClientExpression filters) {
        return (ValuesQueryBuilder) super.where(filters);
    }

    @Override
    public ValuesQueryBuilder where(String expression) {
        return (ValuesQueryBuilder) super.where(expression);
    }

    @Override
    public ValuesQueryBuilder where(ClientExpression filters, Map<String, ClientExpressionContainer> parameters) {
        return (ValuesQueryBuilder) super.where(filters, parameters);
    }

    @Override
    public ValuesQueryBuilder orderBy(ClientOrder... order) {
        return (ValuesQueryBuilder) super.orderBy(order);
    }
}
