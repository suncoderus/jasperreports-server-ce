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
package com.jaspersoft.jasperserver.dto.adhoc.query.filter;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;

import javax.xml.bind.annotation.XmlTransient;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.list;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.string;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
public class ClientFilter extends ClientOperator<ClientFilter> {

    public static final String FILTER = "filter";

    private String id;
    private ClientFilterHint hint;
    private ClientExpression expression;


    public ClientFilter(String id, ClientExpression expression) {
        super(FILTER, list(string(id), string(ClientFilterHint.DYNAMIC.toString()), expression));

        this.id = id;
        setHint(ClientFilterHint.DYNAMIC);
        setExpression(expression);
    }

    public ClientFilter(String id, ClientFilterHint hint, ClientExpression expression) {
        this(id, expression);

        setHint(hint);
    }

    public ClientFilter(ClientFilter filter) {
        super(FILTER, list(string(filter.getId()), string(filter.getHint().toString()), filter.getExpression()), filter.paren);

        this.id = filter.getId();
        this.hint = filter.getHint();
        this.expression = filter.getExpression();
        this.paren = filter.paren;
    }

    @XmlTransient
    public String getId() {
        return id;
    }

    @XmlTransient
    public ClientFilterHint getHint() {
        return hint;
    }

    @XmlTransient
    public ClientExpression getExpression() {
        return expression;
    }

    public ClientFilter setId(String id) {
        this.id = id;
        if (getOperands() != null && !operands.isEmpty()) {
            operands.set(0, new ClientString(id));
        }
        return this;
    }

    public ClientFilter setHint(ClientFilterHint hint) {
        this.hint = hint;
        if (getOperands() != null && operands.size() > 0) {
            operands.set(1, new ClientString(hint.toString()));
        }
        return this;
    }

    public ClientFilter setExpression(ClientExpression expr) {
        expression = expr;
        if (getOperands() != null && operands.size() > 1) {
            operands.set(2, expr);
        }
        return this;
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        if (expression != null) {
            expression.accept(visitor);
        }
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientFilter that = (ClientFilter) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (hint != that.hint) return false;
        return expression != null ? expression.equals(that.expression) : that.expression == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (hint != null ? hint.hashCode() : 0);
        result = 31 * result + (expression != null ? expression.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String args = "";
        int len = (getOperands() != null) ? getOperands().size() : 0;
        if (len > 0) {
            StringBuilder sb = new StringBuilder(getOperands().get(0).toString());
            for (int i = 1; i < len; i++) {
                if (getOperands().get(i) != null) {
                    sb.append(", ");
                    sb.append(getOperands().get(i).toString());
                }
            }
            args = sb.toString();
        }
        return FILTER + "(" + args + ")";
    }

    @Override
    public ClientFilter deepClone() {
        return new ClientFilter(this);
    }
}
