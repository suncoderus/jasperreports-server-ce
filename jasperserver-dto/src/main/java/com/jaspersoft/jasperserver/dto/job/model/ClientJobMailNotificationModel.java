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

package com.jaspersoft.jasperserver.dto.job.model;

import com.jaspersoft.jasperserver.dto.job.ClientJobMailNotification;
import com.jaspersoft.jasperserver.dto.job.ClientMailNotificationSendType;
import java.util.List;

/**
 * Email notification model that can be defined for a thumbnail job.  Model is used in search/ update only.
 * <p/>
 * <p>
 * A notification model will result in an email being send inFolder the specified recipients
 * at each job execution.
 * </p>
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 * @since 4.7
 */
public class ClientJobMailNotificationModel extends ClientJobMailNotification {

    private boolean isMessageTextModified = false;
    private boolean isResultSendTypeModified = false;
    private boolean isSubjectModified = false;
    private boolean isBccAddressesModified = false;
    private boolean isCcAddressesModified = false;
    private boolean isToAddressesModified = false;
    private boolean isSkipEmptyReportsModified = false;
    private boolean isMessageTextWhenJobFailsModified = false;
    private boolean isIncludingStackTraceWhenJobFailsModified = false;
    private boolean isSkipNotificationWhenJobFailsModified = false;

    /**
     * Creates an empty job email notification.
     */
    public ClientJobMailNotificationModel() {
        super();
    }

    public ClientJobMailNotificationModel(ClientJobMailNotificationModel other) {
        super(other);
        this.isBccAddressesModified = other.isBccAddressesModified;
        this.isCcAddressesModified = other.isCcAddressesModified;
        this.isIncludingStackTraceWhenJobFailsModified = other.isIncludingStackTraceWhenJobFailsModified;
        this.isMessageTextModified = other.isMessageTextModified;
        this.isMessageTextWhenJobFailsModified = other.isMessageTextWhenJobFailsModified;
        this.isResultSendTypeModified = other.isResultSendTypeModified;
        this.isSkipEmptyReportsModified = other.isSkipEmptyReportsModified;
        this.isSkipNotificationWhenJobFailsModified = other.isSkipNotificationWhenJobFailsModified;
        this.isSubjectModified = other.isSubjectModified;
        this.isToAddressesModified = other.isToAddressesModified;
    }

    public boolean isBccAddressesModified() {
        return isBccAddressesModified;
    }

    public boolean isCcAddressesModified() {
        return isCcAddressesModified;
    }

    public boolean isIncludingStackTraceWhenJobFailsModified() {
        return isIncludingStackTraceWhenJobFailsModified;
    }

    public boolean isMessageTextModified() {
        return isMessageTextModified;
    }

    public boolean isMessageTextWhenJobFailsModified() {
        return isMessageTextWhenJobFailsModified;
    }

    public boolean isResultSendTypeModified() {
        return isResultSendTypeModified;
    }

    public boolean isSkipEmptyReportsModified() {
        return isSkipEmptyReportsModified;
    }

    public boolean isSkipNotificationWhenJobFailsModified() {
        return isSkipNotificationWhenJobFailsModified;
    }

    public boolean isSubjectModified() {
        return isSubjectModified;
    }

    public boolean isToAddressesModified() {
        return isToAddressesModified;
    }

    @Override
    public ClientJobMailNotificationModel setBccAddresses(List<String> bccAddresses) {
        super.setBccAddresses(bccAddresses);
        isBccAddressesModified = true;
        return this;
    }

    @Override
    public ClientJobMailNotificationModel setCcAddresses(List<String> ccAddresses) {
        super.setCcAddresses(ccAddresses);
        isCcAddressesModified = true;
        return this;
    }

    @Override
    public ClientJobMailNotificationModel setIncludingStackTraceWhenJobFails(Boolean includingStackTraceWhenJobFails) {
        super.setIncludingStackTraceWhenJobFails(includingStackTraceWhenJobFails);
        isIncludingStackTraceWhenJobFailsModified = true;
        return this;
    }

    @Override
    public ClientJobMailNotificationModel setMessageText(String messageText) {
        super.setMessageText(messageText);
        isMessageTextModified = true;
        return this;
    }

    @Override
    public ClientJobMailNotificationModel setMessageTextWhenJobFails(String messageTextWhenJobFails) {
        super.setMessageTextWhenJobFails(messageTextWhenJobFails);
        isMessageTextWhenJobFailsModified = true;
        return this;
    }

    @Override
    public ClientJobMailNotificationModel setResultSendType(ClientMailNotificationSendType resultSendType) {
        super.setResultSendType(resultSendType);
        isResultSendTypeModified = true;
        return this;
    }

    @Override
    public ClientJobMailNotificationModel setSkipEmptyReports(Boolean skipEmptyReports) {
        super.setSkipEmptyReports(skipEmptyReports);
        isSkipEmptyReportsModified = true;
        return this;
    }

    @Override
    public ClientJobMailNotificationModel setSkipNotificationWhenJobFails(Boolean skipNotificationWhenJobFails) {
        super.setSkipNotificationWhenJobFails(skipNotificationWhenJobFails);
        isSkipNotificationWhenJobFailsModified = true;
        return this;
    }

    @Override
    public ClientJobMailNotificationModel setSubject(String subject) {
        super.setSubject(subject);
        isSubjectModified = true;
        return this;
    }

    @Override
    public ClientJobMailNotificationModel setToAddresses(List<String> toAddresses) {
        super.setToAddresses(toAddresses);
        isToAddressesModified = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobMailNotificationModel)) return false;
        if (!super.equals(o)) return false;

        ClientJobMailNotificationModel that = (ClientJobMailNotificationModel) o;

        if (isMessageTextModified() != that.isMessageTextModified()) return false;
        if (isResultSendTypeModified() != that.isResultSendTypeModified()) return false;
        if (isSubjectModified() != that.isSubjectModified()) return false;
        if (isBccAddressesModified() != that.isBccAddressesModified()) return false;
        if (isCcAddressesModified() != that.isCcAddressesModified()) return false;
        if (isToAddressesModified() != that.isToAddressesModified()) return false;
        if (isSkipEmptyReportsModified() != that.isSkipEmptyReportsModified()) return false;
        if (isMessageTextWhenJobFailsModified() != that.isMessageTextWhenJobFailsModified()) return false;
        if (isIncludingStackTraceWhenJobFailsModified() != that.isIncludingStackTraceWhenJobFailsModified())
            return false;
        return isSkipNotificationWhenJobFailsModified() == that.isSkipNotificationWhenJobFailsModified();

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isMessageTextModified() ? 1 : 0);
        result = 31 * result + (isResultSendTypeModified() ? 1 : 0);
        result = 31 * result + (isSubjectModified() ? 1 : 0);
        result = 31 * result + (isBccAddressesModified() ? 1 : 0);
        result = 31 * result + (isCcAddressesModified() ? 1 : 0);
        result = 31 * result + (isToAddressesModified() ? 1 : 0);
        result = 31 * result + (isSkipEmptyReportsModified() ? 1 : 0);
        result = 31 * result + (isMessageTextWhenJobFailsModified() ? 1 : 0);
        result = 31 * result + (isIncludingStackTraceWhenJobFailsModified() ? 1 : 0);
        result = 31 * result + (isSkipNotificationWhenJobFailsModified() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientJobMailNotificationModel{" +
                "isBccAddressesModified=" + isBccAddressesModified +
                ", isMessageTextModified=" + isMessageTextModified +
                ", isResultSendTypeModified=" + isResultSendTypeModified +
                ", isSubjectModified=" + isSubjectModified +
                ", isCcAddressesModified=" + isCcAddressesModified +
                ", isToAddressesModified=" + isToAddressesModified +
                ", isSkipEmptyReportsModified=" + isSkipEmptyReportsModified +
                ", isMessageTextWhenJobFailsModified=" + isMessageTextWhenJobFailsModified +
                ", isIncludingStackTraceWhenJobFailsModified=" + isIncludingStackTraceWhenJobFailsModified +
                ", isSkipNotificationWhenJobFailsModified=" + isSkipNotificationWhenJobFailsModified +
                '}';
    }

    @Override
    public ClientJobMailNotificationModel deepClone() {
        return new ClientJobMailNotificationModel(this);
    }
}
