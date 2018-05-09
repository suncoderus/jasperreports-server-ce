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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = "not")
public class ClientNot extends ClientOperator<ClientNot> {

    public ClientNot() {
        super(ClientOperation.NOT.getName());
        this.setParen();
    }

    public ClientNot(ClientNot not) {
        this();
        final ClientExpression operand = not.getOperand();
        if(operand != null){
            setOperands(new ArrayList<ClientExpression>(){{add(operand);}});
        }

    }

    public ClientNot(ClientOperation operation, List<ClientExpression> operands) {
        super(operation.getName(), operands);
        this.setParen();
    }

    public ClientNot(ClientExpression expression) {
        this(ClientOperation.NOT, new ArrayList<ClientExpression>());
        this.operands.add(expression);
    }

    public static ClientNot not(ClientExpression expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Error creating 'not' filter expression. Expression is null!");
        }
        if (!(expression instanceof ClientOperator)) {
            throw new IllegalArgumentException("Error creating 'not' filter expression. Expression is not operator!");
        }
        return new ClientNot(expression);
    }

    @XmlTransient
    public ClientExpression getOperand() {
        return getOperands() != null && !getOperands().isEmpty() ? this.getOperands().get(0) : null;
    }

    @Override
    public String toString() {
        ClientExpression operand = (getOperand() != null) ? getOperand() : new ClientNull();
        return getOperator() + " (" + operand + ")";
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        if (getOperands() != null && !getOperands().isEmpty()) {
            getOperand().accept(visitor);
        }
        visitor.visit(this);
    }

    @Override
    public ClientNot deepClone() {
        return new ClientNot(this);
    }
}
