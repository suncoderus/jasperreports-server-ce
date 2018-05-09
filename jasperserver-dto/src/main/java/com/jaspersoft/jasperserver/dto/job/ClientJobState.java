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
import com.jaspersoft.jasperserver.dto.job.adapters.DateToStringXmlAdapter;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
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
@XmlRootElement(name = "state")
public class ClientJobState implements DeepCloneable<ClientJobState>{

    private Date previousFireTime;
    private Date nextFireTime;
    private ClientJobStateType state;

    public ClientJobState() {
    }

    public ClientJobState(ClientJobState other) {
        this.nextFireTime = (other.nextFireTime != null) ? new Date(other.nextFireTime.getTime()) : null;
        this.previousFireTime = (other.previousFireTime != null) ? new Date(other.previousFireTime.getTime()) : null;
        this.state = other.state;
    }

    @XmlJavaTypeAdapter(DateToStringXmlAdapter.class)
    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public ClientJobState setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
        return this;
    }

    @XmlJavaTypeAdapter(DateToStringXmlAdapter.class)
    public Date getNextFireTime() {
        return nextFireTime;
    }

    public ClientJobState setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
        return this;
    }
    @XmlElement(name = "value")
    public ClientJobStateType getState() {
        return state;
    }

    public ClientJobState setState(ClientJobStateType state) {
        this.state = state;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobState)) return false;

        ClientJobState that = (ClientJobState) o;

        if (getPreviousFireTime() != null ? !getPreviousFireTime().equals(that.getPreviousFireTime()) : that.getPreviousFireTime() != null)
            return false;
        if (getNextFireTime() != null ? !getNextFireTime().equals(that.getNextFireTime()) : that.getNextFireTime() != null)
            return false;
        return getState() == that.getState();

    }

    @Override
    public int hashCode() {
        int result = getPreviousFireTime() != null ? getPreviousFireTime().hashCode() : 0;
        result = 31 * result + (getNextFireTime() != null ? getNextFireTime().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientJobState{" +
                "nextFireTime=" + nextFireTime +
                ", previousFireTime=" + previousFireTime +
                ", state=" + state +
                '}';
    }

    @Override
    public ClientJobState deepClone() {
        return new ClientJobState(this);
    }
}
