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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.ast;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientList;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
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

/**
 * <p>
 * Helper class
 * </p>
 *
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
public abstract class ClientELVisitorAdapter implements ClientELVisitor {

    public void visit(ClientExpression expression) {
        
    }

    @Override
    public void visit(ClientAnd expression) {
        
    }

    @Override
    public void visit(ClientOr expression) {
        
    }

    @Override
    public void visit(ClientNot expression) {
        
    }

    @Override
    public void visit(ClientEquals expression) {

    }

    @Override
    public void visit(ClientGreater expression) {
        
    }

    @Override
    public void visit(ClientGreaterOrEqual expression) {
        
    }

    @Override
    public void visit(ClientLess expression) {
        
    }

    @Override
    public void visit(ClientLessOrEqual expression) {
        
    }

    @Override
    public void visit(ClientNotEqual expression) {
        
    }

    @Override
    public void visit(ClientAdd expression) {
        
    }

    @Override
    public void visit(ClientSubtract expression) {
        
    }

    @Override
    public void visit(ClientDivide expression) {
        
    }

    @Override
    public void visit(ClientMultiply expression) {
        
    }

    @Override
    public void visit(ClientPercentRatio expression) {

    }

    @Override
    public void visit(ClientLiteral expression) {
        
    }

    @Override
    public void visit(ClientVariable expression) {
        
    }

    @Override
    public void visit(ClientIn expression) {
        
    }

    @Override
    public void visit(ClientFunction expression) {
        
    }

    @Override
    public void visit(ClientRange expression) {
        
    }

    @Override
    public void visit(ClientRangeBoundary expression) {

    }

    @Override
    public void visit(ClientList expression) {
        
    }

    @Override
    public void visit(ClientByte expression) {

    }

    @Override
    public void visit(ClientShort expression) {

    }

    @Override
    public void visit(ClientInteger expression) {

    }

    @Override
    public void visit(ClientLong expression) {

    }

    @Override
    public void visit(ClientBigInteger expression) {

    }

    @Override
    public void visit(ClientFloat expression) {

    }

    @Override
    public void visit(ClientDouble expression) {

    }

    @Override
    public void visit(ClientBigDecimal expression) {

    }

    @Override
    public void visit(ClientBoolean expression) {

    }

    @Override
    public void visit(ClientDate expression) {

    }

    @Override
    public void visit(ClientTime expression) {

    }

    @Override
    public void visit(ClientTimestamp expression) {

    }

    @Override
    public void visit(ClientString expression) {

    }

    @Override
    public void visit(ClientNull expression) {

    }
}
