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

/**
 * This is a non-cypher.  It does not do any token cryptography.
 * It's just a place-holder for the development phase.
 * In production, a real cypher implementation must be used
 *
 * User: dlitvak
 * Date: 12/18/13
 */
public class DevelopmentPlainTextNonCipher implements CipherI {
	@Override
	public String encrypt(String plainText) {
		return plainText;
	}

	@Override
	public String decrypt(String cipherText) {
		return cipherText;
	}
}
