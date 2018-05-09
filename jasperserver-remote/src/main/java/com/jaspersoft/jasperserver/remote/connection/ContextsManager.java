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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import com.jaspersoft.jasperserver.remote.common.JrsBeanValidator;
import com.jaspersoft.jasperserver.remote.connection.storage.ConnectionDataPair;
import com.jaspersoft.jasperserver.remote.connection.storage.ConnectionDataStorage;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.UnsupportedOperationRemoteException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConverter;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.war.helper.GenericParametersHelper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class ContextsManager {
    @Resource
    private List<ContextQueryExecutor> queryExecutors;
    private Map<String, Class<?>> queryTypeToClassMapping;
    private Map<Class<?>, List<ContextQueryExecutor>> queryClassToQueryExecutorsMapping;
    @Resource
    private List<ContextParametrizedMetadataBuilder> parametrizedMetadataBuilders;
    private Map<String, Class<?>> metadataParamsTypeToClassMapping;
    private Map<String, Class<?>> typeToClassMapping;
    @Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;
    @Resource
    private ProfileAttributesResolver profileAttributesResolver;
    @Resource
    private ConnectionDataStorage connectionDataStorage;
    @Resource
    private JrsBeanValidator jrsBeanValidator;

    public Class<?> getConnectionDescriptionClass(String connectionType) {
        return typeToClassMapping.get(connectionType != null ? connectionType.toLowerCase() : null);
    }

    public Class<?> getQueryClass(String queryType) {
        return queryTypeToClassMapping.get(queryType != null ? queryType.toLowerCase() : null);
    }

    public Class<?> getMetadataParamsClass(String paramsType) {
        return metadataParamsTypeToClassMapping.get(paramsType != null ? paramsType.toLowerCase() : null);
    }

    public UUID createConnection(Object connectionDescription) throws IllegalParameterValueException {
        if (connectionDescription == null) {
            throw new MandatoryParameterNotFoundException("body");
        }
        jrsBeanValidator.validate(connectionDescription);
        final Map<String, Object> data = new HashMap<String, Object>();
        // generic type processor registry assures safety of unchecked assignment
        @SuppressWarnings("unchecked")
        final ContextValidator<Object> validator = genericTypeProcessorRegistry.getTypeProcessor(connectionDescription.getClass(), ContextValidator.class, false);
        if (validator != null) {
            validator.validate(connectionDescription);
        }
        final ContextManagementStrategy<Object, Object> strategy = getStrategy(connectionDescription);
        final Object context = strategy.createContext(connectionDescription, data);
        return connectionDataStorage.save(new ConnectionDataPair(context, data));
    }

    public boolean isMetadataSupported(Object connectionDescription, String metadataClientType) {
        final ContextMetadataBuilder<?> metadataBuilder = getMetadataBuilder(connectionDescription);
        boolean result = false;
        if (metadataBuilder instanceof GenericTypeMetadataBuilder) {
            // some connection strategies serves data sources of multiple types, described by generic model.
            final GenericTypeMetadataBuilder genericTypeMetadataBuilder = (GenericTypeMetadataBuilder) metadataBuilder;
            try {
                result = genericTypeMetadataBuilder.isMetadataSupported(connectionDescription, metadataClientType);
            } catch (ClassCastException e) {
                final ToServerConverter typeProcessor = genericTypeProcessorRegistry
                        .getTypeProcessor(connectionDescription.getClass(), ToServerConverter.class, false);
                if (typeProcessor != null) {
                    // it's a case with internal representation of resource. Let's try to convert it and check
                    // if metadata is supported
                    try {
                        result = genericTypeMetadataBuilder.isMetadataSupported(
                                typeProcessor.toServer(connectionDescription, null), metadataClientType);
                    } catch (Exception ex) {
                        // do nothing. Don't support this case
                    }
                }
            }
        } else if(metadataBuilder != null) {
            result = extractMetadataClientResourceType(metadataBuilder, connectionDescription)
                    .equalsIgnoreCase(metadataClientType);
        }
        return result;
    }

    protected ContextMetadataBuilder<?> getMetadataBuilder(Object connectionDescription) {
        final Class<?> contextDescriptionClass = connectionDescription.getClass();
        final ContextManagementStrategy contextManagementStrategy = genericTypeProcessorRegistry.getTypeProcessor(contextDescriptionClass, ContextManagementStrategy.class);
        final Class<?> internalContextType = GenericParametersHelper
                .getGenericTypeArgument(contextManagementStrategy.getClass(), ContextManagementStrategy.class, 1);
        ContextMetadataBuilder metadataBuilder = genericTypeProcessorRegistry.getTypeProcessor(internalContextType,
                ContextMetadataBuilder.class, false);
        if (metadataBuilder == null && contextDescriptionClass != internalContextType) {
            if (internalContextType.isAssignableFrom(contextDescriptionClass)) {
                // real context class is a subtype of internal context type. It's possible if strategy handles multiple types.
                // Let's try to find metadata builder for context class
                metadataBuilder = genericTypeProcessorRegistry.getTypeProcessor(contextDescriptionClass,
                        ContextMetadataBuilder.class, false);
            } else if (contextManagementStrategy instanceof GenericTypeContextStrategy) {
                // context strategy converts external type to internal. Let's ask it for concrete internal type
                final Class concreteInternalType = ((GenericTypeContextStrategy) contextManagementStrategy)
                        .getConcreteInternalType(connectionDescription);
                metadataBuilder = genericTypeProcessorRegistry.getTypeProcessor(concreteInternalType,
                        ContextMetadataBuilder.class, false);
            }
        }
        return metadataBuilder;
    }

    public String getMetadataClientResourceType(Object connectionDescription) {
        final ContextMetadataBuilder<?> metadataBuilder = getMetadataBuilder(connectionDescription);
        String result = null;
        if (metadataBuilder instanceof GenericTypeMetadataBuilder) {
            // some connection strategies serves data sources of multiple types, described by generic model.
            final GenericTypeMetadataBuilder genericTypeMetadataBuilder = (GenericTypeMetadataBuilder) metadataBuilder;
            try {
                result = genericTypeMetadataBuilder.getMetadataClientResourceType(connectionDescription);
            } catch (ClassCastException e) {
                final ToServerConverter typeProcessor = genericTypeProcessorRegistry
                        .getTypeProcessor(connectionDescription.getClass(), ToServerConverter.class, false);
                if (typeProcessor != null) {
                    // it's a case with internal representation of resource. Let's try to convert it
                    // and get metadata client type
                    try {
                        result = genericTypeMetadataBuilder.getMetadataClientResourceType(typeProcessor.toServer(connectionDescription, null));
                    } catch (Exception ex) {
                        // do nothing. Don't support this case
                    }
                }
            }
        }
        return result != null ? result : extractMetadataClientResourceType(metadataBuilder, connectionDescription);
    }

    protected String extractMetadataClientResourceType(ContextMetadataBuilder<?> metadataBuilder,
            Object connectionDescription) {
        String result = null;
        final ClientResourceType clientResourceTypeAnnotation;
        try {
            clientResourceTypeAnnotation = metadataBuilder.getClass().getMethod("build",
                    Object.class, Map.class, Map.class)
                    .getAnnotation(ClientResourceType.class);
        } catch (NoSuchMethodException e) {
            // may not happen
            throw new IllegalStateException("no build method in ContextMetadataBuilder.");
        }
        if (clientResourceTypeAnnotation != null) {
            result = clientResourceTypeAnnotation.value();
        }
        return result != null ? result :
                "repository." + ClientTypeHelper.extractClientType(connectionDescription.getClass()) + ".metadata";
    }

    public Object getConnection(UUID uuid) throws ResourceNotFoundException {
        final ConnectionDataPair pair = connectionDataStorage.get(uuid);
        return pair != null ? getStrategy(pair.getConnection())
                .getContextForClient(pair.getConnection(), pair.getData()) : null;
    }

    public void removeConnection(UUID uuid) throws ResourceNotFoundException {
        final ConnectionDataPair pair = connectionDataStorage.get(uuid);
        if (pair != null) {
            getStrategy(pair.getConnection()).deleteContext(pair.getConnection(), pair.getData());
            connectionDataStorage.delete(uuid);
        }
    }

    // generic processor registry assures safety of call
    @SuppressWarnings("unchecked")
    public Object getConnectionMetadata(UUID uuid, Map<String, String[]> options) throws ResourceNotFoundException, UnsupportedOperationRemoteException {
        final ConnectionDataPair pair = connectionDataStorage.get(uuid);
        final Object connection = pair.getConnection();
        ContextMetadataBuilder typeProcessor = genericTypeProcessorRegistry.getTypeProcessor(connection.getClass(), ContextMetadataBuilder.class, false);
        if (typeProcessor == null && connection instanceof com.jaspersoft.jasperserver.api.metadata.common.domain.Resource) {
            typeProcessor = genericTypeProcessorRegistry
                    .getTypeProcessor(
                            ((com.jaspersoft.jasperserver.api.metadata.common.domain.Resource) connection)
                                    .getResourceType(),
                            ContextMetadataBuilder.class, false);
        }
        if (typeProcessor == null) {
            throw new UnsupportedOperationRemoteException(ClientTypeHelper.extractClientType(connection.getClass()) + "/metadata");
        }
        resolveAttributes(options);
        return typeProcessor.build(profileAttributesResolver.mergeObject(connection, uuid.toString()),
                options, pair.getData());
    }

    public Object getConnectionMetadata(UUID uuid, Object metadataParams) {
        final ConnectionDataPair pair = connectionDataStorage.get(uuid);
        final Object connection = pair.getConnection();
        final ContextParametrizedMetadataBuilder typeProcessor = genericTypeProcessorRegistry
                .getTypeProcessor(connection.getClass(), ContextParametrizedMetadataBuilder.class, false);
        if (typeProcessor == null) {
            throw new UnsupportedOperationRemoteException(ClientTypeHelper.extractClientType(connection.getClass()) + "/metadata");
        }
        return typeProcessor.build(profileAttributesResolver.mergeObject(connection, uuid.toString()),
                profileAttributesResolver.mergeObject(metadataParams, uuid.toString()), pair.getData());
    }

    private void resolveAttributes(Map<String, String[]> options) {
        if (options != null && options.size() > 0) {
            for (String[] option : options.values()) {
                if (option != null && option.length > 0) {
                    for (int i = 0; i < option.length; i++) {
                        String optionValue = option[i];
                        if (profileAttributesResolver.containsAttribute(optionValue)) {
                            option[i] = profileAttributesResolver.merge(optionValue, null);
                        }
                    }
                }
            }
        }
    }

    public Object executeQuery(UUID uuid, Object query, Map<String, String[]> queryParameters) {
        jrsBeanValidator.validate(query);
        final ConnectionDataPair connectionDataPair = connectionDataStorage.get(uuid);
        final Object connection = connectionDataPair.getConnection();
        ContextQueryExecutor<Object, Object> queryExecutor = getQueryExecutor(query, connection);
        return queryExecutor.executeQuery(query, connection, queryParameters, connectionDataPair.getData());
    }

    public Object executeQueryForMetadata(UUID uuid, Object query) {
        jrsBeanValidator.validate(query);
        final ConnectionDataPair connectionDataPair = connectionDataStorage.get(uuid);
        final Object connection = connectionDataPair.getConnection();
        ContextQueryExecutor<Object, Object> queryExecutor = getQueryExecutor(query, connection);
        return queryExecutor.executeQueryForMetadata(query, connection, connectionDataPair.getData());
    }

    protected ContextQueryExecutor<Object, Object> getQueryExecutor(Object query, Object connection) {
        ContextQueryExecutor<Object, Object> queryExecutor = null;
        final ContextManagementStrategy<Object, Object> strategy = getStrategy(connection);
        if (strategy instanceof ContextQueryExecutor) {
            queryExecutor = (ContextQueryExecutor<Object, Object>) strategy;
        } else {
            final List<ContextQueryExecutor> queryExecutorList = queryClassToQueryExecutorsMapping.get(query.getClass());
            if (queryExecutorList != null) {
                for (ContextQueryExecutor currentQueryExecutor : queryExecutorList) {
                    final Class<?> connectionClass = GenericParametersHelper
                            .getGenericTypeArgument(currentQueryExecutor.getClass(), ContextQueryExecutor.class, 1);
                    if (connectionClass.isAssignableFrom(connection.getClass())) {
                        queryExecutor = currentQueryExecutor;
                        break;
                    }
                }
            }
        }
        if (queryExecutor == null) {
            throw new UnsupportedOperationRemoteException(ClientTypeHelper.extractClientType(connection.getClass()));
        }
        return queryExecutor;
    }

    // initialization code in afterPropertiesSet() ensures cast safety in this case.
    @SuppressWarnings("unchecked")
    protected <T> ContextManagementStrategy<T, Object> getStrategy(T object) {
        return (ContextManagementStrategy<T, Object>) genericTypeProcessorRegistry.getTypeProcessor(object.getClass(),
                ContextManagementStrategy.class, false);
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        Map<String, Class<?>> typeToClassMap = new HashMap<String, Class<?>>();
        final Set<Class<?>> connectionDescriptionClasses = genericTypeProcessorRegistry
                .getProcessorMapping(ContextManagementStrategy.class).keySet();
        for (Class<?> connectionDescriptionClass : connectionDescriptionClasses) {
            typeToClassMap.put(ClientTypeHelper.extractClientType(connectionDescriptionClass).toLowerCase(), connectionDescriptionClass);
        }
        typeToClassMapping = Collections.unmodifiableMap(typeToClassMap);
        if (queryExecutors != null) {
            Map<String, Class<?>> queryTypeToClassMap = new HashMap<String, Class<?>>();
            Map<Class<?>, List<ContextQueryExecutor>> queryClassToQueryExecutorMap = new HashMap<Class<?>, List<ContextQueryExecutor>>();
            for (ContextQueryExecutor queryExecutor : queryExecutors) {
                final Class<?> queryClass = GenericParametersHelper.getGenericTypeArgument(queryExecutor.getClass(),
                        ContextQueryExecutor.class, 0);
                final String queryType = ClientTypeHelper.extractClientType(queryClass).toLowerCase();
                queryTypeToClassMap.put(queryType, queryClass);
                List<ContextQueryExecutor> executorsForClass = queryClassToQueryExecutorMap.get(queryClass);
                if (executorsForClass == null) {
                    executorsForClass = new ArrayList<ContextQueryExecutor>();
                    queryClassToQueryExecutorMap.put(queryClass, executorsForClass);
                }
                executorsForClass.add(queryExecutor);
            }
            queryTypeToClassMapping = Collections.unmodifiableMap(queryTypeToClassMap);
            for (Class<?> clazz : queryClassToQueryExecutorMap.keySet()) {
                queryClassToQueryExecutorMap.put(clazz, Collections.unmodifiableList(queryClassToQueryExecutorMap.get(clazz)));
            }
            queryClassToQueryExecutorsMapping = Collections.unmodifiableMap(queryClassToQueryExecutorMap);
        }
    }

    @PostConstruct
    public void initMetadataParamsTypes() {
        Map<String, Class<?>> metadataParamsMapping = new HashMap<String, Class<?>>();
        if (parametrizedMetadataBuilders != null) {
            for (ContextParametrizedMetadataBuilder parametrizedMetadataBuilder : parametrizedMetadataBuilders) {
                final Class<?> metadataOptionsClass = GenericParametersHelper
                        .getGenericTypeArgument(parametrizedMetadataBuilder.getClass(),
                                ContextParametrizedMetadataBuilder.class, 1);
                metadataParamsMapping
                        .put(ClientTypeHelper.extractClientType(metadataOptionsClass).toLowerCase(), metadataOptionsClass);
            }
        }
        metadataParamsTypeToClassMapping = Collections.unmodifiableMap(metadataParamsMapping);
    }
}
