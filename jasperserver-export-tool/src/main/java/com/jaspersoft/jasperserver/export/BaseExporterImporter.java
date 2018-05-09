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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.export.modules.Attributes;
import com.jaspersoft.jasperserver.export.modules.ModuleRegister;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */

public class BaseExporterImporter {
	
	protected static final CommandOut commandOut = CommandOut.getInstance();

	private static final ThreadLocal<Tenant> tenant = new ThreadLocal<Tenant>();

	private String indexFilename;
	private String indexRootElementName;
	private String indexModuleElementName;
	private String indexModuleIdAttributeName;
	private String brokenDependenceFilename;
	private String brokenDependenceRootElementName;
	private String resourceElementName;
	private ModuleRegister moduleRegister;
	private CharacterEncodingProvider encodingProvider;
	private String propertyElementName;
	private String propertyNameAttribute;
	private String propertyValueAttribute;
    private String jsVersion;
    public final String VERSION_ATTR = "jsVersion";

    public String getIndexRootElementName() {
		return indexRootElementName;
	}

	public void setIndexRootElementName(String indexRootElement) {
		this.indexRootElementName = indexRootElement;
	}

	public String getIndexModuleElementName() {
		return indexModuleElementName;
	}

	public void setIndexModuleElementName(String indexModuleElementName) {
		this.indexModuleElementName = indexModuleElementName;
	}

	public String getIndexModuleIdAttributeName() {
		return indexModuleIdAttributeName;
	}

	public void setIndexModuleIdAttributeName(String indexModuleIdAttributeName) {
		this.indexModuleIdAttributeName = indexModuleIdAttributeName;
	}

	public ModuleRegister getModuleRegister() {
		return moduleRegister;
	}

	public void setModuleRegister(ModuleRegister moduleRegister) {
		this.moduleRegister = moduleRegister;
	}

	public CharacterEncodingProvider getEncodingProvider() {
		return encodingProvider;
	}
	
	public String getCharacterEncoding() {
		return encodingProvider.getCharacterEncoding();
	}

	public void setEncodingProvider(CharacterEncodingProvider encodingProvider) {
		this.encodingProvider = encodingProvider;
	}

	public String getIndexFilename() {
		return indexFilename;
	}

	public void setIndexFilename(String indexFilename) {
		this.indexFilename = indexFilename;
	}

	public String getPropertyElementName() {
		return propertyElementName;
	}

	public void setPropertyElementName(String propertyElementName) {
		this.propertyElementName = propertyElementName;
	}

	public String getPropertyNameAttribute() {
		return propertyNameAttribute;
	}

	public void setPropertyNameAttribute(String propertyNameAttribute) {
		this.propertyNameAttribute = propertyNameAttribute;
	}

	public String getPropertyValueAttribute() {
		return propertyValueAttribute;
	}

	public void setPropertyValueAttribute(String propertyValueAttribute) {
		this.propertyValueAttribute = propertyValueAttribute;
	}

    public String getJsVersion() {
        return jsVersion;
    }

    public void setJsVersion(String jsVersion) {
        this.jsVersion = jsVersion;
    }

	public void setBrokenDependenceFilename(String brokenDependenceFilename) {
		this.brokenDependenceFilename = brokenDependenceFilename;
	}

	public String getBrokenDependenceFilename() {
		return brokenDependenceFilename;
	}

	public void setBrokenDependenceRootElementName(String brokenDependenceRootElementName) {
		this.brokenDependenceRootElementName = brokenDependenceRootElementName;
	}

	public String getBrokenDependenceRootElementName() {
		return brokenDependenceRootElementName;
	}

	public void setResourceElementName(String resourceElementName) {
		this.resourceElementName = resourceElementName;
	}

	public String getResourceElementName() {
		return resourceElementName;
	}

	protected static String getAuthenticatedTenantId() {
		String result = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			if (auth.getPrincipal() instanceof TenantQualified) {
				result = ((TenantQualified) auth.getPrincipal()).getTenantId();
			}
		}

		if (result == null) {
			return TenantService.ORGANIZATIONS;
		}

		return result;
	}

	public static Tenant getTenant() {
		return tenant.get();
	}

	public static void setTenant(Tenant t) {
		tenant.set(t);
	}

	/**
	 * Returns a map, which links old tenant id to new unique tenant id
	 */
	protected static Map<String, String> getNewGeneratedTenantIds(Attributes contextAttributes) {
		final String ATTRIBUTE_NAME = "newUniqueTenantIdsMap";
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) contextAttributes.getAttribute(ATTRIBUTE_NAME);
		if (map == null) {
			map = new HashMap<String, String>();
			contextAttributes.setAttribute(ATTRIBUTE_NAME, map);
		}

		return map;
	}
}
