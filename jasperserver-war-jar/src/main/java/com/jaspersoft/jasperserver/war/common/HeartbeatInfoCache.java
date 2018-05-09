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
package com.jaspersoft.jasperserver.war.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: HibernateLoggingService.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HeartbeatInfoCache implements HeartbeatContributor, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map infoCache = new HashMap();
	

	public void update(HeartbeatInfo info)
	{
		HeartbeatInfo oldInfo = (HeartbeatInfo)infoCache.get(info);
		info = oldInfo == null ? info : oldInfo;
		info.incrementCount();
		infoCache.put(info, info);
	}
	
	public void contributeToHttpCall(HeartbeatCall call)
	{
		for(Iterator it = infoCache.keySet().iterator(); it.hasNext();)
		{
			HeartbeatInfo info = (HeartbeatInfo)it.next();
			
			info.contributeToHttpCall(call);
		}
	}
	
	public int size()
	{
		return infoCache.size();
	}
	
}
