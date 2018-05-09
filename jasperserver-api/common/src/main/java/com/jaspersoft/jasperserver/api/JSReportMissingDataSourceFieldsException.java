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

package com.jaspersoft.jasperserver.api;

import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * @author schubar
 */
public class JSReportMissingDataSourceFieldsException extends JSMissingDataSourceFieldsException {

    public static final String REPORT_MESSAGE_ID = "report.missing.data.source.fields";

    public JSReportMissingDataSourceFieldsException(JSMissingDataSourceFieldsException e) {
        super(REPORT_MESSAGE_ID, e.getFields());
    }
}
