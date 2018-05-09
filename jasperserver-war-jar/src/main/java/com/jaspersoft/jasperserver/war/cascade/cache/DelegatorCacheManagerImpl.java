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

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * DelegatorCacheManagerImpl
 * @author jwhang
 * @version $Id$
 */

public class DelegatorCacheManagerImpl implements DelegatorCacheManager {

	private long cacheCleanTriggerTime = 300000l; //default to 5 minutes between cache cleanups.
	private long userCacheTimeout = 120000l; //default to 2 minutes before invalidating an individual session.

    public DelegatorCacheManagerImpl(){
    }

    /*
     * Cache properties.
     */
    public long getCacheCleanTriggerTime() {
        //revert to seconds
        return cacheCleanTriggerTime / 1000;
    }

    public void setCacheCleanTriggerTime(long cacheCleanTriggerTime) {
        //convert seconds into millisecond equivalent
        this.cacheCleanTriggerTime = cacheCleanTriggerTime * 1000;
    }

    public long getUserCacheTimeout() {
        // revert to seconds
        return userCacheTimeout / 1000;
    }

    public void setUserCacheTimeout(long userCacheTimeout) {
        // convert seconds into millisecond equivalent
        this.userCacheTimeout = userCacheTimeout * 1000;
    }

    public void initialize(Map<String, SessionCache> sessionCachePool) {
        Timer cascadeCacheCleanerTimer = new Timer("CascadeCacheCleaner",true);
        TimerTask cacheCleanerTimerTask = new CacheCleaner(sessionCachePool, userCacheTimeout);
        cascadeCacheCleanerTimer.scheduleAtFixedRate(cacheCleanerTimerTask, cacheCleanTriggerTime, cacheCleanTriggerTime);
    }

    public SessionCache createSessionCache(String key){
        return new SessionCacheImpl();
    }

}




















