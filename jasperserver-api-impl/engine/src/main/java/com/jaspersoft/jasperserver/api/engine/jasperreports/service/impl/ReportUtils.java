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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlsInformation;

/**
 * @author Pavel Lysak
 */
public class ReportUtils {
    /**
     * Merge IC Info from two sources. Second has priority (overwrites the first)
     *
     * @param first
     * @param second
     * @return
     */
    public static ReportInputControlsInformation mergeInputControlsInfo(ReportInputControlsInformation first, ReportInputControlsInformation second) {
        if(first == null) {
            return second;
        }
        if(second == null) {
            return first;
        }
        ReportInputControlsInformationImpl res = new ReportInputControlsInformationImpl();
        for(String name: first.getControlNames()) {
            res.setInputControlInformation(name, first.getInputControlInformation(name));
        }
        for(String name: second.getControlNames()) {
            res.setInputControlInformation(name, second.getInputControlInformation(name));
        }
        return res;
    }

}
