/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.buildomatic.crypto;

import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.File;
import java.security.Key;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jaspersoft.jasperserver.crypto.EncryptionProperties.CRYPTO_BLOCK_SIZE_PARAM;
import static com.jaspersoft.jasperserver.crypto.EncryptionProperties.CRYPTO_TRANSFORMATION_PARAM;

/**
 * User: dlitvak
 * Date: 7/17/13
 */
public class MasterPropertiesObfuscator extends Task {
	public static final String ENCRYPT_FLAG = "encrypt";
	public static final String ENCRYPT_DONE_FLAG = "encrypt.done";
	public static final String PROPS_TO_ENCRYPT_PARAM = "propsToEncrypt";
	public static final String PROPS_TO_ENCRYPT_DEF = "dbPassword";

	private String propsFile;   //required
	//keystore property file loc-n
	private String ksp = ".jrsksp";


	@Override
	public void execute() throws BuildException {
		try {
			//load master props
			PropertiesConfiguration config = new PropertiesConfiguration(new File(propsFile));
			PropertiesConfigurationLayout configLayout = config.getLayout();
			configLayout.setGlobalSeparator("=");

			Boolean encFlag = Boolean.parseBoolean(config.getString(ENCRYPT_FLAG));
			Boolean encDoneFlag = Boolean.parseBoolean(config.getString(ENCRYPT_DONE_FLAG));
			if (encFlag && !encDoneFlag) {
				String blockSzStr = config.getString(CRYPTO_BLOCK_SIZE_PARAM);
				EncryptionProperties encProps =
						new EncryptionProperties(blockSzStr != null ? Integer.parseInt(blockSzStr) : null,
								config.getString(CRYPTO_TRANSFORMATION_PARAM));
				List<Object> propsToEncryptList = config.getList(PROPS_TO_ENCRYPT_PARAM, Arrays.<Object>asList(PROPS_TO_ENCRYPT_DEF));
				log("Encrypt " + StringUtils.join(propsToEncryptList.toArray(), ','), Project.MSG_INFO);
				log("Encryption block size: " + encProps.getBlockSize(), Project.MSG_DEBUG);
				log("Encryption mode: " + encProps.getCipherTransformation(), Project.MSG_DEBUG);

				//obtain Keystore Manager
				KeystoreManager.init(this.ksp);
				KeystoreManager ksManager = KeystoreManager.getInstance();

				//obtain key
				Key secret = ksManager.getBuildKey();

				Set<String> paramSet = new HashSet<String>(propsToEncryptList.size());
				for (Object prop : propsToEncryptList) {
					String propNameToEnc = prop.toString().trim();
					if (paramSet.contains(propNameToEnc))
						continue;  //was already encrypted once
					paramSet.add(propNameToEnc);

					String pVal = config.getString(propNameToEnc);
					if (pVal != null) {
						if (EncryptionEngine.isEncrypted(pVal))
							log("encrypt=true was set, but param " + propNameToEnc + " was found already encrypted. " +
								" Skipping its encryption.", Project.MSG_WARN);
						else {
							String ct = EncryptionEngine.encrypt(secret, pVal, encProps);
							config.setProperty(propNameToEnc, ct);
						}
					}
				}

				//set encryption to done
				config.clearProperty(ENCRYPT_FLAG);
				config.setProperty(ENCRYPT_DONE_FLAG, "true");

				//write master props back
				config.save();
			}
			else if (encDoneFlag) {
				log("The master properties have already been encrypted. To re-enable the encryption, " +
						"make sure the passwords are in plain text, set master property " +
						"encrypt to true and remove encrypt.done.", Project.MSG_INFO);
			}
		} catch (Exception e) {
			throw new BuildException(e, getLocation());
		}
	}

	public String getKsp() {
		return ksp;
	}

	public void setKsp(String ksProp) {
		this.ksp = ksProp;
	}

	public String getPropsFile() {
		return propsFile;
	}

	public void setPropsFile(String file) {
		this.propsFile = file;
	}

}
