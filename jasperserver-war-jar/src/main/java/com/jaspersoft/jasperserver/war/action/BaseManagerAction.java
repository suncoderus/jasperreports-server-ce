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

package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.service.RoleManagerService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.ContextLoader;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Base manager action class.
 *
 * @author schubar
 * @author Yuriy Plakosh
 */

abstract public class BaseManagerAction extends BaseFormAction {

    /**
     * State class keeps and manages manager's state.
     */
    public class State implements Serializable{
        // JSON object names.
        private static final String TEXT = "text";
        private static final String TENANT_ID = "tenantId";
        private static final String SELECTED_ENTITY = "selectedEntity";
        private static final String AVAILABLE_TEXT = "availableText";
        private static final String ASSIGNED_TEXT = "assignedText";

        private String tenantId;
        private EntitiesListState entitiesState = new EntitiesListState();
        private String selectedEntity;

        private EntitiesListState availableEntitiesState = new EntitiesListState();
        private EntitiesListState assignedEntitiesState = new EntitiesListState();

        public String getTenantId() {
            return tenantId;
        }

        public void updateTenantId(String tenantId) {
            this.tenantId = tenantId;

            entitiesState.updateResultState(0, 0);
        }

        public EntitiesListState getEntitiesState() {
            return entitiesState;
        }

        public String getSelectedEntity() {
            return selectedEntity;
        }

        public void updateSelectedEntity(String selectedEntity) {
            this.selectedEntity = selectedEntity;

            availableEntitiesState.updateResultState(0, 0);
            assignedEntitiesState.updateResultState(0, 0);
        }

        public EntitiesListState getAvailableEntitiesState() {
            return availableEntitiesState;
        }

        public EntitiesListState getAssignedEntitiesState() {
            return assignedEntitiesState;
        }

        public JSONObject toJson() throws JSONException {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(TEXT, entitiesState.getText());
            jsonObject.put(TENANT_ID, tenantId);
            jsonObject.put(SELECTED_ENTITY, selectedEntity);
            jsonObject.put(AVAILABLE_TEXT, availableEntitiesState.getText());
            jsonObject.put(ASSIGNED_TEXT, assignedEntitiesState.getText());

            return jsonObject;
        }
    }

    // Flow attributes.
    protected static final String FLOW_ATTRIBUTE_STATE = "state";
    protected static final String FLOW_ATTRIBUTE_CURRENT_USER = "signedUser";
    protected static final String FLOW_ATTRIBUTE_CURRENT_USER_ROLES = "currentUserRoles";
    protected static final String FLOW_ATTRIBUTE_DEFAULT_ENTITY = "defaultEntity";

    protected static final String FLOW_ATTRIBUTE_CONFIGURATION = "configuration";
    // Request parameters.
    protected static final String PARAMETER_TENANT_ID = "tenantId";
    protected static final String PARAMETER_TEXT = "text";
    protected static final String PARAMETER_ENTITY = "entity";
    protected static final String PARAMETER_ENTITY_NAME = "entityName";
    protected static final String PARAMETER_ENTITIES = "entities";
    protected static final String PARAMETER_ASSIGNED_ENTITIES = "assignedEntities";
    protected static final String PARAMETER_UNASSIGNED_ENTITIES = "unassignedEntities";

    // JSON attribute.
    protected static final String JSON_ATTRIBUTE_SUPERUSER_ROLE = "superuserRole";
    protected static final String JSON_ATTRIBUTE_ADMIN_ROLE = "adminRole";
    protected static final String JSON_TENANT_URI = "tenantUri";

    // Role constants.
    protected static final String ROLE_SUPERUSER = "ROLE_SUPERUSER";
    protected static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";

    protected ConfigurationBean configuration;
    protected UserAuthorityService userService;

    public void setConfiguration(ConfigurationBean configurationBean) {
        this.configuration = configurationBean;
    }

    public Map getSession(RequestContext context) {
        return context.getExternalContext().getSessionMap().asMap();
    }

    public void setUserService(UserAuthorityService userService) {
        this.userService = userService;
    }

    @Override
    protected void initState(RequestContext context) {
        try {
            State state = new State();

            putState(context, state);

            final String text = getDecodedRequestParameter(context, PARAMETER_TEXT);
            if (text != null && text.length() > 0) {
                state.getEntitiesState().updateText(text);
            }

            final String tenantId = getDecodedRequestParameter(context, PARAMETER_TENANT_ID);
            if (tenantId != null && tenantId.length() > 0) {
                state.updateTenantId(tenantId);
            } else {
                state.updateTenantId(getCurrentTenantId());
            }
            context.getFlashScope().put(FLOW_ATTRIBUTE_STATE, createExtendedStateJson(context));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject createExtendedStateJson(RequestContext context) throws JSONException {
        State state = getState(context);
        JSONObject jsonObject = state.toJson();

        Tenant tenant = tenantService.getTenant(exContext(context),
                state.getTenantId() == null ? TenantService.ORGANIZATIONS : state.getTenantId());
        jsonObject.put(JSON_TENANT_URI, tenant.getTenantUri());

        return jsonObject;
    }

    @Override
    protected State getState(RequestContext context) {
        return (State)super.getState(context);
    }

    public Event browse(RequestContext context) {
        final String tenantId = getParameter(context, PARAMETER_TENANT_ID);

        getState(context).updateTenantId((tenantId != null && tenantId.length() > 0) ? tenantId : null);

        return success();
    }

    public Event search(RequestContext context) {
        getState(context).getEntitiesState().updateText(getParameter(context, PARAMETER_TEXT));

        return success();
    }

    abstract public Event next(RequestContext context);

    public Event select(RequestContext context) {
        getState(context).updateSelectedEntity(getParameter(context, PARAMETER_ENTITY));

        return success();
    }

    abstract public Event getDetails(RequestContext context);

    public Event searchAvailable(RequestContext context) {
        State state = getState(context);
        String text = getParameter(context, PARAMETER_TEXT);

        state.getAvailableEntitiesState().updateText(text);

        return success();
    }

    public Event searchAssigned(RequestContext context) {
        State state = getState(context);
        String text = getParameter(context, PARAMETER_TEXT);

        state.getAssignedEntitiesState().updateText(text);

        return success();
    }

    abstract public Event nextAssigned(RequestContext context);
    abstract public Event nextAvailable(RequestContext context);

    abstract public Event exist(RequestContext context);
    abstract public Event delete(RequestContext context);
    abstract public Event deleteAll(RequestContext context);
    abstract public Event create(RequestContext context);

    protected List<String> getEntities(RequestContext context) throws JSONException {
        List<String> entities = new ArrayList<String>();

        String json = context.getRequestParameters().get(PARAMETER_ENTITIES);
        if (json != null) {
            JSONArray array = new JSONArray(json);

            for(int i = 0; i < array.length(); i ++) {
                entities.add(array.getString(i));
            }
        }

        return entities;
    }

    protected String getDefaultEntity(RequestContext context) {
        State state = getState(context);
        String name = state.getEntitiesState().getText();
        String tId = state.getTenantId();

        StringBuilder qName = new StringBuilder();
        if (name != null) {
            qName.append(name);
        }

        if (tId != null) {
            qName.append(configuration.getUserNameSeparator());
            qName.append(tId);
        }

        return qName.toString();
    }

    protected String getCurrentUser() {
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();

        String name = authenticationToken.getName();

        TenantQualified tenantQualified = (TenantQualified) authenticationToken.getPrincipal();
        if (authenticationToken.getPrincipal() instanceof TenantQualified) {
            String tenantId = tenantQualified.getTenantId();
            return (tenantId != null) ? name + configuration.getUserNameSeparator() + tenantId : name;
        }

        return name;
    }

    protected String createUnexpectedExceptionResponseModel(Exception unexpectedException) throws JSONException {
        return this.createUnexpectedExceptionResponseModel(unexpectedException.getMessage());
    }

    protected String createUnexpectedExceptionResponseModel(String description) throws JSONException {
        String message = messages.getMessage("jsp.userManager.unexpectedException", new Object[0],
                LocaleContextHolder.getLocale());

        JSONObject exceptionJson = jsonHelper.createUnexpectedExceptionJson("", message,
            description);

        return jsonHelper.createErrorResponseModel(exceptionJson);
    }

    protected ExecutionContext exContext(RequestContext context) {
        return JasperServerUtil.getExecutionContext(context);
    }
}
