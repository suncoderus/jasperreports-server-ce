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
package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public enum Permissions{

    NOTHING(0),
    ADMINISTRATION(1),
    READ(2),
    WRITE(4),
    CREATE(8),
    DELETE(16),
    RUN(30); // TODO: Sergey P.: there is no such permission as RUN.
             // here it's a combination of READ+WRITE+CREATE+DELETE so it's useless

    Integer mask;

    Permissions(Integer mask) {
        this.mask = mask;
    }

    public Integer mask(){
        return  mask;
    }

    public boolean equals(Integer that){
         return  this.mask().equals(that);
    }

}
