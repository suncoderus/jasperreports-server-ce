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

package example.cdspro;

import java.util.Collections;
import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.util.SecretsProvider;
import net.sf.jasperreports.util.SecretsProviderFactory;

/**
 * This is a factory which creates an extension registry which creates a provider factory which creates a provider which creates a secret.
 * When asked for {@link SecretsProviderFactory} this eventually returns a secret provider which just returns whatever value is passed to it as the parameter in {@link SecretsProvider#getSecret(String)}.
 */
public class NoopSecretsProviderRegistryFactory implements ExtensionsRegistryFactory {

    private static final ExtensionsRegistry noopSecretsProviderExtensionsRegistry = new NoopSecretsProviderExtensionsRegistry();

    @Override
    public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) {
        return noopSecretsProviderExtensionsRegistry;
    }

    private static final class NoopSecretsProviderExtensionsRegistry implements ExtensionsRegistry {

        private static final NoopSecretsProviderFactory noopSecretsProviderFactory = new NoopSecretsProviderFactory();

        @SuppressWarnings("unchecked")
        @Override
        public <T> List<T> getExtensions(Class<T> extensionType) {
            return (List<T>) (SecretsProviderFactory.class.equals(extensionType) ? Collections.singletonList(noopSecretsProviderFactory) : null);
        }
    }

    private static final class NoopSecretsProviderFactory implements SecretsProviderFactory {

        private static final SecretsProvider noopSecretsProvider = new NoopSecretsProvider();

        @Override
        public SecretsProvider getSecretsProvider(String category) {
            return noopSecretsProvider;
        }
    }

    private static final class NoopSecretsProvider implements SecretsProvider {

        @Override
        public String getSecret(String key) {
            return key;
        }

        @Override
        public boolean hasSecret(String key) {
            return true;
        }
    }
}
