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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb;

import com.jaspersoft.jasperserver.api.common.domain.jaxb.AbstractEnumXmlAdapter;
import com.jaspersoft.jasperserver.api.common.domain.jaxb.NamedPropertyHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ReportJobTriggerIntervalUnitXmlAdapter extends AbstractEnumXmlAdapter<Byte> {
    @Override
    protected NamedPropertyHolder<Byte>[] getEnumConstantsArray() {
        return IntervalUnit.values();
    }

    public enum IntervalUnit implements NamedPropertyHolder<Byte> {
        MINUTE(ReportJobSimpleTrigger.INTERVAL_MINUTE),
        HOUR(ReportJobSimpleTrigger.INTERVAL_HOUR),
        DAY(ReportJobSimpleTrigger.INTERVAL_DAY),
        WEEK(ReportJobSimpleTrigger.INTERVAL_WEEK);

        private final Byte byteValue;

        private IntervalUnit(Byte byteValue) {
            this.byteValue = byteValue;
        }
         public Byte getProperty(){
             return this.byteValue;
         }
    }




}
