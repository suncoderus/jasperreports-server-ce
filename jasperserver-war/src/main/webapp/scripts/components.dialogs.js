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
 * @author: Yuriy Plakosh
 * @version: $Id$
 */

/**
 * Dialogs Module.
 */

var dialogs = {};

///////////////////////////////////////////
// System confirm object and methods
///////////////////////////////////////////
/**
 * System confirm is used to show system confirm about the last action.
 * It fades up rapidly after the action completes, and fades away on the next mouseDown anywhere on the page.
 */
dialogs.systemConfirm = {
    container: null,
    message: null,
    show: function(message, duration, isWarning) {

        if(window.isEmbeddedDesigner){
            if (isWarning) {
                message = '<span class="warning">' + message + '</span>';
            }

            jQuery(document).trigger("adhocDesigner:notification", [message, duration]);
        }else{
            this.container = this.container || jQuery('#systemMessageConsole').on('mouseup touchend', function() {
                dialogs.systemConfirm.container.slideUp();
            });

            this.message = this.message || document.getElementById('systemMessage');
            if (!this.message) {//unable to find DOM elem to output the system message
                console.warn(message);
                return;
            }

            if (!this.closeText){
                this.closeText = this.message.innerHTML.toLowerCase();
            }

            message = xssUtil.escape(message) + ' <span>| <a href="#">'+ xssUtil.escape(this.closeText) +'</a></span>';
            if (isWarning) {
                this.message.innerHTML = '<span class="warning">' + message + '</span>';
            }
            else {
                this.message.innerHTML = message;
            }

            dialogs.systemConfirm.container.slideDown();
            setTimeout('dialogs.systemConfirm.hide()', duration ? duration : 2000);
        }


    },

    showWarning: function(message, duration) {
        this.show(message, duration, true);
    },

    hide: function() {
        if (dialogs.systemConfirm.container) {
            dialogs.systemConfirm.container.slideUp();
        }
    }
};

jQuery(document).on('systemDialogWarn', function(event, message, duration) {
    dialogs.systemConfirm.showWarning(message, duration);
});

//////////////////////////////////////////////
// Ajax Error Popup Dialog object and methods
//////////////////////////////////////////////

/**
 * Ajax Error dialog is used then any ajax call returns and server side error which was not
 * catched by server. It show popup dialog with stackrtace and close button.
 */
dialogs.errorPopup = {
    _dom: null,

    _content: null,

    /**
     * The identifier of the DOM element.
     */
    _DOM_ID: "standardAlert",

    /**
     * The identifier of the element there error content should be placed.
     */
    _CONTENT_ID: "errorPopupContents",

    /**
     * Pattern of element in response where error is placed
     */
    _PAGE_CONTENT_PATTERN: "#errorPageContent",

    _DIALOG_WIDTH: "546px",

    _DIALOG_HEIGHT: "350px",

    clickHandler: null,

    /**
     * Shows popup dialog.
     *
     * @param errorContent error content to be showed
     */
    show: function(errorContent, isStackTraceContent, options) {
        options || (options = {});

        var fromSource = Builder.node('DIV', {style:'display:none'});
        fromSource.innerHTML = xssUtil.escape(errorContent, {softHTMLEscape: true});
        document.body.insertBefore(fromSource, document.body.firstChild);
        var content = $$(this._PAGE_CONTENT_PATTERN)[0];
        var contentText = content ? content.innerHTML : errorContent;
        var isStackTrace = (content && content.innerHTML);

        fromSource.remove();

        if (contentText) {
           if (!this._dom) {
                this._dom = $(this._DOM_ID);
                this._content = $(this._CONTENT_ID);
                this.clickHandler = this._clickHandler.bindAsEventListener(this);
            }

            if (this._dom) {
                var finalContent = contentText;

                //If error is a plain text - wrap it into <p> element
                if (!isStackTrace) {
                    finalContent = Builder.node('P', {'class':'message'});
                    finalContent.update(xssUtil.escape(contentText, {softHTMLEscape: true}));
                }

                this._content.update(xssUtil.escape(finalContent, {softHTMLEscape: true}));
                this._dom.observe('click', this.clickHandler);

                this._dom.setStyle({height: options.height || this._DIALOG_HEIGHT, width: options.width || this._DIALOG_WIDTH});
                isStackTraceContent && this._dom.addClassName(layoutModule.STACKTRACE_CLASS);
                dialogs.popup.show(this._dom, isStackTraceContent);

                var st = document.getElementById('completeStackTrace');
                if(st) {
                    isIPad() && new TouchController(st,st.parentNode,{noInit3d:true});
                }
            }
        }
    },

    /**
     * Hides popup dialog.
     */
    _hide: function() {
        if (this._dom) {
            this._dom.stopObserving('click', this.clickHandler);
            dialogs.popup.hide(this._dom);
        }
    },

    /*
     * Mouse click handler for close button
     */
    _clickHandler: function(event) {
        var element = event.element();

        if (matchAny(element, ['button'], true)) {
            this._hide();
        }
    }
};

 dialogs.clusterErrorPopup = _.extend({}, dialogs.errorPopup, {
    show: function(errorContent) {
        var that = this;
        require(["bundle!jasperserver_messages", "bundle!jsexceptions_messages"], function(bundle1, bundle2) {
            var clusterErrorPopupMsg = bundle2["cluster.exception.session.attribute.missing.popup"];
            dialogs.errorPopup.show.call(that, clusterErrorPopupMsg);

            var buttonLabel = bundle1["button.home"];
            jQuery("#"+that._DOM_ID).find("button span.wrap").html(buttonLabel);
        })
    },

    _hide: function(event) {
        dialogs.errorPopup._hide.apply(this, arguments);
        window.location = "home.html";
    }
 });


/**
 * generic 'popup' dialog controller
 * @param {Object} elem
 */
dialogs.popup = {
    OWNER_ATTR: "data-owner",

    show: function(elem, showDimmer, options) {
        require(['stdnav', 'jquery'], function(stdnav, jQuery){
            options = options || {};

            elem = $(elem);

            if (!elem) {
                return;
            }

            if ('message' in options) {
                // Update the displayed message in the dialog.
                jQuery(elem).find('.body').text(xssUtil.unescape(options.message));
            }

            //dimmer
            if (showDimmer) {
                pageDimmer.show();
                elem.match(layoutModule.DIALOG_LOADING_PATTERN) && pageDimmer.setZindex(elem.getStyle('zIndex') - 1);
            }

            //ensure body is parent
            reParent(elem, document.body);

            elem.setOpacity(0);

            elem.removeClassName(layoutModule.HIDDEN_CLASS);

            layoutModule.createSizer.call(layoutModule, elem);

            if (options.cascade) {
                //cascade
                cascadeElement(elem, {position: options.position, number: options.number, horzOffset: 40, vertOffset: 40});
            } else {
                //center
                centerElement(elem, {horz: true, vert: true});
            }
            // raise if necessary
            dialogs.popup._setHigherZIndex(elem);

            // Drag'&'Drop depends depends from zIndex on dialog
            layoutModule.createMover.call(layoutModule, elem); // To move dialog on foreground we need initialize DnD after we have zIndex for dialog

            isIPad() ? (elem.setOpacity(1.0) && elem.show()) : appear(elem, 0.4);

            // Keep track of the element that had focus prior to the dialog being
            // shown, so that it can be restored.
            jQuery(document.activeElement).addClass('preDialogFocus');

            // Set focus on dialog if options.focus is not present in options or it's set to true.
            // If there is a primary button, ensure it receives focus.  Otherwise, ensure the
            // dialog itself receives focus.
            var focusTarget=elem;
            if (elem && jQuery('.primary', elem).length>0){
                focusTarget=jQuery('.primary', elem)[0];
            }
            if (!options || typeof options.focus === "undefined" || options.focus) {
                // In any case, ensure the dialog itself can be focused.  This helps
                // screen readers to read the text in the dialog itself.
                elem.tabIndex=0;
                focusTarget.tabIndex=0;
                focusTarget.focus();
            }

            !showDimmer && elem.observe('click', dialogs.popup.zIndexHandler);

            // Ensure the TAB key cannot move focus outside of the dialog.  While
            // the "shader" dialog will intercept mouse clicks and touch events, it
            // does not prevent keyboard events.
            // NOTE: This must be done AFTER moving focus into the dialog.
            stdnav.beginModalFocus(elem);
        })
    },

    hide: function(elem) {
        require(['stdnav', 'jquery'], function(stdnav, jQuery){
            // Ensure the TAB key can move focus to all the places it was able to
            // before the dialog was shown.
            // NOTE: This must be done BEFORE moving focus out of the dialog.
            stdnav.endModalFocus(elem);

            // Restore keyboard focus to the element that had it prior to the
            // dialog.
            var jqPreDialogFocus=jQuery('.preDialogFocus');
            if (jqPreDialogFocus.length && jqPreDialogFocus.is(":visible")){
                jqPreDialogFocus[0].focus();
                jqPreDialogFocus.removeClass('preDialogFocus');
            }

            if (!elem) {
                return;
            }

            $elem = $(elem);
            //hide dialog and dimmer
            if (!$elem.hasClassName(layoutModule.HIDDEN_CLASS)) {
                $elem.addClassName(layoutModule.HIDDEN_CLASS);
                pageDimmer.hide();
                $elem.match(layoutModule.DIALOG_LOADING_PATTERN) && pageDimmer.setZindex(layoutModule.DIMMER_Z_INDEX);
            }
            $elem.stopObserving('click', dialogs.popup.zIndexHandler);
        });
    },

    showShared: function(elem, showDimmer, options) {
        if (!elem) {
            return;
        }

        $elem = $(elem);
        options = options || {};

        if (!options.owner) {
            return;
        }

        $elem.writeAttribute(this.OWNER_ATTR, options.owner);

        if ($elem.hasClassName(layoutModule.HIDDEN_CLASS)) {
            this.show($elem, showDimmer, options);
        }
    },


    hideShared: function (elem, ownerId) {
        if (!elem) {
            return;
        }

        $elem = $(elem);

        var ownerAttr = $elem.readAttribute(this.OWNER_ATTR);

        if (ownerAttr == ownerId) {
            $elem.writeAttribute(this.OWNER_ATTR, false);
            this.hide($elem);
        }
    },

    /**
     * Handler for dialog z-index change on click.
     */
    zIndexHandler: function(event) {
        var element = Event.element(event);

        var dialog = matchMeOrUp(element, layoutModule.DIALOG_PATTERN);
        if (!dialog) {
            return;
        }
        dialogs.popup._setHigherZIndex(dialog);
    },

    _setHigherZIndex: function(currentDialog, parentDialog) {

        var
            otherDialogs,
            biggestZIndex,
            currentDialogZIndex,
            parentsZIndex = 0,
            maxZIndexAcrossOtherDialogs = 0;

        if (!currentDialog) {
            return;
        }
        currentDialogZIndex = currentDialog.getStyle('zIndex');

        if (parentDialog) {
            parentsZIndex = parentDialog.getStyle('zIndex');
        }

        otherDialogs = document.body.select(layoutModule.DIALOG_PATTERN);

        // skip dialogs which are:
        // 1) not active
        // 2) loading dialog
        // 3) the current dialog
        otherDialogs = otherDialogs.filter(function(dialog) {

            if (!dialog.visible()) {
                return false;
            }
            if (dialog.match(layoutModule.DIALOG_LOADING_PATTERN)) {
                return false;
            }
            if (dialog === currentDialog) {
                return false;
            }

            return true;
        });

        otherDialogs.each(function(dialog) {
            maxZIndexAcrossOtherDialogs = Math.max(maxZIndexAcrossOtherDialogs, dialog.getStyle('zIndex'));
        });

        biggestZIndex = Math.max(
            parentsZIndex,
            currentDialogZIndex,
            maxZIndexAcrossOtherDialogs,
            layoutModule.DIMMER_Z_INDEX
        );

        // set the current dialog the biggest found z-index
        currentDialog.setStyle({
            zIndex : biggestZIndex
        });

        // now walk thr each other dialog and if it has the same z-index, decrease it by one
        otherDialogs.each(function(dialog) {
            var
                currentZIndex = dialog.getStyle('zIndex');

            if (currentZIndex >= biggestZIndex) {
                dialog.setStyle({
                    zIndex : biggestZIndex - 1
                });
            }
        });
    }
};

dialogs.popupConfirm = _.extend({}, dialogs.popup,
    {
    show: function(elem, showDimmer, options) {
        dialogs.popup.show.apply(this, arguments);
        options = _.extend({okButtonSelector: "button.ok", cancelButtonSelector: "button.cancel"}, options);
        var $elem = jQuery(elem),
            $ok = $elem.find(options.okButtonSelector),
            $cancel = $elem.find(options.cancelButtonSelector),
            deferred = jQuery.Deferred();

        $ok.on("click", function() {
            if (options.validateFunc) {
                if (options.validateFunc() === false) {
                    return;
                }
            }
            $ok.off("click");
            $cancel.off("click");
            dialogs.popupConfirm.hide(elem);
            deferred.resolve();
        });

        $cancel.on("click", function() {
            $ok.off("click");
            $cancel.off("click");
            dialogs.popupConfirm.hide(elem);
            deferred.reject();
        });
        return deferred;
    }
});

dialogs.childPopup = _.extend({}, dialogs.popup,{
    cascadePopups: {},

    show: function(elem, showDimmer, parent, options) {
        if (parent) {
            options = _.extend({parent: parent}, options);

            this.cascadePopups[parent.id] || (this.cascadePopups[parent.id] = []);
            this.cascadePopups[parent.id].push(elem.id);

            $(elem).setAttribute("data-parentDialog", parent.id);
            dialogs.popup.show.apply(this, [elem, showDimmer, options]);

            dialogs.popup._setHigherZIndex(elem, parent);
            showDimmer && pageDimmer.setZindex(elem.getStyle('zIndex') - 1);

            parent.stopObserving('click', dialogs.popup.zIndexHandler);
        } else {
            dialogs.popup.show.apply(this, [elem, showDimmer, options]);
        }
    },

    hide: function(elem) {
        var parentId = $(elem).getAttribute("data-parentDialog");

        if (parentId) {
            if (this.cascadePopups[parentId]) {
                this.cascadePopups[parentId] = _.without(this.cascadePopups[parentId], elem.id);
            }
            dialogs.popup.hide.apply(this, [elem]);

            var parent = $(parentId);
            pageDimmer.show(parent);
            dialogs.popup._setHigherZIndex(parent);
        } else {
            dialogs.popup.hide.apply(this, [elem]);
        }
    }
});
