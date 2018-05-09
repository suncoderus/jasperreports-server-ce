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

package com.jaspersoft.jasperserver.export.modules.repository;

import com.jaspersoft.jasperserver.api.JSException;
import java.io.InputStream;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.lob.SerializableBlob;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * This should not be used any more
 */
public class OlapUnitViewOptionsDataProvider implements ResourceDataProvider {
	
	private static final Log log = LogFactory.getLog(OlapUnitViewOptionsDataProvider.class);

	private String filenameSuffix;

	public String getFileName(Resource resource) {
		return resource.getName() + getFilenameSuffix();
	}

	public InputStream getData(ExporterModuleContext exportContext, Resource resource) {

		OlapUnit unit = (OlapUnit) resource;
		InputStream dataStream;
		Object viewOptions = unit.getOlapViewOptions();
		if (viewOptions == null) {
			dataStream = null;
		} else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLEncoder e = new XMLEncoder(new BufferedOutputStream(baos));
            e.writeObject(unit.getOlapViewOptions());
            e.flush();
            e.close();
			dataStream = new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
		}
		return dataStream;
	}

	public String getFilenameSuffix() {
		return filenameSuffix;
	}

	public void setFilenameSuffix(String filenameSuffix) {
		this.filenameSuffix = filenameSuffix;
	}

}
