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
package com.jaspersoft.jasperserver.dto.reports;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement
public class ReportParameters {
    private List<ReportParameter> reportParameters;

    public ReportParameters() { }

    public ReportParameters(List<ReportParameter> reportParameters) {
        this.reportParameters = reportParameters;
    }

    public ReportParameters(ReportParameters other) {
        final List<ReportParameter> reportParameterList = other.getReportParameters();
        if(reportParameterList != null){
            reportParameters = new ArrayList<ReportParameter>(other.getReportParameters().size());
            for(ReportParameter reportParameter : reportParameterList){
                reportParameters.add(new ReportParameter(reportParameter));
            }
        }
    }

    @XmlElement(name = "reportParameter")
    public List<ReportParameter> getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(List<ReportParameter> reportParameters) {
        this.reportParameters = reportParameters;
    }

    public Map<String, String[]> getRawParameters(){
        Map<String, String[]> rawParameters = new HashMap<String, String[]>();
        if(reportParameters != null){
            for(ReportParameter currentParameter : reportParameters){
                rawParameters.put(
                        currentParameter.getName(),
                        currentParameter.getValues().toArray(new String[currentParameter.getValues().size()])
                );
            }
        }
        return rawParameters;
    }

    @Override
    public String toString() {
        return "ReportParameters{" +
                "reportParameters=" + reportParameters +
                '}';
    }
}
