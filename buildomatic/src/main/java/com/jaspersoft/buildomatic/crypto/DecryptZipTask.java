/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2009 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.buildomatic.crypto;

import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.Key;

public class DecryptZipTask extends Task implements com.jaspersoft.buildomatic.crypto.DiagnosticAntTask {

    private String inFile;
    private String encProps;
    private com.jaspersoft.buildomatic.crypto.DiagnosticCryptoUtil diagnosticCryptoUtil = new com.jaspersoft.buildomatic.crypto.DiagnosticCryptoUtil(this);

    /**
     * Decrypts diagnostic ZIP file
     *
     * @throws BuildException
     */
    @Override
    public void execute() throws BuildException {
        File inF = new File(inFile);

        try {
            EncryptionProperties encryptionProperties = diagnosticCryptoUtil.getEncryptionProperties(encProps);
            Key key = new SecretKeySpec(encryptionProperties.getKeyBytes(),
                    encryptionProperties.getKeyProperties().getKeyAlgo());

            if (!inF.exists()) {
                throw new IllegalArgumentException("Input file not exists.");
            }

            if (inF.isDirectory()) {
                File[] files = inF.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().contains("log") && file.getName().contains("jsEncrypted")) {
                            // TODO danger: deleting file from collection over which iterating
                            File outF = diagnosticCryptoUtil.createDecryptedOutputFile(file);
                            EncryptionEngine.decryptFile(file, outF, key, encryptionProperties);
                        }
                    }
                } else {
                    throw new IllegalArgumentException("No files to decrypt found in directory: " + inF.getName());
                }
            } else {
                File outF = diagnosticCryptoUtil.createDecryptedOutputFile(inF);
                EncryptionEngine.decryptFile(inF, outF, key, encryptionProperties);
            }
        } catch (Exception e) {
            log("Error encrypting the file. " + e);
            e.printStackTrace();
        }
    }

    public void logMessage(String message) {
        log(message);
    }

    public void setInFile(String inFile) {
        this.inFile = inFile;
    }

    public void setEncProps(String encProps) {
        this.encProps = encProps;
    }
}