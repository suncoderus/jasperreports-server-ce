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

import com.jaspersoft.jasperserver.dto.resources.ClientProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement
public class ErrorDescriptor {
    public static final String ERROR_CODE_UNEXPECTED_ERROR = "unexpected.error";
    private String message;
    private String errorCode;
    private String[] parameters;
    private List<ClientProperty> properties;
    private List<ErrorDescriptor> details;
	private String errorUid;

    private Throwable exception;

	public ErrorDescriptor() {}

	public ErrorDescriptor(final ErrorDescriptor ed) {
        message = ed.getMessage();
        errorCode = ed.getErrorCode();
        parameters = ed.getParameters();
        errorUid = ed.getErrorUid();
        exception = ed.getException();
        properties = ed.getProperties() != null ? new ArrayList<ClientProperty>(){{
            for (ClientProperty property : ed.getProperties()) {
                add(new ClientProperty(property));
            }
        }} : null;
        final List<ErrorDescriptor> sourceDetails = ed.getDetails();
        if(sourceDetails != null){
            details = new ArrayList<ErrorDescriptor>(sourceDetails.size());
            for (ErrorDescriptor sourceDetail : sourceDetails) {
                details.add(new ErrorDescriptor(sourceDetail));
            }
        }
    }

    @XmlElementWrapper(name = "details")
    @XmlElement(name = "detail")
    public List<ErrorDescriptor> getDetails() {
        return details;
    }

    public ErrorDescriptor setDetails(List<ErrorDescriptor> details) {
        this.details = details;
        return this;
    }

    public ErrorDescriptor addDetails(ErrorDescriptor... details){
        final List<ErrorDescriptor> descriptorList = Arrays.asList(details);
        if(this.details == null) {
            this.details = new ArrayList<ErrorDescriptor>(descriptorList);
        } else {
            this.details.addAll(descriptorList);
        }
        return this;
    }

    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    public List<ClientProperty> getProperties() {
        return properties;
    }

    public ErrorDescriptor setProperties(List<ClientProperty> properties) {
        this.properties = properties;
        return this;
    }

    public ErrorDescriptor addProperties(ClientProperty... properties){
        final List<ClientProperty> clientProperties = Arrays.asList(properties);
        if(this.properties == null) {
            this.properties = new ArrayList<ClientProperty>(clientProperties);
        } else {
            this.properties.addAll(clientProperties);
        }
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorDescriptor setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorDescriptor setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    public String[] getParameters() {
        return parameters;
    }

    public ErrorDescriptor setParameters(String... parameters) {
        this.parameters = parameters;
        return this;
    }

    public ErrorDescriptor addParameters(Object... args) {
        if (args != null && args.length > 0) {
            List<String> values = new LinkedList<String>();
            for (Object arg : args) {
                if (arg != null) {
                    values.add(arg.toString());
                } else  {
                    values.add(null);
                }
            }
            if (!values.isEmpty()) {
                parameters = values.toArray(new String[values.size()]);
            }
        }

        return this;
    }

	public String getErrorUid() {
		return errorUid;
	}

	public ErrorDescriptor setErrorUid(String errorUid) {
		this.errorUid = errorUid;
        return this;
	}

	public ErrorDescriptor setException(Throwable exception) {
		this.exception = exception;
        return this;
	}

    @XmlTransient
	public Throwable getException() {
		return exception;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorDescriptor)) return false;

        ErrorDescriptor that = (ErrorDescriptor) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (errorCode != null ? !errorCode.equals(that.errorCode) : that.errorCode != null) return false;
        if (!Arrays.equals(parameters, that.parameters)) return false;
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;
        if (details != null ? !details.equals(that.details) : that.details != null) return false;
        if (errorUid != null ? !errorUid.equals(that.errorUid) : that.errorUid != null) return false;
        return exception != null ? exception.equals(that.exception) : that.exception == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (errorCode != null ? errorCode.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(parameters);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (errorUid != null ? errorUid.hashCode() : 0);
        result = 31 * result + (exception != null ? exception.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ErrorDescriptor{" +
                "message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", properties=" + properties +
                ", details=" + details +
                ", errorUid='" + errorUid + '\'' +
                ", exception=" + exception +
                '}';
    }
}
