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

package com.jaspersoft.jasperserver.api.common.util.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReattemptMethodInterceptor implements MethodInterceptor {

	private static final Log log = LogFactory.getLog(ReattemptMethodInterceptor.class);
	
	private ReattemptAttributes reattemptAttributes;
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		ReattemptMethodAttributes attributes = getReattemptAttributes().getMethodAttributes(
				invocation.getMethod());
		Object result = null;
		if (attributes == null) {
			result = invocation.proceed();
		} else {
			int attempt = 0;
			boolean failed;
			do {
				try {
					++attempt;
					result = invocation.proceed();
					failed = false;
				} catch (Exception e) {
					failed = true;
					if (attributes.toReattempt(e, attempt)) {
						if (log.isDebugEnabled()) {
							log.debug("Caught exception on method invocation " + invocation + ", reattempting", e);
						}
					} else {
						if (log.isDebugEnabled()) {
							log.debug("Caught exception on method invocation " + invocation + ", aborting", e);
						}
						throw e;
					}
				}
			} while (failed);
		}
		return result;
	}

	public ReattemptAttributes getReattemptAttributes() {
		return reattemptAttributes;
	}

	public void setReattemptAttributes(ReattemptAttributes reattemptAttributes) {
		this.reattemptAttributes = reattemptAttributes;
	}

}
