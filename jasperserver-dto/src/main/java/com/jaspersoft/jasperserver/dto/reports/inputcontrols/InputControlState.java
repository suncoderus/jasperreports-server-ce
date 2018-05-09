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
package com.jaspersoft.jasperserver.dto.reports.inputcontrols;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * InputControlState
 * @author akasych
 * @version $Id$
 * Simple class to transfer Input Control value.
 */

@XmlRootElement
public class InputControlState implements Serializable {

    private final static long serialVersionUID = 1l;

    private String uri;
    private String id;
    private String value;
    private String error;
    private List<InputControlOption> options;

    public InputControlState(){
    }

    public InputControlState(InputControlState other) {
        this.uri = other.getUri();
        this.id = other.getId();
        this.value = other.getValue();
        this.error = other.getError();

        final List<InputControlOption> clientAttributes = other.getOptions();
        if(clientAttributes != null){
            options = new ArrayList<InputControlOption>(other.getOptions().size());
            for(InputControlOption attribute : clientAttributes){
                options.add(new InputControlOption(attribute));
            }
        }
    }

    public String getId() {
        return id;
    }

    public InputControlState setId(String id) {
        this.id = id;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public InputControlState setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getValue() {
        return value;
    }

    public InputControlState setValue(String value) {
        this.value = value;
        return this;
    }

    public String getError() {
        return error;
    }

    public InputControlState setError(String error) {
        this.error = error;
        return this;
    }

    @XmlElementWrapper(name = "options")
    @XmlElement(name = "option")
    public List<InputControlOption> getOptions() {
        return options;
    }

    public InputControlState setOptions(List<InputControlOption> options) {
        this.options = options;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputControlState)) return false;

        InputControlState that = (InputControlState) o;

        if (error != null ? !error.equals(that.error) : that.error != null) return false;
        if (!id.equals(that.id)) return false;
        if (options != null ? !options.equals(that.options) : that.options != null) return false;
        if (!uri.equals(that.uri)) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        return result;
    }
}




