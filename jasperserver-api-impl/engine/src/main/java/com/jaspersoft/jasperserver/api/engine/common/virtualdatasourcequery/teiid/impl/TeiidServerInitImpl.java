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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.impl;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid.ServerInit;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.FakeWorkManager;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.SimpleMock;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid.TeiidEmbeddedServer;
import org.teiid.runtime.EmbeddedServer;
import javax.resource.spi.XATerminator;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class TeiidServerInitImpl implements ServerInit {

    public void init(EmbeddedServer embeddedServer) {
        if (embeddedServer instanceof TeiidEmbeddedServer) {
            System.out.println("INIT - TEIID SERVER INIT IMPL");
            TeiidEmbeddedServer teiidEmbeddedServer = (TeiidEmbeddedServer) embeddedServer;
            teiidEmbeddedServer.setXaTerminator(SimpleMock.createSimpleMock(XATerminator.class));
    //        teiidEmbeddedServer.setWorkManager(new FakeWorkManager());
    //        teiidEmbeddedServer.setDetectTransactions(false);
        }
    }

}
