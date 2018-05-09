define(function(require) {

    var _ = require("underscore"),
        pathUtil = require("../util/pathUtil");

    function getParentId(options, id) {
        var splitedId = pathUtil.split(id, options.escapeCharacter, options.separator, false).slice(1),
            idLength = splitedId.length;

        splitedId.splice(idLength - 1 , 1);

        return splitedId.length ? "/" + splitedId.join("/") : "/";
    }

    return {
        create: function(escapeCharacter, separator) {
            return _.partial(getParentId, {
                escapeCharacter: escapeCharacter,
                separator: separator
            });
        }
    }
});