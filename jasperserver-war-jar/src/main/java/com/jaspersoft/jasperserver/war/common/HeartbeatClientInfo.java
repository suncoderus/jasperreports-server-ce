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
package com.jaspersoft.jasperserver.war.common;

import java.util.Locale;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: HibernateLoggingService.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HeartbeatClientInfo extends HeartbeatInfo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String navigatorAppName = null;
	private String navigatorAppVersion = null;
	private Locale navigatorLocale = null;
	private Locale userLocale = null;
	private Integer screenWidth = null;
	private Integer screenHeight = null;
	private Integer screenColorDepth = null;
	private String userAgent = null;

	/**
	 * @return Returns the navigatorAppName.
	 */
	public String getNavigatorAppName() {
		return navigatorAppName;
	}
	/**
	 * @param navigatorAppName The navigatorAppName to set.
	 */
	public void setNavigatorAppName(String navigatorAppName) {
		this.navigatorAppName = navigatorAppName;
	}
	/**
	 * @return Returns the navigatorAppVersion.
	 */
	public String getNavigatorAppVersion() {
		return navigatorAppVersion;
	}
	/**
	 * @param navigatorAppVersion The navigatorAppVersion to set.
	 */
	public void setNavigatorAppVersion(String navigatorAppVersion) {
		this.navigatorAppVersion = navigatorAppVersion;
	}
	/**
	 * @return Returns the screenWidth.
	 */
	public Integer getScreenWidth() {
		return screenWidth;
	}
	/**
	 * @param screenWidth The screenWidth to set.
	 */
	public void setScreenWidth(Integer screenWidth) {
		this.screenWidth = screenWidth;
	}
	/**
	 * @return Returns the screenHeight.
	 */
	public Integer getScreenHeight() {
		return screenHeight;
	}
	/**
	 * @param screenHeight The screenHeight to set.
	 */
	public void setScreenHeight(Integer screenHeight) {
		this.screenHeight = screenHeight;
	}
	/**
	 * @return Returns the navigatorLocale.
	 */
	public Locale getNavigatorLocale() {
		return navigatorLocale;
	}
	/**
	 * @param navigatorLocale The navigatorLocale to set.
	 */
	public void setNavigatorLocale(Locale navigatorLocale) {
		this.navigatorLocale = navigatorLocale;
	}
	/**
	 * @return Returns the userLocale.
	 */
	public Locale getUserLocale() {
		return userLocale;
	}
	/**
	 * @param userLocale The userLocale to set.
	 */
	public void setUserLocale(Locale userLocale) {
		this.userLocale = userLocale;
	}
	/**
	 * @return Returns the screenColorDepth.
	 */
	public Integer getScreenColorDepth() {
		return screenColorDepth;
	}
	/**
	 * @param screenColorDepth The screenColorDepth to set.
	 */
	public void setScreenColorDepth(Integer screenColorDepth) {
		this.screenColorDepth = screenColorDepth;
	}
	/**
	 * @return Returns the userAgent.
	 */
	public String getUserAgent() {
		return userAgent;
	}
	/**
	 * @param userAgent The userAgent to set.
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public void contributeToHttpCall(HeartbeatCall call)
	{
		call.addParameter("navAppName[]", getNavigatorAppName() == null ? "" : getNavigatorAppName());
		call.addParameter("navAppVersion[]", getNavigatorAppVersion() == null ? "" : getNavigatorAppVersion());
		call.addParameter("navLocale[]", getNavigatorLocale() == null ? "" : getNavigatorLocale().toString());
		call.addParameter("userLocale[]", getUserLocale() == null ? "" : getUserLocale().toString());
		call.addParameter("scrWidth[]", getScreenWidth() == null ? "" : getScreenWidth().toString());
		call.addParameter("scrHeight[]", getScreenHeight() == null ? "" : getScreenHeight().toString());
		call.addParameter("scrColorDepth[]", getScreenColorDepth() == null ? "" : getScreenColorDepth().toString());
		call.addParameter("userAgent[]", getUserAgent() == null ? "" : getUserAgent());
		call.addParameter("clientCount[]", String.valueOf(getCount()));
	}

	public String getKey()
	{
		return 
			getNavigatorAppName() 
			+ "|" + getNavigatorAppVersion()
			+ "|" + getNavigatorLocale()
			+ "|" + getUserLocale()
			+ "|" + getScreenWidth()
			+ "|" + getScreenHeight()
			+ "|" + getScreenColorDepth()
			+ "|" + getUserAgent();
	}
}
