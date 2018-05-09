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

package com.jaspersoft.jasperserver.api.common.util;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * <p>Tests {@link UpgradeRunMonitor}</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 * @since 4.7.0
 */
public class UpgradeRunMonitorTest extends UnitilsJUnit4 {
    @Test
    public void testUpgradeRun() {
        assertFalse(UpgradeRunMonitor.isUpgradeRun());

        UpgradeRunMonitor.start();

        assertTrue(UpgradeRunMonitor.isUpgradeRun());

        UpgradeRunMonitor.stop();

        assertFalse(UpgradeRunMonitor.isUpgradeRun());
    }
}
