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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * User: dlitvak
 * Date: 3/22/13
 */
// TODO Spring Security Upgrade: fix dependency on org.springframework.security.core.userdetails.UserTests
public class ExternalUserDetailsTest /*extends UserTests */{

	public static final String TEST_USER_NAME = "testUser";
	public static final String TEST_ROLE = "testRole";
	public static final boolean USER_ACCOUNT_ENABLED = true;
	public static final boolean USER_ACCOUNT_NOT_EXPIRED = true;
	public static final boolean USER_CREDS_NOT_EXPIRED = true;
	public static final boolean USER_ACCOUNT_NON_LOCKED = true;
	public static final String TEST_PASSWORD = "testPassword";


	public void construstorExternalUserDetailsTest() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);
        authorities.add(new SimpleGrantedAuthority(TEST_ROLE));
		ExternalUserDetails externalUserDetails= new ExternalUserDetails(TEST_USER_NAME, TEST_PASSWORD,
				USER_ACCOUNT_ENABLED, USER_ACCOUNT_NOT_EXPIRED, USER_CREDS_NOT_EXPIRED, USER_ACCOUNT_NON_LOCKED,
                authorities);

		assertTrue("Test user name did not match after externalUserDetails instantiation", TEST_USER_NAME.equals(externalUserDetails.getUsername()));
		assertTrue("Test user password did not match after externalUserDetails instantiation", TEST_PASSWORD.equals(externalUserDetails.getPassword()));
		assertTrue("Test user is not enabled after externalUserDetails instantiation", externalUserDetails.isEnabled());
		assertTrue("Test user is expired after externalUserDetails instantiation", externalUserDetails.isAccountNonExpired());
		assertTrue("Test user credentials are expired after externalUserDetails instantiation", externalUserDetails.isCredentialsNonExpired());
		assertTrue("Test user account is locked after externalUserDetails instantiation", externalUserDetails.isAccountNonLocked());
		assertNotNull("externalUserDetails has a null additionalDetailMap after instantiation", externalUserDetails.getAdditionalDetailsMap());

		assertNotNull("externalUserDetails GrantedAuthorities array should not be null after externalUserDetails instantiation", externalUserDetails.getAuthorities());
		assertTrue("externalUserDetails GrantedAuthority array should condain only 1 role  after externalUserDetails instantiation: " + TEST_ROLE, externalUserDetails.getAuthorities().size() == 1 && TEST_ROLE.equalsIgnoreCase(externalUserDetails.getAuthorities().iterator().next().getAuthority()));

	}
}
