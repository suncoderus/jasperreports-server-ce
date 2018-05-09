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

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.*;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenko
 * @version $Id$
 */
@Component
public class ListOfValuesValidator extends GenericResourceValidator<ListOfValues> {
    private final Pattern forbiddenCharacters = Pattern.compile("[\"<>]+");

    @Override
    protected void internalValidate(ListOfValues resource, ValidationErrors errors) {
        Set<String> names = new HashSet<String>();
        for (ListOfValuesItem item : resource.getValues()){
            if (empty(item.getLabel())) {
                addMandatoryParameterNotFoundError(errors, "listOfValuesItem.label");
            } else if (names.contains(item.getLabel())) {
                addIllegalParameterValueError(errors, "listOfValuesItem.label", item.getLabel(), "The label " + item.getLabel() + " already exist in the list of values");
            } else {
                names.add(item.getLabel());
                if (item.getLabel().length() > 255){
                    addIllegalParameterValueError(errors, "listOfValuesItem.label", item.getLabel(), "The label " + item.getLabel() + " is longer than 255 characters");
                }
                if (forbiddenCharacters.matcher(item.getLabel()).matches()){
                    addIllegalParameterValueError(errors, "listOfValuesItem.label", item.getLabel(), "The label " + item.getLabel() + " should not contain symbols \"<>");
                }
            }

            if (empty(item.getValue())){
                addMandatoryParameterNotFoundError(errors, "items."+ item.getLabel());
            } else {
                if (item.getValue() instanceof String){
                    String value = (String)item.getValue();

                    if (forbiddenCharacters.matcher(value).matches()){
                        addIllegalParameterValueError(errors, "listOfValuesItem.value", value, "The value " + value
                                + " should not contain symbols \"<>");
                    }
                }
            }
        }
    }
}
