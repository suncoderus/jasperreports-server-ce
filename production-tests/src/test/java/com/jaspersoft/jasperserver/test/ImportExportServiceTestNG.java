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

package com.jaspersoft.jasperserver.test;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.collect.Iterables.getFirst;
import static com.jaspersoft.jasperserver.export.modules.common.ExportImportWarningCode.IMPORT_RESOURCE_DIFFERENT_TYPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author askorodumov
 * @version $Id$
 */
public class ImportExportServiceTestNG extends BaseExportTestCaseTestNG {
    private ImportExportService importExportService;
    private RepositoryService repositoryService;

    @BeforeClass()
    public void onSetUp() throws Exception {
        super.onSetUp();

        importExportService = (ImportExportService) getBean("synchImportExportService");
        repositoryService = (RepositoryService) getBean("hibernateRepositoryService");
    }

    @AfterClass()
    public void onTearDown() throws Exception {
        super.onTearDown();
    }

    @Test
    public void doImport_folderWithSameUriAsExistingResource_expectedWarningMessage() throws Exception {
        File input = new File(TEST_BASE_DIR + FILE_SEPARATOR + "test-classes" + FILE_SEPARATOR + "samples-import" +
                FILE_SEPARATOR + "duplicate-path-folder.zip");
        assertTrue(input.exists() && input.isFile(), "Importing file does not exist: " + input.getPath());

        String existingResourceUri = "/images/JRLogo";
        assertNotNull(repositoryService.getResource(null, existingResourceUri), "Resource does not exist: " + existingResourceUri);

        Map<String, Boolean> importParams = new HashMap<String, Boolean>();
        importParams.put("update", true);
        importParams.put("include-server-settings", true);
        importParams.put("include-access-events", true);
        importParams.put("include-audit-events", false);
        importParams.put("merge-organization", false);
        importParams.put("skip-user-update", false);
        importParams.put("include-monitoring-events", false);
        importParams.put("skip-themes", true);

        String organizationId = "";
        String brokenDependenciesStrategy = "fail";
        Locale locale = LocaleContextHolder.getLocale();
        List<WarningDescriptor> warnings = new ArrayList<WarningDescriptor>();

        importExportService.doImport(input, importParams, organizationId, brokenDependenciesStrategy, locale, warnings);

        assertEquals(warnings.size(), 1, "The 'warnings' should contain a warning");
        WarningDescriptor warning = getFirst(warnings, null);
        assertNotNull(warning, "warning is null");
        assertEquals(warning.getCode(), IMPORT_RESOURCE_DIFFERENT_TYPE.toString(), "wrong warning code");
        assertNotNull(warning.getParameters(), "warning parameter is null");
        assertEquals(warning.getParameters().length, 1, "warning should contain only one parameter");
        assertEquals(warning.getParameters()[0], existingResourceUri);

        System.out.println("ImportExportServiceTestNG.doImport_folderWithSameUriAsExistingResource_expectedWarningMessage");
    }
}
