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

    var $ = require("jquery"),
	    _ = require('underscore'),
        Backbone = require('backbone'),
        config = require('jrs.configs'),
        jobModel = require('scheduler/model/jobModel');

    return Backbone.Collection.extend({

        // collection model
        model: jobModel,

	    // initialize view
	    initialize: function(ignore, options) {
		    this.options = _.extend({}, options);

		    this.initialAmountOfJobsToLoad = 100; // TODO: extract to config files
		    this.amountOfJobsToLoad = 100; // TODO: extract to config files
			this.amountOfLoadedJobs = 0; // variable which holds the amount of jobs loaded already from the backend
	    },

	    setSearchingTerm: function(term) {
		    this.searchingTerm = term;
	    },

		getSearchingTerm: function() {
			return this.searchingTerm;
		},

	    getReportUri: function() {
		    return this.options.parentReportURI || this.options.reportUri;
	    },

	    fetch: function() {

		    //initialize the list of the reports which jobs we might need to fetch
		    this.urlsOfReportsToFetchJobs = [];

		    // In Master View mode we don't work with specific report so we can't fetch
		    // report options, so we need simply skip this part and fetch all jobs we have
		    if (this.options.masterViewMode) {
			    this.getJobsWithPagination();
			    return;
		    }

		    // in other case we have to be sure we have an Report Uri
		    if (!this.getReportUri()) {
			    return;
		    }

		    // Add the url of the report itself which we working with
		    this.urlsOfReportsToFetchJobs.push(this.getReportUri());

		    // If we are running CE version we don't need to have deal with report options,
		    // so we need to fetch only jobs of the report itself.
		    if (!config.isProVersion) {
			    this.getJobsOfAllReportsWeHave();
			    return;
		    }

            // in PRO version get the report options first and then jobs
		    var self = this;
		    this.getReportOptions().always(function(){
			    self.getJobsOfAllReportsWeHave();
		    });
        },

	    getReportOptions: function() {

		    var self = this,
			    reportUri = this.getReportUri();

		    return this.request({

			    url: config.contextPath + '/rest_v2/reports' + reportUri + '/options'

		    }).done(function(data) {

			    data = data.reportOptionsSummary;

			    if (data) {
				    for (var i = 0, l = data.length; i < l; i++) {
					    if (data[i].uri) {
						    self.urlsOfReportsToFetchJobs.push(data[i].uri);
					    }
				    }
			    }

		    }).fail(function() {
			    // Don't panic if we failed to fetch all additional report options, it might be OK.
		    });
	    },

	    getJobsOfAllReportsWeHave: function() {

		    var self = this,
			    jobModels = [],
			    fetchedAmount = 0;

		    // fetch jobs for each Report and count how much we got
		    // once we finish with all reports initialize the collection by resetting it
		    _.each(this.urlsOfReportsToFetchJobs, function (reportUri) {

			    self.getJobOfReport(reportUri).done(function(data) {
				    jobModels = jobModels.concat(self.parse(data));
			    }).fail(function(err) {
				    self.trigger('error', err)
			    }).always(function() {

				    fetchedAmount++;

				    if (fetchedAmount === self.urlsOfReportsToFetchJobs.length) {
					    self.reset(jobModels);
				    }
			    })
		    });
	    },

	    getJobsFromBackend: function(startIndex, amountOfJobs) {

		    var requestParams = [
			    "sortType=SORTBY_REPORTURI"
		    ];

		    if (startIndex) {
			    requestParams.push("startIndex=" + startIndex);
		    }
		    if (amountOfJobs) {
			    requestParams.push("numberOfRows=" + amountOfJobs);
		    }
		    if (this.searchingTerm) {
			    var term = encodeURI(this.searchingTerm);
			    /*
			    var modelToSearch = {
				    label: term, // Job Name
				    description: term, // Job Description.
				    reportLabel: term // Report Label - new API parameter
			    };
			    requestParams.push("example=" + json.stringify(modelToSearch));
			    */
			    requestParams.push("label=" + term);
		    }

		    var self = this;
		    var dfr = $.Deferred();
		    var url = config.contextPath + "/rest_v2/jobs?" + requestParams.join("&");

		    this.request({url: url}).done(function(data) {

			    var models = self.parse(data);
			    self.amountOfLoadedJobs += models.length;
			    dfr.resolve(models);

		    }).fail(function(err) {

			    self.trigger('error', err);
			    dfr.reject(err);

		    });

		    return dfr;
	    },

	    // This method can get jobs not only of the specific report
	    // but also all jobs which are available on the server.
	    // This can be done by sending request without report information
	    getJobOfReport: function(reportUri) {
		    var url = config.contextPath + "/rest_v2/jobs?reportUnitURI=" + encodeURI(reportUri);
		    return this.request({url: url});
	    },

	    getJobsWithPagination: function() {
		    this.amountOfLoadedJobs = 0; // reset this variable if it was used before
		    var self = this;
		    this.getJobsFromBackend(this.amountOfLoadedJobs, this.initialAmountOfJobsToLoad).done(function(models) {
			    self.reset(models);
		    });
	    },

	    loadMoreJobs: function() {
		    var self = this;
		    this.getJobsFromBackend(this.amountOfLoadedJobs, this.amountOfJobsToLoad).done(function(models) {
			    self.add(models);
		    });
	    },

	    // parse response from server
	    parse: function(response) {
		    return response ? response.jobsummary : [];
	    },

	    request: function(options) {
		    options = options || {};
		    options.cache = false;
		    return Backbone.sync.call(this, 'read', new Backbone.Model(), options);
	    },

        permission: function(url, callback) {
            // call backbone sync method manually
            return Backbone.sync.call(this, 'read', new Backbone.Model(), {
                url: config.contextPath + '/rest_v2/resources/' + url.replace(/\/[^\/]+$/, ''),
                cache: false,
                headers:{ 'Accept': 'application/repository.folder+json' },
                type: 'GET',
                success: function(data, xhr){
                    if ('function' === typeof callback)
                        callback(undefined, data.permissionMask);
                },
                error: function(err){
                    if ('function' === typeof callback)
                        callback(err);
                }
            });
        }

    });

});