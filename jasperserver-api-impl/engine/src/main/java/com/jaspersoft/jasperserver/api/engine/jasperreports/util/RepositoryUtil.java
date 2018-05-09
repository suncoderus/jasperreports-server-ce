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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepositoryUtil
{
	private static final Log log = LogFactory.getLog(RepositoryUtil.class);
	
	private static final ThreadLocal repositoryContext = new InheritableThreadLocal();
	
	public static void setThreadRepositoryContext(RepositoryContext context)
	{
		if (log.isDebugEnabled())
		{
			log.debug("set repository context to " + (context == null ? "null" : ("context with path " + context.getContextURI())));
		}
		
		repositoryContext.set(context);
	}
	
	public static void clearThreadRepositoryContext()
	{
		if (log.isDebugEnabled())
		{
			log.debug("cleared repository context");
		}
		
		repositoryContext.set(null);
	}
	
	public static RepositoryContext getThreadRepositoryContext()
	{
		return (RepositoryContext) repositoryContext.get();
	}
	
	public static boolean hasThreadRepositoryContext()
	{
		return repositoryContext.get() != null;
	}
}
