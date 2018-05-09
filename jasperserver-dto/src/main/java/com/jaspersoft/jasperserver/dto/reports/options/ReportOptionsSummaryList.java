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
package com.jaspersoft.jasperserver.dto.reports.options;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement
public class ReportOptionsSummaryList {

    private List<ReportOptionsSummary> summaryList;

    public ReportOptionsSummaryList(){}

    public ReportOptionsSummaryList(List<ReportOptionsSummary> summaryList){
        this.summaryList = summaryList;
    }

    public ReportOptionsSummaryList(ReportOptionsSummaryList other) {
        List<ReportOptionsSummary> otherSummaryList = other.getSummaryList();
        this.summaryList = new ArrayList<ReportOptionsSummary>(otherSummaryList.size());
        for (ReportOptionsSummary reportOptionsSummary : otherSummaryList) {
            this.summaryList.add(new ReportOptionsSummary(reportOptionsSummary));
        }
    }

    @XmlElement(name = "reportOptionsSummary")
    public List<ReportOptionsSummary> getSummaryList() {
        return summaryList;
    }

    public void setSummaryList(List<ReportOptionsSummary> summaryList) {
        this.summaryList = summaryList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportOptionsSummaryList)) return false;

        ReportOptionsSummaryList that = (ReportOptionsSummaryList) o;

        if (summaryList != null ? !summaryList.equals(that.summaryList) : that.summaryList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return summaryList != null ? summaryList.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ReportOptionsSummaryList{" +
                "summaryList=" + summaryList +
                '}';
    }
}
