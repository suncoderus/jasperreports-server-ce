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

package com.jaspersoft.jasperserver.api.common.error.handling;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dlitvak
 * @version $Id$
 */
public class ExceptionOutputManagerImpl implements ExceptionOutputManager {
	private static final Logger logger = LogManager.getLogger(ExceptionOutputManagerImpl.class);

    private static enum CONTROL_KEYS {
		MESSAGE,
        STACKTRACE,
        ERROR_UID
	}

	private Map<String, List<String>> outputControlMap = new HashMap<String, List<String>>();

	public void setOutputControlMap(Map<String, List<String>> outputControlMap) {
		this.outputControlMap = outputControlMap;
	}

	@Override
	public boolean isExceptionMessageAllowed() {
		return isOutputPermitted(CONTROL_KEYS.MESSAGE);
	}

	@Override
	public boolean isStackTraceAllowed() {
		return isOutputPermitted(CONTROL_KEYS.STACKTRACE);
	}

	@Override
	public boolean isUIDOutputOn() {
		return isOutputPermitted(CONTROL_KEYS.ERROR_UID);
	}

	private boolean isOutputPermitted(CONTROL_KEYS k) {
		try {
			List<String> configRoles = Collections.emptyList();
			switch (k) {
				case MESSAGE:
					configRoles = outputControlMap.get(MESSAGE_CONTROL_KEY);
					break;
				case STACKTRACE:
					configRoles = outputControlMap.get(STACKTRACE_CONTROL_KEY);
					break;
				case ERROR_UID:
					configRoles = outputControlMap.get(ERROR_UID_CONTROL_KEY);
					break;
			}

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication.isAuthenticated()) {
				Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
				for (GrantedAuthority ga : grantedAuthorities) {
                    String usrAuthority = ga.getAuthority();
                    usrAuthority = usrAuthority.split("\\|")[0];
                    if (configRoles.contains(usrAuthority))
						return true;
				}
			}
		}
		catch (Exception e) {
			logger.warn("Error figuring out whether to show stacktraces.", e);
		}
		return false;
	}
}
