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

/* global define, isSupportsTouch, isIPad */

define(function (require) {

	"use strict";

    var $ = require('jquery'),
        _ = require("underscore"),
	    i18n = require("bundle!all"),
        Backbone = require('backbone'),
		common= require("utils.common"),
	    domUtil = require("common/util/domUtil"),
		TouchController = require("touchcontroller"),
		InfiniteScroll = require("tools.infiniteScroll"),
        jobView = require('scheduler/view/jobView'),
        jobModel = require('scheduler/model/jobModel'),
        jobsCollection = require('scheduler/collection/jobsCollection'),
        schedulerUtils = require("scheduler/util/schedulerUtils"),
        jobsViewTemplate = require("text!scheduler/template/jobsViewTemplate.htm"),
        masterViewTemplate = require("text!scheduler/template/masterViewTemplate.htm"),
        listOfJobsTemplate = require("text!scheduler/template/list/listOfJobsTemplate.htm"),
        nothingToDisplayTemplate = require("text!scheduler/template/list/nothingToDisplayTemplate.htm");

	return Backbone.View.extend({

        // binded events for view
        events: {
            'click [name=backButton]': 'backButtonClick',
            'click [name=scheduleJob]': 'createButtonClick',
            'click [name=runJob]': 'runButtonClick',
            'click [name=refreshList]': 'refreshButtonClick',
	        // search listeners
	        'keydown [name=jobSearchInput]': 'jobSearchInputKeyDown',
	        'click [name=jobSearchIcon]': 'jobSearchIconClick',
	        'click [name=jobSearchClear]': 'clearJobSearch'
        },

        // initialize view
        initialize: function(options) {
            this.options = _.extend({}, options);

            this.options = _.omit(this.options, "model");

            // create collection
            this.jobsViewCollection = new jobsCollection(null, this.options);

			// handle collection changes
            this.listenTo(this.jobsViewCollection, "add", this.createAndAddJobView);
            this.listenTo(this.jobsViewCollection, "reset destroy", this.renderCollection);
        },

	    // get jobs from the server
	    refresh: function() {
		    this.jobsViewCollection.fetch();
	    },

		searchJobs: function () {
			var searchTerm = this.$el.find("[name=jobSearchInput]").val();
			if (!searchTerm) {
				return;
			}

			this.$el.find("[name=jobSearchClear]").addClass("up");

			this.jobsViewCollection.setSearchingTerm(searchTerm);
			this.jobsViewCollection.fetch();
		},

		clearJobSearch: function() {
			this.$el.find("[name=jobSearchClear]").removeClass("up");
			this.$el.find("[name=jobSearchInput]").val("");
			this.jobsViewCollection.setSearchingTerm("");
			this.jobsViewCollection.fetch();
		},

        render: function() {

	        var template = this.options.masterViewMode ? masterViewTemplate : jobsViewTemplate;
	        var templateData = {
		        i18n: i18n,
		        reportUri: this.options.reportUri,
				searchTerm: this.jobsViewCollection.getSearchingTerm()
	        };

	        this.setElement($(_.template(template, templateData)));
			
	        this.renderCollection();
        },

	    renderCollection: function() {

		    var jobsContainer = this.$el.find("[name=jobsContainer]").empty();

		    // render collection items
		    if (this.jobsViewCollection.length) {

			    jobsContainer.append($(_.template(listOfJobsTemplate, {})));

			    // iterate over collection and add items
			    this.jobsViewCollection.each(_.bind(this.createAndAddJobView, this));

				// initialize the infinite scroll.
			    this.initInfiniteScroll();

			    this.adjustTableHeaderWidth();

			} else {
			    jobsContainer.append($(_.template(nothingToDisplayTemplate, {
				    message: this.options.masterViewMode ? i18n["report.scheduling.list.no.jobs"] : i18n["report.scheduling.list.no.jobs.for.report"]
			    })));

			}
	    },

		initInfiniteScroll: function() {

			var resultList = this.$el.find("#listOfJobs")[0];
			var resultsContainer = this.$el.find("#jobsSummaryContainer")[0];

			if (!this.options.masterViewMode || !resultList) {
				return;
			}

			if (isSupportsTouch()){
				if(!this._touchController){
					this._touchController = new TouchController(resultList, resultList.up(),{
						forceLayout: true
					});
				}
				this.listenTo(resultList, "layout_update orientationchange", function(){
					$(resultList).css('min-width', $(resultsContainer).width()+'px');
					resultList.width($(resultsContainer).width());
				});
			}

			if(isIPad()){
				this._infiniteScroll = new InfiniteScroll({
					control: resultsContainer,
					content: resultList,
					scroll: this._touchController || undefined
				});
			} else {
				this._infiniteScroll = new InfiniteScroll({
					control: resultsContainer,
					content: resultList
				});
			}

			this._infiniteScroll.onLoad = _.bind(function() {
				this._infiniteScroll.wait();
				this.jobsViewCollection.loadMoreJobs();
				this._infiniteScroll.stopWaiting();
			}, this);

		},

		// yes, it's a CSS hack
		adjustTableHeaderWidth: function() {
			var header = this.$el.find(".content > .subheader .wrap");
			var table = this.$el.find(".content > .body");

			var hasScroll = domUtil.hasScrollBar(table[0], 'vertical');

			header.css("margin-right", (hasScroll ? 15 : 0) + "px");
		},

		jobSearchInputKeyDown: function(event) {

			// on Esc key we'll clean the search area
			if (event.keyCode === 27) {

				// prevent other keyboard related logic to handle this Esc keypress
				event.preventDefault();
				event.stopPropagation();

				this.clearJobSearch();
			}

			// we expect Enter/Return key to run the search
			if (event.keyCode === 13) {

				// prevent other keyboard related logic to handle this Esc keypress
				event.preventDefault();
				event.stopPropagation();

				this.searchJobs();
			}
		},

		jobSearchIconClick: function() {
			this.searchJobs();
		},

	    // add one element to collection
	    createAndAddJobView: function(item) {

		    var view = new jobView({
			    model: item,
			    masterViewMode: this.options.masterViewMode
		    });

			this.listenTo(view, "editJobPressed", this.editJobPressed);

			var element = view.render().$el;

		    this.$el.find("[name=listOfJobs]").append(element);
	    },

		editJobPressed: function(jobId) {
			this.trigger("editJobPressed", jobId);
		},

		// go back to previous location
        backButtonClick: function(){
            this.trigger("backButtonPressed");
        },

        // handle new job creation
        createButtonClick: function() {
	        this.trigger("createNewJobRequest");
        },

        // create new job and run it immediately
        runButtonClick: function() {
	        this.trigger("runNowRequest");
        },

	    refreshButtonClick: function() {
		    this.refresh();
	    }

	});

});