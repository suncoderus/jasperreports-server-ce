define(function(require) {

    var _ = require("underscore"),
        pathUtil = require("../util/pathUtil"),
        getLevelNestingFactory = require("./getLevelNestingFactory"),
        getParentIdFactory = require("./getParentIdFactory");

    function coerceIdsToCommonLevel(options, firstId, secondId) {
        var modifier = 0,
            secondLevelNesting = options.getLevelNesting(secondId);

        var dividedFirstId = pathUtil.split(firstId, options.escapeCharacter, options.separator, false).slice(1),
            dividedSecondId = pathUtil.split(secondId, options.escapeCharacter, options.separator, false).slice(1);

        // coerce to same level nesting
        dividedFirstId.splice(secondLevelNesting, dividedFirstId.length).join("/");

        var firstCommonId = options.separator + dividedFirstId.join("/"),
            secondCommonId = options.separator + dividedSecondId.join("/");

        // coerce to same parent to be able to compare
        for (var i = 0; i < dividedFirstId.length; i++) {
            modifier -= i || 1;

            // if parents are equal break and return ids.
            if (options.getParentId(firstCommonId) === options.getParentId(secondCommonId)) {
                break;
            }

            // slice last fragment of id to run another check for same parent level
            firstCommonId = options.separator + dividedFirstId.slice(0, modifier).join(options.separator);
            secondCommonId = options.separator + dividedSecondId.slice(0, modifier).join(options.separator);
        }

        return {
            firstId: firstCommonId,
            secondId: secondCommonId
        }
    }

    return {
        create: function(escapeCharacter, separator) {
            var getLevelNesting = getLevelNestingFactory.create(escapeCharacter, separator),
                getParentId = getParentIdFactory.create(escapeCharacter, separator);

            return _.partial(coerceIdsToCommonLevel, {
                escapeCharacter: escapeCharacter,
                separator: separator,
                getParentId: getParentId,
                getLevelNesting: getLevelNesting
            });
        }
    }
});