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

package com.jaspersoft.jasperserver.dto.reports;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;


/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ReportParameterTest {
    private final ReportParameter param1 = new ReportParameter();
    private final ReportParameter param2 = new ReportParameter();

    @Before
    public void setUp() throws Exception {
        param1.setValues(Arrays.asList("a", "b"));
        param2.setValues(Arrays.asList("b", "a"));
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(param1.equals(param2));
        assertTrue(param2.equals(param1));
        assertFalse(param2.getValues().equals(param1.getValues()));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(param1.hashCode(), param2.hashCode());
    }
}
