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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobMailNotificationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PersistentReportJobMailNotification {
	
	private long id;
	private int version;
	private List recipients;
	private String subject;
	private String messageText;
	private byte resultSendType;
	private boolean skipEmptyReports;
    private String messageTextWhenJobFails = null;
    private boolean includingStackTraceWhenJobFails = false;
    private boolean SkipNotificationWhenJobFails = false;

	public PersistentReportJobMailNotification() {
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List getRecipients() {
		return recipients;
	}

	public void setRecipients(List toAddresses) {
		this.recipients = toAddresses;
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
        return SkipNotificationWhenJobFails;
    }

    public void setSkipNotificationWhenJobFails(boolean skipNotificationWhenJobFails) {
        SkipNotificationWhenJobFails = skipNotificationWhenJobFails;
    }

	public void copyFrom(ReportJobMailNotification mailNotification) {
		copyRecipientsFrom(mailNotification);
		setSubject(mailNotification.getSubject());
		setMessageText(mailNotification.getMessageText());
		setResultSendType(mailNotification.getResultSendType());
		setSkipEmptyReports(mailNotification.isSkipEmptyReports());
        setMessageTextWhenJobFails(mailNotification.getMessageTextWhenJobFails());
        setIncludingStackTraceWhenJobFails(mailNotification.isIncludingStackTraceWhenJobFails());
        setSkipNotificationWhenJobFails(mailNotification.isSkipNotificationWhenJobFails());
	}

    public void copyFrom(ReportJobMailNotificationModel mailNotification) {
        ReportJobMailNotification originalCopy = toClient();
        List newRecipients = new ArrayList();
        if (mailNotification.isToAddressesModified()) collectRecipients(newRecipients, mailNotification.getToAddresses(), PersistentReportJobMailRecipient.TYPE_TO);
        else collectRecipients(newRecipients, originalCopy.getToAddresses(), PersistentReportJobMailRecipient.TYPE_TO);
		if (mailNotification.isCcAddressesModified()) collectRecipients(newRecipients, mailNotification.getCcAddresses(), PersistentReportJobMailRecipient.TYPE_CC);
        else collectRecipients(newRecipients, originalCopy.getCcAddresses(), PersistentReportJobMailRecipient.TYPE_CC);
        if (mailNotification.isBccAddressesModified()) collectRecipients(newRecipients, mailNotification.getBccAddresses(), PersistentReportJobMailRecipient.TYPE_BCC);
        else collectRecipients(newRecipients, originalCopy.getBccAddresses(), PersistentReportJobMailRecipient.TYPE_BCC);
        setRecipients(newRecipients);
		if (mailNotification.isSubjectModified()) setSubject(mailNotification.getSubject());
		if (mailNotification.isMessageTextModified()) setMessageText(mailNotification.getMessageText());
		if (mailNotification.isResultSendTypeModified()) setResultSendType(mailNotification.getResultSendTypeCode());
		if (mailNotification.isSkipEmptyReportsModified()) setSkipEmptyReports(mailNotification.isSkipEmptyReports());
        if (mailNotification.isMessageTextWhenJobFailsModified()) setMessageTextWhenJobFails(mailNotification.getMessageTextWhenJobFails());
        if (mailNotification.isIncludingStackTraceWhenJobFailsModified()) setIncludingStackTraceWhenJobFails(mailNotification.isIncludingStackTraceWhenJobFails());
        if (mailNotification.isSkipNotificationWhenJobFailsModified()) setSkipNotificationWhenJobFails(mailNotification.isSkipNotificationWhenJobFails());
	}

	protected void copyRecipientsFrom(ReportJobMailNotification mailNotification) {
		List newRecipients = new ArrayList();
		collectRecipients(newRecipients, mailNotification.getToAddresses(), PersistentReportJobMailRecipient.TYPE_TO);
		collectRecipients(newRecipients, mailNotification.getCcAddresses(), PersistentReportJobMailRecipient.TYPE_CC);
		collectRecipients(newRecipients, mailNotification.getBccAddresses(), PersistentReportJobMailRecipient.TYPE_BCC);
		setRecipients(newRecipients);
	}

	protected void collectRecipients(List recipientsList, List addresses, byte type) {
		if (addresses != null && !addresses.isEmpty()) {
			for (Iterator it = addresses.iterator(); it.hasNext();) {
				String address = (String) it.next();
				PersistentReportJobMailRecipient recipient = new PersistentReportJobMailRecipient();
				recipient.setType(type);
				recipient.setAddress(address);
				recipientsList.add(recipient);
			}
		}
	}

	public ReportJobMailNotification toClient() {
		ReportJobMailNotification mail = new ReportJobMailNotification();
		mail.setId(getId());
		mail.setVersion(getVersion());
		copyAddressesTo(mail);
		mail.setSubject(getSubject());
		mail.setMessageText(getMessageText());
		mail.setResultSendType(getResultSendType());
		mail.setSkipEmptyReports(isSkipEmptyReports());
        mail.setMessageTextWhenJobFails(getMessageTextWhenJobFails());
        mail.setIncludingStackTraceWhenJobFails(isIncludingStackTraceWhenJobFails());
        mail.setSkipNotificationWhenJobFails(isSkipNotificationWhenJobFails());
		return mail;
	}

	protected void copyAddressesTo(ReportJobMailNotification mail) {
		Map collectedAddresses = new HashMap();
		collectedAddresses.put(new Byte(PersistentReportJobMailRecipient.TYPE_TO), new ArrayList());
		collectedAddresses.put(new Byte(PersistentReportJobMailRecipient.TYPE_BCC), new ArrayList());
		collectedAddresses.put(new Byte(PersistentReportJobMailRecipient.TYPE_CC), new ArrayList());
		if (getRecipients() != null) {
            for (Iterator it = getRecipients().iterator(); it.hasNext();) {
                PersistentReportJobMailRecipient recipient = (PersistentReportJobMailRecipient) it.next();
                ((List) collectedAddresses.get(new Byte(recipient.getType()))).add(recipient.getAddress());
            }
        }
		mail.setToAddresses((List) collectedAddresses.get(new Byte(PersistentReportJobMailRecipient.TYPE_TO)));
		mail.setCcAddresses((List) collectedAddresses.get(new Byte(PersistentReportJobMailRecipient.TYPE_CC)));
		mail.setBccAddresses((List) collectedAddresses.get(new Byte(PersistentReportJobMailRecipient.TYPE_BCC)));
	}

	public boolean isNew() {
		return getVersion() == ReportJob.VERSION_NEW;
	}

	public boolean isSkipEmptyReports() {
		return skipEmptyReports;
	}

	public void setSkipEmptyReports(boolean skipEmptyReports) {
		this.skipEmptyReports = skipEmptyReports;
	}

}
