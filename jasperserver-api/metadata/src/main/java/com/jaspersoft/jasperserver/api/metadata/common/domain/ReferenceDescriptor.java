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

package com.jaspersoft.jasperserver.api.metadata.common.domain;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Represents an object that have some sort of dependency to external resource (e.g. metadata, data source field)
 * Introduced to handle different object hierarchies.
 *
 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.util.RefSets
 *
 * @author schubar
 */
public interface ReferenceDescriptor {
    /**
     * Generic way to identify external resource
     *
     * @return ID
     */
    String getName();

    /**
     * Identifies dependency to external resource based on internal properties (available state).
     * We need this because internal properties may have been mutated externally. So we will call this before {@link ReferenceNormalizer#checkReferences()} to make sure we are aware about needed sources.
     */
    void checkSources();

    /**
     * In most cases objects that are represented by {@link ReferenceDescriptor} is going to be a value object.
     * Thous objects often keep only ID of the source and are going to be wired or managed by some manager class.
     * This manager usually will have access to the data source
     * and may reset {@link SourceDescriptor sources} based on the state of the manager.
     *
     * @param sources
     *
     * @see SourceDescriptor
     */
    void setSources(Set<SourceDescriptor> sources);

    /**
     * Used to query information about sources of this object.
     *
     * @return
     *
     * @see SourceDescriptor
     */
    ImmutableSet<SourceDescriptor> getSources();
}
