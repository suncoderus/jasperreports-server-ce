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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class PaginatorLinksTag extends TagSupport
{
	public static final String DEFAULT_RENDER_JSP = "/WEB-INF/jsp/modules/defaultPaginatorLinks.jsp";
	
	private String renderJsp = null;

	
	/**
	 *
	 */
	public int doEndTag() throws JspException
	{
		try 
		{
			pageContext.include(getRenderJsp());
		}
		catch (Exception e) 
		{
			throw new JspException(e);
		}

		return EVAL_PAGE;
	}


	/**
	 *
	 */
	public String getRenderJsp() 
	{
		if (renderJsp == null || renderJsp.trim().length() == 0) 
		{
			return DEFAULT_RENDER_JSP;
		}
		return renderJsp;
	}


	/**
	 *
	 */
	public void setRenderJsp(String renderJsp) 
	{
		this.renderJsp = renderJsp;
	}
	

	/**
	 *
	 */
	public static class PaginatorInfo
	{
		public int currentPage = 1;
		public int firstPage = 1;
		public int lastPage = 1;
		public int pageCount = 1;
		
		public int getCurrentPage()
		{
			return currentPage;
		}
		
		public int getFirstPage()
		{
			return firstPage;
		}
		
		public int getLastPage()
		{
			return lastPage;
		}
		
		public int getPageCount()
		{
			return pageCount;
		}
	}
	
}
