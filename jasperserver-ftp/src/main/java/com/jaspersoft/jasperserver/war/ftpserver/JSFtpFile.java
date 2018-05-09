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

package com.jaspersoft.jasperserver.war.ftpserver;

import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import org.apache.ftpserver.ftplet.FtpFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author asokolnikov
 */
public class JSFtpFile implements FtpFile {

    private static final Date NULL_DATE = new Date(0);
    private static Pattern supportedPathPattern = Pattern.compile("^((/" + TenantService.ORGANIZATIONS + ")|(/" + TenantService.ORGANIZATIONS + "/([^/]+)))*(/|/themes|/themes/.*)?");


    private JSRepositoryFileSystemView repositoryFileSystemView;
    private String name;
    private String parentFolderURI;
    private boolean isFolder;
    private Date lastModified = NULL_DATE;
    private byte[] content;
    private boolean exists = true;
    private boolean readable = true;
    private boolean writable = true;
    private boolean removable;

    public JSFtpFile(JSRepositoryFileSystemView repositoryFileSystemView, Resource r, byte[] data) {
        this.repositoryFileSystemView = repositoryFileSystemView;
        this.name = r.getName();
        this.parentFolderURI = r.getParentFolder();
        this.lastModified = r.getUpdateDate();
        this.content = data;

        RepositorySecurityChecker chkr = repositoryFileSystemView.getFactory().getRepositorySecurityChecker();
        this.writable = chkr.isEditable(r);
        this.removable = chkr.isRemovable(r);
        if (r instanceof Folder) {
            this.isFolder = true;
            this.readable = chkr.isFolderReadable(this.getAbsolutePath());
        } else {
            this.isFolder = false;
            this.readable = chkr.isResourceReadable(this.getAbsolutePath());
        }
    }

    public JSFtpFile(JSRepositoryFileSystemView repositoryFileSystemView, Resource f) {
        this(repositoryFileSystemView, f, null);
    }

    protected JSFtpFile(JSRepositoryFileSystemView repositoryFileSystemView, String name, String parentFolder) {
        this.repositoryFileSystemView = repositoryFileSystemView;
        this.name = name;
        this.parentFolderURI = parentFolder;
        this.exists = false;
    }

    public String getAbsolutePath() {
        if (parentFolderURI == null) {
            return "/";
        }
        return (parentFolderURI.endsWith("/")) ?  parentFolderURI + name : parentFolderURI + "/" + name;
    }

    public String getName() {
        return name;
    }

    public boolean isHidden() {
        return false;
    }

    public boolean isDirectory() {
        return isFolder;
    }

    public boolean isFile() {
        return !isDirectory();
    }

    public boolean doesExist() {
        return exists;
    }

    public boolean isReadable() {
        return readable;
    }

    public boolean isWritable() {
        return writable;
    }

    public boolean isRemovable() {
        return removable;
    }

    public String getOwnerName() {
        return "jasperserver";
    }

    public String getGroupName() {
        return "jasperserver";
    }

    public int getLinkCount() {
        return 0;
    }

    public long getLastModified() {
        return lastModified == null ? 0 : lastModified.getTime();
    }

    public boolean setLastModified(long l) {
        throw new UnsupportedOperationException();
    }

    public long getSize() {
        return content != null ? content.length : 0;
    }

    public boolean mkdir() {
        //throw new UnsupportedOperationException();
        try {
            //Folder folder = (Folder) repositoryFileSystemView.getResourceFactory().newResource(null, Folder.class);
            Folder folder = new FolderImpl();
            folder.setName(name);
            folder.setLabel(name);
            folder.setCreationDate(new Date());
            folder.setParentFolder(parentFolderURI);
            repositoryFileSystemView.getFactory().getRepositoryService().saveFolder(null, folder);
            this.exists = true;
            this.isFolder = true;
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean delete() {
        //throw new UnsupportedOperationException();
        try {
            if (exists) {
                if (isFolder) {
                    repositoryFileSystemView.getFactory().getRepositoryService().deleteFolder(null, getAbsolutePath());
                } else {
                    repositoryFileSystemView.getFactory().getRepositoryService().deleteResource(null, getAbsolutePath());
                }
                exists = false;
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean move(FtpFile ftpFile) {
        throw new UnsupportedOperationException();
    }

    public static FtpFile getFtpFile(JSRepositoryFileSystemView repositoryFileSystemView, String repoURI) {

        try {
            Folder folder = repositoryFileSystemView.getFactory().getRepositoryService().getFolder(null, repoURI);
            if (folder != null && isSupported(repositoryFileSystemView, folder)) {
                FtpFile ftpFolder = new JSFtpFile(repositoryFileSystemView,folder);
                return ftpFolder;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            Resource resource = repositoryFileSystemView.getFactory().getRepositoryService().getResource(null, repoURI);
            if (resource != null && isSupported(repositoryFileSystemView, resource)) {
                byte[] data = null;
                if (repositoryFileSystemView.getFactory().isLoadContent()) {
                    FileResourceData fileResourceData = repositoryFileSystemView.getFactory().getRepositoryService().getResourceData(null, repoURI);
                    data = fileResourceData.getData();
                }
                FtpFile ftpFile = new JSFtpFile(repositoryFileSystemView, resource, data);
                return ftpFile;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // The file does not exist. Create an non-existing file reference
        int k = repoURI.lastIndexOf("/");
        String parentFolder = repoURI.substring(0, k);
        String name = repoURI.substring(k + 1);

        FtpFile dummyFile = new JSFtpFile(repositoryFileSystemView, name, parentFolder);
        return dummyFile;
    }

    public List<FtpFile> listFiles() {

        if (isFile()) {
            return Collections.EMPTY_LIST;
        }

        List<FtpFile> list = new ArrayList<FtpFile>();
        List<Folder> subfolders = repositoryFileSystemView.getFactory().getRepositoryService().getSubFolders(null, getAbsolutePath());
        if (subfolders != null) {
            for (Folder folder : subfolders) {
                if (isSupported(repositoryFileSystemView, folder)) {
                    FtpFile ftpFolder = new JSFtpFile(repositoryFileSystemView, folder);
                    list.add(ftpFolder);
                }
            }
        }

        FilterCriteria criteria = FilterCriteria.createFilter(FileResource.class);
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(getAbsolutePath()));
        ResourceLookup[] resources = repositoryFileSystemView.getFactory().getRepositoryService().findResource(null, criteria);
        if (resources != null) {
            for (int i = 0; i < resources.length; i++) {
                Resource res = resources[i];
                if (isSupported(repositoryFileSystemView, res)) {
                    byte[] data = null;
                    if (repositoryFileSystemView.getFactory().isLoadContent()) {
                        FileResourceData fileResourceData = repositoryFileSystemView.getFactory().getRepositoryService().getResourceData(null, res.getURI());
                        data = fileResourceData.getData();
                    }
                    FtpFile ftpFolder = new JSFtpFile(repositoryFileSystemView,res, data);
                    list.add(ftpFolder);
                }
            }
        }

        return list;
    }

    public OutputStream createOutputStream(long l) throws IOException {
        //throw new UnsupportedOperationException();
        if (l > 0 && (content == null || content.length < l)) {
            throw new IOException("Cannot set the file offset for writing");
        }
        OutputStream os = new ByteArrayOutputStream() {
            private boolean closed = false;
            @Override
            public void close() throws IOException {
                super.close();
                if (!this.closed) {
                    onOutputStreamClose(this.toByteArray());
                }
                this.closed = true;
            }
        };
        if (l > 0) {
            if (!repositoryFileSystemView.getFactory().isLoadContent() && content == null) {
                FileResourceData fileResourceData = repositoryFileSystemView.getFactory().getRepositoryService().getResourceData(null, getAbsolutePath());
                content = fileResourceData.getData();
            }
            os.write(content, 0, (int) l);
        }

        return os;
    }

    protected void onOutputStreamClose(byte[] content) {
        FileResource resource;
        if (!this.exists) {
            resource = (FileResource) repositoryFileSystemView.getFactory().getResourceFactory().newResource(null, FileResource.class);
            resource.setName(name);
            resource.setLabel(name);
            resource.setCreationDate(new Date());
            resource.setParentFolder(parentFolderURI);
        } else {
            resource = (FileResource) repositoryFileSystemView.getFactory().getRepositoryService().getResource(null, getAbsolutePath());
        }
        resource.setData(content);
        resource.setFileType(detectType(name));
        repositoryFileSystemView.getFactory().getRepositoryService().saveResource(null, resource);
        this.exists = true;
        this.isFolder = false;
        this.content = content;
    }

    protected String detectType(String name) {
        int k = name.lastIndexOf(".");
        if (k >= 0) {
            String ext = name.substring(k + 1).toUpperCase();
            String type = repositoryFileSystemView.getFactory().getTypeMapping().get(ext);
            if (type != null) {
                return type;
            }
        }
        return "unknown";
    }

    public InputStream createInputStream(long l) throws IOException {
        if (content  == null) {
            FileResourceData fileResourceData = repositoryFileSystemView.getFactory().getRepositoryService().getResourceData(null, getAbsolutePath());
            content = fileResourceData.getData();
        }
        return content != null && l < content.length ? new ByteArrayInputStream(content, (int) l, (int) (content.length - l)) : null;
    }

    protected static boolean isSupported(JSRepositoryFileSystemView repositoryFileSystemView, Resource res) {
        String uri = res.getURIString();
        return supportedPathPattern.matcher(uri).matches();
    }
}
