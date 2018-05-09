<%--
  ~ Copyright © 2005 - 2018 TIBCO Software Inc.
  ~ http://www.jaspersoft.com.
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program.  If not, see <https://www.gnu.org/licenses/>.
  --%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>

<%--main menu--%>
<div id='menu' class="menu vertical context hidden" aria-hidden="true" role="menu" js-navtype="actionmenu"></div>

<div id="commonMenu" class="hidden">
    <div class="content">
        <ul id="menuList_template" role="menubar" js-navtype="actionmenu">
        </ul>
    </div>
</div>

<ul class="hidden" aria-hidden="true" role="menubar" js-navtype="actionmenu">
    <%--simply action list option--%>
    <li id="menuList_simpleAction" class="leaf" role="menuitem" js-navtype="actionmenu"><p class="wrap button"><span class="icon"></span><!--Item text goes here--></p></li>

    <%--separator for menu--%>
    <li id="menuList_separator" class="leaf separator" role="separator" js-navtype="actionmenu"></li>

    <%--submenu list--%>
    <li id="menuList_flyout" class="node" role="menu" js-navtype="actionmenu"><p class="wrap button"><span class="icon"></span><!--Item text goes here--></p></li>

    <%--extra pop up menu--%>
    <li id="menuList_extraInput" class="leaf" js-navtype="actionmenu"><p class="wrap button"><span class="icon"></span><!--Item text goes here--></p></li>

    <%--dropdown list element--%>
    <li id="menuList_listItem" class="leaf" role="menuitem" js-navtype="actionmenu"><p class="wrap toggle button"><span class="icon"></span><!--Item text goes here--></p></li>

    <%--main navigation mutton--%>
    <%--<li id="navigation_mutton" class="leaf mutton up button" role="menuitem" js-navtype="actionmenu"><i class="icon"></i></li>--%>
    <li id="navigation_mutton" tabIndex="-1" class="node mutton" role="menuitem" js-navtype="actionmenu"><p class="wrap button"><span class="icon"></span></p></li>
</ul>