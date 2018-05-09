define(function(require) {

    var _ = require("underscore"),
        pathUtil = require("../util/pathUtil");

    function getLevelNesting(options, id) {
        return id === "/"
            ? 0
            : pathUtil.split(id, options.escapeCharacter, options.separator, false).slice(1).length;
    }

    return {
        create: function(escapeCharacter, separator) {
            return _.partial(getLevelNesting, {
                escapeCharacter: escapeCharacter,
                separator: separator
            });
        }
    }
});