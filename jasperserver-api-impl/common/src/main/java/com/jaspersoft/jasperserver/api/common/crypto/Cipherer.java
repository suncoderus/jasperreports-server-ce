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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

/**
 * This class provides the utilities of a cryptographic cipher for encryption and decryption.
 *
 */
public class Cipherer implements InitializingBean {

	private static Log log = LogFactory.getLog(Cipherer.class);
	
    
	private static final byte[] DEFAULT_KEY_BYTES = {(byte)0xC8, (byte)0x43, (byte)0x29, (byte)0x49, 
		                               (byte)0xAE, (byte)0x25, (byte)0x2F, (byte)0xA1, 
		                               (byte)0xC1, (byte)0xF2, (byte)0xC8, (byte)0xD9, 
		                               (byte)0x31, (byte)0x01, (byte)0x2C, (byte)0x52, 
		                               (byte)0x54, (byte)0x0B, (byte)0x5E, (byte)0xEA, 
		                               (byte)0x9E, (byte)0x37, (byte)0xA8, (byte)0x61 };
	
	//Create an 8-byte initialization vector
	//TODO Static IV is bad
	private static final byte[] INIT_VECTOR_8 = { (byte)0x8E, (byte)0x12, (byte)0x39, (byte)0x9C,
			(byte)0x07, (byte)0x72, (byte)0x6F, (byte)0x5A};
//	private static final Random NONCE_GEN = new SecureRandom(SecureRandom.getSeed(16));

	//Create an 16-byte initialization vector
	private static final byte[] INIT_VECTOR_16 =
			{(byte)0x8b, (byte)0x48, (byte)0x10, (byte)0x03, (byte)0x5d, (byte)0xdf, (byte)0xf9, (byte)0xac,
			(byte)0x17, (byte)0xf2, (byte)0xbd, (byte)0x64, (byte)0xb7, (byte)0x51, (byte)0xc0, (byte)0x29};

	private Cipher E_CIPHER = null;
	private Cipher D_CIPHER = null;
	
	private static final String DEFAULT_CIPHER_TRANSFORMATION = "DESede/CBC/PKCS5Padding";
	private static final String DEFAULT_KEY_ALGORITHM = "DESede";
	
	private byte[] keyBytes;
	private String cipherTransformation;
	private String keyAlgorithm;
	
    /**
     * 
     */
    public Cipherer() {
    	keyBytes = DEFAULT_KEY_BYTES;
    	cipherTransformation = DEFAULT_CIPHER_TRANSFORMATION;
    	keyAlgorithm = DEFAULT_KEY_ALGORITHM;
    }

	
    /**
     * Initializes the encoder and decoder with the given parameters
	 *
     * @param isPlainText
     */
    public void init(String inCipherTransformation, String inKeyAlgorithm, String inKeyBytes, boolean isPlainText) {
    	cipherTransformation = inCipherTransformation;
    	keyAlgorithm= inKeyAlgorithm;
    	setKeyBytes(inKeyBytes, isPlainText);
    	init();
    }
    
    /**
	 * Initializes the encoder and decoder. 
	 * Note: The vaues of CIPHER_TRANSFORMATION, KEY_BYTES, KEY_ALGORITHM should be set before calling this method,
	 *       otherwise it will use the default values.
	 */
	public void init() {
		try {
/*
			byte[] nonce = new byte[16];
			NONCE_GEN.nextBytes(nonce);
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(nonce);
*/
			AlgorithmParameterSpec paramSpec = cipherTransformation.toUpperCase().startsWith("AES") ?
					new IvParameterSpec(INIT_VECTOR_16) : new IvParameterSpec(INIT_VECTOR_8);

			E_CIPHER = Cipher.getInstance(cipherTransformation);
			D_CIPHER = Cipher.getInstance(cipherTransformation);

			SecretKeySpec spec = new SecretKeySpec(keyBytes, keyAlgorithm);

			// CBC requires an initialization vector
			E_CIPHER.init(Cipher.ENCRYPT_MODE, spec, paramSpec);
			D_CIPHER.init(Cipher.DECRYPT_MODE, spec, paramSpec);
		} catch (Exception e) {
			log.error("Cipher init failed", e);
			throw new RuntimeException(e);
		}
	}
	
	/** Encodes and hexifies the given content */
	public String encode(String content){
		try {
			if (content == null) content = "";
			return hexify(encode(content.getBytes("UTF-8")));
		} catch (Exception ex) {
			log.warn("Unsuccessfull password encryption", ex);
			return hexify(encode(content.getBytes()));
		}
	}
	
	/** Encodes the given content*/
	public byte[] encode(byte[] content){
		try {
			return E_CIPHER.doFinal(content);
		} catch (Exception ex) {
			log.warn("Unsuccessfull password decryption", ex);
			return content;
		}
	}
	
	/** Dehexifies and decodes the given content
	 * * @param content string to be decoded
	 * @return the decoded content.
	 */
	public String decode(String content) {
		try{ 
			if (content == null) return null;
			return new String(decode(dehexify(content)), "UTF-8");
		} catch (Exception ex) {
			log.warn("Password decrypted unsuccesfully.", ex);
			return new String(decode(dehexify(content)));
		}
	}
	
	/** Decodes the given content*/
	public byte[] decode(byte[] content) {
		try {
			return D_CIPHER.doFinal(content);
		} catch (Exception ex) {
			log.warn("Unsuccessfull password decryption", ex);
			return content;
		}
	}
	
	/**
	 * Decodes the given content if the booleanValue is "true" (case insensitive). 
	 * (Utility Function) 
	 * @param content string to be decoded
	 * @param booleanValue specifies whether content needs to be decoded
	 * @return the decoded content if the booleanValue is "true" (case insensitive). Otherwise it just returns content.
	 */
	public String decode(String content, Object booleanValue) {
		if ((booleanValue == null) || !(booleanValue instanceof String) || (content == null) )
			return content;
		boolean isEncrypted = new Boolean((String) booleanValue).booleanValue();
		if (isEncrypted) return decode(content);
		return content;
	}
	
	
	//
    // used in hexifying
    //
	private static final char[] hexChars ={ '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Convert a byte array response to a hex string
	 */
    public static String hexify(byte[] data)
    {
        StringBuffer hex = new StringBuffer();

        for (int i = 0; i < data.length; i++)
        {
            int highBits = ((int)data[i] & 0x000000F0) >> 4;
            int lowBits  = ((int)data[i] & 0x0000000F);
            hex.append(hexChars[highBits]).append(hexChars[lowBits]);
        }

        return(hex.toString());
    }
    
	/**
    * Convert a hex string response to a byte array
    */
    public static byte[] dehexify(String data)
    {
        byte[] bytes = new byte[data.length()/2];
        
        for (int i = 0; i < bytes.length; i++) {
           bytes[i] = (byte) Integer.parseInt(data.substring(2*i, (2*i)+2), 16);
        }

        return bytes;
    }
    


	/**
	 * @param inKeyBytes The KEY_BYTES to set.
	 * @param isPlainText Whether key_bytes is plain text or a represantation of byte sequence
	 */
	public void setKeyBytes(String inKeyBytes, boolean isPlainText) {
		if (isPlainText) {
			keyBytes = inKeyBytes.getBytes();
		}
		else {
			setKeyBytes(inKeyBytes);
		}

	}
		
	public void setKeyBytes(String inKeyBytes) {
		String[] keyStringArr = inKeyBytes.split("\\s+");
		keyBytes = new byte[keyStringArr.length];
		for (int i=0; i< keyStringArr.length; i++) {
			keyBytes[i] = Integer.decode(keyStringArr[i]).byteValue();
	    }
	}


	/**
	 * @param cipherTransformation The cipherTransformation to set.
	 */
	public void setCipherTransformation(String cipherTransformation) {
		this.cipherTransformation = cipherTransformation;
	}

	/**
	 * @param keyAlgorithm The keyAlgorithm to set.
	 */
	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	/**
	 * Invoked by a BeanFactory after it has set all bean properties supplied
	 * (and satisfied BeanFactoryAware and ApplicationContextAware).
	 * <p>This method allows the bean instance to perform initialization only
	 * possible when all bean properties have been set and to throw an
	 * exception in the event of misconfiguration.
	 *
	 * @throws Exception in the event of misconfiguration (such
	 *                   as failure to set an essential property) or if initialization fails.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.keyBytes, "keyBytes has not been set in " + Cipherer.class);
		Assert.notNull(this.keyAlgorithm, "keyAlgorithm has not been set in " + Cipherer.class);
		Assert.notNull(this.cipherTransformation, "cipherTransformation has not been set in " + Cipherer.class);
		init();
	}
}
