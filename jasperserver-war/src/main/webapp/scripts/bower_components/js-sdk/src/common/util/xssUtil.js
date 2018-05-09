/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
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

/*global xssUtil */

/**
 * @author: Borys Kolesnykov
 */

define(function (require) {
    var jrsConfigs = require("jrs.configs");

    /*
     This map is used to escape certain chars in the HTML output. This disables javascript in HTML context (XSS attacks).
     Note, it's important that applying this function > 1 times does not escape the string to the next level.
     That is the characters being replaced ( < > = ) should not appear in the substitution strings.  Otherwise,
     multiple escapes will break UI.
     E.g. < is replaced with &lt;. None of the chars (& l t or ;) are found as map values.
     */

    var htmlTagWhiteList = 'a,abbr,acronym,address,area,article,aside,b,bdi,bdo,big,blockquote,body,br,button,caption,' +
        'center,cite,code,col,colgroup,dd,details,dfn,div,dl,dt,em,fieldset,font,footer,form,h1,h2,h3,h4,h5,h6,head,' +
        'header,hr,html,i,iframe,img,input,label,legend,li,main,map,mark,menu,menuitem,meta,nav,ol,option,p,pre,' +
        'section,select,small,span,strike,strong,sub,summary,sup,table,tbody,td,textarea,th,thead,title,tr,u,ul,!--';

    var escapeMap = {
        '(': '&#40;',
        ')': '&#41;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    };

    // here, we are escaping all the characters in str that are special in regex's.
    // The str returned here is used to further construct a regex as follows: (?: str ).
    // Eg. str = "a{}" results in "a\{\}" after replace().
    var makeStringRegex = function(str){
        return str == null ? '' : str.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&')
    };

    // Need to escape chars like ) and ( in order to construct correct regex later /(?: \)|\( )/g
    var regexKeys = function(map) {
        var arr = [];
        for (var k in map) {
            if (Object.prototype.hasOwnProperty.call(map, k))
                arr.push(makeStringRegex(k));
        }
        return arr;
    };

    // unescapeMap is made from reverse of escapeMap + extra chars.
    var unescapeMap = (function() {
        var map = {
            // ; is extra character from CHAR_ESCAPE_MAP in EscapeXssScript.java used to escape EL in JSP's.
            // We do not escape this in javascript; however we need to unescape them here in addition to chars in xssUtil.escapeMap.
            '&#111;': 'o',
            '&#110;': 'n',
            '&#115;': 's',
            '&#114;': 'r',
            '&#99;': 'c',
            '&amp;': '&'
        }, k;
        for (k in escapeMap) {
            if (Object.prototype.hasOwnProperty.call(escapeMap, k))
                map[escapeMap[k]] = k;
        }

        return map;
    })();


    //'hard' escape
    var regexp = RegExp('(?:' + regexKeys(escapeMap).join('|') + ')', 'g');

    //'soft' html escape
    var parenEscapeRegex = /(?:\)|\()/g;

    //beef up javascript protection
    var jsRegex1 = /\bjavascript:/ig;
    var jsRegex2 = /\bon(\w+?)\s*=/ig;
    var srcdocRegex = /\bsrcdoc\s*=/ig;

    /*
        options.softHTMLEscape - [boolean] if false (default), escape all symbols according to escapeMap.
                           If true, do not escape valid html tags as defined by htmlTagWhiteList or options.whitelist.
                           Subsequent options are effective only if softHTMLEscape is true.
        options.whitelist - [array] if defined, substitute htmlTagWhiteList tags.  Those tags won't be escaped.
                            E.g. options.whitelist=['a'].  <a> won't be escaped. <img> will be escaped as &lt;img>.
        options.escapeTags - [array] the tags excluded from htmlTagWhiteList; those tags would be escaped.
                            E.g. options.escapeTags = ['iframe'].  <iframe > would become &lt;iframe >.
     */
    var _xssEscape = function(string, options) {
        string = string == null ? '' : string;
        options = options || { };
        if (!(typeof(string) === 'string' || string instanceof String))
            return string;

        // If the string contains nonce, it comes from JRS
        if (string.indexOf(jrsConfigs.xssNonce) >= 0)
            return string;

        if (!options.softHTMLEscape) { // hard escape: simply escapes all the escapeMap key chars.
            string = regexp.test(string) ? string.replace(regexp, function(match) { return escapeMap[match]; }) : string;
        }
        else {  //soft escape: avoid escaping html markup as much as possible
            string = parenEscapeRegex.test(string) ? string.replace(parenEscapeRegex, function(match) { return escapeMap[match]; }) : string;

            //avoid escaping < or > in <TAG> or </TAG>, where TAG is white-listed
            if (options.whiteList && options.whiteList instanceof Array && options.whiteList.length > 0) {
                //escape </TAG>
                var rtLeftTagRegexp = RegExp('<(?!/|' + options.whiteList.join('\\b|') + ')', 'ig');
                string = rtLeftTagRegexp.test(string) ? string.replace(rtLeftTagRegexp, '&lt;') : string;

                //escape <TAG>
                var rtLeftTagRegexp2 = RegExp('</(?!' + options.whiteList.join('\\b|') + ')', 'ig');
                string = rtLeftTagRegexp2.test(string) ? string.replace(rtLeftTagRegexp2, '&lt;/') : string;
            }
            else {
                var tmpTagWhiteList = htmlTagWhiteList;
                if (options.escapeTags && options.escapeTags instanceof Array) {
                    for (var i=0; i < options.escapeTags.length; ++i) {
                        tmpTagWhiteList = htmlTagWhiteList.replace(options.escapeTags[i] + ',', '')
                    }
                }

                //escape </TAG>
                var leftTagRegexp = RegExp('<(?!/|' + tmpTagWhiteList.replace(/,/g,'\\b|') + ')', 'ig');
                string = leftTagRegexp.test(string) ? string.replace(leftTagRegexp, '&lt;') : string;

                //escape <TAG>
                var leftTagRegexp2 = RegExp('</(?!' + tmpTagWhiteList.replace(/,/g,'\\b|') + ')', 'ig');
                string = leftTagRegexp2.test(string) ? string.replace(leftTagRegexp2, '&lt;/') : string;
            }

            // beef up javascript protection by removing javascript: and escaping onload/onfocus/...
            // This assumes that html passed to java script should not have javascript  context inside.
            string = jsRegex1.test(string) ? string.replace(jsRegex1, ''): string;
            string = jsRegex2.test(string) ? string.replace(jsRegex2, '&#111;&#110;$1='): string;

            // iframe's srcdoc allows rendering of the escaped html.  This is dangerous, as the srcdoc html
            // may contain javascript executable in the JRS site domain
            string = srcdocRegex.test(string) ? string.replace(srcdocRegex, '&#115;&#114;&#99;doc=') : string;
        }

        return string;
    };

    //Unescape function
    var unescapeRegexp = RegExp('(?:' + regexKeys(unescapeMap).join('|') + ')', 'ig');
    var _xssUnescape = function(string) {
        string = string == null ? '' : string;
        if (!(typeof(string) === 'string' || string instanceof String)) {
            return string;
        }

        // If the string contains nonce, it comes from JRS: was not escaped
        if (string.indexOf(jrsConfigs.xssNonce) >= 0)
            return string;

        return unescapeRegexp.test(string) ? string.replace(unescapeRegexp, function(match) { return unescapeMap[match]; }) : string;
    };

    /* jshint ignore:start */
    //need to be exposed to global scope without 'window' object
    //because it causes failing of 'optimize' task
    xssUtil = {
        escape: _xssEscape,
        unescape: _xssUnescape,
        noConflict : function(){

        }
    };
    /* jshint ignore:end */

    return xssUtil;

});
