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

function hideShowStackTrace()
{
	var stackTraceDiv;
	var stackTraceDivTitle;

	if(document.getElementById)
	{
		stackTraceDiv = document.getElementById('excptrace');
		stackTraceDivTitle = document.getElementById('excptracetitle');
	}
	else if(document.layers)
	{
		stackTraceDiv = document.layers['excptrace'];
		stackTraceDivTitle = document.layers['excptracetitle'];
	}
	else
	{
		stackTraceDiv = document.all.excptrace;
		stackTraceDivTitle = document.all.excptracetitle;
	}
	hideShow(stackTraceDiv,stackTraceDivTitle);
}

function hideShow(stackTrace,stackTraceTitle)
{
	var hideButton = document.fmJsErrPage._eventId_HideShow;
	if(stackTrace.style.display == "none")
	{
		stackTrace.style.display = "block";
		stackTraceTitle.style.display = "block";
		hideButton.value = document.fmJsErrPage.hideStackTrace.value;
	}
	else
	{
		stackTrace.style.display = "none";
		stackTraceTitle.style.display = "none";
		hideButton.value = document.fmJsErrPage.showStackTrace.value;
	}
}	
