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

import com.jaspersoft.jasperserver.core.util.StringUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Originally called JCryption, but since it only does RSA encryption
 * I renamed it to EncryptionRSA, with the idea that in the future there
 * will be other implementations such as EncryptionAES, EncryptionTwofish, etc.
 *
 *
 * jCryption support (www.jcryption.org) - RSA encryption
 *
 * @author Michal Franc, Jan Novotný, FG Forrest, donated to the www.jcryption.org
 * @version $Id: EncryptionRSA.java 22754 2012-03-23 08:09:40Z sergey.prilukin $
 */
public class EncryptionRSA implements Encryption {
    private static final Pattern SPLIT_BY_WHITESPACE = Pattern.compile("\\s");
    private static final Pattern SPLIT_BY_AMPERSAND = Pattern.compile("&");
    private static final Pattern SPLIT_BY_EQUALITY = Pattern.compile("=");
    public static final int RADIX_16 = 16;

    /**
     * {@inheritDoc}
     */
    public KeyPair generateKeypair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            return kpg.generateKeyPair();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA algorithm not supported", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String decrypt(String encrypted, PrivateKey privateKey)
    {
        Cipher dec;
        try
        {
            dec = Cipher.getInstance("RSA/NONE/NoPadding", new BouncyCastleProvider());
            dec.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (GeneralSecurityException e)
        {
            throw new RuntimeException("RSA algorithm not supported", e);
        }
        String[] blocks = SPLIT_BY_WHITESPACE.split(encrypted);
        StringBuilder result = new StringBuilder();
        try {
            for (int i = blocks.length - 1; i >= 0; i--) {
                byte[] data = StringUtil.hexStringToByteArray(blocks[i]);
                byte[] decryptedBlock = dec.doFinal(data);
                result.append(new String(decryptedBlock));
            }
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Decrypt error", e);
        }
        return result.toString();
    }

    /**
     * Parse url string into the map. Performs URL decode with passed encoding settings.
     * Native chars that doesn't get encoded on the client with encodeURI gets damaged during crypting phase.
     *
     * @param url       value to parse.
     * @param encoding  encoding value.
     * @return Map with param name, value pairs.
     */
    public static Map<String, Object> parse(String url, String encoding)
    {
        try
        {
            String urlToParse = URLDecoder.decode(url, encoding);
            return parseEncryptedData(urlToParse);
        } catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Unknown encoding.", e);
        }
    }

    /**
     * Parses url string into the map of parameters. Map can contain only String or String[] depending on whether
     * parameter is duplicated in input string or not.
     *
     * @param data query param string. Params separated by &
     * @return
     */
    public static Map<String, Object> parseEncryptedData(String data) {
        String[] params = SPLIT_BY_AMPERSAND.split(data);
        Map<String, Object> parsed = new HashMap<String, Object>();
        for (String param : params)
        {
            String[] p = SPLIT_BY_EQUALITY.split(param);
            String name = p.length > 0 ? p[0] : null;
            String value = p.length > 1 ? p[1] : "";
            if (name != null)
            {
                if (parsed.containsKey(name))
                {
                    Object existingValue = parsed.get(name);
                    if (existingValue instanceof String[])
                    {
                        String[] existingValues = (String[]) existingValue;
                        String[] combinedValues = new String[existingValues.length + 1];
                        System.arraycopy(existingValues, 0, combinedValues, 0, existingValues.length);
                        combinedValues[existingValues.length] = value;
                        parsed.put(name, combinedValues);
                    }
                    else
                    {
                        parsed.put(name, new String[]{(String) existingValue, value});
                    }
                }
                else
                {
                    parsed.put(name, value);
                }
            }
        }
        return parsed;
    }

    /**
     * Return public RSA key modulus
     *
     * @param  key RSA public key
     * @return modulus value as hex string
     */
    public static String getPublicKeyModulus(PublicKey key) {
        return ((RSAPublicKey) key).getModulus().toString(RADIX_16);
    }

    /**
     * Return public RSA key exponent
     *
     * @param key RSA public key
     * @return public exponent value as hex string
     */
    public static String getPublicKeyExponent(PublicKey key) {
        return ((RSAPublicKey) key).getPublicExponent().toString(RADIX_16);
    }

    /**
     * Max block size with given key length
     *
     * @param keyLength length of key
     * @return numeber of digits
     */
    public static int getMaxDigits(int keyLength) {
        return keyLength * 2 / RADIX_16 + 3;
    }

}
