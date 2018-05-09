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

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.util.Callback;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSourceModel;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.core.util.PathUtils;
import com.jaspersoft.jasperserver.core.util.PathUtils.SplittedPath;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImporter;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.ReportJobBean;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.ReportUnitJobsIndexBean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportJobsImporter extends BaseImporterModule {

	private final static Log log = LogFactory.getLog(ResourceImporter.class);
	
	protected SchedulingModuleConfiguration configuration;
	private String prependPathArg;
	
	protected String prependPath;
	private Map userIndicator;
    private static String SYS_DUP_SUFFIX = "_SYSDUP_";

    private enum ImportStatus {  UPDATE, ADD, SKIP};

    protected void doInExceptionHandlingContext(String reportUri, Callback callback) {
        callback.execute();
    }

	public void init(ImporterModuleContext moduleContext) {
		super.init(moduleContext);
		prependPath = getPrependPath();
	}

	protected String getPrependPath() {
		String path = getParameterValue(getPrependPathArg());
		if (path != null) {
			path = PathUtils.normalizePath(path);
			if (path.length() == 0 || path.equals(Folder.SEPARATOR)) {
				path = null;
			} else if (!path.startsWith(Folder.SEPARATOR)) {
				path = Folder.SEPARATOR + path;
			}
		}
		return path;
	}

	public List<String> process() {
		initProcess();
		
		for (Iterator i = indexElement.elementIterator(configuration.getIndexReportUnitElement()); i.hasNext(); ) {
			Element ruElement = (Element) i.next();
			String uri = ruElement.getText();
			processReportUnit(uri);
		}
        return null;
	}

	protected void initProcess() {
		userIndicator = new HashMap();
	}

	protected void processReportUnit(String uri) {
		final String newUri = prependedPath(uri);
		if (checkReportUnit(newUri)) {
            final String ruPath = PathUtils.concatPaths(configuration.getReportJobsDir(), uri);
            ReportUnitJobsIndexBean indexBean = (ReportUnitJobsIndexBean) deserialize(ruPath, configuration.getReportUnitIndexFilename(), configuration.getSerializer());
            long[] jobIds = indexBean.getJobIds();
			if (isUpdateResource(newUri)) {
                final ArrayList<ReportJobBean> reportJobBeans = getReportJobBeans(ruPath, jobIds);
                final MutableInt imported = new MutableInt(0);
                final MutableInt updated = new MutableInt(0);
				for (final ReportJobBean reportJobBean : reportJobBeans) {
                    doInExceptionHandlingContext(newUri, new Callback() {
                        public void execute() {
                            ImportStatus status;
                            // look for duplicate from import file.  if duplicate found, add as new job instead
                            if (findDuplicate(reportJobBean, reportJobBeans)) {
                                status = updateJob(newUri, reportJobBean, ImportStatus.ADD);
                            } else {
                                status = updateJob(newUri, reportJobBean, ImportStatus.UPDATE);
                            }
                            if (status == ImportStatus.ADD) imported.increment();
                            else if (status == ImportStatus.UPDATE) updated.increment();
                        }
                    });

				}
				commandOut.info("Created " + imported + " job(s) for report " + newUri);
                commandOut.info("Updated " + updated + " job(s) for report " + newUri);
			} else {
                final Map<String, ReportJob> existingJobs = new HashMap<String, ReportJob>();
                List<ReportJobSummary> jobs
                        = configuration.getReportScheduler().getScheduledJobSummaries(executionContext, newUri);
                for (ReportJobSummary job : jobs) {
                    ReportJob reportJob = configuration.getReportScheduler().getScheduledJob(executionContext, job.getId());
                    existingJobs.put(reportJob.getLabel(), reportJob);
                }
                final MutableInt imported = new MutableInt(0);
				for (int i = 0; i < jobIds.length; i++) {
					final long jobId = jobIds[i];
                    doInExceptionHandlingContext(newUri, new Callback() {
                        public void execute() {
                            if (importReportJob(newUri, ruPath, jobId, existingJobs)) {
                                imported.increment();
                            }
                        }
                    });

				}
				
				commandOut.info("Created " + imported + " job(s) for report " + newUri);
			}
		} else {
			commandOut.warn("Report unit " + newUri + " not found in repository, skipping jobs");
		}
	}

    protected ArrayList<ReportJobBean> getReportJobBeans(String jobsPath, long[] jobIds) {
        ArrayList<ReportJobBean> reportJobBeans = new ArrayList<ReportJobBean>();
        for (int i = 0; i < jobIds.length; i++) {
            String jobFilename = getJobFilename(jobIds[i]);
		    reportJobBeans.add((ReportJobBean) deserialize(jobsPath, jobFilename, configuration.getSerializer()));
        }
        return reportJobBeans;
    }

    private boolean findDuplicate(ReportJobBean reportJob, ArrayList<ReportJobBean> reportJobBeans) {
        for (ReportJobBean reportJobBean : reportJobBeans)
            if ((reportJob != reportJobBean) && reportJob.getLabel().equals(reportJobBean.getLabel())) {
                if (reportJob.getCreationDate() == null) return true;
                else if (reportJob.getCreationDate().compareTo(reportJobBean.getCreationDate()) == 0) return true;
            }
        return false;
    }

	protected boolean checkReportUnit(String uri) {
		SplittedPath splittedPath = PathUtils.splitPathToFolderAndName(uri);

		FilterCriteria filter = FilterCriteria.createFilter(ReportUnit.class);
		filter.addFilterElement(FilterCriteria.createParentFolderFilter(splittedPath.parentPath));
		filter.addFilterElement(FilterCriteria.createPropertyEqualsFilter("name", splittedPath.name));
		ResourceLookup[] lookups = configuration.getRepository().findResource(executionContext, filter);
		return lookups != null && lookups.length > 0;
	}

	protected boolean isUpdateResource(String resourceUri) {
		Collection updateResources = (Collection) getContextAttributes().getAttribute(
				ResourceImporter.ATTRIBUTE_UPDATE_RESOURCES);
		return updateResources != null && updateResources.contains(resourceUri);
	}

	protected boolean importReportJob(String reportUri, String jobsPath, long jobId, Map<String, ReportJob> existingJobs) {
		boolean imported;
		String jobFilename = getJobFilename(jobId);
		ReportJobBean jobBean = (ReportJobBean) deserialize(jobsPath, jobFilename, configuration.getSerializer());
        ReportJob job = new ReportJob();
        jobBean.copyTo(job, reportUri, getConfiguration(), executionContext, importContext);
		if (userExists(job.getUsername())) {
			imported = importJob(reportUri, job, jobBean.isPaused(), existingJobs);
		} else {
			commandOut.warn("User " + job.getUsername() + " does not exist, skipping job " + job.getId() + " of report " + reportUri);
			imported = false;
		}
		return imported;
	}

	protected String getJobFilename(long jobId) {
		return jobId + ".xml";
	}

	protected boolean userExists(String username) {
		Boolean indicator = (Boolean) userIndicator.get(username);
		if (indicator == null) {
            try {
                indicator = Boolean.valueOf(configuration.getAuthorityService().getUser(executionContext, username) != null);
            } catch (AccessDeniedException e) {
                indicator = Boolean.FALSE;
                commandOut.warn("Access Denied to read user " + username);
            }

			userIndicator.put(username, indicator);
		}
		return indicator.booleanValue();
	}

    public boolean shouldReimport(ReportJob existingJob, ReportJob newJob) {
        if (existingJob == null) {
            return true;
        }
        if (newJob == null) {
            return false;
        }

        if (existingJob.getLabel() != null
                ? !existingJob.getLabel().equals(newJob.getLabel()) : newJob.getLabel() != null) return true;
        if (existingJob.getUsername() != null
                ? !existingJob.getUsername().equals(newJob.getUsername()) : newJob.getUsername() != null) return true;
        if (existingJob.getBaseOutputFilename() != null
                ? !existingJob.getBaseOutputFilename().equals(newJob.getBaseOutputFilename())
                : newJob.getBaseOutputFilename() != null) return true;
        if (existingJob.getTrigger() != null
                ? !existingJob.getTrigger().equals(newJob.getTrigger()) : newJob.getTrigger() != null) return true;

        return false;
    }

    protected boolean importJob(String newUri, ReportJob job, boolean paused, Map<String, ReportJob> existingJobs) {
        boolean result = false;
        if (!existingJobs.containsValue(job)
                || (existingJobs.containsValue(job) && shouldReimport(existingJobs.get(job.getLabel()), job))) {
            ReportJob savedJob;
            try {
                savedJob = configuration.getInternalReportScheduler().saveJob(executionContext, job);
                if (paused) {
                    configuration.getReportScheduler().pause(Arrays.asList(savedJob), false);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Created job " + savedJob.getId() + " for report " + newUri + " (old id " + job.getId() + ")");
                }
                existingJobs.put(savedJob.getLabel(), savedJob);
                result = true;
            } catch (JSValidationException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipped job for report " + newUri + " (old id " + job.getId() + "). " + e.getErrors().toString());
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Skipped existing job for report " + newUri + " (old id " + job.getId() + ")");
            }
        }
        return result;
    }

	protected ImportStatus updateJob(String newUri, ReportJobBean jobBean, ImportStatus status) {
        ReportJobModel reportJobModel = new ReportJobModel();
        ReportJobSourceModel sourceModel = new ReportJobSourceModel();
        sourceModel.setReportUnitURI(newUri);
        reportJobModel.setSourceModel(sourceModel);
        reportJobModel.setLabel(jobBean.getLabel());
        // creation date is only available for version 4.7 or above
        if (jobBean.getCreationDate() != null) reportJobModel.setCreationDate(jobBean.getCreationDate());
        boolean updateLabelName = true;
        if (status == ImportStatus.UPDATE) {
            List<ReportJobSummary> existingJobs = configuration.getReportScheduler().getScheduledJobSummaries(executionContext, reportJobModel, 0, -1, ReportJobModel.ReportJobSortType.NONE, true);
            if (existingJobs.size() == 0) {
                // if job doesn't exist, add as new job
                status = ImportStatus.ADD;
                updateLabelName = false;
            } else if (existingJobs.size() > 1) {
                // if more than one job found, add as new job with new label name
                status = ImportStatus.ADD;
                updateLabelName = true;
            } else {    // 1 to 1 UPDATE
                ReportJob reportJob = configuration.getReportScheduler().getScheduledJob(executionContext, existingJobs.get(0).getId());
                jobBean.copyTo(reportJob, newUri, getConfiguration(), executionContext, importContext);

                if (!userExists(reportJob.getUsername())) {
                    commandOut.warn("User " + reportJob.getUsername() + " does not exist, skipping job " + reportJob.getId() + " of report " + newUri);
                    return ImportStatus.SKIP;
                }

                try {
                    configuration.getReportScheduler().updateScheduledJob(executionContext, reportJob);
                    if (log.isDebugEnabled()) {
                        log.debug("Updated job " + reportJob.getId() + " for report " + newUri + " (old id " + reportJob.getId() + ")");
                    }

                    ReportJobRuntimeInformation currentState = configuration.getReportScheduler().getJobRuntimeInformation(executionContext, reportJob.getId());
                    if ((currentState.getStateCode().byteValue() == ReportJobRuntimeInformation.STATE_PAUSED) != jobBean.isPaused()){
                        if (jobBean.isPaused()){
                            configuration.getReportScheduler().pause(Arrays.asList(reportJob), false);
                            log.debug("Paused job " + reportJob.getId() + " for report " + newUri + " (old id " + reportJob.getId() + ")");
                        } else {
                            configuration.getReportScheduler().resume(Arrays.asList(reportJob), false);
                            log.debug("Resumed job " + reportJob.getId() + " for report " + newUri + " (old id " + reportJob.getId() + ")");
                        }
                    }

                    return status;
                } catch (JSValidationException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Skipped job for report " + newUri + " (old id " + reportJob.getId() + "). " + e.getErrors().toString());
                    }
                    return ImportStatus.SKIP;
                }
            }
        }
        if (status == ImportStatus.ADD) {
            ReportJob job = new ReportJob();
            jobBean.copyTo(job, newUri, getConfiguration(), executionContext, importContext);

            if (!userExists(job.getUsername())) {
                commandOut.warn("User " + job.getUsername() + " does not exist, skipping job " + job.getId() + " of report " + newUri);
                return ImportStatus.SKIP;
            }

            // generate new name for the job
            if (updateLabelName) {
                job.setLabel(createUniqueLabel(job.getLabel(), newUri));
            }
            try {
                job = configuration.getInternalReportScheduler().saveJob(executionContext, job);
                if (jobBean.isPaused()){
                    configuration.getReportScheduler().pause(Arrays.asList(job), false);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Created job " + job.getId() + " for report " + newUri + " (old id " + job.getId() + ")");
                }
                return status;
            } catch (JSValidationException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipped job for report " + newUri + " (old id " + job.getId() + "). " + e.getErrors().toString());
                }
                return ImportStatus.SKIP;
            }
       }
       return ImportStatus.SKIP;
	}

    // generate new name for the job
    private String createUniqueLabel(String label, String uri) {
        List<ReportJobSummary> existingJobs = configuration.getReportScheduler().getScheduledJobSummaries(executionContext, uri);
        if (!findLabel(label, existingJobs)) return label;
        int count = 1;
        String newLabel = new StringBuffer(label).append(SYS_DUP_SUFFIX).append(count).toString();
        while (findLabel(newLabel, existingJobs)) {
            count++;
            newLabel = new StringBuffer(label).append(SYS_DUP_SUFFIX).append(count).toString();
        }
        return newLabel;
    }

    private boolean findLabel(String label, List<ReportJobSummary> existingJobs) {
        for (ReportJobSummary reportJobSummary : existingJobs) {
            if (reportJobSummary.getLabel().equals(label)) return true;
        }
        return false;
    }


	protected String prependedPath(String uri) {
		return PathUtils.concatPaths(prependPath, uri);
	}

	public SchedulingModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SchedulingModuleConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getPrependPathArg() {
		return prependPathArg;
	}

	public void setPrependPathArg(String prependPathArg) {
		this.prependPathArg = prependPathArg;
	}

}
