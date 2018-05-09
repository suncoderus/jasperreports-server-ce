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
 * @author: Lucian Chirita (lchirita@tibco.com)
 * @version: $Id: jr.ReportDataSeries.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(['jquery', 'bi/report/Report', 'jrs.configs', 'adhoc/chart/palette/defaultPalette', 'bundle!CommonBundle'], function($, Report, configs, palette, i18n) {
    var ReportDataSeries = function(arrHyperlinks) {
        this.hyperlinks = arrHyperlinks;
        this.reportInstance = null;
        this.reportContainer = null;
	this.selectedLinkIds = [];
	this.selectedLinks = {};
    };

    ReportDataSeries.prototype = {
        register: function() {
            var it = this;
            $(it.hyperlinks[0].selector).on('click', function(evt) {
                var hlData = it._getHyperlinkData($(this).attr('data-id'));
                if (hlData) {
                    it._handleHyperlinkClick(hlData);
                }
            }).css('cursor', 'pointer');
        },
        handleInteraction: function(evt) {
            if ('hyperlinkClicked' == evt.type) {
                var hlData = this._getHyperlinkData(evt.data.hyperlink.id);
                if (hlData) {
                    this._handleHyperlinkClick(hlData);
                }
            }
        },

        // internal functions
        _getHyperlinkData: function(id) {
            var hlData = null;
            $.each(this.hyperlinks, function(i, hl) {
                if (id === hl.id) {
                    hlData = hl;
                    return false; //break each
                }
            });
            return hlData;
        },
        _handleHyperlinkClick: function(hyperlink) {
            var clickedSpan = $("span[data-id='" + hyperlink.id + "'] span");
	    var foundIdx = this.selectedLinkIds.indexOf(hyperlink.id);
	    if (foundIdx >= 0) {
		var link = this.selectedLinks[hyperlink.id];
		clickedSpan.css('color', link.origColor);
		clickedSpan.css('background-color', link.origBackcolor);

		this.selectedLinkIds.splice(foundIdx, 1);
		delete this.selectedLinks[hyperlink.id];
	    } else {
		var paletteIdx = this.selectedLinkIds.length === 0 ? 0 : ((this.selectedLinks[this.selectedLinkIds[this.selectedLinkIds.length - 1]].paletteIdx + 1) % palette.colors.length);
		var link = {
			hyperlink: hyperlink,
			origColor: clickedSpan.css('color'),
			origBackcolor: clickedSpan.css('background-color'),
			paletteIdx: paletteIdx
		};

		clickedSpan.css('color', '#ffffff');
		clickedSpan.css('background-color', palette.colors[paletteIdx]);

		this.selectedLinkIds.push(hyperlink.id);
		this.selectedLinks[hyperlink.id] = link;
	    }
	    
	    var it = this;
	    var entriesList = [];
	    $.each(this.selectedLinkIds, function(i, linkId) {
		var link = it.selectedLinks[linkId];
		var params = {};
		$.each(link.hyperlink.params, function(index, value) {
			if (index.indexOf("param_") === 0) {
				params[index.substring(6)] = value;
			}
		});
		var entry = {
			params: params,
			color: palette.colors[link.paletteIdx]
		};
		entriesList.push(entry);
	    });
	    
	    if ($("#seriesReport").length === 0) {
		var new_dialog = $('<div id="seriesReport"><div id="seriesReportContainer" style="width:100%;height:100%;"></div></div>');
		new_dialog.insertAfter('#reportContainer');

		var width = hyperlink.params._width || 500;
		var height = hyperlink.params._height;
		$('#seriesReport').css('width', width);
		height && $('#seriesReport').css('height', height);

		new_dialog.dialog({autoOpen: false, width: width, height: (height || 'auto'),
			closeText: i18n['button.close']});
	    }

		if (entriesList.length === 0) {
	    $("#seriesReport").dialog("close");
		} else {
	    $("#seriesReport").dialog("open");
	    
			if (!this.report) {
			    this.report = new Report({
				resource: hyperlink.params._report, 
				container: "#seriesReportContainer",
				server: configs.contextPath}); 
			}
			this.report.params({selectedEntries: [JSON.stringify(entriesList)]});
			this.report.refresh();
		}
        }
    };

    return ReportDataSeries;
});
