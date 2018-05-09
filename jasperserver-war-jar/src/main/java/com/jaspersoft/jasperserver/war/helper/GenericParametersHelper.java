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
package com.jaspersoft.jasperserver.war.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class GenericParametersHelper {
    protected final static Log log = LogFactory.getLog(GenericParametersHelper.class);

    public static Class<?> getGenericTypeArgument(Class<?> classToParse, Class<?> genericClassToFind, Integer argumentIndex) {
        Class<?> result = null;
        ParameterizedType parameterizedType = null;
        Class<?> currentClass = classToParse;
        Map<String, Class<?>> currentParameterValues = new HashMap<String, Class<?>>();
        Type[] previousTypeArguments = null;
        while (parameterizedType == null) {
            final TypeVariable<? extends Class<?>>[] typeParameters = currentClass.getTypeParameters();
            currentParameterValues = getCurrentParameterValues(typeParameters, previousTypeArguments, currentParameterValues);
            parameterizedType = findParametrizedType(currentClass, genericClassToFind, currentParameterValues);
            if (parameterizedType == null) {
                // current class doesn't extend/implement searched class directly. Should parse superclass
                final Type genericSuperclassType = currentClass.getGenericSuperclass();
                if (genericSuperclassType instanceof Class<?>) {
                    log.debug(classToParse.getName() + " is raw subclass of " + genericClassToFind.getName());
                    return null;
                }
                final ParameterizedType genericSuperclass = (ParameterizedType) genericSuperclassType;
                previousTypeArguments = genericSuperclass.getActualTypeArguments();
                currentClass = (Class<?>) genericSuperclass.getRawType();
            }
        }
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments[argumentIndex] instanceof Class<?>) {
            result = (Class<?>) actualTypeArguments[argumentIndex];
        } else if (actualTypeArguments[argumentIndex] instanceof TypeVariable) {
            result = currentParameterValues.get(((TypeVariable<?>) actualTypeArguments[argumentIndex]).getName());
        } else if(actualTypeArguments[argumentIndex] instanceof ParameterizedType
                && ((ParameterizedType)actualTypeArguments[argumentIndex]).getRawType() instanceof Class){
            result = (Class<?>) ((ParameterizedType)actualTypeArguments[argumentIndex]).getRawType();
        }
        if (result == null) {
            log.debug("Class " + classToParse.getName() + " has unsupported inheritance structure");
        }
        return result;
    }

    private static Map<String, Class<?>> getCurrentParameterValues(
            TypeVariable<? extends Class<?>>[] typeParameters,
            Type[] previousTypeArguments,
            Map<String, Class<?>> inputParameterValues) {
        Map<String, Class<?>> result = inputParameterValues != null ? inputParameterValues : new HashMap<String, Class<?>>();
        if (typeParameters != null && typeParameters.length > 0) {
            Map<String, Class<?>> currentParameterValues = new HashMap<String, Class<?>>();
            for (int i = 0; i < typeParameters.length; i++) {
                TypeVariable<? extends Class<?>> currentVariable = typeParameters[i];
                if (previousTypeArguments != null && previousTypeArguments.length > i) {
                    // fill current type parameters with arguments from subclass declaration
                    Type argumentType = previousTypeArguments[i];
                    if (argumentType instanceof Class<?>) {
                        currentParameterValues.put(currentVariable.getName(), (Class<?>) argumentType);
                        continue;
                    } else if (argumentType instanceof TypeVariable<?> && result.containsKey(((TypeVariable<?>) argumentType).getName())) {
                        currentParameterValues.put(currentVariable.getName(), result.get(((TypeVariable<?>) argumentType).getName()));
                        continue;
                    }
                }
                Class<?> variableClass = null;
                final Type[] bounds = currentVariable.getBounds();
                if (bounds != null && bounds.length > 0) {
                    if (bounds[0] instanceof Class<?>) {
                        variableClass = (Class<?>) bounds[0];
                    } else if (bounds[0] instanceof ParameterizedType) {
                        variableClass = (Class) ((ParameterizedType) bounds[0]).getRawType();
                    }
                }
                currentParameterValues.put(currentVariable.getName(), variableClass);
            }
            result.clear();
            result.putAll(currentParameterValues);
        }
        return result;
    }

    private static ParameterizedType findParametrizedType(Class<?> classToParse, Class<?> genericClassToFind, Map<String, Class<?>> inputParameterValues) {
        ParameterizedType type = null;
        if (genericClassToFind.isInterface()) {
            final Type[] genericInterfaces = classToParse.getGenericInterfaces();
            if (genericInterfaces != null && genericInterfaces.length > 0) {
                for (Type genericInterface : genericInterfaces) {
                    if (genericInterface == genericClassToFind) {
                        throw new IllegalArgumentException(classToParse.getName() + " is raw implementation of " + genericClassToFind.getName());
                    }
                    if (genericInterface instanceof ParameterizedType) {
                        ParameterizedType currentParametrizedType = (ParameterizedType) genericInterface;
                        Map<String, Class<?>> currentParameterValues = new HashMap<String, Class<?>>(inputParameterValues);
                        if (currentParametrizedType.getRawType() == genericClassToFind) {
                            type = (ParameterizedType) genericInterface;
                        } else {
                            currentParameterValues = getCurrentParameterValues(
                                    ((Class<?>) currentParametrizedType.getRawType()).getTypeParameters(),
                                    currentParametrizedType.getActualTypeArguments(),
                                    new HashMap<String, Class<?>>(inputParameterValues));
                            type = findParametrizedType((Class<?>) currentParametrizedType.getRawType(), genericClassToFind, currentParameterValues);
                        }
                        if (type != null) {
                            inputParameterValues.clear();
                            inputParameterValues.putAll(currentParameterValues);
                            break;
                        }
                    }
                }
            }
        } else {
            final Type genericSuperclass = classToParse.getGenericSuperclass();
            if (genericSuperclass == genericClassToFind) {
                log.debug(classToParse.getName() + " is raw subclass of " + genericClassToFind.getName());
            } else if (genericSuperclass instanceof ParameterizedType &&
                    ((ParameterizedType) genericSuperclass).getRawType() == genericClassToFind) {
                type = (ParameterizedType) genericSuperclass;
            }
        }
        return type;
    }
}
