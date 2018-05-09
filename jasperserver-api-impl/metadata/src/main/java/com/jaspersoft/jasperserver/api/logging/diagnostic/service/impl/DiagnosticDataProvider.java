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
import com.jaspersoft.jasperserver.api.logging.diagnostic.jmx.DiagnosticDynamicMBean;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import java.util.*;

/**
 * @author vsabadosh
 */
public class DiagnosticDataProvider {

    Map<String, Diagnostic> diagnosticDataMap;
    private static final Log log = LogFactory.getLog(DiagnosticDataProvider.class);
    private static MessageSource messageSource;

    public void setDiagnosticDataMap(Map<String, Diagnostic> diagnosticDataMap) {
        this.diagnosticDataMap = diagnosticDataMap;
    }

    public List<List<Object>> getDiagnosticAttributesList() {
        log.debug("STARTING COLLECTION OF DIAGNOSTIC ATTRIBUTES");
        Long globalStartTime = System.currentTimeMillis();
        LinkedList<Object> diagnosticAttributeList = null;
        LinkedList<List<Object>> diagnosticAttributesList = new LinkedList<List<Object>>();
        for (String diagnosticSection : diagnosticDataMap.keySet()) {
            Map<DiagnosticAttribute, DiagnosticCallback> diagnosticData = diagnosticDataMap.get(diagnosticSection).getDiagnosticData();
            List<DiagnosticAttribute> sortedDiagnosticAttributes = new ArrayList<DiagnosticAttribute>(diagnosticData.keySet());
            Collections.sort(sortedDiagnosticAttributes, new DiagnosticDynamicMBean.DiagnosticComparator());
            for (DiagnosticAttribute diagnosticAttribute : sortedDiagnosticAttributes) {
                diagnosticAttributeList = new LinkedList<Object>();
                //Section name
                diagnosticAttributeList.add(parseSection(diagnosticSection));
                //Attribute name
                diagnosticAttributeList.add(diagnosticAttribute.getAttributeName());
                Long startTime = System.currentTimeMillis();
                Object diagnosticAttributeValue = diagnosticData.get(diagnosticAttribute).getDiagnosticAttributeValue();
                Long executionTime = System.currentTimeMillis() - startTime;
                log.debug("Extracting " + diagnosticAttribute.getAttributeName() + "=" + executionTime);
                if (diagnosticAttributeValue != null) {
                    //Attribute value
                    diagnosticAttributeList.add(diagnosticAttributeValue);
                } else {
                    //Attribute value
                    diagnosticAttributeList.add("");
                }
                //Attribute description
                diagnosticAttributeList.add(messageSource.getMessage(DiagnosticAttributeBuilder.DIAGNOSTIC_ATTRIBUTE_MESSAGES_PREFIX +
                        diagnosticAttribute.getAttributeName(), new Object[]{}, Locale.getDefault()));

                diagnosticAttributesList.add(diagnosticAttributeList);
            }
        }
        log.debug("FINISHED COLLECTING DIAGNOSTIC ATTRIBUTES");
        Long globalExecutionTime = System.currentTimeMillis() - globalStartTime;
        log.debug("TOTAL TIME COLLECTING ATTRIBUTES = " + globalExecutionTime);

        return diagnosticAttributesList;
    }

    //

    /**
     * Removes leading text before the equals sign ("=")
     * in the Section name, e.g. changes
     * "jasperserver:name=Users" to just Users
     *
     * @param section  text which gets parsed
     * @return
     */
    private String parseSection(String section) {
        String[] separation = section.split("=");
        if (separation.length == 2) {
            return separation[1];
        } else {
            return section;
        }
    }

    public static void setMessageSource(MessageSource messageSource) {
        DiagnosticDataProvider.messageSource = messageSource;
    }

}
