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
package com.jaspersoft.jasperserver.war.model.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.fop.fo.FObj;

import com.jaspersoft.jasperserver.war.model.JSONObject;
import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import org.apache.log4j.Logger;

/**
 * Implementation of TreeNode
 * @author asokolnikov
 *
 */
public class TreeNodeImpl implements TreeNode {
    public static final Logger log = Logger.getLogger(TreeNodeImpl.class);

    protected String id;
    protected String label; //escaped - see, for example, AvailableFieldsTreeDataProvider.createTreeNode.
    protected String type;
    protected String uri;
    protected String fontStyle;
    protected String fontWeight;
    protected String fontColor;
    protected String cssClass;
    protected int order = Integer.MIN_VALUE;
    protected String tooltip;
    protected JSONObject extraProperty;
    
    protected List children = new ArrayList();
    
    protected TreeDataProvider dataProvider;
    
    public TreeNodeImpl(TreeDataProvider dataProvider, 
            String id, String label, String type, String uri) {
        
        this.dataProvider = dataProvider;
        this.id = id;
        this.label = label;
        this.type = type;
        this.uri = uri;
    }

    public TreeNodeImpl(TreeDataProvider dataProvider, 
            String id, String label, String type, String uri, int order) {

    	this(dataProvider, id, label, type, uri);
    	this.order = order;
    }

    public TreeNodeImpl(TreeDataProvider dataProvider, 
            String id, String label, String type, String uri, JSONObject extraProperty) {
        this(dataProvider, id, label, type, uri);
        this.extraProperty = extraProperty;
    }
    
    public TreeNodeImpl(TreeDataProvider dataProvider, 
            String id, String label, String type, String uri, int order, JSONObject extraProperty) {
        this(dataProvider, id, label, type, uri, order);
        this.extraProperty = extraProperty;
    }
    
    public List getChildren() {
        return children;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public String getUriString() {
        return uri;
    }
    
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Deprecated
    public String getFontStyle() {
        return fontStyle;
    }

    @Deprecated
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    @Deprecated
    public String getFontWeight() {
        return fontWeight;
    }

    @Deprecated
    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    @Deprecated
    public String getFontColor() {
        return fontColor;
    }

    @Deprecated
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getCssClass() {
        return this.cssClass != null ? this.cssClass : "";
    }

    public void setCssClass(String cssClassName) {
        this.cssClass = cssClassName;
    }

    public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public JSONObject getExtraProperty() {
        return extraProperty;
    }

    public void setExtraProperty(JSONObject extraProperty) {
        this.extraProperty = extraProperty;
    }

    public String toJSONString() {
//        StringBuffer sb = new StringBuffer();
//
//        sb.append("{\"id\":\"").append(id).append("\",")
//            .append("\"label\":\"").append(escape(label)).append("\",")
//            .append("\"type\":\"").append(type).append("\",")
//            .append("\"uri\":\"").append(uri).append("\"");
//
//        if(fontStyle != null) {
//            sb.append(",\"fontStyle\":\"").append(fontStyle).append("\"");
//        }
//        if (fontWeight != null) {
//            sb.append(",\"fontWeight\":\"").append(fontWeight).append("\"");
//        }
//        if (fontColor != null) {
//            sb.append(",\"fontColor\":\"").append(fontColor).append("\"");
//        }
//
//        if (order > Integer.MIN_VALUE) {
//        	sb.append(",\"order\":").append(order);
//        }
//
//        if (tooltip != null) {
//        	sb.append(",\"tooltip\":\"").append(tooltip).append("\"");
//        }
//
//        if (extraProperty != null) {
//            sb.append(",\"extra\":").append(extraProperty.toJSONString());
//        }
//
//        if (!children.isEmpty()) {
//            sb.append(",\"children\":[");
//            for (Iterator iter = children.iterator(); iter.hasNext(); ) {
//                TreeNode child = (TreeNode) iter.next();
//                sb.append(child.toJSONString());
//                if (iter.hasNext()) {
//                    sb.append(',');
//                }
//            }
//            sb.append(']');
//        }
//
//        sb.append('}');
//
//        return sb.toString();

        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put(ID, id);
            jsonObject.put(LABEL, label);
            jsonObject.put(TYPE, type);
            jsonObject.put(URI, uri);

            if(fontStyle != null) {
                jsonObject.put("fontStyle", fontStyle);
            }
            if (fontWeight != null) {
                jsonObject.put("fontWeight", fontWeight);
            }
            if (fontColor != null) {
                jsonObject.put("fontColor", fontColor);
            }

            if (this.cssClass != null) {
                jsonObject.put("cssClass", cssClass);
            }

            if (order > Integer.MIN_VALUE) {
                jsonObject.put(ORDER, order);
            }

            if (tooltip != null) {
                jsonObject.put(TOOLTIP, tooltip);
            }

            if (extraProperty != null) {
                jsonObject.put("extra", new org.json.JSONObject(extraProperty.toJSONString()));
            }

            if (!children.isEmpty()) {
                org.json.JSONArray jsonArray = new org.json.JSONArray();
                for (Object child : children) {
                    jsonArray.put(new org.json.JSONObject(((TreeNode) child).toJSONString()));
                }
                jsonObject.put("children", jsonArray);
            }
        } catch (org.json.JSONException e) {
            log.warn("Failed to build JSON String", e);
        }

        return jsonObject.toString();
    }
    
    protected String escape(String str) {
    	return (str == null) ? null : str.replace("\"", "\\\"").replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
    }

    public TreeNode clone(boolean deep) {
		TreeNodeImpl cn = new TreeNodeImpl(dataProvider, id, label, type, uri, order);
		cn.setFontColor(fontColor);
		cn.setFontWeight(fontWeight);
		cn.setFontWeight(fontWeight);
		cn.setCssClass(this.cssClass);
		cn.setTooltip(tooltip);
		if (deep) {
			if (children != null && children.size() > 0) {
				for (Iterator iter = children.iterator(); iter.hasNext(); ) {
					TreeNode chClone = ((TreeNode) iter.next()).clone(deep);
					cn.getChildren().add(chClone);
				}
			}
			if (extraProperty != null) {
				cn.setExtraProperty(new ClonedExtraProperty(extraProperty.toJSONString()));
			}
		} else {
			cn.extraProperty = extraProperty;
			cn.children = new ArrayList(children);
		}
		return cn;
	}

    static class ClonedExtraProperty implements JSONObject {
    	String json;
		public ClonedExtraProperty(String json) {
			this.json = json;
		}
		public String toJSONString() {
			return json;
		}
    }
}
