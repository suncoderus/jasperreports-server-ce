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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util;

import java.util.Date;

/**
 * <p>Indicates if update of creationDate and updatedDate is required.</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 * @since 4.7.0
 */
public class UpdateDatesIndicator {
    private static final ThreadLocal<Date> operationalDate = new ThreadLocal<Date>();
    private static final ThreadLocal<Boolean> useOperationalForUpdateDate = new ThreadLocal<Boolean>();

    /**
     * Initialize the indicator with date to be used.
     *
     * @param operationalDate the operational date.
     * @param useOperationalForUpdateDate indicates if we should use operational date for updateDate.
     */
    public static void required(Date operationalDate, boolean useOperationalForUpdateDate) {
        UpdateDatesIndicator.operationalDate.set(operationalDate);
        UpdateDatesIndicator.useOperationalForUpdateDate.set(useOperationalForUpdateDate);
    }

    /**
     * Cleans the indicator.
     */
    public static void clean() {
        operationalDate.set(null);
        useOperationalForUpdateDate.set(null);
    }

    /**
     * Returns <code>true</code> if creationDate and/or updateDate should be updated, <code>false</code> otherwise.
     *
     * @return <code>true</code> if creationDate and/or updateDate should be updated, <code>false</code> otherwise.
     */
    public static boolean shouldUpdate() {
        return operationalDate.get() != null;
    }

    /**
     * Returns operational date.
     *
     * @return operational date.
     */
    public static Date getOperationalDate() {
        return operationalDate.get();
    }

    /**
     * Returns <code>true</code> if we should use operational date for updateDate, <code>false</code> otherwise.
     *
     * @return <code>true</code> if we should use operational date for updateDate, <code>false</code> otherwise.
     */
    public static boolean useOperationalForUpdateDate() {
        return useOperationalForUpdateDate.get();
    }
}
