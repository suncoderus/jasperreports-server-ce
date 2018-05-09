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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

/**
 * 
 * @author tkavanagh
 * @version $Id$
 */
public class ExecutionContextImpl implements ExecutionContext, Serializable {

    public static final String EXECUTE_OVERRIDE = "execute";
	private Locale locale;
	private TimeZone timeZone;
    private List attributes = new ArrayList();

    public ExecutionContextImpl() {
    }

    public ExecutionContextImpl(Locale locale) {
        this.locale = locale;
    }

    /**
     * Given an existing execution context, return one that has the PermissionOverride object 
     * for execute-only perms in its attributes. Return the same object if it's already there.
     * @param originalContext
     * @return
     */
    public static ExecutionContext getRuntimeExecutionContext(ExecutionContext originalContext) {
    	// do you already have a permission override? Then you don't need to do anything
    	if (originalContext != null) {
    		for (Object attr : originalContext.getAttributes()) {
    			if (attr instanceof PermissionOverride && ((PermissionOverride) attr).getOverrideId().equals(EXECUTE_OVERRIDE)) {
    				return originalContext;
    			}
    		}
    	}
    	ExecutionContextImpl exContext = new ExecutionContextImpl();
        PermissionOverride override = new PermissionOverride(EXECUTE_OVERRIDE);
        exContext.getAttributes().add(override);
    	if (originalContext != null) {
    		exContext.setLocale(originalContext.getLocale());
    		exContext.setTimeZone(originalContext.getTimeZone());
    		exContext.getAttributes().addAll(originalContext.getAttributes());
    	}
        return exContext;

    }
    public static ExecutionContext getRuntimeExecutionContext() {
    	return getRuntimeExecutionContext(null);
    }
	/**
	 * @return List of Attributes for the object
	 */
	public List getAttributes() {
	    return attributes;
	}

    public void setAttributes(List attrs) {
	    attributes = attrs;
    }

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public TimeZone getTimeZone()
	{
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone)
	{
		this.timeZone = timeZone;
	}
}
