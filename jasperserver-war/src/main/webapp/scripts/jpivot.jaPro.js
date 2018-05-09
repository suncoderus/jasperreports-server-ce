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

// TODO: center overly dialog
var bWidth = 1;
var bHeight = 1;

var sWidth = 0;
var sHeight = 0;
var posX = 0;
var posY = 0;
var subobj;

var i, j, k, k0; // index
var nl, al, al0; // list
var n;           // node
var a, a0;       // attribute
var t;           // timer
var argv;
var childWin;

var saveAsViewName, images, links, helpElement, inputs;

// single click for drill-position
function click() {
	for (i = 0; i < document.forms.length; i++) {
		nl = document.forms[i].getElementsByTagName("INPUT");
		for (j = 0; j < nl.length; j++) {
			n = nl.item(j);
			al = n.attributes;
			for (k = 0; k < al.length; k++) {
				a = al.item(k);
				if (a.nodeName == "name") {
					if (a.nodeValue == argv) {
						al0 = n.attributes;
						for (k0 = 0; k0 < al0.length; k0++) {
							a0 = al0.item(k0);
							// disable 'onclick' and 'ondblclick'
							if (a0.nodeName == "onclick" || a0.nodeName == "ondblclick") {
								a0.nodeValue = null;
							}
						}
						// simulate the <input type="image" ... click
						n.click(); 
					} // if a.nodeValue
					else {
						break;
					}
				} // if a.nodeName
			} // for al.length
		} // for nl.length
	} // for document.forms.length
} // function

// TODO: Firefox
// double click for drill-member
function dblClick(argv) {
	for (i = 0; i < document.forms.length; i++) {
		nl = document.forms[i].getElementsByTagName("INPUT");
		for (j = 0; j < nl.length; j++) {
			n = nl.item(j);
			al = n.attributes;
			for (k = 0; k < al.length; k++) {
				a = al.item(k);
				if (a.nodeName == "name") {
					if (a.nodeValue == argv) {
						// goto the next <INPUT ... tag
						n = nl.item(++j);
						// disable 'onclick' and 'ondblclick'
						al0 = n.attributes;
						for (k0 = 0; k0 < al0.length; k0++) {
							a0 = al0.item(k0);
							if (a0.nodeName == "disabled") {
								// turn off disabled
								a0.nodeValue = false;
								break;
							}
						}
						// simulate the <INPUT type="hidden"... click
						n.click(); 
						document.forms[i].submit(); // need the submit here
					} // if a.nodeValue
					else {
						break;
					}
				} // if a.nodeName
			} // for al.length
		} // for nl.length
	} // for document.forms.length
}

// launch a browser window
function launch(url) {
	if (childWin == null || childWin.closed) {
		childWin = launchCenter(url, 'center', 700, 1100);
	}
	else {
		childWin.close();
		childWin = launchCenter(url, 'center', 700, 1100);
	}
	childWin.focus();
}

// position window at center of screen
function launchCenter(url, name, height, width) {
  var str = "height=" + height + ",innerHeight=" + height;
  str += ",width=" + width + ",innerWidth=" + width;

  if (window.screen) {
	var ah = screen.availHeight - 30;
	var aw = screen.availWidth - 10;

	var xc = (aw - width) / 2;
	var yc = (ah - height) / 2;

	str += ",left=" + xc + ",screenX=" + xc;
	str += ",top=" + yc + ",screenY=" + yc;
	str += ",channelmode=0,dependent=1,directories=0,fullscreen=0,location=0,menubar=0,resizable=1,scrollbars=1,status=1,toolbar=0", "drillThroughWindow";
  }

  return window.open(url, name, str);
}

// olapPage and viewURI are declared in the calling JSP

function ld(id) {
    return launch('../' + olapPage + '?name=' + viewURI + '&' + id + '=x&d=x');
    // .//{$olapPage}?{$linkParameters}&amp;{$token}&amp;{@id}=x&amp;d=x')
}

function lc(id) {
	return location.replace('../' + olapPage + '?name=' + viewURI + '&' + id + '=x&d=y');
}

function z(id) {
	return location.replace('../' + olapPage + '?name=' + viewURI + '&' + id + '=x&d=z');
}

// close this window if parent window if closed
function closeChildWin() {
  if (childWin && !childWin.closed){
	childWin.close();
  }
  else{
	//HANDLE THE CASE OF NO WINDOW BEING OPEN
  }
}


function initialize() {
    bWidth = document.body.offsetWidth;
    bHeight = document.body.offsetHeight;

    subobj = document.getElementById('displayFormTable');
    if (subobj) {
        sWidth = subobj.offsetWidth;
        sHeight = subobj.offsetHeight;
        posX = (bWidth - sWidth)/2;
        posY = (bHeight - sHeight)/2;
    }


    // save view
    saveAsViewName = document.getElementById("viewName");
    if ((saveAsViewName) && (saveAsViewName.value=="")) {
  saveAsViewName.value = "${olapSession.olapUnit.label}"+"_"+"<%=copyString%>";
    }

    images = $$('img');
    for (var i = 0; i < images.length; i++) {
    images[i].onclick = function(event){
        pageAlert = false;
    }
    }

    links = $$('a');
    for (var i = 0; i < links.length; i++) {
    links[i].onclick = function(event){
        pageAlert = false;
    }
    }

    //override for help
    helpElement = $('help');
    if (helpElement) {
    helpElement.onclick = webHelpModule.displayWebHelp;
    }

    inputs = $$('input.corner');
    for (var i = 0; i < inputs.length; i++) {
    inputs[i].onclick = function(event){
        pageAlert = false;
    }
    }


    inputs = $$('input.nav');
    for (var i = 0; i < inputs.length; i++) {
    inputs[i].onclick = function(event){
        pageAlert = false;
        t=setTimeout('click()',250);
        argv=inputs[i].id;
        return false;
    }

    inputs[i].ondblclick = function(event){
        clearTimeout(t);
        dblClick(inputs[i].id);
        return false;
    }
    }
}

document.observe('dom:loaded', function() {
    initialize();

    webHelpModule.setCurrentContext("analysis");
    if(isIPad()){
        var scrollElement = $('olapViewForm');
        var touchController = new TouchController(scrollElement, scrollElement.up(), {
            absolute: true
        });
    }
});