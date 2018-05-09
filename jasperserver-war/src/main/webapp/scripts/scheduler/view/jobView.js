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

/* global getTZOffset */

define(function(require){

    "use strict";

    var _ = require('underscore'),
        Backbone = require('backbone'),
        dialogs = require('components.dialogs'),
        templateJob = require('text!scheduler/template/list/oneJob.htm'),
        templateMasterJob = require('text!scheduler/template/list/oneMasterJob.htm'),
        i18n = require('bundle!all'),
        config = require('jrs.configs'),
        moment = require("moment"),
        xssUtil = require("common/util/xssUtil"),
	    ConfirmationDialog = require("common/component/dialog/ConfirmationDialog");

    return Backbone.View.extend({

        // view element tagName
        tagName: 'li',
        className: 'jobs first leaf',

        // bind events for view
        events: {
            'click [name=editJob]': 'edit',
            'click [name=deleteJob]': 'remove',
            'change [name=enableJob]': 'enable'
        },

        // initialize view
        initialize: function(options){
	        this.options = _.extend({}, options);

	        var template = this.options.masterViewMode ? templateMasterJob : templateJob;

            // create template
            this.template = _.template(template);

            // handle model changes
            this.model.on('change', this.render, this);
        },

        // render view
        render: function() {
            this.$el.html(this.template({
                model: this.model.toJSON(),
                i18n: i18n,
                timeZoneOffsetFunction: function(dateString){
                    return getTZOffset(config.usersTimeZone, dateString) * 60;
                }
            }));
            return this;
        },

        // edit job
        edit: function() {
            this.trigger("editJobPressed", this.model.get("id"));
        },

        // remove job
        remove: function(){
            var self = this;

	        var text = i18n['report.scheduling.editing.job.confirm.delete'].
		        replace('{name}', xssUtil.escape(this.model.get('label'))).
		        replace('{newline}', '<br><br>');

	        var dialog = new ConfirmationDialog({
		        title: i18n["report.scheduling.editing.job.confirm.title"],
		        text: text,
		        additionalCssClasses: "schedulerJobRemoveDialog"
	        });
	        this.listenTo(dialog, "button:yes", function() {
		        self.model.destroy();
	        });
	        dialog.open();
        },

        // enable/disable job
        enable: function(event){
            this.model.state(event.target.checked);
        }

    });

});