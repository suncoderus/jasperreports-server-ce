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

package com.jaspersoft.jasperserver.remote.spel;

import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.spel.support.StandardTypeConverter;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ReferenceableTypeConverter extends StandardTypeConverter {
    @Override
    public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType == null){
            for (Class referenceMarker : ClientReference.class.getInterfaces()){
                if (referenceMarker.isAssignableFrom(targetType.getType())){
                    return true;
                }
            }
        } else if (sourceType.getType().equals(String.class)){
            for (Class referenceMarker : ClientReference.class.getInterfaces()){
                if (referenceMarker.isAssignableFrom(targetType.getType())){
                    return true;
                }
            }
        }

        return super.canConvert(sourceType, targetType);
    }

    @Override
    public Object convertValue(Object value, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType == null) {
            for (Class referenceMarker : ClientReference.class.getInterfaces()) {
                if (referenceMarker.isAssignableFrom(targetType.getType())) {
                    return null;
                }
            }
        } else if (sourceType.getType().equals(String.class)) {
            for (Class referenceMarker : ClientReference.class.getInterfaces()) {
                if (referenceMarker.isAssignableFrom(targetType.getType())) {
                    return new ClientReference((String) value);
                }
            }
        }

        return super.convertValue(value, sourceType, targetType);
    }
}
