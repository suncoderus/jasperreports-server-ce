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
package com.jaspersoft.jasperserver.dto.resources;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
// builder methods (all setters) should return concrete ClientReportUnit type.
// Definition of these concrete types (subclasses) assures, that cast is safe
@SuppressWarnings("unchecked")
public abstract class AbstractClientReportUnit<BuilderType extends AbstractClientReportUnit<BuilderType>>
        extends AbstractClientDataSourceHolder<BuilderType> implements ClientReferenceableReportUnit {

    private ClientReferenceableQuery query;
    private ClientReferenceableFile jrxml;
    private List<ClientReferenceableInputControl> inputControls;
    private Map<String, ClientReferenceableFile> files;
    private String inputControlRenderingView;
    private String reportRenderingView;
    private boolean alwaysPromptControls;
    private ControlsLayoutType controlsLayout = ControlsLayoutType.popupScreen;

    public AbstractClientReportUnit(AbstractClientReportUnit other) {
        super(other);

        ClientReferenceableQuery srcClientReferenceableQuery = other.getQuery();
        if (srcClientReferenceableQuery != null){
            if (srcClientReferenceableQuery instanceof ClientQuery){
                query = new ClientQuery((ClientQuery) srcClientReferenceableQuery);
            } else if (srcClientReferenceableQuery instanceof ClientReference){
                query = new ClientReference((ClientReference) srcClientReferenceableQuery);
            }
        }

        ClientReferenceableFile srcSchema = other.getJrxml();
        if (srcSchema != null) {
            if (srcSchema instanceof ClientReference){
                jrxml = new ClientReference((ClientReference) srcSchema);
            } else if (srcSchema instanceof ClientFile){
                jrxml = new ClientFile((ClientFile) srcSchema);
            }
        }

        final List<ClientReferenceableInputControl> srcInputControls = other.getInputControls();
        if(srcInputControls != null){
            inputControls = new ArrayList<ClientReferenceableInputControl>(other.getInputControls().size());
            for(ClientReferenceableInputControl inputControl : srcInputControls){
                ClientReferenceableInputControl clientReferenceableInputControlCopy = null;
                if (inputControl instanceof ClientInputControl){
                    clientReferenceableInputControlCopy = new ClientInputControl((ClientInputControl) inputControl);
                } else if (inputControl instanceof ClientReference){
                    clientReferenceableInputControlCopy = new ClientReference((ClientReference) inputControl);
                }
                if (clientReferenceableInputControlCopy != null){
                    inputControls.add(clientReferenceableInputControlCopy);
                }
            }
        }

        final Map<String, ClientReferenceableFile> srcFiles = other.getFiles();
        if (srcFiles != null){
            files = new HashMap<String, ClientReferenceableFile>();
            for (Map.Entry<String, ClientReferenceableFile> entry : srcFiles.entrySet()) {
                String key = entry.getKey();
                ClientReferenceableFile srcClientReferenceableFile = entry.getValue();

                ClientReferenceableFile clientReferenceableFileCopy = null;
                if (srcClientReferenceableFile instanceof ClientReference){
                    clientReferenceableFileCopy = new ClientReference((ClientReference) srcClientReferenceableFile);
                } else if (srcClientReferenceableFile instanceof ClientFile){
                    clientReferenceableFileCopy = new ClientFile((ClientFile) srcClientReferenceableFile);
                }

                if (clientReferenceableFileCopy != null){
                    files.put(key, clientReferenceableFileCopy);
                }
            }
        }

        this.inputControlRenderingView = other.getInputControlRenderingView();
        this.reportRenderingView = other.getReportRenderingView();
        this.alwaysPromptControls = other.isAlwaysPromptControls();
        this.controlsLayout = other.getControlsLayout();
    }

    public AbstractClientReportUnit() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AbstractClientReportUnit that = (AbstractClientReportUnit) o;

        if (alwaysPromptControls != that.alwaysPromptControls) return false;
        if (controlsLayout != that.controlsLayout) return false;
        if (files != null ? !files.equals(that.files) : that.files != null) return false;
        if (inputControlRenderingView != null ? !inputControlRenderingView.equals(that.inputControlRenderingView) : that.inputControlRenderingView != null)
            return false;
        if (inputControls != null ? !inputControls.equals(that.inputControls) : that.inputControls != null)
            return false;
        if (jrxml != null ? !jrxml.equals(that.jrxml) : that.jrxml != null) return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (reportRenderingView != null ? !reportRenderingView.equals(that.reportRenderingView) : that.reportRenderingView != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (jrxml != null ? jrxml.hashCode() : 0);
        result = 31 * result + (inputControls != null ? inputControls.hashCode() : 0);
        result = 31 * result + (files != null ? files.hashCode() : 0);
        result = 31 * result + (inputControlRenderingView != null ? inputControlRenderingView.hashCode() : 0);
        result = 31 * result + (reportRenderingView != null ? reportRenderingView.hashCode() : 0);
        result = 31 * result + (alwaysPromptControls ? 1 : 0);
        result = 31 * result + (controlsLayout != null ? controlsLayout.hashCode() : 0);
        return result;
    }

    public enum ControlsLayoutType {
        popupScreen, separatePage, topOfPage, inPage
    }

    @XmlElements({
            @XmlElement(name = "queryReference", type = ClientReference.class),
            @XmlElement(name = "query", type = ClientQuery.class)
    })
    public ClientReferenceableQuery getQuery() {
        return query;
    }

    public BuilderType setQuery(ClientReferenceableQuery query) {
        this.query = query;
        return (BuilderType) this;
    }

    @XmlElements({
            @XmlElement(name = "jrxmlFileReference", type = ClientReference.class),
            @XmlElement(name = "jrxmlFile", type = ClientFile.class)
    })
    public ClientReferenceableFile getJrxml() {
        return jrxml;
    }

    public BuilderType setJrxml(ClientReferenceableFile jrxml) {
        this.jrxml = jrxml;
        return (BuilderType) this;
    }

    @XmlElementWrapper(name = "inputControls")
    @XmlElements({
            @XmlElement(name = "inputControlReference", type = ClientReference.class),
            @XmlElement(name = "inputControl", type = ClientInputControl.class)
    })
    public List<ClientReferenceableInputControl> getInputControls() {
        return inputControls;
    }

    public BuilderType setInputControls(List<ClientReferenceableInputControl> inputControls) {
        this.inputControls = inputControls;
        return (BuilderType) this;
    }

    public BuilderType setFiles(Map<String, ClientReferenceableFile> files) {
        this.files = files;
        return (BuilderType) this;
    }

    @XmlJavaTypeAdapter(FilesMapXmlAdapter.class)
    @XmlElement(name = "resources")
    public Map<String, ClientReferenceableFile> getFiles() {
        return files;
    }

    public String getInputControlRenderingView() {
        return inputControlRenderingView;
    }

    public BuilderType setInputControlRenderingView(String inputControlRenderingView) {
        this.inputControlRenderingView = inputControlRenderingView;
        return (BuilderType) this;
    }

    public String getReportRenderingView() {
        return reportRenderingView;
    }

    public BuilderType setReportRenderingView(String reportRenderingView) {
        this.reportRenderingView = reportRenderingView;
        return (BuilderType) this;
    }

    public boolean isAlwaysPromptControls() {
        return alwaysPromptControls;
    }

    public BuilderType setAlwaysPromptControls(boolean alwaysPromptControls) {
        this.alwaysPromptControls = alwaysPromptControls;
        return (BuilderType) this;
    }

    public ControlsLayoutType getControlsLayout() {
        return controlsLayout;
    }

    public BuilderType setControlsLayout(ControlsLayoutType controlsLayout) {
        this.controlsLayout = controlsLayout;
        return (BuilderType) this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "query=" + query +
                ", jrxml=" + jrxml +
                ", inputControls=" + inputControls +
                ", files=" + files +
                ", inputControlRenderingView='" + inputControlRenderingView + '\'' +
                ", reportRenderingView='" + reportRenderingView + '\'' +
                ", alwaysPromptControls=" + alwaysPromptControls +
                ", controlsLayout=" + controlsLayout +
                ", version=" + getVersion() +
                ", permissionMask=" + getPermissionMask() +
                ", uri='" + getUri() + '\'' +
                ", label='" + getLabel() + '\'' +
                '}';
    }
}
