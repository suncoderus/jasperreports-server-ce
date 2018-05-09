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
package com.jaspersoft.jasperserver.war.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Lucian Chirita
 * @version $Id$
 */
public class StandardHeartbeatCall implements HeartbeatCall {

	private List<NameValuePair> parameters;
	
	public StandardHeartbeatCall() {
		parameters = new ArrayList<NameValuePair>();
	}

	@Override
	public void addParameter(String name, String value) {
		BasicNameValuePair param = new BasicNameValuePair(name, value);
		parameters.add(param);
	}

	public List<NameValuePair> getParameters() {
		return parameters;
	}

}
