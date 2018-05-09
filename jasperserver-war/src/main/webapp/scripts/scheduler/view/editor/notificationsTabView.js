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

define(function(require){

    "use strict";

	var  $ = require('jquery'),
		_ = require("underscore"),
		i18n = require('bundle!all'),
	    Backbone = require('backbone'),
		notificationsTabTemplate = require("text!scheduler/template/editor/notificationsTabTemplate.htm");

	return Backbone.View.extend({

        events: {
            "change [name=to_suc]" : "to_suc_Change",
            "change [name=cc_suc]" : "cc_suc_Change",
            "change [name=bcc_suc]" : "bcc_suc_Change",
            "change [name=subject_suc]" : "subject_suc_Change",
            "change [name=message_suc]" : "message_suc_Change",
            "change [name=resultSendTypeRadio]" : "setResultSendType",
            "change [name=includeHtmlReport]" : "setResultSendType",
            "change [name=dontSendEmptyReport]" : "dont_send_empty_report_Change",

            "change [name=job_status_to]" : "job_status_to_Change",
            "change [name=job_status_subject]" : "job_status_subject_Change",
            "change [name=job_status_success_message]" : "job_status_success_message_Change",
            "change [name=job_status_failed_message]" : "job_status_failed_message_Change",
            "change [name=send_success_notification]" : "twoCheckboxes_Change",
            "change [name=send_failure_notification]" : "twoCheckboxes_Change",
            "change [name=include_report]" : "include_report_Change",
            "change [name=include_stack_trace]" : "include_stack_trace_Change"
        },

        initialize: function(options){
            var self = this;

            this.options = options;

            this.model.on("change:mailNotification", function(model) {
                var mn = model.get('mailNotification');
                if (!mn) return;

                self.$el.find("[name=to_suc]").val(mn.toAddresses.address);
                self.$el.find("[name=cc_suc]").val(mn.ccAddresses.address);
                self.$el.find("[name=bcc_suc]").val(mn.bccAddresses.address);

                self.$el.find("[name=subject_suc]").val(mn.subject);
                self.$el.find("[name=message_suc]").val(mn.messageText);
                self.message_suc_DisabledEnabled(model);

                var
                    chBoxValue = false,
                    radioValue = "";
                switch (mn.resultSendType) {
                    case "SEND": chBoxValue = false; radioValue = "asRepoLinks"; break;
                    case "SEND_EMBED": chBoxValue = true; radioValue = "asAttachedFiles"; break;
                    case "SEND_EMBED_ZIP_ALL_OTHERS": chBoxValue = true; radioValue = "asAttachedZip"; break;
                    case "SEND_ATTACHMENT": chBoxValue = false; radioValue = "asAttachedFiles"; break;
                    case "SEND_ATTACHMENT_ZIP_ALL": chBoxValue = false; radioValue = "asAttachedZip"; break;
                }
                self.$el.find('[name=resultSendTypeRadio]').filter('[value=' + radioValue + ']').prop('checked', true);
                self.asRepoLinks_DisabledEnabled(model);

                self.$el.find('[name=includeHtmlReport]').prop('checked', chBoxValue);
                self.includeHtmlReport_DisabledEnabled(model);


                self.$el.find("[name=dontSendEmptyReport]").prop('checked', mn.skipEmptyReports);
            });

            this.model.on("change:alert", function(model) {
                var a = model.get('alert');
                if (!a) return;

                self.$el.find("[name=job_status_to]").val(a.toAddresses.address);

                self.$el.find("[name=send_success_notification]").prop('checked', a.jobState.indexOf("SUCCESS_ONLY") !== -1 || a.jobState.indexOf("ALL") !== -1);
                self.$el.find("[name=send_failure_notification]").prop('checked', a.jobState.indexOf("FAIL_ONLY") !== -1 || a.jobState.indexOf("ALL") !== -1);

                self.$el.find("[name=job_status_subject]").val(a.subject);
                self.$el.find("[name=job_status_success_message]").val(a.messageText);
                self.$el.find("[name=job_status_failed_message]").val(a.messageTextWhenJobFails);

                self.$el.find("[name=include_report]").prop("checked", !!(a.includingReportJobInfo));
                self.$el.find("[name=include_stack_trace]").prop("checked", !!(a.includingStackTrace));
            });

            this.model.on('change:outputFormats', function(model, value){
                var contains = value.outputFormat.indexOf('HTML') >= 0;
                if (!contains) {
                    // we need to change from SEND_EMBED or SEND_EMBED_ZIP_ALL_OTHERS into SEND or SEND_ATTACHMENT
                    var c = self.model.get("mailNotification");
                    if (c && c.resultSendType && (c.resultSendType === "SEND_EMBED" || c.resultSendType === "SEND_EMBED_ZIP_ALL_OTHERS")) {

                        var changeTo = "SEND";

                        var rd = model.get('repositoryDestination');
                        if (rd) {
                            if (!rd.saveToRepository) {
                                changeTo = "SEND_ATTACHMENT"
                            }
                        }

                        model.update("mailNotification", {resultSendType: changeTo});
                        return;
                    }
                }
                self.includeHtmlReport_DisabledEnabled(model);
            });
	        
            this.model.on('change:repositoryDestination', function(model, value){
                var checked = value.saveToRepository;
                if (!checked) {
                    // we need to change from SEND into SEND_ATTACHMENT
                    var c = self.model.get("mailNotification");
                    if (c && c.resultSendType && c.resultSendType === "SEND") {
                        model.update("mailNotification", {resultSendType: "SEND_ATTACHMENT"});
                        return;
                    }
                }
                self.asRepoLinks_DisabledEnabled(model);
            });
        },

		render: function() {
			this.setElement($(_.template(notificationsTabTemplate, {i18n: i18n})));
		},

		includeHtmlReport_DisabledEnabled: function(model) {
            var enabled = true;

            var mn = model.get('mailNotification');
            if (mn) {
                if (mn.resultSendType === "SEND") {
                    enabled = false;
                }
            }

            var of = model.get("outputFormats");
            if (of) {
                if (of.outputFormat.indexOf('HTML') === -1) {
                    enabled = false;
                }
            }

            this.$el.find('[name=includeHtmlReport]').prop("disabled", enabled ? false : "disabled");
        },

        message_suc_DisabledEnabled: function(model) {
            var enabled = true;

            var mn = model.get('mailNotification');
            if (mn) {
                if (mn.resultSendType === "SEND_EMBED" || mn.resultSendType === "SEND_EMBED_ZIP_ALL_OTHERS") {
                    enabled = false;
                }
            }

            this.$el.find('[name=message_suc]').prop("disabled", enabled ? false : "disabled");
        },

        asRepoLinks_DisabledEnabled: function(model) {
            var enabled = true;

            var rd = model.get('repositoryDestination');
            if (rd) {
                if (!rd.saveToRepository) {
                    enabled = false;
                }
            }

            this.$el.find('[name=resultSendTypeRadio][value=asRepoLinks]').prop("disabled", enabled ? false : "disabled");
        },






        to_suc_Change: function(evt){
            this.model.update("mailNotification", {toAddresses: {address: $(evt.target).val()}});
        },

        cc_suc_Change: function(evt){
            this.model.update("mailNotification", {ccAddresses: {address: $(evt.target).val()}});
        },

        bcc_suc_Change: function(evt){
            this.model.update("mailNotification", {bccAddresses: {address: $(evt.target).val()}});
        },

        subject_suc_Change: function(evt){
            this.model.update("mailNotification", {subject: $(evt.target).val()});
        },

        message_suc_Change: function(evt){
            this.model.update("mailNotification", {messageText: $(evt.target).val()});
        },

        setResultSendType: function(evt){
            var radioValue = this.$el.find("[name=resultSendTypeRadio]").filter(":checked").val();
            var checkboxState = this.$el.find("[name=includeHtmlReport]").is(":checked");
            var newVal = "";

            if (checkboxState) {
                if (radioValue === "asRepoLinks") {
                    newVal = "SEND";
                } else if (radioValue === "asAttachedFiles") {
                    newVal = "SEND_EMBED";
                } else if (radioValue === "asAttachedZip") {
                    newVal = "SEND_EMBED_ZIP_ALL_OTHERS";
                }
            } else {
                if (radioValue === "asRepoLinks") {
                    newVal = "SEND";
                } else if (radioValue === "asAttachedFiles") {
                    newVal = "SEND_ATTACHMENT";
                } else if (radioValue === "asAttachedZip") {
                    newVal = "SEND_ATTACHMENT_ZIP_ALL";
                }
            }

            this.model.update("mailNotification", {resultSendType: newVal});
        },

        dont_send_empty_report_Change: function(evt){
            this.model.update("mailNotification", {skipEmptyReports: $(evt.target).is(":checked")});
        },


        job_status_to_Change: function(evt){
            this.model.update("alert", {toAddresses: {address: $(evt.target).val()}});
        },

        job_status_subject_Change: function(evt){
            this.model.update("alert", {subject: $(evt.target).val()});
        },

        twoCheckboxes_Change: function(evt){
            var
                v1 = this.$el.find("[name=send_success_notification]").is(":checked"),
                v2 = this.$el.find("[name=send_failure_notification]").is(":checked");

            var enumValue = [];

            if (v1) enumValue.push("SUCCESS_ONLY");
            if (v2) enumValue.push("FAIL_ONLY");

            if (enumValue.length === 0) {
                enumValue = ["NONE"];
            }
            if (enumValue.length === 2) {
                enumValue = ["ALL"];
            }

            enumValue = enumValue.join("|");

            this.model.update("alert", {jobState: enumValue});
        },

        job_status_success_message_Change: function(evt){
            this.model.update("alert", {messageText: $(evt.target).val()});
        },

        job_status_failed_message_Change: function(evt){
            this.model.update("alert", {messageTextWhenJobFails: $(evt.target).val()});
        },

        include_report_Change: function(evt){
            this.model.update("alert", {includingReportJobInfo: $(evt.target).is(":checked")});
        },

        include_stack_trace_Change: function(evt){
            this.model.update("alert", {includingStackTrace: $(evt.target).is(":checked")});
        }

    });
});