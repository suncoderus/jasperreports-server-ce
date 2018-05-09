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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreater;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRangeBoundary;
import org.junit.Test;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.integer;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.list;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.range;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 9/14/16 10:39
 */
public class EqualsTests {

    @Test
    public void ensureLiteralEquals() {
        ClientInteger seven = new ClientInteger(7);

        assert seven.equals(new ClientInteger(7));
        assert seven.hashCode() == new ClientInteger(7).hashCode();
    }

    @Test
    public void ensureOperatorEquals() {
        ClientOperator or = new ClientOr();
        ClientOperator trueOrFalse = ClientExpressions.or(new ClientBoolean(true), new ClientBoolean(false));

        assert or.equals(new ClientOr());
        assert trueOrFalse.equals(ClientExpressions.or(new ClientBoolean(true), new ClientBoolean(false)));
    }

    @Test
    public void ensureComparisonEquals() {
        ClientGreater greater = new ClientGreater();

        assert greater.equals(new ClientGreater());
        assert greater.hashCode() == new ClientGreater().hashCode();
    }


    @Test
    public void ensureLogicalEquals() {
        ClientOr or = new ClientOr();
        ClientOr trueOrFalse = ClientExpressions.or(new ClientBoolean(true), new ClientBoolean(false));

        assert or.equals(new ClientOr());
        assert trueOrFalse.equals(ClientExpressions.or(new ClientBoolean(true), new ClientBoolean(false)));

        assert or.hashCode() == new ClientOr().hashCode();
        assert trueOrFalse.hashCode() == ClientExpressions.or(new ClientBoolean(true), new ClientBoolean(false)).hashCode();
    }


    @Test
    public void ensureListEquals() {
        ClientList list = new ClientList(list(new ClientInteger(1), new ClientInteger(2), new ClientInteger(3)));

        assert list.equals(new ClientList(list(new ClientInteger(1), new ClientInteger(2), new ClientInteger(3))));
        assert list.hashCode() == new ClientList(list(new ClientInteger(1), new ClientInteger(2), new ClientInteger(3))).hashCode();
    }

    @Test
    public void ensureVariableEquals() {
        ClientVariable var1 = new ClientVariable("var1");

        assert var1.equals(new ClientVariable("var1"));
        assert var1.hashCode() == new ClientVariable("var1").hashCode();
    }

    @Test
    public void ensureRangeEquals() throws Exception {
        ClientRange range = range(1, 2);

        assert range.equals(range(1, 2));
        assertThat(range.getStart().equals(new ClientRangeBoundary().setBoundary(integer(1))), is(true));
        assertThat(range.getEnd().equals(new ClientRangeBoundary().setBoundary(integer(2))), is(true));

        assert range.hashCode() == range(1, 2).hashCode();
    }

}
