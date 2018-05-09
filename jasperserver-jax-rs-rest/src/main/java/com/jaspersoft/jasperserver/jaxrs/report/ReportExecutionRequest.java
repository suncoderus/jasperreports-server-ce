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
package com.jaspersoft.jasperserver.jaxrs.report;

import com.jaspersoft.jasperserver.dto.reports.ReportParameters;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportExecutionRequest.java 26599 2012-12-10 13:04:23Z ykovalchyk $
 */
@XmlRootElement
public class ReportExecutionRequest {
    private String reportUnitUri;
    private Boolean freshData = false;
    private Boolean saveDataSnapshot = false;
    /* reports are interactive by default in v2 services*/
    private Boolean interactive = true;
    private Boolean ignorePagination;
    private Boolean async = false;
    private String transformerKey;
    private String outputFormat;
    private String attachmentsPrefix;
    private Boolean allowInlineScripts = true;
    private String pages;
    private String baseUrl;
    private String markupType;
    private String anchor;
    private ReportParameters parameters;

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getMarkupType() {
        return markupType;
    }

    public void setMarkupType(String markupType) {
        this.markupType = markupType;
    }

    public Boolean isAllowInlineScripts() {
        return allowInlineScripts;
    }

    public void setAllowInlineScripts(Boolean allowInlineScripts) {
        this.allowInlineScripts = allowInlineScripts;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getReportUnitUri() {
        return reportUnitUri;
    }

    public void setReportUnitUri(String reportUnitUri) {
        this.reportUnitUri = reportUnitUri;
    }

    public Boolean getFreshData() {
        return freshData;
    }

    public void setFreshData(Boolean freshData) {
        this.freshData = freshData;
    }

    public Boolean getSaveDataSnapshot() {
        return saveDataSnapshot;
    }

    public void setSaveDataSnapshot(Boolean saveDataSnapshot) {
        this.saveDataSnapshot = saveDataSnapshot;
    }

    public Boolean getInteractive() {
        return interactive;
    }

    public void setInteractive(Boolean interactive) {
        this.interactive = interactive;
    }

    public Boolean getIgnorePagination() {
        return ignorePagination;
    }

    public void setIgnorePagination(Boolean ignorePagination) {
        this.ignorePagination = ignorePagination;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public String getTransformerKey() {
        return transformerKey;
    }

    public void setTransformerKey(String transformerKey) {
        this.transformerKey = transformerKey;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getAttachmentsPrefix() {
        return attachmentsPrefix;
    }

    public void setAttachmentsPrefix(String attachmentsPrefix) {
        this.attachmentsPrefix = attachmentsPrefix;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public ReportParameters getParameters() {
        return parameters;
    }

    public void setParameters(ReportParameters parameters) {
        this.parameters = parameters;
    }
}
