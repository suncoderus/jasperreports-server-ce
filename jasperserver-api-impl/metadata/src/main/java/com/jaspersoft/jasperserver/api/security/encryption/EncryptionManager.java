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
package com.jaspersoft.jasperserver.api.security.encryption;

import com.jaspersoft.jasperserver.api.security.SecurityConfiguration;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.getEncryptionKeySize;
import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.getEncryptionParameters;
import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.getEncryptionType;
import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.isEncryptionOn;

/**
 * EncryptionManager is in charge of keeping track of encryption configuration
 * such as on/off, defaults, etc.  It will also have a handle to the type of
 * encryption being employed based on the configuration.
 *
 * @author nmacaraeg
 *         Date Time: 2/8/12 11:13 AM
 */
public class EncryptionManager {
    private static final Logger logger = Logger.getLogger(EncryptionManager.class);
    public static final String KEYPAIR_SESSION_KEY = "KEYPAIR_SESSION_KEY";
    public static final String UTF8_ENCODING = "UTF-8";
    private static Encryption encryption;

    private enum EncryptionTypes {
        RSA
    }

    static {
//        if (EncryptionTypes.RSA.toString().equalsIgnoreCase(getEncryptionType()))
            encryption = new EncryptionRSA();
    }

    public boolean isEncryptionProcessOn()
    {
        return isEncryptionOn();
    }

    /**
     * Generates public and private keys. Public key gets stored into the session - public is sent over the wire
     * to the browser.
     *
     * @param isDynamicKeygenPerRequest - if true, generate the keys per every request.  If false, read keys from keystore
     * @throws java.io.IOException
     */
    public static KeyPair generateKeys(boolean isDynamicKeygenPerRequest) throws IOException, KeyStoreException {
         if (isDynamicKeygenPerRequest) {
            logger.debug("Generated keys on the fly successfully.");
            return encryption.generateKeypair(getEncryptionKeySize());
            //httpRequest.getSession().setAttribute(KEYPAIR_SESSION_KEY, keys);
        }
        else {
            synchronized (EncryptionManager.class) {   //protect reading key store from race conditions
                InputStream is = null;
                OutputStream os = null;

                try {

                    String keystoreLocation = SecurityConfiguration.getKeystoreLocation() ;
                    String keystorePassword = SecurityConfiguration.getKeystorePassword() ;
                    String keyAlias = SecurityConfiguration.getKeystoreKeyAlias() ;
                    String keyPassword = SecurityConfiguration.getKeystoreKeyPassword() ;

                    if (keystoreLocation == null || keystoreLocation.trim().length() == 0
                            || keyAlias == null || keyAlias.trim().length() == 0)
                        throw new RuntimeException("At least keystore location and key alias is required.  " +
                                "See docs on setting it up.");

                    File keystoreFile = null;
                    final URL keystoreResource = EncryptionManager.class.getClassLoader().getResource(keystoreLocation);
                    is = keystoreResource == null ? null : keystoreResource.openStream();
                    if (is == null) {//if not on the classpath, look for an external file on the system
                        keystoreFile = new File(keystoreLocation);
                        is = new FileInputStream(keystoreLocation);
                    }
                    else
                        keystoreFile = new File(keystoreResource.toURI());

                    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    keyStore.load(is, keystorePassword.toCharArray());

                    PublicKey publicKey = null;
                    PrivateKey privateKey = null;
                    KeyPair returnKeyPair = null;
                    if (keyStore.containsAlias(keyAlias)) {
                        KeyStore.PrivateKeyEntry privateKeyEntry =
                                (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, new KeyStore.PasswordProtection(keyPassword.toCharArray()));

                        privateKey = privateKeyEntry.getPrivateKey();
                        publicKey = privateKeyEntry.getCertificate().getPublicKey();
                        returnKeyPair = new KeyPair(publicKey, privateKey);
                    }
                    else {
                        if (is != null)
                            is.close();

                        os = new FileOutputStream(keystoreFile);

                        //generate private/public key pair
                        returnKeyPair = encryption.generateKeypair(getEncryptionKeySize());
                        Certificate certificate = createCertificate(returnKeyPair);
                        keyStore.setKeyEntry(keyAlias, returnKeyPair.getPrivate(), keyPassword.toCharArray(), new Certificate[] {certificate});

                        keyStore.store(os, keystorePassword.toCharArray());
                    }

                    logger.debug("Red/generated key pair (" + keyAlias + ") in keystore (" + keystoreFile.getAbsolutePath() + ") successfully.");
                    return returnKeyPair;
                }
                catch (Exception e) {
                    throw new RuntimeException("Exception loading keystore and reading the keys.", e);
                }
                finally {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                }
            }
        }
    }

    /**
     * Certificate (version 1) generated using Bouncy Castle lib.
     *
     * Reference: http://www.bouncycastle.org/wiki/display/JA1/X.509+Public+Key+Certificate+and+Certification+Request+Generation
     *
     * @param keyPair
     * @return
     */
    private static Certificate createCertificate(KeyPair keyPair) {
        try {
            Calendar expiry = Calendar.getInstance();
            expiry.add(Calendar.YEAR, 100);

            X509V1CertificateGenerator certGenerator = new X509V1CertificateGenerator();
            final X509Principal dnName = new X509Principal("CN=Jaspersoft Inc.");
            certGenerator.setSubjectDN(dnName);
            certGenerator.setIssuerDN(dnName);
            certGenerator.setNotAfter(expiry.getTime());
            certGenerator.setNotBefore(new Date());
            certGenerator.setPublicKey(keyPair.getPublic());
            certGenerator.setSerialNumber(BigInteger.valueOf(expiry.getTimeInMillis()));
            certGenerator.setSignatureAlgorithm("MD5withRSA");

            return certGenerator.generate(keyPair.getPrivate(), BouncyCastleProvider.PROVIDER_NAME);
        } catch (Exception e) {
            logger.error("Error creating certificate.", e);
            throw new RuntimeException("Error creating certificate.", e);
        }
    }

    /*
    * Decrypt an encrypted string which is tied to the session's KeyPair.
    * The encryption used the KeyPair's public key.
    * The decryption will use the KeyPair's private key.
    *
    * @param httpRequest a HttpServletRequest.
    * @param attribute the attribute key to set in the request.
    * @param encryptedValue an encrypted string value tied to the session's keypair.
    *
    * @return list of decrypted params.  If any of the params fail decryption, RunTimeException is thrown.
    */
    public List<String> decrypt(PrivateKey privateKey, String... encryptedValues) {
        List<String> retList = new ArrayList<String>(encryptedValues.length);
        try {
            if (privateKey != null) {
                for (String encryptedValue : encryptedValues) {
                    if (encryptedValue.trim().length() == 0) {
                        retList.add(encryptedValue);
                        continue;
                    }

                    String decryptedValue = encryption.decrypt(encryptedValue, privateKey);

                    if (decryptedValue != null && decryptedValue.length() > 0) {
                        try {
                            decryptedValue = URLDecoder.decode(decryptedValue, UTF8_ENCODING);

                        } catch (UnsupportedEncodingException e) {
                            logger.error(UTF8_ENCODING + " encoding not supported");
                        }
                    } else {
                        decryptedValue = encryptedValue;
                        logger.warn("Cannot decrypt encryptedValue (hidden for security reasons).");
                    }

                    retList.add(decryptedValue);
                }

                return retList;
            } else {
                throw new RuntimeException("privateKey is NULL when attempting to decrypt!");
            }
        } catch (Exception e) {
            logger.warn("failed to decrypt. assuming plain text ");
            retList.addAll(Arrays.asList(encryptedValues));
            return retList;
        }
    }

    /**
     * @return true only if the param is in the set of enc properties in SecurityConfiguration.
     */
    public static boolean isEncryptedParam(String param) {
        return getEncryptionParameters().contains(param);
    }

    /**
     * @return true only if the param is in the map.
     */
    public static boolean maybeEncryptedJSONParam(String param) {
        Set<String> encParamSet = getEncryptionParameters();
        for (String encParam : encParamSet) {
            if (encParam.startsWith(param))
                return true;
        }

        return false;
    }


    /*
    * Creates the JSONObject response which is expected by the encrypt method
    * in jcryption.js.  This is the public key.
    */
    protected static JSONObject buildPublicKeyJSON(PublicKey pubKey) throws IOException {
        try {
            if (encryption instanceof EncryptionRSA) {
                String exponent  = EncryptionRSA.getPublicKeyExponent(pubKey);
                String modulus   = EncryptionRSA.getPublicKeyModulus(pubKey);
                String maxDigits = String.valueOf(EncryptionRSA.getMaxDigits(getEncryptionKeySize()));

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("e", exponent);
                jsonObj.put("n", modulus);
                jsonObj.put("maxdigits", maxDigits);

                return jsonObj;
            }
            else
                throw new RuntimeException("Encryption algorithm" + getEncryptionType() + " is not implemented.");
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to respond with a key pair.", e);
        }
    }

}
