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

package com.jaspersoft.jasperserver.export.modules.mt;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DefaultMultiTenancyConfiguration.java 15916 2009-02-20 21:26:54Z schubar $
 */
public class DefaultTenantExportConfiguration
{

	private String rootTenantId;
	private String qualifiedNameSeparator;
	private int qualifiedNameSeparatorLength;
    private String defaultThemeName = "default";

	public String getQualifiedNameSeparator()
	{
		return qualifiedNameSeparator;
	}

	public void setQualifiedNameSeparator(String qualifiedNameSeparator)
	{
		this.qualifiedNameSeparator = qualifiedNameSeparator;
		this.qualifiedNameSeparatorLength = this.qualifiedNameSeparator == null ? 0
				: this.qualifiedNameSeparator.length();
	}

	public String makeQualifiedName(String tenantId, String name)
	{
		String qName;
		if (tenantId == null)
		{
			qName = name;
		}
		else
		{
			qName = name + qualifiedNameSeparator + tenantId;
		}
		return qName;
	}

	public void setRootTenantId(String rootTenantId)
	{
		this.rootTenantId = rootTenantId;
	}

	public String getRootTenantId()
	{
		return rootTenantId;
	}

    public String getDefaultThemeName() {
        return defaultThemeName;
    }

    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }

}
