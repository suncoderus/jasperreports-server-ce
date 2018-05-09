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

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class NoTimezoneDateToStringXmlAdapter extends XmlAdapter<String, Date>{

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

    @Override
    public Date unmarshal(String v) throws Exception {
        return new SimpleDateFormat(DATE_TIME_PATTERN).parse(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
        return new SimpleDateFormat(DATE_TIME_PATTERN).format(v);
    }
}
