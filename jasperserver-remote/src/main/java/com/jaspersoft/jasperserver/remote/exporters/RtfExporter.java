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

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.RtfExportParametersBean;

/**
 * @author Giulio Toffoli (original sanda zaharia (shertage@users.sourceforge.net))
 * @version $Id$
 */
@Service("remoteRtfExporter")
@Scope("prototype")
public class RtfExporter extends AbstractExporter {

	public RtfExporter() {
		super(RtfExportParametersBean.PROPERTY_RTF_PAGINATED);
	}

    @Override
    public JRExporter createExporter() throws Exception {
        return new JRRtfExporter(getJasperReportsContext());
    }

    @Override
    public String getContentType() {
        return "application/rtf";
    }
}
