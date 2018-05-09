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

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;


/**
 * @author tkavanagh
 * @version $Id$
 */
public class ReportJobMailNotificationBean {

	private long id;
	private int version;
	private String[] toAddresses;
	private String[] ccAddresses;
	private String[] bccAddresses;
	private String subject;
	private String messageText;
	private byte resultSendType;
	private boolean skipEmptyReports;
    private String messageTextWhenJobFails = null;
    private boolean includingStackTraceWhenJobFails = false;
    private boolean skipNotificationWhenJobFails = false;

	public void copyFrom(ReportJobMailNotification mailNotification) {
		setId(mailNotification.getId());
		setVersion(mailNotification.getVersion());
		setToAddresses(copyAddressesFrom(mailNotification.getToAddresses()));
		setCcAddresses(copyAddressesFrom(mailNotification.getCcAddresses()));
		setBccAddresses(copyAddressesFrom(mailNotification.getBccAddresses()));
		setSubject(mailNotification.getSubject());
		setMessageText(mailNotification.getMessageText());
		setResultSendType(mailNotification.getResultSendType());
		setSkipEmptyReports(mailNotification.isSkipEmptyReports());
        setMessageTextWhenJobFails(mailNotification.getMessageTextWhenJobFails());
        setIncludingStackTraceWhenJobFails(mailNotification.isIncludingStackTraceWhenJobFails());
        setSkipNotificationWhenJobFails(mailNotification.isSkipNotificationWhenJobFails());
	}
	
	protected String[] copyAddressesFrom(List addresses) {
		String[] addressesArray;
		if (addresses == null || addresses.isEmpty()) {
			addressesArray = null;
		} else {
			addressesArray = new String[addresses.size()];
			addressesArray = (String[]) addresses.toArray(addressesArray);
		}
		return addressesArray;
	}

	public void copyTo(ReportJobMailNotification mailNotification) {
		mailNotification.setToAddresses(copyAddressesTo(getToAddresses()));
		mailNotification.setCcAddresses(copyAddressesTo(getCcAddresses()));
		mailNotification.setBccAddresses(copyAddressesTo(getBccAddresses()));
		mailNotification.setSubject(getSubject());
		mailNotification.setMessageText(getMessageText());
		mailNotification.setResultSendType(getResultSendType());
		mailNotification.setSkipEmptyReports(isSkipEmptyReports());
        mailNotification.setMessageTextWhenJobFails(getMessageTextWhenJobFails());
        mailNotification.setIncludingStackTraceWhenJobFails(isIncludingStackTraceWhenJobFails());
        mailNotification.setSkipNotificationWhenJobFails(isSkipNotificationWhenJobFails());
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

	public String[] getToAddresses() {
		return toAddresses;
	}
	
	public void setToAddresses(String[] toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String[] getCcAddresses() {
		return ccAddresses;
	}
	
	public void setCcAddresses(String[] ccAddresses) {
		this.ccAddresses = ccAddresses;
	}
	
	public String[] getBccAddresses() {
		return bccAddresses;
	}
	public void setBccAddresses(String[] bccAddresses) {
		this.bccAddresses = bccAddresses;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}	
	
	public String getMessageText() {
		return messageText;
	}
	
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	
	public byte getResultSendType() {
		return resultSendType;
	}
	
	public void setResultSendType(byte resultSendType) {
		this.resultSendType = resultSendType;
	}

	public boolean isSkipEmptyReports() {
		return skipEmptyReports;
	}

	public void setSkipEmptyReports(boolean skipEmptyReports) {
		this.skipEmptyReports = skipEmptyReports;
	}

    public String getMessageTextWhenJobFails() {
        return messageTextWhenJobFails;
    }

    public void setMessageTextWhenJobFails(String messageTextWhenJobFails) {
        this.messageTextWhenJobFails = messageTextWhenJobFails;
    }

    public boolean isIncludingStackTraceWhenJobFails() {
        return includingStackTraceWhenJobFails;
    }

    public void setIncludingStackTraceWhenJobFails(boolean includingStackTraceWhenJobFails) {
        this.includingStackTraceWhenJobFails = includingStackTraceWhenJobFails;
    }

    public boolean isSkipNotificationWhenJobFails() {
        return skipNotificationWhenJobFails;
    }

    public void setSkipNotificationWhenJobFails(boolean skipNotificationWhenJobFails) {
        this.skipNotificationWhenJobFails = skipNotificationWhenJobFails;
    }
}
