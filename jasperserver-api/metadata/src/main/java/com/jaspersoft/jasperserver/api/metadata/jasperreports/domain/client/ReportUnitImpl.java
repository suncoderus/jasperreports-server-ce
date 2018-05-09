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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client;

import com.google.common.collect.ImmutableSet;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.MarkAllInputControlsResolved;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.RefSets;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

import java.io.Serializable;
import java.util.*;

import static com.google.common.collect.Sets.newHashSet;
import static com.jaspersoft.jasperserver.api.metadata.common.domain.util.RefSets.checkAllSources;
import static com.jaspersoft.jasperserver.api.metadata.common.domain.util.RefSets.newSet;
import static com.jaspersoft.jasperserver.api.metadata.common.domain.util.RefSets.resolveAll;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class ReportUnitImpl extends ResourceImpl implements ReportUnit,
        ReferenceNormalizer<NormalizationStrategy<? super InputControlsContainer>>, Serializable {

    private ResourceReference dataSource = null;
    private ResourceReference query = null;
    private List inputControls;
    private ResourceReference mainReport = null;
    private List resources;
    private String inputControlRenderingView;
    private String reportRenderingView;
    private boolean alwaysPromptControls;
    private byte controlsLayout = LAYOUT_POPUP_SCREEN;
    private Long dataSnapshotId;

    public ReportUnitImpl() {
        resources = new ArrayList();
        inputControls = new ArrayList();
    }


    public ResourceReference getDataSource() {
        return dataSource;
    }

    public void setDataSource(ResourceReference dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(ReportDataSource dataSource) {
        setDataSource(new ResourceReference(dataSource));
    }

    public void setDataSourceReference(String referenceURI) {
        setDataSource(new ResourceReference(referenceURI));
    }

    public ResourceReference getQuery() {
        return query;
    }

    public void setQuery(ResourceReference query) {
        this.query = query;
    }

    public List getInputControls() {
        return inputControls;
    }

    public ResourceReference getMainReport() {
        return mainReport;
    }

    public InputControl getInputControl(String name) {
        ListIterator it = inputControlsLocalNameLocator(name).positionBefore();
        InputControl inputControl;
        if (it.hasNext()) {
            inputControl = (InputControl) ((ResourceReference) it.next()).getLocalResource();
        } else {
            inputControl = null;
        }
        return inputControl;
    }

    public void setMainReport(ResourceReference mainReport) {
        this.mainReport = mainReport;
    }

    public void setMainReport(FileResource report) {
        setMainReport(new ResourceReference(report));
    }

    public void setMainReportReference(String referenceURI) {
        setMainReport(new ResourceReference(referenceURI));
    }

    public List getResources() {
        return resources;
    }

    public void setResources(List resources) {
        this.resources = resources;
    }

    /* (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit#getInputControlRenderingView()
     */
    public String getInputControlRenderingView() {
        return inputControlRenderingView;
    }

    /* (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit#getReportRenderingView()
     */
    public String getReportRenderingView() {
        return reportRenderingView;
    }

    /* (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit#setInputControlRenderingView(java.lang.String)
     */
    public void setInputControlRenderingView(String viewName) {
        this.inputControlRenderingView = viewName;
    }

    /* (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit#setReportRenderingView(java.lang.String)
     */
    public void setReportRenderingView(String viewName) {
        this.reportRenderingView = viewName;
    }

    public boolean isAlwaysPromptControls() {
        return alwaysPromptControls;
    }

    public void setAlwaysPromptControls(boolean alwaysPromptControls) {
        this.alwaysPromptControls = alwaysPromptControls;
    }

    public byte getControlsLayout() {
        return controlsLayout;
    }

    public void setControlsLayout(byte controlsLayout) {
        this.controlsLayout = controlsLayout;
    }

    public void addResource(ResourceReference resourceReference) {
        resources.add(resourceReference);
    }

    public void addResourceReference(String referenceURI) {
        addResource(new ResourceReference(referenceURI));
    }

    public void addResource(FileResource resource) {
        addResource(new ResourceReference(resource));
    }

    public ResourceReference removeResource(int index) {
        return (ResourceReference) resources.remove(index);
    }

    public FileResource removeResourceLocal(String name) {
        FileResource removed = null;
        for (Iterator it = resources.iterator(); it.hasNext(); ) {
            ResourceReference resourceRef = (ResourceReference) it.next();
            if (resourceRef.isLocal()) {
                FileResource resource = (FileResource) resourceRef.getLocalResource();
                if (resource.getName().equals(name)) {
                    it.remove();
                    removed = resource;
                    break;
                }
            }
        }
        return removed;
    }

    public boolean removeResourceReference(String referenceURI) {
        boolean removed = false;
        for (Iterator it = resources.iterator(); it.hasNext(); ) {
            ResourceReference resourceRef = (ResourceReference) it.next();
            if (!resourceRef.isLocal() && resourceRef.getReferenceURI().equals(referenceURI)) {
                it.remove();
                removed = true;
                break;
            }
        }
        return removed;
    }

    public ValidationResult validate() {
        return null;
    }

    public void addInputControl(ResourceReference inputControlReference) {
        inputControls.add(inputControlReference);
    }

    public void addInputControlReference(String referenceURI) {
        addInputControl(new ResourceReference(referenceURI));
    }

    public void setInputControls(List inputControls) {
        this.inputControls = inputControls;
    }

    public void addInputControl(InputControl inputControl) {
        addInputControl(new ResourceReference(inputControl));
    }

    public ResourceReference removeInputControl(int index) {
        return (ResourceReference) inputControls.remove(index);
    }

    public InputControl removeInputControlLocal(String name) {
        ListIterator it = inputControlsLocalNameLocator(name).positionBefore();
        InputControl removed;
        if (it.hasNext()) {
            removed = (InputControl) ((ResourceReference) it.next()).getLocalResource();
            it.remove();
        } else {
            removed = null;
        }
        return removed;
    }

    public boolean removeInputControlReference(String referenceURI) {
        ListIterator it = inputControlsReferenceLocator(referenceURI).positionBefore();
        boolean remove = it.hasNext();
        if (remove) {
            it.remove();
        }
        return remove;
    }

    protected static abstract class ResourceListLocator {
        private final List resources;

        public ResourceListLocator(List resources) {
            this.resources = resources;
        }

        public ListIterator positionBefore() {
            ListIterator it = resources.listIterator();
            while (it.hasNext()) {
                ResourceReference ref = (ResourceReference) it.next();
                if (matches(ref)) {
                    it.previous();
                    break;
                }
            }
            return it;
        }

        protected abstract boolean matches(ResourceReference ref);
    }

    protected static class ResourceListReferenceLocator extends ResourceListLocator {
        private final String referenceURI;

        public ResourceListReferenceLocator(List resources, String referenceURI) {
            super(resources);
            this.referenceURI = referenceURI;
        }

        protected boolean matches(ResourceReference ref) {
            return !ref.isLocal() && ref.getReferenceURI().equals(referenceURI);
        }
    }

    protected static class ResourceListLocalNameLocator extends ResourceListLocator {
        private final String localName;

        public ResourceListLocalNameLocator(List resources, String localName) {
            super(resources);
            this.localName = localName;
        }

        protected boolean matches(ResourceReference ref) {
            return ref.isLocal() && ref.getLocalResource().getName().equals(localName);
        }
    }

    protected ResourceListLocalNameLocator inputControlsLocalNameLocator(String name) {
        return new ResourceListLocalNameLocator(inputControls, name);
    }

    protected ResourceListReferenceLocator inputControlsReferenceLocator(String referenceURI) {
        return new ResourceListReferenceLocator(inputControls, referenceURI);
    }

    public void replaceInputControlLocal(String name, ResourceReference inputControlReference) {
        replaceInputControl(inputControlsLocalNameLocator(name), inputControlReference);
    }

    public void replaceInputControlReference(String referenceURI, ResourceReference inputControlReference) {
        replaceInputControl(inputControlsReferenceLocator(referenceURI), inputControlReference);
    }

    public void replaceInputControlLocal(String name, String newReferenceURI) {
        replaceInputControl(inputControlsLocalNameLocator(name), new ResourceReference(newReferenceURI));
    }

    public void replaceInputControlLocal(String name, InputControl inputControl) {
        replaceInputControl(inputControlsLocalNameLocator(name), new ResourceReference(inputControl));
    }

    public void replaceInputControlReference(String referenceURI, String newReferenceURI) {
        replaceInputControl(inputControlsReferenceLocator(referenceURI), new ResourceReference(newReferenceURI));
    }

    public void replaceInputControlReference(String referenceURI, InputControl inputControl) {
        replaceInputControl(inputControlsReferenceLocator(referenceURI), new ResourceReference(inputControl));
    }

    protected void replaceInputControl(ResourceListLocator locator, ResourceReference inputControlRef) {
        ListIterator it = locator.positionBefore();
        if (it.hasNext()) {
            it.next();
            it.set(inputControlRef);
        } else {
            it.add(inputControlRef);
        }
    }

    public FileResource getResourceLocal(String name) {
        FileResource resource = null;
        if (resources != null && !resources.isEmpty()) {
            for (Iterator iter = resources.iterator(); iter.hasNext(); ) {
                ResourceReference resourceRef = (ResourceReference) iter.next();
                if (resourceRef.isLocal()) {
                    FileResource res = (FileResource) resourceRef.getLocalResource();
                    if (res.getName().equals(name)) {
                        resource = res;
                        break;
                    }
                }
            }
        }
        return resource;
    }

    protected Class getImplementingItf() {
        return ReportUnit.class;
    }

    public Long getDataSnapshotId() {
        return dataSnapshotId;
    }

    public void setDataSnapshotId(Long dataSnapshotId) {
        this.dataSnapshotId = dataSnapshotId;
    }

    @Override
    public void checkReferences() {
        if (dataSource != null && dataSource.isLocal() &&  dataSource.getLocalResource() instanceof ReferenceNormalizer) {
            ((ReferenceNormalizer) dataSource.getLocalResource()).checkReferences();
        }
        resolveAll(checkAllSources(newSet(getInputControls())));
    }

    @Override
    public Set<ReferenceDescriptor> getDependentReferences() {
        return inputControls != null ? newSet(inputControls) : newHashSet();
    }

    @Override
    public boolean normalizeReferences() {
        return normalizeReferences(new MarkAllInputControlsResolved());
    }

    @Override
    public boolean normalizeReferences(NormalizationStrategy<? super InputControlsContainer> strategy) {
        return strategy.normalizeReferences(this);
    }
}
