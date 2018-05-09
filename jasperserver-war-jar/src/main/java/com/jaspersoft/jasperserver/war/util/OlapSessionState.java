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

package com.jaspersoft.jasperserver.war.util;

import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.tonbeller.jpivot.olap.model.OlapModel;
import java.io.Serializable;

/**
 * Wrapper for Olap user session info
 * 
 * @author swood
 */

public class OlapSessionState implements Serializable {

    private transient OlapModel olapModel;
    private OlapUnit olapUnit;
    private OlapClientConnection olapConnection;

    public OlapSessionState(OlapModel olapModel, OlapUnit olapUnit, OlapClientConnection olapConnection) {
        this.olapModel = olapModel;
        this.olapUnit = olapUnit;
        this.olapConnection = olapConnection;
    }

    /**
     * @return Returns the olapModel.
     */
    public OlapModel getOlapModel() {
        return olapModel;
    }

    /**
     * @param olapModel The olapModel to set.
     */
    public void setOlapModel(OlapModel olapModel) {
        this.olapModel = olapModel;
    }

    /**
     * @return Returns the olapUnit.
     */
    public OlapUnit getOlapUnit() {
        return olapUnit;
    }

    /**
     * @param olapUnit The olapUnit to set.
     */
    public void setOlapUnit(OlapUnit olapUnit) {
        this.olapUnit = olapUnit;

    }

    public OlapClientConnection getOlapClientConnection() {
        return olapConnection;
    }

    public void setOlapClientConnection(OlapClientConnection olapConnection) {
        this.olapConnection = olapConnection;
    }
}
