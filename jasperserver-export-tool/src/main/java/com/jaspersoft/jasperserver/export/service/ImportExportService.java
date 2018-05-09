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

package com.jaspersoft.jasperserver.export.service;

import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This is a facade on top of import/export tool
 *
 * @author ztomchenco
 */
public interface ImportExportService {
    String ERROR_CODE_IMPORT_TENANTS_NOT_MATCH = "import.organizations.not.match";
    String ERROR_CODE_IMPORT_BROKEN_DEPENDENCIES = "import.broken.dependencies";
    String ERROR_CODE_IMPORT_ORGANIZATION_INTO_ROOT = "import.organization.into.root.not.allowed";
    String ERROR_CODE_IMPORT_ROOT_INTO_ORGANIZATION = "import.root.into.organization.not.allowed";
    String ERROR_CODE_UPDATE_NOT_PENDING_PHASE = "update.not.pending.phase";
    String ERROR_CODE_RESTART_ALIVE_TASK = "restart.alive.task";

    String ROOT_TENANT_ID = "rootTenantId"; //Tenant id we import into
    String MERGE_ORGANIZATION = "merge-organization";
    String BROKEN_DEPENDENCIES = "broken-dependencies";
    String SKIP_THEMES = "skip-themes";
    String DEFAULT_THEME_NAME = "default";

    /**
     * Entry point to import process
     *
     * @param input - stream, which contains zipped file with data to import
     * @param importParams - parameters for import
     * @param organizationId - tenant Id in which data is importing
     * @param brokenDependenciesStrategy - Broken Dependencies Strategy
     * @param locale - locale for messages while importing
     * @throws ImportFailedException - if input data is corrupted or error occurs during import
     */
    public void doImport(InputStream input, Map<String, Boolean> importParams, String organizationId,
                         String brokenDependenciesStrategy, Locale locale, List<WarningDescriptor> warnings)
                         throws ImportFailedException;

    /**
     * Entry point to import process
     *
     * @param input - pointer to file
     * @param importParams - parameters for import
     * @param organizationId - tenant Id in which data is importing
     * @param brokenDependenciesStrategy - Broken Dependencies Strategy
     * @param locale - locale for messages while importing
     * @param warnings - list of import warnings
     * @throws ImportFailedException - if input data is corrupted or error occurs during import
     */
    public void doImport(File input, Map<String, Boolean> importParams, String organizationId,
                         String brokenDependenciesStrategy, Locale locale, List<WarningDescriptor> warnings) throws ImportFailedException;

    /**
     * Entry point to the export process
     *
     * @param output - stream to output zip file with exports
     * @param exportParams - parameters for export process
     * @param urisOfResources - list of resources URI to be exported, or null if this option not used. Can't be empty
     * @param urisOfScheduledJobs - list of scheduled jobs URI to be exported, or null if this option not used. Can't be empty
     * @param rolesToExport - list of roles to be exported, or null if this option not used. If list is empty, all roles will be exported
     * @param usersToExport - list of users to be exported, or null if this option not used. If list is empty, all users will be exported
     * @param exportResourceTypes - list of resources types to be exported. if list is empty all resources will exported.
     * @param organizationId - export tenant id
     * @param locale - locale for messages while exporting
     * @param warnings - set of resources which have  AccessDeniedException or any parent resource which are not visible   @throws ExportFailedException - if something goes wrong during export process
     */
    public void doExport(OutputStream output, Map<String, Boolean> exportParams, List<String> urisOfResources,
                         List<String> urisOfScheduledJobs, List<String> rolesToExport, List<String> usersToExport,
                         List<String> exportResourceTypes, String organizationId, Locale locale, List<WarningDescriptor> warnings)
                         throws ExportFailedException;
}
