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
 * The latest version of jcryption.js uses AES encryption, but JCryption for Java
 * has not kept up with the AES technique to date.  I suspect they might so this is a place holder
 * for that.  DO NOT USE at this time!
 *
 * @author norm
 * @since 2/9/2012
 */
public class EncryptionAES implements Encryption {
    /**
     * {@inheritDoc}
     */
    public KeyPair generateKeypair(int keyLength) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public String decrypt(String encrypted, PrivateKey privateKey) {
        throw new RuntimeException("Not implemented");
    }
}
