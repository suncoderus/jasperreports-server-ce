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

import com.jaspersoft.jasperserver.dto.job.ClientJobAlert;
import com.jaspersoft.jasperserver.dto.job.ClientJobAlertRecipient;
import com.jaspersoft.jasperserver.dto.job.ClientJobAlertState;
import java.util.List;

/**
 * job execution alert model that can be defined for a thumbnail job.
 * <p/>
 * <p>
 * A notification will result in an email alert being send inFolder the specified recipients
 * at each job execution (including success and fail).
 * </p>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 */
public class ClientJobAlertModel extends ClientJobAlert {

    private boolean isRecipientModified = false;
    private boolean isToAddressesModified = false;
    private boolean isJobStateModified = false;
    private boolean isMessageTextModified = false;
    private boolean isMessageTextWhenJobFailsModified = false;
    private boolean isSubjectModified = false;
    private boolean isIncludingStackTraceModified = false;
    private boolean isIncludingReportJobInfoModified = false;

    public ClientJobAlertModel() {
        super();
    }

    public ClientJobAlertModel(ClientJobAlertModel other) {
        super(other);
        this.isIncludingReportJobInfoModified = other.isIncludingReportJobInfoModified;
        this.isIncludingStackTraceModified = other.isIncludingStackTraceModified;
        this.isJobStateModified = other.isJobStateModified;
        this.isMessageTextModified = other.isMessageTextModified;
        this.isMessageTextWhenJobFailsModified = other.isMessageTextWhenJobFailsModified;
        this.isRecipientModified = other.isRecipientModified;
        this.isSubjectModified = other.isSubjectModified;
        this.isToAddressesModified = other.isToAddressesModified;
    }

    @Override
    public ClientJobAlertModel setRecipient(ClientJobAlertRecipient recipient) {
        super.setRecipient(recipient);
        isRecipientModified = true;
        return this;
    }

    @Override
    public ClientJobAlertModel setToAddresses(List<String> toAddresses) {
        super.setToAddresses(toAddresses);
        isToAddressesModified = true;
        return this;
    }

    @Override
    public ClientJobAlertModel setJobState(ClientJobAlertState jobState) {
        super.setJobState(jobState);
        isJobStateModified = true;
        return this;
    }

    @Override
    public ClientJobAlertModel setMessageText(String messageText) {
         super.setMessageText(messageText);
        isMessageTextModified = true;
        return this;
    }

    @Override
    public ClientJobAlertModel setMessageTextWhenJobFails(String messageTextWhenJobFails) {
         super.setMessageTextWhenJobFails(messageTextWhenJobFails);
        isMessageTextWhenJobFailsModified = true;
        return this;
    }

    @Override
    public ClientJobAlertModel setSubject(String subject) {
         super.setSubject(subject);
        isSubjectModified = true;
        return this;
    }

    @Override
    public ClientJobAlertModel setIncludingStackTrace(Boolean includingStackTrace) {
         super.setIncludingStackTrace(includingStackTrace);
        isIncludingReportJobInfoModified = true;
        return this;
    }

    @Override
    public ClientJobAlertModel setIncludingReportJobInfo(Boolean includingReportJobInfo) {
         super.setIncludingReportJobInfo(includingReportJobInfo);
        isIncludingReportJobInfoModified = true;
        return this;
    }

    public boolean isIncludingReportJobInfoModified() {
        return isIncludingReportJobInfoModified;
    }

    public boolean isIncludingStackTraceModified() {
        return isIncludingStackTraceModified;
    }

    public boolean isJobStateModified() {
        return isJobStateModified;
    }

    public boolean isMessageTextModified() {
        return isMessageTextModified;
    }

    public boolean isMessageTextWhenJobFailsModified() {
        return isMessageTextWhenJobFailsModified;
    }

    public boolean isRecipientModified() {
        return isRecipientModified;
    }

    public boolean isSubjectModified() {
        return isSubjectModified;
    }

    public boolean isToAddressesModified() {
        return isToAddressesModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobAlertModel)) return false;
        if (!super.equals(o)) return false;

        ClientJobAlertModel that = (ClientJobAlertModel) o;

        if (isRecipientModified() != that.isRecipientModified()) return false;
        if (isToAddressesModified() != that.isToAddressesModified()) return false;
        if (isJobStateModified() != that.isJobStateModified()) return false;
        if (isMessageTextModified() != that.isMessageTextModified()) return false;
        if (isMessageTextWhenJobFailsModified() != that.isMessageTextWhenJobFailsModified()) return false;
        if (isSubjectModified() != that.isSubjectModified()) return false;
        if (isIncludingStackTraceModified() != that.isIncludingStackTraceModified()) return false;
        return isIncludingReportJobInfoModified() == that.isIncludingReportJobInfoModified();

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isRecipientModified() ? 1 : 0);
        result = 31 * result + (isToAddressesModified() ? 1 : 0);
        result = 31 * result + (isJobStateModified() ? 1 : 0);
        result = 31 * result + (isMessageTextModified() ? 1 : 0);
        result = 31 * result + (isMessageTextWhenJobFailsModified() ? 1 : 0);
        result = 31 * result + (isSubjectModified() ? 1 : 0);
        result = 31 * result + (isIncludingStackTraceModified() ? 1 : 0);
        result = 31 * result + (isIncludingReportJobInfoModified() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientJobAlertModel{" +
                "isIncludingReportJobInfoModified=" + isIncludingReportJobInfoModified +
                ", isRecipientModified=" + isRecipientModified +
                ", isToAddressesModified=" + isToAddressesModified +
                ", isJobStateModified=" + isJobStateModified +
                ", isMessageTextModified=" + isMessageTextModified +
                ", isMessageTextWhenJobFailsModified=" + isMessageTextWhenJobFailsModified +
                ", isSubjectModified=" + isSubjectModified +
                ", isIncludingStackTraceModified=" + isIncludingStackTraceModified +
                '}';
    }

    @Override
    public ClientJobAlertModel deepClone() {
        return new ClientJobAlertModel(this);
    }
}
