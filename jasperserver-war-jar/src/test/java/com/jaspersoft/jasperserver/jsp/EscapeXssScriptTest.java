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

package com.jaspersoft.jasperserver.jsp;

import junit.framework.Assert;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

/**
 *
 * @author dlitvak
 * @version $Id$
 */
public class EscapeXssScriptTest extends UnitilsJUnit4 {

	/**
	 * Test that < and > are properly escaped as &lt; and &gt;
	 */
	@Test
    public void testEscape() {
		Assert.assertEquals("<script>alert; should be escaped as &lt;script&gt;alert&#059;", EscapeXssScript.escape("<script>alert;"), "&lt;script&gt;alert&#059;");
	}

	/**
	 * Test that ; is not incorrectly escaped in the unicode encoded chars such as &#1071;
	 */
	@Test
    public void testEscapeOfUnicodeChar() {
		Assert.assertEquals("<&#1071;> should be escaped as &lt;&#1071;&gt;", "&lt;&#1071;&gt;", EscapeXssScript.escape("<&#1071;>"));
	}

	/**
	 * Test that ; is not incorrectly escaped in the html encoded chars such as &amp;
	 */
	@Test
    public void testEscapeOfHTMLChar() {
		Assert.assertEquals("<&amp;&amp;&frac34;> should be escaped as &lt;&amp;&amp;&frac34;&gt;", "&lt;&amp;&amp;&frac34;&gt;", EscapeXssScript.escape("<&amp;&amp;&frac34;>"));
	}
}
