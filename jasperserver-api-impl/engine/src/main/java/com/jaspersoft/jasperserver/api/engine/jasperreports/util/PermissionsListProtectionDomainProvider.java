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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PermissionsListProtectionDomainProvider implements ProtectionDomainProvider, InitializingBean {
	
	private static final Log log = LogFactory.getLog(
			PermissionsListProtectionDomainProvider.class);
	
	private List<Permission> permissions;
	private ProtectionDomain protectionDomain;
	
	public PermissionsListProtectionDomainProvider() {
		
	}

	public void afterPropertiesSet() throws Exception {
		if (System.getSecurityManager() == null) {
			log.warn("A security manager has not been configured for the JVM. "
					+ "The protection domain set for the reports will NOT be effective.");
		}
		
		protectionDomain = createProtectionDomain();
	}
	
	protected ProtectionDomain createProtectionDomain() {
		CodeSource codeSource = getCodeSource();
		PermissionCollection permissionCollection = getPermissionCollection();
		return new ProtectionDomain(codeSource, permissionCollection);
	}

	protected CodeSource getCodeSource() {
		try {
			URL location = new URL(null, "repo:/", new URLStreamHandler() {
				protected URLConnection openConnection(URL u) throws IOException {
					throw new IOException("Cannot read from repo:/ location");
				}
			});
			return new CodeSource(location, (Certificate[]) null);
		} catch (MalformedURLException e) {
			throw new JSExceptionWrapper(e);
		}
	}
	
	protected PermissionCollection getPermissionCollection() {
		Permissions permissionCollection = new Permissions();
		if (permissions != null) {
			for (Permission permission : permissions) {
				permissionCollection.add(permission);
			}
		}
		return permissionCollection;
	}
	
	public ProtectionDomain getProtectionDomain() {
		return protectionDomain;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

}
