/*
* Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
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
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/

package com.jaspersoft.jasperserver.api.metadata.tenant.service.impl;

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import com.jaspersoft.jasperserver.api.metadata.tenant.service.impl.TenantServiceImpl;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.PartialMock;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link com.jaspersoft.jasperserver.api.metadata.tenant.service.impl.TenantServiceImpl}
 *
 * @author vsabadosh
 */
public class TenantServiceImplTest extends UnitilsJUnit4 {

    @TestedObject
    private PartialMock<TenantServiceImpl> tenantService;

    @Test
    public void getDiagnosticDataTest() {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = tenantService.getMock().getDiagnosticData();

        //Test total size of diagnostic attributes collected from TenantServiceImpl
        assertEquals(1, resultDiagnosticData.size());

        //Test getting total organization count when parent folder is null;
        tenantService.returns(5).getSubTenantsCount(null, "organizations", null);
        tenantService.returns(5).getTotalTenantsCount(null);

        assertEquals(5, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.TOTAL_ORGANIZATIONS_COUNT, null, null)).getDiagnosticAttributeValue());
    }

}
