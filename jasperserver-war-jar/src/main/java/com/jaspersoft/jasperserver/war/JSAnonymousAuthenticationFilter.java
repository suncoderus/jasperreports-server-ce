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
package com.jaspersoft.jasperserver.war;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.util.HashSet;
import java.util.List;

/**
 * This filter extends Spring's AnonymousAuthenticationFilter in order to insert MetadataUserDetails
 * as principal.
 *
 * @author dlitvak
 * @version 3/11/15
 */
public class JSAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {
	private static final User anonymousUser = new UserImpl();
	static {
		anonymousUser.setUsername("anonymousUser");
		anonymousUser.setFullName("anonymousUser");
		anonymousUser.setEnabled(true);
		List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
		anonymousUser.setRoles(new HashSet<GrantedAuthority>(roles));
	}

	public JSAnonymousAuthenticationFilter(String key) {
		super(key, new MetadataUserDetails(anonymousUser), AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
	}
}
