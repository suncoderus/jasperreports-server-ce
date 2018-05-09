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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author vsabadosh
 */
public class AwsDataSourceBean extends JdbcDataSourceBean {

	private String accessKey;
	private String secretKey;
	private String roleARN;
	private String region;
	private String dbName;
	private String dbInstanceIdentifier;
	private String dbService;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		super.additionalCopyFrom(res, referenceHandler);

		AwsReportDataSource ds = (AwsReportDataSource) res;

		//encrypt for export
		//TODO: in the future, encryption should be done with an asymmetric public key from the TARGET server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved to encryption engine
		setAccessKey(ENCRYPTION_PREFIX + importExportCipher.encode(ds.getAWSAccessKey()) + ENCRYPTION_SUFFIX);
		setSecretKey(ENCRYPTION_PREFIX + importExportCipher.encode(ds.getAWSSecretKey()) + ENCRYPTION_SUFFIX);

		setRoleARN(ds.getRoleARN());
		setRegion(ds.getAWSRegion());
		setDbName(ds.getDbName());
		setDbService(ds.getDbService());
		setDbInstanceIdentifier(ds.getDbInstanceIdentifier());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		super.additionalCopyTo(res, importHandler);

		AwsReportDataSource ds = (AwsReportDataSource) res;
		ds.setRoleARN(getRoleARN());
		ds.setAWSRegion(getRegion());
		ds.setDbName(getDbName());
		ds.setDbService(getDbService());
		ds.setDbInstanceIdentifier(getDbInstanceIdentifier());

		final String accKey = getAccessKey();
		final String secKey = getSecretKey();

		//decrypt keys for import. if decryption fails, set keys as is; this is probably due to legacy import
		//TODO: in the future, decryption should be done with an asymmetric private key from THIS server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved inside encrypt()/decrypt() in encryption engine
		ds.setAWSAccessKey((accKey != null && accKey.startsWith(ENCRYPTION_PREFIX) && accKey.endsWith(ENCRYPTION_SUFFIX)) ?
			importExportCipher.decode(accKey.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : accKey);
		ds.setAWSSecretKey((secKey != null && secKey.startsWith(ENCRYPTION_PREFIX) && secKey.endsWith(ENCRYPTION_SUFFIX)) ?
			importExportCipher.decode(secKey.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : secKey);
	}


	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getRoleARN() {
		return roleARN;
	}

	public void setRoleARN(String roleARN) {
		this.roleARN = roleARN;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbInstanceIdentifier() {
		return dbInstanceIdentifier;
	}

	public void setDbInstanceIdentifier(String dbInstanceIdentifier) {
		this.dbInstanceIdentifier = dbInstanceIdentifier;
	}

	public String getDbService() {
		return dbService;
	}

	public void setDbService(String dbService) {
		this.dbService = dbService;
	}
}
