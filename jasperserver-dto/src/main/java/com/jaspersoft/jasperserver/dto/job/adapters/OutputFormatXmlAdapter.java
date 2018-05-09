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

package com.jaspersoft.jasperserver.dto.job.adapters;

import com.jaspersoft.jasperserver.dto.common.OutputFormat;
import com.jaspersoft.jasperserver.dto.job.wrappers.ClientReportJobOutputFormatsWrapper;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This adapter is used for ReportJob.outputFormats serialization.
 * ClientReportJobOutputFormatsWrapper is used for serialization because of no ability inFolder use @XmlElementWrapper together with @XmlJavaTypeAdapter.
 * See http://java.net/jira/browse/JAXB-787
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */

public class OutputFormatXmlAdapter extends XmlAdapter<ClientReportJobOutputFormatsWrapper, Set<OutputFormat>> {

    @Override
    public Set<OutputFormat> unmarshal(ClientReportJobOutputFormatsWrapper v) throws Exception {
        Set<OutputFormat> result = null;
        if (v != null && v.getFormats() != null && !v.getFormats().isEmpty()) {
            result = new HashSet<OutputFormat>();
            for (String currentValue : v.getFormats()) {
                OutputFormat outputFormat = OutputFormat.valueOf(currentValue);
                if (outputFormat != null)
                    result.add(outputFormat);
            }
        }
        return result;
    }

    @Override
    public ClientReportJobOutputFormatsWrapper marshal(Set<OutputFormat> v) throws Exception {
        ClientReportJobOutputFormatsWrapper result = null;
        if (v != null && !v.isEmpty()) {
            Set<String> set = new HashSet<String>();
            for (OutputFormat currentValue : v) {
                String currentStringValue = currentValue.name();
                if (currentStringValue != null)
                    set.add(currentStringValue);
            }
            if(!set.isEmpty()){
                result = new ClientReportJobOutputFormatsWrapper();
                result.setFormats(set);
            }
        }
        return result;
    }

}
