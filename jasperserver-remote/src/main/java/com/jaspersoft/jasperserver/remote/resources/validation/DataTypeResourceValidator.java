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
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addIllegalParameterValueError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addMandatoryParameterNotFoundError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class DataTypeResourceValidator extends GenericResourceValidator<DataType> {
    @Override
    protected void internalValidate(DataType resource, ValidationErrors errors) {
        if (!empty(resource.getMaxValue()) && !empty(resource.getMinValue()) && resource.getMinValue().compareTo(resource.getMaxValue()) > 0){
            addIllegalParameterValueError(errors, "maxValue", resource.getMaxValue().toString(), "The max value must be greater than min value");
        }

        if (resource.getDataTypeType() == 0){
            addMandatoryParameterNotFoundError(errors, "Type");
        }

        if (resource.getMaxLength() != null && resource.getMaxLength() <= 0){
            addIllegalParameterValueError(errors, "maxLength", resource.getMaxLength().toString(), "The value of the maxLength property must be more than 0");
        }

        if (!empty(resource.getRegularExpr())){
            try {
                Pattern.compile(resource.getRegularExpr());
            } catch (Exception e) {
                addIllegalParameterValueError(errors, "pattern", resource.getRegularExpr(), e.getMessage());
            }
        }
    }
}
