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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;
import com.jaspersoft.jasperserver.api.metadata.olap.util.XMLDecoderHandler;

import javax.xml.parsers.SAXParserFactory;

/**
 * @author tkavanagh
 * @version $Id$
 */
public class OlapUnitBean extends ResourceBean {
	
	public static final String DATA_PROVIDER_VIEW_OPTIONS = "olapUnitViewOptions";
	
	private String mdxQuery;
	private ResourceReferenceBean olapClientConnection;
	private String olapViewOptionsDataFile;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		OlapUnit unit = (OlapUnit) res;
		setMdxQuery(unit.getMdxQuery());
		setOlapClientConnection(exportHandler.handleReference(unit.getOlapClientConnection()));
		setOlapViewOptionsDataFile(exportHandler.handleData(unit, DATA_PROVIDER_VIEW_OPTIONS));
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		OlapUnit unit = (OlapUnit) res;
		unit.setMdxQuery(getMdxQuery());
		unit.setOlapClientConnection(importHandler.handleReference(getOlapClientConnection()));
		unit.setOlapViewOptions(constructViewOptions(importHandler));
	}

	protected Object constructViewOptions(ResourceImportHandler importHandler) {
		Object options;
		if (olapViewOptionsDataFile == null) {
			options = null;
		} else {
			byte[] viewOptionsData = importHandler.handleData(this, 
					olapViewOptionsDataFile, DATA_PROVIDER_VIEW_OPTIONS);
            InputStream stream = new ByteArrayInputStream(viewOptionsData);
            try {
                XMLDecoder decoder = new XMLDecoder(stream);
                options = decoder.readObject();
                decoder.close();
            } catch (Throwable e){
				// We catch Throwable because under WebSphere and IBM Jdk 8
				// XMLDecoder java.beans.XMLDecoder.readObject throws javax.xml.parsers.FactoryConfigurationError
                XMLDecoderHandler hander = new XMLDecoderHandler();
                try {
                    SAXParserFactory.newInstance().newSAXParser().parse(stream, hander);
                    options = hander.getResult();
                } catch (Exception e1) {
                    throw new JSException("Cannot parse file " + olapViewOptionsDataFile);
                }
            }
		}
		return options;
	}

	public String getMdxQuery() {
		return mdxQuery;
	}
	
	public void setMdxQuery(String mdxQuery) {
		this.mdxQuery = mdxQuery;
	}
	
	public ResourceReferenceBean getOlapClientConnection() {
		return olapClientConnection;
	}
	
	public void setOlapClientConnection(ResourceReferenceBean olapClientConnection) {
		this.olapClientConnection = olapClientConnection;
	}

	public String getOlapViewOptionsDataFile() {
		return olapViewOptionsDataFile;
	}

	public void setOlapViewOptionsDataFile(String olapViewOptionsDataFile) {
		this.olapViewOptionsDataFile = olapViewOptionsDataFile;
	}
}
