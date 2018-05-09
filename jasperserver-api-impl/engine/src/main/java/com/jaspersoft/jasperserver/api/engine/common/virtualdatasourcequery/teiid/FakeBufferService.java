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

package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid;

import org.teiid.common.buffer.BufferManager;
import org.teiid.common.buffer.TupleBufferCache;
import org.teiid.dqp.service.BufferService;

public class FakeBufferService implements BufferService {

    private BufferManager bufferMgr;
    private TupleBufferCache tupleBufferCache;
    
    public FakeBufferService() {
    	this(true);
    }
    
    public FakeBufferService(boolean shared) {
    	if (shared) {
    		bufferMgr = BufferManagerFactory.getStandaloneBufferManager();
    	} else {
    		bufferMgr = BufferManagerFactory.createBufferManager();
    	}
    	this.tupleBufferCache = bufferMgr;
    }
    
    public FakeBufferService(BufferManager buffManager, TupleBufferCache tupleBufferCache) {
    	this.bufferMgr = buffManager;
    	this.tupleBufferCache = tupleBufferCache;
    }

    public BufferManager getBufferManager() {
        return bufferMgr;
    }

    public TupleBufferCache getTupleBufferCache() {
    	return tupleBufferCache;
    }

}
