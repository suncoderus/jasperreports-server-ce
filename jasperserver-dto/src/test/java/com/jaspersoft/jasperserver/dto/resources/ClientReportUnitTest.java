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
package com.jaspersoft.jasperserver.dto.resources;



import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;


/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ClientReportUnitTest {
    private ClientReportUnit reportUnit = new ClientReportUnit();

    @Test
    public void testEquals() {
        ClientReportUnit reportUnit1 = new ClientReportUnit();
        ClientReportUnit reportUnit2 = new ClientReportUnit();
        String label = "sdfa";
        String descr = "lall";

        reportUnit1.setLabel(label);
        reportUnit2.setLabel(label);

        reportUnit1.setDescription(descr);
        reportUnit2.setDescription(descr);

        assertTrue(reportUnit1.equals(reportUnit2));
        assertEquals(reportUnit1.hashCode(), reportUnit2.hashCode());
        assertFalse(reportUnit1 == reportUnit2);
    }
}
