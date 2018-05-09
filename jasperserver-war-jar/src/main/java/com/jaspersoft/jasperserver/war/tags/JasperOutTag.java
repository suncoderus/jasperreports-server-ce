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
package com.jaspersoft.jasperserver.war.tags;

import com.jaspersoft.jasperserver.jsp.XSSEscapeXmlELResolver;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * This jstl tag is an 'escape' hatch in case some EL's need to be left unescaped by XSSEscapeXmlELResolver.
 * This tag needs to be used sparingly if at all!
 *
 * User: dlitvak
 * Date: 4/22/14
 */


/**
 * Tag surrounds JSP code in which EL expressions should not be XML-escaped.
 */
public class JasperOutTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	private static final boolean ESCAPE_XSS_SCRIPT_DEFAULT = true;
	private static final boolean UTF8_ESCAPE_XSS_SCRIPT_DEFAULT = false;
	private boolean escapeScript = true;
	private boolean javaScriptEscape;

	public JasperOutTag() {
		release();
	}

	public void setEscapeScript(boolean escapeScript) {
		this.escapeScript = escapeScript;
	}

	public void setJavaScriptEscape(boolean javaScriptEscape) {
		this.javaScriptEscape = javaScriptEscape;
	}

	@Override
	public int doStartTag() {
		pageContext.setAttribute(
				XSSEscapeXmlELResolver.ESCAPE_XSS_SCRIPT, escapeScript);
		pageContext.setAttribute(
				XSSEscapeXmlELResolver.UTF8_ESCAPE_XSS_SCRIPT, javaScriptEscape);
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() {
		pageContext.setAttribute(
				XSSEscapeXmlELResolver.ESCAPE_XSS_SCRIPT, ESCAPE_XSS_SCRIPT_DEFAULT);
		pageContext.setAttribute(
				XSSEscapeXmlELResolver.UTF8_ESCAPE_XSS_SCRIPT, UTF8_ESCAPE_XSS_SCRIPT_DEFAULT);
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		escapeScript = ESCAPE_XSS_SCRIPT_DEFAULT;
		javaScriptEscape = UTF8_ESCAPE_XSS_SCRIPT_DEFAULT;
	}
}

