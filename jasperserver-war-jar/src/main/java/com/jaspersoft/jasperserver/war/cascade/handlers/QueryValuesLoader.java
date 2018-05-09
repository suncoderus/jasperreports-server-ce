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
package com.jaspersoft.jasperserver.war.cascade.handlers;

import com.jaspersoft.jasperserver.api.JSMissingDataSourceFieldsException;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.EhcacheEngineService;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.war.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.handlers.converters.DataConverterService;
import com.jaspersoft.jasperserver.war.cascade.token.FilterResolver;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class QueryValuesLoader implements ValuesLoader {

    public static final String COLUMN_VALUE_SEPARATOR = " | ";

    private static final Log log = LogFactory.getLog(QueryValuesLoader.class);

    @javax.annotation.Resource
    protected FilterResolver filterResolver;
    @javax.annotation.Resource
    protected CachedRepositoryService cachedRepositoryService;
    @javax.annotation.Resource
    protected CachedEngineService cachedEngineService;
    @javax.annotation.Resource
    protected EngineService engineService;
    @javax.annotation.Resource
    private DataConverterService dataConverterService;
    @javax.annotation.Resource
    private AuditContext concreteAuditContext;

    @Override
    public List<ListOfValuesItem> loadValues(InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, Map<String, Class<?>> parameterTypes, ReportInputControlInformation info) throws CascadeResourceNotFoundException {
        createInputControlsAuditEvent(inputControl.getURIString(), parameters);

        List<ListOfValuesItem> result = null;
        ResourceReference dataSourceForQuery = resolveDatasource(inputControl, dataSource);
        final Query query = cachedRepositoryService.getResource(Query.class, inputControl.getQuery());

        Map<String, Object> domainSchemaParameters = new HashMap<String, Object>();

        //TODO Extract this parameter extension to separate interface
        prepareDomainDataSource(dataSourceForQuery, domainSchemaParameters);

        Map<String, Object> executionParameters = filterAndFillMissingQueryParameters(query, parameters, domainSchemaParameters);
        Map<String, Class<?>> executionParameterTypes = filterParameterTypes(executionParameters.keySet(), parameterTypes);

        if (parameters!=null&&parameters.containsKey(EhcacheEngineService.IC_REFRESH_KEY)) {
        	executionParameters.put(EhcacheEngineService.IC_REFRESH_KEY,"true");
        }
        if (parameters!=null&&parameters.containsKey(EhcacheEngineService.DIAGNOSTIC_REPORT_URI)) {
            executionParameters.put(EhcacheEngineService.DIAGNOSTIC_REPORT_URI, parameters.get(EhcacheEngineService.DIAGNOSTIC_REPORT_URI));
        }
        if (parameters!=null&&parameters.containsKey(EhcacheEngineService.DIAGNOSTIC_STATE)) {
            executionParameters.put(EhcacheEngineService.DIAGNOSTIC_STATE, parameters.get(EhcacheEngineService.DIAGNOSTIC_STATE));
        }
        
        /* Typed results are returned */
        OrderedMap results = null;
        try {
            results = cachedEngineService.executeQuery(
                    ExecutionContextImpl.getRuntimeExecutionContext(), inputControl.getQuery(),
                    inputControl.getQueryValueColumn(), inputControl.getQueryVisibleColumns(),
                    dataSourceForQuery, executionParameters, executionParameterTypes, inputControl.getName());
        } catch (JSMissingDataSourceFieldsException e) {
            log.debug(e.getMessage(), e);
            // This occurs when a field previously found in the domain is missing
            // we ignore here as we do not need these values, and an error is rendered on the report canvas
        } catch (IllegalArgumentException e) {
            log.debug(e.getMessage(), e);
            // This occurs when a field previously found in the domain is missing
            // we ignore here as we do not need these values, and an error is rendered on the report canvas
        }

        if (results != null) {
            OrderedMapIterator it = results.orderedMapIterator();
            while (it.hasNext()) {
                if (result == null)
                    result = new ArrayList<ListOfValuesItem>(results.size());
                Object valueColumn = it.next();
                Object[] visibleColumns = (Object[]) it.getValue();

                StringBuilder label = new StringBuilder();
                for (int i = 0; i < visibleColumns.length; i++) {
                    Object visibleColumn = visibleColumns[i];
                    String visibleColumnName = inputControl.getQueryVisibleColumns()[i];
                    boolean isVisibleColumnMatchesValueColumn = inputControl.getQueryValueColumn().equals(visibleColumnName);

                    if (label.length() > 0) {
                        label.append(COLUMN_VALUE_SEPARATOR);
                    }

                    String formattedValue = formatValueToString(visibleColumn, isVisibleColumnMatchesValueColumn, inputControl, info);
                    label.append(visibleColumn != null ? formattedValue : InputControlHandler.NULL_SUBSTITUTION_LABEL);
                }
                ListOfValuesItem item = new ListOfValuesItemImpl();
                item.setLabel(label.toString());
                if(valueColumn instanceof BigDecimal) {
                    valueColumn = ((BigDecimal) valueColumn).stripTrailingZeros();
                }
                item.setValue(valueColumn);
                result.add(item);
            }
        }

        closeInputControlsAuditEvent();

        return result;
    }

    private String formatValueToString(Object visibleColumn, boolean isVisibleColumnMatchesValueColumn,
       InputControl inputControl, ReportInputControlInformation info)
            throws CascadeResourceNotFoundException {

        if (isVisibleColumnMatchesValueColumn) {
            return dataConverterService.formatSingleValue(visibleColumn, inputControl, info);
        } else {
            return dataConverterService.formatSingleValue(visibleColumn, (InputControl) null, null);
        }
    }

    /**
     * DomainFilterResolver needs access to the domain schema, which it can get from
     * the param map. FilterCore doesn't need this, and it would allocate a connection
     * that's not needed.
     */
    protected void prepareDomainDataSource(ResourceReference dataSourceRef, Map<String, Object> parameters) throws CascadeResourceNotFoundException {
    	ReportDataSource dataSource = (ReportDataSource) cachedRepositoryService.getResource(Resource.class, dataSourceRef);
        if (filterResolver.paramTestNeedsDataSourceInit(dataSource)) {
            parameters.putAll(cachedEngineService.getSLParameters(dataSource));
        }
    }

    /**
     * Create new Map with specified parameters, add only those which are used in query, find missing parameters and assign value Null to them.
     * This is done because we indicate Nothing selection in single select control as absence of parameter in map,
     * but QueryManipulator sets query to empty string and Executor throws exception if parameter is absent,
     * so we set Null as most suitable value as yet.
     *
     * @param query Query
     * @param parameters Map&lt;String, Object&gt;
     * @return Map&lt;String, Object&gt; copy of map, where missing parameters filled with Null values.
     */
    protected Map<String, Object> filterAndFillMissingQueryParameters(Query query, Map<String, Object> parameters, Map<String, Object> domainSchemaParameters) {
        HashMap<String, Object> parametersWithSchema = new HashMap<String, Object>();
        parametersWithSchema.putAll(parameters);
        parametersWithSchema.putAll(domainSchemaParameters);
        Set<String> queryParameterNames = filterResolver.getParameterNames(query.getSql(), parametersWithSchema);
        HashMap<String, Object> resolvedParameters = new HashMap<String, Object>();
        for (String queryParameterName : queryParameterNames) {
            // If parameter is missing, set Null.
            resolvedParameters.put(queryParameterName, parameters.get(queryParameterName));
        }
        resolvedParameters.putAll(domainSchemaParameters);
        return resolvedParameters;
    }

    /**
     * Filter only specified parameter types
     * @param parameters Parameter names
     * @param parameterTypes Map of parameter types
     * @return Filtered map of parameter types
     */
    protected Map<String, Class<?>> filterParameterTypes(Set<String> parameters, Map<String, Class<?>> parameterTypes) {
        Map<String, Class<?>> filteredParameterTypes = new HashMap<String, Class<?>>(parameters.size());
        for (String parameterName : parameters) {
            if (parameterTypes.containsKey(parameterName)) {
                filteredParameterTypes.put(parameterName, parameterTypes.get(parameterName));
            }
        }
        return filteredParameterTypes;
    }

    @Override
    public Set<String> getMasterDependencies(InputControl inputControl, ResourceReference dataSource) throws CascadeResourceNotFoundException {
        Map<String, Object> filterParameters = new HashMap<String, Object>();
        ResourceReference dataSourceForQuery = resolveDatasource(inputControl, dataSource);
        prepareDomainDataSource(dataSourceForQuery, filterParameters);
        Query query = cachedRepositoryService.getResource(Query.class, inputControl.getQuery());
        String querySQL = query.getSql();
        return filterResolver.getParameterNames(querySQL, filterParameters);
    }

    protected ResourceReference resolveDatasource(InputControl inputControl, ResourceReference reportDatasource) throws CascadeResourceNotFoundException {
        ResourceReference queryReference = inputControl.getQuery();
        ResourceReference resolvedDatasource = reportDatasource;
        if (queryReference != null) {
            Resource queryResource = cachedRepositoryService.getResource(Resource.class, queryReference);
            if (queryResource instanceof Query && ((Query) queryResource).getDataSource() != null) {
                resolvedDatasource = ((Query) queryResource).getDataSource();
            }
        }
        return resolvedDatasource;
    }

    protected void createInputControlsAuditEvent(final String resourceUri, final Map<String, Object> parameters) {
        concreteAuditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                AuditEvent event = concreteAuditContext.createAuditEvent("inputControlsQuery");
                if (event.getResourceUri() == null) {
                    event.setResourceUri(resourceUri);
                }

                for (Map.Entry<String, Object> entry: parameters.entrySet()) {
                    if (entry.getKey() != null) {
                        concreteAuditContext.addPropertyToAuditEvent("inputControlParam", entry, event);
                    }
                }

            }
        });
    }

    protected void closeInputControlsAuditEvent() {
        concreteAuditContext.doInAuditContext("inputControlsQuery", new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                concreteAuditContext.closeAuditEvent(auditEvent);
            }
        });
    }
}
