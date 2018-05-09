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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 * @deprecated: move to search criterias
 */
public class GenericRequest {

   private Boolean expanded = true;

   private Map<String, Object> params = new HashMap();
   private Boolean optional = false;


    public Boolean isExpanded() {
        return expanded;
    }

    public GenericRequest setExpanded(Boolean expanded) {
        this.expanded = expanded;
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public GenericRequest setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public GenericRequest addParam(String key, String value){
        params.put(key, value);
        return  this;
    }

    public GenericRequest setOptional(Boolean optional) {
        this.optional = optional;
        return this;
    }

    public Boolean isOptional() {
        return optional;
    }
}
