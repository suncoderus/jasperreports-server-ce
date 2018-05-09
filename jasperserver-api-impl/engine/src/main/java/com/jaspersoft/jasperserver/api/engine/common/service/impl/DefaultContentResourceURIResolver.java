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

package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DefaultContentResourceURIResolver implements
		ContentResourceURIResolver, InitializingBean {

	private WebDeploymentInformation deploymentInformation;
	private String contentControllerPath;
	private URI baseURI;

	public void afterPropertiesSet() throws Exception {
		baseURI = new URI(deploymentInformation.getDeploymentURI() + contentControllerPath);
	}
	
	public String resolveURI(String repositoryPath) {
		String uriPath = baseURI.getPath() + repositoryPath;
		try {
			URI uri = new URI(baseURI.getScheme(), baseURI.getUserInfo(), baseURI.getHost(), baseURI.getPort(), 
					uriPath, baseURI.getQuery(), baseURI.getFragment());
			return uri.toASCIIString();
		} catch (URISyntaxException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	public WebDeploymentInformation getDeploymentInformation() {
		return deploymentInformation;
	}

	public void setDeploymentInformation(
			WebDeploymentInformation deploymentInformation) {
		this.deploymentInformation = deploymentInformation;
	}

	public String getContentControllerPath() {
		return contentControllerPath;
	}

	public void setContentControllerPath(String contentControllerPath) {
		this.contentControllerPath = contentControllerPath;
	}

}
