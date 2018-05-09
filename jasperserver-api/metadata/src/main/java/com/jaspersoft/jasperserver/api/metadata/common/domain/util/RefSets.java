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

import com.google.common.base.*;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ReferenceDescriptor;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.SourceDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;

/**
 *
 * @author schubar
 *
 */
public class RefSets {

    public static final Predicate<ReferenceDescriptor> UNRESOLVED_PREDICATE = new Predicate<ReferenceDescriptor>() {
        @Override
        public boolean apply(ReferenceDescriptor input) {
            return SrcSets
                .hasUnresolved(input.getSources());
        }
    };

    public static final Predicate<ReferenceDescriptor> RESOLVED_PREDICATE = Predicates.not(UNRESOLVED_PREDICATE);

    public static <T extends ReferenceDescriptor> boolean hasUnresolved(Set<T> references) {
        return !filterUnresolved(references).isEmpty();
    }

    public static <T extends ReferenceDescriptor> Set<T> filterUnresolved(Set<T> references) {
        Preconditions.checkNotNull(references);

        return filter(references, UNRESOLVED_PREDICATE);
    }

    public static  Set<ReferenceDescriptor> filterResolved(Set<ReferenceDescriptor> references) {
        Preconditions.checkNotNull(references);

        return filter(references, RESOLVED_PREDICATE);
    }

    public static  <R extends SourceDescriptor, T extends ReferenceDescriptor> Set<R> collectSources(Set<T> references) {
        Preconditions.checkNotNull(references);

        return FluentIterable.from(references).transformAndConcat(new Function<T, Set<R>>() {
            @Override
            public Set<R> apply(T input) {
                return (Set<R>) input.getSources();
            }
        }).toSet();
    }

    public static  <R extends SourceDescriptor, T extends ReferenceDescriptor> Set<R> collectUnresolvedSources(Set<T> references) {
        Preconditions.checkNotNull(references);

        return FluentIterable.from(references).filter(UNRESOLVED_PREDICATE).transformAndConcat(new Function<T, Set<R>>() {
            @Override
            public Set<R> apply(T input) {
                return (Set<R>) input.getSources();
            }
        }).toSet();
    }

    public static  <T extends ReferenceDescriptor> Set<T> resolveAll(Set<T> references) {
        Preconditions.checkNotNull(references);

        for (T src : references) {
            SrcSets.resolveAll(src.getSources());
        }

        return references;
    }

    public static  <T extends ReferenceDescriptor> Set<T> checkAllSources(Set<T> references) {
        Preconditions.checkNotNull(references);

        for (T ref : references) {
            ref.checkSources();
        }

        return references;
    }

    public static <T extends ReferenceDescriptor> Set<T> newSet(List<ResourceReference> inputControls) {
        Preconditions.checkNotNull(inputControls);

        return (Set<T>) newHashSet(FluentIterable.from(inputControls).transform(new Function<ResourceReference, ReferenceDescriptor>() {
            @Override
            public ReferenceDescriptor apply(ResourceReference input) {
                if (input.isLocal() && input.getLocalResource() instanceof ReferenceDescriptor) {

                    return (ReferenceDescriptor) input.getLocalResource();
                } else {
                    return null;
                }
            }
        }).filter(Predicates.notNull()));
    }
//
//    public static Collection<String> toStrings(Set<SourceDescriptor> sourceDescriptors) {
//        return Collections2.transform(sourceDescriptors, new Function<SourceDescriptor, String>() {
//            @Override
//            public String apply(SourceDescriptor input) {
//                return input.getId();
//            }
//        });
//    }
//
//
    public static final ToName TO_NAME = new ToName();

    public static class ToName implements Function<ReferenceDescriptor, String> {
        @Override
        public String apply(ReferenceDescriptor input) {
            return input.getName();
        }
    }

}
