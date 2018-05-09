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
package com.jaspersoft.jasperserver.dto.adhoc.query.adapter;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.adapters.DomELCommonSimpleDateFormats;
import org.apache.commons.lang3.time.DateUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static com.jaspersoft.jasperserver.dto.adhoc.DefaultISOFormats.*;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
public class ELTimestampAdapter extends XmlAdapter<String, Timestamp> {

    private ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return DomELCommonSimpleDateFormats.timestampFormat();
        }
    };

    @Override
    public String marshal(Timestamp v) throws Exception {
        return formatter.get().format(v);
    }

    @Override
    public Timestamp unmarshal(String v) throws Exception {
        return new Timestamp(DateUtils.parseDate(v, SIMPLE_TIMESTAMP_WITHOUT_TIMEZONE, TIMESTAMP_WITHOUT_MILLISECONDS_PATTERN).getTime());
    }

}