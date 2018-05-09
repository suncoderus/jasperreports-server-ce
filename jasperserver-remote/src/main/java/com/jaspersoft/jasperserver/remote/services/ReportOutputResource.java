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
package com.jaspersoft.jasperserver.remote.services;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportOutputResource.java 26599 2012-12-10 13:04:23Z ykovalchyk $
 */
@XmlRootElement
public class ReportOutputResource {

    private String contentType;
    private byte[] data;
    private String fileName;
    private String pages;
    private Boolean outputFinal;

    public String getPages() {
        return pages;
    }

    public ReportOutputResource setPages(String pages) {
        this.pages = pages;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public ReportOutputResource setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public ReportOutputResource setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    @XmlTransient
    public byte[] getData() {
        return data;
    }

    public ReportOutputResource setData(byte[] data) {
        this.data = data;
        return this;
    }

    public Boolean getOutputFinal() {
        return outputFinal;
    }

    public ReportOutputResource setOutputFinal(Boolean outputFinal) {
        this.outputFinal = outputFinal;
        return this;
    }
}
