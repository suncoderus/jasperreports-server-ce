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

package com.jaspersoft.jasperserver.api.metadata.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jasperreports.engine.util.Pair;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class LocalLockManager implements LockManager {
	
	private final static Log log = LogFactory.getLog(LocalLockManager.class);
	// what is the max time that a key can be locked?
	// 0 means timeout disabled
	private int lockTimeoutMs = 10000;
	// how often do we clean stuff up? This is not so critical.
	private long timeoutCheckInterval = 100000;
	// when did we check timeouts last?
	private AtomicLong timeoutLastChecked = new AtomicLong();
	
	private Map<LockKey, Long> locks = new HashMap<LockKey, Long>();

	public LockHandle lock(String lockName, String key) {
		Pair<String, String> pair = new Pair<String, String>(lockName, key);
	
		if (log.isDebugEnabled()) {
			log.debug("Acquiring lock for " + pair);
		}
		
		// wait loop
		LockKey lockKey	= new LockKey(pair);
		synchronized (locks) {
			long now;
			while (true) {
				// do cleanup once in awhile for abandoned locks
				// we test for the specific key below
				removeTimedOutLocks();
				// look in map for key and timestamp 
				now = System.currentTimeMillis();
				Long timestamp = locks.get(lockKey);
				if (timestamp != null && (lockTimeoutMs <= 0 || timestamp >= now - lockTimeoutMs)) {
					// yield if your key is still locked and the lock is not too old, or timeout is disabled
					// use a timeout which is the same timeout as the lock timeout,
					// so if the current lock holder doesn't unlock, you will have another go.
					try {
						locks.wait(lockTimeoutMs);
						continue;
					} catch (InterruptedException e) {
						throw new JSExceptionWrapper(e);
					}
				} else {
					break;
				}
			}
			// add key with current timestamp
			locks.put(lockKey, now);
			
			if (log.isDebugEnabled()) {
				log.debug("Acquired lock for " + lockKey);
			}
		}
		return lockKey;
	}

	/**
	 * Before looking for a key in the set, weed out any that have gotten too old.
	 * We call this every time we look for a particular lock. We don't need to perform this each time,
	 * so we won't check unless the timeoutCheckInterval has passed since we last checked.
	 */
	private void removeTimedOutLocks() {
		// nothing to do if timeout disabled or we've checked recently
		if (lockTimeoutMs <= 0) {
			return;
		}
		if (timeoutLastChecked.get() + timeoutCheckInterval < System.currentTimeMillis()) {
			return;
		}
		timeoutLastChecked.set(System.currentTimeMillis());
		// we should already have the lock but let's just be defensive
		synchronized(locks) {
			long tooOld = System.currentTimeMillis() - lockTimeoutMs;
			boolean locksRemoved = false;
			Iterator<LockKey> lockIter = locks.keySet().iterator(); 
			while (lockIter.hasNext()) {
				LockKey lock = lockIter.next();
				if (locks.get(lock) < tooOld) {
					if (log.isDebugEnabled()) {
						log.debug("Removing timed-out lock for " + lock.getLockKey());
					}
					lockIter.remove();
					locksRemoved = true;
				}
			}
			// notify waiting threads if we removed any locks
			if (locksRemoved) {
				locks.notifyAll();
			}
		}
	}

	public void unlock(LockHandle lock) {
		if (log.isDebugEnabled()) {
			log.debug("Releasing lock for " + lock.getLockKey());
		}
		
		synchronized (locks) {
			locks.remove(lock);
			locks.notifyAll();
		}
	}

	public int getLockTimeoutMs() {
		return lockTimeoutMs;
	}

	public void setLockTimeoutMs(int lockTimeoutMs) {
		this.lockTimeoutMs = lockTimeoutMs;
	}

	public long getTimeoutCheckInterval() {
		return timeoutCheckInterval;
	}

	public void setTimeoutCheckInterval(long timeoutCheckInterval) {
		this.timeoutCheckInterval = timeoutCheckInterval;
	}

}
