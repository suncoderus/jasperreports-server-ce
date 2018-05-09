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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryUtil;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class Handler extends URLStreamHandler
{
	public final static String REPOSITORY_PROTOCOL = "repo";
	public final static String URL_PROTOCOL_PREFIX = REPOSITORY_PROTOCOL + ':';
	
	private final RepositoryContext repositoryContext;
	
	public Handler()
	{
		super();
		
		this.repositoryContext = null;
	}
	
	public Handler(RepositoryContext repositoryContext)
	{
		super();
		
		this.repositoryContext = repositoryContext;
	}
	
	protected void parseURL(URL u, String spec, int start, int limit)
	{
		spec = spec.trim();
		
		String protocol = null;
		String path;
		if (spec.startsWith(URL_PROTOCOL_PREFIX))
		{
			protocol = REPOSITORY_PROTOCOL;
			path = spec.substring(URL_PROTOCOL_PREFIX.length());
		}
		else
		{
			path = spec;
		}

		setURL(u, protocol, null, -1, null, null, path, null, null);
	}

	protected URLConnection openConnection(URL url) throws IOException
	{
		RepositoryContext context = repositoryContext;
		if (context == null)
		{
			context = RepositoryUtil.getThreadRepositoryContext();
		}
		
		return new RepositoryConnection(context, url);
	}
}
