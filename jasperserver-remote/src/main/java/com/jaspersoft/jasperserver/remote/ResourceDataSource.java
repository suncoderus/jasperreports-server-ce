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
package com.jaspersoft.jasperserver.remote;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import java.io.IOException;
import javax.activation.DataSource;

/**
 * This class is used to transfer file usimg MTOM and SOAP with Attachments
 * @author gtoffoli
 */
public class ResourceDataSource implements DataSource{
    
    private FileResourceData fileResourceData = null;
    private String name = "";
    private String contentType = "application/octet-stream";
    
    /** Creates a new instance of ResourceDataSource */
    public ResourceDataSource(String name, FileResourceData fileResourceData) {
        this.name = name;
        this.fileResourceData = fileResourceData;
    }

    public java.io.InputStream getInputStream() throws IOException {
        return fileResourceData.getDataStream();
    }

    public java.io.OutputStream getOutputStream() throws IOException {
        throw new java.io.IOException();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
         
    }

    public FileResourceData getFileResourceData() {
        return fileResourceData;
    }

    public void setFileResourceData(FileResourceData fileResourceData) {
        this.fileResourceData = fileResourceData;
    }
    
}
