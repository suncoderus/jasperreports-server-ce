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
package com.jaspersoft.jasperserver.remote.reports;

import com.jaspersoft.jasperserver.dto.reports.ReportParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ReportParametersHelper {

    public static Map<String, String[]> toRawParameters(List<ReportParameter> reportParameters){
        Map<String, String[]> rawParameters = null;
        if(reportParameters != null){
            rawParameters = new HashMap<String, String[]>();
            for(ReportParameter currentParameter : reportParameters){
                rawParameters.put(currentParameter.getName(), currentParameter.getValues().toArray(new String[currentParameter.getValues().size()]));
            }
        }
        return rawParameters;
    }

    public static List<ReportParameter> fromRawParameters(Map<String, String[]> reportParams) {
        List<ReportParameter> reportParameterList = null;
        if (reportParams != null && reportParams.size() > 0) {
            reportParameterList = new ArrayList<ReportParameter>();
            for (String key : reportParams.keySet()) {
                ReportParameter reportParameter = new ReportParameter();
                reportParameter.setName(key);
                if (reportParams.get(key) != null) {
                    reportParameter.setValues(Arrays.asList(reportParams.get(key)));
                }
                reportParameterList.add(reportParameter);
            }
        }
        return reportParameterList;
    }
}
