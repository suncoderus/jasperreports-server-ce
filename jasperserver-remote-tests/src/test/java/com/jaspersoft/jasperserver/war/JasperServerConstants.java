/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war;

import java.util.Properties;

/**
 * Contains the username and password used to log in
 * Also contains the initial url as base usl used by all the pages
 **/
public class JasperServerConstants {

    /* changed from an interface to a singleton class so it can be overridden
       for builds that layer on top of OS that want to reuse remote-tests */

    private static JasperServerConstants mConstants;
    private JasperServerConstants() {
        setupProperties();
        setDefaults();
    }

    public static JasperServerConstants instance() {
	if (mConstants == null) {
	    mConstants = new JasperServerConstants();
	}
	return mConstants;
    }

    protected void setupProperties() {
        Properties sys = System.getProperties();

        HOST = sys.getProperty(PROP_HOST_NAME, "localhost");
        PORT = sys.getProperty(PROP_HOST_PORT, "8080");
        APP_CONTEXT_PATH = sys.getProperty(PROP_APP_CONTEXT_PATH, "jasperserver");
    }

    protected void setDefaults() {

        USERNAME      = "jasperadmin";
        PASSWORD      = "jasperadmin";
        USER_USERNAME = "joeuser";
        USER_PASSWORD = "joeuser";
        BASE_URL      = "http://" + HOST + ":" + PORT;
        XMLA_URL      = BASE_URL + "/" + APP_CONTEXT_PATH + "/xmla";
        HOME_PAGE_URL = BASE_URL + "/" + APP_CONTEXT_PATH + "/home.html";
        WS_END_POINT_URL = BASE_URL + "/" + APP_CONTEXT_PATH + "/services/repository";
        WS_SCHEDULING_END_POINT_URL = BASE_URL + "/" + APP_CONTEXT_PATH + "/services/ReportScheduler";

        WS_PROTOCOL   = "http://";
        WS_BASE_URL = HOST + ":" + PORT + "/" + APP_CONTEXT_PATH + "/services/";
        WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL =
            WS_PROTOCOL + USERNAME + ":" + PASSWORD + "@" + WS_BASE_URL + "UserAndRoleManagementService";
        WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL_AS_USER =
            WS_PROTOCOL + USER_USERNAME + ":" + USER_PASSWORD + "@" + WS_BASE_URL + "UserAndRoleManagementService";

        WS_PERMISSIONS_MANAGEMENT_END_POINT_URL =
            WS_PROTOCOL + USERNAME + ":" + PASSWORD + "@" + WS_BASE_URL + "PermissionsManagementService";

        USERNAME2     = "joeuser";
        PASSWORD2  = "joeuser";
        BAD_PASSWORD2  = "wrongPassword";
    }

    public String PROP_HOST_NAME = "remote.test.host";
    public String PROP_HOST_PORT = "remote.test.port";
    public String PROP_APP_CONTEXT_PATH = "remote.test.app-context-path";

    public String HOST;
    public String PORT;
    public String APP_CONTEXT_PATH;
    public String USERNAME;
    public String PASSWORD;
    public String USER_USERNAME;
    public String USER_PASSWORD;
    public String BASE_URL;
    public String XMLA_URL;
    public String HOME_PAGE_URL;
    public String WS_END_POINT_URL;
    public String WS_SCHEDULING_END_POINT_URL;

    public String WS_PROTOCOL;
    public String WS_BASE_URL;
    public String WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL;
    public String WS_USER_AND_ROLE_MANAGEMENT_END_POINT_URL_AS_USER;

    public String WS_PERMISSIONS_MANAGEMENT_END_POINT_URL;

    public String USERNAME2;
    public String PASSWORD2;
    public String BAD_PASSWORD2;
}
