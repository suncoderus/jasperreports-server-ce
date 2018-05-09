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

package com.jaspersoft.jasperserver.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.ContextConfiguration;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

/**
 * @author Lucian Chirita
 * @version $Id$
 */
public abstract class BaseRepositoryTestTestNG extends BaseServiceSetupTestNG {

	private static final Log m_logger = LogFactory.getLog(BaseRepositoryTestTestNG.class);
	
	protected static final String TEMP_FOLDER_URI = "/unit_tests_tmp";
	protected static final String TEMP_FOLDER_LABEL = "Unit tests temporary folder";
	
	protected static final String USER_JOE = "joeuser";

	public BaseRepositoryTestTestNG() {
	}

	protected interface Callback {
		void execute() throws Exception;
	}
	
	protected void executeInTempFolder(Callback callback) throws Exception {
		createTempFolder();
		boolean deleteTemp = true;
		try {
			callback.execute();
			deleteTemp = false;
			deleteTempFolder();
		} finally {
			if (deleteTemp) {
				try {
					deleteTempFolder();
				} catch (Exception e) {
					m_logger.error("Unable to delete the temp folder", e);
				}
			}
		}
	}

	protected void createTempFolder() {
		Folder tempFolder = new FolderImpl();
		tempFolder.setURIString(TEMP_FOLDER_URI);
		tempFolder.setLabel(TEMP_FOLDER_LABEL);
		getUnsecureRepositoryService().saveFolder(getExecutionContext(), tempFolder);
	}
	
	protected void deleteTempFolder() {
		getUnsecureRepositoryService().deleteFolder(getExecutionContext(), TEMP_FOLDER_URI);
	}
	
	protected ExecutionContext getExecutionContext() {
		return new ExecutionContextImpl();
	}
	
	protected void setBaseResource(Resource resource, String name) {
		setBaseResource(resource, TEMP_FOLDER_URI, name);
	}
	
	protected void setBaseResource(Resource resource, String parent, String name) {
		resource.setParentFolder(parent);
		resource.setName(name);
		resource.setLabel(name);
	}
	
	protected Resource newResource(Class type, String name) {
		return newResource(type, TEMP_FOLDER_URI, name);
	}
	
	protected FileResource newFileResource(String name, String type, String dataResource) {
		FileResource resource = (FileResource) newResource(FileResource.class, name);
		resource.setFileType(type);
		
        InputStream data = getClass().getResourceAsStream(dataResource);
        try {
        	resource.readData(data);
        } finally {
        	try {
				data.close();
			} catch (IOException e) {
				m_logger.warn("Unable to close input stream", e);
			}
        }
        
        return resource;
	}
	
	protected Resource newResource(Class type, Folder parent, String name) {
		return newResource(type, parent.getURIString(), name);
	}
	
	protected Resource newResource(Class type, String parent, String name) {
		Resource res = getUnsecureRepositoryService().newResource(getExecutionContext(), type);
		setBaseResource(res, parent, name);
		return res;
	}
	
	protected Resource saveResource(Resource res) {
		getUnsecureRepositoryService().saveResource(getExecutionContext(), res);
		Resource saved = getUnsecureRepositoryService().getResource(getExecutionContext(), res.getURIString(), res.getClass());
		return saved;
	}
	
	protected Folder saveNewFolder(String name) {
		return saveNewFolder(TEMP_FOLDER_URI, name);
	}
	
	protected Folder saveNewFolder(Folder parent, String name) {
		return saveNewFolder(parent.getURIString(), name);
	}
	
	protected Folder saveNewFolder(String parent, String name) {
		FolderImpl folder = new FolderImpl();
		folder.setParentFolder(parent);
		folder.setName(name);
		folder.setLabel(name);
		getUnsecureRepositoryService().saveFolder(getExecutionContext(), folder);
		Folder saved = getUnsecureRepositoryService().getFolder(getExecutionContext(), folder.getURIString());
		return saved;
	}
	
	protected void assertResourceInexistent(String uri) {
		Resource resource = getUnsecureRepositoryService().getResource(getExecutionContext(), uri);
		assertNull(resource);
	}
	
	protected Resource assertResourceExists(Class type, Folder parent, String name) {
		return assertResourceExists(type, parent.getURIString(), name);
	}
	
	protected Resource assertResourceExists(Class type, String parent, String name) {
		return assertResourceExists(type, RepositoryUtils.concatenatePath(parent, name));
	}
	
	protected Resource assertResourceExists(Class type, String uri) {
		Resource resource = getUnsecureRepositoryService().getResource(getExecutionContext(), uri, type);
		assertNotNull(resource);
		assertEquals(uri, resource.getURIString());
		return resource;
	}
	
	protected void assertFolderInexistent(String uri) {
		Folder folder = getUnsecureRepositoryService().getFolder(getExecutionContext(), uri);
		assertNull(folder);
	}
	
	protected Folder assertFolderExists(Folder parent, String name) {
		return assertFolderExists(parent.getURIString(), name);
	}
	
	protected Folder assertFolderExists(String parent, String name) {
		return assertFolderExists(RepositoryUtils.concatenatePath(parent, name));
	}
	
	protected Folder assertFolderExists(String uri) {
		Folder folder = getUnsecureRepositoryService().getFolder(getExecutionContext(), uri);
		assertNotNull(folder);
		assertEquals(uri, folder.getURIString());
		return folder;
	}
	
	protected void assertFolderChildren(Folder folder, int subFoldersCount, int resourcesCount) {
		List subFolders = getUnsecureRepositoryService().getSubFolders(getExecutionContext(), folder.getURIString());
		if (subFoldersCount == 0) {
			assertTrue(subFolders == null || subFolders.isEmpty());
		} else {
			assertNotNull(subFolders);
			assertEquals(subFoldersCount, subFolders.size());
		}
		
		FilterCriteria filter = FilterCriteria.createFilter();
		filter.addFilterElement(FilterCriteria.createParentFolderFilter(folder.getURIString()));
		List resources = getUnsecureRepositoryService().loadResourcesList(getExecutionContext(), filter);
		if (resourcesCount == 0) {
			assertTrue(resources == null || resources.isEmpty());
		} else {
			assertNotNull(resources);
			assertEquals(resourcesCount, resources.size());
		}
	}
	
	protected String makeUri(String name) {
		return RepositoryUtils.concatenatePath(TEMP_FOLDER_URI, name);
	}
	
	protected void assertExternalReference(ResourceReference reference, String uri) {
		assertNotNull(reference);
		assertFalse(reference.isLocal());
		assertEquals(uri, reference.getReferenceURI());
	}
	
	protected Resource assertLocalReference(ResourceReference reference, Class type) {
		assertNotNull(reference);
		assertTrue(reference.isLocal());
		Resource res = reference.getLocalResource();
		assertNotNull(res);
		assertTrue(type.isInstance(res));
		return res;
	}
	
	protected void setUserPermission(int permissionMask, String uri, String username) {
		ObjectPermission permission = getObjectPermissionService().newObjectPermission(getExecutionContext());
		permission.setPermissionRecipient(getUser(username));
		permission.setURI(Resource.URI_PROTOCOL + ":" + uri);
		permission.setPermissionMask(permissionMask);
		getObjectPermissionService().putObjectPermission(getExecutionContext(), permission);
	}

	protected List getPermissions(Resource resource) {
		return getObjectPermissionService().getObjectPermissionsForObject(getExecutionContext(), resource);
	}
	
	protected void assertUserPermission(int permissionMask, Resource resource, String username) {
		List permissions = getObjectPermissionService().getObjectPermissionsForObjectAndRecipient(getExecutionContext(), 
				resource, getUser(username));
		assertNotNull(permissions);
		assertFalse(permissions.isEmpty());
		assertEquals(1, permissions.size());
		ObjectPermission permission = (ObjectPermission) permissions.get(0);
		assertNotNull(permission);
		assertEquals(permissionMask, permission.getPermissionMask());
	}
	
	protected void assertFileData(FileResource resource, String dataResource) throws IOException {
        ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
        InputStream data = getClass().getResourceAsStream(dataResource);
        try {
        	DataContainerStreamUtil.pipeData(data, dataBuffer);
        } finally {
        	try {
				data.close();
			} catch (IOException e) {
				m_logger.warn("Unable to close input stream", e);
			}
        }
        
        try {
            FileResourceData resourceData = getUnsecureRepositoryService().getResourceData(getExecutionContext(), resource.getURIString());
            assertNotNull(resourceData);
            assertTrue(resourceData.hasData());
            assertDataEqual(dataBuffer.toByteArray(), resourceData.getData());
        } catch (Exception e) {
            m_logger.error("Unable to get resource data, error = ", e);
        }

	}
	
	protected void assertDataEqual(byte[] data1, byte[] data2) {
		assertNotNull(data1);
		assertNotNull(data2);
		assertEquals(data1.length, data2.length);
		
		boolean eq = true;
		for (int i = 0; i < data1.length; i++) {
			if (data1[i] != data2[i]) {
				eq = false;
				break;
			}
		}
		assertTrue(eq);
	}
}
