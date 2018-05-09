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

package com.jaspersoft.jasperserver.war.tiles2;

import org.apache.tiles.preparer.PreparerException;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.preparer.ViewPreparer;

import java.util.ArrayList;

public class TestPreparer extends JSDecoratorPreparer {
//public class TestPreparer implements ViewPreparer {

    public void execute(TilesRequestContext context, AttributeContext attributeContext)
            throws PreparerException {
        super.execute(context, attributeContext);

        ArrayList<TestPreparer.MenuItem> menu = new ArrayList<TestPreparer.MenuItem>();

        menu.add(new MenuItem("Tiles Home", "flow.html?_flowId=tiles2Flow"));
        menu.add(new MenuItem("About", "tiles2.html"));

        context.getRequestScope().put("menu", menu);
        context.getRequestScope().put("menu", menu);
    }

    public static class MenuItem {
        private String url, caption;

        public MenuItem(String caption, String url) {
            this.caption = caption;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public String getCaption() {
            return caption;
        }
    }
}
