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
package com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.domain.Request;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ReportExecuter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class ReportUnitRequestBase implements Request {

	public static final String REPORT_CONTEXT_PARAMETER_REPORT_UNIT = "_reportUnit";

	public static final String REPORT_CONTEXT_PARAMETER_REPORT_UNIT_DATA_SOURCE = "_reportUnitDataSource";
	
	private static final AtomicInteger COUNTER = new AtomicInteger();
	
	private String id;
	private Map reportParameters;
	private boolean asynchronous;
    private ReportContext reportContext;
    private boolean useDataSnapshot;
    private boolean recordDataSnapshot;
    
    private long startTime = System.currentTimeMillis();
    private boolean createAuditEvent;
    
    private JasperReportsContext jasperReportsContext;

	protected ReportUnitRequestBase(Map reportParameters) {
		this.id = System.identityHashCode(this) 
			+ "_" + System.currentTimeMillis() 
			+ "_" + COUNTER.incrementAndGet();

		this.reportParameters = reportParameters;
	}

	/**
	 *
	 */
	public Map getReportParameters()
	{
		return reportParameters;
	}

	/**
	 *
	 */
	public void setReportParameters(Map reportParameters)
	{
		this.reportParameters = reportParameters;
	}

	public String getId()
	{
		return id;
	}

    /**
     * Use this setter carefully. ReportUnitRequest ID should be unique across JRS.
     * For instance you can use UUID.randomUUID().toString() for the unique ID generation.
     *
     * @param id - the unique id to set.
     */
    public void setId(String id){
        this.id = id;
    }

	public boolean isAsynchronous() {
		return asynchronous;
	}

	public void setAsynchronous(boolean asynchronous) {
		this.asynchronous = asynchronous;
	}

	public boolean isUseDataSnapshot() {
		return useDataSnapshot;
	}

	public void setUseDataSnapshot(boolean useDataSnapshot) {
		this.useDataSnapshot = useDataSnapshot;
	}

	public boolean isRecordDataSnapshot() {
		return recordDataSnapshot;
	}

	public void setRecordDataSnapshot(boolean recordDataSnapshot) {
		this.recordDataSnapshot = recordDataSnapshot;
	}
	
	/**
	 * 
	 */
	public ReportContext getReportContext()
	{
		return reportContext;
	}

	/**
	 * 
	 */
	public void setReportContext(ReportContext reportContext)
	{
		this.reportContext = reportContext;//FIXME add this to the parameters automatically
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public boolean isCreateAuditEvent() {
		return createAuditEvent;
	}

	public void setCreateAuditEvent(boolean createAuditEvent) {
		this.createAuditEvent = createAuditEvent;
	}

	public JasperReportsContext getJasperReportsContext() {
		return jasperReportsContext;
	}

	public void setJasperReportsContext(JasperReportsContext jasperReportsContext) {
		this.jasperReportsContext = jasperReportsContext;
	}

	public abstract ReportUnitResult execute(ExecutionContext context, ReportExecuter executer);

}