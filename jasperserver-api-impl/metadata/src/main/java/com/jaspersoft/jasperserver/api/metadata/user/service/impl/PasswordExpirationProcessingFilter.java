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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;


import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;

/**
 * @author achan
 *
 */
public class PasswordExpirationProcessingFilter implements Filter, InitializingBean {

	
    private UserAuthorityService userService;
    private String passwordExpirationInDays;


    protected boolean isPasswordExpired(ExecutionContext context, Authentication auth, int nDays) {
        return userService.isPasswordExpired(context, auth.getName(), nDays);
    }

	public void doFilter(ServletRequest request, ServletResponse response,
	        FilterChain chain) throws IOException, ServletException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		if (auth != null) {
		   // skip password expiration check if from trusted host
		   String fromTrustedHost = (String)request.getAttribute("fromTrustedHost");
		   if ("true".equals(fromTrustedHost)) {
			   request.removeAttribute("fromTrustedHost");
			   chain.doFilter(request, response);
			   return;
		   }
			
		   
		   // get expiration date
		   int nDays = 0;
		   try {
			   nDays = Integer.parseInt(passwordExpirationInDays);
		   } catch (NumberFormatException e) {}		
           if (nDays > 0) {
		      if (isPasswordExpired(null, auth, nDays)) {
			     SecurityContextHolder.getContext().setAuthentication(null);
			     chain.doFilter(request, response);
			     return;
		      }
           }
		}
		request.removeAttribute("fromTrustedHost");
		chain.doFilter(request, response);
	}
	
    public void afterPropertiesSet() throws Exception {
    }
    
    public void destroy() {}
    
    public void init(FilterConfig arg0) throws ServletException {}

	public UserAuthorityService getUserService() {
		return userService;
	}

	public void setUserService(UserAuthorityService userService) {
		this.userService = userService;
	}

	public String getPasswordExpirationInDays() {
		return passwordExpirationInDays;
	}

	public void setPasswordExpirationInDays(String passwordExpirationInDays) {
		this.passwordExpirationInDays = passwordExpirationInDays;
	}
	
    
	
}
