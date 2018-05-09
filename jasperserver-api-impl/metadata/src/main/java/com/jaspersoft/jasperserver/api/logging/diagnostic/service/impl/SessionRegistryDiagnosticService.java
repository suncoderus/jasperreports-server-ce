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

package com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl;

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.springframework.security.core.session.SessionRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Session registry wrapper.
 * @author vsabadosh
 */
public class SessionRegistryDiagnosticService implements Diagnostic {

    private SessionRegistry sessionRegistry;

    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        return new DiagnosticAttributeBuilder()
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.TOTAL_LOGGED_IN_USERS, new DiagnosticCallback<Integer>() {
                @Override
                public Integer getDiagnosticAttributeValue() {
                    return sessionRegistry.getAllPrincipals().size();
                }
            })
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.TOTAL_SESSIONS_BY_USER, new DiagnosticCallback<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> getDiagnosticAttributeValue() {
                    Map<String, Integer> sessionsCountForUser = new HashMap<String, Integer>();
                    for (Object principle : sessionRegistry.getAllPrincipals()) {
                        sessionsCountForUser.put(principle.toString(), sessionRegistry.getAllSessions(principle, false).size());
                    }
                    return sessionsCountForUser;
                }
            }).build();
    }

    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

}
