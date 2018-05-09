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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.azuresql;

import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClientBuilder;
import org.fusesource.hawtbuf.ByteArrayInputStream;

import com.google.common.base.MoreObjects;
import com.microsoft.windowsazure.credentials.SubscriptionCloudCredentials;
import com.microsoft.windowsazure.management.sql.SqlManagementClient;
import com.microsoft.windowsazure.management.sql.SqlManagementClientImpl;

/**
 * A builder for {@link SqlManagementClient}.
 */
class SqlManagementClientBuilder {

    private String subscriptionId;
    private byte[] keyStoreBytes;
    private char[] keyStorePassword;
    private String keyStoreType = KeyStore.getDefaultType();

    public SqlManagementClientBuilder subscriptionId(String subscriptionId) {
        checkArgNotEmpty(subscriptionId, "subscriptionId");
        this.subscriptionId = subscriptionId;
        return this;
    }

    public SqlManagementClientBuilder keyStoreBytes(byte[] keyStoreBytes) {
        checkArgNotEmpty(keyStoreBytes, "keyStoreBytes");
        this.keyStoreBytes = keyStoreBytes;
        return this;
    }

    public SqlManagementClientBuilder keyStorePassword(char[] keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return this;
    }

    public SqlManagementClientBuilder keyStoreType(String keyStoreType) {
        checkArgNotEmpty(keyStoreType, "keyStoreType");
        this.keyStoreType = keyStoreType;
        return this;
    }

    /**
     * Reads KeyStore and instantiates an Azure SQL Management Client.
     * @return client, never <code>null</code>
     */
    public SqlManagementClient build() {
        checkStateNotEmpty(subscriptionId, "subscriptionId");
        checkStateNotEmpty(keyStoreType, "keyStoreType");
        checkStateNotEmpty(keyStoreBytes, "keyStoreBytes");
        try {
            KeyStore keyStore = createKeystore();
            HttpClientBuilder httpClientBuilder = createHttpClientBuilder(keyStore);
            return new SqlManagementClientImpl(httpClientBuilder, null/*we're not calling async methods*/, new Creds(subscriptionId));
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate SqlManagementClient"
                    + "; subscriptionId=" + subscriptionId
                    + ", keyStoreType=" + keyStoreType, e);
        }
    }

    private HttpClientBuilder createHttpClientBuilder(KeyStore keyStore) throws Exception {
        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, keyStorePassword).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory);
        return httpClientBuilder;
    }

    private void checkArgNotEmpty(String arg, String argName) {
        if (arg == null || arg.isEmpty()) {
            throw new IllegalArgumentException(argName + " must not be empty");
        }
    }

    private void checkStateNotEmpty(String arg, String argName) {
        if (arg == null || arg.isEmpty()) {
            throw new IllegalStateException(argName + " must not be empty");
        }
    }

    private void checkArgNotEmpty(byte[] arg, String argName) {
        if (arg == null || arg.length == 0) {
            throw new IllegalStateException(argName + " must not be empty");
        }
    }

    private void checkStateNotEmpty(byte[] arg, String argName) {
        if (arg == null || arg.length == 0) {
            throw new IllegalStateException(argName + " must not be empty");
        }
    }

    private KeyStore createKeystore() throws Exception {
        KeyStore keystore = KeyStore.getInstance(keyStoreType);
        keystore.load(new ByteArrayInputStream(keyStoreBytes), keyStorePassword);
        return keystore;
    }

    /**
     * Internal class just to pass subscriptionId into {@link SqlManagementClientImpl}.
     */
    private static final class Creds extends SubscriptionCloudCredentials {

        private final String subscriptionId;

        public Creds(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }

        @Override
        public String getSubscriptionId() {
            return subscriptionId;
        }

        @Override
        public <T> void applyConfig(String profile, Map<String, Object> properties) {
            // do nothing
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("subscriptionId", subscriptionId).toString();
    }

}
