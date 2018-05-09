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

import net.sf.jasperreports.engine.JRParameter;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author schubar
 * @author Anton Fomin
 * @version $Id$
 */
public class ReportJobTest extends UnitilsJUnit4 {


    @TestedObject
    ReportJob reportJob;

    @InjectInto(property = "source")
    ReportJobSource reportJobSource;

    Map<String, Object> paramValues;

    @Before
    public void setUp() {
        reportJob = new ReportJob();
        reportJobSource = new ReportJobSource();
    }

    @Before
    public void fixtures() {
        this.paramValues = new HashMap<String, Object>();

        this.paramValues.put("test1", "value1");
        this.paramValues.put("test2", 2);
    }

    @Test
    public void shouldSetReportParameters() {
        this.reportJob.setReportParameters(this.paramValues);
        assertEquals(2, this.reportJob.getSource().getParameters().size());
    }

    @Test
    public void shouldSetReportTimeZone() {
        this.reportJob.setOutputTimeZone("America/Chicago");

        assertEquals(TimeZone.getTimeZone("America/Chicago"),
                this.reportJob.getSource().getParameters().get(JRParameter.REPORT_TIME_ZONE));
    }

    @Test
    public void shouldReturnNullIfTimeZoneNotSet() {
        assertNull(this.reportJob.getOutputTimeZone());
    }

    @Test
    public void shouldReturnTimeZoneIDIfSet() {
        this.reportJob.setOutputTimeZone("America/Chicago");
        assertEquals("America/Chicago", this.reportJob.getOutputTimeZone());
    }
}
