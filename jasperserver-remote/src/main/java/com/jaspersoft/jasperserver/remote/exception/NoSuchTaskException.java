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
package com.jaspersoft.jasperserver.remote.exception;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

/**
 * @author: Zakhar.Tomchenco
 */
public class NoSuchTaskException extends ResourceNotFoundException {
    public static final String ERROR_CODE_NO_SUCH_EXPORT_PROCESS = "no.such.export.process";

    public NoSuchTaskException(String taskId) {
        super(new ErrorDescriptor()
                .setErrorCode(ERROR_CODE_NO_SUCH_EXPORT_PROCESS)
                .setMessage("No export task with id " + taskId)
                .setParameters(taskId));
    }
}

