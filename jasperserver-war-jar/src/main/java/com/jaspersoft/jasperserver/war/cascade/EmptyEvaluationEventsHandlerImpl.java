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

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;

import java.util.List;
import java.util.Map;

/**
 * User: chaim
 * Date: 4/4/12
 * Time: 2:09 PM
 *  This is a dummy class to handle input control evaluation.
 */
public class EmptyEvaluationEventsHandlerImpl implements EvaluationEventsHandler {
    @Override
    public void beforeEvaluation(String uri, Map<String, String[]> parameters, User user) {

    }

    @Override
    public void afterEvaluation(String uri, Map<String, String[]> parameters, List<InputControlState> inputControlStates, User user) {

    }
}
