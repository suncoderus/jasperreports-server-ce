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

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class JSRequestDecoratorMapper extends AbstractDecoratorMapper {

    private String attrName = "decorator";

    @Override
    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();

            if (key.equals("attribute.name")) {
                this.attrName = (String) entry.getValue();
            }
        }
    }

    @Override
    public Decorator getDecorator(HttpServletRequest request, Page page) {
        String name = (String) request.getAttribute(this.attrName);

        if (name != null) {
            return getNamedDecorator(request, name);
        }

        return super.getDecorator(request, page);
    }
}