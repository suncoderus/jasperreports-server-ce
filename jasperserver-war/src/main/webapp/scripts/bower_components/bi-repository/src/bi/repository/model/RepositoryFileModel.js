/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 * Licensed under commercial Jaspersoft Subscription License Agreement
 */


/**
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: RepositoryFileModel.js 1979 2016-04-22 20:41:56Z inestere $
 */

define(function (require) {
    "use strict";

    var ResourceModel = require("./RepositoryResourceModel"),
        Backbone = require("backbone"),
        repositoryResourceTypes = require("../enum/repositoryResourceTypes"),
        repositoryFileTypes = require("../enum/repositoryFileTypes"),
        base64 = require("base64"),
        _ = require("underscore");

    return ResourceModel.extend({
        type: repositoryResourceTypes.FILE,
        stringifyContent: true,

        validation: (function() {
            var validation =  _.extend({}, ResourceModel.prototype.validation);

            delete validation.parentFolderUri;

            return validation;
        })(),

        defaults: _.extend({
            type: repositoryFileTypes.UNSPECIFIED,
            content: undefined
        }, ResourceModel.prototype.defaults),

        initialize: function(attrs) {
            ResourceModel.prototype.initialize.apply(this, arguments);

            this.content = this._decodeContent(this.get("content"));

            this.on("change:content", function() {
                this.content = this._decodeContent(this.get("content"));
            }, this);
        },

        setContent: function(content) {
            this.content = content;
            this.set("content", this._encodeContent(content), { silent: true });
        },

        fetchContent: function(options) {
            options || (options = {});

            var self = this;

            return Backbone.ajax(_.defaults(options, {
                type: "GET",
                url: this.url() + "?expanded=false",
                success: function(response) {
                    self.setContent(response);
                }
            }));
        },

        _encodeContent: function (content) {
            if (!_.isUndefined(content)) {
                if (this.stringifyContent) {
                    content = JSON.stringify(content);
                }

                content = base64.encode(content);
            }

            return content;
        },

        _decodeContent: function (content) {
            try {
                if (/[A-Za-z0-9+/=]/.test(content)) {
                    content = base64.decode(content);

                    if (this.stringifyContent) {
                        content = JSON.parse(content);
                    }
                }
            } catch (ex) {
            }

            return content;
        }
    });
});