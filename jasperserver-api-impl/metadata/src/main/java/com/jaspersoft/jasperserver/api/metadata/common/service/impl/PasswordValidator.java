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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.common.crypto.Cipherer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;


/**
 * PasswordValidator is used to compare the password presented by a user during auth to a password stored in the database.
 * During instantiation, a Cipherer must be configured via Spring application context.
 * PasswordValidator uses the Cipherer to encrypt the raw password and compare it to the database one.
 */

public class PasswordValidator implements PasswordEncoder {

	private static final Logger log = LogManager.getLogger(PasswordValidator.class);

    private static Cipherer cipherer;

    /* Checks whether the submitted key for SecretKeySpec in plain text or a Integer represantation of byte sequence.
     * It is configured in Spring environment.
     */
    private boolean keyInPlainText;

    /*
     * the value to be set in Cipherer.keyBytes. It is configured in Spring environment.
     */
    private String secretKey;

    /*
     * the name of the secret-key algorithm to be associated with the given key.
     * the value to be set in Cipherer.keyAlgorithm. It is configured in Spring environment.
     */

    private String secretKeyAlgorithm;

    /*
     * the name of the transformation, e.g., DES/CBC/PKCS5Padding.
     * the value to be set in Cipherer.keyAlgorithm. It is configured in Spring environment.
     */
    private String cipherTransformation;

	/**
     * Constuctor to be called only from Spring framework
     *
     */
	protected PasswordValidator() {
		if ((cipherer == null))  {
			cipherer = new Cipherer();
			if (secretKey != null) cipherer.setKeyBytes(secretKey, keyInPlainText);
			if (cipherTransformation != null) cipherer.setCipherTransformation(cipherTransformation);
			if (secretKeyAlgorithm != null) cipherer.setKeyAlgorithm(secretKeyAlgorithm);
			cipherer.init();
		}
	}

    /**
     * Encrypts the raw password
	 *
     * @param rawPass
     * @return encrypted password
     * @throws org.springframework.dao.DataAccessException
     */
    private String encodePassword(String rawPass) throws DataAccessException {
		try {
			return cipherer.encode(rawPass);
		} catch (Exception ex) {
			log.debug(ex);
			throw new PasswordEncryptionException(ex.getMessage(), ex.getCause());
		}
	}
    
    
    /********** implemented PasswordEncoder METHODS ****************/
	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.encoding.PasswordEncoder#encodePassword(java.lang.String, java.lang.Object)
	 * NOTE: salt will be ignored since we will use the "secretket" defined in Spring configuration
	 */
	public String encodePassword(String rawPass, Object salt) throws DataAccessException {
		return encodePassword(rawPass);
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.encoding.PasswordEncoder#isPasswordValid(java.lang.String, java.lang.String, java.lang.Object)
	 * NOTE: salt will be ignored since we will use the "secretket" defined in Spring configuration
	 */
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
		String pass1 = "" + encPass;
		String pass2 = encodePassword(rawPass);

		return pass1.equals(pass2);
	}
	
	
    /********** SPRING BEAN CALLBACKS ****************/

	/**
	 * @return Returns the cipherTransformation.
	 */
	public String getCipherTransformation() {
		return cipherTransformation;
	}

	/**
	 * @param cipherTransformation The cipherTransformation to set.
	 */
	public void setCipherTransformation(String cipherTransformation) {
		this.cipherTransformation = cipherTransformation;
	}

	/**
	 * @return Returns the keyInPlainText.
	 */
	public boolean isKeyInPlainText() {
		return keyInPlainText;
	}

	/**
	 * @param keyInPlainText The keyInPlainText to set.
	 */
	public void setKeyInPlainText(boolean keyInPlainText) {
		this.keyInPlainText = keyInPlainText;
	}

	/**
	 * @return Returns the secretKey.
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey The secretKey to set.
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @return Returns the secretKeyAlgorithm.
	 */
	public String getSecretKeyAlgorithm() {
		return secretKeyAlgorithm;
	}

	/**
	 * @param secretKeyAlgorithm The secretKeyAlgorithm to set.
	 */
	public void setSecretKeyAlgorithm(String secretKeyAlgorithm) {
		this.secretKeyAlgorithm = secretKeyAlgorithm;
	}


}

