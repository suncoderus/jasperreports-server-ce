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

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobAlert;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ichan
 * @version $Id$
 */
public class ReportJobAlertBean {

	private long id;
	private int version;
    private byte recipient = ReportJobAlert.Recipient.OWNER_AND_ADMIN.getCode();
    private String[] toAddresses;
    private byte jobState = ReportJobAlert.JobState.FAIL_ONLY.getCode();
    private String messageText = null;
    private String messageTextWhenJobFails = null;
    private String subject = null;
    private boolean includingStackTrace = true;
    private boolean includingReportJobInfo = true;

	public void copyFrom(ReportJobAlert alert) {
		setId(alert.getId());
		setVersion(alert.getVersion());
        setRecipient(alert.getRecipient().getCode());
		setToAddresses(copyAddressesFrom(alert.getToAddresses()));
        setJobState(alert.getJobState().getCode());
        setMessageText(alert.getMessageText());
        setMessageTextWhenJobFails(alert.getMessageTextWhenJobFails());
        setSubject(alert.getSubject());
        setIncludingStackTrace(alert.isIncludingStackTrace());
        setIncludingReportJobInfo(alert.isIncludingReportJobInfo());
	}
	
	protected String[] copyAddressesFrom(List<String> addresses) {
		String[] addressesArray;
		if (addresses == null || addresses.isEmpty()) {
			addressesArray = null;
		} else {
			addressesArray = new String[addresses.size()];
			addressesArray = (String[]) addresses.toArray(addressesArray);
		}
		return addressesArray;
	}

	public void copyTo(ReportJobAlert alert) {
        alert.setRecipient(ReportJobAlert.Recipient.fromCode(getRecipient()));
		alert.setToAddresses(copyAddressesTo(getToAddresses()));
        alert.setJobState(ReportJobAlert.JobState.fromCode(getJobState()));
        alert.setMessageText(getMessageText());
        alert.setMessageTextWhenJobFails(getMessageTextWhenJobFails());
        alert.setSubject(getSubject());
        alert.setIncludingStackTrace(isIncludingStackTrace());
        alert.setIncludingReportJobInfo(isIncludingReportJobInfo());
	}
	
	protected List copyAddressesTo(String[] addresses) {
		List addressesList;
		if (addresses == null) {
			addressesList = null;
		} else {
			addressesList = new ArrayList(addresses.length);
			for (int i = 0; i < addresses.length; i++) {
				addressesList.add(addresses[i]);
			}
		}
		return addressesList;
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

    public String[] getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(String[] toAddresses) {
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

}
