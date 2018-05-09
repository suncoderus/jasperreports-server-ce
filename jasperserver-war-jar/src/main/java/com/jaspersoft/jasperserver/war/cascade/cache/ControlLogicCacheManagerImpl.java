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
package com.jaspersoft.jasperserver.war.cascade.cache;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;

/**
 * ControlLogicCacheManagerImpl
 * @author jwhang
 * @version $Id: ControlLogicCacheManagerImpl.java 22557 2012-03-15 10:44:12Z ykovalchyk $
 */

public class ControlLogicCacheManagerImpl implements ControlLogicCacheManager, InitializingBean {

	private long cacheCleanTriggerTime = 300000l; //default to 5 minutes between cache cleanups.
	private long userCacheTimeout = 120000l; //default to 2 minutes before invalidating an individual session.

    private volatile Map<String, SessionCache> sessionCachePool = Collections.synchronizedMap(new HashMap<String, SessionCache>());

    public ControlLogicCacheManagerImpl(){
    }

    public void afterPropertiesSet() throws Exception {
        Timer cascadeCacheCleanerTimer = new Timer("CascadeCacheCleaner",true);
        TimerTask cacheCleanerTimerTask = new CacheCleaner(sessionCachePool, userCacheTimeout);
        cascadeCacheCleanerTimer.scheduleAtFixedRate(cacheCleanerTimerTask, cacheCleanTriggerTime, cacheCleanTriggerTime);
    }


    public void setCacheCleanTriggerTime(long cacheCleanTriggerTime) {
        //convert seconds into millisecond equivalent
        this.cacheCleanTriggerTime = cacheCleanTriggerTime * 1000;
    }

    public void setUserCacheTimeout(long userCacheTimeout) {
        // convert seconds into millisecond equivalent
        this.userCacheTimeout = userCacheTimeout * 1000;
    }

    public void clearCache(){
        getSessionCache().clear();
    }

    // field is volatile
    @SuppressWarnings("SynchronizeOnNonFinalField")
    public SessionCache getSessionCache() {
        String key = getSessionCacheKey();
        SessionCache cache = sessionCachePool.get(key);
        if (cache == null) {
            synchronized (sessionCachePool) {
                cache = sessionCachePool.get(key);
                if (cache == null) {
                    //create a new cache.
                    cache = new SessionCacheImpl();
                    sessionCachePool.put(key, cache);
                }
            }
        }
        return cache;
    }

    public String getSessionCacheKey() {
        String key;
        try {
            key = RequestContextHolder.currentRequestAttributes().getSessionId();
        } catch (IllegalStateException e) {
            // For case when code is invoked on some internal event without real HTTP request.
            key = Long.toString(Thread.currentThread().getId());
        }
    	return key;
    }
}




















