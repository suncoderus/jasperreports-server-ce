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
package com.jaspersoft.jasperserver.api.metadata.common.service;

import java.io.InputStream;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;

/**
 * Cache of data derived from repository resources.
 * 
 * <p>
 * Such a cache is used by the engine service for compiled JRXML reports.
 * </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.0
 */
@JasperServerAPI
public interface RepositoryCache {

	/**
	 * Returns a cached data item for a repository resource, creating the item
	 * if not already present in the cache.
	 * 
     * @param context the caller execution context
	 * @param resource the repository resource for which the cached item is created
	 * @param cacheableItem the cache instance
	 * @return a stream from which the cached data can be read
	 */
	InputStream cache(ExecutionContext context, FileResource resource, RepositoryCacheableItem cacheableItem);

	/**
	 * Returns a cached data item for a repository resource, creating the item
	 * if not already present in the cache.
	 * 
     * @param context the caller execution context
	 * @param uri the repository path of the resource for which the cached item 
	 * is created
	 * @param cacheableItem the cache instance
	 * @return a stream from which the cached data can be read
	 */
	InputStream cache(ExecutionContext context, String uri, RepositoryCacheableItem cacheableItem);
	
	/**
	 * Deletes the cached items for a repository resource.
	 * 
	 * @param uri the repository path of the resource for which the cached item 
	 * should be deleted
	 * @param cacheableItem the cache instance
	 */
	void clearCache(String uri, RepositoryCacheableItem cacheableItem);
	
	/**
	 * Deletes cached items for all resources and a specified cache.
	 * 
	 * @param cacheableItem the cache instance
	 * @since 3.0.0
	 */
	void clearCache(RepositoryCacheableItem cacheableItem);

}
