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

import java.security.ProtectionDomain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.ProtectionDomainFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ProtectionDomainJRAdapter implements ProtectionDomainFactory {

	private static final Log log = LogFactory.getLog(ProtectionDomainJRAdapter.class);
	
	private ProtectionDomainProvider provider;

	public ProtectionDomainProvider getProvider() {
		return provider;
	}

	public void setProvider(ProtectionDomainProvider provider) {
		this.provider = provider;
	}

	public void setJRProtectionDomain() {
		if (log.isDebugEnabled()) {
			log.debug("Setting JR protection domain using " + provider);
		}
		
		JRClassLoader.setProtectionDomainFactory(this);
	}

	public ProtectionDomain getProtectionDomain(ClassLoader classloader) {
		return getProvider().getProtectionDomain();
	}
}
