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

import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.repo.RepositoryURLHandlerFactory;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepositoryResourceClassLoader extends ClassLoader {

	private final Map resourceKeys;
	private final boolean localURLs;
	private final RepositoryContext repositoryContext;

	public RepositoryResourceClassLoader(ClassLoader parent, Map resourceKeys, boolean localURLs) {
		this(parent, resourceKeys, localURLs, null);
	}

	public RepositoryResourceClassLoader(ClassLoader parent, Map resourceKeys, boolean localURLs,
			RepositoryContext repositoryContext) {
		super(parent);
		
		this.resourceKeys = resourceKeys;
		this.localURLs = localURLs;
		this.repositoryContext = repositoryContext;
	}

	protected URL findResource(String name) {
		return getRepositoryURL(name);
	}

	protected URL getRepositoryURL(String name) {
		URL url = null;
		RepositoryResourceKey resourceKey = (RepositoryResourceKey) resourceKeys.get(name);
		if (resourceKey != null) {
			if (localURLs) {
				url = RepositoryURLHandlerFactory.createRepoURL(name, repositoryContext);
			} else {
				url = RepositoryURLHandlerFactory.createRepoURL(resourceKey.getUri(), repositoryContext);
			}
		}
		return url;
	}

	protected Enumeration findResources(String name) {
		final URL url = getRepositoryURL(name);
		return new Enumeration() {
			private Object obj = url;

			public boolean hasMoreElements() {
				return obj != null;
			}

			public Object nextElement() {
				Object next = obj;
				obj = null;
				return next;
			}
		};
	}
}
