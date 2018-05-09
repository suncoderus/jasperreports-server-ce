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

package com.jaspersoft.jasperserver.search.filter;

import java.io.Serializable;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;

/**
 * Base search filter.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
abstract public class BaseSearchFilter extends HibernateDaoImpl implements SearchFilter, Serializable {

    /**
     * Extracts <code>SearchAttributes</code> object from execution context.
     *
     * @param context the execution context.
     * @return <code>SearchAttributes</code> object from execution context.
     */
    protected SearchAttributes getSearchAttributes(ExecutionContext context) {
        return getTypedAttribute(context, SearchAttributes.class);
    }

    /**
     * Get attribute of specific class from the execution context attributes.
     *
     * @param context        - the execution context
     * @param attributeClass - class of the attribute
     * @param <T>            - result should be instanceof this generic parameter value
     * @return first attribute assignable from given attribute class.
     */
    protected <T> T getTypedAttribute(ExecutionContext context, Class<T> attributeClass) {
        T result = null;
        if (context != null && context.getAttributes() != null && !context.getAttributes().isEmpty())
            for (Object currentAttribute : context.getAttributes())
                if (attributeClass.isAssignableFrom(currentAttribute.getClass())) {
                    // casting safety is checked above
                    @SuppressWarnings("unchecked")
                    final T typedAttribute = (T) currentAttribute;
                    result = typedAttribute;
                    break;
                }
        return result;
    }
}
