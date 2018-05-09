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

package com.jaspersoft.jasperserver.export.modules.repository;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.util.diagnostic.DiagnosticSnapshotPropertyHelper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.core.util.PathUtils;
import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.ExportImportWarningCode;
import com.jaspersoft.jasperserver.export.modules.repository.beans.FolderBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.RepositoryObjectPermissionBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceReferenceBean;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ResourceExporter extends BaseExporterModule implements ResourceExportHandler {
	public static final String DIAGNOSTIC = "diagnostic";

	private static final String FOLDER_RESOURCE_TYPES = "folder";
	private static final Log log = LogFactory.getLog(ResourceExporter.class);

	protected Set<String> uris = new HashSet<String>();
	protected ResourceModuleConfiguration configuration;

	protected Map<String, Set<String>> dependencies = new TreeMap<String, Set<String>>();
	protected Set<String> notAccessibleResources = new TreeSet<String>();
	protected String currentResource;

	private MessageSource messageSource;
	private String unavailableExportOperationMessage;

	private Set<String> exportResourceTypes = new HashSet<String>();
	private String skipDependentResource;

	protected static class QueuedUri {
		private final String uri;
		private final boolean ignoreMissing;

		public QueuedUri(String uri, boolean ignoreMissing) {
			this.uri = uri;
			this.ignoreMissing = ignoreMissing;
		}

		public String getUri() {
			return uri;
		}

		public boolean isIgnoreMissing() {
			return ignoreMissing;
		}
	}

	protected static class UrisQueue {
		private final LinkedList queue = new LinkedList();

		public boolean isEmpty() {
			return queue.isEmpty();
		}

		public void push(String uri, boolean ignoreMissing) {
			queue.addLast(new QueuedUri(uri, ignoreMissing));
		}

		public QueuedUri pop() {
			return (QueuedUri) queue.removeFirst();
		}

		public QueuedUri removeLast() {
			return (QueuedUri) queue.removeLast();
		}

		public int size() {
			return queue.size();
		}
	}

	private String urisArgument;
	private String permissionsArgument;

	protected boolean exportPermissions;
	protected UrisQueue urisQueue;
	private Set exportedURIs;

	public void init(ExporterModuleContext moduleContext) {
		super.init(moduleContext);
		initProcess();

		String[] resourceTypes = getParameterValues(ImportExportServiceImpl.RESOURCE_TYPES);
		if (resourceTypes != null) {
			exportResourceTypes = new HashSet<String>(Arrays.asList(resourceTypes));
		}

        if (exportEverything) {
			uris.add("/");
        } else if (getParameterValues(getUrisArgument()) != null) {
			uris.addAll(Arrays.asList(getParameterValues(getUrisArgument())));
        } else if (hasParameter(ImportExportServiceImpl.RESOURCE_TYPES)) {
			uris.add("/");
		}
        exportPermissions = exportEverything || hasParameter(getPermissionsArgument());
	}

	protected boolean isToProcess() {
		return (uris != null && !uris.isEmpty());
	}

	public void process() {
        mkdir(configuration.getResourcesDirName());

        for (String uri : uris) {
            processUri(uri, true, false);
        }
		if (!isExportWithoutDependencies()) {
			exportDependencies();
		}
		handleResourceWithBrokenDependencies();
	}

	private void exportDependencies() {
		while (!urisQueue.isEmpty()) {
			QueuedUri queuedUri = urisQueue.pop();
			try {
				processUri(queuedUri.getUri(), false, queuedUri.isIgnoreMissing());
			} catch (AccessDeniedException e) {
				notAccessibleResources.add(queuedUri.getUri());
				markExported(queuedUri.getUri());
			}
		}
	}

	protected void initProcess() {
		urisQueue = new UrisQueue();
		exportedURIs = new HashSet();
	}

	private void handleResourceWithBrokenDependencies() {
		Set<String> brokenDependencies = new TreeSet<String>();
		for (String uri : notAccessibleResources) {
			checkBrokenResource(brokenDependencies, uri);
		}

		for (String resource : brokenDependencies) {
			exportContext.getExportTask().getWarnings().
					add(new WarningDescriptor().setCode(ExportImportWarningCode.EXPORT_BROKEN_DEPENDENCY.toString()).
							setParameters(new String[]{resource}));
		}
	}

	private void checkBrokenResource(Set<String> brokenDependencies, String uri) {
		Set<String> result = dependencies.get(uri);
		if (result != null) {
			for (String resourceUri : result) {
				checkBrokenResource(brokenDependencies, resourceUri);
			}
			brokenDependencies.addAll(result);
		}
	}

	public void markExported(String uri) {
		exportedURIs.add(uri);
	}

	public boolean alreadyExported(String uri) {
		return exportedURIs.contains(uri);
	}

	public void processUri(String uri, boolean entry, boolean ignoreMissing) {
		if (alreadyExported(uri) || exportFilter.excludeFolder(uri, exportParams)) {
			return;
		}

		ResourceLookup resource = getResourceFromRepository(uri);
		if (resource == null) {
			Folder folder = configuration.getRepository().getFolder(executionContext, uri);
			if (folder == null) {
                // Adding non localized message cause import-export tool does not support localization.
                StringBuilder message = new StringBuilder("URI ");
                message.append(uri);
                message.append(" was not found in the repository");
				if (!ignoreMissing) {
					throw new JSException(message.toString());
				}

                message.append(", skipping from export");
				commandOut.info(message.toString());
			} else {
				if (exportFolder(folder)) {
					if (entry) {
						addFolderIndexElement(folder.getURIString());
					}
					writeIndexesForAllParentFolders(folder.getParentFolder());
				}
			}
		} else {
			if (!isToExportResource(resource)) return;

			if (exportResource(resource)) {
				if (entry) {
					addResourceIndexElement(resource.getURIString());
				}
				writeIndexesForAllParentFolders(resource.getParentFolder());
			}
		}
	}

	private ResourceLookup getResourceFromRepository(String uri) {
		if (uri.equals(Folder.SEPARATOR)) return null;

		PathUtils.SplittedPath splittedPath = PathUtils.splitPathToFolderAndName(uri);

		FilterCriteria filter = FilterCriteria.createFilter();
		filter.addFilterElement(FilterCriteria.createParentFolderFilter(splittedPath.parentPath));
		filter.addFilterElement(FilterCriteria.createPropertyEqualsFilter("name", splittedPath.name));

		ResourceLookup[] resources = configuration.getRepository().findResource(executionContext, filter);
		if (resources == null || resources.length == 0) {
			return null;
		} else {
			return resources[0];
		}
	}

	protected void addFolderIndexElement(String uri) {
		Element folderElement = getIndexElement().addElement(configuration.getFolderIndexElement());
		folderElement.addText(uri);
	}

	protected boolean exportFolder(Folder folder) {
		String uri = folder.getURIString();
		if (alreadyExported(uri) || exportFilter.excludeFolder(folder.getURIString(), exportParams)) {
			return false;
		}

		commandOut.info("Exporting repository folder " + uri);

		List<Folder> subFolders = null;
		ResourceLookup[] resources = null;
		if (exportFilter.toExportContents(folder)) {
			subFolders = getSubfolders(uri);
			resources = getFolderResources(uri);
		}

		exportFolders(subFolders);
		resources = exportResources(filterResources(resources));

		subFolders = getExportedFolders(subFolders);

		if (isNotFolderToWrite(subFolders, resources)) return false;

		writeFolder(folder, subFolders, resources);
		markExported(uri);
		return true;
	}

	private boolean isNotFolderToWrite(List<Folder> subFolders, ResourceLookup[] resources) {
		return  !exportResourceTypes.isEmpty() &&
				!exportResourceTypes.contains(FOLDER_RESOURCE_TYPES) &&
				(subFolders == null || subFolders.isEmpty()) &&
				(resources == null || resources.length == 0);
	}

	private List<Folder> getExportedFolders(List<Folder> folders) {
		List<Folder> result = new ArrayList<Folder>();
		if (folders == null) return result;

		for (Folder folder : folders) {
			if (exportedURIs.contains(folder.getURIString())) {
				result.add(folder);
			}
		}
		return result;
	}

	private ResourceLookup[] filterResources(ResourceLookup[] resources) {
		if (resources == null) return null;

		List<ResourceLookup> result = new ArrayList<ResourceLookup>();
		for (ResourceLookup res : resources) {
			if (isToExportResource(res)) {
				result.add(res);
			}
		}
		return result.toArray(new ResourceLookup[result.size()]);
	}

	private boolean isToExportResource(ResourceLookup res) {
		if (!exportFilter.isToExportResource(res.getResourceType(), exportResourceTypes)) {
			if (exportParams.hasParameter(skipDependentResource)) {
				if (dependencies.containsKey(res.getURIString())) {
					notAccessibleResources.add(res.getURIString());
				}
				return false;
			} else {
				if (!dependencies.containsKey(res.getURIString())) {
					return false;
				}
			}
		}
		return true;
	}

	protected List getSubfolders(String uri) {
		return configuration.getRepository().getSubFolders(executionContext, uri);
	}

	protected ResourceLookup[] getFolderResources(String uri) {
		FilterCriteria filter = FilterCriteria.createFilter();
		filter.addFilterElement(FilterCriteria.createParentFolderFilter(uri));
		return configuration.getRepository().findResource(executionContext, filter);
	}

	protected void writeFolder(Folder folder, List subFolders, ResourceLookup[] resources) {
		FolderBean bean = createFolderBean(folder, subFolders, resources);

		String outputFolder = mkdir(configuration.getResourcesDirName(), folder.getURIString());
		serialize(bean, outputFolder, configuration.getFolderDetailsFileName(), configuration.getSerializer());
	}

	protected FolderBean createFolderBean(Folder folder, List subFolders, ResourceLookup[] resources) {
		FolderBean bean = new FolderBean();
		bean.copyFrom(folder);

		String[] subFolderNames;
		if (subFolders == null || subFolders.isEmpty()) {
			subFolderNames = null;
		} else {
			subFolderNames = new String[subFolders.size()];
			int c = 0;
			for (Iterator it = subFolders.iterator(); it.hasNext(); ++c) {
				Folder subFolder = (Folder) it.next();
				subFolderNames[c] = subFolder.getName();
			}
		}
		bean.setSubFolders(subFolderNames);

		String[] resourceNames;
		if (resources == null || resources.length == 0) {
			resourceNames = null;
		} else {
			resourceNames = new String[resources.length];
			for (int i = 0; i < resources.length; i++) {
				resourceNames[i] = resources[i].getName();
			}
		}
		bean.setResources(resourceNames);

		if (exportPermissions) {
			RepositoryObjectPermissionBean[] permissions = handlePermissions(folder);
			bean.setPermissions(permissions);
			bean.setExportedWithPermissions(true);
		}

		return bean;
	}

	protected void exportFolders(List subFolders) {
		if (subFolders != null && !subFolders.isEmpty()) {
			for (Iterator it = subFolders.iterator(); it.hasNext();) {
				Folder subFolder = (Folder) it.next();
				exportFolder(subFolder);
			}
		}
	}

	protected ResourceLookup[] exportResources(ResourceLookup[] resources) {
		if (resources == null || resources.length == 0) return resources;

		List<ResourceLookup> result = new ArrayList<ResourceLookup>();
		for (ResourceLookup resLookup : resources) {
			if (exportResource(resLookup)) {
				result.add(resLookup);
			}
		}
		return result.toArray(new ResourceLookup[result.size()]);
	}

	protected void addResourceIndexElement(String uri) {
		Element folderElement = getIndexElement().addElement(configuration.getResourceIndexElement());
		folderElement.addText(uri);
	}

	protected boolean exportResource(ResourceLookup lookup) {
		String uri = lookup.getURIString();
		if (!alreadyExported(uri)) {
			Resource resource = configuration.getRepository().getResource(executionContext, uri);
			exportResource(resource);
			return true;
		}
		return false;
	}

	protected void exportResource(Resource resource) {
		String uri = resource.getURIString();
		if (alreadyExported(uri)) {
			return;
		}
		commandOut.info("Exporting repository resource " + uri);

		writeResource(resource);

		markExported(uri);
	}

	protected void writeResource(Resource resource) {
		currentResource = resource.getURIString();
		ResourceBean bean = createResourceBean(resource);

        String parentFolder = bean.getFolder();
		String folder = mkdir(configuration.getResourcesDirName(), parentFolder);

		serialize(bean, folder, getResourceFileName(resource), configuration.getSerializer());
	}

	protected ResourceBean createResourceBean(Resource resource){
		ResourceBean bean = handleResource(resource);
		if (exportPermissions) {
			RepositoryObjectPermissionBean[] permissions = handlePermissions(resource);
			bean.setPermissions(permissions);
            bean.setExportedWithPermissions(true);
		}
		return bean;
	}

	// writing indexes for all parent folders to save labels and descriptions
	protected void writeIndexesForAllParentFolders(String parentFolder) {
		while (parentFolder != null && !parentFolder.equals("") && !parentFolder.equals("/")) {
			Folder fld = configuration.getRepository().getFolder(executionContext, parentFolder);

			if (fld == null) return;

			if (!alreadyExported(fld.getURIString())) {
				writeFolder(fld, null, null);
				markExported(fld.getURIString());
			}
			parentFolder = fld.getParentFolder();
		}
	}

	public ResourceBean handleResource(Resource resource) {
		ResourceBean bean = (ResourceBean) configuration.getCastorBeanMappings().newObject(resource.getClass());

		bean.setDiagnostic(hasParameter(DIAGNOSTIC));
		// this works for report with diagnostic proposals
		if (hasParameter(DIAGNOSTIC)) {
			putAttributeToContext(
					DiagnosticSnapshotPropertyHelper.ATTRIBUTE_IS_DIAG_SNAPSHOT);
		}
		bean.copyFrom(resource, this);
		return bean;
	}

	private void  putAttributeToContext(String value) {
		if (!getExecutionContext().getAttributes().contains(value)) {
			getExecutionContext().getAttributes().add(value);
		}
	}

	protected String getResourceFileName(Resource resource) {
		return resource.getName() + ".xml";
	}

	public ResourceReferenceBean handleReference(ResourceReference reference) {
		ResourceReferenceBean beanRef;
		if (reference == null) {
			beanRef = null;
		} else if (reference.isLocal()) {
			beanRef = handleLocalResource(reference);
		} else {
			beanRef = handleExternalReference(reference);
		}
		return beanRef;
	}

	protected ResourceReferenceBean handleLocalResource(ResourceReference reference) {
		ResourceBean resourceDTO = handleResource(reference.getLocalResource());
		return new ResourceReferenceBean(resourceDTO);
	}

	protected ResourceReferenceBean handleExternalReference(ResourceReference reference) {
		String uri = reference.getReferenceURI();
		handleExternalReferenceUri(uri);
		return new ResourceReferenceBean(uri);
	}

	protected void handleExternalReferenceUri(String uri) {
		addResourceToDependenciesMap(uri);
		if (exportParams.hasParameter(skipDependentResource)) {
			notAccessibleResources.add(uri);
			return;
		}
		queueResource(uri);
	}

	protected void addResourceToDependenciesMap(String uri) {
		if (dependencies.get(uri) == null) {
			Set<String> set = new TreeSet<String>();
			set.add(currentResource);
			dependencies.put(uri, set);
		} else {
			dependencies.get(uri).add(currentResource);
		}
	}

	public void queueResource(String uri) {
		queueResource(uri, false);
	}

	public void queueResource(String uri, boolean ignoreMissing) {
		if (!alreadyExported(uri)) {
			urisQueue.push(uri, ignoreMissing);
		}
	}

	public String handleData(Resource resource, String dataProviderId) {
		ResourceDataProvider dataProvider = configuration.getResourceDataProvider(dataProviderId);

		InputStream dataIn = dataProvider.getData(exportContext, resource);
		String fileName = null;
		if (dataIn != null) {
			fileName = dataProvider.getFileName(resource);
			handleData(resource, fileName, dataIn);
		}

		return fileName;
	}

	public void handleData(Resource resource, String fileName, InputStream dataIn) {
		if (dataIn != null) {
			boolean closeInput = true;
			try {
				writeResourceData(resource, dataIn, fileName);
				closeInput = false;
				dataIn.close();
			} catch (IOException e) {
				log.error(e);
				throw new JSExceptionWrapper(e);
			} finally {
				if (closeInput) {
					try {
						dataIn.close();
					} catch (IOException e) {
						log.error(e);
					}
				}
			}
		}
	}

	protected void writeResourceData(Resource resource, InputStream dataIn, String outDataFilename) {
		String folder = mkdir(configuration.getResourcesDirName(), resource.getParentFolder());
		writeData(dataIn, folder, outDataFilename);
	}

	private boolean isExportWithoutDependencies() {
		return exportParams.hasParameter(skipDependentResource) && exportResourceTypes.isEmpty();
	}

	public String getUrisArgument() {
		return urisArgument;
	}

	public void setUrisArgument(String urisArgument) {
		this.urisArgument = urisArgument;
	}

	public ResourceModuleConfiguration getConfiguration() {
		return configuration;
	}

    protected Locale getLocale() {
        return Locale.getDefault();
    }

	public void setConfiguration(ResourceModuleConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getPermissionsArgument() {
		return permissionsArgument;
	}

	public void setPermissionsArgument(String permissionsArgument) {
		this.permissionsArgument = permissionsArgument;
	}

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setUnavailableExportOperationMessage(String unavailableExportOperationMessage) {
        this.unavailableExportOperationMessage = unavailableExportOperationMessage;
    }

	public void setSkipDependentResource(String skipDependentResource) {
		this.skipDependentResource = skipDependentResource;
	}
}
