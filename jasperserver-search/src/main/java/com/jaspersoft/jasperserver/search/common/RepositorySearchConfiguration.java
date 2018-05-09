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

package com.jaspersoft.jasperserver.search.common;

import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.search.service.SearchSecurityResolver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Repository search configuration.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class RepositorySearchConfiguration implements Serializable{
    private List<CustomSorter> customSorters;
    private List<CustomFilter> customFilters;
    private int itemsPerPage;
    private int maxItemsPerQuery;
    private List<SearchFilter> systemFilters;
    private int resourceLabelMaxLength;
    private int resourceNameMaxLength;
    private int resourceDescriptionMaxLength;

    private SearchSecurityResolver searchSecurityResolver;

    /**
     * @deprecated Temporary solution for REST by Yaroslav. Shoul be refactored to use the same source of default
     * filter option for specified filter. At the moment in the configuration there are 2 places where default filter
     * options are defined: customFilters metadata and defaultCustomFiltersMap used by defaultInitialStateResolver.
     * InitialStateResolver should get default filter options from customFilters metadata.
     */
    private Map<String, String> customFiltersMap;

    public Map<String, String> getCustomFiltersMap() {
        return customFiltersMap;
    }

    public void setCustomFiltersMap(Map<String, String> customFiltersMap) {
        this.customFiltersMap = customFiltersMap;
    }

    public List<CustomSorter> getCustomSorters() {
        List<CustomSorter> accessibleSorters = new ArrayList<CustomSorter>(customSorters.size());

        for (CustomSorter customSorter : customSorters) {
            for (RoleAccess roleAccess : customSorter.getRoleAccessList()) {
                if (searchSecurityResolver.hasAccess(roleAccess)) {
                    accessibleSorters.add(customSorter);
                    break;
                }
            }
        }

        return accessibleSorters;
    }

    public void setCustomSorters(List<CustomSorter> customSorters) {
        this.customSorters = customSorters;
    }

    public void setCustomFilters(List<CustomFilter> customFilters) {
        this.customFilters = customFilters;
    }

    public List<CustomFilter> getCustomFilters() {
        List<CustomFilter> accessibleFilters = new ArrayList<CustomFilter>(customFilters.size());

        for (CustomFilter customFilter : customFilters) {
            for (RoleAccess roleAccess : customFilter.getRoleAccessList()) {
                if (searchSecurityResolver.hasAccess(roleAccess)) {
                    accessibleFilters.add(customFilter);
                    break;
                }
            }
        }

        return accessibleFilters;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getMaxItemsPerQuery() {
        return maxItemsPerQuery;
    }

    public void setMaxItemsPerQuery(int maxItemsPerQuery) {
        this.maxItemsPerQuery = maxItemsPerQuery;
    }

    public void setSearchSecurityResolver(SearchSecurityResolver searchSecurityResolver) {
        this.searchSecurityResolver = searchSecurityResolver;
    }

    public List<SearchFilter> getSystemFilters() {
        return systemFilters;
    }

    public void setSystemFilters(List<SearchFilter> systemFilters) {
        this.systemFilters = systemFilters;
    }

    public int getResourceLabelMaxLength() {
        return resourceLabelMaxLength;
    }

    public void setResourceLabelMaxLength(int resourceLabelMaxLength) {
        this.resourceLabelMaxLength = resourceLabelMaxLength;
    }

    public int getResourceNameMaxLength() {
        return resourceNameMaxLength;
    }

    public void setResourceNameMaxLength(int resourceNameMaxLength) {
        this.resourceNameMaxLength = resourceNameMaxLength;
    }

    public int getResourceDescriptionMaxLength() {
        return resourceDescriptionMaxLength;
    }

    public void setResourceDescriptionMaxLength(int resourceDescriptionMaxLength) {
        this.resourceDescriptionMaxLength = resourceDescriptionMaxLength;
    }
}
