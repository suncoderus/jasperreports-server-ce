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

import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import org.apache.tools.ant.Task;

import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.util.Properties;

/**
 * @author Yakiv Tymoshenko
 * @version $Id: Id $
 * @since 11.03.2015
 */
public class DiagnosticCryptoUtil {

    public static final String KEY = "key";
    public static final String KEY_ALGORITHM = "key.algorithm";
    public static final String KEY_IS_PLAIN_TEXT = "key.is.plain.text";

    private DiagnosticAntTask callerAntTask;

    DiagnosticCryptoUtil(DiagnosticAntTask callerAntTask) {
        this.callerAntTask = callerAntTask;
    }

    File createEncryptedOutputFile(File inF) {
        return createOutputFile(inF, "enc");
    }

    File createDecryptedOutputFile(File inF) {
        return createOutputFile(inF, "dec");
    }

    private File createOutputFile(File inF, String mode) {
        String prefixToRemove = "";
        String prefixToAdd = "";
        if ("enc".equals(mode)) {
            prefixToRemove = "dec_";
            prefixToAdd = "enc_";
        }
        if ("dec".equals(mode)) {
            prefixToRemove = "enc_";
            prefixToAdd = "dec_";
        }
        String name = inF.getName();
        if (name.startsWith(prefixToRemove)) {
            name = name.replaceFirst(prefixToRemove, "");
        }
        String encryptionFileExtension = ".jsEncrypted";
        if (name.contains(encryptionFileExtension)) {
            name = name.replaceFirst(encryptionFileExtension, "");
        }
        String parentDir = inF.getParent();
        File outF = new File(parentDir + File.separator + prefixToAdd + name);
        if (!outF.exists()) {
            try {
                if (!outF.createNewFile()) {
                    callerAntTask.logMessage("Failed to create out file");
                }
            } catch (IOException e) {
                callerAntTask.logMessage("Failed to create out file." + e);
            }
        }
        return outF;

    }

    EncryptionProperties getEncryptionProperties(String encProps) {
        File masterProperties = new File(encProps);
        if (!masterProperties.exists()) {
            callerAntTask.logMessage("Wrong encryption properties file.");
        }
        return new EncryptionProperties(masterProperties);
    }

    Key readKeyFromFile(String keyFile) {
        Key result = null;
        Properties keyProperties = readSecretKeyProperties(keyFile);
        if (keyProperties != null) {
            String key = keyProperties.getProperty(KEY);
            String keyAlgorithm = keyProperties.getProperty(KEY_ALGORITHM);
            boolean isKeyPlainText = Boolean.parseBoolean(keyProperties.getProperty(KEY_IS_PLAIN_TEXT));

            byte [] keyBytes;
            if (isKeyPlainText) {
                keyBytes = key.getBytes();
            } else {
                String[] keyStringArr = key.split("\\s+");
                keyBytes = new byte[keyStringArr.length];
                for (int i=0; i< keyStringArr.length; i++) {
                    keyBytes[i] = Integer.decode(keyStringArr[i]).byteValue();
                }
            }

            try {
                result = new SecretKeySpec(keyBytes, keyAlgorithm);
            } catch (Exception e) {
                callerAntTask.logMessage("Unable to create Key instance." + e);
            }
        }
        return result;
    }

    private Properties readSecretKeyProperties(String propertiesFilePath) {
        File propertiesFile = new File(propertiesFilePath);
        if (!propertiesFile.exists()) {
            callerAntTask.logMessage(String.format("SecretKey properties file not found: %s", propertiesFilePath));
            return null;
        }
        FileInputStream fis = null;
        Properties result = new Properties();
        try {
            fis = new FileInputStream(propertiesFile);
            result.load(fis);
        } catch (FileNotFoundException e) {
            callerAntTask.logMessage(String.format("SecretKey properties file not found: %s", propertiesFilePath));
        } catch (IOException e) {
            callerAntTask.logMessage(String.format("Error reading SecretKey properties^ %s", propertiesFilePath));
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    callerAntTask.logMessage("Error closing FileInputStream after reading SecretKey properties");
                }
            }
        }
        return result;
    }
}
