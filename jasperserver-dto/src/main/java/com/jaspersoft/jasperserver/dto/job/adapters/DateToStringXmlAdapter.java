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
package com.jaspersoft.jasperserver.dto.job.adapters;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class DateToStringXmlAdapter extends XmlAdapter<String, Date>{
    @Override
    public Date unmarshal(String v) throws Exception {
        return DatatypeConverter.parseDateTime(v).getTime();
    }

    @Override
    public String marshal(Date v) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(v);
        return DatatypeConverter.printDateTime(calendar);
    }
}
