/**
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author: Olesya Bobruyko
 * @version:
 */


define(function(require) {

    var $ = require("jquery"),
        assetsToIncludeExtendedVersionTemplate = require("text!tenantImportExport/export/template/assetsToIncludeExtendedVersionTemplate.htm"),
        eventsToIncludeTemplate = require("text!tenantImportExport/export/template/eventsToIncludeTemplate.htm"),
        exportDataFileTemplate = require("text!tenantImportExport/export/template/exportDataFileTemplate.htm"),
        rolesAndUsersToIncludeTemplate = require("text!tenantImportExport/export/template/rolesAndUsersToIncludeTemplate.htm"),
        exportOptionsContainerTemplate = require("text!tenantImportExport/export/template/exportOptionsContainerTemplate.htm"),
        repositoryExportTemplate = require("text!tenantImportExport/export/template/repositoryExportTemplate.htm"),
        exportTypesEnum = require("tenantImportExport/export/enum/exportTypesEnum");

    var exportTemplateByType = {};

    exportTemplateByType[exportTypesEnum.ROOT_TENANT] = [rolesAndUsersToIncludeTemplate, assetsToIncludeExtendedVersionTemplate, eventsToIncludeTemplate].join("");
    exportTemplateByType[exportTypesEnum.TENANT] = [rolesAndUsersToIncludeTemplate, assetsToIncludeExtendedVersionTemplate].join("");
    exportTemplateByType[exportTypesEnum.SERVER_PRO] = [rolesAndUsersToIncludeTemplate, assetsToIncludeExtendedVersionTemplate, eventsToIncludeTemplate].join("");
    exportTemplateByType[exportTypesEnum.SERVER_CE] = [rolesAndUsersToIncludeTemplate, assetsToIncludeExtendedVersionTemplate, eventsToIncludeTemplate].join("");
    exportTemplateByType[exportTypesEnum.REPOSITORY] = [repositoryExportTemplate].join("");


    return function(options) {
        options = options || {};

        return exportDataFileTemplate + $(exportOptionsContainerTemplate).append(exportTemplateByType[options.type])[0].outerHTML;
    }
});
