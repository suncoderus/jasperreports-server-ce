define(function (require) {
    "use strict";

    var RelativeDate = require("common/util/datetime/RelativeDate"),
        _ = require("underscore");

    var RelativeTime = function() {
        RelativeDate.apply(this, arguments);
    };

    var F = function() { };
    F.prototype = RelativeDate.prototype;
    RelativeTime.prototype = new F();
    RelativeTime.prototype.constructor = RelativeTime;

    RelativeTime.PATTERNS = {
        MINUTE : /^(MINUTE)(([+|\-])(\d{1,9}))?$/i,
        HOUR : /^(HOUR)(([+|\-])(\d{1,9}))?$/i
    };

    RelativeTime.parse = function(relativeTimeString) {
        if (RelativeTime.isValid(relativeTimeString)) {
            for (var pattern in RelativeTime.PATTERNS) {
                var result = RelativeTime.PATTERNS[pattern].exec(relativeTimeString);
                if (result !== null && _.isArray(result) && result.length === 5) {
                    return new RelativeTime(result[1], result[3], result[4]);
                }
            }
        }

        return undefined;
    };

    RelativeTime.isValid = function(relativeTime) {
        if (relativeTime instanceof RelativeTime) {
            return relativeTime.toString() !== "";
        } else if (_.isString(relativeTime)) {
            for (var pattern in RelativeTime.PATTERNS) {
                if (RelativeTime.PATTERNS[pattern].test(relativeTime)) {
                    return true;
                }
            }
        }

        return false;
    };

    return RelativeTime;
});
