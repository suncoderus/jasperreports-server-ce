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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeCategory;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityPersistenceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Transfer all properties from GlobalPropertiesList resource, or from other ListOfValues sources (i.e. resource
 * in zip-file) to JIProfileAttribute table.
 * After GlobalPropertiesList resource will be deleted
 *
 * @author Vlad Zavadskii
 * @version $Id$
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GlobalPropertiesListUpgradeExecutor {
    protected static final Log log = LogFactory.getLog(GlobalPropertiesListUpgradeExecutor.class);
    private RepositoryService repositoryService;
    private ProfileAttributeService profileAttributeService;
    private ResourceFactory resourceFactory;
    private ObjectPermissionService objectPermissionService;
    private List<ObjectPermission> defaultAttributesRootPermission;
    private UserAuthorityPersistenceService userAuthorityPersistenceService;

    public static final String PATH =
            Folder.SEPARATOR + "properties" +
            Folder.SEPARATOR + "GlobalPropertiesList";

    public void upgrade() {
        ListOfValues resource = (ListOfValues) repositoryService.getResource(null, PATH);
        upgrade(resource, true);
    }

    public void upgrade(ListOfValues resource, boolean includeServerSettings) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        if (resource != null) {
            UpgradeCallable callable = new UpgradeCallable(resource.getValues(), includeServerSettings);
            executor.submit(callable);
        }

        if (objectPermissionService != null && defaultAttributesRootPermission != null) {
            executor.submit(new InsertingPermissionCallable());
        }

        executor.shutdown();
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setProfileAttributeService(ProfileAttributeService profileAttributeService) {
        this.profileAttributeService = profileAttributeService;
    }

    public void setObjectPermissionService(ObjectPermissionService objectPermissionService) {
        this.objectPermissionService = objectPermissionService;
    }

    public void setDefaultAttributesRootPermission(List<ObjectPermission> defaultAttributesRootPermission) {
        this.defaultAttributesRootPermission = defaultAttributesRootPermission;
    }

    public void setUserAuthorityPersistenceService(UserAuthorityPersistenceService userAuthorityPersistenceService) {
        this.userAuthorityPersistenceService = userAuthorityPersistenceService;
    }

    public void setResourceFactory(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    private class UpgradeCallable implements Callable<Void> {
        private ListOfValuesItem[] values;
        private boolean includeServerSettings;

        public UpgradeCallable(ListOfValuesItem[] values, boolean includeServerSettings) {
            this.values = values;
            this.includeServerSettings = includeServerSettings;
        }

        @Override
        public Void call() throws Exception {
            try {
                Object principal = ProfileAttributeCategory.SERVER.getPrincipal(resourceFactory);
                for (ListOfValuesItem item : values) {
                    // TODO: re-implement without getting changer names, remove "getChangerName" method
                    // Ignore server settings
                    if (!includeServerSettings) {
                        String changerName = profileAttributeService.getChangerName(item.getLabel());
                        // For custom attribute changerName will always be null
                        if (changerName != null && !changerName.equals("custom")) continue;
                    }

                    ProfileAttribute profileAttribute = profileAttributeService.newProfileAttribute(null);
                    profileAttribute.setAttrName(item.getLabel());
                    profileAttribute.setAttrValue((String) item.getValue());
                    profileAttribute.setPrincipal(principal);
                    profileAttributeService.putProfileAttribute(null, profileAttribute);
                }

                profileAttributeService.applyProfileAttributes();

                if (repositoryService.resourceExists(null, PATH)) {
                    repositoryService.deleteResource(null, PATH);
                }
            } catch (Exception e) {
                log.error("Unexpected runtime error occurred during processing GlobalPropertiesList upgrade task", e);
            }

            return null;
        }
    }

    private class InsertingPermissionCallable implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            try {
                ExecutionContext context = getExecutionContext();
                for (ObjectPermission permission : defaultAttributesRootPermission) {
                    Object recipient = permission.getPermissionRecipient();
                    if (recipient instanceof TenantQualified) {
                        String tenantId = ((TenantQualified) recipient).getTenantId() != null ?
                                ((TenantQualified) recipient).getTenantId() : TenantService.ORGANIZATIONS;
                        if (userAuthorityPersistenceService.
                                getPersistentTenants(Arrays.asList(tenantId)).size() == 0) {
                            break;
                        }
                    }
                    if (userAuthorityPersistenceService.getPersistentObject(recipient) != null) {
                        objectPermissionService.putObjectPermission(context, permission);
                    }
                }
            } catch (Exception e) {
                log.error("Unexpected error occurred during inserting administrator root attributes permission", e);
            }

            return null;
        }
    }

    private ExecutionContext getExecutionContext() {
        ExecutionContextImpl context = new ExecutionContextImpl();
        context.setLocale(LocaleContextHolder.getLocale());
        context.setTimeZone(TimeZoneContextHolder.getTimeZone());
        context.getAttributes().add(ObjectPermissionService.PRIVILEGED_OPERATION);
        return context;
    }
}
