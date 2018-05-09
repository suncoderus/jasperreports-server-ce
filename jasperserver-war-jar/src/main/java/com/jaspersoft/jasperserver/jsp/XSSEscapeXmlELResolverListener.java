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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.jsp.JspFactory;

/**
 * Listener that inserts our ElResolver (XSSEscapeXmlELResolver) int EL resolution chain.
 *
 * @author  dlitvak
 * @version $id$
 */
public class XSSEscapeXmlELResolverListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		JspFactory.getDefaultFactory()
				.getJspApplicationContext(event.getServletContext())
				.addELResolver(new XSSEscapeXmlELResolver());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
