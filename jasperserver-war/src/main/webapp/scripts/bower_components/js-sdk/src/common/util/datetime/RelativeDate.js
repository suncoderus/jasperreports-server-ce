define(function (require) {
    "use strict";

    var _ = require("underscore");

    var RelativeDate = function(keyword, sign, number) {
        this.setKeyword(keyword);
        this.setSign(sign);
        this.setNumber(number);
    };

    RelativeDate.prototype.setKeyword = function(keyword) {
        this.keyword = keyword.toUpperCase();
    };

    RelativeDate.prototype.setSign = function(sign) {
        this.sign = sign;
    };

    RelativeDate.prototype.setNumber = function(number) {
        if (_.isNumber(number))  {
            this.number = number;
        } else {
            var value = parseInt(number, 10);

            if (!isNaN(value)) {
                this.number = value;
            } else {
                this.number = 0;
            }
        }
    };


    RelativeDate.prototype.toString = function() {
        if (_.isNumber(this.number) && !isNaN(this.number) && this.number > 0 && (this.sign === "+" || this.sign === "-") && this.keyword in RelativeDate.PATTERNS) {
            return this.keyword + this.sign + this.number.toString();
        } else if (this.keyword in RelativeDate.PATTERNS) {
            return this.keyword;
        } else {
            return "";
        }
    };

    RelativeDate.PATTERNS = {
        DAY : /^(DAY)(([+|\-])(\d{1,9}))?$/i,
        WEEK : /^(WEEK)(([+|\-])(\d{1,9}))?$/i,
        MONTH : /^(MONTH)(([+|\-])(\d{1,9}))?$/i,
        QUARTER : /^(QUARTER)(([+|\-])(\d{1,9}))?$/i,
        SEMI : /^(SEMI)(([+|\-])(\d{1,9}))?$/i,
        YEAR : /^(YEAR)(([+|\-])(\d{1,9}))?$/i
    };

    RelativeDate.parse = function(relativeDateString) {
        if (RelativeDate.isValid(relativeDateString)) {
            for (var pattern in RelativeDate.PATTERNS) {
                var result = RelativeDate.PATTERNS[pattern].exec(relativeDateString);
                if (result !== null && _.isArray(result) && result.length === 5) {
                    return new RelativeDate(result[1], result[3], result[4]);
                }
            }
        }

        return undefined;
    };

    RelativeDate.isValid = function(relativeDate) {
        if (relativeDate instanceof RelativeDate) {
            return relativeDate.toString() !== "";
        } else if (_.isString(relativeDate)) {
            for (var pattern in RelativeDate.PATTERNS) {
                if (RelativeDate.PATTERNS[pattern].test(relativeDate)) {
                    return true;
                }
            }
        }

        return false;
    };

    return RelativeDate;
});
