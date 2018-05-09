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
package com.jaspersoft.jasperserver.remote.exporters;

import java.util.HashMap;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JRExporter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.JsonMetadataExportParametersBean;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net))
 * @version $Id: CsvExporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service("remoteJsonMetadataExporter")
@Scope("prototype")
public class JsonMetadataExporter extends AbstractExporter {
    @Resource(name = "jsonMetadataExportParameters")
    private JsonMetadataExportParametersBean exportParams;

    /**
     * @return Returns the exportParams.
     */
    public JsonMetadataExportParametersBean getExportParams() {
        return exportParams;
    }

    /**
     * @param exportParams The exportParams to set.
     */
    public void setExportParams(JsonMetadataExportParametersBean exportParams) {
        this.exportParams = exportParams;
    }

    @Override
    public JRExporter createExporter() throws Exception {
        return new net.sf.jasperreports.engine.export.JsonMetadataExporter(getJasperReportsContext());
    }

    @Override
    public void configureExporter(JRExporter exporter, HashMap exportParameters) throws Exception {
    }

    @Override
    public String getContentType() {
        return "application/json";
    }
}
