define(function(require){

    var Epoxy = require("backbone.epoxy"),
        _ = require("underscore"),
        epoxyCustomBindingHandlers = require("./epoxyCustomBindingHandlers"),
        epoxyCustomBindingFilters = require("./epoxyCustomBindingFilters");

    return {

        epoxifyView: function(){
            var originalRemove = this.remove;

            Epoxy.View.mixin(this);

            // Epoxy overrides remove method by it's own when mixin;
            originalRemove && (this.remove = originalRemove);

            this.bindingFilters ? (this.bindingFilters = _.extend({}, epoxyCustomBindingFilters, this.bindingFilters))
                : epoxyCustomBindingFilters;
            this.bindingHandlers ? (this.bindingHandlers = _.extend({}, epoxyCustomBindingHandlers, this.bindingHandlers))
                : epoxyCustomBindingHandlers;
        },

        applyEpoxyBindings: function(){
            this.applyBindings && this.applyBindings();
        },

        removeEpoxyBindings: function(){
            this.removeBindings && this.removeBindings();
        }
    };
});