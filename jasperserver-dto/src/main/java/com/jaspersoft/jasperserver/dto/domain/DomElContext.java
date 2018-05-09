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
package com.jaspersoft.jasperserver.dto.domain;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class DomElContext extends BaseDomElContext<DomElContext> {

    public DomElContext(){}
    public DomElContext(DomElContext source){
        super(source);
    }

    private DomElContext(BaseDomElContext source){
        super(source);
    }

    @Override
    public DomElContext deepClone() {
        return new DomElContext(this);
    }

    public static DomElContext valueOf(BaseDomElContext baseDomElContext){
        return new DomElContext(baseDomElContext);
    }
}
