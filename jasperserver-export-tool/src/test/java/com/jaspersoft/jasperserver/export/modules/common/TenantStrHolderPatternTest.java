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

package com.jaspersoft.jasperserver.export.modules.common;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Vlad Zavadskii
 * @author askorodumov
 * @version $Id$
 */
public class TenantStrHolderPatternTest {
    @Test
    public void replaceWithNewTenantIdsTest() {
        // URI Type: /organizations/tenantId_1/organizations/tenantId_2/.../organizations/tenantId_N/resource/path
        String originalUri = "/organizations/tenant1/organizations/tenant2/organizations/tenant3/resource/path";
        String result;
        Map<String, String> oldToNewIds = new HashMap<String, String>();
        oldToNewIds.put("tenant1", "tenant11");
        oldToNewIds.put("tenant3", "tenant33");

        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_FOLDER_URI);
        assertEquals("/organizations/tenant11/organizations/tenant2/organizations/tenant33/resource/path", result);

        // URI Type: str/organizations/tenantId_1/organizations/tenantId_2/.../organizations/tenantId_N/resource/path
        originalUri = "repo/organizations/tenant1/organizations/tenant2/organizations/tenant3/resource/path";
        oldToNewIds.put("tenant1", "tenant11");
        oldToNewIds.put("tenant2", "tenant22");
        oldToNewIds.put("tenant3", "tenant33");

        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_FOLDER_URI);
        assertEquals("repo/organizations/tenant1/organizations/tenant2/organizations/tenant3/resource/path", result);

        originalUri = "/organizations/tenant1/organizations/tenant2/notAnOrgsDelimiter/organizations/tenant3/resource/path";
        oldToNewIds.clear();
        oldToNewIds.put("tenant2", "tenant22");
        oldToNewIds.put("tenant3", "tenant33");

        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_FOLDER_URI);
        assertEquals("/organizations/tenant1/organizations/tenant22/notAnOrgsDelimiter/organizations/tenant3/resource/path", result);

        originalUri = "/organizations/tenant1/organizations/";
        oldToNewIds.clear();
        oldToNewIds.put("tenant1", "tenant2");

        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_FOLDER_URI);
        assertEquals("/organizations/tenant2/organizations/", result);

        // URI Type: /tenantId_1/tenantId_2/.../tenantId_N
        originalUri = "/tenant1/tenant222/tenant3";
        oldToNewIds.clear();
        oldToNewIds.put("tenant2", "tenant22");
        oldToNewIds.put("tenant3", "tenant33");

        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_URI);
        assertEquals("/tenant1/tenant222/tenant33", result);


        originalUri = "/org/org1/org2";
        oldToNewIds.clear();
        oldToNewIds.put("organization_1", "org3");
        oldToNewIds.put("org1", "org11");
        oldToNewIds.put("org2", "org21");
        oldToNewIds.put("org", "org4");
        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_URI);
        assertEquals("/org4/org11/org21", result);


        originalUri = "/organizations/org/organizations/org1/organizations/org2";
        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_FOLDER_URI);
        assertEquals("/organizations/org4/organizations/org11/organizations/org21", result);


        originalUri = "/tenant1/tenant2/tenant3/";
        oldToNewIds.clear();
        oldToNewIds.put("enan", "_____");

        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_URI);
        assertEquals("/tenant1/tenant2/tenant3/", result);

        // URI Type: tenantId
        originalUri = "justTenantId";
        oldToNewIds.clear();

        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_ID);
        assertEquals("justTenantId", result);

        originalUri = "justTenantId";
        oldToNewIds.clear();
        oldToNewIds.put("justTenantId", "anotherTenantId");

        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_ID);
        assertEquals("anotherTenantId", result);

        // URI Type: recipientId|tenantId
        originalUri = "joeuser|organization_1";
        oldToNewIds.clear();
        oldToNewIds.put("organization_1", "organization_2");

        result = TenantStrHolderPattern.replaceWithNewTenantIds(oldToNewIds, originalUri,
                TenantStrHolderPattern.TENANT_QUALIFIED_NAME);
        assertEquals("joeuser|organization_2", result);
    }
}
