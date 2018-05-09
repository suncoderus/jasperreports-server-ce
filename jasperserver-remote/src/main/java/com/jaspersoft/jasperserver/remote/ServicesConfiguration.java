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


package com.jaspersoft.jasperserver.remote;

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;

/**
 * For now this class is just a wrapper of the services configuration.
 * It is good enough for us.
 *
 * @author gtoffoli
 */
public interface ServicesConfiguration {

    /**
     * Returns an export parameters object for a specific output format.
     *
     * @param outputFormat the output format requested by the service.
     * @return an export parameters object for the output format, or <code>null</code>
     * if no export parameters object is configured for the format
     */
     ExportParameters getExportParameters(String outputFormat);

    /**
     * Returns uri of temp folder.
     *
     * @return uri of temp folder.
     */
    String getTempFolder();

    /**
     * Returns role name for which temp folder should be visible.
     *
     * @return role name for which temp folder should be visible.
     */
    String getRoleToAccessTempFolder();

    /**
     * Returns an exporter object for a specific output format.
     *
     * @param outputFormat the output format requested by the service.
     * @return an exporter object for the output format, or <code>null</code>
     * if no export parameters object is configured for the format
     */
    ReportExporter getExporter(String outputFormat);


    
}
