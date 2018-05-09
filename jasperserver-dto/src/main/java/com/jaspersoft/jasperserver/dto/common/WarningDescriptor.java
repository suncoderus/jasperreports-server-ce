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
package com.jaspersoft.jasperserver.dto.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 *
 * @author Volodya Sabadosh
 * @version $Id: $
 */
@XmlRootElement(name = "warning")
public class WarningDescriptor {
    private String code;
    private String[] parameters;
    private String message;

    public WarningDescriptor(String code, String[] parameters, String message) {
        this.code = code;
        this.parameters = parameters;
        this.message = message;
    }

    public WarningDescriptor() {
    }

    @XmlElement(name = "code")
    public String getCode() {
        return code;
    }

    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    public String[] getParameters() {
        return parameters;
    }

    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    public WarningDescriptor setCode(String code) {
        this.code = code;
        return this;
    }

    public WarningDescriptor setParameters(String[] parameters) {
        this.parameters = parameters;
        return this;
    }

    public WarningDescriptor setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "Warning: " +
                "code='" + code + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", message='" + message + '\'' +
                '}';
    }
}
