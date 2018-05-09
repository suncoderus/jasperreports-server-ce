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
package com.jaspersoft.jasperserver.jaxrs;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.RuntimeDelegateImpl;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.WebConfig;

import javax.servlet.ServletException;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class JrsJerseyServletContainer extends ServletContainer {

    public JrsJerseyServletContainer() {
    }

    public JrsJerseyServletContainer(ResourceConfig resourceConfig) {
        super(resourceConfig);
    }

    @Override
    protected void init(WebConfig webConfig) throws ServletException {
        // in line below we are making sure, that JAX-RS 2 will be used.
        // See more information in JRS-13860
        RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
        super.init(webConfig);
    }
}
