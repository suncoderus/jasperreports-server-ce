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

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author asokolnikov
 */
public class JSRepositoryFileSystemView implements FileSystemView {

    private User user;
    private JSRepositoryFileSystemFactory factory;
    private FtpFile workingDir;

    public JSRepositoryFileSystemView(User user, JSRepositoryFileSystemFactory factory) {
        this.user = user;
        this.factory = factory;

        FtpFile file = JSFtpFile.getFtpFile(this, user.getHomeDirectory());
        if (file != null && file.isDirectory()) {
            this.workingDir = file;
        }
    }

    public FtpFile getHomeDirectory() throws FtpException {
        FtpFile file = JSFtpFile.getFtpFile(this, user.getHomeDirectory());
        if (file != null && file.isDirectory()) {
            return file;
        }
        throw new FtpException("Cannot get home directory");
    }

    public FtpFile getWorkingDirectory() throws FtpException {
        if (workingDir == null) {
            workingDir = JSFtpFile.getFtpFile(this, "/");
        }
        return workingDir;
    }

    protected String resolvePath(String path) throws FtpException {
        // convert relative path to absolute
        if (!path.startsWith("/")) {
            if (path.startsWith(".") && !path.startsWith("..")) {
                path = path.substring(1);
            }
            path = (getWorkingDirectory().getAbsolutePath() + "/" + path).replace("//", "/");
        }
        // resolve back references like ".."
        if (path.contains("..")) {
            List<String> parts = new ArrayList(Arrays.asList(path.split("/")));
            int k;
            while ( (k = parts.indexOf("..")) > 0 ) {
                parts.remove(k);
                parts.remove(k - 1);
            }
            StringBuffer sb = new StringBuffer();
            for (String s : parts) {
                if (s.length() > 0) {
                    sb.append("/");
                    sb.append(s);
                }
            }
            if (sb.length() == 0) {
                sb.append("/");
            }
            path = sb.toString();
        }

        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

    public boolean changeWorkingDirectory(String folderURI) throws FtpException {
        try {
            folderURI = resolvePath(folderURI);
            FtpFile file = JSFtpFile.getFtpFile(this, folderURI);
            if (file != null && file.isDirectory()) {
                this.workingDir = file;
                return true;
            }
            return false;
        } catch (FtpException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public FtpFile getFile(String file) throws FtpException {
        file = resolvePath(file);
        return JSFtpFile.getFtpFile(this, file);
    }

    public boolean isRandomAccessible() throws FtpException {
        return false;
    }

    public void dispose() {
        //
    }

    public JSRepositoryFileSystemFactory getFactory() {
        return factory;
    }
}
