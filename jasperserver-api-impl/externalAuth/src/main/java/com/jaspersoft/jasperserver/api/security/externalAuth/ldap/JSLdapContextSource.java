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
package com.jaspersoft.jasperserver.api.security.externalAuth.ldap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import java.util.Hashtable;

/**
 * This class is a fix to spring-security 2.0.7 bug in ldap (JS bug 25501).
 *
 * User: dlitvak
 * Date: 2/1/13
 */
public class JSLdapContextSource extends DefaultSpringSecurityContextSource {
	private static final Log logger = LogFactory.getLog(JSLdapContextSource.class);

	/**
	 * Create and initialize an instance which will connect to the supplied LDAP URL.
	 *
	 * @param providerUrl an LDAP URL of the form <code>ldap://localhost:389/base_dn<code>
	 */
	public JSLdapContextSource(String providerUrl) {
		super(providerUrl);
	}

	public DirContext getReadWriteContext(String userDn, Object credentials) {
		Hashtable env = new Hashtable(getAnonymousEnv());

		env.put(Context.SECURITY_PRINCIPAL, userDn);
		env.put(Context.SECURITY_CREDENTIALS, credentials);
		env.remove(SUN_LDAP_POOLING_FLAG);

		if (logger.isDebugEnabled()) {
			logger.debug("Creating context with principal: '" + userDn + "'");
		}

		return createContext(env);
	}
}
