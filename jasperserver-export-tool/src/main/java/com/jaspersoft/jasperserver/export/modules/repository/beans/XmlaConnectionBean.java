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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

import java.util.Map;

/**
 * @author tkavanagh
 * @version $Id$
 */


public class XmlaConnectionBean extends ResourceBean {

	private String uri;
	private String dataSource;
	private String catalog;
	private String username;
	private String password;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		XMLAConnection xmla = (XMLAConnection) res;
		setUri(xmla.getURI());
		setDataSource(xmla.getDataSource());
		setCatalog(xmla.getCatalog());
		setUsername(xmla.getUsername());

		//encrypt for export
		//TODO: in the future, encryption should be done with an asymmetric public key from the TARGET server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved to encryption engine
		setPassword(ENCRYPTION_PREFIX + importExportCipher.encode(xmla.getPassword()) + ENCRYPTION_SUFFIX);
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		XMLAConnection xmla = (XMLAConnection) res;
		xmla.setURI(getUri());
		xmla.setDataSource(getDataSource());
		xmla.setCatalog(getCatalog());
		xmla.setUsername(getUsername());

		//decrypt pwd for import. if decryption fails, set password as is; this is probably due to legacy import
		//TODO: in the future, decryption should be done with an asymmetric private key from THIS server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved inside encrypt()/decrypt() in encryption engine
		final String pwd = getPassword();
		xmla.setPassword((pwd != null && pwd.startsWith(ENCRYPTION_PREFIX) && pwd.endsWith(ENCRYPTION_SUFFIX)) ?
			importExportCipher.decode(pwd.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : pwd);
	}

	@Override
	protected void handleNewTenantIds(Resource res, Map<String, String> map) {
		super.handleNewTenantIds(res, map);
		((XMLAConnection) res).setUsername(TenantStrHolderPattern.
				TENANT_QUALIFIED_NAME.replaceWithNewTenantIds(map, ((XMLAConnection) res).getUsername()));
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
