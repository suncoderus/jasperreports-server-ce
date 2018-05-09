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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobMailNotificationModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Email notification that can be defined for a report job.
 * 
 * <p>
 * A notification will result in an email being send to the specified recipients
 * at each job execution.
 * </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.0
 * @see ReportJob#getMailNotification()
 */
@JasperServerAPI
public class ReportJobMailNotification implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Used when the email notification should contain links to the job output
	 * generated in the repository.
	 * 
	 * @see #getResultSendType()
	 */
	public static final byte RESULT_SEND = 1;
	
	/**
	 * Used when the email notification should contain the job output as 
	 * attachments.
	 * 
	 * @see #getResultSendType()
	 */
	public static final byte RESULT_SEND_ATTACHMENT = 2;

    /**
	 * Used when the email notification should contain the job output as
	 * non-zip format attachments.
	 *
	 * @see #getResultSendType()
	 */
	public static final byte RESULT_SEND_ATTACHMENT_NOZIP = 3;

    /**
	 * Used when the email notification should embed the HTML job output in
	 * the email body.
	 *
	 * @see #getResultSendType()
	 */
    public static final byte RESULT_SEND_EMBED = 4;

    /**
	 * Used when the email notification should contain the job output as
	 * attachments in one zip file.
	 *
	 * @see #getResultSendType()
	 */
	public static final byte RESULT_SEND_ATTACHMENT_ZIP_ALL = 5;

    /**
	 * Used when the email notification should embed the HTML job output in
	 * the email body and put the other output type attachments in one zip file.
	 *
	 * @see #getResultSendType()
	 */
    public static final byte RESULT_SEND_EMBED_ZIP_ALL_OTHERS = 6;


    private long id;
	private int version = ReportJob.VERSION_NEW;
	private List toAddresses;
	private List ccAddresses;
	private List bccAddresses;
	private String subject;
	private String messageText;
	private Byte resultSendType = RESULT_SEND;
	private boolean skipEmptyReports;
    private String messageTextWhenJobFails = null;
    private boolean includingStackTraceWhenJobFails = false;
    private boolean skipNotificationWhenJobFails = false;

	/**
	 * Creates an empty job email notification.
	 */
	public ReportJobMailNotification() {
		super();
		toAddresses = new ArrayList();
		ccAddresses = new ArrayList();
		bccAddresses = new ArrayList();
	}
	
	/**
	 * Returns the ID of the email notification.
	 * 
	 * <p>
	 * The ID is automatically generated when the job for which the notification
	 * is defined gets saved in the scheduler.
	 * </p>
	 * 
	 * @return the ID of the email notification
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the ID of the email notification.
	 * 
	 * <p>
	 * The ID needs to be set when updating a report job, but not when saving
	 * a new job.
	 * </p>
	 * 
	 * @param id the notification ID
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the persistent version of this email notification.
	 * 
	 * <p>
	 * The version is automatically set when the notification is retrieved
	 * (along with the job) from the scheduling service.
	 * </p>
	 * 
	 * @return the persistent notification version
	 * @see ReportJob#VERSION_NEW
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the persistent email notification version.
	 * 
	 * <p>
	 * The version doesn't have to be set when saving a new report job,
	 * but it needs to be set when updating a report job (along with its 
	 * notification).
	 * </p>
	 * 
	 * @param version the persistent notification version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Returns the text of the email notification.
	 * 
	 * <p>
	 * At job execution time, links to the output and errors might get appended
	 * to the notification message text. 
	 * </p>
	 * 
	 * @return the notification message text
	 * @see #setMessageText(String)
	 */
	public String getMessageText() {
		return messageText;
	}

	/**
	 * Sets the message text to be used for the email notification.
	 * 
	 * @param messageText the notification message text
	 */
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	/**
	 * Determines whether the notification would include the job output as
	 * attachments, or include links to the output in the repository.
	 * 
	 * @return one of {@link #RESULT_SEND} and {@link #RESULT_SEND_ATTACHMENT}
	 */
	public Byte getResultSendTypeCode() {
		return resultSendType;
	}

	/**
	 * Determines whether the notification would include the job output as
	 * attachments, or include links to the output in the repository.
	 *
	 * @return one of {@link #RESULT_SEND} and {@link #RESULT_SEND_ATTACHMENT}
     * @deprecated use #getResultSendTypeCode() instead
	 */
	public byte getResultSendType() {
		return getResultSendTypeCode();
	}

	/**
	 * Specifies whether the notification would include the job output as
	 * attachments, or include links to the output in the repository.
	 * 
	 * @param resultSendType one of {@link #RESULT_SEND} and 
	 * {@link #RESULT_SEND_ATTACHMENT}
     * @deprecated use #setResultSendTypeCode(Byte resultSendType) instead
	 */
	public void setResultSendType(byte resultSendType) {
		setResultSendTypeCode(resultSendType);
	}

	/**
	 * Specifies whether the notification would include the job output as
	 * attachments, or include links to the output in the repository.
	 *
	 * @param resultSendType one of {@link #RESULT_SEND} and
	 * {@link #RESULT_SEND_ATTACHMENT}
	 */
	public void setResultSendTypeCode(Byte resultSendType) {
		this.resultSendType = resultSendType;
	}

	/**
	 * Returns the subject to be used for the email notification.
	 * 
	 * @return the email notification subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject to be used for the email notification.
	 * 
	 * @param subject the email notification subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Returns the list of Bcc (Blind carbon copy) recipients of the email
	 * notification.
	 * 
	 * @return the list of Bcc notification recipients as 
	 * <code>java.lang.String</code> email addresses
	 */
	public List getBccAddresses() {
		return bccAddresses;
	}

	/**
	 * Sets the email addresses that should be used as Bcc (Blind carbon copy)
	 * recipients for the email notification.
	 * 
	 * @param bccAddresses the list of Bcc recipients as 
	 * <code>java.lang.String</code> email addresses
	 */
	public void setBccAddresses(List bccAddresses) {
		this.bccAddresses = bccAddresses;
	}
	
	/**
	 * Adds a Bcc (Blind carbon copy) recipient for the email notification.
	 * 
	 * @param address the email address of the recipient
	 */
	public void addBcc(String address) {
		this.bccAddresses.add(address);
	}

	/**
	 * Returns the list of CC (Carbon copy) recipients of the email
	 * notification.
	 * 
	 * @return the list of CC notification recipients as 
	 * <code>java.lang.String</code> email addresses
	 */
	public List getCcAddresses() {
		return ccAddresses;
	}

	/**
	 * Sets the email addresses that should be used as CC (Carbon copy)
	 * recipients for the email notification.
	 * 
	 * @param ccAddresses the list of CC recipients as 
	 * <code>java.lang.String</code> email addresses
	 */
	public void setCcAddresses(List ccAddresses) {
		this.ccAddresses = ccAddresses;
	}
	
	/**
	 * Adds a CC (Carbon copy) recipient for the email notification.
	 * 
	 * @param address the email address of the recipient
	 */
	public void addCc(String address) {
		this.ccAddresses.add(address);
	}

	/**
	 * Returns the list of direct recipients of the email notification.
	 * 
	 * @return the list of direct notification recipients as 
	 * <code>java.lang.String</code> email addresses
	 */
	public List getToAddresses() {
		return toAddresses;
	}

	/**
	 * Sets the email addresses that should be used as direct recipients for 
	 * the email notification.
	 * 
	 * @param toAddresses the list of CC recipients as 
	 * <code>java.lang.String</code> email addresses
	 */
	public void setToAddresses(List toAddresses) {
		this.toAddresses = toAddresses;
	}
	
	/**
	 * Adds a CC (Carbon copy) recipient for the email notification.
	 * 
	 * @param address the email address of the recipient
	 */
	public void addTo(String address) {
		this.toAddresses.add(address);
	}

	/**
	 * Determines if this notification definition should be considered empty.
	 * 
	 * <p>
	 * A notification is considered empty is there are no direct recipients for it.
	 * If the notification is empty it will not be used by the report job.
	 * </p>
	 * 
	 * @return
	 * @see #getToAddresses()
	 */
	public boolean isEmpty() {
        boolean isEmpty = true;
        if( getToAddresses() != null ) {
            isEmpty = getToAddresses().isEmpty();
        }
		return isEmpty;
	}

	/**
	 * Decides whether the email notification should be skipped for job 
	 * executions the produce empty reports.
	 * 
	 * <p>
	 * An executed report is considered empty if it doesn't have any generated
	 * content.
	 * </p>
	 * 
	 * @return a flag that determines whether notifications should be skipped for
	 * empty reports 
	 * @since 2.0
	 */
	public boolean isSkipEmptyReports() {
		return skipEmptyReports;
	}

	/**
	 * Specifies whether the email notification should be skipped for job 
	 * executions the produce empty reports.
	 * 
	 * @param skipEmptyReports if <code>true</code>, no email notification will
	 * be sent if job executions that generate empty reports
	 * @since 2.0
	 */
	public void setSkipEmptyReports(boolean skipEmptyReports) {
		this.skipEmptyReports = skipEmptyReports;
	}

  /**
   * Convenience constructor that returns a distinct copy of the input ReportJob.
   * All of the copy's Object members are themselves copies as well.
   *
   * We're deliberately avoiding using clone()
   *
   * @param jobMail
   * @return
   */
  public ReportJobMailNotification(ReportJobMailNotification jobMail) {
    // skip id and version which are generated
    // a null ReportJobMailNotification is valid - in this case, we don't copy anything...
    if( jobMail != null )
    {
        this.setMessageText(jobMail.getMessageText());
        this.setResultSendTypeCode(jobMail.getResultSendTypeCode());
        this.setSubject(jobMail.getSubject());
        List l = jobMail.getBccAddresses();
        if (l != null) this.setBccAddresses(new ArrayList(l));
        l = jobMail.getCcAddresses();
        if (l != null) this.setCcAddresses(new ArrayList(l));
        l = jobMail.getToAddresses();
        if (l != null) this.setToAddresses(new ArrayList(l));
        this.setSkipEmptyReports(jobMail.isSkipEmptyReports());
		this.setMessageTextWhenJobFails(jobMail.getMessageTextWhenJobFails());
		this.setIncludingStackTraceWhenJobFails(jobMail.includingStackTraceWhenJobFails);
    }
  }

    /**
	 * Returns the text of the email notification when the job fails.
	 *
	 * <p>
	 * At job execution time, links to the output and errors might get appended
	 * to the notification message text.
	 * </p>
	 *
	 * @return the notification message text
	 * @see #setMessageText(String)
	 */
    public String getMessageTextWhenJobFails() {
        return messageTextWhenJobFails;
    }

	/**
	 * Sets the message text to be used for the email notification when job fails.
	 *
	 * @param messageTextWhenJobFails the notification message text
	 */
    public void setMessageTextWhenJobFails(String messageTextWhenJobFails) {
        this.messageTextWhenJobFails = messageTextWhenJobFails;
    }

    /**
	 * Determines whether the mail notification would include detail stack trace of exception
	 *
	 * @return true if including stack trace in mail notification
	 */
    public boolean isIncludingStackTraceWhenJobFails() {
        return includingStackTraceWhenJobFails;
    }

    /**
	 * Specifies whether the mail notification would include detail stack trace of exception
	 *
	 * @param includingStackTraceWhenJobFails including stack trace in mail notification
	 */
    public void setIncludingStackTraceWhenJobFails(boolean includingStackTraceWhenJobFails) {
        this.includingStackTraceWhenJobFails = includingStackTraceWhenJobFails;
    }

    /**
	 * Determines whether the mail notification should send if job fails
	 *
	 * @return true to skip mail notification when job fails
	 */
    public boolean isSkipNotificationWhenJobFails() {
        return skipNotificationWhenJobFails;
    }

    /**
	 * Specifies whether the mail notification should send if job fails
	 *
	 * @param skipNotificationWhenJobFails skip mail notification when job fails
	 */
    public void setSkipNotificationWhenJobFails(boolean skipNotificationWhenJobFails) {
        this.skipNotificationWhenJobFails = skipNotificationWhenJobFails;
    }

	public void copyFromModel(ReportJobMailNotificationModel mailNotificationModel) {
		if (mailNotificationModel.isToAddressesModified()) setToAddresses(copyAddressList(mailNotificationModel.getToAddresses()));
		if (mailNotificationModel.isCcAddressesModified()) setCcAddresses(copyAddressList(mailNotificationModel.getCcAddresses()));
		if (mailNotificationModel.isBccAddressesModified()) setBccAddresses(copyAddressList(mailNotificationModel.getBccAddresses()));
		if (mailNotificationModel.isSubjectModified()) setSubject(mailNotificationModel.getSubject());
		if (mailNotificationModel.isMessageTextModified()) setMessageText(mailNotificationModel.getMessageText());
		if (mailNotificationModel.isResultSendTypeModified()) setResultSendTypeCode(mailNotificationModel.getResultSendTypeCode());
		if (mailNotificationModel.isSkipEmptyReportsModified()) setSkipEmptyReports(mailNotificationModel.isSkipEmptyReports());
		if (mailNotificationModel.isMessageTextWhenJobFailsModified()) setMessageTextWhenJobFails(mailNotificationModel.getMessageTextWhenJobFails());
		if (mailNotificationModel.isIncludingStackTraceWhenJobFailsModified()) setIncludingStackTraceWhenJobFails(mailNotificationModel.isIncludingStackTraceWhenJobFails());
		if (mailNotificationModel.isSkipNotificationWhenJobFailsModified()) setSkipNotificationWhenJobFails(mailNotificationModel.isSkipNotificationWhenJobFails());
	}

	private List copyAddressList(List originList) {
		List copiedAddresses = new ArrayList(originList.size());
		for (Object address : originList) {
            copiedAddresses.add(address);
        }
		return copiedAddresses;
	}
}
