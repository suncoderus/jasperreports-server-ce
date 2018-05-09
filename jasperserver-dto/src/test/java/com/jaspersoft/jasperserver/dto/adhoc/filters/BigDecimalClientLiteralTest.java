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

package com.jaspersoft.jasperserver.dto.adhoc.filters;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigDecimal;
import org.junit.Test;

import java.math.BigDecimal;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ELUtils.isFloatingPointString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 3/10/16 10:47 AM
 */
public class BigDecimalClientLiteralTest {

    @Test
    public void ensureValid() throws Exception {
        final String testString = "0.999999";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));

    }

    @Test
    public void ensureValid_noLeadingDigit() throws Exception {
        final String testString = ".999999";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));

    }

    @Test
    public void ensureValid_1enegative5() throws Exception {
        final String testString = "1e-5";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureValid_negativeExponential() throws Exception {
        final String testString = "1.234e-54";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureValid_positiveExponential() throws Exception {
        final String testString = "1.234e+54";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureValid_1dot_e10() throws Exception {
        final String testString = "1.e10";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureValid_withPlus() throws Exception {
        final String testString = "+123.999999";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureValid_fullwidthDigits() throws Exception {
        final String testString = "１２８.１３３０８００４";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureValid_mixed_fullwidthDigits_latinDigits() throws Exception {
        final String testString = "+１.2８１33０８００4";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureValid_arabicDigits() throws Exception {
        // 0123456789
        final String testString = ".٠١٢٣٤٥٦٧٨٩";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);
        assertThat(isFloatingPointString(testString), is(true));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureValid_arabicDigits_withMinus() throws Exception {
        // -0123456789
        String testString = "-٠١.٢٣٤٥٦٧٨٩";
        ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(instanceOf(BigDecimal.class)));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureValid_withMinus() throws Exception {
        final String testString = "-42.000";
        final ClientBigDecimal clientTest = ClientBigDecimal.valueOf(testString);

        assert(isFloatingPointString(testString));
        assertThat(clientTest.getValue(), is(new BigDecimal(testString)));
    }

    @Test
    public void ensureInvalid_null() throws Exception {
        assert(!isFloatingPointString(null));
    }

    @Test
    public void ensureInvalid_twoDecimalPoints() throws Exception {
        final String testString = "-99.99.9";

        assert(!isFloatingPointString(testString));
    }

    @Test
    public void ensureInvalid_justE() throws Exception {
        final String testString = "e";

        assert(!isFloatingPointString(testString));
    }

    @Test
    public void ensureInvalid_justE10() throws Exception {
        final String testString = "e10";

        assert(!isFloatingPointString(testString));
    }

    @Test
    public void ensureInvalid_twoDecimalPointsAtBeginning() throws Exception {
        final String testString = "..99999";

        assert(!isFloatingPointString(testString));
    }

    @Test
    public void ensureInvalid_charInMiddle_withMinus() throws Exception {
        final String testString = "-99.9a999";

        assert(!isFloatingPointString(testString));
    }

    @Test
    public void ensureInvalid_lastCharLetter() throws Exception {
        final String testString = "12314392a.";

        assert(!isFloatingPointString(testString));
    }

    @Test
    public void ensureInvalid_firstCharLetter() throws Exception {
        final String testString = "b12.314392";

        assert(!isFloatingPointString(testString));
    }

    @Test
    public void ensureInvalid_secondCharMinus() throws Exception {
        final String testString = "1.-12314392";

        assert(!isFloatingPointString(testString));
    }

    @Test
    public void ensureInvalid_secondCharPlus() throws Exception {
        final String testString = "1+123.14392";

        assert(!isFloatingPointString(testString));
    }
}
