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
 * @author: ztomchenco
 * @version: $Id: AjaxFormSubmitter.js 9178 2015-08-10 16:48:28Z ztomchen $
 */

define(function (require) {

    var _ = require("underscore"),
        request = require("request"),
        $ = require("jquery");

    function parseResponse(document) {
        var result, firstDocChild = null;
        if (document.firstChild.innerText && document.firstChild.innerText !== "") {
            if (document.body) {
                result = new window.ActiveXObject("Microsoft.XMLDOM");
                result.async = false;
                //remove injected by IE symbols
                result.loadXML(document.firstChild.innerText.replace("\r\n-", "\r\n"));
            } else {
                result = document;
            }
        } else {
            result = document;
        }

        result.firstChild && (firstDocChild = result.firstChild.nodeName.toLowerCase());
        if (!firstDocChild || firstDocChild === "html" || firstDocChild === "#comment") {
            return {errorCode: "unexpected.error"}
        }

        return parseChildren((result.firstChild.nodeName.toLowerCase() === "xml")
            ? result.firstChild.nextSibling : result.firstChild);
    }

    function parseChildren(node) {
        var children, ob;
        if (!node.children) {
            children = [];
            for (var i = 0, l = node.childNodes.length; i < l; i++) {
                if (node.childNodes[i].nodeValue === null) {
                    children.push(node.childNodes[i]);
                }
            }
        } else {
            children = node.children;
        }

        if (children.length === 0 && node.childNodes.length === 1) {
            return node.childNodes[0].nodeValue;
        } else {
            if (children.length > 1 && (children[0].nodeName === children[1].nodeName)) {
                ob = [];
                for (var i = 0, l = children.length; i < l; i++) {
                    ob[i] = parseChildren(children[i]);
                }
            } else {
                ob = {};
                for (var i = 0, l = children.length; i < l; i++) {
                    ob[children[i].nodeName] = parseChildren(children[i]);
                }
            }
        }
        return ob;
    }

    function ajaxUpload(form) {
        var res = new $.Deferred();

        form.submit(function (e) {
            e.preventDefault();

            request({
                url: form.attr("action"),
                type: form.attr("method"),
                data: new FormData(form[0]),
                cache: false,
                contentType: false,
                processData: false,
                headers: {
                    Accept: "application/json"
                }
            }).done(function (result) {
                res.resolve(result);
            }).fail(function (error) {
                error = error.responseJSON || error || {};

                // default error code
                _.defaults(error, {
                    errorCode: "error.load.error"
                });

                res.reject(error);
            });

            form.unbind("submit");
        });

        form.submit();

        return res;
    }

    function iframeUpload(form, name) {
        var result = $.Deferred(),
            iframe = $("<iframe style='display:none' name='" + name + "'></iframe>");

        form.append(iframe).attr("target", name);

        iframe.load(function () {
            try {
                result.resolve(parseResponse(this.contentWindow.document));
            } catch (e) {
                result.reject({errorCode: "error.invalid.response"});
            }

            iframe.remove();
        });

        iframe.on("abort", function () {
            result.reject({errorCode: "error.load.aborted"});
            iframe.remove();
        });

        iframe.on("error", function () {
            result.reject({errorCode: "error.load.error"});
            iframe.remove();
        });

        form.submit();

        return result;
    }

    var AjaxUploader = function (uploadForm, url, method, enctype) {
        this.name = _.uniqueId("uploadTarget");
        this.form = $(uploadForm);

        url && this.form.attr("action", url);
        method && this.form.attr("method", method);
        enctype && this.form.attr("enctype", enctype);

        this.parceXmlDocToObject = parseResponse;
    };

    AjaxUploader.prototype.submit = function () {
        if (window.FormData) {
            return ajaxUpload(this.form);
        }

        return iframeUpload(this.form, this.name);
    };

    return AjaxUploader;
});
