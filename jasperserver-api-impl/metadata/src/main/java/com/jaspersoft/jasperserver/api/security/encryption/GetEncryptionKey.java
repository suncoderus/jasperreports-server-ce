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
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.PublicKey;

import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.getEncryptionKeySize;

/**
 * Created by IntelliJ IDEA.
 * User: dlitvak
 * Date: 3/5/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Obtain the public encryption key from server
 */
public class GetEncryptionKey extends HttpServlet {
    private static final Logger logger = Logger.getLogger(GetEncryptionKey.class);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = null;
        try {
            out = res.getWriter();

            if (SecurityConfiguration.isEncryptionOn()) {
                KeyPair keyPair = (KeyPair) req.getSession().getAttribute(EncryptionManager.KEYPAIR_SESSION_KEY);
                final boolean dynamicKeyGeneration = SecurityConfiguration.isEncryptionDynamicKeyGeneration();
                if (dynamicKeyGeneration || keyPair == null) {
                    keyPair = EncryptionManager.generateKeys(dynamicKeyGeneration);
                    req.getSession().setAttribute(EncryptionManager.KEYPAIR_SESSION_KEY, keyPair);
                }

                JSONObject jsonObj = EncryptionManager.buildPublicKeyJSON(keyPair.getPublic());
                out.print(jsonObj.toString());
            }
            else
                out.print("{Error: Key generation is off}");

        }
        catch (Exception e) {
            logger.error("Unable to generate keys.", e);
            out.print("{Error: Exception during key generation: " + e.getMessage() + "}");
        }
        finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

}
