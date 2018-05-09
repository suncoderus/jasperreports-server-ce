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

package com.jaspersoft.jasperserver.api.security.externalAuth.processors;

import com.jaspersoft.jasperserver.api.common.service.impl.ObjectFactoryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceFactoryImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chaim Arbiv
 * @version $id$
 * Unit tests for ExternalUserProcessor
 */
public class ExternalUserSetupProcessorTest  extends UnitilsJUnit4 {
	private static final Logger logger = LogManager.getLogger(ExternalUserSetupProcessorTest.class);

    Mock<UserAuthorityService> userAuthorityServiceMock;
    Mock<RepositoryService> repositoryServiceMock;
    Mock<TenantService> tenantServiceMock;
    Mock<ProfileAttributeService> profileAttributeServiceMock;
    Mock<ObjectPermissionService> objectPermissionServiceMock;


    @Test
    public void testAlignInternalUser() throws Exception {
		final String organization_1 = "organization_1";

//        Scenario 1 - user has no internal roles and there are no roles in the organizationRoleMap
		Set<Role> remoteExternalUserRoles = new HashSet<Role>();

		// roles from LDAP
		Role externalRole = new RoleImpl();
		externalRole.setRoleName("ROLE_EXTERNAL");
		externalRole.setExternallyDefined(true);
		externalRole.setTenantId(organization_1);
        remoteExternalUserRoles.add(externalRole);

        // user
        User user = new UserImpl();

        // preparing objects and mockes
        ExternalUserSetupProcessor userProcessor = new ExternalUserSetupProcessor();
        userAuthorityServiceMock.oncePerforms(null).putRole(null, externalRole);
        userProcessor.setUserAuthorityService(userAuthorityServiceMock.getMock());

        // test
        userProcessor.alignInternalAndExternalUser(remoteExternalUserRoles, user);
        Assert.assertTrue("user should have one externally defined role",
                                user.getRoles().size()==1 &&((Role)user.getRoles().toArray()[0]).isExternallyDefined());


        //        Scenario 2 - user has internal roles which is the value of one of the rootRoleMap
        // The internal role should be removed from the user
        
        user = new UserImpl();
        Role adminRole = new RoleImpl();
        adminRole.setRoleName("ROLE_ADMINISTRATOR");
        user.addRole(adminRole);

		// populating OrganizationRoleMap
		Map<String, String> organizationRoleMap = new HashMap<String, String>();
		organizationRoleMap.put("ROLE_EXTERNAL", "ROLE_ADMINISTRATOR");
        organizationRoleMap.put("ROLE_SCENARIO_3", "ROLE_ADMINISTRATOR");
		userProcessor.setOrganizationRoleMap(organizationRoleMap);

        // test
        userProcessor.alignInternalAndExternalUser(new HashSet<Role>(), user);
        Assert.assertTrue("user should have the internally defined role removed.", user.getRoles() != null && user.getRoles().size() == 0);
    }

	@Test
	public void testConvertGrantedAuthoritiesToRoles() {
		try {
			ExternalUserSetupProcessor userProcessor = new ExternalUserSetupProcessor();
			userProcessor.setRepositoryService(repositoryServiceMock.getMock());

			final UserAuthorityServiceImpl userAuthorityService = new UserAuthorityServiceImpl();
			final ResourceFactoryImpl resourceFactory = new ResourceFactoryImpl();
			resourceFactory.setObjectFactory(new ObjectFactoryImpl());
			Map<String, String> mapping = new HashMap<String, String>();
			mapping.put("com.jaspersoft.jasperserver.api.metadata.user.domain.Role",
					"com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl");
			resourceFactory.setImplementationClassMappings(mapping);
			userAuthorityService.setObjectMappingFactory(resourceFactory);
			userProcessor.setUserAuthorityService(userAuthorityService);

			userProcessor.setTenantService(tenantServiceMock.getMock());
			userProcessor.setProfileAttributeService(profileAttributeServiceMock.getMock());
			userProcessor.setObjectPermissionService(objectPermissionServiceMock.getMock());

			Map<String, String> organizationRoleMap = new HashMap<String, String>();
			organizationRoleMap.put("ROLE_EXTERNAL", "ROLE$ADMINISTRATOR$");
			organizationRoleMap.put("ROLE$EXT", "ROLE$ADMINISTRATOR$|*");    //tenant id is set on the role
			userProcessor.setOrganizationRoleMap(organizationRoleMap);
			userProcessor.setPermittedExternalRoleNameRegex("[A-Za-z_]+");
			userProcessor.afterPropertiesSet();

			Map<String, String> roleMap = userProcessor.getOrganizationRoleMap();
			Assert.assertTrue("Internal role should be cleaned of $ (invalid according to "
					+ userProcessor.getPermittedExternalRoleNameRegex() + ")",
					"ROLE_ADMINISTRATOR".equals(roleMap.get("ROLE_EXTERNAL")));

			//Case 1: ROLE$EXT is mapped to internal ROLE_ADMINISTRATOR
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>() {{add(new SimpleGrantedAuthority("ROLE$EXT"));}};
			Set<Role> roleSet = userProcessor.convertGrantedAuthoritiesToRoles(authorities, "tenant");
			Assert.assertTrue("Case 1: roleSet must have 1 elem.", roleSet != null && roleSet.size() == 1);
			Role role = roleSet.iterator().next();
			Assert.assertTrue("Case 1: role name must be ROLE_ADMINISTRATOR.", "ROLE_ADMINISTRATOR".equals(role.getRoleName()));
			Assert.assertTrue("Case 1: role must be mapped to internal ROLE_ADMINISTRATOR.", !role.isExternallyDefined());
			Assert.assertTrue("Case 1: role must have tenantId 'tenant'.", "tenant".equalsIgnoreCase(role.getTenantId()));

			//Case 2: ROLE$EXTERNAL-NOT MAPPED+ is converted to external ROLE_EXTERNAL_NOT_MAPPED.
			// Illegal (not matching permittedExternalRoleNameRegex) char sequences are substituted by _
			authorities = new ArrayList<GrantedAuthority>() {{add(new SimpleGrantedAuthority("ROLE$EXTERNAL-NOT MAPPED+"));}};
			roleSet = userProcessor.convertGrantedAuthoritiesToRoles(authorities, "tenant");
			Assert.assertTrue("Case 2: roleSet must have 1 elem.", roleSet != null && roleSet.size() == 1);
			role = roleSet.iterator().next();
			Assert.assertTrue("Case 2: role name must be ROLE_EXTERNAL_NOT_MAPPED.", "ROLE_EXTERNAL_NOT_MAPPED".equals(role.getRoleName()));
			Assert.assertTrue("Case 2: role must be external.", role.isExternallyDefined());
			Assert.assertTrue("Case 2: role must have tenantId 'tenant'.", "tenant".equalsIgnoreCase(role.getTenantId()));


			//Case 3: ROLE_EXTERNAL is mapped to internal ROLE_ADMINISTRATOR
			authorities = new ArrayList<GrantedAuthority>() {{add(new SimpleGrantedAuthority("ROLE_EXTERNAL"));}};
			roleSet = userProcessor.convertGrantedAuthoritiesToRoles(authorities, "tenant");
			Assert.assertTrue("Case 3: roleSet must have 1 elem.", roleSet != null && roleSet.size() == 1);
			role = roleSet.iterator().next();
			Assert.assertTrue("Case 3: role name must be ROLE_ADMINISTRATOR.", "ROLE_ADMINISTRATOR".equals(role.getRoleName()));
			Assert.assertTrue("Case 3: role must be mapped to internal ROLE_ADMINISTRATOR.", !role.isExternallyDefined());
			Assert.assertNull("Case 3: role must have tenantId null.", role.getTenantId());
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
}
