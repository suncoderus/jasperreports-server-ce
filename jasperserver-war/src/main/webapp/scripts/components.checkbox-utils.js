/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id$
 */

var checkboxLists = [];

function checkboxListInit(id, formName, checkAllName, checkboxName, totalCount, selectedCount)
{
	checkboxLists[id] = new checkboxList(formName, checkAllName, checkboxName, totalCount, selectedCount);
}

function checkboxListAllClicked(id, chckBox)
{
	checkboxLists[id].setAllChecked(chckBox.checked);
}

function checkboxListCheckboxClicked(id, chckBox)
{
	checkboxLists[id].setChecked(chckBox.checked);
}

function checkboxListAnySelected(id)
{
	return checkboxLists[id] && checkboxLists[id].hasChecked();
}

function checkboxList(formName, checkAllName, checkboxName, totalCount, selectedCount)
{
	this.formName = formName;
	this.checkAllName = checkAllName;
	this.checkboxName = checkboxName;
	this.totalCount = totalCount;
	this.selectedCount = selectedCount;
	this.setAllChecked = checkboxListSetAllChecked;
	this.setNameChecked = checkboxListSetNameChecked;
	this.setChecked = checkboxListSetChecked;
	this.hasChecked = checkboxListHasChecked;
}

function checkboxListSetAllChecked(checked)
{
	if (typeof this.checkboxName == 'string')
	{
		this.setNameChecked(checked, this.checkboxName);
	}
	else
	{
		for(var i = 0; i < this.checkboxName.length; ++i)
		{
			this.setNameChecked(checked, this.checkboxName[i]);
		}
	}
}

function checkboxListSetNameChecked(checked, name)
{
	var checkboxInput = document.forms[this.formName].elements[name];
	if (!checkboxInput)
	{
		return;
	}
	if (checkboxInput.length)
	{
		for(var i = 0; i < checkboxInput.length; ++i)
		{
			checkboxInput[i].checked = checked;
		}
	}
	else
	{
		checkboxInput.checked = checked;
	}
	this.selectedCount = checked ? this.totalCount : 0;
}

function checkboxListSetChecked(checked)
{
	var checkboxAll = document.forms[this.formName].elements[this.checkAllName];
	if (checked)
	{
		++this.selectedCount;
		if (this.selectedCount == this.totalCount)
		{
			checkboxAll.checked = true;
		}
	}
	else
	{
		--this.selectedCount;
		checkboxAll.checked = false;
	}
}

function checkboxListHasChecked()
{
	return this.selectedCount > 0;
}
