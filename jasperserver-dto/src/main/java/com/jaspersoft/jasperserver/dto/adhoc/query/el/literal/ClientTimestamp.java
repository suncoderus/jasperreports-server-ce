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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.literal;

import com.jaspersoft.jasperserver.dto.adhoc.query.adapter.ELTimestampAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.adapters.DomELCommonSimpleDateFormats;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.TIMESTAMP;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp.LITERAL_ID;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = LITERAL_ID)
public class ClientTimestamp extends ClientLiteral<Timestamp, ClientTimestamp> {
    public static final String LITERAL_ID = TIMESTAMP;

    public static final DateFormat FORMATTER = DomELCommonSimpleDateFormats.timestampFormat();

    public ClientTimestamp() {
    }

    public ClientTimestamp(Timestamp timestamp) {
        super(timestamp);
    }

    public ClientTimestamp(ClientTimestamp t) {
        super(t.getValue() != null ? (Timestamp) t.getValue().clone() : null);
    }

    @Override
    @XmlJavaTypeAdapter(ELTimestampAdapter.class)
    public Timestamp getValue() {
        return value;
    }

    @Override
    public ClientTimestamp setValue(Timestamp value) {
        this.value = value;
        return this;
    }

    @Override
    public ClientTimestamp deepClone() {
        return new ClientTimestamp(this);
    }

    public static ClientTimestamp valueOf(String string){
        try {
            return new ClientTimestamp().setValue(new Timestamp(FORMATTER.parse(string).getTime()));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Unable to parse given value for timestamp: " + string);
        }
    }

    /**
     * This covers a special case where in the event a timestamp has no suffixed time, it is assumed to be 00:00:00
     *
     * The implication is that were a timestamp to have a specified time "00:00:00" (a moment no different from any
     * other second of a day, in my personal opinion) that this time will be erased from the string representation
     * of the Timestamp.
     *
     * @link com.jaspersoft.commons.dataset.expr.TimestampLiteral#format(boolean)
     */
    @Override
    public String toString() {

        final String resultFormat;
        String formattedValue;
        final Timestamp value = getValue();
        try {
            formattedValue = FORMATTER.format(value);
        } catch (IllegalArgumentException e) {
            formattedValue = ClientExpressions.MISSING_REPRESENTATION;
        }

        if (formattedValue.endsWith("00:00:00")) {
            resultFormat = formattedValue.replaceFirst("\\s(00:?){3}", "");
        } else {
            resultFormat = formattedValue;
        }
        return "ts'" + resultFormat + "'";
    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
    }

}
