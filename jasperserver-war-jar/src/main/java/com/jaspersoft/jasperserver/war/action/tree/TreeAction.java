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
package com.jaspersoft.jasperserver.war.action.tree;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.core.util.CipherUtil;
import com.jaspersoft.jasperserver.jsp.EscapeXssScript;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeDataProviderFactory;
import com.jaspersoft.jasperserver.war.model.TreeHelper;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Spring Action class for Tree flow.
 *
 * @author asokolnikov
 */
public class TreeAction extends MultiAction {

	public static final String AJAX_REPORT_MODEL = "ajaxResponseModel";
	public static final String PROVIDER = "provider";
	public static final String URI = "uri";
	public static final String URIS = "uris";
	public static final String DEPTH = "depth";
    public static final String PREFETCH = "prefetch";
    public static final String MESSAGE_ID = "messageId";
    private static final String FORCE_HTML_ESCAPE = "forceHtmlEscape";
    private static final String QUERY = "query";

	protected final Log log = LogFactory.getLog(this.getClass());

	private TreeDataProviderFactory treeDataProviderFactory;
	private MessageSource messageSource;
    private ConfigurationBean configurationBean;

	// Actions

	/**
	 * Returns internationalized message for tree client
	 */
    public Event getMessage(RequestContext context) {
    	String messageId = context.getRequestParameters().get(MESSAGE_ID);
        // use HTML escaped messageId as default message to prevent
        // code injection via the messageId parameter
        String defaultMessage = HtmlUtils.htmlEscape(messageId);
        String message = messageSource.getMessage(messageId, null, defaultMessage,
                LocaleContextHolder.getLocale());

    	context.getRequestScope().put(AJAX_REPORT_MODEL, message);

    	return success();
    }

	/**
	 * Gets the data, builds tree model and returns serialized tree data.
	 */
    public Event getNode(RequestContext context) {
    	String providerId = context.getRequestParameters().get(PROVIDER);
    	String uri = context.getRequestParameters().get(URI);
    	String depth = context.getRequestParameters().get(DEPTH);
        String prefetchNodesList = context.getRequestParameters().get(PREFETCH);
        String forceHtmlEscape = context.getRequestParameters().get(FORCE_HTML_ESCAPE);
        String query = context.getRequestParameters().get(QUERY);

        //In order to keep TreeDataProvider API unchanged
        //add search term to uri instead of passing it as a parameter.
        //Since this is legacy code lets change it as small as possible
        if (query != null && !query.isEmpty()) {
            uri += "?q=" + query;
        }

    	int d = 0;
    	if (depth != null && depth.length() > 0) {
	    	try {
	    		d = Integer.parseInt(depth);
	    		if (d < 0) {
	    			d = 0;
	    		}
	    	} catch (Exception e) {
	            log.error("Invalid parameter : depth : " + depth, e);
	        }
    	}

        List<String> prefetchList = null;
        if (prefetchNodesList != null && prefetchNodesList.length() > 0) {
            prefetchList = Arrays.asList(prefetchNodesList.split(","));
        }

    	TreeDataProvider treeDataProvider = findProvider(context, providerId);

        TreeNode treeNode;
        if (prefetchList == null) {
            try {
                treeNode = treeDataProvider.getNode(exContext(context), uri, d);
            }  catch (AccessDeniedException e) {
                treeNode = treeDataProvider.getNode(exContext(context), configurationBean.getPublicFolderUri(), d);
            }
        } else {
            try {
                treeNode = TreeHelper.getSubtree(exContext(context), treeDataProvider, uri, prefetchList, d);
            } catch (AccessDeniedException e) {
                treeNode = TreeHelper.getSubtree(exContext(context), treeDataProvider,
                        configurationBean.getPublicFolderUri(), prefetchList, d);
            }
        }

    	String model = "";
    	if (treeNode != null) {
    	    StringBuffer sb = new StringBuffer();
    	    sb.append("<div id='treeNodeText'>");
//            String response = EscapeXssScript.escape(treeNode.toJSONString());
            String response = treeNode.toJSONString();
            if (forceHtmlEscape != null && forceHtmlEscape.equals("true")){
                sb.append(StringEscapeUtils.escapeHtml(response));
            }else{
                sb.append(response);
            }
    	    sb.append("</div>");
    	    model = sb.toString();
    	}

    	context.getRequestScope().put(AJAX_REPORT_MODEL, model);

    	return success();
    }

    /**
     * Gets children for specified tree node.
     *
     * @param context the request context.
     *
     * @return children for specified tree node.
     */
    public Event getChildren(RequestContext context) {
    	String providerId = context.getRequestParameters().get(PROVIDER);
    	String uri = CipherUtil.uriDecode(context.getRequestParameters().get(URI));
		String query = context.getRequestParameters().get(QUERY);

        //In order to keep TreeDataProvider API unchanged
        //add search term to uri instead of passing it as a parameter.
        //Since this is legacy code lets change it as small as possible
        if (query != null && !query.isEmpty()) {
            uri += "?q=" + query;
        }

    	TreeDataProvider treeDataProvider = findProvider(context, providerId);

    	TreeNode treeNode = treeDataProvider.getNode(exContext(context), uri, 1);

    	String model = "";
    	if (treeNode != null) {
    	    StringBuffer sb = new StringBuffer();
    	    sb.append("<div id='treeNodeText'>");
    	    //sb.append(treeNode.toJSONString());
    	    sb.append('[');
    	    for (Iterator i = treeNode.getChildren().iterator(); i.hasNext(); ) {
    	        TreeNode n = (TreeNode) i.next();
    	        //sb.append(EscapeXssScript.escape(n.toJSONString()));
                sb.append(n.toJSONString());
    	        if (i.hasNext()) {
    	            sb.append(',');
    	        }
    	    }
    	    sb.append(']');
    	    sb.append("</div>");
    	    model = sb.toString();
    	}

    	context.getRequestScope().put(AJAX_REPORT_MODEL, model);

    	return success();
    }

    /**
     * Gets children for specified tree node.
     *
     * @param context the request context.
     *
     * @return children for specified tree node.
     */
    public Event getMultipleChildren(RequestContext context) {
    	String providerId = context.getRequestParameters().get(PROVIDER);
    	String uriParam = CipherUtil.uriDecode(context.getRequestParameters().get(URIS));
    	String[] uris = uriParam.split(",");

    	TreeDataProvider treeDataProvider = findProvider(context, providerId);

    	String model = "";
	    StringBuffer sb = new StringBuffer();
	    sb.append("<div id='treeNodeText'>");
	    sb.append('[');

	    boolean empty = true;
        for (String uri : uris) {
            TreeNode treeNode = treeDataProvider.getNode(exContext(context), uri, 1);

            if (treeNode != null) {
                if (empty) {
                    empty = false;
                } else {
                    sb.append(',');
                }

                sb.append("{\"parentUri\":\"")
                        .append(JavaScriptUtils.javaScriptEscape(treeNode.getUriString())).append("\",\"children\":[");
                for (Iterator i = treeNode.getChildren().iterator(); i.hasNext();) {
                    TreeNode n = (TreeNode) i.next();
                    //sb.append(EscapeXssScript.escape(n.toJSONString()));
                    sb.append(n.toJSONString());
                    if (i.hasNext()) {
                        sb.append(',');
                    }
                }
                sb.append("]}");
            }
        }

	    sb.append(']');
	    sb.append("</div>");
	    model = sb.toString();

    	context.getRequestScope().put(AJAX_REPORT_MODEL, model);

    	return success();
    }

    private TreeDataProvider findProvider(RequestContext context, String providerId) {
    	TreeDataProvider treeDataProvider = null;
    	// First, try to find data provider in session scope
    	treeDataProvider = (TreeDataProvider) context.getExternalContext().getSessionMap().get(providerId);
    	// Then, try to find it in the factory
		if (treeDataProvider == null) {
    		treeDataProvider =  treeDataProviderFactory.getDataProvider(providerId);
		}
		// Fail if not found
		if (treeDataProvider == null) {
			log.error("Cannot find tree data provider with id : " + providerId);
			throw new IllegalArgumentException("Cannot find tree data provider with id : " + providerId);
		}

    	return treeDataProvider;
    }

	// Getters and Setters

    public TreeDataProviderFactory getTreeDataProviderFactory() {
        return treeDataProviderFactory;
    }

    public void setTreeDataProviderFactory(
            TreeDataProviderFactory treeDataProviderFactory) {
        this.treeDataProviderFactory = treeDataProviderFactory;
    }

	private ExecutionContext exContext(RequestContext rContext) {
		return JasperServerUtil.getExecutionContext(rContext);
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

    public void setConfigurationBean(ConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }
}

