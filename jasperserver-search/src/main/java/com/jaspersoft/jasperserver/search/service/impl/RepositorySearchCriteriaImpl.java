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
package com.jaspersoft.jasperserver.search.service.impl;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.search.mode.AccessType;
import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 * @version $Id$
 */
public class RepositorySearchCriteriaImpl implements RepositorySearchCriteria {

    private SearchMode searchMode;
    private String sortBy;
    private int startIndex;
    private int maxCount;
    private boolean forceFullPage = false;
    private String searchText;
    private String folderUri;
    private List<String> resourceTypes;
    private List<String> excludeResourceTypes;
    private List<String> containerResourceTypes;
    private List<String> fileResourceTypes;
    private List<SearchFilter> customFilters;
    private boolean showHidden = false;
    private boolean excludeFolders = false;
    private AccessType accessType = AccessType.ALL;
    private List<String> excludeRelativePaths;
    private User user;
    private List<ClientResourceLookup> resources;
    private String lookupClass=null;

    public List<ClientResourceLookup> getResources() {
		return resources;
	}

	public void setResources(List<ClientResourceLookup> resources) {
		this.resources = resources;
	}

	public List<SearchFilter> getCustomFilters() {
        return customFilters;
    }

    public void setCustomFilters(List<SearchFilter> customFilters) {
        this.customFilters = customFilters;
    }

    @Override
    public void addCustomFilter(SearchFilter customFilter) {
        if(customFilters == null)
            customFilters = new ArrayList<SearchFilter>();
        customFilters.add(customFilter);
    }

    public SearchMode getSearchMode() {
        return searchMode;
    }

    public void setSearchMode(SearchMode searchMode) {
        this.searchMode = searchMode;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public boolean isForceFullPage() {
        return forceFullPage;
    }

    public void setForceFullPage(boolean forceFullPage) {
        this.forceFullPage = forceFullPage;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getFolderUri() {
        return folderUri;
    }

    public void setFolderUri(String folderUri) {
        this.folderUri = folderUri;
    }

    public List<String> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public List<String> getExcludeResourceTypes() {
        return excludeResourceTypes;
    }

    public void setExcludeResourceTypes(List<String> excludeResourceTypes) {
        this.excludeResourceTypes = excludeResourceTypes;
    }

    public List<String> getContainerResourceTypes() {
        return containerResourceTypes;
    }

    public void setContainerResourceTypes(List<String> containerResourceTypes) {
        this.containerResourceTypes = containerResourceTypes;
    }

    public List<String> getFileResourceTypes() {
        return fileResourceTypes;
    }

    public void setFileResourceTypes(List<String> fileResourceTypes) {
        this.fileResourceTypes = fileResourceTypes;
    }

    public boolean isShowHidden() {
        return showHidden;
    }

    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }

    public boolean isExcludeFolders() {
        return excludeFolders;
    }

    public void setExcludeFolders(boolean excludeFolders) {
        this.excludeFolders = excludeFolders;
    }

    public AccessType getAccessType() {
        if (accessType == null){
            return AccessType.ALL;
        }
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void setExcludeRelativePaths(List<String> strings) {
        excludeRelativePaths = strings;
    }

    @Override
    public List<String> getExcludeRelativePaths() {
        return excludeRelativePaths;
    }

    public static class Builder{
        private final RepositorySearchCriteria criteria = new RepositorySearchCriteriaImpl();
        public Builder setSearchMode(SearchMode searchMode){
            criteria.setSearchMode(searchMode);
            return this;
        }
        
        public Builder setLookupClass(String lookupClass){
        	criteria.setLookupClass(lookupClass);
        	return this;
        }

        public Builder setSortBy(String sortBy){
            criteria.setSortBy(sortBy);
            return this;
        }

        public Builder setShowHidden(Boolean showHidden) {
            criteria.setShowHidden(showHidden != null && showHidden);
            return this;
        }

        public Builder setStartIndex(int currentIndex){
            criteria.setStartIndex(currentIndex);
            return this;
        }

        public Builder setMaxCount(int maxCount){
            criteria.setMaxCount(maxCount);
            return this;
        }

        public Builder setForceFullPage(boolean forceFullPage){
            criteria.setForceFullPage(forceFullPage);
            return this;
        }

        public Builder setSearchText(String searchText){
            criteria.setSearchText(searchText);
            return this;
        }

        public Builder setFolderUri(String folderUri){
            criteria.setFolderUri(folderUri);
            return this;
        }

        public Builder setResourceTypes(List<String> resourceTypes){
            criteria.setResourceTypes(resourceTypes);
            return this;
        }

        public Builder setResourceTypes(String... resourceTypes){
            // single null value is ignored
            if(!(resourceTypes.length == 1 && resourceTypes[0] == null)){
                criteria.setResourceTypes(new ArrayList<String>(Arrays.asList(resourceTypes)));
            }
            return this;
        }

        public Builder setExcludeResourceTypes(List<String> excludeResourceTypes){
            criteria.setExcludeResourceTypes(excludeResourceTypes);
            return this;
        }

        public Builder setContainerResourceTypes(List<String> resourceTypes){
            criteria.setContainerResourceTypes(resourceTypes);
            return this;
        }

        public Builder setContainerResourceTypes(String... resourceTypes){
            // single null value is ignored
            if(!(resourceTypes.length == 1 && resourceTypes[0] == null)){
                criteria.setContainerResourceTypes(new ArrayList<String>(Arrays.asList(resourceTypes)));
            }
            return this;
        }

        public Builder setCustomFilters(List<SearchFilter> customFilters){
            criteria.setCustomFilters(customFilters);
            return this;
        }

        public Builder setExcludeFolders(boolean exclude) {
            criteria.setExcludeFolders(exclude);
            return this;
        }

        public Builder setUser(User user){
            criteria.setUser(user);
            return this;
        }

        public Builder setAccessType(AccessType type){
            criteria.setAccessType(type);
            return this;
        }
        public Builder setExcludeRelativePaths(List<String> excludeRelativePaths){
            criteria.setExcludeRelativePaths(excludeRelativePaths);
            return this;
        }
        
        public Builder setResources(List<ClientResourceLookup> resources){
        	criteria.setResources(resources);
        	return this;
        }

        public RepositorySearchCriteria getCriteria(){
            return criteria;
        }
    }

    @Override
    public RepositorySearchCriteria clone() throws CloneNotSupportedException {
        RepositorySearchCriteria criteria = (RepositorySearchCriteria) super.clone();
        criteria.setSearchMode(this.searchMode);
        criteria.setStartIndex(this.startIndex);
        criteria.setMaxCount(this.maxCount);
        criteria.setForceFullPage(this.forceFullPage);
        criteria.setSearchText(this.searchText);
        criteria.setFolderUri(this.folderUri);
        criteria.setResources(this.resources);
        
        if (this.resourceTypes != null) {
            criteria.setResourceTypes(new ArrayList<String>(this.resourceTypes));
        }

        if (this.customFilters != null) {
            criteria.setCustomFilters(new ArrayList<SearchFilter>(this.customFilters));
        }
        criteria.setShowHidden(this.showHidden);
        criteria.setExcludeFolders(this.excludeFolders);
        criteria.setAccessType(this.accessType);
        if (this.excludeRelativePaths != null) {
            criteria.setExcludeRelativePaths(new ArrayList<String>(this.excludeRelativePaths));
        }

        // We don't do dep clone for the user
        criteria.setUser(this.user);

        return criteria;
    }

	@Override
	public String getLookupClass() {
		return lookupClass;
	}

	@Override
	public void setLookupClass(String lookupClass) {
		this.lookupClass=lookupClass;
	}
}
