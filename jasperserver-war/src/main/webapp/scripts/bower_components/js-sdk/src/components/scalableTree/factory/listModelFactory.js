define(function(require) {

    var addToSelectionModelTrait = require("common/component/list/model/trait/addToSelectionModelTrait"),
    ListWithSelectionAsObjectHashModel = require("common/component/list/model/ListWithSelectionAsObjectHashModel").extend(addToSelectionModelTrait);

    return {
        create: function(options) {
            return new ListWithSelectionAsObjectHashModel(options);
        }
    }
});