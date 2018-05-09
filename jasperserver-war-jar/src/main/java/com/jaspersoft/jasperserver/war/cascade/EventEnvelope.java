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
package com.jaspersoft.jasperserver.war.cascade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * InputControlState
 * @author jwhang
 * @version $Id$
 */

public class EventEnvelope implements Serializable {

    private final static long serialVersionUID = 1l;

    private String controlName = "";
    private String controlValue = "";
    private String resourceUriPrefix = "";
    private int controlType = 0;
    private List<EventOption> optionsList;
    private boolean visible = true;
    private boolean disabled = false;
    private boolean permanent = false;
    private String wrappersUUID = "";
    private boolean mandatory = false;


    public EventEnvelope(){
    }

    public String getResourceUriPrefix() {
        return resourceUriPrefix;
    }

    public void setResourceUriPrefix(String resourceUriPrefix) {
        this.resourceUriPrefix = resourceUriPrefix;
    }

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

    public String getControlValue() {
        return controlValue;
    }

    public void setControlValue(String controlValue) {
        this.controlValue = controlValue;
    }

    public int getControlType() {
        return controlType;
    }

    public void setControlType(int controlType) {
        this.controlType = controlType;
    }

    public List<EventOption> getOptionsList() {
        return optionsList;
    }

    public void setOptionsList(List<EventOption> optionsList) {
        this.optionsList = optionsList;
    }

    public List<String> getSelections(){ //convenience method to assemble selected options.
        ArrayList<String> selections = new ArrayList<String>();
        for (EventOption mo : optionsList){
            if (mo.isSelected()){
                selections.add(mo.getValue());
            }
        }
        return selections;
    }

    public void setSelections(List<String> selections){
        //do nothing. TODO: support for additional possible operations.
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public String getWrappersUUID() {
        return wrappersUUID;
    }

    public void setWrappersUUID(String wrappersUUID) {
        this.wrappersUUID = wrappersUUID;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}




