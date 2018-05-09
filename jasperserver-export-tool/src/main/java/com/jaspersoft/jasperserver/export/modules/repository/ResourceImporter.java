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

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.GlobalPropertiesListUpgradeExecutor;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.core.util.PathUtils;
import com.jaspersoft.jasperserver.core.util.PathUtils.SplittedPath;
import com.jaspersoft.jasperserver.dto.common.BrokenDependenciesStrategy;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.ExportImportWarningCode;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;
import com.jaspersoft.jasperserver.export.modules.repository.beans.FolderBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.PermissionRecipient;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceReferenceBean;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ResourceImporter extends BaseImporterModule implements ResourceImportHandler, InitializingBean {
	
	public final static String ATTRIBUTE_UPDATE_RESOURCES = "updateResources";
	public final static String THEME_UPDATE_FOLDERS_ATTRIBUTE_NAME = "themeUpdateFolders";

	protected final static Log log = LogFactory.getLog(ResourceImporter.class);
	
	protected ResourceModuleConfiguration configuration;
    protected TenantService tenantService;
	private String prependPathArg;
	private String updateArg;
	private String includeSettingsArg;
	private String skipThemesArgument;
    protected Set<String> rootSubTenantFolderUris;
    private Pattern orgPattern = Pattern.compile("((/" + TenantService.ORGANIZATIONS + "/[^/]+)*)");
    private Pattern themesPattern = Pattern.compile(".*/themes/.*");
	private GlobalPropertiesListUpgradeExecutor globalPropertiesListUpgradeExecutor;

	protected RepositoryService repository;
	protected String prependPath;
	private boolean update;

	protected Set importedURIs;
	private LinkedList folderQueue;
	private LinkedList resourceQueue;
    Deque<ResourceReference> createdResourcesStack;

	public void afterPropertiesSet() {
		this.repository = configuration.getRepository();
	}

	public void init(ImporterModuleContext moduleContext) {
		super.init(moduleContext);
		configuration.setApplicationContext((ApplicationContext) moduleContext.getAttributes().getAttribute("appContext"));
		prependPath = getPrependPath();
		update = getUpdateFlag();
        //Retrieve all root subtenants
        List<Tenant> allRootSubTenantList = tenantService.getAllSubTenantList(executionContext, TenantService.ORGANIZATIONS);
		rootSubTenantFolderUris = new HashSet<String>();
        for (Tenant tenant : allRootSubTenantList) {
            rootSubTenantFolderUris.add(tenant.getTenantFolderUri());
        }
	}

	protected String getPrependPath() {
		String path = getParameterValue(getPrependPathArg());
		if (path != null) {
			path = PathUtils.normalizePath(path);
			if (path.length() == 0 || path.equals(Folder.SEPARATOR)) {
				path = null;
			} else if (!path.startsWith(Folder.SEPARATOR)) {
				path = Folder.SEPARATOR + path;
			}
		}
		return path;
	}

	protected boolean getUpdateFlag() {
		return hasParameter(getUpdateArg());
	}

    protected boolean getIncludeSettingsFlag() {
		return hasParameter(getIncludeSettingsArg());
	}

    protected boolean skipThemes() {
		return hasParameter(getSkipThemesArgument());
	}

	protected boolean isUpdate() {
		return update;
	}
	
	public List<String> process() {
		initProcess();
		
		createPrependFolder();
		
		queueEntryFolders();
		queueEntryResources();
		
		while (!folderQueue.isEmpty() || !resourceQueue.isEmpty()) {
			if (folderQueue.isEmpty()) {
				String uri = (String) resourceQueue.removeFirst();
				try {
                    importResource(uri, false);
                } catch (ResourceBeanDataNotFoundException e) {
					String referenceUri = e.getMessage();
                    String msg;
                    if (uri.equals(referenceUri)) {
                        msg = "Reference resource \"" + referenceUri + "\" not found.";
						logWarning(ExportImportWarningCode.IMPORT_RESOURCE_NOT_FOUND,
								new String[]{referenceUri}, msg);
                    } else {
                        msg = "Reference resource \"" + referenceUri +
                                "\" not found when importing resource \"" + uri + "\".";
						logWarning(ExportImportWarningCode.IMPORT_REFERENCE_RESOURCE_NOT_FOUND,
								new String[]{referenceUri, uri}, msg);
                    }
                }
			} else {
				String uri = (String) folderQueue.removeFirst();
				importFolder(uri, true);
			}
		}

		upgradeGlobalPropertiesList();

        return null;
	}

    protected void queueEntryFolders() {
		List entryFolders = new ArrayList();
		for (Iterator it = indexElement.elementIterator(configuration.getFolderIndexElement()); it.hasNext(); ) {
			Element folderElement = (Element) it.next();
			entryFolders.add(folderElement.getText());
		}

		if (!entryFolders.isEmpty()) {
			Collections.sort(entryFolders);
			for (Iterator it = entryFolders.iterator(); it.hasNext();) {
				String uri = (String) it.next();
				folderQueue.addLast(uri);
			}
		}
	}

	protected void queueEntryResources() {
		for (Iterator it = indexElement.elementIterator(configuration.getResourceIndexElement()); it.hasNext(); ) {
			Element resourceElement = (Element) it.next();
			String uri = resourceElement.getText();
			resourceQueue.addLast(uri);
		}
	}

	protected void createPrependFolder() {
		if (prependPath != null) {
			LinkedList toCreateURIs = new LinkedList();
			for (String path = prependPath; 
					repository.getFolder(executionContext, path) == null; 
					path = PathUtils.splitPath(path).parentPath) {
				toCreateURIs.addFirst(path);
			}
			
			while(!toCreateURIs.isEmpty()) {
				String path = (String) toCreateURIs.removeFirst();
				Folder folder = createFolder(path);

				commandOut.debug("About to save folder " + path);
				try{
				    repository.saveFolder(executionContext, folder);
                } catch (Exception er) {
					if (!handledException(er, folder.getURIString())) {
						throwRuntimeException(er);
					}
                }
 			}
		}
	}

	protected void upgradeGlobalPropertiesList() {
		if (hasResourceBeanData(GlobalPropertiesListUpgradeExecutor.PATH)) {
			// Take resource from import file not from the repo because it can be ignored during import
			ListOfValues globalPropertiesList = (ListOfValues) createResource(
					readResourceBean(GlobalPropertiesListUpgradeExecutor.PATH));
			globalPropertiesListUpgradeExecutor.upgrade(globalPropertiesList, getIncludeSettingsFlag());
		}
	}

	@Override
	protected void initProcess() {
		super.initProcess();

		importedURIs = new HashSet();
		folderQueue = new LinkedList();
		resourceQueue = new LinkedList();
		createdResourcesStack = new ArrayDeque<ResourceReference>();
	}

	protected void importFolder(String uri, boolean detailsRequired) {
		if (importedURIs.contains(uri) || skipResource(uri)) {
			return;
		}

		String importUri = prependedPath(uri);

		// All organization folder uris have pattern /organizations/org_1../organizations/org2/folder except of template folder
		// which has pattern /organizations/org_template, so we have to skip it.
		String tenantFolderUri = "";
		int orgTeplateIndex = importUri.indexOf("/" + TenantService.ORGANIZATIONS + "/" + TenantService.ORG_TEMPLATE);
		if (orgTeplateIndex >= 0) {
			tenantFolderUri = importUri.substring(0, orgTeplateIndex);
		} else {
			Matcher m = orgPattern.matcher(importUri);
			if (m.find()) {
				tenantFolderUri = m.group(1);
			}
		}

		if (!(rootSubTenantFolderUris.contains(tenantFolderUri) || tenantFolderUri.equals(""))) {
			String msg = "Folder with the uri \"" + importUri + "\" is attached to not existing organization. Not imported";
			logWarning(ExportImportWarningCode.IMPORT_FOLDER_ATTACHED_NOT_EXIST_ORG, new String[]{importUri}, msg);
			return;
		}

		FolderBean folderBean = getFolderDetails(uri, detailsRequired);

		Folder folder = null;
		try {
			folder = repository.getFolder(executionContext, importUri);
		} catch (Exception ex) {
			if (handledException(ex, importUri)) {
				importedURIs.add(uri);
			} else {
				throwRuntimeException(ex);
			}
		}

        if (executionContext.getAttributes() == null) {
            executionContext.setAttributes(new ArrayList());
        }
        if (!executionContext.getAttributes().contains(RepositoryService.IS_IMPORTING)) {
            executionContext.getAttributes().add(RepositoryService.IS_IMPORTING);
        }

		if (folder == null) {
			ensureParent(uri);

			if (folderBean == null) {
				folder = createFolder(importUri);
			} else {
				folder = createFolder(folderBean);
			}

            try{
                commandOut.debug("About to save folder " + importUri);
                repository.saveFolder(executionContext, folder);
            } catch (Exception er) {
				if (!handledException(er, folder.getURIString())) {
					throwRuntimeException(er);
				}
            }

            if (folderBean != null) {
                setPermissions(folder, folderBean.getPermissions(), false);
            }

            commandOut.info("Created repository folder " + importUri);
		} else if (update && folderBean != null) {
			prepareFolderBeanBeforeFolderUpdate(folderBean, folder);

            folder.setLabel(folderBean.getLabel());
            folder.setDescription(folderBean.getDescription());

            try {
                commandOut.debug("About to save folder " + importUri);
                repository.saveFolder(executionContext, folder);
            } catch (Exception er) {
				if (!handledException(er, folder.getURIString())) {
					throwRuntimeException(er);
				}
            }
            commandOut.info("Updating folder " + importUri);

            if (folderBean.isExportedWithPermissions()){
				deleteObjectPermissions(folder);
            }
            setPermissions(folder, folderBean.getPermissions(), false);
        } else {
			if (folderBean != null) {
                commandOut.info("Folder " + importUri + " already exists, importing permissions only");
				
				setPermissions(folder, folderBean.getPermissions(), true);
			}
		}
		//If it`s theme folder - we need to remember them and post-process after resource import
		if (themesPattern.matcher(uri).matches() && !skipThemes()) {
			Set<String> themeUpdates = (HashSet<String>) getImportContext().getAttributes().getAttribute(THEME_UPDATE_FOLDERS_ATTRIBUTE_NAME);
			if (themeUpdates==null) {
				themeUpdates = new HashSet<String>();
			}
			themeUpdates.add(tenantFolderUri);
			getImportContext().getAttributes().setAttribute(THEME_UPDATE_FOLDERS_ATTRIBUTE_NAME,themeUpdates);
		}
		
		importedURIs.add(uri);
		
		if (folderBean != null) {
			queueSubFolders(uri, folderBean);
			
			queueResources(uri, folderBean);
		}
	}

	private void prepareFolderBeanBeforeFolderUpdate(FolderBean folderBean, Folder folder) {
		//Tenant id we import into
		String destinationTenantId = importContext.getImportTask()
				.getParameters().getParameterValue(ImportExportServiceImpl.ORGANIZATION);
		// check if we are importing into tenant destinationTenantId
		if (folder != null
				&& StringUtils.isNotBlank(destinationTenantId)
				&& !destinationTenantId.equals(TenantService.ORGANIZATIONS)) {

			// check for the root folder from import input
			if (folderBean.getParent() == null || Folder.SEPARATOR.equals(folderBean.getName())) {
				// it is the root folder from import input
				// for the root folder from import input, update only label and description
				String description = folderBean.getDescription();
				folderBean.copyFrom(folder);
				folderBean.setDescription(description);
			}
		}
	}

	protected boolean skipResource(String uri) {
		String brokenDependenciesStrategy = importContext.getImportTask()
				.getParameters().getParameterValue(ImportExportService.BROKEN_DEPENDENCIES);
		if (BrokenDependenciesStrategy.SKIP.getLabel().equals(brokenDependenciesStrategy)) {
			Set<String> brokenDependencies = importContext.getImportTask().getInputMetadata().getBrokenDependencies();
			if (brokenDependencies != null && brokenDependencies.contains(uri)) {
				return true;
			}
		}

		return themesPattern.matcher(uri).matches() && skipThemes();
	}

	protected void queueSubFolders(String uri, FolderBean folderBean) {
		String[] subFolders = folderBean.getSubFolders();
		if (subFolders != null) {
			for (int i = 0; i < subFolders.length; i++) {
				String subfolderURI = appendPath(uri, subFolders[i]);
				folderQueue.addLast(subfolderURI);
			}
		}
	}

	protected void queueResources(String uri, FolderBean folderBean) {
		String[] resources = folderBean.getResources();
		if (resources != null) {
			for (int i = 0; i < resources.length; i++) {
				String resourceUri = appendPath(uri, resources[i]);
				resourceQueue.addLast(resourceUri);
			}
		}
	}

	protected String prependedPath(String uri) {
		return PathUtils.concatPaths(prependPath, uri);
	}

	protected String appendPath(String uri, String name) {
		String subUri;
		if (uri.equals(Folder.SEPARATOR)) {
			subUri = Folder.SEPARATOR + name;
		} else {
			subUri = uri + Folder.SEPARATOR + name;
		}
		return subUri;
	}

	protected FolderBean getFolderDetails(String uri, boolean required) {
		FolderBean folderBean = null;
		String folderPath = PathUtils.concatPaths(configuration.getResourcesDirName(), uri);
		if (input.fileExists(folderPath, configuration.getFolderDetailsFileName())) {
			folderBean = (FolderBean) deserialize(folderPath, configuration.getFolderDetailsFileName(), configuration.getSerializer());
		} else {
			if (required) {
                String message = "Folder details for folder \"" + uri + "\"" +
						" were not found in the import information.";
				commandOut.error(message);
				throw new JSException(message);
			}
 		}

		return folderBean;
	}
	
	protected void ensureParent(String uri) {
		SplittedPath splitPath = PathUtils.splitPath(uri);
		if (splitPath != null && splitPath.parentPath != null) {
			importFolder(splitPath.parentPath, false);
		}
	}

	protected Folder createFolder(String uri) {
		Folder folder = new FolderImpl();
		SplittedPath splPath = PathUtils.splitPath(uri);
		folder.setParentFolder(splPath.parentPath);
		folder.setName(splPath.name);
		folder.setLabel(splPath.name);
		return folder;
	}
	
	protected Folder createFolder(FolderBean folderBean) {
		Folder folder = new FolderImpl();
		folderBean.copyTo(folder, importContext);
		folder.setCreationDate(folderBean.getCreationDate());
		folder.setUpdateDate(folderBean.getUpdateDate());
		folder.setParentFolder(prependedPath(folder.getParentFolder()));
		return folder;
	}

	protected String importResource(String uri, boolean ignoreMissing) {
		String importUri = prependedPath(uri);
		if (!importedURIs.contains(uri) && !skipResource(uri)) {
			if (ignoreMissing && !hasResourceBeanData(uri)) {
				String warningMessage = "Resource \"" + uri + "\" data missing from the catalog, skipping from import";
				//logWarning(ExportImportWarningCode.IMPORT_RESOURCE_DATA_MISSING, new String[]{uri}, warningMessage);
				commandOut.warn(warningMessage);
				importedURIs.add(uri);
			} else {
                if (!hasResourceBeanData(uri)) {
                    throw new ResourceBeanDataNotFoundException(uri);
                }

                boolean importingSettings = uri.equals(configuration.getUriOfSettingsList());
                if (importingSettings) {
                    update = getIncludeSettingsFlag();
                }

				Resource resource = null;
				try {
					resource = repository.getResource(executionContext, importUri);

					if (executionContext.getAttributes() == null) {
						executionContext.setAttributes(new ArrayList());
					}
					if (!executionContext.getAttributes().contains(RepositoryService.IS_IMPORTING)) {
						executionContext.getAttributes().add(RepositoryService.IS_IMPORTING);
					}

					if (resource == null) {
						ensureParent(uri);

						ResourceBean bean = readResourceBean(uri);
						resource = createResource(bean);

						commandOut.debug("About to save resource " + importUri);

						repository.saveResource(executionContext, resource);

						setPermissions(resource, bean.getPermissions(), false);
						commandOut.info("Imported resource " + importUri);
					} else if (update) {
						registerUpdateResource(importUri);

						ResourceBean bean = readResourceBean(uri);
						Resource updated = createResource(bean);

						if (resource.isSameType(updated)) {
							// We need to re-read the object to get the latest version
							// this is a fix to bug # 27803
							// http://bugzilla.jaspersoft.com/show_bug.cgi?id=27803
							Resource resource2 = repository.getResource(executionContext, importUri);
							updated.setVersion(resource2.getVersion());
							handleSubResources(resource, updated);
							commandOut.debug("About to save resource " + importUri);
							repository.saveResource(executionContext, updated);

							if (bean.isExportedWithPermissions()) {
								deleteObjectPermissions(resource);
							}
							setPermissions(resource, bean.getPermissions(), false);

							commandOut.info("Updated resource " + importUri);
						} else {
							String warningMessage = "Resource \"" + importUri + "\" already exists in " +
									"the repository and has a different type than in the catalog, not updating";
							logWarning(ExportImportWarningCode.IMPORT_RESOURCE_DIFFERENT_TYPE, new String[]{importUri}, warningMessage);
						}
					} else {
						commandOut.warn("Resource \"" + importUri + "\" already exists, not importing");
					}
				} catch (Exception er) {
					if (!handledException(er, resource == null ? importUri : resource.getURIString())) {
						throwRuntimeException(er);
					}
				}

                if (importingSettings) {
                    update = getUpdateFlag();
                }
				
				importedURIs.add(uri);
			}
		}
		return importUri;
	}

	protected void deleteObjectPermissions(Resource resource) {
		@SuppressWarnings("unchecked")
		List<ObjectPermission> objectPermissions = configuration.getPermissionService().
				getObjectPermissionsForObject(executionContext, resource);
		if (objectPermissions != null) {
			for (ObjectPermission objectPermission : objectPermissions) {
				try {
					configuration.getPermissionService().deleteObjectPermission(executionContext, objectPermission);
				} catch (Exception ex) {
					if (ex instanceof AccessDeniedException
							|| ExceptionUtils.getRootCause(ex) instanceof  AccessDeniedException) {
						PermissionRecipient permissionRecipient =
								toPermissionRecipient(objectPermission.getPermissionRecipient());

						String msg = "Access denied. Cannot delete permission for resource \"" +
								objectPermission.getURI() + "\" with " +
								permissionRecipient.getRecipientType() + " "
								+ makeRecipientId(permissionRecipient);

						logWarning(ExportImportWarningCode.IMPORT_ACCESS_DENIED, new String[]{objectPermission.getURI()}, msg);
					} else {
						throwRuntimeException(ex);
					}
				}
			}
		}
	}

	protected boolean handledException(Exception ex, String uri) {
		String msg = null;
		ExportImportWarningCode warningCode = null;

		String[] warningParams = new String[]{uri};
		if (ex instanceof AccessDeniedException
				|| ExceptionUtils.getRootCause(ex) instanceof AccessDeniedException) {
			msg  = "Access denied for \"" + uri + "\"";
			warningCode = ExportImportWarningCode.IMPORT_ACCESS_DENIED;
		}
		else if (ex instanceof JSResourceNotFoundException
				|| ExceptionUtils.getRootCause(ex) instanceof JSResourceNotFoundException) {
			Throwable rootCause = ex;
			if (!(ex instanceof JSResourceNotFoundException)) {
				rootCause = ExceptionUtils.getRootCause(ex);
			}
			msg  = "Resource \"" + uri + "\" cannot be found.";
			warningCode = ExportImportWarningCode.IMPORT_RESOURCE_NOT_FOUND;
			JSException jsException = (JSException) rootCause;
			String referenceUri = extractResourceUriFromException(jsException);
			if (referenceUri != uri) {
				msg = "Reference resource \"" + referenceUri + "\" not found when importing resource \"" + uri + "\".";
				warningCode = ExportImportWarningCode.IMPORT_REFERENCE_RESOURCE_NOT_FOUND;
				warningParams = new String[]{referenceUri, uri};
			}
		}
		else if (ex instanceof JSDuplicateResourceException
				|| ExceptionUtils.getRootCause(ex) instanceof JSDuplicateResourceException) {
			if (StringUtils.equals(ex.getMessage(), "jsexception.folder.already.exists")
					|| StringUtils.equals(ex.getMessage(), "jsexception.resource.already.exists")) {
				String warningMessage = "Resource \"" + uri + "\" already exists in the repository";
				logWarning(ExportImportWarningCode.IMPORT_RESOURCE_DIFFERENT_TYPE, new String[]{uri}, warningMessage);
			}
			else {
				logWarning(ExportImportWarningCode.IMPORT_ACCESS_DENIED, new String[]{uri}, "Access denied for \"" + uri + "\"");
			}
			return true;
		}
		else if (ex.getMessage() != null && HibernateRepositoryServiceImpl.JS_EXCEPTION_FOLDER_TOO_LONG_URI.equals(ex.getMessage())) {
			Exception rootException = ex;
			Throwable rootCause = ExceptionUtils.getRootCause(ex);
			if (rootCause instanceof Exception) {
				rootException = (Exception) rootCause;
			}
			if (rootException instanceof JSException) {
				msg = "The URI \"" + uri + "\" is too long. The maximum length is "
						+ ((JSException) rootException).getArgs()[0] + " characters.";
				warningParams = new String[]{uri, Integer.toString((Integer)((JSException) rootException).getArgs()[0])};
			} else {
				msg = "The URI \"" + uri + "\" is too long.";
			}

			warningCode = ExportImportWarningCode.IMPORT_RESOURCE_URI_TOO_LONG;
		}

		if (msg != null) {
			logWarning(warningCode, warningParams, msg);
			return true;
		}

		return false;
	}

	protected String extractResourceUriFromException(JSException jsException) {
		if (jsException.getArgs() != null && jsException.getArgs().length > 0 && jsException.getArgs()[0] instanceof String) {
			String uri = (String) jsException.getArgs()[0];
			if (StringUtils.isEmpty(uri))
				return uri.replace("\"","");
		}
		return null;
	}

	private void throwRuntimeException(Exception ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		} else {
			throw new JSExceptionWrapper(ex);
		}
	}

	protected void handleSubResources(Resource resource, Resource clientResource) {
		;
	}

	protected void registerUpdateResource(String resourceUri) {
		Set updateResources = (Set) getContextAttributes().getAttribute(
				ATTRIBUTE_UPDATE_RESOURCES);
		if (updateResources == null) {
			updateResources = new HashSet();
			getContextAttributes().setAttribute(ATTRIBUTE_UPDATE_RESOURCES,
					updateResources);
		}
		
		updateResources.add(resourceUri);
	}

	protected Resource createResource(ResourceBean bean) {
		Class resourceItf = configuration.getCastorBeanMappings().getInterface(bean.getClass());
		Resource resource = repository.newResource(executionContext, resourceItf);

        this.createdResourcesStack.push(new ImportResourceReference(resource));
        try {
            bean.copyTo(resource, this);
        } finally {
            this.createdResourcesStack.pop();
        }
        resource.setParentFolder(prependedPath(resource.getParentFolder()));
		return resource;
	}

	protected boolean hasResourceBeanData(String uri) {
		String resourceFileName = getResourceFileName(uri);
		return input.fileExists(configuration.getResourcesDirName(), resourceFileName);
	}
	
	protected ResourceBean readResourceBean(String uri) {
		String resourceFileName = getResourceFileName(uri);
		ResourceBean bean = (ResourceBean) deserialize(configuration.getResourcesDirName(), resourceFileName, configuration.getSerializer());

		return bean;
	}
	
	protected String getResourceFileName(String uri) {
		return uri + ".xml";
	}

    @Override
    public String getSourceJsVersion() {
        return super.getSourceJsVersion();
    }

    @Override
    public String getTargetJsVersion() {
        return super.getTargetJsVersion();
    }

    public ResourceReference handleReference(ResourceReferenceBean beanReference) {
		ResourceReference reference;
		if (beanReference == null) {
			reference = null;
		} else if (beanReference.isLocal()) {
			ResourceBean localResBean = beanReference.getLocalResource();
			Resource localRes = createResource(localResBean);
			reference = new ResourceReference(localRes);
		} else {
			String referenceURI = beanReference.getExternalURI();
			if (referenceURI == null || referenceURI.equals("")) {
                return null;
            }

			String brokenDependenciesStrategy = importContext.getImportTask()
					.getParameters().getParameterValue(ImportExportService.BROKEN_DEPENDENCIES);
			boolean ignoreMissing = BrokenDependenciesStrategy.SKIP.getLabel().equals(brokenDependenciesStrategy)
					|| BrokenDependenciesStrategy.INCLUDE.getLabel().equals(brokenDependenciesStrategy);

			 // Import referenced URI, or the containing URI
			 // In some cases the containing URI needs to be imported instead of the specific resource
			 if (beanReference.useContainingURI()) {
			     importResource(beanReference.getContainingURI(), ignoreMissing);
			 } else {
				 importResource(referenceURI, ignoreMissing);
			 }

			if (!importContext.getNewGeneratedTenantIds().isEmpty()) {
				referenceURI = TenantStrHolderPattern.TENANT_FOLDER_URI
						.replaceWithNewTenantIds(importContext.getNewGeneratedTenantIds(), referenceURI);
			}

			reference = new ResourceReference(prependedPath(referenceURI));
		}
		return reference;
	}

	public byte[] handleData(ResourceBean resourceBean, String dataFile, String providerId) {
		String filename = PathUtils.concatPaths(resourceBean.getFolder(), dataFile);
		InputStream dataInput = getFileInput(configuration.getResourcesDirName(), filename);
		boolean closeInput = true;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataContainerStreamUtil.pipeData(dataInput, out);
			
			closeInput = false;
			dataInput.close();
			
			return out.toByteArray();
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeInput) {
				try {
					dataInput.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}

	public Resource handleResource(ResourceBean resource) {
		Resource res = createResource(resource);
		return res;
	}

	public String handleResource(String uri) {
		String brokenDependenciesStrategy = importContext.getImportTask()
				.getParameters().getParameterValue(ImportExportService.BROKEN_DEPENDENCIES);
		boolean ignoreMissing = BrokenDependenciesStrategy.INCLUDE.getLabel().equals(brokenDependenciesStrategy);

		return handleResource(uri, ignoreMissing);
	}

	public String handleResource(String uri, boolean ignoreMissing) {
		return importResource(uri, ignoreMissing);
	}

	public ResourceModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ResourceModuleConfiguration configuration) {
		this.configuration = configuration;
	}

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public String getPrependPathArg() {
		return prependPathArg;
	}

	public void setPrependPathArg(String prependPathArg) {
		this.prependPathArg = prependPathArg;
	}

	public String getUpdateArg() {
		return updateArg;
	}

	public void setUpdateArg(String updateArg) {
		this.updateArg = updateArg;
	}

    public String getIncludeSettingsArg() {
        return includeSettingsArg;
    }

    public void setIncludeSettingsArg(String includeSettingsArg) {
        this.includeSettingsArg = includeSettingsArg;
    }

    public String getSkipThemesArgument() {
        return skipThemesArgument;
    }

    public void setSkipThemesArgument(String skipThemesArgument) {
        this.skipThemesArgument = skipThemesArgument;
    }

	public void setGlobalPropertiesListUpgradeExecutor(GlobalPropertiesListUpgradeExecutor
															   globalPropertiesListUpgradeExecutor) {
		this.globalPropertiesListUpgradeExecutor = globalPropertiesListUpgradeExecutor;
	}

	@Override
    public Resource getHandledResource(String uri) {
        ResourceReference lookup = new ImportResourceReference(uri);
        for (ResourceReference reference : this.createdResourcesStack) {
            if (reference.equals(lookup)){
                return reference.getLocalResource();
            }
        }

        return null;
    }
    
    public boolean fileExists(String filename) {
    	return input.fileExists(configuration.getResourcesDirName(), filename);
    }

	@Override
	public ImporterModuleContext getImportContext() {
		return importContext;
	}
}
