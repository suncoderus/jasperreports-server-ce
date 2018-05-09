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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Introduced to keep track if external resource is resolvable. Resource is resolved if it's present in metadata
 * (in some cases we mark sources as resolved without check because we cant perform it or don't care).
 *
 * Needed because information about actual source of some object is usually stored as a string or as part of some other complex string (e.g. expression)
 * we may not be able to identify source details without some external class or complex logic. At the same time this operation may be expensive.
 *
 * Use this object to remember  availability on the external resource.
 *
 * {@link SourceDescriptor} is identified by composite id + type.
 *
 * @author schubar
 *
 */
public class SourceDescriptor implements Serializable {

    public enum Kind {
        DIMENSION,
        MEASURE,
        ANY // use any we don't need to differentiate
        ;

        public static Kind valueOfOrAny(final String v) {
            try {
                return valueOf(v);
            } catch (IllegalArgumentException e) {
                return ANY;
            }
        }
    }

    private String id;
    private String type;
    private Kind kind = Kind.ANY;
    private boolean resolved = false;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isResolved() {
        return resolved;
    }

    private SourceDescriptor(String id, String type, Kind kind) {
        checkNotNull(id);

        this.id = id;
        this.type = type;
        this.kind = kind;
    }

    private SourceDescriptor(String id, String type, boolean resolved) {
        this(id, type, Kind.ANY);
        this.resolved = resolved;
    }

    private SourceDescriptor(String id, String type, Kind kind, boolean resolved) {
        this(id, type, kind);
        this.resolved = resolved;
    }


    private SourceDescriptor(SourceDescriptor sourceDescriptor) {
        this(sourceDescriptor.getId(), sourceDescriptor.getType(), sourceDescriptor.getKind(), sourceDescriptor.isResolved());
    }

    public static SourceDescriptor of(String id, String type) {
        return new SourceDescriptor(id, type, Kind.ANY);
    }

    public static SourceDescriptor of(String id, String type, Kind kind) {
        return new SourceDescriptor(id, type, kind);
    }

    public static SourceDescriptor of(String id) {
        return new SourceDescriptor(id, null, Kind.ANY);
    }

    public static SourceDescriptor ofUnresolvable(String id) {
        return new SourceDescriptor(id, null, Kind.ANY, false);
    }

    public static SourceDescriptor ofUnresolvable(String id, String type, Kind kind) {
        return new SourceDescriptor(id, type, kind,false);
    }

    public static SourceDescriptor of(SourceDescriptor originalSourceDescriptor) {
        return new SourceDescriptor(originalSourceDescriptor);
    }

    public static SourceDescriptor ofResolved(String id) {
        return new SourceDescriptor(id, null, Kind.ANY, true);
    }

    public static SourceDescriptor ofResolved(String id, String type) {
        return ofResolved(id, type, Kind.ANY);
    }

    public static SourceDescriptor ofResolved(String id, String type, Kind kind) {
        return new SourceDescriptor(id, type, kind,true);
    }

    public static SourceDescriptor ofResolved(SourceDescriptor originalSourceDescriptor) {
        return of(originalSourceDescriptor).resolved();
    }


    public SourceDescriptor resolved() {
        this.resolved = true;
        return this;
    }

    public SourceDescriptor unresolved() {
        this.resolved = false;
        return this;
    }

    public SourceDescriptor resolveAs(final String type, final Kind kind) {
        this.resolved = true;
        this.type = type;
        this.kind = kind;
        return this;
    }

    public SourceDescriptor classify(final String type, final Kind kind) {
        this.type = type;
        this.kind = kind;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceDescriptor that = (SourceDescriptor) o;
        return Objects.equal(id, that.id) && Objects.equal(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, type);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("type", type)
            .add("kind", kind)
            .add("resolved", resolved)
            .toString();
    }
}
