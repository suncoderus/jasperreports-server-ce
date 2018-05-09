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
package com.jaspersoft.jasperserver.api.metadata.common.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.io.InputStream;


/**
 * FileResource is the interface which represents the JasperServer resource containing some files
 * of any type: images, fonts, jrxml, jar, resource bundles, style templates, xml.
 * It extends {@link com.jaspersoft.jasperserver.api.metadata.common.domain.Resource}
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface FileResource extends Resource, FileResourceBase
{

    /**
     * Returns <code>true</code> if the resource has some file.
     *
     * @return <code>true</code> if the resource has some file.
     */
	boolean hasData();

    /**
     * Returns the data stream of the file
     *
     * @return data stream of the file
     */
	InputStream getDataStream();

    /**
     * Sets the data of the file input stream
     *
     * @param is the input stream of file to be set
     */
	void readData(InputStream is);

    /**
     * Returns the data from the file as a byte array
     *
     * @return data
     */
	byte[] getData();

    /**
     * Sets the data of the file
     *
     * @param data
     */
	void setData(byte[] data);

    /**
     * Returns the file type
     *
     * @return file type
     */
	String getFileType();

    /**
     * Sets the file type
     *
     * @param fileType
     */
	void setFileType(String fileType);

    /**
     * Shows if the file resource is only a reference
     *
     * @return <code>true</code> if file resource is a reference
     */
	boolean isReference();

    /**
     * Returns the URI that the resource references to
     *
     * @return reference URI
     */
	String getReferenceURI();

    /**
     * Sets the URI that resource should reference to
     *
     * @param referenceURI
     */
	void setReferenceURI(String referenceURI);
}
