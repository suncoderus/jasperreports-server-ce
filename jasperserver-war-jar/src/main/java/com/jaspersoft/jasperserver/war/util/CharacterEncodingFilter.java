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
package com.jaspersoft.jasperserver.war.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CharacterEncodingFilter implements Filter {

	private String encodingRequestAttrName;
	private CharacterEncodingProvider encodingProvider;

	public void init(FilterConfig arg0) throws ServletException {
		// nothing
	}

	public void destroy() {
		// nothing
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		if (hasEncoding(request)) {
			chain.doFilter(request, response);
		} else {
			String encoding = getEncoding();
			setEncoding(request, encoding);

			if (request.getCharacterEncoding() == null) {
				request.setCharacterEncoding(encoding);
			}

			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			CharsetEncodingResponseWrapper responseWrapper = new CharsetEncodingResponseWrapper(httpServletResponse, encoding);
	        chain.doFilter(request, responseWrapper);
		}	
	}

	protected void setEncoding(ServletRequest request, String encoding) {
		request.setAttribute(getEncodingRequestAttrName(), encoding);
	}

	protected boolean hasEncoding(ServletRequest request) {
		return request.getAttribute(getEncodingRequestAttrName()) != null;
	}
	
	protected String getEncoding() {
		return getEncodingProvider().getCharacterEncoding();
	}

	protected static class CharsetEncodingResponseWrapper extends HttpServletResponseWrapper {

		private boolean encodingSpecified = false;
		private final String encoding;

		public CharsetEncodingResponseWrapper(HttpServletResponse response, String encoding) {
			super(response);
			this.encoding = encoding;
		}

		public void setContentType(String type) {
			String encType = type;

			if (!encodingSpecified) {
				String lowerType = type.toLowerCase();
				
				if (lowerType.indexOf("charset") < 0) {
					if (lowerType.startsWith("text/html")) {
						encType = type + "; charset=" + encoding;
					}
				} else {
					encodingSpecified = true;
				}
			}

			super.setContentType(encType);
		}
	}

	public CharacterEncodingProvider getEncodingProvider() {
		return encodingProvider;
	}

	public void setEncodingProvider(CharacterEncodingProvider encodingProvider) {
		this.encodingProvider = encodingProvider;
	}

	public String getEncodingRequestAttrName() {
		return encodingRequestAttrName;
	}

	public void setEncodingRequestAttrName(String filteredReqAttrName) {
		this.encodingRequestAttrName = filteredReqAttrName;
	}
}
