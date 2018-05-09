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
package com.jaspersoft.jasperserver.remote;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

/**
 * This is the default implementation of the Handler Registry. It uses a simple map
 * to bind a resource handler with resource types using the wsType.
 *
 * @author Giulio Toffoli (giulio@jaspersoft.com)
 * @version $Id$
 */
public class MapResourceHandlerRegistry implements ResourceHandlerRegistry, InitializingBean{

    private Map handlers;
    private Map resourceTypes;
    private ResourceActionResolver resourceActionResolver;

    public void setResourceActionResolver(ResourceActionResolver resourceActionResolver) {
        this.resourceActionResolver = resourceActionResolver;
    }

    public void afterPropertiesSet() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        resourceTypes = new HashMap();

        for (Iterator it = handlers.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String wsType = (String) entry.getKey();
            ResourceHandler handler = (ResourceHandler) entry.getValue();

            Class resourceType = handler.getResourceType();
            resourceTypes.put(resourceType.getName(), wsType);

            ResourceResolverAdapter adapter = (ResourceResolverAdapter)handler;
            adapter.setResourceActionHandler(resourceActionResolver);
        }
    }

    public ResourceHandler getHandler(String wsType) {
        return (ResourceHandler) handlers.get(wsType);
    }

    public ResourceHandler getHandler(Resource resource) {
        String resourceType = resource.getResourceType();
        String wsType = (String) resourceTypes.get(resourceType);
        if (wsType == null) {
            synchronized (resourceTypes) {
                //determine a (more generic) type for the resource
                wsType = determineWSType(resource);
                //cache the type
                resourceTypes.put(resourceType, wsType);
            }
        }
        return getHandler(wsType);
    }

    protected String determineWSType(Resource resource) {
        String wsType = null;

        LinkedList itfQueue = new LinkedList();
        Class[] itfs = resource.getClass().getInterfaces();
        itfQueue.addAll(Arrays.asList(itfs));

        while (!itfQueue.isEmpty()) {
            Class itf = (Class) itfQueue.removeFirst();
            wsType = (String) resourceTypes.get(itf.getName());
            if (wsType != null) {
                break;
            }

            Class[] interfaces = itf.getInterfaces();
            itfQueue.addAll(Arrays.asList(interfaces));
        }

        return wsType;
    }

//    public boolean typeExtends(String type, String parentType) {
//        ResourceHandler handler = (ResourceHandler) handlers.get(type);
//        Class resourceType = handler.getResourceType();
//        ResourceHandler parentHandler = (ResourceHandler) handlers.get(parentType);
//        Class parentResourceType = parentHandler.getResourceType();
//        return parentResourceType.isAssignableFrom(resourceType);
//    }


    public Map getHandlers() {
        return handlers;
    }

    public void setHandlers(Map handlers) {
        this.handlers = handlers;
    }
}
