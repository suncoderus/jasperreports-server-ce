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
package com.jaspersoft.jasperserver.api.engine.scheduling.hibernate;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobAlert;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobAlertModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobCalendarTriggerModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobMailNotificationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobRepositoryDestinationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSimpleTriggerModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSourceModel;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceLight;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PersistentReportJob {

	private long id;
	private int version;
	private RepoUser owner;
	private String label;
	private String description;
	private PersistentReportJobSource source;
	private RepoResourceLight scheduledResource;
	private PersistentReportJobTrigger trigger;
	private String baseOutputFilename;
	@SuppressWarnings("rawtypes")
	private Set outputFormats;
	private String outputLocale;
	private PersistentReportJobMailNotification mailNotification;
	private PersistentReportJobRepositoryDestination contentRepositoryDestination;
    private PersistentReportJobAlert alert;
    private Timestamp creationDate;


	@SuppressWarnings("rawtypes")
	public PersistentReportJob() {
		version = ReportJob.VERSION_NEW;
		outputFormats = new HashSet();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public PersistentReportJobSource getSource() {
		return source;
	}

	public void setSource(PersistentReportJobSource source) {
		this.source = source;
	}

	public PersistentReportJobTrigger getTrigger() {
		return trigger;
	}

	public void setTrigger(PersistentReportJobTrigger trigger) {
		this.trigger = trigger;
	}

	public PersistentReportJobMailNotification getMailNotification() {
		return mailNotification;
	}

	public void setMailNotification(PersistentReportJobMailNotification mailNotification) {
		this.mailNotification = mailNotification;
	}

    public PersistentReportJobAlert getAlert() {
		return alert;
	}

	public void setAlert(PersistentReportJobAlert alert) {
		this.alert = alert;
	}

	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	public void copyFrom(
			ReportJob job, 
			HibernateTemplate hibernateTemplate, 
			List unusedEntities, 
			ProfileAttributeService profileAttributeService,
			HibernateRepositoryService referenceResolver, 
            ExecutionContext context) {
		if ((job.getVersion() != ReportJob.VERSION_NEW) && (getVersion() != job.getVersion())) {
			throw new JSException("jsexception.job.no.versions.match", new Object[] {new Integer(job.getVersion()), new Integer(getVersion())});
		}

		// This method calls SELECT so we have to put it first, because
		// other methods might put Hibernate session in unstable state which would throw exception
		// on any select issued in the same session.
		
        copyScheduledResource(referenceResolver, job);
		copyContentRepositoryDestination(job, unusedEntities, profileAttributeService, context, referenceResolver);

		
		//////// @WARNING!!!!!!
		////////
		//////// These methods below might put underlying Hibernate session
		//////// in a state where simple unrelated detached queries 
		////////
		//////// (example: 
		////////     DetachedCriteria criteria = DetachedCriteria.forClass(RepoFolder.class);
		////////     criteria.add(Restrictions.naturalId().set("URI", workUri));
		////////     criteria.getExecutableCriteria(getSession()).setCacheable(true);
		////////     List foldersList = getHibernateTemplate().findByCriteria(criteria);
		//////// FAIL  ----------------------------------------^^^^^^^^^^^^^^  !!!)
		////////
		//////// because of transient nature of modified alerts and mail notifications!
		//////// AVOID putting any code below... until further investigation of
		//////// what is going on there...
		////////
		//////// @WARNING!!!!!!
		
        
        setLabel(job.getLabel());
        setCreationDate(job.getCreationDate());
		setDescription(job.getDescription());
		copySource(job);
		copyTrigger(job, hibernateTemplate, unusedEntities);
		setBaseOutputFilename(job.getBaseOutputFilename());
		setOutputFormats(job.getOutputFormats() != null ? new HashSet(job.getOutputFormats()) : null);
		setOutputLocale(job.getOutputLocale());
		copyMailNotification(job, unusedEntities);
        copyAlert(job, unusedEntities);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void copyFrom(ReportJobModel job, boolean replaceTrigger,  HibernateTemplate hibernateTemplate, List unusedEntities,
                         ProfileAttributeService profileAttributeService, ExecutionContext context) {
		if (job.isLabelModified()) setLabel(job.getLabel());
        if (job.isCreationDateModified() && job.getCreationDate() != null) setCreationDate(job.getCreationDate());
		if (job.isDescriptionModified()) setDescription(job.getDescription());
		if (job.isSourceModified()) copySource(job.getSourceModel());
		if (job.isTriggerModified() && job.getTriggerModel() != null) copyTrigger(job.getTriggerModel(), replaceTrigger, hibernateTemplate, unusedEntities);
		if (job.isBaseOutputFileNameModified()) setBaseOutputFilename(job.getBaseOutputFilename());
		if (job.isOutputFormatsModified()) {
            if (job.getOutputFormatsSet() == null) setOutputFormats(new HashSet());
            else setOutputFormats(new HashSet(job.getOutputFormatsSet()));
        }
		if (job.isOutputLocaleModified()) setOutputLocale(job.getOutputLocale());
		if (job.isContentRepositoryDestinationModified()) copyContentRepositoryDestination(job.getContentRepositoryDestinationModel(), unusedEntities, profileAttributeService, context);
		if (job.isMailNotificationModified()) copyMailNotification(job.getMailNotificationModel(), unusedEntities);
        if (job.isAlertModified()) copyAlert(job.getAlertModel(), unusedEntities);
	}

	private void copyScheduledResource(HibernateRepositoryService referenceResolver, ReportJob job) {
		// look for scheduled resource, then URI from source object
		String schedResourceURI = null;
		if (job.getScheduledResource() != null) {
			schedResourceURI = job.getScheduledResource().getReferenceURI();
		} else {
			schedResourceURI = job.getSource().getReportUnitURI();
		}
		// look up resource ref
		RepoResource repoResource = referenceResolver.findByURI(RepoResource.class, schedResourceURI, false);
		setScheduledResource(RepoResourceLight.fromRepoResource(repoResource));
	}

	protected void copySource(ReportJob job) {
		if (getSource() == null) {
			setSource(new PersistentReportJobSource());
		}
		getSource().copyFrom(job.getSource());
	}

    protected void copySource(ReportJobSourceModel source) {
		if (getSource() == null) {
            setSource(new PersistentReportJobSource());
            if (source == null) return;
        }
		if (source != null) getSource().copyFrom(source);
        else setSource(new PersistentReportJobSource());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void copyTrigger(ReportJob job, HibernateTemplate hibernateTemplate, List unusedEntities) {
		ReportJobTrigger jobTrigger = job.getTrigger();
		PersistentReportJobTrigger persistentTrigger = getTrigger();
		if (persistentTrigger != null && !persistentTrigger.supports(jobTrigger.getClass())) {
			unusedEntities.add(persistentTrigger);
			persistentTrigger = null;
		}
		
		if (persistentTrigger == null) {
			if (jobTrigger instanceof ReportJobSimpleTrigger) {
				persistentTrigger = new PersistentReportJobSimpleTrigger();
			} else if (jobTrigger instanceof ReportJobCalendarTrigger) {
				persistentTrigger = new PersistentReportJobCalendarTrigger();
			} else {
				String quotedJobTrigger = "\"" + jobTrigger.getClass().getName() + "\"";
				throw new JSException("jsexception.job.unknown.trigger.type", new Object[] {quotedJobTrigger});
			}
			setTrigger(persistentTrigger);
		}
		
		persistentTrigger.copyFrom(jobTrigger);
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected void copyTrigger(ReportJobTrigger jobTrigger, boolean replaceTrigger, HibernateTemplate hibernateTemplate, List unusedEntities) {
		PersistentReportJobTrigger persistentTrigger = getTrigger();
		if (persistentTrigger != null && !persistentTrigger.supports(jobTrigger.getClass())) {
            if (!replaceTrigger) return;
	        unusedEntities.add(persistentTrigger);
			persistentTrigger = null;
		}
		if (persistentTrigger == null) {
			if (jobTrigger instanceof ReportJobSimpleTriggerModel) {
				persistentTrigger = new PersistentReportJobSimpleTrigger();
			} else if (jobTrigger instanceof ReportJobCalendarTriggerModel) {
				persistentTrigger = new PersistentReportJobCalendarTrigger();
			} else {
				String quotedJobTrigger = "\"" + jobTrigger.getClass().getName() + "\"";
				throw new JSException("jsexception.job.unknown.trigger.type", new Object[] {quotedJobTrigger});
			}
			setTrigger(persistentTrigger);
		}
		persistentTrigger.copyFromModel(jobTrigger);
	}

	@SuppressWarnings("rawtypes")
	protected void copyContentRepositoryDestination(ReportJob job, List unusedEntities, ProfileAttributeService profileAttributeService,  ExecutionContext context, HibernateRepositoryService referenceResolver) {
		if (getContentRepositoryDestination() == null) {
			setContentRepositoryDestination(new PersistentReportJobRepositoryDestination());
		}
		getContentRepositoryDestination().copyFrom(job.getContentRepositoryDestination(), unusedEntities, profileAttributeService, getOwner(), context, referenceResolver);
	}

    @SuppressWarnings("rawtypes")
	protected void copyContentRepositoryDestination(ReportJobRepositoryDestinationModel repositoryDestinationModel, List unusedEntities,
                ProfileAttributeService profileAttributeService, ExecutionContext context) {
		if (getContentRepositoryDestination() == null) {
            setContentRepositoryDestination(new PersistentReportJobRepositoryDestination());
            if (repositoryDestinationModel == null) return;
        }
		if (repositoryDestinationModel != null) getContentRepositoryDestination().copyFrom(repositoryDestinationModel, unusedEntities, profileAttributeService, getOwner(), context, null);
        else setContentRepositoryDestination(new PersistentReportJobRepositoryDestination());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void copyMailNotification(ReportJob job, List unusedEntities) {
		ReportJobMailNotification jobMail = job.getMailNotification();
		PersistentReportJobMailNotification persistentMail = getMailNotification();
		if (jobMail == null) {
			if (persistentMail != null) {
				unusedEntities.add(persistentMail);
				setMailNotification(null);
			}
		} else {
			if (persistentMail == null) {
				persistentMail = new PersistentReportJobMailNotification();
				setMailNotification(persistentMail);
			}
			persistentMail.copyFrom(jobMail);
		}
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected void copyMailNotification(ReportJobMailNotificationModel jobMail, List unusedEntities) {
        PersistentReportJobMailNotification persistentMail = getMailNotification();
        if (persistentMail == null) {
            persistentMail = new PersistentReportJobMailNotification();
            setMailNotification(persistentMail);
            if (jobMail == null) return;
        } else if (jobMail == null) {
            unusedEntities.add(persistentMail);
        }
        if (jobMail != null) persistentMail.copyFrom(jobMail);
        else setMailNotification(null);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected void copyAlert(ReportJob job, List unusedEntities) {
		ReportJobAlert jobAlert = job.getAlert();
		PersistentReportJobAlert persistentAlert = getAlert();
		if (jobAlert == null) {
			if (persistentAlert != null) {
				unusedEntities.add(persistentAlert);
				setAlert(null);
			}
		} else {
			if (persistentAlert == null) {
				persistentAlert = new PersistentReportJobAlert();
				setAlert(persistentAlert);
			}
			persistentAlert.copyFrom(jobAlert);
		}
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void copyAlert(ReportJobAlertModel jobAlert, List unusedEntities) {
        PersistentReportJobAlert persistentAlert = getAlert();
        if (persistentAlert == null) {
            persistentAlert = new PersistentReportJobAlert();
            setAlert(persistentAlert);
        } else if (jobAlert == null) {
            unusedEntities.add(persistentAlert);
        }
        if (jobAlert != null) persistentAlert.copyFrom(jobAlert);
        else setAlert(new PersistentReportJobAlert());
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	public ReportJob toClient(ProfileAttributeService profileAttributeService, ExecutionContext context) {
		ReportJob job = new ReportJob();
		job.setId(getId());
		job.setUsername((getOwner() != null) ? getOwner().getUsername(): null);
		job.setVersion(getVersion());
		job.setLabel(getLabel());
        job.setCreationDate(getCreationDate());
		job.setDescription(getDescription());
        job.setCreationDate(getCreationDate());
		job.setSource(getSource().toClient());
		job.setTrigger(getTrigger().toClient());
		job.setBaseOutputFilename(getBaseOutputFilename());
		job.setOutputFormats(getOutputFormats() == null ? null : new HashSet(getOutputFormats()));
		job.setOutputLocale(getOutputLocale());
		job.setContentRepositoryDestination(getContentRepositoryDestination().toClient(profileAttributeService, getOwner(), context));
		job.setMailNotification(getMailNotification() == null ? null : getMailNotification().toClient());
        job.setAlert(getAlert() == null ? null : getAlert().toClient());
        
		return job;
	}

	public ReportJobSummary toSummary() {
		ReportJobSummary job = new ReportJobSummary();
		job.setId(getId());
		job.setVersion(getVersion());
		job.setReportUnitURI(getSource().getReportUnitURI());
		job.setLabel(getLabel());
		job.setDescription(getDescription());
		if (getScheduledResource() != null) {
			job.setReportLabel(getScheduledResource().getLabel());
		}
		return job;
	}

	public PersistentReportJobRepositoryDestination getContentRepositoryDestination() {
		return contentRepositoryDestination;
	}

	public void setContentRepositoryDestination(
			PersistentReportJobRepositoryDestination contentRepositoryDestination) {
		this.contentRepositoryDestination = contentRepositoryDestination;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBaseOutputFilename() {
		return baseOutputFilename;
	}

	public void setBaseOutputFilename(String baseOutputFilename) {
		this.baseOutputFilename = baseOutputFilename;
	}

	@SuppressWarnings("rawtypes")
	public Set getOutputFormats() {
		return outputFormats;
	}

	@SuppressWarnings("rawtypes")
	public void setOutputFormats(Set outputFormats) {
		this.outputFormats = outputFormats;
	}

    public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public boolean isNew() {
		return getVersion() == ReportJob.VERSION_NEW;
	}

	public void cascadeSave(HibernateTemplate hibernateTemplate) {
		if (getTrigger().isNew()) {
			hibernateTemplate.save(getTrigger());
		}
		if (getContentRepositoryDestination() != null && getContentRepositoryDestination().isNew()) {
			hibernateTemplate.save(getContentRepositoryDestination());
		}
		if (getMailNotification() != null && getMailNotification().isNew()) {
			hibernateTemplate.save(getMailNotification());
		}
        if (getAlert() != null && getAlert().isNew()) {
    		hibernateTemplate.save(getAlert());
        }
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void cascadeSave(List newEntities) {
		if (getTrigger().isNew()) {
			newEntities.add(getTrigger());
		}
		if (getContentRepositoryDestination() != null && getContentRepositoryDestination().isNew()) {
			newEntities.add(getContentRepositoryDestination());
		}
		if (getMailNotification() != null && getMailNotification().isNew()) {
			newEntities.add(getMailNotification());
		}
        if (getAlert() != null && getAlert().isNew()) {
			newEntities.add(getAlert());
		}
	}

	public void delete(HibernateTemplate hibernateTemplate) {
		hibernateTemplate.delete(this);
		hibernateTemplate.delete(getTrigger());
		if (getContentRepositoryDestination() != null) {
			hibernateTemplate.delete(getContentRepositoryDestination());
		}
		if (getMailNotification() != null) {
			hibernateTemplate.delete(getMailNotification());
		}
        if (getAlert() != null) {
			hibernateTemplate.delete(getAlert());
		}
	}

	public String getOutputLocale() {
		return outputLocale;
	}

	public void setOutputLocale(String outputLocale) {
		this.outputLocale = outputLocale;
	}

	public RepoUser getOwner() {
		return owner;
	}

	public void setOwner(RepoUser owner) {
		this.owner = owner;
	}

	public RepoResourceLight getScheduledResource() {
		return scheduledResource;
	}

	public void setScheduledResource(RepoResourceLight resource) {
		this.scheduledResource = resource;
	}
}
