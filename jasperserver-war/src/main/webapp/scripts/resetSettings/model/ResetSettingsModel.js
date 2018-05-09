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

define(function(require) {
    var Epoxy = require("backbone.epoxy");

    var ResetSettingsModel = Epoxy.Model.extend({
        defaults: {
            id: undefined,
            name: undefined,
            value: "",
            description: ""
        },

        initialize: function() {
            if (!this.get("id")) {
                // By default id is generated from the attribute name.
                this.setId();
            }
        },

        url: function() {
            // duplicated on purpose - overrides some strange behaviour of FF
            var safeId = encodeURIComponent(this.id).replace("'", "%27");
            safeId = safeId.replace("'", "%27");
            return this.collection.url(this.isNew() ? "" : safeId);
        },

        setId: function() {
            var name = this.get("name"),
                id = this.get("id");

            name !== id && this.set("id", name);
        }

    });

    return ResetSettingsModel;
});

