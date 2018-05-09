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
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: dlitvak
 * Date: 3/8/12
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class EncryptionManagerTest {
    private EncryptionRSA encryption = new EncryptionRSA();
    private EncryptionManager encMgr = new EncryptionManager();

    @Test
    public void testDecryptDynamicKey() throws Exception {
        KeyPair keyPair = EncryptionManager.generateKeys(true);
        Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding", new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        String testStr = "test";
        byte[] encArr = cipher.doFinal(testStr.getBytes());
        String encTestStr = StringUtil.byteArrayToHexString(encArr);

        List<String> resStrList = encMgr.decrypt(keyPair.getPrivate(), encTestStr);
        Assert.assertTrue("", resStrList.size() == 1 && resStrList.get(0).equals(testStr));
    }

    @Test
    public void testDecryptStaticKey() throws Exception {
        KeyPair keyPair = EncryptionManager.generateKeys(false);
        Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding", new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        String testStr = "test2";
        byte[] encArr = cipher.doFinal(testStr.getBytes());
        String encTestStr = StringUtil.byteArrayToHexString(encArr);

        List<String> resStrList = encMgr.decrypt(keyPair.getPrivate(), encTestStr);
        Assert.assertTrue("", resStrList.size() == 1 && resStrList.get(0).equals(testStr));
    }
}
