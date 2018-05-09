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
package com.jaspersoft.jasperserver.jaxrs.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jaspersoft.jasperserver.dto.bridge.BridgeRegistry;
import com.jaspersoft.jasperserver.dto.bridge.SettingsBridge;
import com.jaspersoft.jasperserver.jaxrs.common.JacksonMapperContextResolver;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.settings.SettingsProvider;
import com.jayway.jsonpath.JsonPath;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Component
@Path("/settings")
@Scope("prototype")
public class SettingsJaxrsService implements SettingsBridge {
    @Resource
    private Map<String, Object> settingsGroups;
    @Resource
    private JacksonMapperContextResolver jacksonMapperContextResolver;

    @GET
    @Path("/{groupName}")
    @Produces("application/json")
    public Response getSettingsGroup(@PathParam("groupName") String groupName) {
        return Response.ok(getSettingsGroupJson(groupName)).build();
    }

    protected String getSettingsGroupJson(String groupName){
        Object settingsGroup = settingsGroups.get(groupName);
        if(settingsGroup instanceof SettingsProvider){
            settingsGroup = ((SettingsProvider) settingsGroup).getSettings();
        }
        if (settingsGroup == null) {
            throw new ResourceNotFoundException(groupName);
        }
        try {
            return jacksonMapperContextResolver.getContext(settingsGroup.getClass()).writer().writeValueAsString(settingsGroup);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to provide settings group JSON. Group name [" + groupName + "]", e);
        }
    }

    @PostConstruct
    public void registerSettingsBridge(){
        BridgeRegistry.registerBridge(SettingsBridge.class, this);
    }

    @Override
    public <T> T getSetting(String groupName, String path) {
        final String groupJson = getSettingsGroupJson(groupName);
        return JsonPath.read(groupJson, path);
    }
}
