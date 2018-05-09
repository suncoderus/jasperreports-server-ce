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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.dto.discovery.Parameter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>Factory for different discovery strategies</p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: $
 */

@Component
public class DiscoveryStrategyProvider implements ApplicationContextAware{
    private ApplicationContext context;
    private volatile List<DiscoveryStrategy> strategies;

    private final DiscoveryStrategy defaultStrategy = new DefaultStrategy();

    public DiscoveryStrategy getDiscoveryStrategyFor(Resource resource){
        DiscoveryStrategy res = defaultStrategy;
        if (strategies == null){
            initializeBeans();
        }

        for (DiscoveryStrategy strategy : strategies){
            if (strategy.getSupportedResourceType().isAssignableFrom(resource.getClass())){
                if (res.getSupportedResourceType().isAssignableFrom(strategy.getSupportedResourceType())){
                    res = strategy;
                }
            }
        }

        return res;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    private synchronized void initializeBeans(){
        if (strategies == null){
            Map<String, DiscoveryStrategy> rawStrategies = context.getBeansOfType(DiscoveryStrategy.class);
            Map<Class<Resource>, DiscoveryStrategy> sortmap = new HashMap<Class<Resource>, DiscoveryStrategy>();

            for (DiscoveryStrategy strategy :rawStrategies.values()){
                if (sortmap.containsKey(strategy.getSupportedResourceType())){
                    if (sortmap.get(strategy.getSupportedResourceType()).getClass().isAssignableFrom(strategy.getClass())){
                        sortmap.put(strategy.getSupportedResourceType(), strategy);
                    }
                } else {
                    sortmap.put(strategy.getSupportedResourceType(), strategy);
                }
            }

            this.strategies = new ArrayList<DiscoveryStrategy>(sortmap.values());
        }
    }

    private class DefaultStrategy implements DiscoveryStrategy<Resource>{
        @Override
        public List<Parameter> discoverParameters(Resource resource) {
            return Collections.EMPTY_LIST;
        }

        @Override
        public List<Parameter> discoverOutputParameters(Resource resource) {
            return Collections.EMPTY_LIST;
        }

        @Override
        public Class<Resource> getSupportedResourceType() {
            return Resource.class;
        }
    }
}
