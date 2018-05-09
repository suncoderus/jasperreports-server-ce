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

/* global dialogs, alert, console, buttonManager, Report */

define(function(require) {

    var JReport = require("jasperreports-report"),
        $ = require("jquery"),
        css = require("csslink!jive.vm.css"),
        browserDetection = require("common/util/browserDetection");

    require("jquery-ui/jquery.ui.draggable");

    // i18n
    var jivei18nText = require("text!jive.i18n.tmpl"),
        jivei18n = JSON.parse(jivei18nText),
        i18n = {
            get: function (key) {
                if (jivei18n.hasOwnProperty(key)) {
                    return jivei18n[key];
                } else {
                    return key;
                }
            }
        };

    var Viewer = function(options) {
        var it = this;
        it.reportInstance = null;
        it.jive = null;
        it.isUndoRedo = false;
        it.config = {
            at: null,
            reporturi: null,
            async: true,
            page: 0,
            toolbar: true
        };
        it.dfds = {
            'jive.inactive': null
        };
        it.loading = false;
        it.loaded = false;
        it.search = {
            currentIndex: 0,
            results: []
        };
        it.tabs = {
            maxCount: null,
            maxLabelLength: null
        };

        $.extend(it.config, options);

        it.container = it._getContainer();
        it.hyperlinkHandlers = {};

        it.stateStack.newState();

        $('body').on({
            'jive.initialized': function(evt, jive) {
                it.jive = jive;
            },
            'jive.inactive': function() {
                it.dfds['jive.inactive'] && it.dfds['jive.inactive'].resolve();
            }
        });

        it.zoomAdhocViewDfd = null;
        it.features = {
            zoom: {
                options: [],
                optionType: 'toggle',
                selectedKeys: ['1', 'fit_actual'],
                onClick: function(val) {
                    it.saveCurrentLevel();
                    it.processZoomOption(val);
                    if (val === 'fit_actual' || val === '1') {
                        it.features.zoom.selectedKeys = ['1', 'fit_actual'];
                    } else {
                        it.features.zoom.selectedKeys = [val];
                    }
                    it.zoomChanged();
                },
				levels: {
                    previous: {value: null, literal: null},
					current: {value: 1, literal: null},
					dflt: null
				},
				containerWidth: $('div#reportContainer').width(),
                localZoomChange: false
            },
            search: {
                options: [],
                optionType: 'select',
                selectedKeys: [],
                config: {
                    caseSensitive: false,
                    wholeWordsOnly: false
                },
                onClick: function(val) {
                    var cfg = it.features.search.config,
                        prop;
                    cfg[val] = !cfg[val];

                    it.features.search.selectedKeys = [];
                    for (prop in cfg) {
                        if (cfg.hasOwnProperty(prop) && cfg[prop]) {
                            it.features.search.selectedKeys.push(prop);
                        }
                    }
                }
            }
        };

        /********** Report Container Zoom **********/
        if ($('script#reportZoomText').size() > 0) {
            it.features.zoom.options = JSON.parse($('script#reportZoomText').html());

            $('button#zoom_in').on('click', function(evt) {
                it.saveCurrentLevel();
                it.zoomIn();
                it.zoomChanged();
            });
            $('button#zoom_out').on('click', function(evt) {
                it.saveCurrentLevel();
                it.zoomOut();
                it.zoomChanged();
            });

            $('button#zoom_value_button').on('click', function(evt) {
                var btnOpt = $(this),
                    offset = $('input#zoom_value').offset(),
                    optionsMenu = it.getOptionsMenu('zoom'),
                    openFor = optionsMenu.data('openFor') || "";

                if (optionsMenu.is(':visible') && openFor == "zoom") {
                    optionsMenu.hide();
                    optionsMenu.data('openFor', null);
                } else {
                    if (optionsMenu.is(':visible') && openFor != "zoom") {
                        optionsMenu.hide();
                    }
                    optionsMenu.show().offset({
                        left: offset.left,
                        top: offset.top + btnOpt.height()
                    });
                    optionsMenu.data('openFor', 'zoom');
                }
            });

            // restore zoom on resize
            $(window).on('resize', function() {
                var zf = it.features.zoom,
                    currentZoom = $.extend({}, zf.levels.current);

                it.processZoomOption(1);
                zf.containerWidth = $('div#reportContainer').width();
                it.processZoomOption(currentZoom.literal ? currentZoom.literal : currentZoom.value);
            });
        }
        /************************/

        /*********** Report Search **********/
		if ($('script#reportSearchText').size() > 0) {
            it.features.search.options = JSON.parse($('script#reportSearchText').html());
            $('input#search_report').on('keyup', function(evt) {
                if (evt.which == 13) { // on enter key released
                    if (this.value.length) {
                        it.reportInstance.search({
                            searchString: this.value,
                            caseSensitive: it.features.search.config.caseSensitive,
                            wholeWordsOnly: it.features.search.config.wholeWordsOnly
                        });
                    } else {
                        // FIXME: use localized message
                        dialogs.errorPopup.show("Search string cannot be empty!");
                    }
                }
            });

            $('button#search_report_button').on('click', function(evt) {
                $('input#search_report').trigger($.Event("keyup", {which: 13}));
            });

            $('button#search_options').on('click', function(evt) {
                var btnOpt = $(this),
                    offset = $('input#search_report').offset(),
                    optionsMenu = it.getOptionsMenu('search'),
                    openFor = optionsMenu.data('openFor') || "";

                if (optionsMenu.is(':visible') && openFor == "search") {
                    optionsMenu.hide();
                    optionsMenu.data('openFor', null);
                } else {
                    if (optionsMenu.is(':visible') && openFor != "search") {
                        optionsMenu.hide();
                    }
                    optionsMenu.show().offset({
                        left: offset.left,
                        top: offset.top + btnOpt.height()
                    });
                    optionsMenu.data('openFor', 'search');
                }
            });

            var btnSearchNext = $('button#search_next'),
                btnSearchPrev = $('button#search_previous');

            btnSearchNext.on('click', function(evt) {
                var spans = $('.jr_search_result'),
                    currentPage = it.reportInstance.currentpage,
                    nextPage = null,
                    resultsForPage = 0, i, ln, elem;

                // search results are sorted by page number in ascending order
                for (i = 0, ln = it.search.results.length; i < ln; i++) {
                    if (currentPage == it.search.results[i].page - 1) {
                        resultsForPage = it.search.results[i].hitCount;
                    }
                    if (currentPage < it.search.results[i].page - 1) {
                        nextPage = it.search.results[i].page - 1;
                        break;
                    }
                }

                if (nextPage == null) {
                    nextPage = it.search.results[0].page - 1;
                }

                if (it.search.currentIndex < resultsForPage - 1) {
                    spans.eq(it.search.currentIndex).removeClass('highlight');
                    elem = spans.eq(++it.search.currentIndex);
                    elem.addClass('highlight');
                    it.scrollElementIntoView(elem[0]);

                    it.search.currentPage = currentPage;
                } else {
                    if (nextPage == currentPage) {
                        spans.eq(it.search.currentIndex).removeClass('highlight');
                        it.search.currentIndex = 0;

                        elem = spans.eq(it.search.currentIndex);
                        elem.addClass('highlight');
                        it.scrollElementIntoView(elem[0]);

                        it.search.currentPage = currentPage;
                    } else {
                        it.disableSearchButtons();
                        it.reportInstance.gotoPage(nextPage).then(function(reportInstance) {
                            it.enableSearchButtons();
                            var elem = $('.jr_search_result:first');
                            elem.addClass('highlight');
                            it.scrollElementIntoView(elem[0]);
                            it.search.currentIndex = 0;
                            it.search.currentPage = reportInstance.currentpage;
                        });
                    }
                }
            });

            btnSearchPrev.on('click', function(evt) {
                var spans = $('.jr_search_result'),
                    currentPage = it.reportInstance.currentpage,
                    prevPage = null,
                    prevPageResults = 0,
                    resultsForPage = 0, i, elem;

                for (i = it.search.results.length - 1; i >= 0; i--) {
                    if (currentPage == it.search.results[i].page - 1) {
                        resultsForPage = it.search.results[i].hitCount;
                    }
                    if (currentPage > it.search.results[i].page - 1) {
                        prevPage = it.search.results[i].page - 1;
                        prevPageResults = it.search.results[i].hitCount;
                        break;
                    }
                }

                if (prevPage == null) {
                    prevPage = it.search.results[it.search.results.length - 1].page - 1;
                    prevPageResults = it.search.results[it.search.results.length - 1].hitCount;
                }

                if (it.search.currentIndex > 0) {
                    spans.eq(it.search.currentIndex).removeClass('highlight');
                    elem = spans.eq(--it.search.currentIndex);
                    elem.addClass('highlight');
                    it.scrollElementIntoView(elem[0]);

                    it.search.currentPage = currentPage;
                } else {
                    if (prevPage == currentPage) {
                        spans.eq(it.search.currentIndex).removeClass('highlight');
                        it.search.currentIndex = resultsForPage - 1;

                        elem = spans.eq(it.search.currentIndex);
                        elem.addClass('highlight');
                        it.scrollElementIntoView(elem[0]);

                        it.search.currentPage = currentPage;
                    } else {
                        it.disableSearchButtons();
                        it.reportInstance.gotoPage(prevPage).then(function(reportInstance) {
                            it.enableSearchButtons();
                            var elem = $('.jr_search_result:last');
                            elem.addClass('highlight');
                            it.scrollElementIntoView(elem[0]);
                            it.search.currentIndex = prevPageResults - 1;
                            it.search.currentPage = reportInstance.currentpage;
                        });
                    }
                }
            });
        }
        /*********************/

        /*************** Report Bookmarks **************/
        it.bookmarksContainer = null;
        $('button#bookmarksDialog').on('click', function() {
            it.bookmarksContainer && it.bookmarksContainer.show().offset({left: 10, top: 95});
        });
        /*****************************/

    };

    Viewer.privateMethods = {
        // bookmarks
		prepareBookmarks: function(bookmarks) {
			var it = this,
				container = $('div#jr_bookmarks'),
				body,
				html;

            // if bookmarks already in place, do nothing
            if (container.length && container.find('.jrbookmark').size() > 0) {
                return;
            }

			if (!container.length) {
				container = $(
					"<div id='jr_bookmarks' class='panel dialog overlay moveable sizeable' style='width: 250px; height: 600px; z-index: 2000; display: none'>" +
                        "<div class='sizer diagonal ui-resizable-handle ui-resizable-se'></div>" +
						"<div class='content hasFooter'>" +
							"<div class='header mover'>" +
                                "<div class='closeIcon'></div>" +
                                "<div class='title'>" + i18n.get("bookmarks.dialog.title") + "</div>" +
                            "</div>" +
						    "<div class='body' style='top: 29px; bottom: 24px; padding: 0'></div>" +
                            "<div class='footer' style='height: 24px'></div>" +
						"</div>" +
					"</div>"
				).appendTo('body');
				container.on('click', 'a.jrbookmark', function(evt) {
					evt.preventDefault();
					var span = $(this),
						pageIndex = span.data('pageindex'),
						anchor = span.text();

					if (it.reportInstance.currentpage == pageIndex) {
							window.location.hash = anchor;
					} else {
						it.reportInstance.gotoPage(pageIndex).then(function() {
							window.location.hash = anchor;
						});
					}
				});
                container.on('click', 'div.closeIcon', function() {
                    container.hide();
                });
                container.on('click', 'b.icon', function() {
                    var it = $(this),
                        parentLi;
                    if (!it.is('.noninteractive')) {
                        parentLi = it.closest('li.subtree');
                        if (parentLi.is('.open')) {
                            parentLi.removeClass('open').addClass('closed');
                        } else {
                            parentLi.removeClass('closed').addClass('open');
                        }
                    }
                });
				container.draggable({ handle: ".mover" });
                container.resizable({ handles: {se: '.sizer'} });

                it.bookmarksContainer = container;
			}
			body = container.find('.body');
			body.empty();

			html = "<ul class='list collapsible type_basic'>";
			$.each(bookmarks, function(i, bookmark) {
				html += it.exportBookmark(bookmark);
			});
			html += "</ul>";

			body.html(html);
		},
		exportBookmark: function(bookmark) {
			var it = this,
				html = "",
				href = 'JR_BKMRK_0_' + bookmark.pageIndex + '_' + bookmark.elementAddress,
				label = bookmark.label || href;

			if (bookmark.bookmarks && bookmark.bookmarks.length) {
		    	html += "<li class='node open subtree'><p class='wrap button'><b class='icon'></b><a href='" + href + "' data-pageindex='" + bookmark.pageIndex + "' class='jrbookmark'>" + label + "</a></p>";
                html += "<ul class='list collapsible'>";
				$.each(bookmark.bookmarks, function(i, bkmk) {
					html += it.exportBookmark(bkmk);
				});
				html += "</ul>";
			} else {
                html += "<li class='leaf'><p class='wrap button'><b class='icon noninteractive'></b><a href='" + href + "' data-pageindex='" + bookmark.pageIndex + "' class='jrbookmark'>" + label + "</a></p>";
            }

			html += "</li>"

			return html;
		},
        cleanupBookmarks: function() {
            this.bookmarksContainer && this.bookmarksContainer.find('.body').empty();
        },

        // report parts
        prepareReportParts: function(parts, forceRedraw) {
            var it = this,
                partsContainer = $('div#reportPartsContainer'),
                navigationContainer = $('div#reportPartsNavigation'),
                html = "";

            // if parts already in place, do nothing
            if (partsContainer.length && partsContainer.find('div.reportPart').size() > 0 && !forceRedraw) {
                return;
            }

            if (!it.initializedPartsContainer) {
                partsContainer.parent().show();
                partsContainer.on('click', 'div.reportPart', function(evt) {
                    evt.preventDefault();
                    var tab = $(this),
                        pageIndex = tab.data('pageindex');

                    if (it.reportInstance.currentpage != pageIndex) {
                        partsContainer.find('div.reportPart.active').removeClass('active');
                        tab.addClass('active');
                        it.reportInstance.gotoPage(pageIndex);
                    }
                });

                navigationContainer.on('click', 'button#part_prev', function(evt) {
                    var activeTab = partsContainer.find('div.reportPart.active'),
                        prevPart,
                        prevTab = activeTab.prev();

                    if (!prevTab.length) {
                        prevPart = it.getPreviousPart(activeTab);
                        partsContainer.find('div.reportPart:last').remove();
                        prevTab = $(it.exportPart(prevPart));
                        partsContainer.prepend(prevTab);
                    }

                    $(this).prop('disabled', true);
                    prevTab.trigger('click');
                });
                navigationContainer.on('click', 'button#part_next', function(evt) {
                    var activeTab = partsContainer.find('div.reportPart.active'),
                        nextPart,
                        nextTab = activeTab.next();

                    if (!nextTab.length) {
                        nextPart = it.getNextPart(activeTab);
                        partsContainer.find('div.reportPart:first').remove();
                        nextTab = $(it.exportPart(nextPart));
                        partsContainer.append(nextTab);
                    }

                    $(this).prop('disabled', true);
                    nextTab.trigger('click');
                });

                it.reportPartsContainer = partsContainer;
                it.initializedPartsContainer = true;
            }
            partsContainer.empty();
            it.partsStartIndex = [];

            $.each(parts, function(i, part) {
                if (i < it.tabs.maxCount) {
                    html += it.exportPart(part);
                }
                it.partsStartIndex.push(part.idx);
            });

            partsContainer.html(html);
            it.markActiveReportPart();
        },
        exportPart: function(part) {
            var it = this,
                name = part.name;

            if (part.name.length > it.tabs.maxLabelLength) {
                name = part.name.substring(0, it.tabs.maxLabelLength) + "...";
            }

            return "<div class='reportPart' role='button' tabindex='0' js-navtype='button' data-pageindex='"
                + part.idx + "'><span aria-label='" + name +"'>" + name + "</span></div>";
        },
        getNextPart: function(tab) {
            var it = this,
                parts = it.getReportParts(),
                indexOfTab = it.partsStartIndex.indexOf(tab.data('pageindex'));

            return parts[indexOfTab + 1];
        },
        getPreviousPart: function(tab) {
            var it = this,
                parts = it.getReportParts(),
                indexOfTab = it.partsStartIndex.indexOf(tab.data('pageindex'));

            return parts[indexOfTab - 1];
        },
        getReportParts: function() {
            var it = this,
                parts = it.reportInstance.components.reportparts[0].config.parts;

            if (it.reportInstance.reportComponents && it.reportInstance.reportComponents.reportparts
                && it.reportInstance.reportComponents.reportparts[0].config.parts.length > parts.length) {
                parts = it.reportInstance.reportComponents.reportparts[0].config.parts;
            }

            return parts;
        },
        markActiveReportPart: function() {
            var it = this,
                activePartIndex = -1,
                activePartStartIndex = -1,
                btnPrev,
                btnNext,
                tabToActivate,
                parts,
                html,
                maxParts = it.tabs.maxCount;

            if (!it.reportPartsContainer) {
                return;
            }

            $.each(it.partsStartIndex, function (i, sIndex) {
                if (it.reportInstance.currentpage >= sIndex) {
                    activePartIndex = i;
                    activePartStartIndex = sIndex;
                }
            });

            if (activePartIndex != -1) {
                it.reportPartsContainer.find('div.reportPart.active').removeClass('active');
                tabToActivate = it.reportPartsContainer.find("div.reportPart[data-pageindex='"+ activePartStartIndex + "']");

                if (!tabToActivate.length) {
                    it.reportPartsContainer.empty();

                    parts = it.getReportParts();
                    html = "";

                    var i = 0, j, min, partsForTabs;
                    if (activePartIndex !== 0) {
                        i = parseInt(parseInt(activePartIndex/maxParts) * maxParts);
                        partsForTabs = parts.slice(i, i + maxParts); // try to select maxParts parts

                        // if we don't have enough parts try to lower the index
                        if (partsForTabs.length < maxParts && i - (maxParts - partsForTabs.length) > 0) {
                            i = i - (maxParts - partsForTabs.length);
                        }
                    }

                    min = Math.min(parts.length, i + maxParts);
                    for (j = i; j < min; j++) {
                        html += it.exportPart(parts[j]);
                    }

                    it.reportPartsContainer.html(html);
                    tabToActivate = it.reportPartsContainer.find("div.reportPart[data-pageindex='"+ activePartStartIndex + "']");
                }

                tabToActivate.addClass('active');

                btnPrev = it.reportPartsContainer.parent().find('#part_prev');
                btnNext = it.reportPartsContainer.parent().find('#part_next');

                if (activePartIndex === 0) {
                    btnPrev.prop('disabled', true);
                } else {
                    btnPrev.prop('disabled', false);
                }

                if (activePartIndex < it.partsStartIndex.length - 1) {
                    btnNext.prop('disabled', false);
                } else {
                    btnNext.prop('disabled', true);
                }
            }
        },
        cleanupParts: function() {
            this.reportPartsContainer && this.reportPartsContainer.find('.body').empty();
            this.partsStartIndex = [];
        },

        // search
        enableSearchButtons: function() {
            $('button#search_next').prop('disabled', false);
            $('button#search_previous').prop('disabled', false);
        },
        disableSearchButtons: function() {
            $('button#search_next').prop('disabled', true);
            $('button#search_previous').prop('disabled', true);
        },
        resetSearch: function(bKeepSearchText) {
            var it = this;
            it.search = {
                currentIndex: 0,
                results: []
            };

            !bKeepSearchText && $('input#search_report').val('');
            $('button#search_next').prop('disabled', true);
            $('button#search_previous').prop('disabled', true);

        },
        restoreLastActiveSearchHighlight: function() {
            var it = this, spans;
            if (it.search.results.length) {
                if (it.reportInstance.currentpage == it.search.currentPage) {
                    spans = $('.jr_search_result');
                    spans.eq(it.search.currentIndex).addClass('highlight');
                }
            }
        },

        getOptionsMenu: function(feature) {
            var it = this,
                featureConfig = it.features[feature],
                options = featureConfig.options,
                menu = $("div#templateElements > div#reportViewerMenuHolder > div#vwroptions > div.menu"),
                optionsContainer, i, opt, buf, once = false;

            if (menu.size() === 0) {
                buf = "<div id ='reportViewerMenuHolder'><div id='vwroptions'><div class='menu vertical dropDown fitable' style='display: none; z-index: 99999'><div class='content'><ul>";
                buf += "</ul></div></div></div></div>";
                menu = $("div#templateElements").append(buf).find('div#vwroptions div.menu');
                menu.on('mouseleave', function(evt) {
                    $(this).hide();
                });
            }

            optionsContainer = menu.find('ul');

            // cleanup
            optionsContainer.empty();
            menu.off('click', 'p');

            for (i = 0; i < options.length; i++) {
                opt = options[i];
                if (opt.key.startsWith('fit_') && !once) {
                    once = true;
                    optionsContainer.append("<li class='leaf separator'></li>");
                }
                if (featureConfig.selectedKeys.length && $.inArray(opt.key, featureConfig.selectedKeys) != -1) {
                    optionsContainer.append("<li class='leaf'><p class='wrap toggle button down' data-val='" + opt.key + "'><span class='icon'></span>" + opt.value + "</p></li>");
                } else {
                    optionsContainer.append("<li class='leaf'><p class='wrap toggle button' data-val='" + opt.key + "'><span class='icon'></span>" + opt.value + "</p></li>");
                }
            }

            menu.on('click', 'p', function(evt) {
                var p = $(this),
                    val = p.attr('data-val');

                if (featureConfig.optionType == 'select') {
                    p.closest('ul').find('p').removeClass('down');
                    p.addClass('down');
                } else if (featureConfig.optionType == 'toggle') {
                    p.toggleClass('down');
                }

                featureConfig.onClick.apply(null, [val]);
                menu.hide();
            });

            return menu;
        },

		// zoom
		getZoomByValue: function(val) {
			var zf = this.features.zoom,
                reportViewFrame = $('div#reportViewFrame div.body:first'),
                jrPage = $('table.jrPage'),
				zum;

            // fix case when report is viewed with viewAsDashboardFrame=true
            // JRS-4235 Bug 39573 - [case 54645] ReportViewer Zooming issue with viewAsDashboardFrame=true set in URI
            if (!reportViewFrame.length) {
                reportViewFrame = $('#reportContainer');
            }

			if (val === 'fit_width') {
				zum = zf.containerWidth/jrPage.width();
			} else if (val === 'fit_height') {
				zum = reportViewFrame.height()/jrPage.height();
			} else if (val === 'fit_page') {
				var availableHeight = reportViewFrame.height(),
					availableWidth = zf.containerWidth,
					pageHeight = jrPage.height(),
					pageWidth = jrPage.width();

				zum = Math.min(availableWidth/pageWidth, availableHeight/pageHeight);
			} else if (val === 'fit_actual') {
				zum = 1;
			} else {
				zum = parseFloat(val);
			}

			return zum;
		},
		processZoomOption: function(val) {
			var it = this,
				zum,
				literalValue,
				zoomLevels = it.features.zoom.levels;

			if (typeof val === 'string') {
				zum = it.getZoomByValue(val);
				if (val.indexOf('fit_') === 0) {
					literalValue = val;
				}
			} else {
				zum = val;
			}

			zum = parseFloat(zum.toFixed(2));

			$('#zoom_value').val((zum*100).toFixed(0) + '%');

			it.zoomTo(zum, literalValue);
		},
        propagateZoom: function(o) {
            var components, uimodule, uimoduleName;
            if (this.reportInstance && this.reportInstance.components) {
                components = this.reportInstance.components;
                $.each(components, function(i, componentArray) {
                    if (componentArray.length > 0) {
                        uimoduleName = componentArray[0].config.uimodule;
                        // propagate zoom only to the loaded ui modules
                        if (require.defined(uimoduleName)) {
                            uimodule = require(uimoduleName);
                            uimodule.zoom && uimodule.zoom(o);
                        }
                    }
                });
            }
        },
        zoomIn: function(amount, literalValue) {
            var it = this,
                currentZoom = it.features.zoom.levels.current.value,
                amount = amount || 0.1,
                table = $('table.jrPage'),
                tableWidth = table.width(),
                actualTableWidth = tableWidth * currentZoom,
                containerWidth = it.features.zoom.containerWidth,
                newZoomLevel,
                zoom = {};

            newZoomLevel = parseFloat((currentZoom + amount).toFixed(2));

            if (newZoomLevel === 1) {
                it.features.zoom.selectedKeys = ['1', 'fit_actual'];
            } else {
				if (literalValue) {
                	it.features.zoom.selectedKeys = [literalValue];
				} else {
                	it.features.zoom.selectedKeys = ['' + newZoomLevel];
				}
            }

            if (actualTableWidth < containerWidth && tableWidth * newZoomLevel > containerWidth) { // new scaling will overflow the container
                table.css('margin-left', 0); // remove left margin
                it.applyScaleTransform(table, newZoomLevel, '0 0');   // then scale to desired zoom level with '0 0' transform origin
                zoom.overflow = true;
            } else if (actualTableWidth >= containerWidth) {
                it.applyScaleTransform(table, newZoomLevel, '0 0');
                zoom.overflow = true;
            } else {
                it.applyScaleTransform(table, newZoomLevel);  // scale with default '50% 0' transform origin
                zoom.overflow = false;
            }

            zoom.level = newZoomLevel;
			if (literalValue) {
				it.features.zoom.levels.current.literal = literalValue;
			} else {
				it.features.zoom.levels.current.literal = null;
			}
			it.features.zoom.levels.current.value = newZoomLevel;
			// this is used by jive to "know" about the actual size of the report
            it.reportInstance.zoom = zoom;

            $('#zoom_value').val((newZoomLevel*100).toFixed(0) + '%');

            it.propagateZoom(zoom);
        },
        zoomOut: function(amount, literalValue) {
            var it = this,
				currentZoom = it.features.zoom.levels.current.value,
                amount = amount || 0.1,
                table = $('table.jrPage'),
                tableWidth = table.width(),
                actualTableWidth = tableWidth * currentZoom,
                containerWidth = it.features.zoom.containerWidth,
                newZoomLevel,
                zoom = {};

            newZoomLevel = parseFloat((currentZoom - amount).toFixed(2));
            if (newZoomLevel <= 0) {
                newZoomLevel = 0.1;
            }

			if (newZoomLevel === 1) {
				it.features.zoom.selectedKeys = ['1', 'fit_actual'];
			} else {
				if (literalValue) {
					it.features.zoom.selectedKeys = [literalValue];
				} else {
					it.features.zoom.selectedKeys = ['' + newZoomLevel];
				}
			}

            if (actualTableWidth > containerWidth && tableWidth * newZoomLevel < containerWidth) {
                table.css('margin-left', 'auto');
                it.applyScaleTransform(table, newZoomLevel);
                zoom.overflow = false;
            } else if (actualTableWidth <= containerWidth) {
                it.applyScaleTransform(table, newZoomLevel);
                zoom.overflow = false;
            } else {
                it.applyScaleTransform(table, newZoomLevel, '0 0');
                zoom.overflow = true;
            }

            zoom.level = newZoomLevel;
			if (literalValue) {
				it.features.zoom.levels.current.literal = literalValue;
			} else {
				it.features.zoom.levels.current.literal = null;
			}
			it.features.zoom.levels.current.value = newZoomLevel;
			// this is used by jive to "know" about the actual size of the report
            it.reportInstance.zoom = zoom;

            $('#zoom_value').val((newZoomLevel*100).toFixed(0) + '%');

            it.propagateZoom(zoom);
        },
        zoomTo: function(zoomLevel, literalValue) {
			var it = this,
                currentZoom = it.features.zoom.levels.current.value,
                newZoomLevel;

            if(zoomLevel > currentZoom) { // zoom in
                newZoomLevel = parseFloat((zoomLevel - currentZoom).toFixed(2));
                it.zoomIn(newZoomLevel, literalValue);
            } else if (zoomLevel < currentZoom) { // zoom out
                newZoomLevel = parseFloat((currentZoom - zoomLevel).toFixed(2));
                it.zoomOut(newZoomLevel, literalValue);
            } else {
                it.features.zoom.levels.current.literal = literalValue || null;

                // necessary for components that rely on zoom level
                it.reportInstance && it.reportInstance.zoom && (it.reportInstance.zoom.level = zoomLevel);

                if (zoomLevel === 1) {
                    it.features.zoom.selectedKeys = ['1', 'fit_actual'];
                } else {
                    if (literalValue) {
                        it.features.zoom.selectedKeys = [literalValue];
                    } else {
                        it.features.zoom.selectedKeys = ['' + zoomLevel];
                    }
                }
            }
        },
        saveCurrentLevel: function() {
            this.features.zoom.levels.previous.value = this.features.zoom.levels.current.value;
            this.features.zoom.levels.previous.literal = this.features.zoom.levels.current.literal;
        },
        zoomChanged: function() {
            var currentLevel = this.features.zoom.levels.current,
                prevLevel = this.features.zoom.levels.previous;

            if (currentLevel.value != prevLevel.value || currentLevel.literal != prevLevel.literal) {
                this.features.zoom.localZoomChange = true;
                this.reportInstance.saveZoom(currentLevel.literal || currentLevel.value);
            }
        },
        applyScaleTransform: function($container, zoom, origin) {
            var scale = 'scale(' + zoom + ")",
                origin = origin || '50% 0',
                transform =  {
                    '-webkit-transform': scale,
                    '-webkit-transform-origin': origin,
                    '-moz-transform':    scale,
                    '-moz-transform-origin': origin,
                    '-ms-transform':     scale,
                    '-ms-transform-origin': origin,
                    '-o-transform':      scale,
                    '-o-transform-origin': origin,
                    'transform':         scale,
                    'transform-origin': origin
                };

                $container.css(transform);
        },
        scrollElementIntoView: function(elem) {
            this.reportInstance.eventManager.triggerEvent("beforeSearchAdvance");
            elem.scrollIntoView(false);
        },

        render: function(htmlOutput) {
            var it = this;

            // Due to the html markup of the AdHoc Chart, there is an issue when the
            // AdHoc Chart Report is rendering and there is an TopOfThePage Control there:
            // they interfere with each other because AdHoc uses absolute positioning to get as much space as it needs,
            // and this prevents controls from being visible.
            // To fix this issue, we need to move the controls inside the AdHoc report area (which uses the absolute positioning).
            var topOfThePageControls = jQuery("#inputControlsForm.topOfPage");
            if (topOfThePageControls.length) {
                // detach controls at first
                topOfThePageControls.detach();
            }

            // put the html of the report
            it.container.html(htmlOutput);

            if (topOfThePageControls.length) {
                // and now, get controls back to the right place (just before any elements in the report area)
                topOfThePageControls.insertBefore(jQuery("#reportContainer").children()[0]);
            }

            // ARIA fixups
            $('table.jrPage').prop('tabindex', '"0"');
            // Prevent screen-readers from guessing that our table is used only for layout purposes,
            // because of all the blank cells at the edges
            $('table.jrPage').attr('role', 'grid');
            $('table.jrPage tr').attr('role', 'row');
            $('table.jrPage tbody tr').attr('role', 'row');
            $('table.jrPage tr th').attr('role', 'columnheader');
            $('table.jrPage tbody tr th').attr('role', 'columnheader');
            $('table.jrPage tr td').attr('role', 'gridcell');
            $('table.jrPage tbody tr td').attr('role', 'gridcell');

            /*
                Extract scripts in textarea tags.
             */
            try {
                var scripts = [];

                it.container.find("[name='_evalScript']").each(function() {
                    var jo = jQuery(this);
                    scripts.push(jo.html());
                    jo.remove();
                });

                $.each(scripts, function(i, v) {
                    var $ = window.$;
                    eval(v); // jshint ignore: line
                    if(it.reportInstance.loader.jasperPrintName != Report.jasperPrintName) {
                        it.reportInstance.loader.jasperPrintName = Report.jasperPrintName;
                    }
                });
            } catch(ex) {
                alert(ex);
            }
        },
        _getContainer: function() {
            var sel = this.config.at,
                container = $(sel),
                customClass = '_jr_report_container_';
            if (!container.length) {
                container = $('#' + sel);
                if (!container.length) {
                    container = $('.' + sel);
                }
            }
            !container.hasClass(customClass) && container.addClass(customClass);
            return container;
        },
        setupEventsForReport: function(report) {
            var it = this;

            report.on("reportHtmlReady", function() {
                var htm = this.html;
                it.renderReportLater = false;

                var regexMatch = htm.match(/flowExecutionKeyOutput = ["|'](\w+)["|']/);
                if(regexMatch[1]) Report.flowExecutionKeyOutput = regexMatch[1];

                it.dfds['jive.inactive'] = new $.Deferred();

                if(it.jive) {
                    if(!it.jive.active || it.isUndoRedo) {
                        it.isUndoRedo && it.jive.hide();

                        var elemNodesOnly = $(htm).filter(function(){return 1===this.nodeType});  //Address: $(htm) could be an array, comment/text.html() is undefined
                        it.render(elemNodesOnly.removeClass("hidden").html());
                        $('table.jrPage').css({'margin-left': 'auto', 'margin-right': 'auto'}); /* FIXME: remove or place inside CSS file */
                        it.dfds['jive.inactive'].resolve();
                        // Give the table a tabindex so that Standard Navigation can work with it.
                        $('table.jrPage').prop('tabindex', '"0"');
                        // Prevent screen-readers from guessing that our table is used only for layout purposes,
                        // because of all the blank cells at the edges
                        $('table.jrPage').attr('role', 'grid');
                        $('table.jrPage tr').attr('role', 'row');
                        $('table.jrPage tbody tr').attr('role', 'row');
                        $('table.jrPage tr th').attr('role', 'columnheader');
                        $('table.jrPage tbody tr th').attr('role', 'columnheader');
                        $('table.jrPage tr td').attr('role', 'gridcell');
                        $('table.jrPage tbody tr td').attr('role', 'gridcell');
                    } else {
                        it.renderReportLater = true;
                    }
                } else {
                    it.render($(htm).removeClass("hidden").html());
                    $('table.jrPage').css({'margin-left': 'auto', 'margin-right': 'auto'}); /* FIXME: remove or place inside CSS file */
                    // Give the table a tabindex so that Standard Navigation can work with it.
                    $('table.jrPage').prop('tabindex', '"0"');
                    // Prevent screen-readers from guessing that our table is used only for layout purposes,
                    // because of all the blank cells at the edges
                    $('table.jrPage').attr('role', 'grid');
                    $('table.jrPage tr').attr('role', 'row');
                    $('table.jrPage tbody tr').attr('role', 'row');
                    $('table.jrPage tr th').attr('role', 'columnheader');
                    $('table.jrPage tbody tr th').attr('role', 'columnheader');
                    $('table.jrPage tr td').attr('role', 'gridcell');
                    $('table.jrPage tbody tr td').attr('role', 'gridcell');
                    it.dfds['jive.inactive'].resolve();
                }

				var levels = it.features.zoom.levels,
					currentZoom = $.extend({}, levels.current),
					dflt;

				if (!it.features.zoom.localZoomChange && report.status.defaultZoom != null) {
					levels.dflt = dflt = report.status.defaultZoom;

					if (!isNaN(dflt)) {
						currentZoom.value = parseFloat(dflt).toFixed(2);
						currentZoom.literal = null;
					} else if (typeof dflt === 'string' && dflt.indexOf('fit_') === 0) {
						currentZoom.literal = dflt;
					}
				}
				levels.current.value = 1;

                /*
                    For a report with chart(s) it is necessary to postpone zooming the viewer so that,
                    if it contains an Adhoc based chart, it won't interfere with the zooming algorithm
                    for the chart itself. Unfortunatelly at this point we do not know which charts are
                    Adhoc based, so we do it for all the Highcharts chart reports.
                 */
                it.zoomAdhocViewDfd = new $.Deferred();
                it.zoomAdhocViewDfd.then(function() {
                    it.processZoomOption(currentZoom.literal ? currentZoom.literal : currentZoom.value);
                });

                if (!$(htm).find('div.highcharts_parent_container').length) {
                    it.zoomAdhocViewDfd.resolve();
                }

                it.restoreLastActiveSearchHighlight();

                if (!it.tabs.maxCount && report.status.tabs) {
                    it.tabs = report.status.tabs;
                }
                it.markActiveReportPart();
            });
            report.on("beforeAction", function(evt) {
                // reset scrollbars
                var scrollContainer = $('div#reportViewFrame .body');
                scrollContainer.scrollLeft(0);
                scrollContainer.scrollTop(0);

                // search is an action, but the search controls should not be reset before it
                if (!evt.type || evt.type != "search") {
                    it.resetSearch();
                }

                it.cleanupBookmarks();
                it.cleanupParts();
                this.cancelStatusUpdates();

                it.features.zoom.localZoomChange = false;
            });
            report.on("error", function(evt) {
                if (evt.data && evt.data.error && evt.data.message) {
                    // TODO: replace with logger
                    console && console.log && typeof console.log === 'function' && console.log(evt.data.error);
                    dialogs.errorPopup.show(evt.data.message);
                }
                if (evt.type && evt.type === "highchartsInternalError") {
                	buttonManager.disable($("#fileOptions")[0]);
                	buttonManager.disable($("#export")[0]);
                }
            });
            report.on("undo", function() {
                it.isUndoRedo = true;
                it.stateStack.prevState();

                if (it.stateStack.hasNext()) {
                    buttonManager.enable($('#redo')[0]);
                }

                if (!it.stateStack.hasPrevious()) {
                    buttonManager.disable($('#undo')[0]);
                    buttonManager.disable($('#undoAll')[0]);
                }
                this.refreshPage(0);
            });
            report.on("redo", function() {
                it.isUndoRedo = true;
                it.stateStack.nextState();
                buttonManager.enable($('#undo')[0]);
                buttonManager.enable($('#undoAll')[0]);

                if (!it.stateStack.hasNext()) {
                    buttonManager.disable($('#redo')[0]);
                }
                this.refreshPage(0);
            });
            report.on("undoall", function() {
                it.isUndoRedo = true;
                it.stateStack.firstState();
                buttonManager.enable($('#redo')[0]);
                buttonManager.disable($('#undo')[0]);
                buttonManager.disable($('#undoAll')[0]);
                this.refreshPage(0);
            });
            report.on("action", function() {
                it.isUndoRedo = false;
                it.stateStack.newState();
                buttonManager.enable($('#undo')[0]);
                buttonManager.enable($('#undoAll')[0]);
                buttonManager.disable($('#redo')[0]);
                this.refreshPage(0);
            });
            report.on("search", function(evt) {
                var data = evt.data;
                it.isUndoRedo = false;
                if (data && data.result.actionResult.searchResults && data.result.actionResult.searchResults.length) {
                    var results = data.result.actionResult.searchResults,
                        searchPage = results[0].page - 1,
                        currentPage = it.reportInstance.currentpage,
                        i, ln;
                    results.sort(function(r1, r2) {
                        return r1.page - r2.page;
                    });

                    it.search.results = results;

                    // if we have results for the current page, just refresh it
                    for (i = 0, ln = results.length; i < ln; i++) {
                        if (currentPage == results[i].page - 1) {
                            searchPage = currentPage;
                            break;
                        }
                    }

                    this.gotoPage(searchPage).then(function() {
                        var elem = $('.jr_search_result:first');
                        elem.addClass('highlight');
                        it.scrollElementIntoView(elem[0]);

                        it.search.currentPage = searchPage;

                        if (results.length > 1 || (results.length == 1 && results[0].hitCount > 1)) {
                            $('button#search_next').prop('disabled', false);
                            $('button#search_previous').prop('disabled', false);
                        } else {
                            $('button#search_next').prop('disabled', true);
                            $('button#search_previous').prop('disabled', true);
                        }
                    });
                } else if (data && data.result.actionResult.searchString) {
                    // FIXME: use localized message
                    dialogs.errorPopup.show("Jaspersoft has finished searching the document. No matches were found for: \"" + it.encodeString(data.result.actionResult.searchString) + "\"!");
                    it.resetSearch(true);
                    this.gotoPage(0);
                }
            });
            report.on("saveZoom", function() {
                // enable undo/redo; similar to report.on('action') but without triggering report refresh
                it.isUndoRedo = false;
                it.stateStack.newState();
                buttonManager.enable($('#undo')[0]);
                buttonManager.enable($('#undoAll')[0]);
                buttonManager.disable($('#redo')[0]);
            });
            report.on("pageModified", function() {
                this.gotoPage(this.currentpage);
            });
            report.on("reportFinished", function() {
                if (this.status.pageFinal) {
                    it.handleReportBookmarks(this.reportComponents);
                    it.handleReportParts(this.reportComponents);

                    Report.snapshotSaveStatus = this.status.originalStatus.snapshotSaveStatus;
                    Report.lastPageIndex = this.status.originalStatus.lastPageIndex;
                    Report.reportRefreshed();
                } else {
                    this.gotoPage(this.currentpage);
                }
            });
            report.on('componentsRegistered', function() {
                var isAdhocView = false, components = it.reportInstance.components,
                    uimodules = [],
                    uimodule;

                it.loading = false;
                it.loaded = true; // Hack for bug 34982. Conditional statement added to controls.core.js line 229.

                /*
                    Load and initialize JIVE modules
                 */
                $.each(components, function(i, componentArray) {
                    if (componentArray.length > 0) {
                        uimodule = componentArray[0].config.uimodule;
                        if (uimodule) {
                            uimodules.push(uimodule);
                        }
                    }
                });

                it.dfds['jive.inactive'].then(function() {
                    it.renderReportLater && it.render($(it.reportInstance.html).removeClass("hidden").html());
                    if(uimodules.length) {
                        require(uimodules, function() {
                            $.each(arguments, function(i, thisModule) {
                                thisModule.init(it.reportInstance);
                            });
                        });
                    }
                });

                /*
                    If AdhocView based chart, resize report page to width of window.
                 */
                function calculateHighchartsParentContainerHeight() {
                    if (window.location.href.indexOf("viewAsDashboardFrame=true") > -1) {
                        try {
                            var iframes = parent.jQuery("iframe"),
                                currentFrame;

                            for (var i = 0; i < iframes.length; i++) {
                                if (iframes[i].contentWindow === window) {
                                    currentFrame = iframes[i];
                                    break;
                                }
                            }

                            if (currentFrame) {
                                return $(currentFrame).parent().height() - hcParentContainer.position().top;
                            }
                        } catch(e) {
                            return $(document).height()
                                ? $(document).height() - hcParentContainer.position().top
                                : 400;
                        }
                    }

                    return Math.max(400, it.container.height() - hcParentContainer.position().top);
                }
                if(it.reportInstance.components && it.reportInstance.components.chart && it.reportInstance.components.chart.length && it.reportInstance.components.chart[0].config.hcinstancedata.services[0].service == 'adhocHighchartsSettingService') {
                    isAdhocView = true;
                    it.container.css({position:'absolute',top:0,right:0,bottom:0,left:0});
                    var jrPage = $('table.jrPage'),
                        hcParentContainer = jrPage.find('div.highcharts_parent_container'),
                        pageWidth = jrPage.width(),
                        hcTr = hcParentContainer.closest('tr'),
                        allRows = jrPage.find('tr'),
                        hcTrIndex = allRows.index(hcTr);

                    hcParentContainer.css({"font-size": "11px"});


                    jrPage.find('tr:first td').each(function(i, td) {
                        var jo = $(td);
                        jo.css('width', (jo.width() / pageWidth) * 100 + '%');
                    });

                    hcTr.css('height', '100%');	// the row with the chart must be the highest
                    allRows.each(function(i, row) {	// each row below the one with the chart must have 0px in height
                        i > hcTrIndex && $(row).height(0);
                    });

                    jrPage.css({width: '100%', height: '100%'});
                    hcParentContainer.css('height', calculateHighchartsParentContainerHeight() + 'px');

                    delete it.reportInstance.components.chart[0].hcConfig.chart.height;
                    delete it.reportInstance.components.chart[0].hcConfig.chart.width;

                    /*
                        Setup resize events
                     */
                    var hcTimeOut = null;
                    $(window).on('resize', function() {
                        hcTimeOut && clearTimeout(hcTimeOut);
                        hcTimeOut = setTimeout(function() {
                            $(window).trigger('customResizeEnd');
                        }, 500);
                    });

                    $(window).on('customResizeEnd', function() {
                        hcParentContainer.css('height', calculateHighchartsParentContainerHeight() + 'px');
                        $.each(it.reportInstance.components.chart, function(){
                            this.render();
                        });
                    });
                }

                // resolve deferred zoom (necessary for reports with HC charts to be done after the Adhoc ones do their CSS mods for resizing)
                it.zoomAdhocViewDfd.resolve();

                /*
                    If Highcharts are present render them
                 */
                if(components.chart) {
                    $.each(components.chart, function(){
                        var el = $('#'+this.config.hcinstancedata.renderto).length,
                            self = this;

                        // delay Hicharts rendering until container will be fully initialized with sizes
                        // fixes JRS-4228 Bug 36977 - [Case #46447+1] Dashboard pie size incorrect in Firefox,
                        // and JRS-4531 Bug 38264 - [case #51452+2]Overlapped HTML5 chart legend display in a dashboard in FF and IE browsers
                        if(isAdhocView) {
                            el && it.container.height() && setTimeout(function() { self.render(); }, 1);
                        } else {
                            el && setTimeout(function() { self.render(); }, 1);
                        }
                    });
                }

                /*
                 Handle hyperlinks
                 */
                if (components.hyperlinks && components.hyperlinks.length) {
                    var hyperlinks = components.hyperlinks[0].config.hyperlinks,
                        hyperlinksByType = {};

                    $.each(hyperlinks, function(i, hyperlink) {
                        if (!hyperlinksByType[hyperlink.type]) {
                            hyperlinksByType[hyperlink.type] = [];
                        }

                        hyperlinksByType[hyperlink.type].push(hyperlink);
                    });

                    $.each(hyperlinksByType, function(key, links) {
                        require(["jr." + key], function(HyperLinkHandler) {
                            var hh = new HyperLinkHandler(links);
                            hh.reportInstance = it.reportInstance;
                            hh.reportContainer = it.container;
                            hh.register();

                            it.hyperlinkHandlers[key] = hh;
                        }, function(err) {
                            var failedId = err.requireModules && err.requireModules[0];
                            if (failedId && console && console.error && typeof console.error === 'function') {
                                // TODO: replace with logger
                                console.error("Failed to load module: '" + failedId + "' for handling hyperlinks of type: '" + key + "'!");
                            }
                        });
                    });
                }

                /*
                 Handle report parts
                 */
                it.handleReportParts(components);

				/*
					Handle report bookmarks
				 */
                it.handleReportBookmarks(components);

				/*
					Handle webfonts
				 */
				if (components.webfonts && components.webfonts.length) {
					var webFonts = components.webfonts[0].config.webfonts,
						modules = [],
						webFontsConfig = {paths: {}},
						moduleName;

					$.each(webFonts, function(i, webfont) {
						moduleName = 'webfont_' + webfont.id;
						modules.push('csslink!' + moduleName);
						webFontsConfig.paths[moduleName] = webfont.path;
					});

					requirejs.config(webFontsConfig);
					require(modules, function() {
						/*
							IE Webfonts fix
						 */
						if (/msie/i.test(navigator.userAgent)) {
							var links = document.querySelectorAll('link.jrWebFont');
							setTimeout(function() {
								if (links) {
									for (var i = 0; i < links.length; i++) {
										links.item(i).href = links.item(i).href;
									}
								}
							}, 0);
						}
					});
				}

                Report.reportRefreshed();

                // very dirty hack to fix mystical issue http://bugzilla.jaspersoft.com/show_bug.cgi?id=39694
                // for some reason in IE9 window resize event is not happening and Highcharts charts are not rendered
                try {
                    if (browserDetection.isIE9() && (window.location.href.indexOf("viewAsDashboardFrame=true") > -1) && components.chart) {
                        setTimeout(function () {
                            $(window).resize();
                        }, 1);
                    }
                } catch(ex) {}
            });

            report.on('hyperlinkInteraction', function(evt) {
                var hlType = evt.data.hyperlink.type;
                if (hlType && it.hyperlinkHandlers[hlType]) {
                    it.hyperlinkHandlers[hlType].handleInteraction(evt);
                }
            });
        },
        handleReportBookmarks: function(componentsContainer) {
            var it = this;
            if (componentsContainer.bookmarks && componentsContainer.bookmarks.length) {
                it.prepareBookmarks(componentsContainer.bookmarks[0].config.bookmarks);
                $('button#bookmarksDialog').show();
            } else {
                $('button#bookmarksDialog').hide();
                it.bookmarksContainer && it.bookmarksContainer.hide();
            }
        },
        handleReportParts: function(componentsContainer) {
            var it = this;
            if (componentsContainer.reportparts && componentsContainer.reportparts.length) {
                var parts = componentsContainer.reportparts[0].config.parts,
                    forceRedraw =  true;

                // check if we have a different number of parts than existing
                if (it.partsStartIndex && it.partsStartIndex.length == parts.length) {
                    forceRedraw = false;
                }

                it.prepareReportParts(parts, forceRedraw);
                it.reportPartsContainer && it.reportPartsContainer.show();
            }
        },
        setLocation: function(loc) {
            document.location = loc;
        },
        encodeString: function(stringToEncode) {
            return $("<div/>").text(stringToEncode).html();
        }
    };

    Viewer.publicMethods = {
        loadReport: function() {
            var it = this;
            it.loading = true;

            it.reportInstance = new JReport({
                reporturi: it.config.reporturi,
                async: it.config.async,
                page: it.config.page,
                contextPath: it.config.contextPath,
                postProcess: function() {
                    if(this.html.indexOf('id="emptyReportMessageHolder"') > 0) {
                        this.status = {
                            isComponentMetadataEmbedded: false,
                            pageTimestamp: 0,
                            totalPages: -1
                        };
                    } else {
                        this.loader.jasperPrintName = this.status.jasperPrintName;
                        this.loader.contextid = this.status.contextid;
                    }
                },
                container: it.container
            });

            it.setupEventsForReport(it.reportInstance);

            return it.reportInstance.init();
        },
        exit: function() {
            var dfd = new $.Deferred();
            dfd.resolve();
            return this.reportInstance ? this.reportInstance.loader.exit() : dfd;
        }
    };

    Viewer.actionStateCounter = {
        stateStack: {
            counter: 0,
            states: [],
            position: -1
        },
        currentState: function() {
            return this.stateStack.currentState();
        }
    };

    $.extend(Viewer.actionStateCounter.stateStack, {
        newState: function() {
            if (this.position + 2 < this.states.length) {
                this.states.splice(this.position + 2, this.states.length - this.position - 2);
            }

            ++this.position;
            ++this.counter;
            this.states[this.position] = this.counter;
        },
        prevState: function() {
            if (this.position > 0) {
                --this.position;
            }
        },
        firstState: function() {
            this.position = 0;
        },
        nextState: function() {
            if (this.position + 1 < this.states.length) {
                ++this.position;
            }
        },
        hasPrevious: function() {
            return this.position > 0;
        },
        hasNext: function() {
            return this.position + 1 < this.states.length;
        },
        currentState: function() {
            return this.states[this.position];
        }
    });

    $.extend(Viewer.prototype, Viewer.privateMethods);
    $.extend(Viewer.prototype, Viewer.publicMethods);
    $.extend(Viewer.prototype, Viewer.actionStateCounter);

    return Viewer;
});
