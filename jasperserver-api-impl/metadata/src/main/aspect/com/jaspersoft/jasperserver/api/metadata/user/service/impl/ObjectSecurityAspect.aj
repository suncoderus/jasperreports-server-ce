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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import org.acegisecurity.intercept.method.aspectj.AspectJCallback;
import org.acegisecurity.intercept.method.aspectj.AspectJSecurityInterceptor;
import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * @author swood
 *
 */
public aspect ObjectSecurityAspect implements InitializingBean {
	private AspectJSecurityInterceptor securityInterceptor;

	pointcut inDomainModel(): 
		within(com.jaspersoft.jasperserver.api.metadata..*);
	
	pointcut javaLangObjectMethodExecution(): 
		execution(* Object.*(..));
	
	pointcut domainObjectInstanceExecution(): 
		target(Resource) && 
		execution(public * *(..)) && 
		!javaLangObjectMethodExecution() && 
		inDomainModel();

	Object around(): domainObjectInstanceExecution() {
		if (this.securityInterceptor != null) {
			AspectJCallback callback = new AspectJCallback() {
				public Object proceedWithObject() {
					return proceed();
				}
			};
			return this.securityInterceptor.invoke(thisJoinPoint, callback);
		} else {
			return proceed();
		}
	}

	public AspectJSecurityInterceptor getSecurityInterceptor() {
		return securityInterceptor;
	}

	public void setSecurityInterceptor(AspectJSecurityInterceptor securityInterceptor) {
		this.securityInterceptor = securityInterceptor;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */

	public void afterPropertiesSet() throws Exception {
		if (this.securityInterceptor == null)
			throw new IllegalArgumentException("securityInterceptor required");
	}

}
