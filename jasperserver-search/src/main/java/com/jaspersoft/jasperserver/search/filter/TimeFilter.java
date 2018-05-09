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

package com.jaspersoft.jasperserver.search.filter;

import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class TimeFilter extends BaseSearchFilter implements Serializable {

    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
        SearchAttributes searchAttributes = getSearchAttributes(context);

        if (searchAttributes != null && searchAttributes.getState() != null) {
            String timeFilter = searchAttributes.getState().getCustomFiltersMap().get("timeFilter");
            if (timeFilter != null && !timeFilter.equals("timeFilter-anyTime")) {
                Calendar cal = Calendar.getInstance();

                if (timeFilter.equals("timeFilter-today")) {
                    cal.setTime(new Date());
                    resetDateTime(cal);
                    Date startDate = cal.getTime();

                    criteria.add(Restrictions.gt("updateDate", startDate));
                } else if (timeFilter.equals("timeFilter-yesterday")) {
                    cal.setTime(new Date());
                    resetDateTime(cal);
                    Date endDate = cal.getTime();

                    cal.setTime(rollDay(new Date(), -1));
                    resetDateTime(cal);
                    Date startDate = cal.getTime();

                    criteria.add(Restrictions.between("updateDate", startDate, endDate));
                } else if (timeFilter.equals("timeFilter-pastWeek")) {
                    cal.setTime(new Date());
                    cal.setTime(rollDay(new Date(), -7));
                    resetDateTime(cal);
                    Date startDate = cal.getTime();

                    criteria.add(Restrictions.gt("updateDate", startDate));
                } else if (timeFilter.equals("timeFilter-pastMonth")) {
                    cal.setTime(new Date());
                    cal.setTime(rollDay(new Date(), -30));
                    resetDateTime(cal);
                    Date startDate = cal.getTime();

                    criteria.add(Restrictions.gt("updateDate", startDate));
                }
            }
        }
    }

    private void resetDateTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private Date rollDay(Date date, long amount) {
        long millisecondsInDay = 24 * 60 * 60 * 1000;
        long rolledTime = date.getTime() + amount * millisecondsInDay;
        return new Date(rolledTime);
    }
}
