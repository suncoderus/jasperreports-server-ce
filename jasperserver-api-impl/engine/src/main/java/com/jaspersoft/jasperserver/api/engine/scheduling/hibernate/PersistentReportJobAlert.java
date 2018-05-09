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

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobAlert;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobAlertModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class PersistentReportJobAlert {

	private long id;
	private int version;
    private byte recipient = ReportJobAlert.Recipient.OWNER_AND_ADMIN.getCode();
    private List<String> toAddresses = new ArrayList<String>();
    private byte jobState = ReportJobAlert.JobState.FAIL_ONLY.getCode();
    private String messageText = null;
    private String messageTextWhenJobFails = null;
    private String subject = null;
    private boolean includingStackTrace = true;
    private boolean includingReportJobInfo = true;

	public PersistentReportJobAlert() {
		version = ReportJob.VERSION_NEW;
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

    public byte getRecipient() {
        return recipient;
    }

    public void setRecipient(byte recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getToAddresses() {
        if (toAddresses == null) return new ArrayList<String>();
        ArrayList<String> addresses = new ArrayList<String>();
        for (String address : toAddresses) addresses.add(address);
        return addresses;
    }

    public void setToAddresses(List<String> toAddresses) {
        if (toAddresses == null) toAddresses = new ArrayList<String>();
        this.toAddresses = toAddresses;
    }

    public byte getJobState() {
        return jobState;
    }

    public void setJobState(byte jobState) {
        this.jobState = jobState;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageTextWhenJobFails() {
        return messageTextWhenJobFails;
    }

    public void setMessageTextWhenJobFails(String messageTextWhenJobFails) {
        this.messageTextWhenJobFails = messageTextWhenJobFails;
    }

    public boolean isIncludingStackTrace() {
        return includingStackTrace;
    }

    public void setIncludingStackTrace(boolean includingStackTrace) {
        this.includingStackTrace = includingStackTrace;
    }

    public boolean isIncludingReportJobInfo() {
        return includingReportJobInfo;
    }

    public void setIncludingReportJobInfo(boolean includingReportJobInfo) {
        this.includingReportJobInfo = includingReportJobInfo;
    }

	public void copyFrom(ReportJobAlert alert) {
        setRecipient(alert.getRecipient().getCode());
        setToAddresses(alert.getToAddresses());
        setJobState(alert.getJobState().getCode());
        setMessageText(alert.getMessageText());
        setMessageTextWhenJobFails(alert.getMessageTextWhenJobFails());
        setSubject(alert.getSubject());
        setIncludingStackTrace(alert.isIncludingStackTrace());
        setIncludingReportJobInfo(alert.isIncludingReportJobInfo());
	}

    public void copyFrom(ReportJobAlertModel alert) {
        if (alert.isRecipientModified()) setRecipient(alert.getRecipient().getCode());
        if (alert.isToAddressesModified()) setToAddresses(alert.getToAddresses());
        if (alert.isJobStateModified()) setJobState(alert.getJobState().getCode());
        if (alert.isMessageTextModified()) setMessageText(alert.getMessageText());
        if (alert.isMessageTextWhenJobFailsModified()) setMessageTextWhenJobFails(alert.getMessageTextWhenJobFails());
        if (alert.isIncludingStackTraceModified()) setIncludingStackTrace(alert.isIncludingStackTrace());
        if (alert.isIncludingReportJobInfoModified()) setIncludingReportJobInfo(alert.isIncludingReportJobInfo());
        if (alert.isSubjectModified()) setSubject(alert.getSubject());
	}

	public ReportJobAlert toClient() {
		ReportJobAlert alert = new ReportJobAlert();
        alert.setRecipient(ReportJobAlert.Recipient.fromCode(getRecipient()));
        alert.setToAddresses(getToAddresses());
        alert.setJobState(ReportJobAlert.JobState.fromCode(getJobState()));
        alert.setMessageText(getMessageText());
        alert.setMessageTextWhenJobFails(getMessageTextWhenJobFails());
        alert.setIncludingStackTrace(isIncludingStackTrace());
        alert.setIncludingReportJobInfo(isIncludingReportJobInfo());
        alert.setSubject(getSubject());
		return alert;
	}

	public boolean isNew() {
		return getVersion() == ReportJob.VERSION_NEW;
	}

}
