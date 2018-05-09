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

package com.jaspersoft.jasperserver.core.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: dlitvak
 * Date: 2/27/12
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Base64CoderTest {
    @Test
    public void testEncodeBase64() throws Exception {
        String testStr = "жасперадмин";
        assertEquals("Did not encode/decode Base64 correctly for 16 bit chars.",
                testStr, Base64Coder.decode16BitBase64(Base64Coder.encode16BitBase64(testStr)));
    }
}
