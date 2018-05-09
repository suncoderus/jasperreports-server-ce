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

package com.jaspersoft.jasperserver.dto.job;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import com.jaspersoft.jasperserver.dto.common.OutputFormat;
import com.jaspersoft.jasperserver.dto.job.adapters.OutputFormatXmlAdapter;
import com.jaspersoft.jasperserver.dto.job.adapters.TimestampToStringXmlAdapter;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
@XmlRootElement(name = "job")
public class ClientReportJob implements DeepCloneable<ClientReportJob>{

    private Long id;
    private Integer version;
    private String username;
    private String label;
    private String description;
    private Timestamp creationDate;
    private ClientJobTrigger trigger;
    private ClientJobSource source;
    private String baseOutputFilename;
    private Set<OutputFormat> outputFormats;
    private String outputLocale;
    private String outputTimeZone;
    private ClientJobRepositoryDestination repositoryDestination;
    private ClientJobMailNotification mailNotification;
    private ClientJobAlert alert;

    public ClientReportJob() {
    }

    public ClientReportJob(ClientReportJob other) {
        this.alert = (other.alert != null) ? new ClientJobAlert(other.alert) : null;
        this.baseOutputFilename = other.baseOutputFilename;
        this.description = other.description;
        this.id = other.id;
        this.label = other.label;
        this.mailNotification = (other.mailNotification != null) ? new ClientJobMailNotification(other.mailNotification) : null;
        if (other.outputFormats != null) {
            this.outputFormats = new HashSet<OutputFormat>();
            for (OutputFormat outputFormat : other.outputFormats) {
                this.outputFormats.add(outputFormat);
            }
        }
        this.outputLocale = other.outputLocale;
        this.outputTimeZone = other.outputTimeZone;
        this.repositoryDestination = (other.repositoryDestination != null) ?
                new ClientJobRepositoryDestination(other.repositoryDestination) : null;
        this.source = (other.source != null) ? new ClientJobSource(other.source) : null;
        this.username = other.username;
        this.version = other.version;
        this.creationDate = (other.creationDate != null) ? new Timestamp(other.creationDate.getTime()) : null;
        this.trigger = (other.trigger != null) ? other.trigger.deepClone() : null;
    }

    public Long getId() {
        return id;
    }

    public ClientReportJob setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public ClientReportJob setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ClientReportJob setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public ClientReportJob setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ClientReportJob setDescription(String description) {
        this.description = description;
        return this;
    }

    @XmlJavaTypeAdapter(TimestampToStringXmlAdapter.class)
    public Timestamp getCreationDate() {
        return creationDate;
    }

    public ClientReportJob setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public String getBaseOutputFilename() {
        return baseOutputFilename;
    }

    public ClientReportJob setBaseOutputFilename(String baseOutputFilename) {
        this.baseOutputFilename = baseOutputFilename;
        return this;
    }

    public String getOutputLocale() {
        return outputLocale;
    }

    public ClientReportJob setOutputLocale(String outputLocale) {
        this.outputLocale = outputLocale;
        return this;
    }

    @XmlElement(name = "repositoryDestination")
    public ClientJobRepositoryDestination getRepositoryDestination() {
        return repositoryDestination;
    }

    public ClientReportJob setRepositoryDestination(ClientJobRepositoryDestination repositoryDestination) {
        this.repositoryDestination = repositoryDestination;
        return this;
    }

    @XmlElement(name = "mailNotification")
    public ClientJobMailNotification getMailNotification() {
        return mailNotification;
    }

    public ClientReportJob setMailNotification(ClientJobMailNotification mailNotification) {
        this.mailNotification = mailNotification;
        return this;
    }

    @XmlElement(name = "source")
    public ClientJobSource getSource() {
        return source;
    }

    public ClientReportJob setSource(ClientJobSource source) {
        this.source = source;
        return this;
    }

    @XmlElement(name = "alert")
    public ClientJobAlert getAlert() {
        return alert;
    }

    public ClientReportJob setAlert(ClientJobAlert alert) {
        this.alert = alert;
        return this;
    }

    @XmlElements({
            @XmlElement(name = "simpleTrigger", type = ClientJobSimpleTrigger.class),
            @XmlElement(name = "calendarTrigger", type = ClientJobCalendarTrigger.class)})
    public ClientJobTrigger getTrigger() {
        return trigger;
    }

    public ClientReportJob setTrigger(ClientJobTrigger trigger) {
        this.trigger = trigger;
        return this;
    }

    @XmlElement(name = "outputFormats")
    @XmlJavaTypeAdapter(OutputFormatXmlAdapter.class)
    public Set<OutputFormat> getOutputFormats() {
        return outputFormats;
    }

    public ClientReportJob setOutputFormats(Set<OutputFormat> outputFormats) {
        this.outputFormats = outputFormats;
        return this;
    }

    public String getOutputTimeZone() {
        return outputTimeZone;
    }

    public ClientReportJob setOutputTimeZone(String outputTimeZone) {
        this.outputTimeZone = outputTimeZone;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientReportJob job = (ClientReportJob) o;

        if (alert != null ? !alert.equals(job.alert) : job.alert != null) return false;
        if (baseOutputFilename != null ? !baseOutputFilename.equals(job.baseOutputFilename) : job.baseOutputFilename != null)
            return false;
        if (creationDate != null ? !creationDate.equals(job.creationDate) : job.creationDate != null) return false;
        if (description != null ? !description.equals(job.description) : job.description != null) return false;
        if (id != null ? !id.equals(job.id) : job.id != null) return false;
        if (label != null ? !label.equals(job.label) : job.label != null) return false;
        if (mailNotification != null ? !mailNotification.equals(job.mailNotification) : job.mailNotification != null)
            return false;
        if (outputFormats != null ? !outputFormats.equals(job.outputFormats) : job.outputFormats != null) return false;
        if (outputLocale != null ? !outputLocale.equals(job.outputLocale) : job.outputLocale != null) return false;
        if (repositoryDestination != null ? !repositoryDestination.equals(job.repositoryDestination) : job.repositoryDestination != null)
            return false;
        if (source != null ? !source.equals(job.source) : job.source != null) return false;
        if (trigger != null ? !trigger.equals(job.trigger) : job.trigger != null) return false;
        if (username != null ? !username.equals(job.username) : job.username != null) return false;
        if (version != null ? !version.equals(job.version) : job.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (baseOutputFilename != null ? baseOutputFilename.hashCode() : 0);
        result = 31 * result + (outputLocale != null ? outputLocale.hashCode() : 0);
        result = 31 * result + (repositoryDestination != null ? repositoryDestination.hashCode() : 0);
        result = 31 * result + (mailNotification != null ? mailNotification.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (alert != null ? alert.hashCode() : 0);
        result = 31 * result + (outputFormats != null ? outputFormats.hashCode() : 0);
        result = 31 * result + (trigger != null ? trigger.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", version=" + version +
                ", username='" + username + '\'' +
                ", label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", baseOutputFilename='" + baseOutputFilename + '\'' +
                ", outputLocale='" + outputLocale + '\'' +
                ", repositoryDestination=" + repositoryDestination +
                ", mailNotification=" + mailNotification +
                ", source=" + source +
                ", alert=" + alert +
                ", outputFormats=" + outputFormats +
                ", trigger=" + trigger +
                '}';
    }

    @Override
    public ClientReportJob deepClone() {
        return new ClientReportJob(this);
    }
}
