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
package com.jaspersoft.jasperserver.api.security.externalAuth.wrappers.jasig;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;

/**
 * Wrapper class for org.jasig.cas.client.validation.Cas20ServiceTicketValidator
 * @author dlitvak
 * @version $Id$
 * @since 6.0
 */
@JasperServerAPI
public class JSCas20ServiceTicketValidator extends Cas20ServiceTicketValidator {
	/**
	 * Constructs an instance of the CAS 2.0 Service Ticket Validator with the supplied
	 * CAS server url prefix.
	 *
	 * @param casServerUrlPrefix the CAS Server URL prefix.
	 */
	public JSCas20ServiceTicketValidator(String casServerUrlPrefix) {
		super(casServerUrlPrefix);
	}
}
