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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientFloat;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDouble;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLong;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientShort;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientByte;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigDecimal;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLiteralType;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRangeBoundary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * <p/>
 * </p>
 *
 * @author Stas Chubar (schubar@tibco.com)
 * @version $Id$
 */
public abstract class VariableOperations extends Operations<ClientVariable> {

    public ClientIn in(ClientLiteral... values) {
        List<ClientExpression> exprList = new ArrayList<ClientExpression>();
        Collections.addAll(exprList, values);
        return new ClientIn(getMe(), exprList);
    }

    public ClientIn in(String... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.STRING, values));
    }

    public ClientIn in(Boolean... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.BOOLEAN, values));
    }

    public ClientIn in(Byte... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.BYTE, values));
    }

    public ClientIn in(Short... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.SHORT, values));
    }

    public ClientIn in(Integer... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.INTEGER, values));
    }

    public ClientIn in(Long... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.LONG, values));
    }

    public ClientIn in(BigInteger... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.BIG_INTEGER, values));
    }

    public ClientIn in(Float... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.FLOAT, values));
    }

    public ClientIn in(Double... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.DOUBLE, values));
    }

    public ClientIn in(BigDecimal... values) {
        return new ClientIn(getMe(), valuesToLiteralList(ClientLiteralType.BIG_DECIMAL, values));
    }


    public ClientIn in(ClientRange range) {
        return new ClientIn(getMe(), range);
    }

    public ClientIn inRange(Byte start, Byte end) {
        return new ClientIn(getMe(),
                new ClientRange(new ClientRangeBoundary(new ClientByte(start)),
                        new ClientRangeBoundary(new ClientByte(end))
                )
        );
    }

    public ClientIn inRange(Short start, Short end) {
        return new ClientIn(getMe(),
                new ClientRange(new ClientRangeBoundary(new ClientShort(start)),
                        new ClientRangeBoundary(new ClientShort(end))
                )
        );
    }

    public ClientIn inRange(Integer start, Integer end) {
        return new ClientIn(getMe(),
                new ClientRange(new ClientRangeBoundary(new ClientInteger(start)),
                        new ClientRangeBoundary(new ClientInteger(end))
                )
        );
    }

    public ClientIn inRange(Long start, Long end) {
        return new ClientIn(getMe(),
                new ClientRange(new ClientRangeBoundary(ClientLong.valueOf(start)),
                        new ClientRangeBoundary(ClientLong.valueOf(end))
                )
        );
    }

    public ClientIn inRange(BigInteger start, BigInteger end) {
        return new ClientIn(getMe(),
                new ClientRange(new ClientRangeBoundary(new ClientBigInteger(start)),
                        new ClientRangeBoundary(new ClientBigInteger(end))
                )
        );
    }

    public ClientIn inRange(Float start, Float end) {
        return new ClientIn(getMe(),
                new ClientRange(new ClientRangeBoundary(new ClientFloat(start)),
                        new ClientRangeBoundary(new ClientFloat(end))
                )
        );
    }

    public ClientIn inRange(Double start, Double end) {
        return new ClientIn(getMe(),
                new ClientRange(new ClientRangeBoundary(new ClientDouble(start)),
                        new ClientRangeBoundary(new ClientDouble(end))
                )
        );
    }

    public ClientIn inRange(BigDecimal start, BigDecimal end) {
        return new ClientIn(getMe(),
                new ClientRange(new ClientRangeBoundary(new ClientBigDecimal(start)),
                        new ClientRangeBoundary(new ClientBigDecimal(end))
                )
        );
    }

    public ClientIn inRange(String start, String end) {
        return new ClientIn(getMe(),
                new ClientRange(new ClientRangeBoundary(new ClientString(start)),
                        new ClientRangeBoundary(new ClientString(end))
                )
        );
    }

}
