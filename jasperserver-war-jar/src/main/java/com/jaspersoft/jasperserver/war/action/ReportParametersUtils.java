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
package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlOption;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;

import java.util.*;

public class ReportParametersUtils {

    public static Map<String, String[]> getValueMapFromInputControlStates(List<InputControlState> states) {
        Map<String, String[]> valueMap = new HashMap<String, String[]>(states.size());
        for (InputControlState state : states) {
            if (state != null)
                valueMap.put(state.getId(), getValueFromInputControlState(state));
        }

        return valueMap;
    }

    public static Map<String, String[]> getValueMapFromInputControls(List<ReportInputControl> inputControls) {
        LinkedHashMap<String, String[]> valueMap = new LinkedHashMap<String, String[]>(inputControls.size());
        for (ReportInputControl ic : inputControls) {
            InputControlState state = ic.getState();
            if (state != null) {
                valueMap.put(state.getId(), getValueFromInputControlState(state));
            }
        }
        return valueMap;
    }

    protected static String[] getValueFromInputControlState(InputControlState state) {
        if (state.getValue() != null) {
            return new String[]{state.getValue()};
        } else if (state.getOptions() != null) {
            List<String> values = new ArrayList<String>(state.getOptions().size());
            for (InputControlOption option : state.getOptions()) {
                if (option.isSelected()) {
                    values.add(option.getValue());
                }
            }
            return values.toArray(new String[values.size()]);
        } else {
            return new String[0];
        }
    }
}
