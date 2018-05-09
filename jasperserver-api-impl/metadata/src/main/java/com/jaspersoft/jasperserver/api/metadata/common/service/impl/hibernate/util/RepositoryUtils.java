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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util;

import java.util.StringTokenizer;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;

/**
 * @author Lucian Chirita
 *
 */
public class RepositoryUtils {
	
	public static final String FOLDER_PARENT = "..";
	public static final String FOLDER_CURRENT = ".";

	public static String getParentPath(String path) {
		if (path.equals(Folder.SEPARATOR)) {
			return null;
		}
		
		int lastSep = path.lastIndexOf(Folder.SEPARATOR);
		if (lastSep < 0) {
			throw new JSException("Path " + path + " is not an absolute repository path");
		}
		
		String parentPath;
		if (lastSep == 0) {
			parentPath = Folder.SEPARATOR;
		} else {
			parentPath = path.substring(0, lastSep);
		}
		return parentPath;
	}
	
	public static String getName(String path) {
		if (path.equals(Folder.SEPARATOR)) {
			return Folder.SEPARATOR;
		}
		
		int lastSep = path.lastIndexOf(Folder.SEPARATOR);
		if (lastSep < 0) {
			throw new JSException("Path " + path + " is not an absolute repository path");
		}
		
		return path.substring(lastSep + 1);
	}

	public static String concatenatePath(String parentPath, String name) {
		StringBuffer concantenated = new StringBuffer(parentPath);
		if (!parentPath.equals(Folder.SEPARATOR)) {
			concantenated.append(Folder.SEPARATOR);
		}
		concantenated.append(name);
		return concantenated.toString();
	}

	public static String concatenatePaths(String parentPath, String path) {
		StringBuffer concantenated = new StringBuffer();
		if (parentPath.equals(Folder.SEPARATOR)) {
			concantenated.append(path);
		} else {
			concantenated.append(parentPath);
			if (!path.equals(Folder.SEPARATOR)) {
				concantenated.append(path);
			}
		}
		return concantenated.toString();
	}
	
	/**
	 * Determines whether a repository path is an ancestor of another path.
	 * 
	 * @param parent the parent path
	 * @param path the path
	 * @return whether <code>parent</code> is an ancestor of <path>
	 */
	public static boolean isAncestorOrEqual(String ancestorPath, String path) {
		//if equal
		if (ancestorPath.equals(path)) {
			return true;
		}
		
		//if ancestor path is not a prefix
		if (!path.startsWith(ancestorPath)) {
			return false;
		}
		
		//if the ancestor is /, or the ancestor is followed by a / in the child path
		return Folder.SEPARATOR.equals(ancestorPath) 
				|| path.substring(ancestorPath.length()).startsWith(Folder.SEPARATOR);
	}

	/**
	 * Determines whether a repository path is an local resource of another path.
	 *
	 * @param ancestorPath the parent path
	 * @param path the path
	 * @return whether <code>parent</code> is an ancestor of <path>
	 */
	public static boolean isLocalResource(String ancestorPath, String path) {
		//if ancestor path is not a prefix
		if (!path.startsWith(ancestorPath)) {
			return false;
		}

		//if the ancestor is /, or the ancestor is followed by a '_files/' in the child path
		return path.substring(ancestorPath.length()).startsWith("_files" + Folder.SEPARATOR);
	}

	public static String resolveRelativePath(String contextPath, String path) {
		if (contextPath == null || contextPath.isEmpty() || !contextPath.startsWith(Folder.SEPARATOR)) {
			throw new IllegalArgumentException("contextPath needs to be an absolute path");
		}
		
		if (path == null || path.isEmpty()) {
			throw new IllegalArgumentException("path cannot be empty");
		}
		
		if (path.startsWith(Folder.SEPARATOR)) {
			// absolute path
			return path;
		}
		
		StringBuilder pathBuilder = new StringBuilder(contextPath.length() + path.length() + 1);
		pathBuilder.append(contextPath);
		
		for (StringTokenizer pathTokenizer = new StringTokenizer(path, Folder.SEPARATOR);
				pathTokenizer.hasMoreTokens();) {
			String pathToken = pathTokenizer.nextToken();
			if (pathToken.equals(FOLDER_PARENT)) {
				// if we are on the root folder, stay there (root's parent is root by convention)
				if (pathBuilder.length() > 1) {
					// go to parent folder by removing the last folder
					int lastPathIndex = pathBuilder.lastIndexOf(Folder.SEPARATOR);
					// we can assert that lastPathIndex is >= 0
					pathBuilder.delete(lastPathIndex == 0 ? 1 : lastPathIndex, pathBuilder.length());
				}
			} else if (pathToken.equals(FOLDER_CURRENT)) {
				//NOP, nothing to change in the path
			} else {
				// proper path token, append it
				if (pathBuilder.length() > 1) {
					pathBuilder.append(Folder.SEPARATOR);
				}
				pathBuilder.append(pathToken);
			}
		}
		return pathBuilder.toString();
	}
}
