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

package com.jaspersoft.jasperserver.api.engine.jasperreports.common;

import java.io.Serializable;
import java.util.Map;

/**
 * @author sanda zaharia
 * @version $Id$
 */
public class PptxExportParametersBean extends AbstractExportParameters {
	
	public static final String PROPERTY_PPTX_PAGINATED = "com.jaspersoft.jrs.export.pptx.paginated";
	private Boolean ignoreHyperlink;
	
	/**
	 * @return Returns the ignoreHyperlink.
	 */
	public Boolean getIgnoreHyperlink() {
		return ignoreHyperlink;
	}
	/**
	 * @param ignoreHyperlink The ignoreHyperlink to set.
	 */
	public void setIgnoreHyperlink(Boolean ignoreHyperlink) {
		this.ignoreHyperlink = ignoreHyperlink;
	}
	
	public Object getObject(){
		return this;
	}
	
	public void setPropertyValues(Object object){
		if(object instanceof PptxExportParametersBean){
			PptxExportParametersBean bean =(PptxExportParametersBean)object;
			this.setIgnoreHyperlink(bean.getIgnoreHyperlink());
		}
	}
	
}
