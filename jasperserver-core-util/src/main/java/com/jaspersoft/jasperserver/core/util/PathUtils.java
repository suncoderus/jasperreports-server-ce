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

package com.jaspersoft.jasperserver.core.util;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PathUtils {

	public static final char FILE_SEPARATOR = '/';
	
	protected static final Pattern MULTI_FILE_SEP_PATTERN = Pattern.compile("/{2,}");
	protected static final String MULTI_FILE_SEP_REPLACEMENT = "/";
	
	protected static final Pattern SPLIT_URI_PATTERN = Pattern.compile("^(.*)/([^/]+)/*$");
	protected static final int SPLIT_URI_PATTERN_PARENT_IDX = 1;
	protected static final int SPLIT_URI_PATTERN_NAME_IDX = 2;

    protected static final Pattern PATH_COMPONENT_INVALID_CHAR =
            Pattern.compile("[^\\p{L}\\p{N}]");

    protected static final String PATH_COMPONENT_CHAR_REPLACEMENT = "_";


    public static class SplittedPath {
		public final String parentPath;
		public final String name;
		
		public SplittedPath(final String parentPath, final String name) {
			this.parentPath = parentPath;
			this.name = name;
		}
	}
	
	public static SplittedPath splitPath(String uri) {
		Matcher matcher = SPLIT_URI_PATTERN.matcher(uri);
		SplittedPath splUri;
		if (matcher.matches()) {
			String parentURI = matcher.group(SPLIT_URI_PATTERN_PARENT_IDX);
			if (parentURI.length() == 0) {
				parentURI = null;
			}

			String name = matcher.group(SPLIT_URI_PATTERN_NAME_IDX);
			
			splUri = new SplittedPath(parentURI, name);
		} else {
			splUri = null;
		}
		return splUri;
	}

	/**
	 * Split path to folder and name, name can be folder or resource. <br>
	 *
	 * For example:<br>
	 *     uri: /folder/resource -> parent: /folder, name: resource <br>
	 *     uri: /folder -> parent: /, name: folder <br>
	 *     uri: / -> parent: /, name: null
	 *
	 * @param uri path to splitting
	 * @return SplittedPath which never equals <tt>null</tt>
	 **/
	public static SplittedPath splitPathToFolderAndName(String uri) {
		if (uri.equals(Folder.SEPARATOR)) new SplittedPath(Folder.SEPARATOR, null);

		SplittedPath splittedPath = splitPath(uri);
		if (splittedPath == null) {
			String message = "Path: " + uri + " is not correct.";
			throw new JSException(message.toString());
		}
		if (splittedPath.parentPath == null) {
			return new SplittedPath(Folder.SEPARATOR, splittedPath.name);
		}
		return splittedPath;
	}

	public static String concatPaths(String path1, String path2) {
		if (path1 == null) {
			if (path2 == null) {
				return null;
			}
			
			return normalizePath(path2);
		}
		if (path2 == null) {
			return normalizePath(path1);
		}
		
		return normalizePath(path1 + FILE_SEPARATOR + path2);
	}

	/**
	 * Concat three or more path
	 *
	 * For example:<br>
	 *     parent: /folder, child: folder2, name: folder3 -> result: /folder/folder2/folder3 <br>
	 *
	 * @param parentPath the parent path
	 * @param childPath the child path
	 * @param otherPaths list of children paths
	 *
	 * @return concated path
	 **/
	public static String concatPaths(String parentPath, String childPath, String ... otherPaths) {
		String result = concatPaths(parentPath, childPath);
		for (String p : otherPaths) {
			result = concatPaths(result, p);
		}
		return result;
	}

	/**
	 * Helper method for building paths from the array of path segments.
	 * Prepends path with {@link #FILE_SEPARATOR}
	 *
	 * Skip all empty paths
	 *
	 * @param pathSegments Path segments
	 * @return String representing path
     */
	public static String buildPath(String ... pathSegments) {
		return buildPath(Arrays.asList(pathSegments));
	}

	/**
	 * Helper method for building paths from the array of path segments.
	 * Prepends path with {@link #FILE_SEPARATOR}
	 *
	 * Skip all empty paths
	 *
	 * @param pathSegments Path segments
	 * @return String representing path
     */
	public static String buildPath(List<String> pathSegments) {
		if (pathSegments == null || pathSegments.size() == 0) {
			return null;
		}
		StringBuilder pathBuilder = new StringBuilder();
		for (String pathSeg : pathSegments) {
			pathBuilder.append(FILE_SEPARATOR).append(pathSeg);
		}

		return pathBuilder.toString();
	}

	public static String normalizePath(String path) {
		if (path == null) {
			return null;
		}

		path = MULTI_FILE_SEP_PATTERN.matcher(path).replaceAll(MULTI_FILE_SEP_REPLACEMENT);
		
		if (path.length() > 1 && path.charAt(path.length() - 1) == FILE_SEPARATOR) {
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}

    public static String preparePathComponent(String name) {

        return PATH_COMPONENT_INVALID_CHAR.matcher(name).
                replaceAll(PATH_COMPONENT_CHAR_REPLACEMENT);
    }


}
