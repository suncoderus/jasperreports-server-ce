define(function(require){

    var Epoxy = require("backbone.epoxy.original"),
        Backbone = require("backbone"),
        AttachableColorPicker = require("common/component/colorPicker/SimpleAttachableColorPicker"),
        _ = require("underscore");


    function getAttrName($el, handlerName) {
        var selector = $el.data("bind"),
            pattern = new RegExp(handlerName +  ":(\\w*)\\(?(\\w*)?\\)?");

        if (selector) {
            return _.last(_.compact(selector.match(pattern)));
        } else {
            return $el.data("bindAttribute");
        }
    }

    var bindingSettings = {
        optionText: 'label',
        optionValue: 'value',
        optionClass: 'className'
    };

    var isCollection = function(obj) { return obj instanceof Backbone.Collection; };
    var isModel = function(obj) { return obj instanceof Backbone.Model; };
    var isFunction = _.isFunction;
    var isObject = _.isObject;

    // optionsWithAdditionalProperties: write-only. Sets option items to a <select> element, then updates the value.
    Epoxy.binding.addHandler("optionsWithAdditionalProperties", {
        /* start same part of code as in the Epoxy for the options binding handler */
        init: function($element, value, context, bindings) {
            this.e = bindings.optionsEmpty;
            this.d = bindings.optionsDefault;
            this.v = bindings.value;
        },
        set: function($element, value) {

            // Pre-compile empty and default option values:
            // both values MUST be accessed, for two reasons:
            // 1) we need to need to guarentee that both values are reached for mapping purposes.
            // 2) we'll need their values anyway to determine their defined/undefined status.
            var self = this;
            var optionsEmpty = readAccessor(self.e);
            var optionsDefault = readAccessor(self.d);
            var currentValue = readAccessor(self.v);
            var options = isCollection(value) ? value.models : value;
            var numOptions = options.length;
            var enabled = true;
            var html = '';

            // No options or default, and has an empty options placeholder:
            // display placeholder and disable select menu.
            if (!numOptions && !optionsDefault && optionsEmpty) {

                html += self.opt(optionsEmpty, numOptions);
                enabled = false;

            } else {
                // Try to populate default option and options list:

                // Configure list with a default first option, if defined:
                if (optionsDefault) {
                    options = [ optionsDefault ].concat(options);
                }

                // Create all option items:
                _.each(options, function(option, index) {
                    html += self.opt(option, numOptions);
                });
            }
            // Set new HTML to the element and toggle disabled status:
            $element.html(html).prop('disabled', !enabled).val(currentValue);

            // Forcibly set default selection:
            if ($element[0].selectedIndex < 0 && $element.children().length) {
                $element[0].selectedIndex = 0;
            }

            // Pull revised value with new options selection state:
            var revisedValue = $element.val();

            // Test if the current value was successfully applied:
            // if not, set the new selection state into the model.
            if (self.v && !_.isEqual(currentValue, revisedValue)) {
                self.v(revisedValue);
            }
        },
        /* end same part of code as in the Epoxy for the options binding handler */
        opt: function(option, numOptions) {
            // Set both label and value as the raw option object by default:
            var label = option;
            var value = option;
            var className = option;
            var textAttr = bindingSettings.optionText;
            var valueAttr = bindingSettings.optionValue;
            var classAttr = bindingSettings.optionClass;
            var optionClass;

            // Dig deeper into label/value settings for non-primitive values:
            if (isObject(option)) {
                // Extract a label and value from each object:
                // a model's 'get' method is used to access potential computed values.
                label = isModel(option) ? option.get(textAttr) : option[ textAttr ];
                value = isModel(option) ? option.get(valueAttr) : option[ valueAttr ];
                className = isModel(option) ? option.get(classAttr) : option[ classAttr ];
            }

            if (className) {
                optionClass = 'class="' + className + '"';
            } else {
                optionClass = '';
            }

            return ['<option value="', value, '"', optionClass , '>', label, '</option>'].join('');
        }
    });

    Epoxy.binding.addHandler("validationErrorClass", {
        init: function( $element, value, bindings, context ) {
            this.attr = getAttrName($element, "validationErrorClass");
            var model = this.view.model;

            this._onAttrValidated = function(model, attr, error) {
                $element[error ? "addClass" : "removeClass"]("error");
            };

            model.on("validate:" + this.attr, this._onAttrValidated);
        },
        get: function( $element, value, event ) {
            // Get data from the bound element...
            return $element.val();
        },
        set: function( $element, value ) {
            // Set data into the bound element...
            $element.val( value );
        },
        clean: function() {
            this.view.model.off("validate:" + this.attr, this._onAttrValidated);
        }
    });

    Epoxy.binding.addHandler("validationErrorText", {
        init: function( $element, value, bindings, context ) {
            this.attr = getAttrName($element, "validationErrorText");
            var model = this.view.model;

            this._onAttrValidated = function(model, attr, error) {
                $element.text(error || "");
            };

            model.on("validate:" + this.attr, this._onAttrValidated);
        },
        get: function( $element, value, event ) {
            // Get data from the bound element...
            return $element.val();
        },
        set: function( $element, value ) {
            // Set data into the bound element...
            $element.val( value );
        },
        clean: function() {
            this.view.model.off("validate:" + this.attr, this._onAttrValidated);
        }
    });

    Epoxy.binding.addFilter("escapeCharacters", {
        get: function( value ) {
            // model -> view
            return _.escape(value);
        },
        set: function( value ) {
            // view -> model
            return _.unescape(value);
        }
    });

    Epoxy.binding.addHandler("colorpicker", {
        init: function( $element, value, bindings, context ) {
            var showTransparentInput = !!$element.data("showTransparentInput"),
                label = $element.data("label"),
                attr = getAttrName($element, "colorpicker");

            this.attachableColorPicker = new AttachableColorPicker($element, {top: 5, left: 5}, {label: label, showTransparentInput: showTransparentInput});

            this.callback = function(color){
                bindings[attr](color);
            };

            this.attachableColorPicker.on("color:selected", _.bind(this.callback, this));
        },
        get: function( $element, value, event ) {
            // Get data from the bound element...
            return $element.val();
        },
        set: function( $element, value ) {
            var colorIndicator = $element.find(".colorIndicator");
            this.attachableColorPicker.highlightColor(value);
            colorIndicator.css("background-color", value);
        },
        clean: function() {
            this.attachableColorPicker.off("color:selected", _.bind(this.callback, this));
            this.attachableColorPicker.remove();
        }
    });

    Epoxy.binding.addHandler("radioDiv", {
        init: function( $element, value, bindings, context ) {
            var attr = getAttrName($element, "radioDiv");

            this.callback = function(){
                var value = $element.data("value");
                bindings[attr](value);
            };

            $element.on('click', _.bind(this.callback, this));
        },
        get: function( $element, value, event ) {
            // Get data from the bound element...
            return $element.data("value");
        },
        set: function( $element, value ) {
            var radioDivs = $element.siblings("div[data-bind*='radioDiv:']");

            if($element.data("value") === value){
                $element.addClass('checked');
                $element.children(".radioChild").addClass('checked');
                radioDivs.removeClass('checked');
                radioDivs.children(".radioChild").removeClass('checked');
            }
        }
    });

    Epoxy.binding.addHandler("checkboxDiv", {
        init: function( $element, value, bindings, context ) {
            var attr = getAttrName($element, "checkboxDiv");

            this.callback = function(){
                bindings[attr](!bindings[attr]());
            };

            $element.on('click', _.bind(this.callback, this));
        },
        get: function( $element, value, event ) {
            // Get data from the bound element...
            return $element.data("value");
        },
        set: function( $element, value ) {
            if(value){
                $element.addClass('checked');
                $element.children(".checkboxChild").addClass('checked');
            }else{
                $element.removeClass('checked');
                $element.children(".checkboxChild").removeClass('checked');
            }
        }
    });

    Epoxy.binding.addHandler("slide", function($element, value) {
        $element[value ? "slideDown" : "slideUp"]({
            complete: function() {
                !value && $element.hide();
            }
        });
    });

    Epoxy.binding.addHandler("selectionRange", {

        get: function( $element, value, event ) {
            return {
                selectionRange: {
                    start: $element[0].selectionStart,
                    end: $element[0].selectionEnd
                }
            }
        },
        set: function( $element, value ) {
            if ($element.is(":visible")) {
                $element[0].setSelectionRange(value.start, value.end);
                $element.focus();
            }
        }
    });

    /**
     * @type {function}
     * @description Epoxy filter to prepend some text before binding. To pass multi-word text weap it in single quotes.
     * @example
     *  <select data-bind="text:prependText(myAttr, 'multi-word text to prepend')></select>
     *  <select data-bind="text:prependText(myAttr, 'single-word')"></select>
     */

    Epoxy.binding.addFilter("prependText", function(value, text) {
        if (text.charAt(0) === "'" && text.charAt(text.length - 1) === "'") {
            text = text.slice(1, text.length - 1);
        }

        return text + " " + (_.isUndefined(value) ? "" : value);
    });

    // Reads value from an accessor:
    // Accessors come in three potential forms:
    // => A function to call for the requested value.
    // => An object with a collection of attribute accessors.
    // => A primitive (string, number, boolean, etc).
    // This function unpacks an accessor and returns its underlying value(s).

    function readAccessor(accessor) {

        if (isFunction(accessor)) {
            // Accessor is function: return invoked value.
            return accessor();
        }
        else if (isObject(accessor)) {
            // Accessor is object/array: return copy with all attributes read.
            accessor = _.clone(accessor);

            _.each(accessor, function(value, key) {
                accessor[ key ] = readAccessor(value);
            });
        }
        // return formatted value, or pass through primitives:
        return accessor;
    }

    return Epoxy;
});
