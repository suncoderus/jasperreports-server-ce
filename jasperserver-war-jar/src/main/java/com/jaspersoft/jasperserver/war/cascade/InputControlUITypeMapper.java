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
package com.jaspersoft.jasperserver.war.cascade;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents Input Controls and JR Parameter data
 * needed for rendering controls and maintaining state on UI.
 */
public class InputControlUITypeMapper {

    private static Map<Byte, String> backEndToUI;

    static {
        backEndToUI = new HashMap<Byte, String>();
        backEndToUI.put(InputControl.TYPE_BOOLEAN, "bool");
        backEndToUI.put(InputControl.TYPE_SINGLE_VALUE, "singleValue");
        backEndToUI.put(InputControl.TYPE_SINGLE_SELECT_QUERY, "singleSelect");
        backEndToUI.put(InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES, "singleSelect");
        backEndToUI.put(InputControl.TYPE_SINGLE_SELECT_QUERY_RADIO, "singleSelectRadio");
        backEndToUI.put(InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO, "singleSelectRadio");
        backEndToUI.put(InputControl.TYPE_MULTI_SELECT_QUERY, "multiSelect");
        backEndToUI.put(InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES, "multiSelect");
        backEndToUI.put(InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX, "multiSelectCheckbox");
        backEndToUI.put(InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX, "multiSelectCheckbox");
    }

    public static String getUiType(Byte backEndType) {
        return backEndToUI.get(backEndType);
    }
}
