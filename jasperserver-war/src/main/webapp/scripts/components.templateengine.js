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
 * @author: inesterenko
 * @version: $Id$
 */

/* global jaspersoft, _, Mustache */

//TODO: this module from controls.core.js, remove it from controls.core.js
jaspersoft.components.templateEngine = (function(jQuery, _, Mustache){

    // Provide functionality to get templates

    return {

            // Populate template with data
            render:function (templateText, model, type) {
                if (!type){
                    //Mustache by default
                    return Mustache.to_html(templateText, model);
                }else if(type == this.STD_PLACEHOLDERS){
                    var result = String(templateText);
                    _.each(model,  function(val, index){
                        var regExp = new RegExp("\\{"+index+"\\}");
                        result = result.replace(regExp, val);
                    });
                    return result;
                }
            },
            renderUrl:function(templateText, model, encode){
                var url = Mustache.to_html(templateText, model);
                if(encode){
                    url = encodeURI(url);
                }
                return url;
            },
            // Return template's text
            getTemplateText:function (templateId) {
                var scriptTag = jQuery("#" + templateId);
                return scriptTag.html();
            },

            // Return template's function for given id
            createTemplate:function (templateId) {
                var scriptTag = jQuery("#" + templateId);
                var templateText = scriptTag.html();

                if (templateText && templateText.length > 0) {
                    return function (model) {
                        return Mustache.to_html(templateText, model);
                    };
                }
            },

            createTemplateFromText: function(templateText){
                if (templateText && templateText.length > 0) {
                    return function (model) {
                        return Mustache.to_html(templateText, model);
                    };
                }
            },

            // Cut template's text chunk and wrap with a function
            createTemplateSection:function (section, templateId) {
                var regexpTemplate = '\\{\\{#val\\}\\}(\\s|\\S)*\\{\\{/val\\}\\}';
                var concreteSectionRegexpPattern = regexpTemplate.replace(/val/g, section);
                var regexp = new RegExp(concreteSectionRegexpPattern, "g");
                var templateText = this.getTemplateText(templateId);
                var templateSectionText = templateText.match(regexp)[0];
                return  function (model) {
                    return Mustache.to_html(templateSectionText, model);
                };
            },

            STD_PLACEHOLDERS : "std_placeholder"

    };

})(
    jQuery,
    _,
    Mustache
);

