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
package com.jaspersoft.jasperserver.war.dto;

import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

public class ReportUnitWrapper extends BaseDTO {
	private List existingResources; // List of resource names already present in the chosen folder
	private ReportUnit reportUnit;
	private List suggestedResources;
	private List suggestedControls;
	private List reusableJrxmls;	
	private boolean anyJrxmlAvailable;
	private String source;
	private byte[] jrxmlData;
	private String jrxmlUri;
	private String oldJrxmlUri;//TODO remove?
	private String originalJrxmlUri;//TODO remove?
	private String validationMessage;
	private boolean jrxmlChanged;
	private boolean jrxmlLocated;
	private boolean result;
	private boolean named;
	private boolean datasourceIdentified;
	private List reports;
	private boolean hasNonSuggestedResources;
	private boolean hasSuggestedResources;
	private boolean hasNonSuggestedControls;
	private boolean hasSuggestedControls;

	private String inputControlSource;
	private String inputControlPath;
	private List inputControlList;
	
	public boolean isHasNonSuggestedResources() {
		return hasNonSuggestedResources;
	}
	public void setHasNonSuggestedResources(boolean hasNonSuggestedResources) {
		this.hasNonSuggestedResources = hasNonSuggestedResources;
	}
	public boolean isHasSuggestedResources() {
		return hasSuggestedResources;
	}
	public void setHasSuggestedResources(boolean hasSuggestedResources) {
		this.hasSuggestedResources = hasSuggestedResources;
	}
	public boolean isDatasourceIdentified() {
		return datasourceIdentified;
	}
	public void setDatasourceIdentified(boolean datasourceIdentified) {
		this.datasourceIdentified = datasourceIdentified;
	}
	public boolean isNamed() {
		return named;
	}
	public void setNamed(boolean named) {
		this.named = named;
	}
	public boolean isJrxmlChanged() {
		return jrxmlChanged;
	}
	public void setJrxmlChanged(boolean jrxmlChanged) {
		this.jrxmlChanged = jrxmlChanged;
		setJrxmlLocated(true);
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
    @Deprecated
	public List getReusableJrxmls() {
		return reusableJrxmls;
	}
    @Deprecated
    public void setReusableJrxmls(List reusableJrxmls) {
		this.reusableJrxmls = reusableJrxmls;
	}
	public ReportUnit getReportUnit() {
		return reportUnit;
	}
	public void setReportUnit(ReportUnit reportUnit) {
		this.reportUnit = reportUnit;
	}
	public String getOldJrxmlUri() {
		return oldJrxmlUri;
	}
	public void setOldJrxmlUri(String oldJrxmlUri) {
		this.oldJrxmlUri = oldJrxmlUri;
	}
	public String getOriginalJrxmlUri() {
		return originalJrxmlUri;
	}
	public void setOriginalJrxmlUri(String originalJrxmlUri) {
		this.originalJrxmlUri = originalJrxmlUri;
	}
	public boolean isJrxmlLocated() {
		return jrxmlLocated;
	}
	public void setJrxmlLocated(boolean jrxmlLocated) {
		this.jrxmlLocated = jrxmlLocated;
	}
	public byte[] getJrxmlData() {
		return jrxmlData;
	}
	public void setJrxmlData(byte[] jrxmlData) {
		this.jrxmlData = jrxmlData;
	}
	public String getJrxmlUri() {
		return jrxmlUri;
	}
	public void setJrxmlUri(String jrxmlUri) {
		this.jrxmlUri = jrxmlUri;
	}
	public List getSuggestedControls() {
		return suggestedControls;
	}
	public void setSuggestedControls(List controlWrappers) {
		this.suggestedControls = controlWrappers;
	}
	public List getSuggestedResources() {
		return suggestedResources;
	}
	public void setSuggestedResources(List resourceWrappers) {
		this.suggestedResources = resourceWrappers;
	}
	public boolean isHasNonSuggestedControls() {
		return hasNonSuggestedControls;
	}
	public void setHasNonSuggestedControls(boolean hasNonSuggestedControls) {
		this.hasNonSuggestedControls = hasNonSuggestedControls;
	}
	public boolean isHasSuggestedControls() {
		return hasSuggestedControls;
	}
	public void setHasSuggestedControls(boolean hasSuggestedControls) {
		this.hasSuggestedControls = hasSuggestedControls;
	}
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public List getReports() {
		return reports;
	}
	public void setReports(List reports) {
		this.reports = reports;
	}
	public String getValidationMessage() {
		return validationMessage;
	}
	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}
	@Deprecated
    public List getExistingResources() {
		return existingResources;
	}
    @Deprecated
	public void setExistingResources(List existingResources) {
		this.existingResources = existingResources;
	}

	public List getInputControlList()
	{
		return inputControlList;
	}

	public void setInputControlList(List inputControlList)
	{
		this.inputControlList = inputControlList;
	}

	public String getInputControlPath()
	{
		return inputControlPath;
	}

	public void setInputControlPath(String inputControlPath)
	{
		this.inputControlPath = inputControlPath;
	}

	public String getInputControlSource()
	{
		return inputControlSource;
	}

	public void setInputControlSource(String inputControlSource)
	{
		this.inputControlSource = inputControlSource;
	}

    public boolean isAnyJrxmlAvailable() {
        return anyJrxmlAvailable;
    }

    public void setAnyJrxmlAvailable(boolean anyJrxmlAvailable) {
        this.anyJrxmlAvailable = anyJrxmlAvailable;
    }
}
