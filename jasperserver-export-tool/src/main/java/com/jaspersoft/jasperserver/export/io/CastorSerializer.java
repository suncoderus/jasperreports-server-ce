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

package com.jaspersoft.jasperserver.export.io;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CastorSerializer implements ObjectSerializer, InitializingBean {

	private static final Log log = LogFactory.getLog(CastorSerializer.class);
	
	private Resource[] castorMappings;
	
	private Mapping castorMapping;

	public Resource[] getCastorMappings() {
		return castorMappings;
	}

	public void setCastorMappings(Resource[] castorMappings) {
		this.castorMappings = castorMappings;
	}

	public void afterPropertiesSet() throws Exception {
		createCastorMapping();
	}
	
	protected void createCastorMapping() {
		castorMapping = new Mapping();
		
		if (castorMappings != null) {
			try {
				for (int i = 0; i < castorMappings.length; i++) {
					Resource mappingRes = castorMappings[i];
					castorMapping.loadMapping(mappingRes.getURL());
				}
			} catch (IOException e) {
				log.error(e);
				throw new JSExceptionWrapper(e);
			} catch (MappingException e) {
				log.error(e);
				throw new JSExceptionWrapper(e);
			}
		}
	}

	public void write(Object object, OutputStream stream, ExporterModuleContext exportContext) throws IOException {
		try {
			Writer writer = new OutputStreamWriter(stream, exportContext.getCharacterEncoding());
			Marshaller marshaller = new Marshaller(writer);
            synchronized (castorMapping) {
			    marshaller.setMapping(castorMapping);
            }
			marshaller.marshal(object);
		} catch (UnsupportedEncodingException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} catch (MappingException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} catch (XMLException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	public Object read(InputStream stream, ImporterModuleContext importContext) throws IOException {
		try {
			Reader reader = new InputStreamReader(stream, importContext.getCharacterEncoding());
			Unmarshaller unmarshaller = new Unmarshaller();
			unmarshaller.setMapping(castorMapping);
            unmarshaller.setWhitespacePreserve(true);
            Object object = unmarshaller.unmarshal(reader);
			return object;
		} catch (XMLException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} catch (MappingException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}
}
