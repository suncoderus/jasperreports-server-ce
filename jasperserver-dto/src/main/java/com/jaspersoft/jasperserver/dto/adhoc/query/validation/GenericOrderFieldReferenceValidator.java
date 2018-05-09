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

package com.jaspersoft.jasperserver.dto.adhoc.query.validation;

import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Andriy Godovanets
 * @version $Id$
 */
public class GenericOrderFieldReferenceValidator implements ConstraintValidator<CheckGenericOrderFieldReference, ClientGenericOrder> {
    @Override
    public void initialize(CheckGenericOrderFieldReference checkGenericOrderFieldReference) {
        // no op
    }

    @Override
    public boolean isValid(ClientGenericOrder order, ConstraintValidatorContext context) {
        boolean hasFieldRefXORAggregationFlag = order.getFieldReference() != null ^ order.isAggregationLevel();

        return hasFieldRefXORAggregationFlag;
    }
}
