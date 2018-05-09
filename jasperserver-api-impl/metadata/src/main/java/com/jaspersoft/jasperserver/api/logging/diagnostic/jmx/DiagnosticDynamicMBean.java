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
package com.jaspersoft.jasperserver.api.logging.diagnostic.jmx;

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.springframework.beans.factory.InitializingBean;

import javax.management.*;
import java.util.*;

/**
 * Implementation of the Dynamic MBean (MBean that exposes a dynamic management interface).
 *
 * @author ogavavka, vsabadosh
 */
public class DiagnosticDynamicMBean implements DynamicMBean, Diagnostic, InitializingBean {
    /**
     * Identifies a diagnostic services.
     */
    private Set<Diagnostic> diagnosticServices;

    /**
     * Identifies a diagnostic attributes.
     */
    private Set<String> excludedDiagnosticAttributes;

    private Map<DiagnosticAttribute, DiagnosticCallback> diagnosticData = new HashMap<DiagnosticAttribute, DiagnosticCallback>();

    public void setDiagnosticServices(Set<Diagnostic> diagnosticServices) {
        this.diagnosticServices = diagnosticServices;
    }

    public void setExcludedDiagnosticAttributes(Set<String> excludedDiagnosticAttributes) {
        this.excludedDiagnosticAttributes = excludedDiagnosticAttributes;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Diagnostic diagnostic : diagnosticServices) {
            diagnosticData.putAll(diagnostic.getDiagnosticData());
        }
        //Remove excluded attributes.
        for (String excludedAttribute : excludedDiagnosticAttributes) {
            diagnosticData.remove(new DiagnosticAttributeImpl(excludedAttribute, null, null));
        }
    }

    /**
     * Obtains the value of a specific attribute of the Dynamic MBean by calling #getDiagnosticData method.
     *
     * @param attribute The name of the attribute to be retrieved
     *
     * @return  The value of the attribute retrieved.
     *
     * @exception AttributeNotFoundException
     * @exception MBeanException  Wraps a <CODE>java.lang.Exception</CODE> thrown by the MBean's getter.
     * @exception ReflectionException  Wraps a <CODE>java.lang.Exception</CODE> thrown while trying to invoke the getter.
     */
    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException,
            ReflectionException {
        return diagnosticData.get(new DiagnosticAttributeImpl(attribute, null, null)).getDiagnosticAttributeValue();
    }

    /**
     * Set the value of a specific attribute of the Dynamic MBean.
     *
     * @param attribute The identification of the attribute to be set and  the value it is to be set to.
     *
     * @exception AttributeNotFoundException
     * @exception InvalidAttributeValueException
     * @exception MBeanException Wraps a <CODE>java.lang.Exception</CODE> thrown by the MBean's setter.
     * @exception ReflectionException Wraps a <CODE>java.lang.Exception</CODE> thrown while trying to invoke the MBean's
     * setter.
     *
     * @see #setAttribute
     */
    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException,
            MBeanException, ReflectionException {
        //Empty body.
    }

     /**
      * Get the values of several attributes of the Dynamic MBean based on diagnostic attributes which is setup for
      * current instance.
      *
      * @param attributes A list of the attributes to be retrieved.
      *
      * @return  The list of attributes retrieved.
      *
      * @see #setAttributes
      */
    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList list = new AttributeList();
        for (String attribute : attributes) {
            list.add(new Attribute(attribute, diagnosticData.get(new DiagnosticAttributeImpl(attribute, null, null)).getDiagnosticAttributeValue()));
        }
        return list;
    }

    /**
     * Sets the values of several attributes of the Dynamic MBean.
     *
     * @param attributes A list of attributes: The identification of the attributes to be set and  the values they are
     * to be set to.
     *
     * @return  The list of attributes that were set, with their new values.
     *
     * @see #getAttributes
     */
    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        //Empty body.
        return null;
    }

    /**
     * Allows an action to be invoked on the Dynamic MBean.
     *
     * @param actionName The name of the action to be invoked.
     * @param params An array containing the parameters to be set when the action is invoked.
     * @param signature An array containing the signature of the action. The class objects will be loaded through the
     * same class loader as the one used for loading the MBean on which the action is invoked.
     *
     * @return  The object returned by the action, which represents the result of invoking the action on the MBean
     * specified.
     *
     * @exception MBeanException  Wraps a <CODE>java.lang.Exception</CODE> thrown by the MBean's invoked method.
     * @exception ReflectionException  Wraps a <CODE>java.lang.Exception</CODE> thrown while trying to invoke the method
     */
    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        //Empty body.
        return null;
    }

    /**
     * Provides the exposed attributes and actions of the Dynamic MBean using an MBeanInfo object.
     *
     * @return  An instance of <CODE>MBeanInfo</CODE> allowing all attributes and actions exposed by this Dynamic MBean
     * to be retrieved.
     */
    @Override
    public MBeanInfo getMBeanInfo() {
        MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[diagnosticData.keySet().size()];
        int iterator = 0;
        List<DiagnosticAttribute> sortedDiagnosticAtributes = new ArrayList<DiagnosticAttribute>(diagnosticData.keySet());
        Collections.sort(sortedDiagnosticAtributes, new DiagnosticComparator());
        for (DiagnosticAttribute diagnosticAttribute : sortedDiagnosticAtributes) {
            attrs[iterator++]= new MBeanAttributeInfo(diagnosticAttribute.getAttributeName(), diagnosticAttribute.getAttributeType(),
                    diagnosticAttribute.getAttributeDescription(), true, false, false);
        }
        return new MBeanInfo(getClass().getName(), "Property Manager MBean", attrs, null, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        return diagnosticData;
    }

    public static class DiagnosticComparator implements Comparator<DiagnosticAttribute> {
        @Override
        public int compare(DiagnosticAttribute diagnAttribute, DiagnosticAttribute diagnAttribute1) {
            return (diagnAttribute.getAttributeName().compareTo(diagnAttribute1.getAttributeName()) < 0 ? -1 :
                    (diagnAttribute.getAttributeName().equals(diagnAttribute1.getAttributeName()) ? 0 : 1));
        }
    }
}
