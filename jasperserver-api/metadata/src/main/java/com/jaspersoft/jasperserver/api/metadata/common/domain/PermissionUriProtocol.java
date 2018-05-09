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
package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Volodya Sabadosh
 * @version $Id: $
 */
public enum PermissionUriProtocol {
    ATTRIBUTE("attr") {
        @Override
        public String getParentUri(String uri) {
            if (uri == null || uri.length() == 0 || uri.trim().equals(Folder.SEPARATOR)) {
                return null;
            }

            int pos = uri.lastIndexOf("/attributes/");
            if (pos == -1) {
                pos = uri.lastIndexOf(Folder.SEPARATOR);
            }
            if (pos == 0) {
                return Folder.SEPARATOR;
            }
            String tail = pos > 0 ? uri.substring(pos, uri.length()) : Folder.SEPARATOR;

            Matcher matcher = attrPattern.matcher(uri);
            if (pos > 0) {
                matcher.region(0, pos);
            }
            pos = -1;
            while (matcher.find()) {
                pos = matcher.start();
            }

            if (pos == -1) {
                return tail;
            } else {
                return uri.substring(0, pos).concat(tail);
            }
        }

        @Override
        public String addPrefix(String uri) {
            if (uri != null) {
                return getProtocolPrefix().concat(uri);
            } else {
                return getProtocolPrefix() + uri;
            }
        }
    },

    RESOURCE("repo") {
        @Override
        public String getParentUri(String uri) {
            if (uri == null || uri.length() == 0 || uri.trim().equals(Folder.SEPARATOR)) {
                return null;
            }
            uri = cleanUri(uri);

            int lastSeparator = uri.lastIndexOf(Folder.SEPARATOR);

            if (lastSeparator < 0) {
                return null;
            }

            if (lastSeparator == 0) {
                return Folder.SEPARATOR;
            }

            return uri.substring(0, lastSeparator);
        }

        @Override
        public String addPrefix(String uri) {
            if (uri != null) {
                return getProtocolPrefix().concat(uri);
            } else {
                return getProtocolPrefix() + uri;
            }
        }
    };

    private static final Map<String, PermissionUriProtocol> stringToEnum = new HashMap<String, PermissionUriProtocol>();
    public static final String DOBLE_FOLDER_SEPARATOR = Folder.SEPARATOR.concat(Folder.SEPARATOR);
    static { // Initialize map from constant name to enum constant
        for (PermissionUriProtocol protocol : values())
            stringToEnum.put(protocol.toString(), protocol);
    }

    // Returns PermissionUriProtocol for string, or null if string is invalid
    public static PermissionUriProtocol fromString(String symbol) {
        return stringToEnum.get(symbol);
    }

    private String protocol;
    public Pattern attrPattern = Pattern.compile("/organizations/|/users/|/roles/");

    //Constructor
    PermissionUriProtocol(String protocol) {
        this.protocol = protocol;
    }

    public abstract String getParentUri(String uri);

    public abstract String addPrefix(String uri);

    @Override
    public String toString() {
        return this.protocol;
    }

    public String getProtocolPrefix() {
        return protocol.concat(":");
    }

    public static PermissionUriProtocol getProtocol(String uri) {
        if (uri != null) {
            for (PermissionUriProtocol protocol : values()) {
                if (uri.startsWith(protocol.getProtocolPrefix())) {
                    return protocol;
                }
            }
        }
        return RESOURCE;
    }

    public static String addDefaultPrefixIfNotExist(String uri) {
        for (PermissionUriProtocol protocol : values()) {
            if (uri.startsWith(protocol.getProtocolPrefix())) {
                return uri;
            }
        }
        return RESOURCE.getProtocolPrefix().concat(uri);
    }

    public static String removePrefix(String uri) {
        if (uri != null) {
            for (PermissionUriProtocol protocol : values()) {
                if (uri.startsWith(protocol.getProtocolPrefix())) {
                    return uri.substring(protocol.getProtocolPrefix().length());
                }
            }
        }
        return uri;
    }

    public static String cleanUri(String uri) {
        if ((uri != null) && ( uri.contains(DOBLE_FOLDER_SEPARATOR) )) {
            return uri.replace(DOBLE_FOLDER_SEPARATOR,Folder.SEPARATOR);
        }
        return uri;
    }
}
