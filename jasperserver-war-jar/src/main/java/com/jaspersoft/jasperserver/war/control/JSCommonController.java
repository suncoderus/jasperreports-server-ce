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
package com.jaspersoft.jasperserver.war.control;


import com.jaspersoft.jasperserver.api.common.util.TimeZonesList;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl;
import com.jaspersoft.jasperserver.api.security.SecurityConfiguration;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalAuthProperties;
import com.jaspersoft.jasperserver.war.common.HeartbeatBean;
import com.jaspersoft.jasperserver.war.common.HeartbeatClientInfo;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.LocalesList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;


/**
 * @author aztec
 * @version $Id$
 */
public class JSCommonController extends JRBaseMultiActionController {
	public static final String JSP_REQUEST_HANDLING_PREFIX = "jsp:";
    public static final String IS_DEVELOPMENT_ENVIRONMENT_TYPE = "isDevelopmentEnvironmentType";
    public static final String USERS_EXCEEDED = "usersExceeded";
    public static final String BAN_USER = "banUser";
	protected HeartbeatBean heartbeat;
	private ExternalAuthProperties externalAuthPropertiesBean;

	private LocalesList locales;
	private TimeZonesList timezones;
	private String allowUserPasswordChange;
	private String passwordExpirationInDays;
    /* On/Off auto completion for login form */
	private String autoCompleteLoginForm;

	private String externalAuthPropertiesBeanName = "externalAuthProperties";

 	private static final Log log = LogFactory.getLog(JSCommonController.class);

	/*
	 * Overridden method for handling the requests
	 * @args HttpServletRequest, HttpServletResponse
	 * @returns ModelAndView - Home Page
	 */
	public ModelAndView homePage(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		return new ModelAndView("modules/home");
	}

    public ModelAndView login(HttpServletRequest req, HttpServletResponse res)
            throws ServletException {
        setupLoginPage(req);

        return new ModelAndView("modules/login/login");
    }

    public ModelAndView externalLogin(HttpServletRequest req, HttpServletResponse res)
            throws ServletException {
		req.setAttribute("externalAuthPropertiesBean", getExternalAuthPropertiesBean());
        return new ModelAndView("modules/login/externalLogin");
    }

    protected void setupLoginPage(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        String locale = null;
        String preferredTz = null;
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie.getName().equals(JasperServerConstImpl.getUserLocaleSessionAttr()))
                    locale = cookie.getValue();
                if (cookie.getName().equals(JasperServerConstImpl.getUserTimezoneSessionAttr()))
                    preferredTz = cookie.getValue();
            }
        }

        Locale displayLocale = req.getLocale();
        String preferredLocale;
        if (locale == null || locale.length() == 0) {
            preferredLocale = displayLocale.toString();
        } else {
            preferredLocale = locale;
        }

        if (preferredTz == null) {
            preferredTz = timezones.getDefaultTimeZoneID();
        }

        req.setAttribute("preferredLocale", preferredLocale);
        req.setAttribute("userLocales", locales.getUserLocales(displayLocale));
        req.setAttribute("preferredTimezone", preferredTz);
        req.setAttribute("userTimezones", timezones.getTimeZones(displayLocale));
        try {
            if (Integer.parseInt(passwordExpirationInDays) > 0) {
                allowUserPasswordChange = "true";
            }
        } catch (NumberFormatException e) {
            // if the value is NaN, then assume it's non postive.
            // not overwrite allowUserPasswordChange
        }
        req.setAttribute("allowUserPasswordChange", allowUserPasswordChange);
        req.setAttribute("passwordExpirationInDays", passwordExpirationInDays);
        req.setAttribute("passwordPattern", userAuthService.getAllowedPasswordPattern().replace("\\", "\\\\"));
        req.setAttribute("autoCompleteLoginForm", autoCompleteLoginForm);
        req.setAttribute(IS_DEVELOPMENT_ENVIRONMENT_TYPE, false);
        req.setAttribute(USERS_EXCEEDED, false);
        req.setAttribute(BAN_USER, false);
        req.setAttribute("isEncryptionOn", SecurityConfiguration.isEncryptionOn());
    }

    public ModelAndView heartbeat(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        boolean isCallPermitted = false;

        String permit = req.getParameter("permit");
        if (permit != null) {
            isCallPermitted = Boolean.valueOf(permit);
        }

        heartbeat.permitCall(isCallPermitted);

        return new ModelAndView("ajax/ajaxresponse");
    }

    public ModelAndView heartbeatInfo(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        final HeartbeatClientInfo info = new HeartbeatClientInfo();

        info.setNavigatorAppName(req.getParameter("navAppName"));
        info.setNavigatorAppVersion(req.getParameter("navAppVersion"));
        info.setNavigatorLocale(req.getLocale());
        info.setUserLocale(LocaleContextHolder.getLocale());
        if (req.getParameter("scrWidth") != null) {
            info.setScreenWidth(new Integer(req.getParameter("scrWidth")));
        }
        if (req.getParameter("scrHeight") != null) {
            info.setScreenHeight(new Integer(req.getParameter("scrHeight")));
        }
        if (req.getParameter("scrColorDepth") != null) {
            info.setScreenColorDepth(new Integer(req.getParameter("scrColorDepth")));
        }
        info.setUserAgent(req.getHeader("user-agent"));

        new Thread(
                new Runnable() {
                    public void run() {
                        heartbeat.updateClientInfo(info);
                    }
                }
        ).start();

        return new ModelAndView("ajax/ajaxresponse");
    }


    public ModelAndView exitUser(HttpServletRequest req, HttpServletResponse res) {
		String redirectURL = "/logout.html";
		if (req.getParameter("showPasswordChange") == null){
			if (req.getParameter("weakPassword") != null){
				redirectURL += "?weakPassword=" + req.getParameter("weakPassword");
			}
		} else {
			redirectURL += "?showPasswordChange=" + req.getParameter("showPasswordChange");
			if (req.getParameter("weakPassword") != null){
				redirectURL += "&weakPassword=" + req.getParameter("weakPassword");
			}
		}

	if (UserAuthorityServiceImpl.isUserSwitched()) {
	    redirectURL = "/j_acegi_exit_user";
	}
	return new ModelAndView("redirect:" + redirectURL);
    }

	public ModelAndView logout(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		// invalidate session
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		//Determine if the user logging out is internal.  If it is, logout to the internal JRS logout url.
		//If the user is external, logout to the url defined in ExternalAuthProperties bean (app context)
		boolean loggedinUserIsExternal = false;
		final Authentication authObj = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authObj.getPrincipal();
		if (principal instanceof MetadataUserDetails) {
			loggedinUserIsExternal = ((MetadataUserDetails) principal).isExternallyDefined();
		}

		// we aren't using RememberMe but this is how we'd log out if we did
		// Cookie terminate = new Cookie(TokenBasedRememberMeServices.ACEGI_SECURITY_HASHED_REMEMBER_ME_COOKIE_KEY, null);
		// terminate.setMaxAge(0);
		// res.addCookie(terminate);
		SecurityContextHolder.clearContext(); //invalidate authentication

		ExternalAuthProperties externalAuthProperties = getExternalAuthPropertiesBean();
		String externalLogoutUrl = (externalAuthProperties != null ? externalAuthProperties.getLogoutUrl() : null);
		if (loggedinUserIsExternal && externalLogoutUrl != null && externalLogoutUrl.length() > 0) {
			return new ModelAndView("redirect:" + externalLogoutUrl);
		}
		else {
			String redirectURL = "redirect:/login.html";
			if (req.getParameter("showPasswordChange") == null){
				if (req.getParameter("weakPassword") != null){
					redirectURL += "?weakPassword=" + req.getParameter("weakPassword");
				}
			} else {
				redirectURL += "?showPasswordChange=" + req.getParameter("showPasswordChange");
				if (req.getParameter("weakPassword") != null){
					redirectURL += "&weakPassword=" + req.getParameter("weakPassword");
				}
			}

			return new ModelAndView(redirectURL);
		}
	}

	protected ExternalAuthProperties getExternalAuthPropertiesBean() {
		try {
			if (externalAuthPropertiesBean == null)
				externalAuthPropertiesBean = (ExternalAuthProperties) getApplicationContext().getBean(externalAuthPropertiesBeanName);
		}
		catch (Exception e) {
			log.warn("Could not find " + ExternalAuthProperties.class + " bean in the context (using default).  Check that external " +
					"authentication context xml is in the path.");
		}
		return externalAuthPropertiesBean;
	}

	public ModelAndView loginError(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		log.warn("There was a login error");
		return new ModelAndView("modules/loginError");
	}

	public ModelAndView securityError(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		log.warn("There was a security error");
		return new ModelAndView("modules/system/prepErrorPage");
	}

	/*
	 * @args req, res
	 * @returns ModelAndView - menutest.jsp
	 */
	public ModelAndView menuTest(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		return new ModelAndView("menutest");
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			String methodName = this.getMethodNameResolver().getHandlerMethodName(request);
			if (methodName.startsWith(JSP_REQUEST_HANDLING_PREFIX))
				return new ModelAndView(methodName.substring(JSP_REQUEST_HANDLING_PREFIX.length()));

			return invokeNamedMethod(methodName, request, response);
		}
		catch (NoSuchRequestHandlingMethodException ex) {
			return handleNoSuchRequestHandlingMethod(ex, request, response);
		}
	}

	public LocalesList getLocales()
	{
		return locales;
	}

	public void setLocales(LocalesList locales)
	{
		this.locales = locales;
	}

	public TimeZonesList getTimezones()
	{
		return timezones;
	}

	public void setTimezones(TimeZonesList timezones)
	{
		this.timezones = timezones;
	}

	public String getAllowUserPasswordChange()
	{
		return allowUserPasswordChange;
	}

	public void setAllowUserPasswordChange(String changePassword)
	{
		this.allowUserPasswordChange = changePassword;
	}

	public String getPasswordExpirationInDays() {
		return passwordExpirationInDays;
	}

	public void setPasswordExpirationInDays(String passwordExpirationInDays) {
		this.passwordExpirationInDays = passwordExpirationInDays;
	}

	public HeartbeatBean getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(HeartbeatBean heartbeat) {
		this.heartbeat = heartbeat;
	}

    public String getAutoCompleteLoginForm() {
        return autoCompleteLoginForm;
    }

    public void setAutoCompleteLoginForm(String autoCompleteLoginForm) {
        this.autoCompleteLoginForm = autoCompleteLoginForm;
    }

	public String getExternalAuthPropertiesBeanName() {
		return externalAuthPropertiesBeanName;
	}

	public void setExternalAuthPropertiesBeanName(String externalAuthPropertiesBeanName) {
		this.externalAuthPropertiesBeanName = externalAuthPropertiesBeanName;
	}

	/*
	 * Overridden method for handling the requests
	 * @args HttpServletRequest, HttpServletResponse
	 * @returns ModelAndView - Home Page
	 */
	public ModelAndView encryptionPage(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		return new ModelAndView("modules/encrypt");
	}
}
