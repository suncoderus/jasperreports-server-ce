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
package com.jaspersoft.jasperserver.dto.resources.domain.validation;

import com.jaspersoft.jasperserver.dto.bridge.BridgeRegistry;
import com.jaspersoft.jasperserver.dto.bridge.SettingsBridge;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.common.ValidationErrorDescriptorBuilder;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class ValidEnumValueOrProfileAttributeValidator implements
        ConstraintValidator<ValidEnumValueOrProfileAttribute, String>, ValidationErrorDescriptorBuilder {
    private Set<String> enumNames = new HashSet<String>();
    private String errorCode;
    @Override
    public void initialize(ValidEnumValueOrProfileAttribute constraintAnnotation) {
        Object[] constants = constraintAnnotation.enumClass().getEnumConstants();
        errorCode = constraintAnnotation.message();
        if(constants == null){
            throw new IllegalStateException("enumClass [" + constraintAnnotation.enumClass() + "]is not enum");
        }
        for (Object constant : constants) {
            enumNames.add(constant.toString());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        final Pattern pattern = getProfileAttributeFunctionPattern();
        return value == null || enumNames.contains(value) || pattern == null || pattern.matcher(value).matches();
    }

    protected Pattern getProfileAttributeFunctionPattern(){
        final SettingsBridge bridge = BridgeRegistry.getBridge(SettingsBridge.class);
        return bridge != null ? Pattern.compile((String) bridge.getSetting(SettingsBridge.GROUP_NAME_PROFILE_ATTRIBUTES,
                "$." + SettingsBridge.SETTING_NAME_ATTRIBUTE_PLACEHOLDER_SIMPLE_PATTERN)) : null;
    }

    @Override
    public ErrorDescriptor build(ConstraintViolation violation) {
        return new ErrorDescriptor().setErrorCode(errorCode).setMessage(violation.getPropertyPath()
                + " should be one of " + enumNames.toString() + " or profile attribute placeholder but is ["
                + violation.getInvalidValue() + "]")
                .addProperties(
                        new ClientProperty().setKey("propertyPath").setValue(violation.getPropertyPath().toString()),
                        new ClientProperty().setKey("invalidValue").setValue(violation.getInvalidValue().toString()),
                        new ClientProperty().setKey("allowedValues").setValue(enumNames.toString())
                        );
    }
}

