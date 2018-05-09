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

package com.jaspersoft.jasperserver.remote.discovery;

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.discovery.VisualizationMetadata;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.remote.services.DiscoveryService;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import org.springframework.stereotype.Service;

/**
 * <p>Performs discovery of different abilities of resources</p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: $
 */

@Service("discoveryService")
public class DiscoveryServiceImpl implements DiscoveryService {
    @javax.annotation.Resource
    private DiscoveryStrategyProvider provider;

    @javax.annotation.Resource(name = "concreteRepository")
    protected RepositoryService repositoryService;

    @javax.annotation.Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    @Override
    public VisualizationMetadata discover(String uri) {
        Resource resource = repositoryService.getResource(ExecutionContextImpl.getRuntimeExecutionContext(), uri);

        if (resource != null){
            DiscoveryStrategy strategy = provider.getDiscoveryStrategyFor(resource);
            VisualizationMetadata res = new VisualizationMetadata();

            res.setParameters(strategy.discoverParameters(resource));
            res.setOutputParameters(strategy.discoverOutputParameters(resource));

            res.setRepositoryType(toClientResourceType(resource.getClass()));

            return res;
        }

        throw new ResourceNotFoundException(uri);
    }

    protected String toClientResourceType(Class serverResourceType){
        String serverResourceTypeString = "";

        if (serverResourceType.getInterfaces() != null && serverResourceType.getInterfaces().length > 0){
            for (Class interf : serverResourceType.getInterfaces()){
                if (Resource.class.isAssignableFrom(interf)){
                    serverResourceTypeString = interf.getName();
                }
            }
        } else {
            serverResourceTypeString = serverResourceType.getName();
        }

        final ToClientConverter typeProcessor = genericTypeProcessorRegistry.getTypeProcessor(serverResourceTypeString, ToClientConverter.class, false);
        return typeProcessor != null ? typeProcessor.getClientResourceType() : "unknown";
    }
}
