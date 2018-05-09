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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import org.springframework.dao.DataAccessException;

/**
 * Created by IntelliJ IDEA.
 * User: dlitvak
 * Date: 7/27/12
 * Time: 3:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class PasswordEncryptionException extends DataAccessException {
	/**
	 * Constructor for PasswordEncryptionException.
	 * @param msg the detail message
	 */
	public PasswordEncryptionException(String msg) {
		super(msg);
	}


	/**
	 * Constructor for PasswordEncryptionException.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public PasswordEncryptionException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
