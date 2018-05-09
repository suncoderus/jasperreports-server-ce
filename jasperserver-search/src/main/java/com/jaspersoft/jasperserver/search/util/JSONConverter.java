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

package com.jaspersoft.jasperserver.search.util;

import com.jaspersoft.jasperserver.api.common.util.DateUtils;
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.search.action.RepositorySearchAction;
import com.jaspersoft.jasperserver.search.common.*;
import com.jaspersoft.jasperserver.search.model.FilterPath;
import com.jaspersoft.jasperserver.search.model.PathItem;
import com.jaspersoft.jasperserver.search.model.permission.Permission;
import com.jaspersoft.jasperserver.search.state.State;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.util.JSONConverterBase;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.DateFormat;

public class JSONConverter extends JSONConverterBase implements Serializable {

    public static String RESOURCE_NAME = "name";
    public static String RESOURCE_LABEL = "label";
    public static String RESOURCE_DESC = "description";
    public static String RESOURCE_DATE = "date";
    public static String RESOURCE_DATE_TIMESTAMP = "dateTimestamp";
    public static String RESOURCE_DATE_TIME = "dateTime";
    public static String RESOURCE_UPDATE_DATE = "updateDate";
    public static String RESOURCE_UPDATE_DATE_TIMESTAMP = "updateDateTimestamp";
    public static String RESOURCE_UPDATE_DATE_TIME = "updateDateTime";
    public static String RESOURCE_URI = "URI";
    public static String RESOURCE_URI_STRING = "URIString";
    public static String RESOURCE_PARENT_URI = "parentURI";
    public static String RESOURCE_PARENT_FOLDER = "parentFolder";
    public static String RESOURCE_TYPE = "type";
    public static String RESOURCE_RESOURCE_TYPE = "resourceType";
    public static String RESOURCE_SCHEDULED = "scheduled";
    public static String RESOURCE_PERMISSIONS = "permissions";
    public static String RESOURCE_NUMBER = "resourceNumber";
    public static String RESOURCE_HAS_CHILDREN = "hasChildren";

    public static String DELETED_RESOURCES = "deletedResources";
    public static String DEPENDENT_RESOURCES = "dependentResources";
    public static String RESOURCES = "resources";
    public static String RESOURCES_COUNT = "resourcesCount";
    public static String STATE = "state";
    public static String FILTER_STATES = "filterStates";

    public static String FILTER_PATH = "filterPath";
    public static String PATH_ITEM_POSITION = "position";
    public static String PATH_ITEM_TYPE = "type";
    public static String PATH_ITEM_LABEL = "label";

    public static String STATE_RESULT_TYPE = "resultType";
    public static String STATE_RESOURCE_TYPE = "resourceType";
    public static String STATE_FOLDER_URI = "folderUri";
    public static String STATE_SORT_BY = "sortBy";
    public static String STATE_STATE_NAME = "stateName";
    public static String STATE_TEXT = "text";
    public static String STATE_CURRENT_RESULT = "currentResult";
    public static String STATE_FILTER_TYPE = "filterType";
    public static String STATE_CUSTOM_FILTERS = "customFilters";
    public static String STATE_HIDDEN = "isHidden";

    public static String FOLDER_LABEL = "label";
    public static String FOLDER_DESC = "desc";
    public static String FOLDER_URI = "URI";

    private static final String CONFIGURATION_SORTERS = "sorters";
    private static final String CONFIGURATION_FILTERS = "filters";
    private static final String CONFIGURATION_FILTER_ID = "id";
    private static final String CONFIGURATION_FILTER_OPTIONS = "options";
    private static final String CONFIGURATION_FILTER_SHOW_COUNT = "showCount";
    private static final String CONFIGURATION_RESOURCE_LABEL_MAX_LENGTH = "resourceLabelMaxLength";
    private static final String CONFIGURATION_RESOURCE_NAME_MAX_LENGTH = "resourceNameMaxLength";
    private static final String CONFIGURATION_RESOURCE_DESCRIPTION_MAX_LENGTH = "resourceDescriptionMaxLength";
    private static final String CONFIGURATION_CUSTOM_DATA_SOURCES = "customDataSources";

    private static final String PERMISSIONS = "permissions";
    private static final String PERMISSION_NAME = "name";
    private static final String PERMISSION_LABEL_ID = "labelId";

    @Autowired
    @Qualifier("messageSource")
    protected MessageSource messages;
    private RepositorySearchConfiguration repositorySearchConfiguration;
    protected ConfigurationBean configurationBean;

    @Autowired(required = false)
    private List<CustomDataSourceDefinition> definitions;


    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    public void setRepositorySearchConfiguration(RepositorySearchConfiguration repositorySearchConfiguration) {
        this.repositorySearchConfiguration = repositorySearchConfiguration;
    }

    public void setConfigurationBean(ConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }

    public JSONObject resourceToJson(ResourceDetails resource) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        String desc = resource.getDescription();

        jsonObject.put(RESOURCE_NAME, resource.getName());
        jsonObject.put(RESOURCE_LABEL, resource.getLabel());
        jsonObject.put(RESOURCE_DESC, (desc != null) ? desc.replace("\\n", "<br>") : "");
        jsonObject.put(RESOURCE_URI, resource.getURI());
        jsonObject.put(RESOURCE_URI_STRING, resource.getURIString());
        jsonObject.put(RESOURCE_PARENT_URI, resource.getParentURI());
        jsonObject.put(RESOURCE_PARENT_FOLDER, resource.getParentFolder());
        jsonObject.put(RESOURCE_RESOURCE_TYPE, resource.getResourceType());
        jsonObject.put(RESOURCE_TYPE, messages.getMessage("resource." + resource.getResourceType() + ".label", null,
                resource.getResourceType(), LocaleContextHolder.getLocale()));
        jsonObject.put(RESOURCE_SCHEDULED, resource.isScheduled());
        jsonObject.put(RESOURCE_PERMISSIONS, getPermissionsMask(resource));
        jsonObject.put(RESOURCE_HAS_CHILDREN, resource.hasChildren());

        jsonObject.put(RESOURCE_DATE, getFormattedDate(resource.getCreationDate()));
        jsonObject.put(RESOURCE_DATE_TIMESTAMP,
                getDateFormat(configurationBean.getTimestampFormat()).format(resource.getCreationDate()));
        jsonObject.put(RESOURCE_DATE_TIME,
                getDateFormat(configurationBean.getTimeFormat()).format(resource.getCreationDate()));
        jsonObject.put(RESOURCE_UPDATE_DATE, getFormattedDate(resource.getUpdateDate()));
        jsonObject.put(RESOURCE_UPDATE_DATE_TIMESTAMP,
                getDateFormat(configurationBean.getTimestampFormat()).format(resource.getUpdateDate()));
        jsonObject.put(RESOURCE_UPDATE_DATE_TIME,
                getDateFormat(configurationBean.getTimeFormat()).format(resource.getUpdateDate()));
        jsonObject.put(RESOURCE_NUMBER, resource.getResourceNumber());

        return jsonObject;
    }

    private DateFormat getDateFormat(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, LocaleContextHolder.getLocale());
        dateFormat.setTimeZone(TimeZoneContextHolder.getTimeZone());

        return dateFormat;
    }

    private String getFormattedDate(Date date) {
        String formattedDate = getDateFormat(configurationBean.getDateFormat()).format(date);

        if (DateUtils.isToday(date)) {
            formattedDate = messages.getMessage("SEARCH_DATE_TODAY", null, formattedDate,
                    LocaleContextHolder.getLocale());
        } else if (DateUtils.isYesterday(date)) {
            formattedDate = messages.getMessage("SEARCH_DATE_YESTERDAY", null, formattedDate,
                    LocaleContextHolder.getLocale());
        } else if (DateUtils.isThisYear(date)) {
            formattedDate = new SimpleDateFormat(configurationBean.getCurrentYearDateFormat(),
                    LocaleContextHolder.getLocale()).format(date);
        }

        return formattedDate;
    }

    public static String getPermissionsMask(ResourceDetails resource) {
        StringBuilder mask = new StringBuilder();

        if(resource.isReadable()) {
            mask.append(Permissions.READ);
        }

        if(resource.isEditable()) {
            mask.append(Permissions.EDIT);
        }

        if(resource.isRemovable()) {
            mask.append(Permissions.REMOVE);
        }

        if(resource.isAdministrable()) {
            mask.append(Permissions.ADMINISTRATE);
        }

        return mask.toString();
    }

    public JSONArray resourcesToJson(List<ResourceDetails> resources) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (ResourceDetails r : resources) {
            jsonArray.put(resourceToJson(r));
        }

        return jsonArray;
    }

    public JSONArray collectionToJsonStringArray(Collection<String> collection) throws JSONException {
        return new JSONArray(collection);
    }

    public Set<String> jsonStringArrayToSet(String s) throws JSONException {
        JSONArray jsonArray = new JSONArray((s != null) ? s : "[]");

        Set<String> set = new HashSet<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            set.add(jsonArray.getString(i));
        }

        return set;
    }

    public Resource jsonToResource(String s) throws JSONException {
        JSONObject json = new JSONObject((s != null) ? s : "{}");
        ResourceDetails resource = new ResourceDetails();

        if (json.has(RESOURCE_URI_STRING)) {
            resource.setURIString(json.getString(RESOURCE_URI_STRING));
        }

        if (json.has(RESOURCE_LABEL)) {
            resource.setLabel(json.getString(RESOURCE_LABEL));
        }

        if (json.has(RESOURCE_DESC)) {
            resource.setDescription(json.getString(RESOURCE_DESC));
        }

        if (json.has(RESOURCE_TYPE)) {
            resource.setResourceType(json.getString(RESOURCE_TYPE));
        }

        return resource;
    }

    public List<Resource> jsonArrayToResources(String s) throws JSONException {
        JSONArray jsonArray = new JSONArray((s != null) ? s : "[]");

        ArrayList<Resource> list = new ArrayList<Resource>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonToResource(jsonArray.getString(i)));
        }

        return list;
    }

    public JSONObject deleteResourcesJSONResponse(List<Resource> list)
            throws JSONException {
        JSONObject jsonObject = new JSONObject();

        HashSet<String> set = new HashSet<String>();
        for (Resource resource : list) {
            set.add(resource.getURIString());
        }

        jsonObject.put(DELETED_RESOURCES, collectionToJsonStringArray(set));

        return createJSONResponse(jsonObject);
    }

    public JSONObject dependentResourcesJSONResponse(List<ResourceDetails> list)
            throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(DEPENDENT_RESOURCES, resourcesToJson(list));

        return createJSONResponse(jsonObject);
    }

    public Folder jsonToFolder(String s) throws JSONException {
        JSONObject json = new JSONObject((s != null) ? s : "{}");
        Folder folder = new FolderImpl();

        if (json.has(FOLDER_LABEL)) {
            folder.setLabel(json.getString(FOLDER_LABEL));
        }

        if (json.has(FOLDER_DESC)) {
            folder.setDescription(json.getString(FOLDER_DESC));
        }

        if (json.has(FOLDER_URI)) {
            folder.setURIString(json.getString(FOLDER_URI));
        }

        return folder;
    }

    public JSONObject folderToJson(Folder folder) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(FOLDER_LABEL, folder.getLabel());
        json.put(FOLDER_DESC, folder.getDescription());
        json.put(FOLDER_URI, folder.getURIString());

        return json;
    }

    public enum Permissions {
        READ("r"),
        EDIT("e"),
        REMOVE("d"),
        ADMINISTRATE("a");

        String mask;

        Permissions(String mask) {
            this.mask = mask;
        }


        @Override
        public String toString() {
            return this.mask;
        }
    }

    public JSONObject createResult(List<ResourceDetails> resources, State state, FilterPath filterPath) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(RESOURCES, resourcesToJson(resources));
        jsonObject.put(FILTER_PATH, filterPathToJson(filterPath));
        jsonObject.put(STATE, state.toJson());

        return jsonObject;
    }

    private JSONArray filterPathToJson(FilterPath filterPath) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (PathItem pathItem : filterPath.getItems()) {
            jsonArray.put(pathItemToJson(pathItem));
        }

        return jsonArray;
    }

    private JSONObject pathItemToJson(PathItem pathItem) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(PATH_ITEM_POSITION, pathItem.getPosition());
        jsonObject.put(PATH_ITEM_TYPE, pathItem.getType());
        jsonObject.put(PATH_ITEM_LABEL, pathItem.getLabel());

        return jsonObject;
    }

    public JSONObject createJSONConfiguration() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        // Sorters.
        JSONArray jsonSorters = new JSONArray();
        for (CustomSorter customSorter : repositorySearchConfiguration.getCustomSorters()) {
            if (customSorter.isExposed()){
                jsonSorters.put(customSorter.toJson());
            }
        }
        jsonObject.put(CONFIGURATION_SORTERS, jsonSorters);

        // Filters.
        JSONArray jsonFilters = new JSONArray();
        for (CustomFilter customFilter : repositorySearchConfiguration.getCustomFilters()) {
            jsonFilters.put(createFilterJSON(customFilter));
        }
        jsonObject.put(CONFIGURATION_FILTERS, jsonFilters);

        // Resource properties max length.
        jsonObject.put(CONFIGURATION_RESOURCE_LABEL_MAX_LENGTH,
                repositorySearchConfiguration.getResourceLabelMaxLength());
        jsonObject.put(CONFIGURATION_RESOURCE_NAME_MAX_LENGTH,
                repositorySearchConfiguration.getResourceNameMaxLength());
        jsonObject.put(CONFIGURATION_RESOURCE_DESCRIPTION_MAX_LENGTH,
                repositorySearchConfiguration.getResourceDescriptionMaxLength());

        JSONArray permissionsLabels = new JSONArray();
        for (Permission permission : Permission.values()) {
            JSONObject permissionJson = new JSONObject();
            permissionJson.put(PERMISSION_NAME, permission.name());
            permissionJson.put(PERMISSION_LABEL_ID, permission.getLabelId());

            permissionsLabels.put(permissionJson);
        }
        jsonObject.put(PERMISSIONS, permissionsLabels);

        JSONArray customDataSources = new JSONArray();
        if (definitions != null) {
            for (CustomDataSourceDefinition definition : this.definitions) {
                customDataSources.put(definition.getName());
            }
        }
        jsonObject.put(CONFIGURATION_CUSTOM_DATA_SOURCES, customDataSources);

        return jsonObject;
    }

    public JSONObject createFilterJSON(CustomFilter customFilter) throws JSONException {
        JSONObject jsonFilter = new JSONObject();
        jsonFilter.put(CONFIGURATION_FILTER_ID, customFilter.getId());
        jsonFilter.put(CONFIGURATION_FILTER_SHOW_COUNT, customFilter.getShowCount());
        jsonFilter.put(CONFIGURATION_FILTER_OPTIONS, optionsToJson(customFilter.getOptions()));

        return jsonFilter;
    }

    private JSONArray optionsToJson(List<Option> options) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (Option option : options) {
            jsonArray.put(option.toJson());
        }

        return jsonArray;
    }
}
