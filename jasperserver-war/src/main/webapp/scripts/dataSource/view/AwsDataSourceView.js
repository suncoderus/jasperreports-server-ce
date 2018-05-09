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

define(function(require) {
    "use strict";

    var _ = require("underscore"),
        AlertDialog = require("common/component/dialog/AlertDialog"),
        JdbcDataSourceView = require("dataSource/view/JdbcDataSourceView"),
        BaseDataSourceView = require("dataSource/view/BaseDataSourceView"),
        AwsDataSourceModel = require("dataSource/model/AwsDataSourceModel"),
        UploadJdbcDriverDialog = require("dataSource/view/dialog/UploadJdbcDriverDialog"),
        dynamicTree = require("dynamicTree.utils"),
        buttonManager = require("core.events.bis"),
        i18n = require("bundle!jasperserver_messages"),
        awsSpecificTemplate = require("text!dataSource/template/awsSpecificTemplate.htm"),
        awsSettings = require("settings!awsSettings"),
        settingsUtility = require("dataSource/util/settingsUtility");

    return JdbcDataSourceView.extend({
        PAGE_TITLE_NEW_MESSAGE_CODE: "resource.datasource.aws.page.title.new",
        PAGE_TITLE_EDIT_MESSAGE_CODE: "resource.datasource.aws.page.title.edit",

        modelConstructor: AwsDataSourceModel,

        events: function() {
            var events = {};

            _.extend(events, JdbcDataSourceView.prototype.events);

            events["change input[name='credentialsType']"] = "changeCredentialsType";
            events["click #findAwsDataSources"] = "refreshAwsDataSourceTree";

            return events;
        },

        initialize: function(options) {
            BaseDataSourceView.prototype.initialize.apply(this, arguments);

            this.deepDefaults = settingsUtility.deepDefaults(options, {
                awsSettings: awsSettings
            });

            this.listenTo(this.model, "change:credentialsType", this.onCredentialsTypeChange);
            this.listenTo(this.model, "change:driverClass", this.changeUploadDriverButtonState);
            this.listenTo(this.model, "change", this.updateInput);

            this.listenTo(this.model.drivers, "change add", this.recheckDriver);
        },

        changeCredentialsType: function() {
            var value = this.$("input[name='credentialsType']:checked").val();

            this.model.set("credentialsType", value);
        },

        recheckDriver: function() {
            this.model.validate({ driverClass: this.model.get("driverClass") });
            this.changeUploadDriverButtonState();
        },

        changeUploadDriverButtonState: function() {
            var $driverUploadButton = this.$("#driverUploadButton");

            if (this.model.get("driverClass") === "") {
                buttonManager.disable($driverUploadButton[0]);
            } else {
                buttonManager.enable($driverUploadButton[0]);

                var driver = this.model.drivers.getDriverByClass(this.model.get("driverClass"));

                $driverUploadButton.find(".wrap").text(i18n[!driver || !driver.get("available")
                    ? "resource.dataSource.jdbc.upload.addDriverButton"
                    : "resource.dataSource.jdbc.upload.editDriverButton"]);
            }
        },

        initDriverUploadDialog: function() {
            var self = this, driver = this.model.drivers.getDriverByClass(this.model.get("driverClass"));

            this.driverUploadDialog = new UploadJdbcDriverDialog({
                driverAvailable: !(!driver || !driver.get("available")),
                driverClass: this.model.get("driverClass")
            });

            this.driverUploadDialog.on("driverUpload", function(driver) {
                self.model.drivers.markDriverAsAvailable(driver.jdbcDriverClass);

                _.defer(_.bind(self.model.validate, self.model));
            });
        },

        updateInput: function() {
            var changedAttributes = _.keys(this.model.changedAttributes()),
                inputsToUpdate = ["accessKey", "secretKey", "roleArn", "connectionUrl", "driverClass", "dbName"],
                self = this;

            _.each(_.intersection(changedAttributes, inputsToUpdate), function(attr) {
                var
                    newValue = self.model.get(attr),
                    currValue = self.$("[name='" + attr + "']").val();
                if (currValue !== newValue) {
                    self.$("[name='" + attr + "']").val(newValue);
                }
            });
        },

        onCredentialsTypeChange: function() {
            var ec2CredentialsSelected = this.model.get("credentialsType") === AwsDataSourceModel.credentialsType.EC2;

            this.$("#aws_settings")[ec2CredentialsSelected ? "hide" : "show"]();

            if (ec2CredentialsSelected) {
                this.showAwsDsTree(this.model.getFullDbTreePath());
            }
        },

        refreshAwsDataSourceTree: function(e) {
            e.preventDefault();
            this.showAwsDsTree("/");
        },

        render: function() {
            this.$el.empty();

            this.renderTimezoneSection();
            this.renderAwsSpecificSection();
			this.renderTestConnectionSection();

			this.initDataSourceTree();

			// Do not try to load AWS Tree if we are creating DS in non-AWS environment without properly set credentials
			if (this.options.isEditMode || this.model.get("credentialsType") === AwsDataSourceModel.credentialsType.EC2) {
				this.showAwsDsTree(this.model.getFullDbTreePath());
			}

            return this;
        },

        showAwsDsTree : function (path) {
            this.model.validate({
                accessKey: this.model.get("accessKey"),
                secretKey: this.model.get("secretKey")
            });

            if (this.model.isValid("secretKey") && this.model.isValid("accessKey")) {
                this.awsDataSourceTree.showTreePrefetchNodes(path || "/");
            }
        },

        templateData: function() {
            var data = JdbcDataSourceView.prototype.templateData.apply(this, arguments);

            _.extend(data, {
                credentialsType: AwsDataSourceModel.credentialsType,
                awsRegions: this.deepDefaults.awsSettings.awsRegions,
                disableAwsDefaults:
                    !this.deepDefaults.awsSettings.isEc2Instance || this.deepDefaults.awsSettings.suppressEc2CredentialsWarnings
            });

            return data;
        },

        renderAwsSpecificSection: function() {
            this.$el.append(_.template(awsSpecificTemplate, this.templateData()));
			this.changeUploadDriverButtonState();
        },

        initDataSourceTree : function () {
            var isEdit = this.options.isEditMode,
                self = this,
                treeOptions = {
                    hideLoader : true,
                    bShowRoot : false,
                    treeId : "awsDataSourceTree",
                    providerId : 'awsDataSourceTreeDataProvider',
                    selectLeavesOnly : true,
                    additionalParams : function () {
                        return {
                            arn: self.model.get("roleArn"),
                            awsAccessKey: self.model.get("accessKey"),
                            awsSecretKey: self.model.get("secretKey"),
                            region: self.model.get("region"),
                            datasourceUri: self.model.get("uri")
                        }
                    }
                };

            this.awsDataSourceTree = dynamicTree.createRepositoryTree("awsDataSourceTree", treeOptions);
            this.awsDataSourceTree.httpErrorHandler = function(ajaxAgent) {

	            // some requests came with 500 error code, some with 200, so let's just check error test
	            var alertDialog, error = false;

                if (ajaxAgent.responseText.indexOf("AWS Access Key is invalid") !== -1 ||
                    ajaxAgent.responseText.indexOf("The security token included in the request is invalid") !== -1) {
	                error = i18n["error.aws.key.is.invalid"];
                }
                if (error === false && (ajaxAgent.responseText.indexOf("AWS Secret Key is invalid") !== -1 ||
                    ajaxAgent.responseText.indexOf("The request signature we calculated does not match the signature you provided. Check your AWS Secret Access Key and signing method") !== -1)) {
                    error = i18n["error.aws.secret.key.is.invalid"];
                }
                if (error === false && ajaxAgent.responseText.indexOf("is not authorized to perform") !== -1) {
                    error = i18n["error.aws.arn.is.invalid"];
                }
                if (error === false && ajaxAgent.responseText.indexOf("The security token included in the request is invalid") !== -1) {
	                error = i18n["error.security.token.is.invalid"];
                }

	            if (error) {
		            alertDialog = new AlertDialog({ modal: true });
		            alertDialog.setMessage(error);
		            alertDialog.open();
		            // return 'true' means don't let the parent's handler to handle this error
		            return true;
	            }

                return false;
            };

            // Initialize tree events: auto-fill connection setting from tree leaf
            this.awsDataSourceTree.observe('leaf:selected', function (ev) {
                var node = ev.memo.node.param;

                if (node.type === "awsDb" && !isEdit) {
                    var extra = node.extra,
                        uriComponents = extra.dbUri.split("/");

                    self.model.set({
                        connectionUrlTemplate: extra.jdbcTemplate,
                        driverClass: extra.jdbcDriverClass,
                        dbName: extra.dBName,
                        dbHost: extra.dnsAddress,
                        dbPort : extra.dbPort,
                        sName : extra.dBName,
                        dbService: uriComponents[1],
                        dbInstanceIdentifier: uriComponents[2]
                    });

                    self.model.validate({
                        driverClass: self.model.get("driverClass")
                    });
                }
            });

            this.awsDataSourceTree.observe('tree:loaded', function () {
                if (self.model.getFullDbTreePath()) {
                    self.awsDataSourceTree.openAndSelectNode(self.model.getFullDbTreePath(), function () {
                        isEdit = false;
		            });
                }
            });
        },

		remove: function() {
            this.awsDataSourceTree && this.awsDataSourceTree.stopObserving();
            JdbcDataSourceView.prototype.remove.apply(this, arguments);
        }
    });
});