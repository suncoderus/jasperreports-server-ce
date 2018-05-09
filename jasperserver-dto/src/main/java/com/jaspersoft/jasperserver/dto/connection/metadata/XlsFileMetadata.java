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
package com.jaspersoft.jasperserver.dto.connection.metadata;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@XmlRootElement
public class XlsFileMetadata {
    private List<XlsSheet> sheets;

    public XlsFileMetadata(){
    }

    public XlsFileMetadata(XlsFileMetadata other){
        final List<XlsSheet> xlsSheets = other.getSheets();
        if(xlsSheets != null){
            sheets = new ArrayList<XlsSheet>(other.getSheets().size());
            for(XlsSheet xlsSheet : xlsSheets){
                sheets.add(new XlsSheet(xlsSheet));
        }
    }
    }

    public List<XlsSheet> getSheets() {
        return sheets;
    }

    public XlsFileMetadata setSheets(List<XlsSheet> sheets) {
        this.sheets = sheets;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XlsFileMetadata that = (XlsFileMetadata) o;

        if (sheets != null ? !sheets.equals(that.sheets) : that.sheets != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return sheets != null ? sheets.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "XlsFileMetadata{" +
                "sheets=" + sheets +
                "} " + super.toString();
    }
}
