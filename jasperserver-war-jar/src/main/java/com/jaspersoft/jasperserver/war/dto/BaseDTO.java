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
package com.jaspersoft.jasperserver.war.dto;

import java.io.Serializable;

public class BaseDTO implements Serializable {

	private byte mode;

	public final static byte MODE_STAND_ALONE_NEW = 1;

	public final static byte MODE_STAND_ALONE_EDIT = 2;

	public final static byte MODE_SUB_FLOW_NEW = 3;

	public final static byte MODE_SUB_FLOW_EDIT = 4;

	public byte getMode() {
		return mode;
	}

	public void setMode(byte mode) {
		this.mode = mode;
	}

	public boolean isEditMode() {
		return isAloneEditMode() || isSubEditMode();
	}

	public boolean isNewMode() {
		return isAloneNewMode() || isSubNewMode();
	}

	public boolean isStandAloneMode() {
		return isAloneEditMode() || isAloneNewMode();
	}

	public boolean isSubflowMode() {
		return isSubEditMode() || isSubNewMode();
	}

	public boolean isSubEditMode() {
		return mode == MODE_SUB_FLOW_EDIT;
	}

	public boolean isSubNewMode() {
		return mode == MODE_SUB_FLOW_NEW;
	}

	public boolean isAloneEditMode() {
		return mode == MODE_STAND_ALONE_EDIT;
	}

	public boolean isAloneNewMode() {
		return mode == MODE_STAND_ALONE_NEW;
	}
}
