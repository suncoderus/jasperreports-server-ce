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

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class InputControlResourceValidatorTest {
    @InjectMocks
    private InputControlResourceValidator validator = new InputControlResourceValidator();
    @Mock
    private Map<String, Map<String, Object>> inputControlTypeConfiguration;
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;
    @Spy
    private Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();
    @Mock
    private Map<String, Object> configuration;

    private InputControl control;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        control = new InputControlImpl();
        control.setLabel("Label");

        reset(inputControlTypeConfiguration, configuration);
        when(inputControlTypeConfiguration.get(anyString())).thenReturn(configuration);
    }

    @Test(groups = {"INIT"})
    public void testInit() throws Exception {
        validator.initialize();
    }

    @Test(groups = {"INIT"})
    public void testInit_requiredPropertyNotMandatory() throws Exception {
        when(inputControlTypeConfiguration.keySet()).thenReturn(new HashSet<String>());

        validator.initialize();
    }

    @Test(groups = {"INIT"})
    public void testInit_propertySet() throws Exception {
        when(inputControlTypeConfiguration.keySet()).thenReturn(new HashSet<String>(Arrays.asList("1")));
        when(configuration.get(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn("dataType");

        validator.initialize();
    }

    @Test(groups = {"INIT"})
    public void testInit_propertiesSet() throws Exception {
        when(inputControlTypeConfiguration.keySet()).thenReturn(new HashSet<String>(Arrays.asList("1")));
        when(configuration.get(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn("dataType;listOfValues");

        validator.initialize();
    }

    @Test(groups = {"INIT"}, expectedExceptions = {IllegalStateException.class})
    public void testInit_propertySet_unknown() throws Exception {
        when(inputControlTypeConfiguration.keySet()).thenReturn(new HashSet<String>(Arrays.asList("1")));
        when(configuration.get(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn("wakka");

        validator.initialize();
    }

    @Test(groups = {"INIT"}, expectedExceptions = {IllegalStateException.class})
    public void testInit_propertiesSet_unknown() throws Exception {
        when(inputControlTypeConfiguration.keySet()).thenReturn(new HashSet<String>(Arrays.asList("1")));
        when(configuration.get(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn("wakka;pinux");

        validator.initialize();
    }

    @Test(groups = {"INIT"}, expectedExceptions = {IllegalStateException.class})
    public void testInit_propertiesSet_one_unknown() throws Exception {
        when(inputControlTypeConfiguration.keySet()).thenReturn(new HashSet<String>(Arrays.asList("1")));
        when(configuration.get(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn("dataType;listOfValues;pinux");

        validator.initialize();
    }

    @Test(groups = {"VALIDATE"}, dependsOnGroups = {"INIT"})
    public void testValidate() throws Exception {
        validator.validate(control);
    }

    @Test(groups = {"VALIDATE"}, dependsOnGroups = {"INIT"}, expectedExceptions = {JSValidationException.class})
    public void testValidate_unknownType() throws Exception {
        reset(inputControlTypeConfiguration);

        validator.validate(control);
    }

    @Test(groups = {"VALIDATE"}, dependsOnGroups = {"INIT"}, expectedExceptions = {JSValidationException.class})
    public void testValidate_simpleType_nothingShouldBeSet() throws Exception {
        control.setDataTypeReference("/a");

        validator.validate(control);
    }

    @Test(groups = {"VALIDATE"}, dependsOnGroups = {"INIT"})
    public void testValidate_customType_dataTypeShouldBeSet() throws Exception {
        control.setDataTypeReference("/a");

        when(configuration.get(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn("dataType");
        when(configuration.containsKey(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn(true);

        validator.validate(control);
    }

    @Test(groups = {"VALIDATE"}, dependsOnGroups = {"INIT"}, expectedExceptions = {JSValidationException.class})
    public void testValidate_customType_dataTypeShouldBeSet_extraField() throws Exception {
        control.setDataTypeReference("/a");
        control.setListOfValuesReference("/b");

        when(configuration.get(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn("dataType");
        when(configuration.containsKey(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn(true);

        validator.validate(control);
    }

    @Test(groups = {"VALIDATE"}, dependsOnGroups = {"INIT"})
    public void testValidate_customType_dataTypeAndLoVShouldBeSet() throws Exception {
        control.setDataTypeReference("/a");
        control.setListOfValuesReference("/b");

        when(configuration.get(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn("dataType;listOfValues");
        when(configuration.containsKey(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn(true);

        validator.validate(control);
    }

    @Test(groups = {"VALIDATE"}, dependsOnGroups = {"INIT"}, expectedExceptions = {JSValidationException.class})
    public void testValidate_customType_dataTypeSetButLoVShouldBeSet() throws Exception {
        control.setDataTypeReference("/a");

        when(configuration.get(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn("listOfValues");
        when(configuration.containsKey(InputControlResourceValidator.PROPERTY_REQUIRED)).thenReturn(true);

        validator.validate(control);
    }
}
