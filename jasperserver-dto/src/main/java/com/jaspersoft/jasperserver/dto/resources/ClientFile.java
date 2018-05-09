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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = ResourceMediaType.FILE_CLIENT_TYPE)
public class ClientFile extends ClientResource<ClientFile> implements ClientReferenceableFile {
    private FileType type;
    private String content;

    public ClientFile(ClientFile other) {
        super(other);
        this.type = other.getType();
        this.content = other.getContent();
    }

    public ClientFile() {
    }

    public FileType getType() {
        return type;
    }

    public ClientFile setType(FileType type) {
        this.type = type;

        return this;
    }

    public String getContent() {
        return content;
    }

    public ClientFile setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientFile that = (ClientFile) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientFile{" +
                "type=" + type +
                ", version=" + getVersion() +
                ", permissionMask=" + getPermissionMask() +
                ", uri='" + getUri() + '\'' +
                ", label='" + getLabel() + '\'' +
                '}';
    }

    public enum FileType{
        pdf("application/pdf"),
        html("text/html"),
        xls("application/xls"),
        rtf("application/rtf"),
        csv("text/csv"),
        odt("application/vnd.oasis.opendocument.text"),
        txt("text/plain"),
        docx("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        ods("application/vnd.oasis.opendocument.spreadsheet"),
        xlsx("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        pptx("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        img("image/*"),
        font("font/*"),
        jrxml("application/jrxml"),
        jar("application/jar"),
        prop("application/properties"),
        jrtx("application/jrtx"),
        xml("application/xml"),
        json("application/json"),
        css("text/css"),
        accessGrantSchema("application/accessGrantSchema"),
        olapMondrianSchema("application/olapMondrianSchema"),
        dashboardComponent("application/dashboardComponentsSchema+json"),
        unspecified("application/octet-stream");

        FileType(String mime){
            this.mime = mime;
        }
        private String mime;

        public String getMimeType() {
            return mime;
        }
    }
}
