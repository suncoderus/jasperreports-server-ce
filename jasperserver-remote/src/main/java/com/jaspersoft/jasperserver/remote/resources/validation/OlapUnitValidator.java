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

import com.jaspersoft.jasperserver.api.common.domain.ValidationDetail;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.List;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.addMandatoryParameterNotFoundError;
import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class OlapUnitValidator extends GenericResourceValidator<OlapUnit> {
    @Resource
    private OlapConnectionService olapConnectionService;

    @Override
    protected void internalValidate(OlapUnit resource, ValidationErrors errors) {
        if (empty(resource.getMdxQuery())){
            addMandatoryParameterNotFoundError(errors, "mdxQuery");
        }

        if (empty(resource.getOlapClientConnection()) || empty(resource.getOlapClientConnection().getTargetURI())){
            addMandatoryParameterNotFoundError(errors, "olapConnection");
        } else {
            ValidationResult result = olapConnectionService.validate(null, resource);
            if (ValidationResult.STATE_ERROR.equals(result.getValidationState())){
                for (ValidationDetail detail : (List<ValidationDetail>)result.getResults()){
                    addError(errors, IllegalParameterValueException.ERROR_CODE,
                            empty(detail.getName()) ? detail.getLabel() : detail.getName(),
                            detail.getMessage(), detail.getException(), detail.getId(), detail.getResult());
                }
            }
        }
    }
}
