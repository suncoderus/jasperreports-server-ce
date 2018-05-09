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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoInputControl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoQuery;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoReportDataSource;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 *
 * @hibernate.joined-subclass table="ReportUnit"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoReportUnit extends RepoResource
{

	private RepoResource dataSource = null;
	private RepoQuery query = null;
	private List resources;
	private List inputControls;
	private RepoFileResource mainReport = null;
	private String inputControlRenderingView;
	private String reportRenderingView;
    private boolean alwaysPromptControls;
    private byte controlsLayout;
    private Long dataSnapshotId;

	public RepoReportUnit() {
	}

	/**
	 * @hibernate.many-to-one
	 * 		column="reportDataSource"
	 */
	public RepoResource getDataSource()
	{
		return dataSource;
	}

	/**
	 *
	 */
	public void setDataSource(RepoResource dataSource)
	{
		this.dataSource = dataSource;
	}

	/**
	 * @hibernate.many-to-one
	 * 		column="query"
	 */
	public RepoQuery getQuery()
	{
		return query;
	}

	public void setQuery(RepoQuery query)
	{
		this.query = query;
	}

	/**
	 * @hibernate.list table="ReportUnitInputControl"
	 * @hibernate.key column="report_unit_id"
	 * @hibernate.many-to-many column="input_control_id" class="com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoInputControl"
	 * @hibernate.list-index column="control_index"
	 */
	public List getInputControls()
	{
		return inputControls;
	}

	public void setInputControls(List inputControls)
	{
		this.inputControls = inputControls;
	}

	/**
	 * @hibernate.list table="Report_Unit_Resource"
	 * @hibernate.key column="report_unit_id"
	 * @hibernate.many-to-many column="resource_id" class="com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFileResource"
	 * @hibernate.list-index column="resource_index"
	 */
	public List getResources()
	{
		return resources;
	}

	public void setResources(List resources)
	{
		this.resources = resources;
	}


	/**
	 * @hibernate.many-to-one
	 * 		column="mainReport"
	 */
	public RepoFileResource getMainReport()
	{
		return mainReport;
	}

	/**
	 *
	 */
	public void setMainReport(RepoFileResource mainReport)
	{
		this.mainReport = mainReport;
	}

	/**
	 * @hibernate.property
	 * 		column="controlrenderer" type="string" length="100"
	 *
	 * @return Returns the inputControlRenderingView.
	 */
	public String getInputControlRenderingView() {
		return inputControlRenderingView;
	}

	/**
	 * @param inputControlRenderingView The inputControlRenderingView to set.
	 */
	public void setInputControlRenderingView(String inputControlRenderingView) {
		this.inputControlRenderingView = inputControlRenderingView;
	}

	/**
	 * @hibernate.property
	 * 		column="reportrenderer" type="string" length="100"
	 *
	 * @return Returns the reportRenderingView.
	 */
	public String getReportRenderingView() {
		return reportRenderingView;
	}

	/**
	 * @param reportRenderingView The reportRenderingView to set.
	 */
	public void setReportRenderingView(String reportRenderingView) {
		this.reportRenderingView = reportRenderingView;
	}


    public boolean isAlwaysPromptControls() {
        return alwaysPromptControls;
    }

    public void setAlwaysPromptControls(boolean alwaysPromptControls) {
        this.alwaysPromptControls = alwaysPromptControls;
    }


    public byte getControlsLayout() {
        return controlsLayout;
    }

    public void setControlsLayout(byte controlsLayout) {
        this.controlsLayout = controlsLayout;
    }

	public ValidationResult validate() {
		return null;
	}


	protected Class getClientItf() {
		return ReportUnit.class;
	}


	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);

		ReportUnit unit = (ReportUnit) clientRes;
		unit.setDataSource(getClientReference(getDataSource(), resourceFactory));
		unit.setMainReport(getClientReference(getMainReport(), resourceFactory));
		unit.setQuery(getClientReference(getQuery(), resourceFactory));

		List resList = getResources();
		for (Iterator it = resList.iterator(); it.hasNext();) {
			RepoFileResource fileRes = (RepoFileResource) it.next();
			unit.addResource(getClientReference(fileRes, resourceFactory));
		}

		List inputCtrls = getInputControls();
		for (Iterator it = inputCtrls.iterator(); it.hasNext();) {
			RepoInputControl inputControl = (RepoInputControl) it.next();
			unit.addInputControl(getClientReference(inputControl, resourceFactory));
		}

		unit.setInputControlRenderingView(getInputControlRenderingView());
		unit.setReportRenderingView(getReportRenderingView());
		unit.setAlwaysPromptControls(isAlwaysPromptControls());
        unit.setControlsLayout(getControlsLayout());
        unit.setDataSnapshotId(getDataSnapshotId());
	}

	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);

		ReportUnit unit = (ReportUnit) clientRes;

		copyResources(referenceResolver, unit);
		copyDataSource(referenceResolver, unit);
		copyMainReport(referenceResolver, unit);

		ResourceReference unitQuery = unit.getQuery();
		setQuery((RepoQuery) getReference(unitQuery, RepoQuery.class, referenceResolver));

		List clientInputCtrls = unit.getInputControls();
		List inputCtrls = new ArrayList();
		if (clientInputCtrls != null && !clientInputCtrls.isEmpty()) {
			for (Iterator it = clientInputCtrls.iterator(); it.hasNext();) {
				ResourceReference inputCtrlRef = (ResourceReference) it.next();
				inputCtrls.add(getReference(inputCtrlRef, RepoInputControl.class, referenceResolver));
			}
		}
		setInputControls(inputCtrls);

		setInputControlRenderingView(unit.getInputControlRenderingView());
		setReportRenderingView(unit.getReportRenderingView());
        setAlwaysPromptControls(unit.isAlwaysPromptControls());
        setControlsLayout(unit.getControlsLayout());
        setDataSnapshotId(unit.getDataSnapshotId());
	}

	private void copyMainReport(ReferenceResolver referenceResolver, ReportUnit unit) {
		ResourceReference report = unit.getMainReport();
		RepoFileResource repoReport = (RepoFileResource) getReference(report, RepoFileResource.class, referenceResolver);
		setMainReport(repoReport);
	}

	private void copyDataSource(ReferenceResolver referenceResolver, ReportUnit unit) {
		ResourceReference ds = unit.getDataSource();
		RepoResource repoDS = getReference(ds, RepoReportDataSource.class, referenceResolver);
		if (repoDS != null && !(repoDS instanceof RepoReportDataSource)) {
			throw new JSException("jsexception.report.unit.datasource.has.an.invalid.type", new Object[] {repoDS.getClass().getName()});
		}
		setDataSource(repoDS);
	}

	private void copyResources(ReferenceResolver referenceResolver, ResourceContainer unit) {
		List clientResources = unit.getResources();
		List resList = new ArrayList();
		if (clientResources != null && !clientResources.isEmpty()) {
			for (Iterator it = clientResources.iterator(); it.hasNext();) {
				ResourceReference resourceRef = (ResourceReference) it.next();
				RepoFileResource repoRes = (RepoFileResource) getReference(resourceRef, RepoFileResource.class, referenceResolver);
				resList.add(repoRes);
			}
		}
		setResources(resList);
	}

	public Long getDataSnapshotId() {
		return dataSnapshotId;
	}

	public void setDataSnapshotId(Long dataSnapshotId) {
		this.dataSnapshotId = dataSnapshotId;
	}

}
