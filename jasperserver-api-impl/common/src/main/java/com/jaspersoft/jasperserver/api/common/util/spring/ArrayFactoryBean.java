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
package com.jaspersoft.jasperserver.api.common.util.spring;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ArrayFactoryBean extends AbstractFactoryBean {
    private String typeClass = "java.lang.String";
    private List sourceList = Collections.EMPTY_LIST;
    private Object[] array = new String[0];

    @Override
    public Class<?> getObjectType() {
        return array.getClass();
    }

    @Override
    protected Object createInstance() throws Exception {
        return sourceList.toArray(array);
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(String typeClass) {
        try {
            this.array = (Object[])Array.newInstance(Class.forName(typeClass), 0);
            this.typeClass = typeClass;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load class "+ typeClass);
        }
    }

    public List getSourceList() {
        return sourceList;
    }

    public void setSourceList(List sourceList) {
        this.sourceList = sourceList;
    }
}
