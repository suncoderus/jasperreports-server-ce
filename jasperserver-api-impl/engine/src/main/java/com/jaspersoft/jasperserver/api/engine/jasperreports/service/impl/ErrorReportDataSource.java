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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSMissingDataSourceFieldsException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.springframework.context.MessageSource;

import java.util.*;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.unmodifiableList;


/**
 * @author schubar
 * @version $Id: $
 */
public class ErrorReportDataSource implements JRDataSource {

    public static final String MESSAGE = "message";
    public static final String FIELDS = "fields";

    private Exception error;
    private MessageSource messageSource;
    private Locale locale;

    private boolean next = false;

    public ErrorReportDataSource(Exception error) {
        this.error = error == null ? new JSException("Unknown error") : error;
    }

    public ErrorReportDataSource(Exception error, MessageSource messageSource, Locale locale) {
        this(error);
        this.messageSource = messageSource;
        this.locale = locale != null ? locale : Locale.getDefault();
    }

    public boolean next() throws JRException {
        return (next = !next);
    }

    public Object getFieldValue(JRField field) throws JRException {
        if (MESSAGE.equals(field.getName())) {
            return messageSource.getMessage(error.getMessage(), new Object[0], locale);

        } else if(FIELDS.equals(field.getName()) && error instanceof JSMissingDataSourceFieldsException) {
            return ((JSMissingDataSourceFieldsException) error).getFields();

        } else {
            return null;
        }
    }
}
