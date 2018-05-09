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

import java.security.KeyPair;
import java.security.PrivateKey;

/**
 * Javadoc and signatures originally from the JCryption source, with some minor modifications.
 * Uses RSA for now.
 *
 * @author norm macaraeg
 * @see "https://github.com/novoj/jCryption4J" and "jcryption.org"
 */
public interface Encryption
{
    /**
     * Generates the Keypair with the given keyLength.
     *
     * @param keyLength length of key.
     * @return KeyPair object.
     * @throws RuntimeException if the RSA algorithm not supported.
     */
    public KeyPair generateKeypair(int keyLength);

    /**
     * Decrypts a given string with the RSA keys
     *
     * @param encrypted full encrypted text.
     * @param privateKey RSA private key.
     * @return decrypted text.
     * @throws RuntimeException if the RSA algorithm not supported or decrypt operation failed.
     */
    public String decrypt(String encrypted, PrivateKey privateKey);
}
