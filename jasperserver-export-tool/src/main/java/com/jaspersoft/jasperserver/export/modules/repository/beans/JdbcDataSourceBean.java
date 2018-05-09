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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.export.BaseExporterImporter;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id$
 */
public class JdbcDataSourceBean extends ResourceBean {

	private String driverClass;
	private String url;
	private String username;
	private String password;
	private String timezone;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		JdbcReportDataSource ds = (JdbcReportDataSource) res;
		setDriverClass(ds.getDriverClass());
		setConnectionUrl(ds.getConnectionUrl());
		setConnectionUsername(ds.getUsername());

		//encrypt for export
		//TODO: in the future, encryption should be done with an asymmetric public key from the TARGET server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved to encryption engine
		setConnectionPassword(ENCRYPTION_PREFIX + importExportCipher.encode(ds.getPassword()) + ENCRYPTION_SUFFIX);

		setTimezone(ds.getTimezone());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		JdbcReportDataSource ds = (JdbcReportDataSource) res;
		ds.setDriverClass(getDriverClass());
		ds.setConnectionUrl(getConnectionUrl());
		ds.setUsername(getConnectionUsername());
		ds.setTimezone(getTimezone());

		//decrypt pwd for import. if decryption fails, set password as is; this is probably due to legacy import
		//TODO: in the future, decryption should be done with an asymmetric private key from THIS server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved inside encrypt()/decrypt() in encryption engine
		final String pwd = getConnectionPassword();
		ds.setPassword((pwd != null && pwd.startsWith(ENCRYPTION_PREFIX) && pwd.endsWith(ENCRYPTION_SUFFIX)) ?
			importExportCipher.decode(pwd.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : pwd);
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getConnectionUrl() {
		return url;
	}

	public void setConnectionUrl(String url) {
		this.url = url;
	}

	public String getConnectionUsername() {
		return username;
	}

	public void setConnectionUsername(String username) {
		this.username = username;
	}

	public String getConnectionPassword() {
		return password;
	}

	public void setConnectionPassword(String password) {
		this.password = password;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

}
