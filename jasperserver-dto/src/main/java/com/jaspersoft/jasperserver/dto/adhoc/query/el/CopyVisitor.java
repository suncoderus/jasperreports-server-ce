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
package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitorAdapter;
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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientDivide;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientMultiply;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientPercentRatio;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientSubtract;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreater;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreaterOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLess;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLessOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRangeBoundary;

import java.util.ArrayDeque;
import java.util.Deque;

import static java.util.Arrays.asList;

/**
 * <p>
 * Helper class
 * </p>
 *
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
public class CopyVisitor extends ClientELVisitorAdapter implements ClientELVisitor {

    Deque<ClientExpression> currentExpression = new ArrayDeque<ClientExpression>();

    public ClientExpression getCopy() {
        return currentExpression.pollLast();
    }

    public void visit(ClientExpression expression) {
        
    }

    @Override
    public void visit(ClientAnd expression) {
        currentExpression.push(new ClientAnd(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientOr expression) {
        currentExpression.push(new ClientOr(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientNot expression) {
        currentExpression.push(new ClientNot(currentExpression.pollLast()));
    }

    @Override
    public void visit(ClientEquals expression) {
        currentExpression.push(new ClientEquals(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientGreater expression) {
        currentExpression.push(new ClientGreater(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientGreaterOrEqual expression) {
        currentExpression.push(new ClientGreaterOrEqual(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientLess expression) {
        currentExpression.push(new ClientLess(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientLessOrEqual expression) {
        currentExpression.push(new ClientLessOrEqual(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientNotEqual expression) {
        currentExpression.push(new ClientNotEqual(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientAdd expression) {
        currentExpression.push(new ClientAdd(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientSubtract expression) {
        currentExpression.push(new ClientSubtract(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientDivide expression) {
        currentExpression.push(new ClientDivide(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientMultiply expression) {
        currentExpression.push(new ClientMultiply(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientPercentRatio expression) {
        currentExpression.push(new ClientPercentRatio(asList(currentExpression.pollLast(), currentExpression.pollLast())));
    }

    @Override
    public void visit(ClientBoolean expression) {
        currentExpression.push(new ClientBoolean(expression));
    }

    @Override
    public void visit(ClientByte expression) {
        currentExpression.push(new ClientByte(expression));
    }

    @Override
    public void visit(ClientShort expression) {
        currentExpression.push(new ClientShort(expression));
    }

    @Override
    public void visit(ClientInteger expression) {
        currentExpression.push(new ClientInteger(expression));
    }

    @Override
    public void visit(ClientLong expression) {
        currentExpression.push(new ClientLong(expression));
    }

    @Override
    public void visit(ClientBigInteger expression) {
        currentExpression.push(new ClientBigInteger(expression));
    }

    @Override
    public void visit(ClientDouble expression) {
        currentExpression.push(new ClientDouble(expression));
    }

    @Override
    public void visit(ClientFloat expression) {
        currentExpression.push(new ClientFloat(expression));
    }

    @Override
    public void visit(ClientBigDecimal expression) {
        currentExpression.push(new ClientBigDecimal(expression));
    }

    @Override
    public void visit(ClientDate expression) {
        currentExpression.push(new ClientDate(expression));
    }

    @Override
    public void visit(ClientTimestamp expression) {
        currentExpression.push(new ClientTimestamp(expression));
    }

    @Override
    public void visit(ClientTime expression) {
        currentExpression.push(new ClientTime(expression));
    }

    @Override
    public void visit(ClientString expression) {
        currentExpression.push(new ClientString(expression));
    }

    @Override
    public void visit(ClientVariable expression) {
        currentExpression.push(new ClientVariable(expression.getName()));
    }

    @Override
    public void visit(ClientRangeBoundary expression) { currentExpression.push(expression.deepClone()); }


    @Override
    public void visit(ClientIn expression) {
        currentExpression.push(new ClientIn(currentExpression.pollLast(), currentExpression.pollLast()));
    }

    @Override
    public void visit(ClientFunction expression) {
        ClientFunction f = new ClientFunction(expression.getFunctionName()) ;
        while (currentExpression.peek() != null) {
            f.addArgument(currentExpression.pollLast());
        }
        currentExpression.push(f);
    }

    @Override
    public void visit(ClientRange expression) {
        ClientRange range = new ClientRange((ClientLiteral) currentExpression.pollLast(), (ClientLiteral) currentExpression.pollLast());
        currentExpression.push(range);
    }

    @Override
    public void visit(ClientList expression) {
        ClientList list = new ClientList();
        while (currentExpression.peek() != null) {
            list.addItem(currentExpression.pollLast());
        }
        currentExpression.push(list);
    }

    @Override
    public void visit(ClientNull expression) {
        currentExpression.push(new ClientNull(expression));
    }
}
