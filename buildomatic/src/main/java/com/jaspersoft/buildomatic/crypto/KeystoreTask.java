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

import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.jaspersoft.buildomatic.crypto.MasterPropertiesObfuscator.ENCRYPT_FLAG;

/**
 * This class is invoked by ant during the build.  It, in turn, calls KeystoreManager to generate
 * a centralized keystore and secret keys.
 *
 * User: dlitvak
 * Date: 7/16/13
 */
public class KeystoreTask extends Task {
	//keystore dir
	private String ks;

	//keystore property file loc-n
	private String ksp = ".jrsksp";

	private String propsFile = null;

	@Override
	public void execute() throws BuildException {
		FileInputStream masterPropFis = null;
		try {
			if (propsFile == null)
			   throw new RuntimeException("Failed to find master properties file");

			//TODO TEMP: load master props
			masterPropFis = new FileInputStream(propsFile);
			Properties masterProps = new Properties();
			masterProps.load(masterPropFis);

			KeystoreManager.init(ks, ksp, new EncryptionProperties(new File(propsFile)));
		} catch (Exception e) {
			log(e.getMessage(), Project.MSG_ERR);
			throw new BuildException("Keystore may have been tempered with.", e, getLocation());
		}
		finally {
			try {
				if (masterPropFis != null)
					masterPropFis.close();
			} catch (IOException e) {
				log("Failed to close master prop: " + e, Project.MSG_ERR);
			}
		}
	}

	public String getKs() {
		return ks;
	}

	public void setKs(String directory) {
		this.ks = directory;
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

	public void setPropsFile(String propsFile) {
		this.propsFile = propsFile;
	}

}
