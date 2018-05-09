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

package com.jaspersoft.jasperserver.search.state.impl;

import com.jaspersoft.jasperserver.search.common.CustomFilter;
import com.jaspersoft.jasperserver.search.common.CustomSorter;
import com.jaspersoft.jasperserver.search.common.Option;
import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.model.FilterPath;
import com.jaspersoft.jasperserver.search.model.PathItem;
import com.jaspersoft.jasperserver.search.state.InitialStateResolver;
import com.jaspersoft.jasperserver.search.state.State;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.*;

/**
 * Implements {@link State} and {@link InitialStateResolver} interfaces.
 *
 * @author Stas Chubar
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class StateImpl implements State, InitialStateResolver, Cloneable, Serializable {
    public static String JSON_TEXT = "text";
    public static String JSON_SORT_BY = "sortBy";
    public static String JSON_FOLDER_URI = "folderUri";
    public static String JSON_CUSTOM_FILTERS = "customFilters";

    private String text;
    private String sortBy;
    private String folderUri;

    private Set<String> uris = new HashSet<String>();

    /**
     * @deprecated customFiltersMap is partially moved to *ModeRepositorySearchConfiguration.
     * This duplication should be removed. See {@link RepositorySearchConfiguration#customFiltersMap}.
      */
    private Map<String, String> customFiltersMap;
    private int resultIndex;
    private int resultsCount;
    private Map<String, String> selectedFiltersMap = new LinkedHashMap<String, String>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.uris.clear();
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
        this.uris.clear();
    }

    public String getFolderUri() {
        return folderUri;
    }

    public void setFolderUri(String folderUri) {
        this.folderUri = folderUri;
        this.uris.clear();
    }

    /**
     * @deprecated customFiltersMap is partially moved to *ModeRepositorySearchConfiguration.
     * This duplication should be removed. See {@link RepositorySearchConfiguration#customFiltersMap}.
     */
    public Map<String, String> getCustomFiltersMap() {
        return Collections.unmodifiableMap(customFiltersMap);
    }
    /**
     * @deprecated customFiltersMap is partially moved to *ModeRepositorySearchConfiguration.
     * This duplication should be removed. See {@link RepositorySearchConfiguration#customFiltersMap}.
     */
    public void setCustomFiltersMap(Map<String, String> customFiltersMap) {
        this.customFiltersMap = customFiltersMap;
        this.uris.clear();
    }

    public int getResultIndex() {
        return resultIndex;
    }

    public void setResultIndex(int resultIndex) {
        this.resultIndex = resultIndex;
    }

    public int getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(int resultsCount) {
        this.resultsCount = resultsCount;
    }

    public void updateText(String text) {
        this.text = text;
        updateResultState(0, 0);
        this.uris.clear();
    }

    public void updateSorter(String sortBy) {
        this.sortBy = sortBy;
        updateResultState(0, 0);
        this.uris.clear();
    }

    public void updateFolder(String folderUri) {
        this.folderUri = folderUri;
        updateResultState(0, 0);
        this.uris.clear();
    }

    public void updateFilter(String filterId, String optionId, boolean isDefault) {
        customFiltersMap.put(filterId, optionId);
        updateResultState(0, 0);
        this.uris.clear();

        if (isDefault) {
            selectedFiltersMap.remove(filterId);
        } else {
            selectedFiltersMap.put(filterId, optionId);
        }
    }

    public void updateResultState(int resultIndex, int resultsCount) {
        this.resultIndex = resultIndex;
        this.resultsCount = resultsCount;
    }

    public void rollback(int position, RepositorySearchConfiguration configuration) {
        // Text item is always on the first position, so decrement position for filters.
        int filterPosition = --position;

        // Rollback to filter in the position.
        Map<String, String> newSelectedFiltersMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : selectedFiltersMap.entrySet()) {
            if (filterPosition < 0) {
                break;
            }
            filterPosition--;

            newSelectedFiltersMap.put(entry.getKey(), entry.getValue());
        }
        selectedFiltersMap = newSelectedFiltersMap;

        // Resetting not selected filters to default state.
        for (Map.Entry<String, String> entry : customFiltersMap.entrySet()) {
            if (!selectedFiltersMap.containsKey(entry.getKey())) {
                customFiltersMap.put(entry.getKey(), getDefaultOption(entry.getKey(), configuration));
            }
        }

        updateResultState(0, 0);
        this.uris.clear();
    }

    private String getDefaultOption(String filterId, RepositorySearchConfiguration configuration) {
        for (CustomFilter customFilter : configuration.getCustomFilters()) {
            if (customFilter.getId().equals(filterId)) {
                return customFilter.getDefaultOption();
            }
        }

        throw new RuntimeException("Custom filter with filter id '" + filterId + "' not found!");
    }

    public FilterPath getFilterPath(RepositorySearchConfiguration configuration, MessageSource messageSource) {
        List<PathItem> pathItems = new ArrayList<PathItem>(selectedFiltersMap.size() + 1);

        int position = 0;
        PathItem textPathItem = new PathItem();
        textPathItem.setPosition(position++);
        textPathItem.setType(PathItem.Type.TEXT);
        if (text == null || text.length() == 0) {
            textPathItem.setLabel(messageSource.getMessage("SEARCH_FILTER_PATH_ALL", null, LocaleContextHolder.getLocale()));
        } else {
            textPathItem.setLabel("'" + text + "'");
        }
        pathItems.add(textPathItem);

        for (Map.Entry<String, String> entry : selectedFiltersMap.entrySet()) {
            PathItem filterPathItem = new PathItem();
            filterPathItem.setPosition(position++);
            filterPathItem.setType(PathItem.Type.FILTER);
            filterPathItem.setLabel(getPathItemLabelForFilter(configuration, messageSource, entry.getKey(), entry.getValue()));

            pathItems.add(filterPathItem);
        }

        return new FilterPath(pathItems);
    }

    private String getPathItemLabelForFilter(RepositorySearchConfiguration configuration, MessageSource messageSource,
            String filterId, String filterOption) {
        for (CustomFilter customFilter : configuration.getCustomFilters()) {
            if (customFilter.getId().equals(filterId)) {
                for (Option option : customFilter.getOptions()) {
                    if (option.getId().equals(filterOption)) {
                        return messageSource.getMessage(option.getLabelId(), null, LocaleContextHolder.getLocale());
                    }
                }
            }
        }

        throw new RuntimeException("Custom filter with filter id '" + filterId + "' and filter option '" +
                filterOption + "' not found!");
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(JSON_TEXT, text);
        jsonObject.put(JSON_SORT_BY, sortBy);
        jsonObject.put(JSON_FOLDER_URI, folderUri);
        jsonObject.put(JSON_CUSTOM_FILTERS, new JSONObject(customFiltersMap));

        return jsonObject;
    }

    @Override
    public Set<String> getServedUri() {
        return uris;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        StateImpl state = (StateImpl)super.clone();

        state.customFiltersMap = new HashMap<String, String>(customFiltersMap);
        state.selectedFiltersMap = new LinkedHashMap<String, String>(selectedFiltersMap);
        state.uris = new HashSet<String>(uris);

        return state;
    }

    private StateImpl cloneState() {
        try {
            return (StateImpl) this.clone();
        } catch (CloneNotSupportedException e) {
            // Should never occur.
            throw new RuntimeException("Error occurred when cloning state", e);
        }
    }

    public State getInitialState(RepositorySearchConfiguration repositorySearchConfiguration) {
        StateImpl state = cloneState();

        // Checking if user has access to the sorter.
        List<CustomSorter> customSorters = repositorySearchConfiguration.getCustomSorters();

        boolean hasSorterAccess = false;
        for (CustomSorter customSorter : customSorters) {
            if (customSorter.getId().equals(state.sortBy)) {
                hasSorterAccess = true;
                break;
            }
        }

        // Reseting sorter to the first one if user has no access to the current one.
        if (!hasSorterAccess && customSorters.size() > 0) {
            state.sortBy = customSorters.get(0).getId();
        }

        List<CustomFilter> customFilters = repositorySearchConfiguration.getCustomFilters();

        Map<String, String> clonedCustomFiltersMap = new HashMap<String, String>(state.customFiltersMap);
        for (Map.Entry<String, String> entry : clonedCustomFiltersMap.entrySet()) {
            String filterId = entry.getKey();
            String optionId = entry.getValue();

            // Checking if user has access to the filter.
            CustomFilter currentCustomFilter = null;
            for (CustomFilter customFilter : customFilters) {
                if (customFilter.getId().equals(filterId)) {
                    currentCustomFilter = customFilter;
                    break;
                }
            }

            if (currentCustomFilter == null) {
                // Removing filter if user has no access to it.
                state.customFiltersMap.remove(entry.getKey());
            } else {
                // Checking if user has access to the filter option.
                boolean hasOptionAccess = false;
                for (Option option : currentCustomFilter.getOptions()) {
                    if (option.getId().equals(optionId)) {
                        hasOptionAccess = true;
                        break;
                    }
                }

                // Resetting option to the first one if user has no access to the current one.
                if (!hasOptionAccess) {
                    state.customFiltersMap.put(entry.getKey(), currentCustomFilter.getOptions().get(0).getId());
                }
            }
        }

        return state;
    }
}
