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

package com.jaspersoft.jasperserver.dto.dashboard;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>DTO for dashboard executions.</p>
 *
 * @author Zakhar Tomchenko
 * @version $Id: $
 */
@XmlRootElement(name = "dashboardExportExecution")
public class DashboardExportExecution {
    private int width;
    private int height;
    private int referenceWidth;
    private int referenceHeight;
    private ExportFormat format;
    private String uri;
    private String id;
    private DashboardParameters parameters;
    private String markup;
    private List<String> jrStyle;
    private String outputTimeZone;
    private String outputLocale;

    public int getWidth() {
        return width;
    }

    public DashboardExportExecution setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public DashboardExportExecution setHeight(int heigt) {
        this.height = heigt;
        return this;
    }

    public ExportFormat getFormat() {
        return format;
    }

    public DashboardExportExecution setFormat(ExportFormat format) {
        this.format = format;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public DashboardExportExecution setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public DashboardParameters getParameters() {
        return parameters;
    }

    public DashboardExportExecution setParameters(DashboardParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    public String getId() {
        return id;
    }

    public DashboardExportExecution setId(String id) {
        this.id = id;
        return this;
    }

    public String getMarkup() {
        return markup;
    }

    public DashboardExportExecution setMarkup(String markup) {
        this.markup = markup;
        return this;
    }

    public List<String> getJrStyle() {
        return jrStyle;
    }

    public DashboardExportExecution setJrStyle(List<String> jrStyle) {
        this.jrStyle = jrStyle;
        return this;
    }

    public int getReferenceWidth() {
        return referenceWidth;
    }

    public DashboardExportExecution setReferenceWidth(int referenceWidth) {
        this.referenceWidth = referenceWidth;
        return this;
    }

    public int getReferenceHeight() {
        return referenceHeight;
    }

    public DashboardExportExecution setReferenceHeight(int referenceHeight) {
        this.referenceHeight = referenceHeight;
        return this;
    }

    public String getOutputTimeZone() {
        return outputTimeZone;
    }

    public DashboardExportExecution setOutputTimeZone(String outputTimeZone) {
        this.outputTimeZone = outputTimeZone;
        return this;
    }

    public String getOutputLocale() {
        return outputLocale;
    }

    public DashboardExportExecution setOutputLocale(String outputLocale) {
        this.outputLocale = outputLocale;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashboardExportExecution that = (DashboardExportExecution) o;

        if (width != that.width) return false;
        if (height != that.height) return false;
        if (referenceWidth != that.referenceWidth) return false;
        if (referenceHeight != that.referenceHeight) return false;
        if (format != that.format) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        if (markup != null ? !markup.equals(that.markup) : that.markup != null) return false;
        if (jrStyle != null ? !jrStyle.equals(that.jrStyle) : that.jrStyle != null) return false;
        if (outputTimeZone != null ? !outputTimeZone.equals(that.outputTimeZone) : that.outputTimeZone != null)
            return false;
        return outputLocale != null ? outputLocale.equals(that.outputLocale) : that.outputLocale == null;

    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + referenceWidth;
        result = 31 * result + referenceHeight;
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (markup != null ? markup.hashCode() : 0);
        result = 31 * result + (jrStyle != null ? jrStyle.hashCode() : 0);
        result = 31 * result + (outputTimeZone != null ? outputTimeZone.hashCode() : 0);
        result = 31 * result + (outputLocale != null ? outputLocale.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DashboardExportExecution{" +
                "width=" + width +
                ", height=" + height +
                ", referenceWidth=" + referenceWidth +
                ", referenceHeight=" + referenceHeight +
                ", format=" + format +
                ", uri='" + uri + '\'' +
                ", id='" + id + '\'' +
                ", parameters=" + parameters +
                ", markup='" + markup + '\'' +
                ", jrStyle=" + jrStyle +
                ", outputTimeZone='" + outputTimeZone + '\'' +
                ", outputLocale='" + outputLocale + '\'' +
                '}';
    }

    public enum ExportFormat {
        png("image/png"),
        pptx("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
        docx("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
        odt("application/vnd.oasis.opendocument.text"),
        pdf("application/pdf");

        private String mime;
        ExportFormat(String mime){
            this.mime = mime;
        }

        public String getMimeFormat(){
            return mime;
        }
    }
}
