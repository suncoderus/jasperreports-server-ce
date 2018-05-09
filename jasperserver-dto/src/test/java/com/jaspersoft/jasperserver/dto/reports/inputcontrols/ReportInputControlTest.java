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
package com.jaspersoft.jasperserver.dto.reports.inputcontrols;

import com.jaspersoft.jasperserver.dto.resources.ClientDataType;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: ReportInputControlTest.java 58644 2015-10-16 12:19:26Z ykovalch $
 */
public class ReportInputControlTest {

    @Test
    public void setDataType_resetGenericResourceFields(){
        final ClientDataType dataType = new ClientDataType();
        //set specific fields
        dataType.setMaxLength(100).setMaxValue("100").setMinValue("10").setPattern("testPattern").setStrictMax(true)
                .setStrictMin(true).setType(ClientDataType.TypeOfDataType.number);
        // clone data type to verify result
        final ClientDataType dataTypeClone = new ClientDataType(dataType);
        // set generic fields
        dataType.setUri("testUri").setVersion(100)
                .setCreationDate("creationDate").setUpdateDate("updateDate").setPermissionMask(32)
                .setDescription("description").setLabel("label");
        final ClientDataType result = new ReportInputControl().setDataType(dataType).getDataType();
        // verify that generic fields are reset
        assertTrue(dataTypeClone.equals(result));
    }


}
