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

package com.jaspersoft.jasperserver.war.webHelp;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author angus
 *
 */
public class WebHelpLookup implements InitializingBean {

	private static WebHelpLookup myInstance;
	
	private HashMap<String,String> helpContextMap;
	private String showHelp;
	private String hostURL;
	private String pagePrefix;	
	
	public HashMap<String,String> getHelpContextMap() {
		return this.helpContextMap;
	}
	
	public void setHelpContextMap(HashMap<String,String> helpContextMap) {
		this.helpContextMap = helpContextMap;
	}
	
	public String getDisplay() {
		return getShowHelp().equals("true") ? "" : "none"; 
	}

    public boolean isShowHelpTrue() {
        return "true".equals(getShowHelp());
    }

    public String getShowHelp() {
		return showHelp;
	}

	public void setShowHelp(String showHelp) {
		this.showHelp = showHelp;
	}

	public String getHostURL() {
		return hostURL;
	}

	public void setHostURL(String hostURL) {
		this.hostURL = hostURL;
	}

	public String getPagePrefix() {
		return pagePrefix;
	}

	public void setPagePrefix(String pagePrefix) {
		this.pagePrefix = pagePrefix;
	}

	public void afterPropertiesSet() throws Exception {
    	WebHelpLookup.myInstance = this;
    }	
    
    public static WebHelpLookup getInstance() {
    	return WebHelpLookup.myInstance;
    }
    
    public String getHelpContextMapAsJSON() {
    	return new JSONObject(getHelpContextMap()).toString();
    }
}
