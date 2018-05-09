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

package com.jaspersoft.jasperserver.search.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.util.RepositoryLabelIDHelper;
import com.jaspersoft.jasperserver.search.service.FolderService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Folders management service.
 *
 * @author Stas Chubar
 * @author Yuriy Plakosh
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class FolderServiceImpl extends BaseService implements FolderService {

    private static final Log log = LogFactory.getLog(FolderServiceImpl.class);

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Folder create(String parentUri, String label, String description) {
        if (parentUri == null || label == null || description == null) {
            return null;
        }

        label = label.trim();
        description = description.trim();

        if (isObjectLabelExist(parentUri, label)) {
            throw new JSException("jsexception.folder.duplicate.name", new Object[]{label, parentUri});
        }

        Folder folder = new FolderImpl();

        String generatedId = RepositoryLabelIDHelper.generateIdBasedOnLabel(repositoryService, parentUri, label);

        folder.setParentFolder(parentUri);
        folder.setName(generatedId);
        folder.setLabel(label);
        folder.setDescription(description);

        repositoryService.saveFolder(null, folder);

        return folder;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Folder update(String folderUri, String label, String description) {
        Folder folder = repositoryService.getFolder(null, folderUri);

        if (folder == null) {
            throw new JSException("jsexception.folder.not.found", new Object[]{folderUri});
        }

        if (isObjectLabelExist(folder.getParentURI(), label, folderUri)) {
            throw new JSException("jsexception.folder.duplicate.name", new Object[]{label, folder.getParentURI()});
        }

        if ((!label.equals(folder.getLabel())) || (!description.equals(folder.getDescription()))) {
            folder.setLabel(label);
            folder.setDescription(description);

            repositoryService.saveFolder(null, folder);
        }

        return folder;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(String folderUri) {
        if (folderUri != null && folderUri.length() == 0) {
            return;
        }

        repositoryService.deleteFolder(null, folderUri);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void copy(String folderUri, String destinationFolderUri) {
        String parentFolderUri = destinationFolderUri;

        int lastIndex = folderUri.lastIndexOf("/");
        if (lastIndex != -1) {
            destinationFolderUri = destinationFolderUri + "/" + folderUri.substring(lastIndex + 1);
        }

        Folder folder = repositoryService.getFolder(null, folderUri);
        if (folder == null) {
            throw new JSException("jsexception.folder.not.found", new Object[]{folderUri});
        }

        String folderLabel = folder.getLabel();
        // check if the label already exist in the destination folder
        if (isObjectLabelExist(parentFolderUri, folderLabel)) {
            throw new JSException("jsexception.folder.duplicate.label",
							new Object[]{folderLabel, parentFolderUri});
        }

        repositoryService.copyFolder(null, folderUri, destinationFolderUri);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void move(String folderUri, String destinationFolderUri) {
        Folder folder = repositoryService.getFolder(null, folderUri);
        if (folder == null) {
            throw new JSException("jsexception.folder.not.found", new Object[]{folderUri});
        }

        String folderLabel = folder.getLabel();
        // check if the label already exist in the destination folder
        if (isObjectLabelExist(destinationFolderUri, folderLabel)) {
            throw new JSException("jsexception.folder.duplicate.label",
							new Object[]{folderLabel, destinationFolderUri});
        }

        repositoryService.moveFolder(null, folderUri, destinationFolderUri);
    }

    private String getParentUri(String folderUri) {
        return folderUri.substring(0, folderUri.lastIndexOf('/'));
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }
}