/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package example.cds;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author bob
 * 
 */
public class HibernateSessionFactoryFinder implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	private SessionFactory defaultSessionFactory;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	// Create a Hibernate session object from a named Spring bean which can either be
	// a SessionFactory or HibernateDaoSupport.
	// Hibernate has various ways of creating session factories.
	// JasperServer uses a Spring wrapper that does all kinds of handy things which the 
	// HibernateQueryExecuter doesn't really care about, so we need to extract a session from
	// that somehow. I'm being paranoid in creating sessions--I always create a new session,
	// and set it to FlushMode.NEVER so it doesn't interfere w/ JS use of the session factory.
	
	// Jun's example has a factory which needs no configuration but looks up hbm.xml files in the classpath.
	// This is invoked when there's no bean name.
	public Session getSession(String name) {
		// run code found in Jun's example if no name specified
		if (name == null || name.length() == 0) {
			return getDefaultSessionFactory().getCurrentSession();
		}
		Object sessionManager = applicationContext.getBean(name);
		if (sessionManager == null) {
			throw new IllegalArgumentException("no spring bean found with the name '" + name + "'");
		}
		if (sessionManager instanceof SessionFactory) {
			Session session = ((SessionFactory) sessionManager).openSession();
			session.setFlushMode(FlushMode.NEVER);
			return session;
		} else if (sessionManager instanceof HibernateDaoSupport) {
			HibernateDaoSupport daoSupport = (HibernateDaoSupport) sessionManager;
			Session session = daoSupport.getSessionFactory().openSession();
			session.setFlushMode(FlushMode.NEVER);
			return session;
		} else {
			throw new IllegalArgumentException("spring bean with name '" + name + "' has bogus type '" + sessionManager.getClass().getName() + "'");
		}
	}
	
	public SessionFactory getDefaultSessionFactory() {
		if (defaultSessionFactory == null) {
			try {
				// Create the SessionFactory from hibernate.cfg.xml
				defaultSessionFactory = new Configuration().configure().buildSessionFactory();
			} catch (Throwable ex) {
				System.err.println("Initial SessionFactory creation failed." + ex);
			}
		}
		return defaultSessionFactory;
	}
}
