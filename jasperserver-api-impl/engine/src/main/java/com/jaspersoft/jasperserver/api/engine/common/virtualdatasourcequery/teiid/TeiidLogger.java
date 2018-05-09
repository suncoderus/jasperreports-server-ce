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

import groovy.util.ObjectGraphBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.teiid.logging.Logger;
import java.util.HashMap;
import java.util.Map;


public class TeiidLogger implements Logger {

        private Map<String, Integer> contextMap = new HashMap<String, Integer>();
    	private int defaultLevel = 0;
        private static final Log log = LogFactory.getLog(TeiidEmbeddedServer.class);

        public TeiidLogger() {};

        public void log(int level, String context, Object msg) {
            log.debug(context);
            if (msg != null) log.debug(msg.toString());
        //    System.out.println(msg.toString());
        }

		public void log(int level, String context, Throwable t, Object msg) {
            log.debug(context);
            log.debug((msg != null? msg.toString() : null), t);
        //    System.out.println(msg.toString());
		}

        public void log(int i, java.lang.String s, java.lang.Object... objects) {
            log.debug(s);
            if (objects != null) {
                for (Object obj : objects) if (obj != null) log.debug(obj.getClass().toString() +  ":" + obj.toString());
            }
        }

        public void log(int i, java.lang.String s, java.lang.Throwable throwable, java.lang.Object... objects) {
            log.debug(s);
            if (objects != null) {
                for (Object obj : objects) if (obj != null) log.debug(obj.getClass().toString() +  ":" + obj.toString(), throwable);
            }
        }

        public void shutdown() {};

    	public int getLogLevel(String context) {
    		Integer level = this.contextMap.get(context);
    		if (level != null) return level;
    		return defaultLevel;
    	}

    	public void setLogLevel(String context, int logLevel) {
    		this.contextMap.put(context, logLevel);
    	}

    	public boolean isEnabled(String context, int msgLevel) {
    		int level = getLogLevel(context);
    		return level >= msgLevel;
    	}
        public void putMdc(java.lang.String s, java.lang.String s1) {};
        public void removeMdc(java.lang.String s) {};
    }