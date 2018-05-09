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


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id$
 */
public class OdsExportParametersBean extends XlsExportParametersBean {
	
	public void setPropertyValues(Object object){
		if(object instanceof OdsExportParametersBean){
			OdsExportParametersBean bean =(OdsExportParametersBean)object;
			this.setDetectCellType(bean.getDetectCellType());
			this.setOnePagePerSheet(bean.getOnePagePerSheet());
			this.setRemoveEmptySpaceBetweenRows(bean.getRemoveEmptySpaceBetweenRows());
			this.setRemoveEmptySpaceBetweenColumns(bean.getRemoveEmptySpaceBetweenColumns());
			this.setWhitePageBackground(bean.getWhitePageBackground());
			this.setIgnoreGraphics(bean.getIgnoreGraphics());
			this.setCollapseRowSpan(bean.getCollapseRowSpan());
			this.setIgnoreCellBorder(bean.getIgnoreCellBorder());
			this.setFontSizeFixEnabled(bean.getFontSizeFixEnabled());
			this.setMaximumRowsPerSheet(bean.getMaximumRowsPerSheet());
			this.setXlsFormatPatternsMap(bean.getXlsFormatPatternsMap());
		}
	}
	
}
