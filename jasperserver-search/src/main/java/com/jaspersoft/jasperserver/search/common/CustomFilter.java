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

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import com.jaspersoft.jasperserver.search.service.SearchSecurityResolver;

/**
 * Custom filter.
 *
 * @author Stas Chubar
 * @version $Id$
 */
public class CustomFilter implements Serializable {
    private String id;
    private String defaultOption;
    private List<Option> options;
    private SearchFilter filter;
    private List<RoleAccess> roleAccessList;
    private int showCount;

    private SearchSecurityResolver searchSecurityResolver;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaultOption() {
        if (this.defaultOption == null && !this.options.isEmpty()) {
            this.defaultOption = this.options.get(0).getId();
        }
        return defaultOption;
    }

    public void setDefaultOption(String defaultOption) {
        this.defaultOption = defaultOption;
    }

    public List<Option> getOptions() {
        List<Option> accessibleOptions = new ArrayList<Option>(options.size());

        for (Option option : options) {
            for (RoleAccess roleAccess : option.getRoleAccessList()) {
                if (searchSecurityResolver.hasAccess(roleAccess)) {
                    accessibleOptions.add(option);
                    break;
                }
            }
        }

        return accessibleOptions;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public SearchFilter getFilter() {
        return filter;
    }

    public void setFilter(SearchFilter filter) {
        this.filter = filter;
    }

    public List<RoleAccess> getRoleAccessList() {
        return roleAccessList;
    }

    public void setRoleAccessList(List<RoleAccess> roleAccessList) {
        this.roleAccessList = roleAccessList;
    }

    public void setSearchSecurityResolver(SearchSecurityResolver searchSecurityResolver) {
        this.searchSecurityResolver = searchSecurityResolver;
    }

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }
}