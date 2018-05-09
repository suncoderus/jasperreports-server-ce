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

package com.jaspersoft.jasperserver.export.modules.scheduling;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExporter;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.ReportJobBean;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.ReportUnitJobsIndexBean;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportJobsExporter extends BaseExporterModule {

	protected class OutputFolderCreator {
		private String uri;
		private String folderPath;

		OutputFolderCreator(String uri) {
			this.uri = uri;
		}

		public String getFolderPath() {
			if (folderPath == null) {
				folderPath = mkdir(configuration.getReportJobsDir(), uri);
			}
			return folderPath;
		}
	}

	protected interface ExceptionHandlingCallback {
		void execute();
	}

	protected SchedulingModuleConfiguration configuration;

	protected String reportJobsArg;

    protected String urisArg;

	protected Set exportedURIs;

	private String resourceExporterId;

	/**
	 * This method can be overridden in subclasses
	 * @return <code>true</code> in successful case, or <code>false</code> in case when exception was handled
	 */
	protected boolean doInExceptionHandlingContext(ExceptionHandlingCallback callback) {
		callback.execute();
		return true;
	}

	protected boolean isToProcess() {
		return hasParameter(reportJobsArg);
	}
	
	public void process() {
		mkdir(configuration.getReportJobsDir());
                                            
		exportedURIs = new HashSet();

		String[] reportURIs = exportEverything ? new String[]{"/"} : getParameterValues(reportJobsArg);
		if (reportURIs == null) {
            reportURIs = getParameterValues(urisArg);
			if (reportURIs == null) reportURIs = new String[]{"/"};
		}
        for (int i = 0; i < reportURIs.length; i++) {
			String uri = reportURIs[i];
			if (!exportFilter.excludeFolder(uri, exportParams)) {
				processUri(uri);
			}
		}
	}

	private void processUri(final String uri) {
		Folder folder = configuration.getRepository().getFolder(executionContext, uri);
		if (folder == null) {
			doInExceptionHandlingContext(new ExceptionHandlingCallback() {
				@Override
				public void execute() {
					Resource resource = configuration.getRepository().getResource(executionContext, uri);
					if (resource == null) {
						throw new JSException("jsexception.repository.uri.neither.report.nor.folder", new Object[] {uri});
					}

					processResource(resource);
				}
			});

		} else {
			processFolder(uri);
		}
	}
	
	protected void processResource(Resource resource) {
		if (resource instanceof ReportUnit) {
			processReportUnit(resource.getURIString());
		} else {
            if (configuration.isAllowReportsUnitsUrisOnly()){
                throw new JSException("jsexception.resource.not.report.unit", new Object[] {resource.getURIString()});
            }
		}
	}

	protected void processFolder(String uri) {
		if (exportFilter.excludeFolder(uri, exportParams)) return;

		processFolderResources(uri);
		
		List subFolders = configuration.getRepository().getSubFolders(executionContext, uri);
		if (subFolders != null && !subFolders.isEmpty()) {
			for (Iterator it = subFolders.iterator(); it.hasNext();) {
				Folder subFolder = (Folder) it.next();
				processFolder(subFolder.getURIString());
			}
		}
	}

	protected void processFolderResources(String folderURI) {
		FilterCriteria filter = FilterCriteria.createFilter(ReportUnit.class);
		filter.addFilterElement(FilterCriteria.createParentFolderFilter(folderURI));
		ResourceLookup[] reportUnits = configuration.getRepository().findResource(executionContext, filter);
		if (reportUnits != null && reportUnits.length > 0) {
			for (int i = 0; i < reportUnits.length; i++) {
				ResourceLookup reportUnit = reportUnits[i];
				processReportUnit(reportUnit.getURIString());
			}
		}
	}

	protected void processReportUnit(String uri) {
		if (exportedURIs.contains(uri)) {
			return;
		}

		List jobs = configuration.getReportScheduler().getScheduledJobSummaries(executionContext, uri);
		if (jobs != null && !jobs.isEmpty()) {
			if (exportJobs(uri, jobs)) {
				writeIndexReportUnitEntry(uri);
			}
			
			exportedURIs.add(uri);

			commandOut.info("Exported " + jobs.size() + " job(s) for the " + uri + " report unit");
		} else {
			commandOut.debug("Report " + uri + " does not have any scheduled jobs");
		}
	}

	/**
	 * @return <b>true</b> - at least one job was exported, <b>false</b> - no one report job was exported
	 */
	protected boolean exportJobs(String uri, List jobs) {
		final OutputFolderCreator folderCreator = new OutputFolderCreator(uri);
		
		final Set<String> sshKeysQueue = new HashSet<String>();
		List<Long> processedIds = new ArrayList<Long>();

		for (Object job : jobs) {
			final ReportJobSummary jobSummary = (ReportJobSummary) job;
			final long jobId = jobSummary.getId();

			boolean ok = doInExceptionHandlingContext(new ExceptionHandlingCallback() {
				@Override
				public void execute() {
					ReportJob job = configuration.getReportScheduler().getScheduledJob(executionContext, jobId);
					exportJob(folderCreator.getFolderPath(), job, jobSummary.getRuntimeInformation());

					//  Add SSH Key file resource to the export queue
					if (job.getContentRepositoryDestination() != null
							&& job.getContentRepositoryDestination().getOutputFTPInfo() != null
							&& job.getContentRepositoryDestination().getOutputFTPInfo().getSshKey() != null) {
						sshKeysQueue.add(job.getContentRepositoryDestination().getOutputFTPInfo().getSshKey());
					}
				}
			});

			if (ok) {
				processedIds.add(jobId);
			}
		}

		// Export dependent resources
		if (!exportParams.hasParameter(ImportExportServiceImpl.SKIP_DEPENDENT_RESOURCES)) {
			exportResources(sshKeysQueue);
		}

		if (processedIds.isEmpty()) {
			return false;
		} else {
			ReportUnitJobsIndexBean indexBean = new ReportUnitJobsIndexBean();
			long[] jobIds = ArrayUtils.toPrimitive(processedIds.toArray(new Long[processedIds.size()]));
			indexBean.setJobIds(jobIds);
			serialize(indexBean, folderCreator.getFolderPath(), configuration.getReportUnitIndexFilename(), configuration.getSerializer());
			return true;
		}
	}

	protected void exportJob(String folderPath, ReportJob job, ReportJobRuntimeInformation runtimeInformation) {
		ReportJobBean jobBean = new ReportJobBean();
		jobBean.copyFrom(job, getConfiguration());
        jobBean.setPaused(runtimeInformation.getStateCode().byteValue() == ReportJobRuntimeInformation.STATE_PAUSED);
		serialize(jobBean, folderPath, getJobFilename(job), configuration.getSerializer());
	}

	protected void exportResources(Set<String> resources) {

		// Export SSH key file resources
		ResourceExporter resourceExporter = (ResourceExporter) exportContext.getModuleRegister().getExporterModule(resourceExporterId);
		if (!resources.isEmpty() && resourceExporter != null) {

			// Create the Resource Module index element when exporting only jobs
			if (resourceExporter.getIndexElement() == null) {
				Element reportJobsModuleIndexElement = getIndexElement();
				Element rootIndexElement = reportJobsModuleIndexElement.getParent();
				String indexModuleElementName = "module"; // TODO: resolve this value from baseExporterImporter bean
				String indexModuleIdAttributeName = "id"; // TODO: resolve this value from baseExporterImporter bean
				// The Resource module element should be placed before the ReportJobs element. Thus we remove the
				// reportJobsModuleIndexElement from root before adding resourceModuleIndexElement and then put it back:
				rootIndexElement.remove(reportJobsModuleIndexElement);
				Element resourceModuleIndexElement = rootIndexElement.addElement(indexModuleElementName);
				resourceModuleIndexElement.addAttribute(indexModuleIdAttributeName, resourceExporterId);
				rootIndexElement.add(reportJobsModuleIndexElement);
			}

			// Perform export
			for (String resourceURI : resources) {
				if (!resourceExporter.alreadyExported(resourceURI)) {
					resourceExporter.processUri(resourceURI, true, false);
				}
			}
		}
	}

	protected String getJobFilename(ReportJob job) {
		return job.getId() + ".xml";
	}

	protected void writeIndexReportUnitEntry(String uri) {
		Element ruElement = getIndexElement().addElement(configuration.getIndexReportUnitElement());
		ruElement.setText(uri);
	}

	public SchedulingModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SchedulingModuleConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getReportJobsArg() {
		return reportJobsArg;
	}

	public void setReportJobsArg(String reportJobsArg) {
		this.reportJobsArg = reportJobsArg;
	}

    public String getUrisArg() {
        return urisArg;
    }

    public void setUrisArg(String urisArg) {
        this.urisArg = urisArg;
    }

	public void setResourceExporterId(String resourceExporterId) {
		this.resourceExporterId = resourceExporterId;
	}
}
