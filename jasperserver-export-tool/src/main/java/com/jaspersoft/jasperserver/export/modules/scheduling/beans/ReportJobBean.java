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
package com.jaspersoft.jasperserver.export.modules.scheduling.beans;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobAlert;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;
import com.jaspersoft.jasperserver.export.modules.scheduling.SchedulingModuleConfiguration;

import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @author tkavanagh
 * @version $Id$
 */
public class ReportJobBean {

	private long id;
	private int version;
	private String username;
	private String label;
	private String description;
	private ReportJobTriggerBean trigger;
	private ReportJobSourceBean source;
	private String baseOutputFilename;
	private byte[] outputFormats;
	private String outputLocale;
	private ReportJobRepositoryDestinationBean contentRepositoryDestination;
	private ReportJobMailNotificationBean mailNotification;
    private Timestamp creationDate;
    private ReportJobAlertBean alert;
    private boolean paused;

	public void copyFrom(ReportJob job, SchedulingModuleConfiguration configuration) {
		setId(job.getId());
		setVersion(job.getVersion());
		setUsername(job.getUsername());
		setLabel(job.getLabel());
		setDescription(job.getDescription());
		setTrigger(copyTriggerFrom(job));
		setSource(copySourceFrom(job, configuration));
		setBaseOutputFilename(job.getBaseOutputFilename());
		setOutputFormats(copyOutputFormatsFrom(job));
		setOutputLocale(job.getOutputLocale());
		setContentRepositoryDestination(copyRepositoryDestinationFrom(job));
		setMailNotification(copyMailNotificationFrom(job));
        setAlert(copyAlertFrom(job));
        setCreationDate(job.getCreationDate());

	}

	protected byte[] copyOutputFormatsFrom(ReportJob job) {
		Set jobFormats = job.getOutputFormats();
		byte[] formats;
		if (jobFormats == null || jobFormats.isEmpty()) {
			formats = null;
		} else {
			formats = new byte[jobFormats.size()];
			int c = 0;
			for (Iterator it = jobFormats.iterator(); it.hasNext(); ++c) {
				Byte format = (Byte) it.next();
				formats[c] = format.byteValue();
			}
		}
		return formats;
	}

	protected ReportJobTriggerBean copyTriggerFrom(ReportJob job) {
		ReportJobTriggerBean triggerBean;
		ReportJobTrigger jobTrigger = job.getTrigger();
		if (jobTrigger == null) {
			triggerBean = null;
		} else {
			triggerBean = createTriggerBean(jobTrigger);
			triggerBean.copyFrom(jobTrigger);
		}
		return triggerBean;
	}

	protected ReportJobTriggerBean createTriggerBean(ReportJobTrigger jobTrigger) {
		ReportJobTriggerBean triggerBean;
		if (jobTrigger instanceof ReportJobSimpleTrigger) {
			triggerBean = new ReportJobSimpleTriggerBean();
		} else if (jobTrigger instanceof ReportJobCalendarTrigger) {
			triggerBean = new ReportJobCalendarTriggerBean();
		} else {
			throw new JSException("jsexception.job.unknown.trigger.type", new Object[] {jobTrigger.getClass().getName()});
		}
		return triggerBean;
	}

	protected ReportJobSourceBean copySourceFrom(ReportJob job, SchedulingModuleConfiguration configuration) {
		ReportJobSourceBean srcBean;
		ReportJobSource src = job.getSource();
		if (src == null) {
			srcBean = null;
		} else {
			srcBean = new ReportJobSourceBean();
			srcBean.copyFrom(src, configuration);
		}
		return srcBean;
	}

	protected ReportJobRepositoryDestinationBean copyRepositoryDestinationFrom(ReportJob job) {
		ReportJobRepositoryDestinationBean destBean;
		ReportJobRepositoryDestination dest = job.getContentRepositoryDestination();
		if (dest == null) {
			destBean = null;
		} else {
			destBean = new ReportJobRepositoryDestinationBean();
			destBean.copyFrom(dest);
		}
		return destBean;
	}

	protected ReportJobMailNotificationBean copyMailNotificationFrom(ReportJob job) {
		ReportJobMailNotificationBean mailNotificationBean;
		ReportJobMailNotification jobMail = job.getMailNotification();
		if (jobMail == null) {
			mailNotificationBean = null;
		} else {
			mailNotificationBean = new ReportJobMailNotificationBean();
			mailNotificationBean.copyFrom(jobMail);
		}
		return mailNotificationBean;
	}

    protected ReportJobAlertBean copyAlertFrom(ReportJob job) {
		ReportJobAlertBean alertBean;
		ReportJobAlert jobMail = job.getAlert();
		if (jobMail == null) {
			alertBean = null;
		} else {
			alertBean = new ReportJobAlertBean();
			alertBean.copyFrom(jobMail);
		}
		return alertBean;
	}

	public void copyTo(ReportJob job, String newReportUri, 
			SchedulingModuleConfiguration configuration, ExecutionContext context, ImporterModuleContext importContext) {
		job.setUsername(getUsername());
		job.setLabel(getLabel());
		job.setDescription(getDescription());
		job.setTrigger(copyTriggerTo());
		job.setSource(copySourceTo(newReportUri, configuration, context, importContext));
		job.setBaseOutputFilename(getBaseOutputFilename());
		job.setOutputFormats(copyOutputFormatsTo());
		job.setOutputLocale(getOutputLocale());
		job.setContentRepositoryDestination(copyRepositoryDestinationTo(importContext));
		job.setMailNotification(copyMailNotificationTo());
        job.setAlert(copyAlertTo());
        job.setCreationDate(getCreationDate() != null? getCreationDate() : new Timestamp((new GregorianCalendar()).getTimeInMillis()));

		if (!importContext.getNewGeneratedTenantIds().isEmpty()) {
			job.setUsername(TenantStrHolderPattern.TENANT_QUALIFIED_NAME
					.replaceWithNewTenantIds(importContext.getNewGeneratedTenantIds(), job.getUsername()));
		}
	}

	protected ReportJobTrigger copyTriggerTo() {
		ReportJobTrigger jobTrigger;
		if (trigger == null) {
			jobTrigger = null;
		} else {
			jobTrigger = trigger.toJobTrigger();
			trigger.copyTo(jobTrigger);
		}
		return jobTrigger;
	}

	protected ReportJobSource copySourceTo(String newReportUri,
			SchedulingModuleConfiguration configuration, ExecutionContext context,
			ImporterModuleContext importContext) {
		ReportJobSource jobSource;
		if (source == null) {
			jobSource = null;
		} else {
			jobSource = new ReportJobSource();
			source.copyTo(jobSource, newReportUri, configuration, context, importContext);
		}
		return jobSource;
	}

	protected Set copyOutputFormatsTo() {
		Set formats;
		if (outputFormats == null) {
			formats = null;
		} else {
			formats = new HashSet();
			for (int i = 0; i < outputFormats.length; i++) {
				formats.add(new Byte(outputFormats[i]));
			}
		}
		return formats;
	}

	protected ReportJobRepositoryDestination copyRepositoryDestinationTo(ImporterModuleContext importContext) {
		ReportJobRepositoryDestination dest;
		if (contentRepositoryDestination == null) {
			dest = null;
		} else {
			dest = new ReportJobRepositoryDestination();
			contentRepositoryDestination.copyTo(dest, importContext);
		}
		return dest;
	}

	protected ReportJobMailNotification copyMailNotificationTo() {
		ReportJobMailNotification mail;
		if (mailNotification == null) {
			mail = null;
		} else {
			mail = new ReportJobMailNotification();
			mailNotification.copyTo(mail);
		}
		return mail;
	}

    protected ReportJobAlert copyAlertTo() {
		ReportJobAlert jobAlert;
		if (alert == null) {
			jobAlert = null;
		} else {
			jobAlert = new ReportJobAlert();
			alert.copyTo(jobAlert);
		}
		return jobAlert;
	}

	public String getBaseOutputFilename() {
		return baseOutputFilename;
	}
	
	public void setBaseOutputFilename(String baseOutputFilename) {
		this.baseOutputFilename = baseOutputFilename;
	}
	
	public ReportJobRepositoryDestinationBean getContentRepositoryDestination() {
		return contentRepositoryDestination;
	}
	
	public void setContentRepositoryDestination(
			ReportJobRepositoryDestinationBean contentRepositoryDestination) {
		this.contentRepositoryDestination = contentRepositoryDestination;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public ReportJobMailNotificationBean getMailNotification() {
		return mailNotification;
	}
	
	public void setMailNotification(ReportJobMailNotificationBean mailNotification) {
		this.mailNotification = mailNotification;
	}

	public ReportJobAlertBean getAlert() {
		return alert;
	}

	public void setAlert(ReportJobAlertBean alert) {
		this.alert = alert;
	}

	public byte[] getOutputFormats() {
		return outputFormats;
	}
	
	public void setOutputFormats(byte[] outputFormats) {
		this.outputFormats = outputFormats;
	}
	
	public String getOutputLocale() {
		return outputLocale;
	}

	public void setOutputLocale(String outputLocale) {
		this.outputLocale = outputLocale;
	}

	public ReportJobSourceBean getSource() {
		return source;
	}
	
	public void setSource(ReportJobSourceBean source) {
		this.source = source;
	}
	
	public ReportJobTriggerBean getTrigger() {
		return trigger;
	}
	
	public void setTrigger(ReportJobTriggerBean trigger) {
		this.trigger = trigger;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
