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

/* global Class */

var LinkButton = Class.create({

    initialize : function (id, defaultClassName, hoverClassName, disabledClassName) {

        this.id = id;
        this.element = $(id);
        this.disabled = false;

        this.defaultClassName = defaultClassName;
        this.hoverClassName = hoverClassName;
        this.disabledClassName = disabledClassName;

        this.element.className = defaultClassName;

        var inst = this;

        this.element.observe('click', function (event) {
            if (!inst.disabled) {
                inst.onclick(event);
            }
            return false;
        });

        this.element.observe('mouseout', function (event) {

            inst.element.className = (!inst.disabled) ? defaultClassName : disabledClassName;
//            inst.element.style.cursor = 'default';
        });

        this.element.observe('mouseover', function (event) {

            inst.element.className = (!inst.disabled) ? hoverClassName : disabledClassName;
//            inst.element.style.cursor = (!inst.disabled) ? 'pointer' : 'default';
        });
    },

    getElement : function () {

        return this.element;
    },

    setDisabled : function (disabled) {

        this.disabled = disabled;
        this.element.className = (!this.disabled) ? this.defaultClassName : this.disabledClassName;
    },

    onclick : function (event) {

    }
});

var InputButton = Class.create({

    initialize : function (id, defaultClassName, hoverClassName, downClassName, disabledClassName) {

        this.id = id;
        this.element = $(id);
        this.disabled = false;

        this.defaultClassName = defaultClassName;
        this.hoverClassName = hoverClassName;
        this.downClassName = downClassName;
        this.disabledClassName = disabledClassName;

        this.element.className = defaultClassName;

        var inst = this;

        this.element.observe('click', function (event) {
            if (!inst.disabled) {
                inst.onclick(event);
            }
        });

        this.element.observe('mouseout', function (event) {

            inst.element.className = (!inst.disabled) ? defaultClassName : disabledClassName;
        });

        this.element.observe('mouseover', function (event) {

            inst.element.className = (!inst.disabled) ? hoverClassName : disabledClassName;
        });

        this.element.observe('mousedown', function (event) {

            inst.element.className = (!inst.disabled) ? downClassName : disabledClassName;
        });

        this.element.observe('mouseup', function (event) {

            inst.element.className = (!inst.disabled) ? hoverClassName : disabledClassName;
        });
    },

    getElement : function () {

        return this.element;
    },

    setDisabled : function (disabled) {

        this.disabled = disabled;
        this.element.className = (!this.disabled) ? this.defaultClassName : this.disabledClassName;
    },

    onclick : function (event) {

    }
});

var ImageButton = Class.create({

    initialize : function (id, defaultClassName, hoverClassName, downClassName, disabledClassName, defaultImg, hoverImg, downImg, disabledImg) {

        this.id = id;
        this.element = $(id);
        this.disabled = false;

        this.defaultClassName = defaultClassName;
        this.hoverClassName = hoverClassName;
        this.downClassName = downClassName;
        this.disabledClassName = disabledClassName;

        this.defaultImg = defaultImg;
        this.hoverImg = hoverImg;
        this.downImg = downImg;
        this.disabledImg = disabledImg;

        this.element.className = defaultClassName;

        var inst = this;

        this.element.observe('click', function (event) {

            if (!inst.disabled) {
                inst.onclick(event);
            }
            
            return false;
        });

        this.element.observe('mouseout', function (event) {

            inst.element.className = (!inst.disabled) ? defaultClassName : disabledClassName;
            inst.element.src = (!inst.disabled) ? defaultImg : disabledImg;
        });

        this.element.observe('mouseover', function (event) {

            inst.element.className = (!inst.disabled) ? hoverClassName : disabledClassName;
            inst.element.src = (!inst.disabled) ? hoverImg : disabledImg;
        });

        this.element.observe('mousedown', function (event) {

            inst.element.className = (!inst.disabled) ? downClassName : disabledClassName;
            inst.element.src = (!inst.disabled) ? downImg : disabledImg;
        });

        this.element.observe('mouseup', function (event) {

            inst.element.className = (!inst.disabled) ? hoverClassName : disabledClassName;
            inst.element.src = (!inst.disabled) ? hoverImg : disabledImg;
        });
    },

    getElement : function () {

        return this.element;
    },

    setDisabled : function (disabled) {

        this.disabled = disabled;
        this.element.className = (!this.disabled) ? this.defaultClassName : this.disabledClassName;
        this.element.src = (!this.disabled) ? this.defaultImg : this.disabledImg;
    },

    onclick : function (event) {

    }
});

var DivButton = Class.create({

    initialize : function (id, defaultClassName, hoverClassName, downClassName, disabledClassName) {

        this.id = id;
        this.element = $(id);
        this.disabled = false;

        this.defaultClassName = defaultClassName;
        this.hoverClassName = hoverClassName;
        this.downClassName = downClassName;
        this.disabledClassName = disabledClassName;

        this.element.className = defaultClassName;

        var inst = this;

        this.element.observe('click', function (event) {

            if (!inst.disabled) {
                inst.onclick(event);
            }

            return false;
        });

        this.element.observe('mouseout', function (event) {

            inst.element.className = (!inst.disabled) ? defaultClassName : disabledClassName;
        });

        this.element.observe('mouseover', function (event) {

            inst.element.className = (!inst.disabled) ? hoverClassName : disabledClassName;
        });

        this.element.observe('mousedown', function (event) {

            inst.element.className = (!inst.disabled) ? downClassName : disabledClassName;
        });

        this.element.observe('mouseup', function (event) {

            inst.element.className = (!inst.disabled) ? hoverClassName : disabledClassName;
        });
    },

    getElement : function () {

        return this.element;
    },

    setDisabled : function (disabled) {

        this.disabled = disabled;
        this.element.className = (!this.disabled) ? this.defaultClassName : this.disabledClassName;
    },

    onclick : function (event) {

    }
});
