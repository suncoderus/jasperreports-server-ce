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
package com.jaspersoft.jasperserver.api.security.externalAuth;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.security.externalAuth.processors.ExternalUserProcessor;
import com.jaspersoft.jasperserver.api.security.externalAuth.processors.ProcessorData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.api.security.externalAuth.processors.ProcessorData.Key.EXTERNAL_AUTHORITIES;
import static com.jaspersoft.jasperserver.api.security.externalAuth.processors.ProcessorData.Key.EXTERNAL_AUTH_DETAILS;
import static com.jaspersoft.jasperserver.api.security.externalAuth.processors.ProcessorData.Key.EXTERNAL_LOADED_DETAILS;

/**
 *
 *     
 * @author dlitvak
 *
 */
public class ExternalDataSynchronizerImpl implements ExternalDataSynchronizer, InitializingBean {

	private static final Logger logger = LogManager.getLogger(ExternalDataSynchronizerImpl.class);


	private ExternalUserDetailsService externalUserDetailsService = new EmptyExternalUserDetailsService();
    private List<ExternalUserProcessor> externalUserProcessors;

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(externalUserProcessors, "externalUserProcessors must not be null");
        Assert.notEmpty(externalUserProcessors, "externalUserProcessors must not be empty: at least external user setup processor must be present to enter th user into JRS DB.");
    }

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void synchronize() {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (logger.isDebugEnabled())
			    logger.debug("Authentication token: " + (auth == null ? "none" : auth));

			if (auth != null && auth.getPrincipal() != null &&
					auth.isAuthenticated() &&
					!(auth instanceof AnonymousAuthenticationToken)) {

				//make sure this thread has fresh ProcessorData each time it invokes synchronize()
				ProcessorData.getInstance().clearData();

				loadExternalUserDetailsToProcessorData(auth);
				for  (ExternalUserProcessor processor : externalUserProcessors)
					processor.process();

				Authentication newAuth = SecurityContextHolder.getContext().getAuthentication();
				// The authentication can be null if there are no roles. This sets the anonymous user as the
				// logged-in user, if the anonymousUserFilter is in the chain
				if (newAuth != null && newAuth.getPrincipal() instanceof MetadataUserDetails) {
					MetadataUserDetails newPrincipal = (MetadataUserDetails) newAuth.getPrincipal();

					// Keep a hold of the original principal: it may be useful later
					newPrincipal.setOriginalAuthentication(auth);
				}
			}
		} catch (Exception e) {
			logger.error("Error during synchronization", e);
			// Because exception happened during successful authentication processing,
			// we need to clear the session of the authentication token.
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpSession sess = attr.getRequest().getSession();
			if (sess != null)
				sess.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
			throw new AuthenticationServiceException("Error during synchronization", e);
		}
	}

	/**
	 * This method merges external user data from Authentication and ExternalUserDetailsService and transfers them
	 * to ProcessorData.  ExternalUserProcessor's process ProcessorData into internal database entries.
	 *
	 * @param auth
	 */
	protected void loadExternalUserDetailsToProcessorData(final Authentication auth) {
		final Object principal = auth.getPrincipal();
		if (principal == null)
			throw new JSException("Principal is null.");

		final String username = principal instanceof UserDetails ? ((UserDetails)principal).getUsername() : principal.toString();
		final List<Map<String, Object>> userDetails = externalUserDetailsService.loadDetails(username);

		if (logger.isDebugEnabled())
			logger.debug("Loaded " + (userDetails != null ? userDetails.size() : "userDetails=null") + " userDetails for user " + username + ".");

		final ProcessorData processorData = ProcessorData.getInstance();

		if (userDetails != null && !userDetails.isEmpty())
			processorData.addData(EXTERNAL_LOADED_DETAILS, userDetails.get(0));

		processorData.addData(EXTERNAL_AUTH_DETAILS, auth.getPrincipal());
		processorData.addData(EXTERNAL_AUTHORITIES, auth.getAuthorities());
	}


	protected ExternalUserDetailsService getExternalUserDetailsService() {
		return externalUserDetailsService;
	}

	public void setExternalUserDetailsService(ExternalUserDetailsService externalUserDetailsService) {
		Assert.notNull(externalUserDetailsService, "Cannot set user details service to null.");
		this.externalUserDetailsService = externalUserDetailsService;
	}

	public void setExternalUserProcessors(List<ExternalUserProcessor> externalUserProcessors) {
		Assert.notNull(externalUserProcessors, "Cannot set empty processor list.");
		this.externalUserProcessors = externalUserProcessors;
	}

	protected List<ExternalUserProcessor> getExternalUserProcessors() {
		return externalUserProcessors;
	}
}
