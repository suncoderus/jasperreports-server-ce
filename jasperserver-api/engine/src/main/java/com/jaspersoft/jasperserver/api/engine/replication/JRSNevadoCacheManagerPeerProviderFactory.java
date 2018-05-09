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

package com.jaspersoft.jasperserver.api.engine.replication;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
import net.sf.ehcache.distribution.CacheManagerPeerProviderFactory;
import net.sf.ehcache.distribution.jms.AcknowledgementMode;
import net.sf.ehcache.distribution.jms.JMSCacheManagerPeerProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.skyscreamer.nevado.jms.NevadoConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jms.*;
import java.util.Properties;

import static com.jaspersoft.jasperserver.api.engine.replication.JRSActiveMQInitialContextFactory.CACHE_REPLICATION_SETTINGS_XML_FILE;

public class JRSNevadoCacheManagerPeerProviderFactory extends CacheManagerPeerProviderFactory {
	static final Log log = LogFactory.getLog(JRSNevadoCacheManagerPeerProviderFactory.class);

	private static synchronized JMSCacheManagerPeerProvider getProvider(CacheManager cacheManager, Properties props){
		JMSCacheManagerPeerProvider provider = null;
		try {
			
			ApplicationContext context = new ClassPathXmlApplicationContext(CACHE_REPLICATION_SETTINGS_XML_FILE);
			NevadoConnectionFactory nevadoConnectionFactory = (NevadoConnectionFactory)context.getBean("connectionFactory");
			nevadoConnectionFactory.setOverrideJMSTTL(new Long(0));

//			SQSConnectionFactory connectionFactory =
//				    SQSConnectionFactory.builder()
//				        .withRegion(Region.getRegion(Regions.US_EAST_1))
//				        .withAWSCredentialsProvider(new ClasspathPropertiesFileCredentialsProvider("aws.properties"))
//				        .build();
//				 
//			// Create the connection.
//			SQSConnection connection = connectionFactory.createConnection();
//			
//			// Get the wrapped client
//			
//			
//			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//			TopicConnection topicConnection = nevadoConnectionFactory.createTopicConnection(nevadoConnectionFactory.getAwsAccessKey(), nevadoConnectionFactory.getAwsSecretKey());
//			Topic nevadoTopic = topicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE).createTopic(nevadoConnectionFactory.getEhcacheTopic().getName());
////			Topic amazonTopic = session.createTopic(nevadoConnectionFactory.getEhcacheTopic().getName());
//			Queue amazonQueue = session.createQueue(nevadoConnectionFactory.getEhcacheQueue().getName());
//			
//			log.debug("NEVADO: JRSNevadoCacheManagerPeerProviderFactory: creating Provider *********************");
//            provider = new JMSCacheManagerPeerProvider(cacheManager,
//				topicConnection,
//				nevadoTopic,
//				connection,
//				amazonQueue,
//				AcknowledgementMode.AUTO_ACKNOWLEDGE,
//				true);

            TopicConnection topicConnection = nevadoConnectionFactory.createTopicConnection(nevadoConnectionFactory.getAwsAccessKey(), nevadoConnectionFactory.getAwsSecretKey());
			QueueConnection queueConnection = nevadoConnectionFactory.createQueueConnection(nevadoConnectionFactory.getAwsAccessKey(), nevadoConnectionFactory.getAwsSecretKey());
			Topic nevadoTopic = topicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE).createTopic(nevadoConnectionFactory.getEhcacheTopic().getName());
			Queue nevadoQueue = queueConnection.createSession(false,  Session.AUTO_ACKNOWLEDGE).createQueue(nevadoConnectionFactory.getEhcacheQueue().getName());
			log.debug("NEVADO: JRSNevadoCacheManagerPeerProviderFactory: creating Provider *********************");
            provider = new JMSCacheManagerPeerProvider(cacheManager,
				topicConnection,
				nevadoTopic,
				queueConnection,
				nevadoQueue,
				AcknowledgementMode.AUTO_ACKNOWLEDGE,
				true);
            
	    } catch(Exception e) {
			log.error("NEVADO: JRSNevadoCacheManagerPeerProviderFactory returned error", e);
        } finally {
			log.debug("NEVADO: JRSNevadoCacheManagerPeerProviderFactory: done creating provider *********************");
        }
		return provider;
	}
	
	@Override
    public CacheManagerPeerProvider createCachePeerProvider(CacheManager cacheManager, Properties props) {
		return getProvider(cacheManager, props);
	} 
}
