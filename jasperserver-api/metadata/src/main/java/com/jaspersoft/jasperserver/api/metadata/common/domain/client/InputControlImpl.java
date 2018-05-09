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
package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;

import java.util.*;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Sets.difference;


/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class InputControlImpl extends ResourceImpl implements InputControl, InputControlsContainer {

    private byte inputControlType = TYPE_SINGLE_VALUE;
    private boolean isMandatory = false;
    private boolean isReadOnly = false;
    private boolean isVisible = true;
    private ResourceReference dataType = null;
    private ResourceReference listOfValues = null;
    private ResourceReference query = null;
    private List<String> queryVisibleColumns;
    private String queryValueColumn = null;
    private Object defaultValue = null;
    private List defaultValues = null;

    private Map<String, SourceDescriptor> sources;

    public InputControlImpl() {
        sources = new HashMap<String, SourceDescriptor>();
        queryVisibleColumns = new ArrayList<String>();
    }

    public InputControlImpl(InputControlImpl another) {
        super(another);

        if (another != null) {
            this.inputControlType = another.inputControlType;
            this.isMandatory = another.isMandatory;
            this.isReadOnly = another.isReadOnly;
            this.isVisible = another.isVisible;
            // TODO: set copy of references for dataType, listOfValues and query.
            this.dataType = another.dataType;
            this.listOfValues = another.listOfValues;
            this.query = another.query;
            this.queryVisibleColumns = another.queryVisibleColumns;
            this.queryValueColumn = another.queryValueColumn;
            this.defaultValue = another.defaultValue;
            this.defaultValues = another.defaultValues != null ? Arrays.asList(another.defaultValues.toArray().clone()) : null;

            this.sources = new HashMap<String, SourceDescriptor>();
            checkSources();
        }
    }

    public byte getInputControlType() {
        return inputControlType;
    }

    public void setInputControlType(byte type) {
        this.inputControlType = type;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public ResourceReference getDataType() {
        return dataType;
    }

    public void setDataType(ResourceReference dataType) {
        this.dataType = dataType;
    }

    public void setDataType(DataType dataType) {
        setDataType(new ResourceReference(dataType));
    }

    public void setDataTypeReference(String referenceURI) {
        setDataType(new ResourceReference(referenceURI));
    }

    public ResourceReference getListOfValues() {
        return listOfValues;
    }

    public void setListOfValues(ResourceReference values) {
        this.listOfValues = values;
    }

    public void setListOfValues(ListOfValues listOfValues) {
        setListOfValues(new ResourceReference(listOfValues));
    }

    public void setListOfValuesReference(String referenceURI) {
        setListOfValues(new ResourceReference(referenceURI));
    }

    public ResourceReference getQuery() {
        return query;
    }

    public void setQuery(ResourceReference query) {
        this.query = query;
    }

    public void setQuery(Query query) {
        setQuery(new ResourceReference(query));
    }

    public void setQueryReference(String referenceURI) {
        setQuery(new ResourceReference(referenceURI));
    }

    public String[] getQueryVisibleColumns() {
        return (String[]) queryVisibleColumns.toArray(new String[queryVisibleColumns.size()]);
    }

    public List<String> getQueryVisibleColumnsAsList() {
        return queryVisibleColumns;
    }

    public void addQueryVisibleColumn(String column) {
        queryVisibleColumns.add(column);
        checkSources();
    }

    public void removeQueryVisibleColumn(String column) {
        queryVisibleColumns.remove(column);
        checkSources();
    }

    public String getQueryValueColumn() {
        return queryValueColumn;
    }

    public void setQueryValueColumn(String column) {
        this.queryValueColumn = column;
        checkSources();
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object value) {
        this.defaultValue = value;
    }

    public List getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(List values) {
        this.defaultValues = values;
    }

    protected Class getImplementingItf() {
        return InputControl.class;
    }

    @Override
    public void checkSources() {
        ImmutableSet.Builder<String> medatadaBuilder = ImmutableSet.<String>builder();
        if (queryValueColumn != null) {
            medatadaBuilder.add(queryValueColumn);
        }
        if (queryVisibleColumns != null) {
            medatadaBuilder.addAll(filter(queryVisibleColumns, Predicates.<String>notNull()));
        }

        ImmutableSet<String> metadata = medatadaBuilder.build();
        // JRS-13572
        //sources.keySet().removeAll(removed) was failing because difference returns just the reference
        // to items in sources so, used immutableCopy instead.
        final ImmutableSet<String> added = difference(metadata, sources.keySet()).immutableCopy();
        final ImmutableSet<String> removed = difference(sources.keySet(), metadata).immutableCopy();

        for (String name : added) {
            sources.put(name, SourceDescriptor.of(name));
        }

        sources.keySet().removeAll(removed);
    }

    @Override
    public void setSources(Set<SourceDescriptor> sources) {
        Preconditions.checkNotNull(sources);
        this.sources = Maps.newHashMap();

        for (SourceDescriptor source: sources) {
            this.sources.put(source.getId(), source);
        }
    }

    @Override
    public ImmutableSet<SourceDescriptor> getSources() {
        return ImmutableSet.copyOf(sources.values());
    }

    @Override
    public ResourceReference getDataSource() {
        // will be resolved anyway
        return null;
    }

    @Override
    public List<ResourceReference> getInputControls() {
        return Arrays.asList(new ResourceReference(this));
    }

    @Override
    public void setInputControls(List<ResourceReference> inputControls) {
        // TODO cascading?
    }

    @Override
    public void addInputControl(InputControl inputControl) {
        // TODO cascading?
    }

    @Override
    public void addInputControl(ResourceReference inputControlReference) {
        // TODO cascading?
    }

    @Override
    public void addInputControlReference(String referenceURI) {
        // TODO cascading?
    }

    @Override
    public ResourceReference removeInputControl(int index) {
        // TODO cascading?
        return null;
    }

    @Override
    public boolean removeInputControlReference(String referenceURI) {
        // TODO cascading?
        return false;
    }

    @Override
    public InputControl removeInputControlLocal(String name) {
        // TODO cascading?
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputControl that = (InputControl) o;
        return Objects.equal(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
