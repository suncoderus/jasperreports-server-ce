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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.dto.resources.AbstractClientReportUnit;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableInputControl;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableQuery;
import com.jaspersoft.jasperserver.dto.resources.ClientReportUnit;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class GenericReportUnitResourceConverter
        <ServerReportUnitType extends ReportUnit, ClientReportUnitType extends AbstractClientReportUnit<ClientReportUnitType>>
        extends DataSourceHolderResourceConverter<ServerReportUnitType, ClientReportUnitType> {
    private static final String EXTRACT_RESOURCE_NAME_FROM_URI_PATTERN = ".+/([^/]+)$";

    @Override
    protected void setDataSourceToResource(ResourceReference dataSourceReference, ServerReportUnitType resource) {
        resource.setDataSource(dataSourceReference);
    }

    @Override
    protected ResourceReference getDataSourceFromResource(ServerReportUnitType resource) {
        return resource.getDataSource();
    }

    @Override
    protected ServerReportUnitType resourceSpecificFieldsToServer(ClientReportUnitType clientObject, ServerReportUnitType resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        resultToUpdate.setAlwaysPromptControls(clientObject.isAlwaysPromptControls());
        final byte controlsLayout;
        if (clientObject.getControlsLayout() != null) {
            switch (clientObject.getControlsLayout()) {
                case popupScreen:
                    controlsLayout = ReportUnit.LAYOUT_POPUP_SCREEN;
                    break;
                case separatePage:
                    controlsLayout = ReportUnit.LAYOUT_SEPARATE_PAGE;
                    break;
                case inPage:
                    controlsLayout = ReportUnit.LAYOUT_IN_PAGE;
                    break;
                case topOfPage:
                    controlsLayout = ReportUnit.LAYOUT_TOP_OF_PAGE;
                    break;
                default:
                    controlsLayout = ReportUnit.LAYOUT_POPUP_SCREEN;
            }
            resultToUpdate.setControlsLayout(controlsLayout);
        }
        resultToUpdate.setInputControlRenderingView(clientObject.getInputControlRenderingView());
        resultToUpdate.setReportRenderingView(clientObject.getReportRenderingView());
        resultToUpdate.setQuery(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableQuery.class)
                .toServer(clientObject.getQuery(), resultToUpdate.getQuery(), options));
        resultToUpdate.setMainReport(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class)
                .addReferenceRestriction(new ResourceReferenceConverter.FileTypeRestriction(ClientFile.FileType.jrxml))
                .toServer(clientObject.getJrxml(), resultToUpdate.getMainReport(), options));
        List<ResourceReference> inputControls = null;
        final List<ClientReferenceableInputControl> clientInputControls = clientObject.getInputControls();
        if (clientInputControls != null && !clientInputControls.isEmpty()) {
            inputControls = new ArrayList<ResourceReference>(clientInputControls.size());
            final ResourceReferenceConverter<ClientReferenceableInputControl> inputControlReferenceConverter =
                    resourceReferenceConverterProvider.getConverterForType(ClientReferenceableInputControl.class);
            for (ClientReferenceableInputControl inputControlReference : clientInputControls) {
                inputControls.add(inputControlReferenceConverter.toServer(inputControlReference,
                        findReference(resultToUpdate.getInputControls(), inputControlReference.getUri()), options));
            }
        }
        resultToUpdate.setInputControls(inputControls);
        // Raw List is used in core classes, but it is of type List<ResourceReference>. So, cast is safe.
        @SuppressWarnings("unchecked")
        final List<ResourceReference> resources = resultToUpdate.getResources();
        resultToUpdate.setResources(convertResourcesToServer(clientObject.getFiles(), resources, options));
        return resultToUpdate;
    }

    protected List<ResourceReference> convertResourcesToServer(Map<String, ClientReferenceableFile> clientResources,
            List<ResourceReference> serverReferencesToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        List<ResourceReference> result = new ArrayList<ResourceReference>();
        if (clientResources != null && !clientResources.isEmpty()) {
            Map<String, ResourceReference> serverResourcesMap = getServerResourcesAsMap(serverReferencesToUpdate);
            for (String currentResourceName : clientResources.keySet()) {
                final ClientReferenceableFile clientObject = clientResources.get(currentResourceName);
                ResourceReference serverObjectToUpdate = serverResourcesMap.get(currentResourceName);
                if (serverObjectToUpdate != null && serverObjectToUpdate.isLocal()
                        && !(serverObjectToUpdate.getLocalResource() instanceof FileResource)) {
                    // this case is invalid. Local resources should be of type FileResource only.
                    // but if wrong type comes, then let's proceed like no server object to update.
                    serverObjectToUpdate = null;
                }
                if (serverObjectToUpdate != null && serverObjectToUpdate.isLocal()
                        && serverObjectToUpdate.getLocalResource() instanceof FileResource
                        && !((FileResource) serverObjectToUpdate.getLocalResource()).isReference()
                        && clientObject instanceof ClientReference
                        && !(serverObjectToUpdate.getLocalResource().getURIString().equals(clientObject.getUri()))) {
                    // if current server resource is local non reference file but client object points to another one,
                    // then this local file should be removed and new one should be created, reference one
                    serverObjectToUpdate = null;
                }
                final ResourceReferenceConverter<ClientReferenceableFile> fileResourceReferenceConverter =
                        resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class);
                if (clientObject instanceof ClientReference &&
                        (serverObjectToUpdate == null || serverObjectToUpdate.isLocal())) {
                    // Server side FileResource can be a reference.
                    // It is done for ReportUnit's resources referencing in JRXML.
                    // Currently this is the only known case, where FileResource can be a reference.
                    // So, handle this tricky case here.
                    if (serverObjectToUpdate == null) {
                        // new local FileResource-reference should be created
                        // ensure, that reference points to a valid file resource
                        fileResourceReferenceConverter.toServer(clientObject, options);
                        FileResource fileResourceReference = (FileResource) objectFactory.newResource(null, FileResource.class);
                        fileResourceReference.setName(currentResourceName);
                        fileResourceReference.setLabel(currentResourceName);
                        fileResourceReference.setReferenceURI(clientObject.getUri());
                        serverObjectToUpdate = new ResourceReference(fileResourceReference);
                    } else {
                        final FileResource localResource = (FileResource) serverObjectToUpdate.getLocalResource();
                        if (localResource.isReference() && !clientObject.getUri().equals(localResource.getReferenceURI())) {
                            // reference URI is changed. Let's validate reference target
                            // ensure, that reference points to a valid file resource
                            fileResourceReferenceConverter.toServer(clientObject, options);
                            // update reference if conversion above succeed
                            ((FileResource) serverObjectToUpdate.getLocalResource()).setReferenceURI(clientObject.getUri());
                        }
                    }
                } else {
                    // normal file reference case.
                    serverObjectToUpdate = fileResourceReferenceConverter.toServer(clientObject, serverObjectToUpdate, options);
                    if (serverObjectToUpdate != null && serverObjectToUpdate.getLocalResource() != null) {
                        // explicitly set file resource name. It is required for the case if local resource should be created.
                        // In update case the name is the same, in create case original name doesn't matter,
                        // it should be overridden by currentResourceName
                        serverObjectToUpdate.getLocalResource().setName(currentResourceName);
                    }
                }
                result.add(serverObjectToUpdate);
            }
            if (serverReferencesToUpdate == null) {
                // no existing resources, let's create new ArrayList with resources
                serverReferencesToUpdate = new ArrayList<ResourceReference>(result);
            } else {
                // there was a list with resources.
                // remove all previously existing resources
                serverReferencesToUpdate.clear();
                // add all updated/created resources
                serverReferencesToUpdate.addAll(result);
            }
        } else if (serverReferencesToUpdate != null) {
            // here is no resources in incoming ClientReportUnit.
            // So, let's remove all existing resources.
            serverReferencesToUpdate.clear();
        }
        return serverReferencesToUpdate;
    }

    protected Map<String, ResourceReference> getServerResourcesAsMap(List<ResourceReference> list) {
        final HashMap<String, ResourceReference> result = new HashMap<String, ResourceReference>();
        if (list != null) {
            for (ResourceReference reference : list) {
                final String name = reference.isLocal() ? reference.getLocalResource().getName() :
                        getResourceNameForReference(reference);
                result.put(name, reference);
            }
        }
        return result;
    }

    protected String getResourceNameForReference(ResourceReference reference) {
        String name;
        final String referenceURI = reference.getReferenceURI();
        Matcher matcher = Pattern.compile(EXTRACT_RESOURCE_NAME_FROM_URI_PATTERN).matcher(referenceURI);
        if (matcher.find()) {
            name = matcher.group(1);
        } else {
            throw new IllegalStateException("Invalid reference URI '" + referenceURI + "'. Resource name can't be extracted");
        }
        return name;
    }

    @Override
    protected ClientReportUnitType resourceSpecificFieldsToClient(ClientReportUnitType client, ServerReportUnitType serverObject, ToClientConversionOptions options) {
        client.setAlwaysPromptControls(serverObject.isAlwaysPromptControls());
        final ClientReportUnit.ControlsLayoutType controlsLayout;
        switch (serverObject.getControlsLayout()) {
            case ReportUnit.LAYOUT_POPUP_SCREEN:
                controlsLayout = ClientReportUnit.ControlsLayoutType.popupScreen;
                break;
            case ReportUnit.LAYOUT_SEPARATE_PAGE:
                controlsLayout = ClientReportUnit.ControlsLayoutType.separatePage;
                break;
            case ReportUnit.LAYOUT_IN_PAGE:
                controlsLayout = ClientReportUnit.ControlsLayoutType.inPage;
                break;
            case ReportUnit.LAYOUT_TOP_OF_PAGE:
                controlsLayout = ClientReportUnit.ControlsLayoutType.topOfPage;
                break;
            default:
                controlsLayout = ClientReportUnit.ControlsLayoutType.popupScreen;
        }
        client.setControlsLayout(controlsLayout);
        client.setInputControlRenderingView(serverObject.getInputControlRenderingView());
        client.setReportRenderingView(serverObject.getReportRenderingView());
        client.setQuery(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableQuery.class)
                .toClient(serverObject.getQuery(), options));
        // don't need to add restriction for JRXML type of file.
        // In case of toClient conversion restrictions are not called.
        client.setJrxml(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class)
                .toClient(serverObject.getMainReport(), options));
        List<ClientReferenceableInputControl> inputControls = null;
        final List<ResourceReference> serverInputControls = serverObject.getInputControls();
        if (serverInputControls != null && !serverInputControls.isEmpty()) {
            inputControls = new ArrayList<ClientReferenceableInputControl>(serverInputControls.size());
            final ResourceReferenceConverter<ClientReferenceableInputControl> inputControlResourceReferenceConverter =
                    resourceReferenceConverterProvider.getConverterForType(ClientReferenceableInputControl.class);
            for (ResourceReference inputControlReference : serverInputControls) {
                inputControls.add(inputControlResourceReferenceConverter.toClient(inputControlReference, options));
            }
        }
        client.setInputControls(inputControls);
        // core module uses raw List, but there is List<ResourceReference>. So, cast is safe.
        @SuppressWarnings("unchecked")
        final List<ResourceReference> resources = serverObject.getResources();
        client.setFiles(convertResourcesToClient(resources, options));
        return client;
    }

    protected Map<String, ClientReferenceableFile> convertResourcesToClient(List<ResourceReference> serverResources, ToClientConversionOptions options) {
        Map<String, ClientReferenceableFile> result = null;
        if (serverResources != null && !serverResources.isEmpty()) {
            ResourceReferenceConverter<ClientReferenceableFile> fileResourceReferenceConverter =
                    resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class);
            result = new HashMap<String, ClientReferenceableFile>(serverResources.size());
            for (ResourceReference reference : serverResources) {
                if (reference.isLocal()) {
                    // partially process file references here because local file can be reference too.
                    // I hope this is the only case, when FileResource can be a reference ;)
                    if (reference.getLocalResource() instanceof FileResource) {
                        FileResource file = (FileResource) reference.getLocalResource();
                        if (file.isReference()) {
                            result.put(file.getName(),
                                    fileResourceReferenceConverter.toClient(new ResourceReference(file.getReferenceURI()), options));
                        } else {
                            result.put(file.getName(), fileResourceReferenceConverter.toClient(new ResourceReference(file), options));
                        }
                    } else {
                        throw new IllegalStateException("ReportUnit can't contain references of type " +
                                reference.getLocalResource());
                    }
                } else {
                    result.put(getResourceNameForReference(reference), fileResourceReferenceConverter.toClient(reference, options));
                }
            }
        }
        return result;
    }
}
