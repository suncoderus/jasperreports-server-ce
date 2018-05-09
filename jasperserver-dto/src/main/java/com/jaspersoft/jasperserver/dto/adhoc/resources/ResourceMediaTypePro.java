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
package com.jaspersoft.jasperserver.dto.adhoc.resources;

import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface ResourceMediaTypePro extends ResourceMediaType {
    // Dashboard
    public static final String DASHBOARD_CLIENT_TYPE = "dashboard";
    public static final String DASHBOARD_JSON = RESOURCE_MEDIA_TYPE_PREFIX + DASHBOARD_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String DASHBOARD_XML = RESOURCE_MEDIA_TYPE_PREFIX + DASHBOARD_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // reportOptions
    public static final String REPORT_OPTIONS_CLIENT_TYPE = "reportOptions";
    public static final String REPORT_OPTIONS_JSON = RESOURCE_MEDIA_TYPE_PREFIX + REPORT_OPTIONS_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String REPORT_OPTIONS_XML = RESOURCE_MEDIA_TYPE_PREFIX + REPORT_OPTIONS_CLIENT_TYPE + RESOURCE_XML_TYPE;
    // domainTopic
    public static final String DOMAIN_TOPIC_CLIENT_TYPE = "domainTopic";
    public static final String DOMAIN_TOPIC_CLIENT_TYPE_JSON = RESOURCE_MEDIA_TYPE_PREFIX + DOMAIN_TOPIC_CLIENT_TYPE + RESOURCE_JSON_TYPE;
    public static final String DOMAIN_TOPIC_CLIENT_TYPE_XML = RESOURCE_MEDIA_TYPE_PREFIX + DOMAIN_TOPIC_CLIENT_TYPE + RESOURCE_XML_TYPE;

}
