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
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptorBuilder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
@Component
public class SqlErrorDescriptorBuilder implements ErrorDescriptorBuilder<SQLException> {
    @Override
    public ErrorDescriptor build(SQLException e) {

        ErrorDescriptor errorDescriptor = new ErrorDescriptor();
        errorDescriptor.setErrorCode("sql.exception");
        StringBuilder stringBuilder = new StringBuilder("SQL error.");
        List<String> parameters = new ArrayList<String>(3);
        String message = e.getMessage();
        if (message != null && !message.isEmpty()) {
            stringBuilder.append(" Reason: " + message + ".");
            parameters.add(message);
        }
        String sqlState = e.getSQLState();
        if (sqlState != null && !sqlState.isEmpty()) {
            stringBuilder.append(" SQL State: " + sqlState + ".");
            parameters.add(sqlState);
        }
        int errorCode = e.getErrorCode();
        if (errorCode != 0) {
            stringBuilder.append(" Vendor code: " + errorCode + ".");
            parameters.add(String.valueOf(errorCode));
        }
        errorDescriptor.setMessage(stringBuilder.toString());
        errorDescriptor.addParameters(parameters);

        return errorDescriptor;
    }
}
