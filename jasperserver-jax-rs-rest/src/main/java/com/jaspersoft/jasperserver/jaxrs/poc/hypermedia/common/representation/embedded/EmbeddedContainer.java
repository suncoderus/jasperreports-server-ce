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
package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.embedded;


import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;

import java.util.LinkedHashMap;

/**
 * @author Igor.Nesterenko
 * @version $Id$
 */

public class EmbeddedContainer {

    private LinkedHashMap<Relation, EmbeddedElement> map = new LinkedHashMap<Relation, EmbeddedElement> ();

    public EmbeddedContainer put(Relation relation, EmbeddedElement newElement){

        if (map.containsKey(relation)){

            EmbeddedElement existingElement = map.get(relation);
            if(existingElement instanceof PluralEmbeddedElement){
                ((PluralEmbeddedElement)existingElement).push(newElement);
            }else {
                PluralEmbeddedElement pluralEmbeddedElement = new PluralEmbeddedElement(relation);
                pluralEmbeddedElement.push(existingElement);
                pluralEmbeddedElement.push(newElement);
                map.remove(existingElement);
                map.put(relation, pluralEmbeddedElement);
            }

        } else{
            map.put(relation, newElement);
        }

        return this;
    }

    public EmbeddedElement get(Relation relation){
        return map.get(relation);
    }

    public LinkedHashMap<Relation, EmbeddedElement> toMap(){
        return map;
    }
}
