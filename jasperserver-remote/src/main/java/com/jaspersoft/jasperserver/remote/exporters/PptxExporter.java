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
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.export.PptxReportConfiguration;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.PptxExportParametersBean;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id$
 */
@Service("remotePptxExporter")
@Scope("prototype")
public class PptxExporter extends AbstractExporter {

    @Resource(name = "pptxExportParameters")
    private PptxExportParametersBean exportParams;

    public PptxExporter() {
    	super(PptxExportParametersBean.PROPERTY_PPTX_PAGINATED);
	}
    
    @Override
    public JRExporter createExporter() throws Exception {
    	SimpleJasperReportsContext localContext = new SimpleJasperReportsContext(getJasperReportsContext());
        return new JRPptxExporter(localContext);
    }

    @Override
    public void configureExporter(JRExporter exporter, HashMap exportParameters) throws Exception {
		if(exportParams != null)
		{
			if(exportParams.getIgnoreHyperlink() != null) {
				//this local context trick should be removed when new JR exporter API is adopted in JRS
				SimpleJasperReportsContext localContext = (SimpleJasperReportsContext)((JRPptxExporter)exporter).getJasperReportsContext();
				localContext.setProperty(PptxReportConfiguration.PROPERTY_IGNORE_HYPERLINK, exportParams.getIgnoreHyperlink().toString());
			}
		}	
    }

    /**
     * @param exportParams The exportParams to set.
     */
    public void setExportParams(PptxExportParametersBean exportParams) {
        this.exportParams = exportParams;
    }

    @Override
    public String getContentType() {
        return "application/pptx";
    }
}
