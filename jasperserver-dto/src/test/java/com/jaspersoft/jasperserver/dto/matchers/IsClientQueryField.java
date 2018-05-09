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

package com.jaspersoft.jasperserver.dto.matchers;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static com.google.common.base.Objects.equal;

/**
 * Created by schubar on 2/6/16.
 */
public class IsClientQueryField extends TypeSafeMatcher<ClientField> {

    private String id;
    private String type;
    private boolean measure;
    private String format;
    private String fieldName;

    public IsClientQueryField(String id, String type, boolean measure, String fieldName, String format) {
        this.id = id;
        this.type = type;
        this.fieldName = fieldName;
        this.measure = measure;
        this.format = format;
    }

    @Override
    protected boolean matchesSafely(ClientField clientField) {
        if (clientField instanceof ClientQueryField){
            ClientQueryField actualField = (ClientQueryField) clientField;

            return
                    equal(actualField.getId(), id)
                            && equal(actualField.getType(), type)
                            && actualField.isMeasure() == measure
                            && equal(actualField.getFieldName(), fieldName);

        } else {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendValue(new ClientQueryField().setId(id).setFieldName(fieldName));
    }

    @Factory
    public static Matcher isClientQueryField(String id, String type, boolean measure, String fieldName, String format) {
        return new IsClientQueryField(id, type, measure, fieldName, format);
    }

    @Factory
    public static Matcher isClientQueryField(String id) {
        return new IsClientQueryField(id, null, false, null, null);
    }
    @Factory
    public static Matcher isClientQueryField(String id, String fieldName) {
        return new IsClientQueryField(id, null, false, fieldName, null);
    }
    @Factory
    public static Matcher isClientQueryField(String id, String fieldName, String format) {
        return new IsClientQueryField(id, null, false, fieldName, format);
    }
    @Factory
    public static Matcher isClientQueryField(String id, boolean measure) {
        return new IsClientQueryField(id, null, measure, null, null);
    }
    @Factory
    public static Matcher isClientQueryField(String id, boolean measure, String fieldName) {
        return new IsClientQueryField(id, null, measure, fieldName, null);
    }
    @Factory
    public static Matcher isClientQueryField(String id, String type, boolean measure) {
        return new IsClientQueryField(id, type, measure, null, null);
    }
    @Factory
    public static Matcher isClientQueryField(String id, String type, boolean measure, String fieldName) {
        return new IsClientQueryField(id, type, measure, fieldName, null);
    }
}
