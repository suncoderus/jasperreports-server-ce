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
package com.jaspersoft.jasperserver.api.engine.scheduling.security;

import org.springframework.security.acls.model.ObjectIdentity;

import java.io.Serializable;

/**
 * @author Oleg Gavavka
 *         02.10.2014.
 */
public class ReportJobObjectIdentity implements ObjectIdentity {
    private final long jobId;
    public ReportJobObjectIdentity(long jobId) {
        this.jobId = jobId;
    }

    private long getJobId() {
        return jobId;
    }
    @Override
    public Serializable getIdentifier() {
        return getJobId();
    }

    @Override
    public String getType() {
        return long.class.getName();
    }
}
