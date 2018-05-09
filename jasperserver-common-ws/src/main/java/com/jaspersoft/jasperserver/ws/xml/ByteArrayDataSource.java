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
package com.jaspersoft.jasperserver.ws.xml;


/**
 *
 * @author  Administrator
 */
public class ByteArrayDataSource implements javax.activation.DataSource {
    
    private byte[] buffer;
    private String contenType = "application/octet-stream";

    private String name = "";
    /** Creates a new instance of ByteArrayDataSource */
    public ByteArrayDataSource(String name, byte[] buffer) {
        this(name, buffer, "application/octet-stream");
    }
    
    /** Creates a new instance of ByteArrayDataSource */
    public ByteArrayDataSource(byte[] buffer) {
        this(null, buffer, "application/octet-stream");
    }
    
    /** Creates a new instance of ByteArrayDataSource */
    public ByteArrayDataSource(byte[] buffer, String contentType) {
        this(null, buffer, contentType);
    }
    
    /** Creates a new instance of ByteArrayDataSource */
    public ByteArrayDataSource(String name, byte[] buffer, String contentType) {
        if (name != null) this.setName(name);
        this.setBuffer(buffer);
        if (contentType != null) this.setContenType(contentType);
    }
       
    public String getContentType() {
        return getContenType();
    }

    
    public java.io.InputStream getInputStream() throws java.io.IOException {
        return new java.io.ByteArrayInputStream( getBuffer() );
    }
    
    public String getName() {
        return name;
    }
    
    public java.io.OutputStream getOutputStream() throws java.io.IOException {
        throw new java.io.IOException();
    }

    public String getContenType() {
        return contenType;
    }

    public void setContenType(String contenType) {
        this.contenType = contenType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }
}

