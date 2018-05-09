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
package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import java.util.Properties;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Factory for  MondrianConnection cache updates
 * @author swood
 */
public class MondrianConnectionCacheEventListenerFactory extends CacheEventListenerFactory  {

    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        ApplicationContext ctx = StaticApplicationContext.getApplicationContext();

        if (ctx == null) {
            throw new JSException("StaticApplicationContext not configured in Spring");
        }

        if (properties == null || properties.getProperty("type", "external").equalsIgnoreCase("external")) {
            return new MondrianConnectionCacheEventListener(ctx, properties);
        } else {
            return new MondrianConnectionSharedCacheEventListener(ctx, properties);
        }
    }


}
