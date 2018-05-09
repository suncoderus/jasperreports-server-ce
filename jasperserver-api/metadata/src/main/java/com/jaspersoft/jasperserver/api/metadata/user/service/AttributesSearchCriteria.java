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

package com.jaspersoft.jasperserver.api.metadata.user.service;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Set;

/**
 * @author askorodumov
 * @version $Id$
 */
public class AttributesSearchCriteria {
    private String holder = null;
    private Set<String> names = null;
    private Set<String> groups = null;
    private boolean effective = false;
    private boolean recursive = false;
    private int startIndex = 0;
    private int maxRecords = 0;
    private boolean skipServerSettings = false;



    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof AttributesSearchCriteria))
            return false;
        if(obj == this)
            return true;

        AttributesSearchCriteria target = (AttributesSearchCriteria) obj;

        return new EqualsBuilder()
                .append(getHolder(), target.getHolder())
                .append(getNames(), target.getNames())
                .append(getGroups(), target.getGroups())
                .append(getStartIndex(), target.getStartIndex())
                .append(getMaxRecords(), target.getMaxRecords())
                .append(isEffective(), target.isEffective())
                .append(isRecursive(), target.isRecursive())
                .append(isSkipServerSettings(), target.isSkipServerSettings())
                .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getHolder())
                .append(getNames())
                .append(getGroups())
                .append(getStartIndex())
                .append(getMaxRecords())
                .append(isEffective())
                .append(isRecursive())
                .append(isSkipServerSettings())
                .append(isSkipServerSettings())
                .toHashCode();
    }



    private AttributesSearchCriteria(Builder builder) {
        setNames(builder.names);
        setHolder(builder.holder);
        setGroups(builder.groups);
        setEffective(builder.effective);
        setRecursive(builder.recursive);
        setStartIndex(builder.startIndex);
        setMaxRecords(builder.maxRecords);
        setSkipServerSettings(builder.skipServerSettings);
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public boolean isEffective() {
        return effective;
    }

    public void setEffective(boolean effective) {
        this.effective = effective;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public boolean isSkipServerSettings() {
        return skipServerSettings;
    }

    public void setSkipServerSettings(boolean skipServerSettings) {
        this.skipServerSettings = skipServerSettings;
    }

    public static class Builder {
        private String holder = null;
        private Set<String> names = null;
        private Set<String> groups = null;
        private boolean effective = false;
        private boolean recursive = false;
        private int startIndex = 0;
        private int maxRecords = 0;
        private boolean skipServerSettings = false;

        public AttributesSearchCriteria build() {
            return new AttributesSearchCriteria(this);
        }

        public Builder setHolder(String holder) {
            this.holder = holder;
            return this;
        }

        public Builder setGroups(Set<String> groups) {
            this.groups = groups;
            return this;
        }

        public Builder setEffective(boolean effectives) {
            this.effective = effectives;
            return this;
        }

        public Builder setRecursive(boolean recursive) {
            this.recursive = recursive;
            return this;
        }

        public Builder setMaxRecords(int maxRecords) {
            this.maxRecords = maxRecords;
            return this;
        }

        public Builder setNames(Set<String> names) {
            this.names = names;
            return this;
        }

        public Builder setStartIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public Builder setSkipServerSettings(boolean skipServerSettings) {
            this.skipServerSettings = skipServerSettings;
            return this;
        }
    }
}
