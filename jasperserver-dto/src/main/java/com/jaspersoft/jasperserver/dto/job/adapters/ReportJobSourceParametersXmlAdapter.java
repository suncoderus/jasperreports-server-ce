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

import com.jaspersoft.jasperserver.dto.job.wrappers.ClientReportParametersMapWrapper;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ReportJobSourceParametersXmlAdapter extends XmlAdapter<ClientReportParametersMapWrapper, Map<String, String[]>> {

    @Override
    public ClientReportParametersMapWrapper marshal(Map<String, String[]> v) throws Exception {
        HashMap<String, String[]> result = null;
        if (v != null && !v.isEmpty()) {
            result = new HashMap<String, String[]>();
            for (String currentKey : v.keySet()) {
                String[] currentValue = v.get(currentKey);
                result.put(currentKey, currentValue);
            }
        }
        return result != null ? new ClientReportParametersMapWrapper(result) : null;
    }

    @Override
    public Map<String, String[]> unmarshal(ClientReportParametersMapWrapper v) throws Exception {
        Map<String, String[]> result = null;
        if (v != null) {
            final HashMap<String, String[]> parameterValues = v.getParameterValues();
            if (parameterValues != null && !parameterValues.isEmpty()) {
                result = new HashMap<String, String[]>();
                for (String currentKey : parameterValues.keySet()) {
                    String[] currentValue = parameterValues.get(currentKey);
                        result.put(currentKey, currentValue);
                }
            }
        }
        return result;
    }
}
