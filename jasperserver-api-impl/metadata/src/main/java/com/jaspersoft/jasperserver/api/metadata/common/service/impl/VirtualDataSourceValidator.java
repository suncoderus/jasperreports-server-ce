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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;


import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.NullValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Paul Lysak
 * @version $Id: $
 */
public class VirtualDataSourceValidator extends DefaultResourceValidator {
    private Pattern subDsIdInvalidChars = Pattern.compile("\\W");

    public boolean validateSubDsId(String value, String nameField, ValidationErrors errors) {
        if(!validateNameString(value, nameField, errors)) {
            return false;
        }

        if(subDsIdInvalidChars.matcher(value).find()) {
            errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.invalid.chars", null, "Only letters, digits and underscore allowed in subDsId", nameField));
        }
        if(!StringUtils.isEmpty(value)) {
            char firstChar = value.charAt(0);
            if(firstChar == '_' || Character.isDigit(firstChar)) {
                errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "error.first.letter.required", null, "Should start with letter", nameField));
            }
        }
        return true;
    }

    @Override
    public ValidationErrors validate(Resource resource, ValidationErrorFilter filter) {
        String subDsMapFieldName = getFieldPrefix()+"subDsMap";
        if(filter == null) {
            filter = NullValidationErrorFilter.getInstance();
        }

        ValidationErrors errors = super.validate(resource, filter);
        VirtualReportDataSource vds = (VirtualReportDataSource)resource;

        if(filter.matchErrorField(subDsMapFieldName) &&
                (vds.getDataSourceUriMap() == null || vds.getDataSourceUriMap().size() < 2)) {
            errors.add(new ValidationErrorImpl(getErrorMessagePrefix()+"minimal_subds_count", null, "At least 2 sub-datasources required", "dataSourceUriMap"));
            return errors;
        }

        Set<String> visitedUris = new HashSet<String>();
        String subDsIdFieldName = getFieldPrefix()+"subDsId";
        for(Map.Entry<String, ResourceReference> entry: vds.getDataSourceUriMap().entrySet()) {
            //validate subDsId
            if (filter.matchErrorField(subDsIdFieldName)) {
                validateSubDsId(entry.getKey(), subDsIdFieldName, errors);
            }

            String uri = entry.getValue().getReferenceURI();
            if(filter.matchErrorField(subDsMapFieldName) && visitedUris.contains(uri)) {
                errors.add(new ValidationErrorImpl(getErrorMessagePrefix() + "duplicate_subds_uri", new Object[] {uri},
                        "Same resource referenced twice: {0}"));
            } else {
                visitedUris.add(uri);
            }
        }
        return errors;
    }
}
