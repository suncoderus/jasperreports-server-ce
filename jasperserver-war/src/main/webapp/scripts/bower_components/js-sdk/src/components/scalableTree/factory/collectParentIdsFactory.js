define(function(require) {

    var _ = require("underscore"),
        getParentIdFactory = require("./getParentIdFactory");

    var collectParentIds = function(options, id, parents) {
        parents  = parents || [];

        if (id === "/") {
            return parents;
        }

        var parentId = options.getParentId(id);

        parents.push(parentId);

        collectParentIds(options, parentId, parents);

        return parents;
    };

    return {
        create: function(escapeCharacter, separator) {
            var getParentId = getParentIdFactory.create(escapeCharacter, separator);

            return _.partial(collectParentIds, {
                escapeCharacter: escapeCharacter,
                separator: separator,
                getParentId: getParentId
            });
        }
    }

});