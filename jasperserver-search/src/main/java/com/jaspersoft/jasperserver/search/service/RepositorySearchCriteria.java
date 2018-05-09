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
package com.jaspersoft.jasperserver.search.service;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.search.mode.AccessType;
import com.jaspersoft.jasperserver.search.mode.SearchMode;

import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface RepositorySearchCriteria extends Cloneable {
    SearchMode getSearchMode();
    void setSearchMode(SearchMode searchMode);
    String getSortBy();
    void setSortBy(String sortBy);
    int getStartIndex();
    void setStartIndex(int startIndex);
    int getMaxCount();
    void setMaxCount(int maxCount);
    String getSearchText();
    void setSearchText(String searchText);
    String getFolderUri();
    void setFolderUri(String folderUri);
    List<String> getResourceTypes();
    void setResourceTypes(List<String> resourceTypes);
    List<String> getExcludeResourceTypes();
    void setExcludeResourceTypes(List<String> excludeResourceTypes);
    List<String> getFileResourceTypes();
    void setFileResourceTypes(List<String> resourceTypes);
    List<String> getContainerResourceTypes();
    void setContainerResourceTypes(List<String> resourceTypes);
    List<SearchFilter> getCustomFilters();
    void setShowHidden(boolean show);
    boolean isShowHidden();
    void setExcludeFolders(boolean withFolders);
    boolean isExcludeFolders();
    void setCustomFilters(List<SearchFilter> customFilters);
    void addCustomFilter(SearchFilter customFilter);
    void setUser(User user);
    User getUser();
    AccessType getAccessType();
    void setAccessType(AccessType mod);
    void setExcludeRelativePaths(List<String> strings);
    List<String> getExcludeRelativePaths();
    void setForceFullPage(boolean forceFullPage);
    boolean isForceFullPage();
    void setResources(List<ClientResourceLookup> resources);
    List<ClientResourceLookup> getResources();
    RepositorySearchCriteria clone() throws CloneNotSupportedException;
    String getLookupClass();
    void setLookupClass(final String lookupClass);
}
