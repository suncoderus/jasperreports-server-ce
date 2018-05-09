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

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.PartialMock;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityServiceImpl}
 *
 * @author vsabadosh
 */
public class UserAuthorityServiceImplTest extends UnitilsJUnit4 {

    @TestedObject
    private PartialMock<UserAuthorityServiceImpl> userAuthorityService;

    @Test
    public void getDiagnosticDataTest() {
        int total_users_count = 5;
        int total_enabled_users_count = 6;
        int total_roles_count = 7;

        userAuthorityService.returns(total_users_count).getUsersCountExceptExcluded(null, null, false);
        userAuthorityService.returns(total_enabled_users_count).getUsersCountExceptExcluded(null, null, true);
        userAuthorityService.returns(total_roles_count).getTenantRolesCount(null, null, null);
        userAuthorityService.returns(total_roles_count).getTotalRolesCount(null);

        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = userAuthorityService.getMock().getDiagnosticData();

        //Test total size of diagnostic attributes collected from UserAuthorityServiceImpl
        assertEquals(3, resultDiagnosticData.size());

        assertEquals(total_users_count, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.TOTAL_USERS_COUNT, null, null)).getDiagnosticAttributeValue());

        assertEquals(total_enabled_users_count, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.TOTAL_ENABLED_USERS_COUNT, null, null)).getDiagnosticAttributeValue());

        assertEquals(total_roles_count, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.TOTAL_ROLES_COUNT, null, null)).getDiagnosticAttributeValue());
    }

}
