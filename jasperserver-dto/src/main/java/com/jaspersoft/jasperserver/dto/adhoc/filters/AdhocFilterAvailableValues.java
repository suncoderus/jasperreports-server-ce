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
package com.jaspersoft.jasperserver.dto.adhoc.filters;

import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;

import java.util.List;


/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AdhocFilterAvailableValues {
    private Integer total;
    private List<InputControlOption> data;

    public AdhocFilterAvailableValues() {
    }

    public AdhocFilterAvailableValues(Integer total, List<InputControlOption> data) {
        this.total = total;
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<InputControlOption> getData() {
        return data;
    }

    public void setData(List<InputControlOption> data) {
        this.data = data;
    }
}
