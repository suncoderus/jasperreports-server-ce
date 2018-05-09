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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientList;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.CopyFactory;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = "in")
public class ClientIn extends ClientOperator<ClientIn> {

    public static final String OPERATOR_ID = "in";

    public ClientIn() {
        super(ClientOperation.IN.getName());
    }

    public ClientIn(ClientVariable variable, List<ClientExpression> list) {
        this(variable, (ClientExpression) new ClientList(list));
    }

    public ClientIn(ClientVariable variable, ClientRange range) {
        this(variable, (ClientExpression) range);
    }

    public ClientIn(ClientVariable variable, ClientList list) {
        this(variable, (ClientExpression) list);
    }

    public ClientIn(ClientVariable variable, ClientVariable inVariable) {
        this(variable, (ClientExpression) inVariable);
    }


    public ClientIn(ClientExpression lhs, ClientExpression rhs) {
        this();
        if (compatibleRhs(rhs)) {
            this.operands.add(lhs);
            this.operands.add(rhs);
        } else {
            throw new IllegalArgumentException("The RHS of an In expression may only be a List, Range or Variable");
        }
    }

    public ClientIn(ClientIn in) {
        this();
        if (in.getLhs() != null) {
            this.operands.set(0, CopyFactory.copy(in.getLhs()));
        }
        if (in.getRhs() != null) {
            this.operands.set(1, new ClientList(CopyFactory.copy(in.getRhs())));
        }
    }

    private static boolean compatibleRhs(ClientExpression expression) {
        return expression instanceof ClientRange
                || expression instanceof ClientList
                || expression instanceof ClientVariable;
    }

    @XmlTransient
    public ClientExpression getLhs() {
        return (this.operands.get(0) == null) ? null : this.operands.get(0);
    }

    @XmlTransient
    public List<ClientExpression> getRhs() {
        if (this.operands != null && this.operands.size() > 1) {
            return this.operands.subList(1, this.operands.size());
        } else {
            throw new IllegalAccessError("Unable to find RHS of In expression");
        }
    }

    @XmlTransient
    public boolean isRange() {
        return this.operands != null && this.operands.size() > 1 && this.operands.get(1) instanceof ClientRange;
    }

    @XmlTransient
    public boolean isList() {
        return this.operands != null && this.operands.size() > 1 && this.operands.get(1) instanceof ClientList;
    }

    @XmlTransient
    public boolean isVariable() {
        return this.operands != null && this.operands.size() > 1 && this.operands.get(1) instanceof ClientVariable;
    }

    @XmlTransient
    public ClientRange getRhsRange() {
        return isRange() ? (ClientRange) this.operands.get(1) : null;
    }

    @XmlTransient
    public ClientList getRhsList() {
        return isList() ? (ClientList) this.operands.get(1) : null;
    }

    @XmlTransient
    public ClientVariable getRhsVariable() {
        return isVariable() ? (ClientVariable) this.operands.get(1) : null;
    }

    @Override
    public String toString() {
        final String lhs = (getLhs() != null) ? getLhs().toString() : ClientExpressions.MISSING_REPRESENTATION;
        final String rhs;
        if (isRange()) {
            rhs = (getRhs() != null) ? getRhsRange().toString() : ClientExpressions.MISSING_REPRESENTATION;
        } else if (isList()) {
            rhs = (getRhs() != null) ? getRhsList().toString() : ClientExpressions.MISSING_REPRESENTATION;
        } else if (isVariable()) {
            rhs = (getRhs() != null) ? getRhsVariable().toString() : ClientExpressions.MISSING_REPRESENTATION;
        } else {
            rhs = (getRhs() != null) ? getRhsList().toString() : ClientExpressions.MISSING_REPRESENTATION;
        }

        return lhs + " " + this.getOperator() + " " + rhs;
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        if (getLhs() != null) {
            getLhs().accept(visitor);
        }

        if (getRhs() != null) {
            for (ClientExpression expr : getRhs()) {
                if (expr != null) {
                    expr.accept(visitor);
                }
            }
        }
        visitor.visit(this);
    }

    @Override
    public ClientIn deepClone() {
        return new ClientIn(this);
    }
}
