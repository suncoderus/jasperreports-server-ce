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
package com.jaspersoft.jasperserver.dto.domain;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class DomElExpressionContext extends BaseDomElContext<DomElExpressionContext> {
    @Valid
    @NotNull
    private ClientExpressionContainer expression;

    public DomElExpressionContext(){}

    public DomElExpressionContext(DomElExpressionContext source) {
        super(source);
        final ClientExpressionContainer sourceExpression = source.getExpression();
        this.expression = sourceExpression != null ? sourceExpression.deepClone() : null;
    }

    public DomElExpressionContext(BaseDomElContext baseDomElContext, ClientExpressionContainer expression){
        super(baseDomElContext);
        this.expression = expression;
    }

    public ClientExpressionContainer getExpression() {
        return expression;
    }

    public DomElExpressionContext setExpression(ClientExpressionContainer expression) {
        this.expression = expression;
        return this;
    }

    @Override
    public DomElExpressionContext deepClone() {
        return new DomElExpressionContext(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomElExpressionContext)) return false;
        if (!super.equals(o)) return false;

        DomElExpressionContext that = (DomElExpressionContext) o;

        return expression != null ? expression.equals(that.expression) : that.expression == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (expression != null ? expression.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DomElExpressionContext{" +
                "expression=" + expression +
                "} " + super.toString();
    }
}
