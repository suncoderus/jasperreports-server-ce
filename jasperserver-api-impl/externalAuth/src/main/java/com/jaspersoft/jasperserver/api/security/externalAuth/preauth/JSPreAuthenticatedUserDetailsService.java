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
package com.jaspersoft.jasperserver.api.security.externalAuth.preauth;

import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalUserDetails;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.util.Assert;

import javax.security.auth.login.CredentialExpiredException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * User: dlitvak
 * Date: 12/19/13
 */
public class JSPreAuthenticatedUserDetailsService extends PreAuthenticatedGrantedAuthoritiesUserDetailsService implements InitializingBean {
	private static final Logger logger = LogManager.getLogger(JSPreAuthenticatedUserDetailsService.class);

	public static final String PREAUTH_USERNAME = "username";
	public static final String PREAUTH_ROLES = "roles";
	public static final String PREAUTH_ORG = "orgId";
	public static final String PREAUTH_EXPIRE_TIME = "expireTime";
	public static final String PROFILE_ATTRIBUTES = "profile.attribs";


    public static final String TOKEN_KEY_VALUE_PAIR_SEPARATOR = "=";
	public static final String TOKEN_VALUE_SEPARATOR = ",";

	private String tokenPairSeparator = "\\|";
	private Map<String,Object> tokenFormatMapping = new HashMap<String, Object>();
	private Map<String, String> tokenProfileAttributeMap = new HashMap<String, String>();

	//expireDateFormatter is ThreadLocal to avoid multi-threading issues
	private static ThreadLocal<DateFormat> expireDateFormatter = new ThreadLocal<DateFormat>();
	private String tokenExpireTimestampFormat = "yyyyMMddHHmmssZ";

	/**
	 * Invoked by a BeanFactory after it has set all bean properties supplied
	 * (and satisfied BeanFactoryAware and ApplicationContextAware).
	 * <p>This method allows the bean instance to perform initialization only
	 * possible when all bean properties have been set and to throw an
	 * exception in the event of misconfiguration.
	 *
	 * @throws Exception in the event of misconfiguration (such
	 *                   as failure to set an essential property) or if initialization fails.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.isTrue(this.tokenFormatMapping.containsKey(PREAUTH_USERNAME), PREAUTH_USERNAME + " must be mapped in tokenFormatMapping");
		Assert.isTrue(this.tokenFormatMapping.containsKey(PREAUTH_ROLES), PREAUTH_ROLES + " must be mapped in tokenFormatMapping");
		Assert.isTrue(this.tokenFormatMapping.containsKey(PREAUTH_ORG), PREAUTH_ORG + " must be mapped in tokenFormatMapping");
	}

	/**
	 * Creates the final <tt>UserDetails</tt> object. Can be overridden to customize the contents.
	 *
	 * @param token       the authentication request token
	 * @param authorities the pre-authenticated authorities.
	 */
	@Override
	protected UserDetails createuserDetails(Authentication token, Collection<? extends GrantedAuthority> authorities) {
		try {
			logger.debug("Start building pre-auth user details");

			String preAuthTokenStr = (String)token.getPrincipal();
			String[] tokenKeyValuePairs = preAuthTokenStr.split(tokenPairSeparator);

			String externalUsername = null;
			String[] externalUserOrg = null;
			Set<String> externalUserRoles = new HashSet<String>();
			Map<String, String> externalUserProfileAttribsMap = new HashMap<String, String>();
			Map<String, String> unmappedParamsMap = new HashMap<String, String>();
			String tokenExpireDateStr = null;

			//create a token map
			for (String tokenPair : tokenKeyValuePairs) {
				String[] tokenPairArr = tokenPair.split(TOKEN_KEY_VALUE_PAIR_SEPARATOR);

				if (tokenPairArr.length != 2) {
					logger.error("Invalid pre-authenticated token format. Pair " + tokenPair + " is missing key-value separator "
						   + TOKEN_KEY_VALUE_PAIR_SEPARATOR + ". Skipping this pair processing.");
					continue;
				}

				final String tokenKey = tokenPairArr[0];
				final String tokenValue = tokenPairArr[1];
				final String[] tokenValuesArr = tokenValue.split(TOKEN_VALUE_SEPARATOR);

				if (tokenKey.equalsIgnoreCase(String.valueOf(tokenFormatMapping.get(PREAUTH_USERNAME)))) {
					if (tokenValuesArr.length != 1)
						logger.warn("A list of users was passed in. Only one user is supported.  Using only the 1st user: " + tokenValuesArr[0]);
					externalUsername = tokenValuesArr[0];
				}
				else if (tokenKey.equalsIgnoreCase(String.valueOf(tokenFormatMapping.get(PREAUTH_ROLES)))) {
					externalUserRoles.addAll(Arrays.asList(tokenValuesArr));
				}
				else if (tokenKey.equalsIgnoreCase(String.valueOf(tokenFormatMapping.get(PREAUTH_ORG)))) {
					externalUserOrg = tokenValuesArr;
				}
				else if (tokenKey.equalsIgnoreCase(String.valueOf(tokenFormatMapping.get(PREAUTH_EXPIRE_TIME)))) {
					tokenExpireDateStr = tokenValue;
				}
				else if (tokenProfileAttributeMap.containsKey(tokenKey)) {
					externalUserProfileAttribsMap.put(tokenProfileAttributeMap.get(tokenKey), tokenValue);
				}
				else {
					unmappedParamsMap.put(tokenKey, tokenValue);
				}
			}

            List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>(externalUserRoles.size());
			for (String r : externalUserRoles) {
                authorityList.add(new SimpleGrantedAuthority(r));
            }

			ExternalUserDetails eud = createExternalUserDetails(externalUsername, authorityList, externalUserOrg, tokenExpireDateStr);
			Map<String, Object> addlDetailsMap = eud.getAdditionalDetailsMap();
			addlDetailsMap.put(ExternalUserDetails.PROFILE_ATTRIBUTES_ADDITIONAL_MAP_KEY, externalUserProfileAttribsMap);
			addlDetailsMap.put(ExternalUserDetails.UNMAPPED_PARAMS_MAP_KEY, unmappedParamsMap);
			return eud;
		} catch (CredentialExpiredException e) {
			logger.warn(e);
			throw new BadCredentialsException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e);
			throw new BadCredentialsException(e.getMessage(), e);
		}
	}

	/**
	 * Method creating Authentication principal UserDetails
	 *
	 * @param externalUsername - external username
	 * @param authorities - array of the external user roles
	 * @param externalUserOrgArr - Not Used in non-MT version
	 * @param tokenExpireDateStr - Token expire date.  If absent, token never expires.
	 * @return Authentication principal UserDetails
	 * @throws Exception if the token expire date format cannot be parsed
	 */
	protected ExternalUserDetails createExternalUserDetails(String externalUsername, Collection<? extends GrantedAuthority> authorities, String[] externalUserOrgArr, String tokenExpireDateStr)
		throws Exception
	{
		if  (logger.isDebugEnabled())
			logger.debug("Creating pre-auth external user details for user " + externalUsername + ", expire date: " + tokenExpireDateStr);

		if (tokenExpireDateStr != null) {
			Date expireDate = getExpireDateFormatter().parse(tokenExpireDateStr);
			if (new Date().after(expireDate))
				throw new CredentialExpiredException("Pre Auth token for " + externalUsername + " has expired.");
		}

		return new ExternalUserDetails(externalUsername, authorities);
	}

	public void setTokenPairSeparator(String tokenPairSeparator) {
		this.tokenPairSeparator = Pattern.quote(tokenPairSeparator);
	}

	public void setTokenFormatMapping(Map<String, Object> tokenFormatMapping) {
		this.tokenFormatMapping = tokenFormatMapping;
		final Map<String, String> paMap = (Map<String, String>) tokenFormatMapping.get(PROFILE_ATTRIBUTES);
		Map<String, String> invertedPaMap = new HashMap<String, String>();
		if (paMap != null) {
			for (Map.Entry<String, String> e : paMap.entrySet()) {
				invertedPaMap.put(e.getValue(), e.getKey());
			}
		}
		this.tokenProfileAttributeMap = (paMap != null ? invertedPaMap : this.tokenProfileAttributeMap);
	}

	public void setTokenExpireTimestampFormat(String tokenExpireTimestampFormat) {
		this.tokenExpireTimestampFormat = tokenExpireTimestampFormat;
	}

	public String getTokenExpireTimestampFormat() {
		return tokenExpireTimestampFormat;
	}

	protected DateFormat getExpireDateFormatter() {
		DateFormat dateFormat = expireDateFormatter.get();
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat(tokenExpireTimestampFormat);
			expireDateFormatter.set(dateFormat);
		}

		return dateFormat;
	}
}
