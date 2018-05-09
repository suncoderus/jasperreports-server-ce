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
package com.jaspersoft.jasperserver.war.dto;

import java.io.Serializable;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ResourceReferenceDTO implements Serializable {

	private String source;
	private Resource localResource;
	private String referenceURI;
	
	public ResourceReferenceDTO() {
		this(null);
	}
	
	public ResourceReferenceDTO(ResourceReference reference) {
		if (reference == null) {
			source = JasperServerConst.FIELD_CHOICE_NONE;
		} else if (reference.isLocal()) {
			source = JasperServerConst.FIELD_CHOICE_LOCAL;
			localResource = (Query) reference.getLocalResource();
		} else {
			source = JasperServerConst.FIELD_CHOICE_CONT_REPO;
			referenceURI = reference.getReferenceURI();
		}
	}
	
	/**
	 * @return Returns the referenceURI.
	 */
	public String getReferenceURI() {
		return referenceURI;
	}
	
	/**
	 * @param referenceURI The referenceURI to set.
	 */
	public void setReferenceURI(String referenceURI) {
		this.referenceURI = referenceURI;
	}
	
	/**
	 * @return Returns the source.
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * @param source The source to set.
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * @return Returns the localResource.
	 */
	public Resource getLocalResource() {
		return localResource;
	}
	/**
	 * @param localResource The localResource to set.
	 */
	public void setLocalResource(Resource localResource) {
		this.localResource = localResource;
	}
	
	public ResourceReference toResourceReference() {
		ResourceReference ref;
		if (getSource().equals(JasperServerConst.FIELD_CHOICE_NONE)) {
			ref = null;
		} else if (getSource().equals(JasperServerConst.FIELD_CHOICE_LOCAL)) {
			ref = new ResourceReference(getLocalResource());
		} else if (getSource().equals(JasperServerConst.FIELD_CHOICE_CONT_REPO)) {
			ref = new ResourceReference(getReferenceURI());
		} else {
			String quotedSource = "\"" + getSource() + "\"";
			throw new JSException("jsexception.invalid.resource.reference.source", new Object[] {quotedSource});
		}
		return ref;
	}
}
