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
package com.jaspersoft.jasperserver.war.model;

import java.io.Serializable;
import java.util.List;

/**
 * The base interface for each node in the UI Tree
 * 
 * @author asokolnikov
 */
public interface TreeNode extends JSONObject, Serializable {
    
    public static final String ID = "id";
    public static final String LABEL = "label";
    public static final String TYPE = "type";
    public static final String URI = "uri";
    public static final String ORDER = "order";
    public static final String TOOLTIP = "tooltip";

    public String getId();
    
    public String getLabel();
    
    public String getType();
    
    public String getUriString();
    
    public int getOrder();
    
    public void setOrder(int order);

    @Deprecated
    public String getFontStyle();

    @Deprecated
    public void setFontStyle(String fontStyle);

    @Deprecated
    public String getFontColor();

    @Deprecated
    public void setFontColor(String fontColor);

    @Deprecated
    public String getFontWeight();

    @Deprecated
    public void setFontWeight(String fontWeight);

    public String getCssClass();

    public void setCssClass(String cssClass);

    public String getTooltip();
    
    public void setTooltip(String tooltip);


    /**
     * Extra property is a way for TreeDataProvider to attach its specific
     * property or set of objects and properties to be available on client side.
     * Each client side tree node will have it in its node.param.extra property
     * @return
     */
    public JSONObject getExtraProperty();
    
    public List getChildren();
    
    /**
     * Clones the node
     * @param deep if true, makes a full clone including clones of children and 
     * extraProperty, otherwise keeps references to original children and extraProperty objects
     */
    public TreeNode clone(boolean deep);
   
}
