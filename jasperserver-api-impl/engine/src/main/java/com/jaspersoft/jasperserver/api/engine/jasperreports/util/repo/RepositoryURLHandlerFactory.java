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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util.repo;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryContext;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepositoryURLHandlerFactory implements URLStreamHandlerFactory
{
	protected static final Log log = LogFactory.getLog(RepositoryURLHandlerFactory.class);
	
	private final static RepositoryURLHandlerFactory instance = new RepositoryURLHandlerFactory();
	
	public static RepositoryURLHandlerFactory getInstance()
	{
		return instance;
	}
	
	private final Handler repositoryHandler;
	
	protected RepositoryURLHandlerFactory()
	{
		repositoryHandler = new Handler();
	}
	
	public URLStreamHandler createURLStreamHandler(String protocol)
	{
		if (protocol.equals(Handler.REPOSITORY_PROTOCOL))
		{
			return repositoryHandler;
		}

		return null;
	}
	
	public Handler getRepoHandler() {
		return repositoryHandler;
	}

	public static URL createRepoURL(String uri) {
		return createRepoURL(uri, null);
	}

	public static URL createRepoURL(String uri, RepositoryContext repositoryContext) {
		Handler handler = repositoryContext == null ? getInstance().getRepoHandler()
				//cache per context?  looks pretty cheap to instantiate
				: new Handler(repositoryContext);
		try {
			return new URL(null, Handler.URL_PROTOCOL_PREFIX + uri, handler);
		} catch (MalformedURLException e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}
}
