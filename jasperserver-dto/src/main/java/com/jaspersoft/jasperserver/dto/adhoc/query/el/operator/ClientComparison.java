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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreater;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreaterOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLess;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLessOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation.EQUALS;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation.GREATER;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation.GREATER_OR_EQUAL;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation.LESS;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation.LESS_OR_EQUAL;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation.NOT_EQUAL;
import static java.util.Arrays.asList;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = "comparison")
public abstract class ClientComparison<F extends ClientComparison<F>> extends ClientOperator<F> {

    private ClientOperation type;

    protected ClientComparison() {
        super();
        this.type = ClientOperation.UNDEFINED;
    }

    protected ClientComparison(ClientOperation operation) {
        super(operation.getName());
        this.type = operation;
    }

    protected ClientComparison(ClientOperation operation, List<? extends ClientExpression> operands) {
        super(operation.getName());

        this.type = operation;
        setOperands(new ArrayList<ClientExpression>(operands));
    }


    public static ClientComparison eq(ClientExpression lhs, ClientExpression rhs) {
        return new ClientEquals(asList(lhs, rhs));
    }

    public static ClientGreaterOrEqual gtOrEq(ClientExpression lhs, ClientExpression rhs) {
        return new ClientGreaterOrEqual(asList(lhs, rhs));
    }

    public static ClientLessOrEqual ltOrEq(ClientExpression lhs, ClientExpression rhs) {
        return new ClientLessOrEqual(asList(lhs, rhs));
    }

    public static ClientGreater gt(ClientExpression lhs, ClientExpression rhs) {
        return new ClientGreater(asList(lhs, rhs));
    }

    public static ClientComparison lt(ClientExpression lhs, ClientExpression rhs) {
        return new ClientLess(asList(lhs, rhs));
    }

    public static ClientComparison notEq(ClientExpression lhs, ClientExpression rhs) {
        return new ClientNotEqual(asList(lhs, rhs));
    }

    public static ClientComparison createComparison(String name, List<ClientExpression> operands) {
        ClientOperation operation = ClientOperation.fromString(name);
        if (operation != null && ClientOperation.isSupported(operation.getName())) {
            if (operation.equals(ClientOperation.GREATER)) {
                return new ClientGreater(operands);
            } else if (operation.equals(ClientOperation.GREATER_OR_EQUAL)) {
                return new ClientGreaterOrEqual(operands);
            } else if (operation.equals(ClientOperation.LESS)) {
                return new ClientLess(operands);
            } else if (operation.equals(ClientOperation.LESS_OR_EQUAL)) {
                return new ClientLessOrEqual(operands);
            } else if (operation.equals(ClientOperation.EQUALS)) {
                return new ClientEquals(operands);
            } else if (operation.equals(ClientOperation.NOT_EQUAL)) {
                return new ClientNotEqual(operands);
            }

            return null;
        } else {
            return null;
        }
    }

    @Size(min = 2, max = 2, message = "query.expression.is.not.valid")
    @Override
    public List<ClientExpression> getOperands() {
        return super.getOperands();
    }

    @Override
    protected ClientOperator setOperands(List<ClientExpression> operands) {
        if (operands != null) {
            this.operands = new ArrayList<ClientExpression>(operands);
        } else {
            this.operands = null;
        }
        return this;
    }

    @XmlTransient
    public ClientOperation getType() {
        return type;
    }

    /*
     * We have to calculate LHS and RHS on the fly because XMLMapper may use getOperands().add() to add new operands
     */
    @XmlTransient
    public ClientExpression getLhs() {
        return operands.size() > 0 ? operands.get(0) : null;
    }

    @XmlTransient
    public ClientExpression getRhs() {
        return operands.size() > 1 ? operands.get(1) : null;
    }

    public static boolean isSupported(String text) {
        return ClientOperation.isSupported(text) && (
                EQUALS.getName().equals(text) ||
                NOT_EQUAL.getName().equals(text) ||
                GREATER.getName().equals(text) ||
                GREATER_OR_EQUAL.getName().equals(text) ||
                LESS.getName().equals(text) ||
                LESS_OR_EQUAL.getName().equals(text)
        );
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        if (this.getLhs() != null) {
            this.getLhs().accept(visitor);
        }
        if (this.getRhs() != null) {
            this.getRhs().accept(visitor);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String lhsString = (getLhs() != null) ? getLhs().toString() : ClientExpressions.MISSING_REPRESENTATION;
        String rhsString = (getRhs() != null) ? getRhs().toString() : ClientExpressions.MISSING_REPRESENTATION;

        if (hasParen()) {
            sb.append("(");
        }

        sb
                .append(lhsString)
                .append(" ")
                .append(getType().getDomelOperator())
                .append(" ")
                .append(rhsString);

        if (hasParen()) {
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientComparison)) return false;
        if (!super.equals(o)) return false;

        ClientComparison<?> that = (ClientComparison<?>) o;

        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
