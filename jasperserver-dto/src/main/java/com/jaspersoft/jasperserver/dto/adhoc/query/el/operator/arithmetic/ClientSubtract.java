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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientOperator;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientOperation;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = ClientSubtract.OPERATOR_ID)
public class ClientSubtract extends ClientOperator<ClientSubtract> {

    public static final String OPERATOR_ID = "subtract";
    public static final String DOMEL_OPERATOR = "-";

    public ClientSubtract() {
        super(ClientOperation.SUBTRACT.getName());
    }

    public ClientSubtract(List<ClientExpression> operands) {
        super(ClientOperation.SUBTRACT.getName(), operands);
    }

    public ClientSubtract(ClientSubtract difference) {
        super(difference.getOperator(), difference.getOperands(), difference.paren);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (getOperands() != null && !getOperands().isEmpty()) {
            result.append(addStringOperand(getOperands().get(0)));
        } else {
            result.append(ClientExpressions.MISSING_REPRESENTATION);
        }
        result.append(" " + DOMEL_OPERATOR + " ");
        if (getOperands() != null && getOperands().size() > 0) {
            result.append(addStringOperand(getOperands().get(1)));
        } else {
            result.append(ClientExpressions.MISSING_REPRESENTATION);
        }

        return hasParen() ? "(" + result.toString() + ")" : result.toString();
    }

    @Override
    public ClientSubtract setParen() {
        super.setParen();
        return this;
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        super.accept(visitor);
        visitor.visit(this);
    }

    @Override
    public ClientSubtract deepClone() {
        return new ClientSubtract(this);
    }
}
