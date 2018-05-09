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
package com.jaspersoft.jasperserver.api.metadata.olap.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLATestResult {
	public enum XMLATestCode {
		OK,
		URI_CONNECTION_FAILED,
		BAD_URI,
		BAD_CREDENTIALS,
		BAD_DATASOURCE,
		BAD_CATALOG,
		OTHER;
	}
	
	protected XMLATestCode status;
	protected String message;
	protected String details;
	protected String[] options;

    protected static final String regexUri = "(.*//.*:).*(@.*)";

    protected static final Set<XMLATestCode> ioExceptionTestCodes = new HashSet<XMLATestCode>(Arrays.asList(
            XMLATestCode.URI_CONNECTION_FAILED,
            XMLATestCode.BAD_URI,
            XMLATestCode.BAD_CREDENTIALS));


    public XMLATestResult(XMLATestCode status, String[] options, ErrorDescriptor ed) {
		this.status = status;
		this.options = options;

        if (ed != null) {
            String message = ed.getMessage();

            // In some cases the message contains the plain password.
            // Obfuscate the password if possible, otherwise - set the XMLATestCode name. Bug #34979.
            Matcher m = Pattern.compile(regexUri).matcher(message);

            boolean obfuscatePassword = ioExceptionTestCodes.contains(status) && m.matches();

            // set the status.name() in case if ioExceptionTestCodes has different URI format (not matching regexUri)
            this.message = obfuscatePassword ? m.replaceFirst("$1********$2") : message;

            // set the stack trace
            final String[] edParams = ed.getParameters();
            if (edParams != null && edParams.length > 0)
                this.details = edParams[0];
        }
	}

	public XMLATestResult(XMLATestCode status, ErrorDescriptor errorDescriptor) {
		this(status,null,errorDescriptor);
	}

	public XMLATestResult(XMLATestCode status) {
		this(status,null,null);
	}

    public String buildJson() throws Exception {
        Map<String, String> response = new HashMap<String, String>();
        response.put("status", status.name());
        response.put("message", message);
        if (details != null) {
            response.put("details", details);
        }

        ObjectMapper mapper = new ObjectMapper();
        StringWriter result = new StringWriter();
        mapper.writeValue(result, response);
        return result.toString();
    }

	public XMLATestCode getStatus() {
		return status;
	}
	public void setStatus(XMLATestCode status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
}
