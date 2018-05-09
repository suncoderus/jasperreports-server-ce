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
package com.jaspersoft.jasperserver.api.common.crypto;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @author Yakiv Tymoshenko
 * @version $Id$
 * @since 12.03.2015
 */
public class DiagnosticDataCipherer {

    public static final String DEFAULT_ALGORITHM = "AES";
    public static final String DEFAULT_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static final String DEFAULT_KEY = "0x1b 0xd4 0xa6 0x10 0x44 0x42 0x6f 0xb5 0x15 0xda 0xd3 0xf2 0x1f 0x18 0xaa 0x57";
    public static final int FILE_BUFFER_SIZE = 8192;

    // Temporary workaround for bug http://bugzilla.jaspersoft.com/show_bug.cgi?id=41419
    public static final String FILE_NAME_TO_EXCLUDE = "collectorSettings";

    // Default block size = 16
    private static final byte[] INIT_VECTOR_16 =
            {(byte)0x8b, (byte)0x48, (byte)0x10, (byte)0x03, (byte)0x5d, (byte)0xdf, (byte)0xf9, (byte)0xac,
                    (byte)0x17, (byte)0xf2, (byte)0xbd, (byte)0x64, (byte)0xb7, (byte)0x51, (byte)0xc0, (byte)0x29};
    private static final Logger LOG = LoggerFactory.getLogger(DiagnosticDataCipherer.class);

    private String keyAlgorithm;
    private String cipherTransformation;
    private String keyBytes;
    private boolean keyInPlainText;
    private boolean allowEncryption;
    private String encryptedFileExtension;

    public DiagnosticDataCipherer() {
        keyAlgorithm = DEFAULT_ALGORITHM;
        cipherTransformation = DEFAULT_TRANSFORMATION;
        keyBytes = DEFAULT_KEY;
    }

    /**
     * Encrypts each file separately in given collector's dir.
     * Encrypted file's content is placed in file named "unencrypted_file_name" + "." + "encryptedFileExtension".
     * After file's content was encrytped and copied to "xxx.jsEncrytped" file, the original (unencrypted) file is
     * deleted.
     *
     * @param dirPath path to collector's dir
     */
    public void encryptDiagnosticData(String dirPath) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        doCryptoDir(dirPath, Cipher.ENCRYPT_MODE);
    }

    /**
     * Decrypts each file in given folder.
     * When files are decrypted, their original encrypted versions are deleted.
     *
     * @param dirPath path to collector's dir
     */
    public void decryptDiagnosticData(String dirPath) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        doCryptoDir(dirPath, Cipher.DECRYPT_MODE);
    }

    /**
     * Decrypts "export.zip" file on import.
     *
     * @param input encrypted "export.zip" file
     * @return decrypted "export.zip" file
     */
    public File decryptExportZip(File input) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        File output;
        try {
            output = File.createTempFile("tmp" + input.hashCode() + "decrypted", null);
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            doCryptoFile(input, output, cipher, Cipher.DECRYPT_MODE);
        } catch (IOException e) {
            LOG.error("Failed to create tmp file for export.zip decryption.", e);
            throw new IllegalStateException("Tmp file creation for export.zip decryption failed. " +
                    "Can not proceed with import.");
        }
        return output;
    }

    private void doCryptoDir(String dirPath, int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = getCipher(mode);
        if (cipher == null) {
            LOG.error("Error encrypting Diagnostics data: Cipher not initialized.");
            return;
        }
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            LOG.error(String.format("Error encrypting/decrypting Diagnostics data: wrong folder path: %s", dirPath));
            return;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.getName().contains(FILE_NAME_TO_EXCLUDE)) {
                    doCryptoFile(file, cipher, mode);
                    if (Cipher.ENCRYPT_MODE == mode) {
                        // Remove the original (unencrypted) file after encryption is finished
                        if (!file.delete()) {
                            LOG.error("Failed to delete original file after encryption: ", file.getPath());
                        }
                    }
                }
            }
        } else {
            LOG.error(String.format("Error encrypting/decrypting Diagnostics data: no files found: %s", dirPath));
        }
    }

    private Cipher getCipher(int mode) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        // Create SecretKey
        byte[] bytes;
        if (keyInPlainText) {
            bytes = keyBytes.getBytes();
        } else {
            String[] keyStringArr = keyBytes.split("\\s+");
            bytes = new byte[keyStringArr.length];
            for (int i=0; i< keyStringArr.length; i++) {
                bytes[i] = Integer.decode(keyStringArr[i]).byteValue();
            }
        }
        Key secretKey = new SecretKeySpec(bytes, keyAlgorithm);
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(INIT_VECTOR_16);

        // Init Cipher
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(cipherTransformation);
            cipher.init(mode, secretKey, paramSpec);
        } catch (NoSuchAlgorithmException e) {
            LOG.error(String.format("Error initiating Cipher for Diagnostics: no such algorithm. %s", e));
            throw e;
        } catch (NoSuchPaddingException e) {
            LOG.error(String.format("Error initiating Cipher for Diagnostics: no such padding. %s", e));
            throw e;
        } catch (InvalidKeyException e) {
            LOG.error(String.format("Error initiating Cipher for Diagnostics: invalid key. %s", e));
            throw e;
        } catch (InvalidAlgorithmParameterException e) {
            LOG.error(String.format("Error initiating Cipher for Diagnostics: invalid algorithm parameter. %s", e));
            throw e;
        }
        return cipher;
    }

    private void doCryptoFile(File inFile, Cipher cipher, int mode) {
        File outFile = createOutputFile(inFile, mode);
        doCryptoFile(inFile, outFile, cipher, mode);
    }

    private void doCryptoFile(File inFile, File outFile, Cipher cipher, int mode) {
        InputStream is = null;
        BufferedInputStream bis = null;
        CipherInputStream cis = null;

        OutputStream os = null;
        BufferedOutputStream bos = null;
        CipherOutputStream cos = null;

        try {
            is = new FileInputStream(inFile);
            bis = new BufferedInputStream(is, FILE_BUFFER_SIZE);
            os = new FileOutputStream(outFile);
            bos = new BufferedOutputStream(os, FILE_BUFFER_SIZE);
            if (Cipher.ENCRYPT_MODE == mode) {
                // Write encryption marker in file's header
                bos.write(encryptedFileExtension.getBytes());
                bos.flush();
                // Write encrypted file's content after encryption file header
                cos = new CipherOutputStream(bos, cipher);
                copyAndEncryptOrDecryptFileData(bis, cos);
            } else {
                // Ignore encryption file's header
                int length = encryptedFileExtension.getBytes().length;
                byte[] buffer = new byte[length];
                if (bis.read(buffer, 0, length) > 0) {
                    if (!encryptedFileExtension.equals(new String(buffer, "UTF-8"))) {
                        // Case when encryption indicator file header was already cut off
                        // and buffer contains data that needs to be decrypted
                        cos = new CipherOutputStream(bos, cipher);
                        cos.write(buffer, 0, length);
                        cos.flush();
                    }
                }
                // Decrypt file's content after encryption file header
                cis = new CipherInputStream(bis, cipher);
                copyAndEncryptOrDecryptFileData(cis, bos);
            }
        } catch (IOException e) {
            String encryptOrDecrypt = Cipher.ENCRYPT_MODE == mode ? "encryption" : "decryption";
            LOG.error("Error in diagnostic data " + encryptOrDecrypt, e);
        } finally {
            // Close output streams
            IOUtils.closeQuietly(cos);
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(os);
            // Close input streams
            IOUtils.closeQuietly(cis);
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(is);
            // The "inFile" will be deleted by ImportRunnable.run() in finally() block.
        }
    }

    private void copyAndEncryptOrDecryptFileData(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[FILE_BUFFER_SIZE];
        int count;
        while ((count = is.read(buffer)) != -1) {
            os.write(buffer, 0, count);
        }
        os.flush();
    }

    private File createOutputFile(File in, int mode) {
        File out = null;
        try {
            if (in.getName().endsWith(".zip")) {
                if (Cipher.ENCRYPT_MODE == mode) {
                    out = new File(in.getParent() + File.separator + in.getName().replaceAll(".zip", "") +
                            "." + encryptedFileExtension + ".zip");
                } else {
                    out = new File(in.getParent() + File.separator + in.getName().replaceAll("." + encryptedFileExtension, ""));
                }
            } else {
                if (Cipher.ENCRYPT_MODE == mode) {
                    out = new File(in.getPath() + "." + encryptedFileExtension);
                } else {
                    String inFilePath = in.getPath();
                    inFilePath = inFilePath.replaceAll("." + encryptedFileExtension, "");
                    out = new File(inFilePath);
                }
                if (!out.createNewFile()) {
                    LOG.error(String.format("Failed to create output file for Diagnostics data encryption/decryption: %s",
                            out.getPath()));
                }
            }
        } catch (IOException e) {
            LOG.error("Error during temp file creation for diagnostic data encryption/decryption", e);
        }
        return out;
    }

    public void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
    }

    public void setKeyBytes(String keyBytes) {
        this.keyBytes = keyBytes;
    }

    public void setKeyInPlainText(boolean keyInPlainText) {
        this.keyInPlainText = keyInPlainText;
    }

    public void setEncryptedFileExtension(String encryptedFileExtension) {
        this.encryptedFileExtension = encryptedFileExtension;
    }

    public boolean isAllowEncryption() {
        return allowEncryption;
    }

    public void setAllowEncryption(boolean allowEncryption) {
        this.allowEncryption = allowEncryption;
    }

    public void setCipherTransformation(String cipherTransformation) {
        this.cipherTransformation = cipherTransformation;
    }

    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public String getCipherTransformation() {
        return cipherTransformation;
    }

    public String getKeyBytes() {
        return keyBytes;
    }

    public boolean isKeyInPlainText() {
        return keyInPlainText;
    }

    public String getEncryptedFileExtension() {
        return encryptedFileExtension;
    }
}
