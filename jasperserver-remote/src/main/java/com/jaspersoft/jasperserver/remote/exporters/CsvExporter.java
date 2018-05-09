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

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.CsvExportParametersBean;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author Giulio Toffoli (original sanda zaharia (shertage@users.sourceforge.net))
 * @version $Id$
 */
@Service("remoteCsvExporter")
@Scope("prototype")
public class CsvExporter extends AbstractExporter {
    @Resource(name = "csvExportParameters")
    private CsvExportParametersBean exportParams;

    public CsvExporter() {
    	super(CsvExportParametersBean.PROPERTY_CSV_PAGINATED);
    }
    
    /**
     * @return Returns the exportParams.
     */
    public CsvExportParametersBean getExportParams() {
        return exportParams;
    }

    /**
     * @param exportParams The exportParams to set.
     */
    public void setExportParams(CsvExportParametersBean exportParams) {
        this.exportParams = exportParams;
    }

    @Override
    public JRExporter createExporter() throws Exception {
        return new JRCsvExporter(getJasperReportsContext());
    }

    @Override
    public void configureExporter(JRExporter exporter, HashMap exportParameters) throws Exception {
        if (exportParams != null && exportParams.getFieldDelimiter() != null) {
            exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, exportParams.getFieldDelimiter());
        }
    }

    @Override
    public String getContentType() {
        return "application/vnd.ms-excel";
    }
}
