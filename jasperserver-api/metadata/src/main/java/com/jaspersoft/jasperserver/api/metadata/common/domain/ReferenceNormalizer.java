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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ReferenceDescriptor;

import java.util.Set;

/**
 * @author schubar
 *
 */
public interface ReferenceNormalizer<T extends NormalizationStrategy<?>> {

    /**
     * Collects all objects that may reference/depend a external source (e.g. field metadata or field data from data source).
     * Relation to the {@link SourceDescriptor source} is represented by {@link ReferenceDescriptor}.
     * Which is also used describe common ID for potentially different object hierarchies.
     *
     * @return ReferenceDescriptor
     *
     *  @see ReferenceDescriptor
     *  @see SourceDescriptor
     */
    <R extends ReferenceDescriptor> Set<R> getDependentReferences();

    /**
     *  Iterate over all know references and check if expected source (e.g. field) is present in metadata.
     *  Uses {@link SourceDescriptor} to keep track of results of last check
     *
     *  @see ReferenceDescriptor
     *  @see SourceDescriptor
     */
    void checkReferences();

    /**
     *  Default strategy
     *  @see NormalizationStrategy
     */
    boolean normalizeReferences();

    boolean normalizeReferences(T strategy);
//    boolean normalizeReferences();
}
