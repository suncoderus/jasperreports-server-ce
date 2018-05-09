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

/**
 * @author Vasyl Spachynskyi
 * @version $Id: $
 * @since 24.09.2015
 */
public enum ExportImportWarningCode {
    EXPORT_BROKEN_DEPENDENCY("export.broken.dependency"),
    IMPORT_RESOURCE_NOT_FOUND("import.resource.not.found"),
    IMPORT_RESOURCE_DIFFERENT_TYPE("import.resource.different.type.already.exists"),
    IMPORT_RESOURCE_ATTACHED_NOT_EXIST_ORG("import.resource.attached.not.exist.org"),
    IMPORT_RESOURCE_DATA_MISSING("import.resource.data.missing"),
    IMPORT_RESOURCE_URI_TOO_LONG("import.resource.uri.too.long"),
    IMPORT_REFERENCE_RESOURCE_NOT_FOUND("import.reference.resource.not.found"),
    IMPORT_REPORT_JOB_REFERENCE_RESOURCE_NOT_FOUND("import.report.job.reference.resource.not.found"),
    IMPORT_ACCESS_DENIED("import.access.denied"),
    IMPORT_FOLDER_ATTACHED_NOT_EXIST_ORG("import.folder.attached.not.exist.org"),
    IMPORT_MULTI_TENANCY_NOT_SUPPORTED("import.multi.tenancy.not.supported");

    private String warningCode;

    ExportImportWarningCode(String warningCode) {
        this.warningCode = warningCode;
    }

    @Override
    public String toString() {
        return warningCode;
    }
}
