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

package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.jaspersoft.jasperserver.api.metadata.common.domain.SourceDescriptor;

import java.util.Set;

import static com.google.common.collect.Sets.filter;

/**
 *
 * @author schubar
 *
 */
public class SrcSets {

    private static final Predicate<SourceDescriptor> RESOLVED_PREDICATE = new Predicate<SourceDescriptor>() {
        @Override
        public boolean apply(SourceDescriptor input) {
            return input.isResolved();
        }
    };
    
    private static final Predicate<SourceDescriptor> UNRESOLVED_PREDICATE = Predicates.not(RESOLVED_PREDICATE);

    private static final Function<SourceDescriptor, String> TO_ID_FUNCTION = new Function<SourceDescriptor, String>() {
        @Override
        public String apply(SourceDescriptor input) {
            return input.getId();
        }
    };

    public static <T extends SourceDescriptor> boolean hasUnresolved(Set<T> sources) {
        return !filterUnresolved(sources).isEmpty();

    }
    public static  <T extends SourceDescriptor> Set<T> filterUnresolved(Set<T> sources) {
        Preconditions.checkNotNull(sources);

        return filter(sources, UNRESOLVED_PREDICATE);
    }

    public static  <T extends SourceDescriptor> Set<T> filterResolved(Set<T> sources) {
        Preconditions.checkNotNull(sources);

        return filter(sources, RESOLVED_PREDICATE);
    }

    public static  <T extends SourceDescriptor> Set<String> transformToId(Set<T> sources) {
        Preconditions.checkNotNull(sources);

        return FluentIterable.from(sources).transform(TO_ID_FUNCTION).toSet();
    }

    public static  <T extends SourceDescriptor> Set<T> resolveAll(Set<T> sources) {
        Preconditions.checkNotNull(sources);

        for (T src : sources) {
            src.resolved();
        }
        return sources;
    }
}
