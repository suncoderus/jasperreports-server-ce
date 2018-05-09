define(function (require) {
    "use strict";

    var RelativeDate = require("common/util/datetime/RelativeDate"),
        _ = require("underscore");

    var RelativeTimestamp = function() {
        RelativeDate.apply(this, arguments);
    };

    var F = function() { };
    F.prototype = RelativeDate.prototype;
    RelativeTimestamp.prototype = new F();
    RelativeTimestamp.prototype.constructor = RelativeTimestamp;

    RelativeTimestamp.PATTERNS = {
//        MINUTE : /^(MINUTE)(([+|\-])(\d{1,9}))?$/i,
//        HOUR : /^(HOUR)(([+|\-])(\d{1,9}))?$/i,
        DAY : /^(DAY)(([+|\-])(\d{1,9}))?$/i,
        WEEK : /^(WEEK)(([+|\-])(\d{1,9}))?$/i,
        MONTH : /^(MONTH)(([+|\-])(\d{1,9}))?$/i,
        QUARTER : /^(QUARTER)(([+|\-])(\d{1,9}))?$/i,
        SEMI : /^(SEMI)(([+|\-])(\d{1,9}))?$/i,
        YEAR : /^(YEAR)(([+|\-])(\d{1,9}))?$/i
    };

    RelativeTimestamp.parse = function(relativeTimestampString) {
        if (RelativeTimestamp.isValid(relativeTimestampString)) {
            for (var pattern in RelativeTimestamp.PATTERNS) {
                var result = RelativeTimestamp.PATTERNS[pattern].exec(relativeTimestampString);
                if (result !== null && _.isArray(result) && result.length === 5) {
                    return new RelativeTimestamp(result[1], result[3], result[4]);
                }
            }
        }

        return undefined;
    };

    RelativeTimestamp.isValid = function(relativeTimestamp) {
        if (relativeTimestamp instanceof RelativeTimestamp) {
            return relativeTimestamp.toString() !== "";
        } else if (_.isString(relativeTimestamp)) {
            for (var pattern in RelativeTimestamp.PATTERNS) {
                if (RelativeTimestamp.PATTERNS[pattern].test(relativeTimestamp)) {
                    return true;
                }
            }
        }

        return false;
    };

    return RelativeTimestamp;
});
