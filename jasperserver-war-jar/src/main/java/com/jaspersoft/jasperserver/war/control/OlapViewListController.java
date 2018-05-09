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
package com.jaspersoft.jasperserver.war.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.ModelAndView;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;

/**
 * The OlapViewListController lists OLAP units defined in the repository.
 * 
 * @author jshih
 *
 */
public class OlapViewListController extends JRBaseMultiActionController {

	protected final Logger logger = Logger.getLogger(getClass());

    @Autowired
    @Qualifier("messageSource")
	private MessageSource messages;//FIXME not used

	/**
	 * The listOlapViews() method retrives OLAP units from the repository.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView listOlapViews(HttpServletRequest request,
									  HttpServletResponse response) {

		ResourceLookup[] olapUnits = getOlapUnits();

		return new ModelAndView("modules/listOlapViews", "olapUnits", olapUnits);
	}

	public MessageSource getMessages()
	{
		return messages;
	}

	public void setMessages(MessageSource messages)
	{
		this.messages = messages;
	}
}

 