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
package com.jaspersoft.jasperserver.war.cascade.token;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.BuiltInParameterProvider;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FilterCore
 * @author jwhang
 * @version $Id$
 */
public class FilterCore implements FilterResolver, Serializable {

    public static enum ParameterTypes {
        P, P_INC("P!"), X;

        private String stringRepresentation;

        static Pattern pattern = Pattern.compile("\\$(X|P|P!)\\{([^}]+)\\}");

        ParameterTypes()  {
            // Left blank
        }

        ParameterTypes(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        public String toString() {
            return stringRepresentation != null ? stringRepresentation : super.toString();
        }

        public static ParameterTypes value(String rawValue) {
            return P_INC.toString().equals(rawValue) ? P_INC : ParameterTypes.valueOf(rawValue);
        }
    }

    private static Logger log = Logger.getLogger(FilterCore.class);

    List builtInParameterProviders;

    public List getBuiltInParameterProviders() {
        return builtInParameterProviders;
    }

    public void setBuiltInParameterProviders(List builtInParameterProviders) {
        this.builtInParameterProviders = builtInParameterProviders;
    }

    /* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.cascade.token.FilterResolver#hasParameters(java.lang.String)
	 */
    public boolean hasParameters(String queryString, Map parameters) {
        return hasParameters(queryString, 0);
    }

    public boolean hasParameters(String queryString, int start) {
        return nextParameter(queryString, start) != null;
    }

    private Parameter nextParameter(String queryString, int start) {
        if (start < 0 && start > queryString.length() - 1) return null;

        Matcher m = ParameterTypes.pattern.matcher(queryString);

        Parameter p = null;
        if (m.find(start)) {
            p = createParameter(m.group(1), m.group(2));
            if (p != null) {
                p.setStartPosition(m.start());
                p.setEndPosition(m.end());
            }
        }

        return p;
    }

    private Parameter createParameter(String parameterType, String parameterContent) {
        Parameter p = null;
        final ParameterTypes type = ParameterTypes.value(parameterType);
        if (ParameterTypes.P.equals(type) || ParameterTypes.P_INC.equals(type)) {
            p = createPParameter(parameterContent);
        } else if (ParameterTypes.X.equals(type)) {
            p = createXParameter(parameterContent);
        }
        return p;
    }

    private Parameter createPParameter(String parameterContent) {
        Parameter p = new Parameter();
        p.addParameterName(parameterContent.trim());
        return p;
    }

    private Parameter createXParameter(String parameterContent) {
        Parameter p = new Parameter();
        String[] parts = parameterContent.split(",");
        for (int i = 2; i < parts.length; i++) {
            p.addParameterName(parts[i].trim());
        }
        return p;
    }

    /* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.cascade.token.FilterResolver#getParameters(java.lang.String)
	 */
    public List<Parameter> getParameters(String queryString) {
        List<Parameter> parameters = new ArrayList<Parameter>();

        if (queryString == null) {
            return parameters;
        }

        int index = 0;
        while (index < queryString.length() && hasParameters(queryString, index)) {
            Parameter p = nextParameter(queryString, index);

            if (p == null) {
                break;
            }

            index = p.getEndPosition() + 1;

            // skip over escaped parameters
            if (p.getStartPosition() > 0 && queryString.charAt(p.getStartPosition() - 1) == '$') {
                continue;
            }

            // we have a parameter!
            parameters.add(p);
        }

        return parameters;
    }

    /* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.cascade.token.FilterResolver#getParameterNames(java.lang.String)
	 */
    public Set<String> getParameterNames(String queryString, Map<String, Object> providedParameters) {
        Set<String> parameterNames = new LinkedHashSet<String>();
        for (Parameter p : getParameters(queryString)) {
            parameterNames.addAll(p.getParameterNames());
        }
        return parameterNames;
    }

    /* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.cascade.token.FilterResolver#resolveParameters(java.lang.String, java.util.Map)
	 */
    public Object getCacheKey(String queryString, Map<String, Object> providedParameters, String keyColumn, String[] resultColumns) {
    	Map<String, Object> resolvedParameters = resolveParameters(queryString, providedParameters);

    	if (resolvedParameters != null && queryString != null && !queryString.isEmpty()) {
            StringBuilder sb = new StringBuilder(queryString);
            if (!resolvedParameters.isEmpty()) {
                sb.append("; ").append(getString(resolvedParameters));
            }
            if (keyColumn != null && resultColumns != null) {
                sb.append("; ").append(keyColumn).append("; ").append(StringUtils.join(resultColumns,";"));
            }
            return sb.toString();
    	}
    	return null;
    }

    /* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.cascade.token.FilterResolver#resolveParameters(java.lang.String, java.util.Map)
	 */
    public Object getCacheKey(String queryString, Map<String, Object> providedParameters) {
        return getCacheKey(queryString, providedParameters, null, null);
    }

    /**
     * Convert Map into string representation,
     * embrace not null values with "", this helps to distinguish special cases:
     * Empty collection - [], collection with single element empty string - [""], collection with signle element "" - [""""],
     * Collection with null - [null], collection with string "null" - ["null"].
     *
     * @param parameters Map<String, Object>
     * @return String
     */
    protected String getString(Map<String, Object> parameters) {
        StringBuilder map = new StringBuilder().append("{");
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if (map.length() > 1) {
                map.append(", ");
            }
            map.append(entry.getKey()).append("=");
            if (entry.getValue() instanceof Collection) {
                StringBuilder list = new StringBuilder().append("[");
                for (Object value : (Collection) entry.getValue()) {
                    if (list.length() > 1) {
                        list.append(", ");
                    }
                    list.append(getString(value));
                }
                list.append("]");
                map.append(list);
            } else {
                map.append(getString(entry.getValue()));
            }

        }
        return map.append("}").toString();
    }

    protected StringBuilder getString(Object o) {
        StringBuilder sb = new StringBuilder();
        if (o != null) {
            sb.append("\"").append(String.valueOf(o)).append("\"");
        } else {
            sb.append(String.valueOf(o));
        }
        return sb;
    }


    /**
     * Resolve the build-in parameters if any are used in the query.
     * If we still have missing params then return null
     * @return map of actual params resolved
     */
    public Map<String, Object> resolveParameters(String queryString, Map<String, Object> providedParameters) {

        Set<String> queryParameterNames = getParameterNames(queryString, providedParameters);
        // Map is empty, thus we guarantee that no class cast error will occur
        @SuppressWarnings("unchecked")
        Map<String, Object> allParams = new LinkedHashMap<String, Object>(providedParameters != null ? providedParameters : Collections.EMPTY_MAP);

        // The Build-In parameters values have higher priority than provided ones if there are any.
        // Rewriting resolved values...
        Map<String, Object> resolvedBuildInParams = resolveBuiltInParameters(queryParameterNames);
        allParams.putAll(resolvedBuildInParams);

        Map<String, Object> resolvedParams = new LinkedHashMap<String, Object>();
        Set<String> missingQueryParameterNames = new LinkedHashSet<String>();
        for (String parameterName : queryParameterNames) {
            if (allParams.containsKey(parameterName)) {
                resolvedParams.put(parameterName, allParams.get(parameterName));
            } else {
                missingQueryParameterNames.add(parameterName);
            }
        }

        if (log.isDebugEnabled()) {
        	log.debug("provided params: " + providedParameters);
        	log.debug("resolved params: " + resolvedParams);
        	log.debug("resolved build-in params: " + resolvedBuildInParams);
        	log.debug("missing params: " + missingQueryParameterNames);
        }

        // If we still have missing params then return null
        if (missingQueryParameterNames.size() > 0) {
	        resolvedParams = null;
        }
        
        if (log.isDebugEnabled() && missingQueryParameterNames.size() > 0) {
        	log.debug("final resolved params: " + resolvedParams);
        }
        return resolvedParams;
    }

    /**
     * fill in missing param values using the BuiltInParameterProvider impls
     * @param queryParameterNames list of params with missing values
     * @return map of resolved params with values. Empty map if no built-in parameter found
     */
    protected Map<String, Object> resolveBuiltInParameters(Set<String> queryParameterNames) {
        Map<String, Object> resolvedParams = new HashMap<String, Object>();
        if (queryParameterNames.size() > 0) {
            for (Object o : getBuiltInParameterProviders() ) {
                BuiltInParameterProvider pProvider = (BuiltInParameterProvider) o;

                for (String name : queryParameterNames) {
                    Object[] aResult = pProvider.getParameter(null, null, null, name);
                    if (aResult != null) {
                        resolvedParams.put(name, aResult[1]);
                    }
                }
            }
        }
        return resolvedParams;
    }

    private static class Parameter {
        private Set<String> parameterNames = new LinkedHashSet<String>();
        private int startPosition;
        private int endPosition;

        public Set<String> getParameterNames() {
            return parameterNames;
        }

        public void addParameterName(String name) {
            parameterNames.add(name);
        }

        public int getStartPosition() {
            return startPosition;
        }

        public void setStartPosition(int startPosition) {
            this.startPosition = startPosition;
        }

        public int getEndPosition() {
            return endPosition;
        }

        public void setEndPosition(int endPosition) {
            this.endPosition = endPosition;
        }
    }

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.cascade.token.FilterResolver#paramTestNeedsDataSourceInit()
	 */
	public boolean paramTestNeedsDataSourceInit(ReportDataSource dataSource) {
		return false;
	}

    @Override
    public LinkedHashSet<String> resolveCascadingOrder(Map<String, Set<String>> masterDependencies) {
        Deque<String> orderedNames = new LinkedList<String>();
        Queue<String> workingQueue = new LinkedList<String>(masterDependencies.keySet());
        int maxIterations =  (masterDependencies.size() * (masterDependencies.size() + 1)) / 2 + 1;
        while (workingQueue.size() > 0 && maxIterations-- > 0) {
            String currentName = workingQueue.remove();

            Set<String> masterDependency = masterDependencies.get(currentName);
            if (masterDependency == null || masterDependency.isEmpty()) {
                orderedNames.addFirst(currentName);
            } else {
                if (orderedNames.containsAll(masterDependency)) {
                    orderedNames.addLast(currentName);
                } else {
                    workingQueue.add(currentName);
                }
            }
        }
        if (maxIterations > 0) {
            return new LinkedHashSet<String>(orderedNames);
        } else {
            throw new JSException("Order cannot be resolved because of circular or non-existing dependencies.");
        }
    }
}
