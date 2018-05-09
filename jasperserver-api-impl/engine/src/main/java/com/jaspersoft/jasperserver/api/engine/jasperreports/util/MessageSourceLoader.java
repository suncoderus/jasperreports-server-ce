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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class which helps to load instance of {@link MessageSource} from {@link ResourceContainer}.
 *
 * @author Sergey Prilukin
 */
public class MessageSourceLoader {

    public static MessageSource loadMessageSource(ExecutionContext exContext, ResourceContainer resourceContainer, RepositoryService repository) {
        List<ResourceReference> resources = resourceContainer.getResources();

        Map<String, RepositoryResourceKey> map = new LinkedHashMap<String, RepositoryResourceKey>();
        Set<String> baseNames = new LinkedHashSet<String>();

        for (ResourceReference resRef : resources) {
            Resource genericResource = getResource(exContext, repository, resRef);
            if (!isBundle(genericResource)) {
                continue;
            }

            FileResource resource = (FileResource) genericResource;
            String fileName = resource.getName();

            map.put(fileName, new RepositoryResourceKey(resource));
            String baseName = ResourceBundleHelper.getBaseName(fileName);

            if (!baseName.isEmpty()) {
                baseNames.add(baseName);
            }
        }

        if (map.isEmpty()) {
            return null;
        } else {
            setupThreadRepositoryContext(exContext, repository);

            JasperResourceBundleMessageSource messageSource = new JasperResourceBundleMessageSource();
            messageSource.setBundleClassLoader(
                    new RepositoryResourceClassLoader(
                        Thread.currentThread().getContextClassLoader(), map, false, RepositoryUtil.getThreadRepositoryContext()));
            messageSource.setBasenames(baseNames.toArray(new String[baseNames.size()]));

            return messageSource;
        }
    }

    public static List<String> getBaseNames(ExecutionContext exContext, ResourceContainer resourceContainer, RepositoryService repository) {
        List<ResourceReference> resources = resourceContainer.getResources();
        List<String> baseNames = new ArrayList<String>();

        for (ResourceReference resRef : resources) {
            Resource genericResource = getResource(exContext, repository, resRef);
            if (!isBundle(genericResource)) {
                continue;
            }
            baseNames.add(ResourceBundleHelper.getBaseName(genericResource.getName()));
        }
        return baseNames;
    }

    private static boolean isBundle(Resource resource) {
        if (!isFileResource(resource)) {
            return false;
        }

        FileResource fileResource = (FileResource) resource;
        String fileName = fileResource.getName();
        String fileType = fileResource.getFileType();

        return isBundle(fileName, fileType);
    }

    private static boolean isFileResource(Resource genericResource) {
        return FileResource.class.getName().equals(genericResource.getResourceType());
    }

    private static boolean isBundle(String fileName, String fileType) {
        return (fileType != null)
                ? fileType.equals(FileResource.TYPE_RESOURCE_BUNDLE)
                : ResourceBundleHelper.isBundle(fileName);
    }

    private static Resource getResource(
            ExecutionContext exContext, RepositoryService repository, ResourceReference resourceReference) {

        return resourceReference.isLocal()
                ? resourceReference.getLocalResource()
                : repository.getResource(exContext, resourceReference.getReferenceURI());
    }

    private static void setupThreadRepositoryContext(ExecutionContext exContext, RepositoryService repository) {
        if (RepositoryUtil.getThreadRepositoryContext() == null) {
            RepositoryContext rc = new RepositoryContext();
            rc.setRepository(repository);
            rc.setExecutionContext(exContext);
            RepositoryUtil.setThreadRepositoryContext(rc);
        }
    }
}
