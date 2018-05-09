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
package com.jaspersoft.jasperserver.export.modules.scheduling.beans;

import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.FTPInfo;
import com.jaspersoft.jasperserver.api.common.crypto.Cipherer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean.ENCRYPTION_PREFIX;
import static com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean.ENCRYPTION_SUFFIX;

/**
 * @author ichan
 * @version $Id$
 */
public class FTPInfoBean {
	private static final Logger logger = LogManager.getLogger(FTPInfoBean.class);

	String userName;
	String password;
	String folderPath;
	String serverName;
	private String[] propertiesMapKeys;
	private String[] propertiesMapValues;

	private static final Cipherer importExportCipher =
			(Cipherer) StaticApplicationContext.getApplicationContext().getBean("importExportCipher");

	public void copyFrom(FTPInfo src) {
		setUserName(src.getUserName());

		//encrypt for export
		//TODO: in the future, encryption should be done with an asymmetric public key from the TARGET server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved to encryption engine
        String password = importExportCipher.encode(src.getPassword());
		setPassword(password == null ? password : ENCRYPTION_PREFIX + password + ENCRYPTION_SUFFIX);

		setFolderPath(src.getFolderPath());
		setServerName(src.getServerName());
		if (src.getPropertiesMap() == null) {
			propertiesMapKeys = new String[0];
			propertiesMapValues = new String[0];
		} else {
			propertiesMapKeys = new String[src.getPropertiesMap().size()];
			propertiesMapValues = new String[propertiesMapKeys.length];
			int i = 0;
			for (Map.Entry<String, String> entry : src.getPropertiesMap().entrySet()) {
				propertiesMapKeys[i] = entry.getKey();
				//encrypt SSH Private Key passphrase for export
				propertiesMapValues[i++] = entry.getKey().equals(FTPInfo.SSH_PASSPHRASE_PROPERTY) ?
						ENCRYPTION_PREFIX + importExportCipher.encode(entry.getValue()) + ENCRYPTION_SUFFIX :
						entry.getValue();
			}
		}
	}

	public void copyTo(FTPInfo dest) {
		dest.setUserName(getUserName());
		dest.setFolderPath(getFolderPath());
		dest.setServerName(getServerName());
		HashMap<String, String> map = new HashMap<String, String>();
		if (propertiesMapKeys != null) {
			for (int i = 0; i < propertiesMapKeys.length; i++) {
				map.put(propertiesMapKeys[i], propertiesMapValues[i]);
			}
		}
		dest.setPropertiesMap(map);

		// decrypt SSH Private Key passphrase for import
		if (map.containsKey(FTPInfo.SSH_PASSPHRASE_PROPERTY)) {
			String val = map.get(FTPInfo.SSH_PASSPHRASE_PROPERTY);
			if (val.startsWith(ENCRYPTION_PREFIX) && val.endsWith(ENCRYPTION_SUFFIX)) {
				map.put(FTPInfo.SSH_PASSPHRASE_PROPERTY, importExportCipher.decode(val.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")));
			}
		}

		//decrypt pwd for import. if decryption fails, set password as is; this is probably due to legacy import
		//TODO: in the future, decryption should be done with an asymmetric private key from THIS server
		//ENCRYPTION_PREFIX, ENCRYPTION_SUFFIX operations will be moved inside encrypt()/decrypt() in encryption engine
		final String pwd = getPassword();
		dest.setPassword((pwd != null && pwd.startsWith(ENCRYPTION_PREFIX) && pwd.endsWith(ENCRYPTION_SUFFIX)) ?
			importExportCipher.decode(pwd.replaceFirst(ENCRYPTION_PREFIX, "").replaceAll(ENCRYPTION_SUFFIX + "$", "")) : pwd);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String[] getPropertiesMapKeys() {
		return propertiesMapKeys;
	}

	public void setPropertiesMapKeys(String[] propertiesMapKeys) {
		this.propertiesMapKeys = propertiesMapKeys;
	}

	public String[] getPropertiesMapValues() {
		return propertiesMapValues;
	}

	public void setPropertiesMapValues(String[] propertiesMapValues) {
		this.propertiesMapValues = propertiesMapValues;
	}


}
