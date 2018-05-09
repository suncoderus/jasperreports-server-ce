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

package com.jaspersoft.jasperserver.war.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class SecurityContextHolderStrategyInitializer implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(SecurityContextHolderStrategyInitializer.class);
	
	public void contextInitialized(ServletContextEvent sce) {
		if (SecurityContextHolder.getInitializeCount() > 1) {
			log.warn("SecurityContextHolder already initialized, not setting the strategy");
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Setting SecurityContext strategy to MODE_INHERITABLETHREADLOCAL");
			}
			
			SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		//nothing
	}

}
