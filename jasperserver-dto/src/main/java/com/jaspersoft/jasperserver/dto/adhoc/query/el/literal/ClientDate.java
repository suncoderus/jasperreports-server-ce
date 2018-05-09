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

import com.jaspersoft.jasperserver.dto.adhoc.query.adapter.ELDateAdapter;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.adapters.DomELCommonSimpleDateFormats;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.JavaAlias.DATE;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate.LITERAL_ID;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @author Stas Chubar <schubar@tibco.com>
 * @version $Id $
 */
@XmlRootElement(name = LITERAL_ID)
public class ClientDate extends ClientLiteral<Date, ClientDate> {
    public static final String LITERAL_ID = DATE;

    public static final DateFormat FORMATTER = DomELCommonSimpleDateFormats.dateFormat();

    public ClientDate() {
    }

    public ClientDate(Date date) {
        super(date);
    }


    public ClientDate(ClientDate d) {
        super(d.getValue() != null ? (Date) d.getValue().clone() : null);
    }

    @Override
    public ClientDate deepClone() {
        return new ClientDate(this);
    }

    @Override
    public ClientDate setValue(Date value) {
        this.value = value;
        return this;
    }

    @Override
    @XmlJavaTypeAdapter(ELDateAdapter.class)
    public Date getValue() {
        return value;
    }

    @Override
    public String toString() {
        String valueString;
        Date value = getValue();
        try {
            valueString = (value != null) ? FORMATTER.format(value) : ClientExpressions.MISSING_REPRESENTATION;
        } catch (IllegalArgumentException e) {
            valueString = ClientExpressions.MISSING_REPRESENTATION;
        }
        return "d'" + valueString + "'";
    }

    public ClientDate valueOf(String string){
        try {
            return new ClientDate(DomELCommonSimpleDateFormats.dateFormat().parse(string));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Unable to parse given value for date: " + string);
        }

    }

    @Override
    public void accept(ClientELVisitor visitor) {
        visitor.visit(this);
    }

}
