/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.buildomatic.sql.ant;

import java.lang.reflect.Field;

/**
 * @author Vladimir Tsukur
 */
public final class ReflectionUtils {

    /**
     * Default private constructor.
     * Suppresses creation of instances of this class outside the class body.
     */
    private ReflectionUtils() {
        // No operations.
    }

    public static Field findField(Class clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        }
        catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Cannot find " +
                    "[" + name + "] field in class " + clazz.getName() + ". " +
                    "Reason: " + e.getMessage(), e);
        }
    }

    public static Object getFieldValue(Field field, Object target) {
        try {
            field.setAccessible(true);
            return field.get(target);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot access (get) " +
                    "[" + field + "] field in target [" + target + "]. " +
                    "Reason: " + e.getMessage(), e);
        }
    }

    public static void setFieldValue(Field field, Object target, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot access (set) " +
                    "[" + field + "] field in target [" + target + "]. " +
                    "Reason: " + e.getMessage(), e);
        }
    }

}
