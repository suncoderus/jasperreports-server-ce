/*
 * Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author: Taras Bidyuk
 */

define(function(require) {

    var _ = require("underscore");

    return {

        _isDelimiter: function(escapeCharacterQuantity) {
            return escapeCharacterQuantity % 2;
        },

        split: function(string, escapeCharacter, delimiter, unescapeOutput) {
            delimiter = _.isArray(delimiter) ? delimiter : [delimiter];

            unescapeOutput = _.isUndefined(unescapeOutput) ? true : unescapeOutput;

            var	self = this,
                lastDelimiterIndex = 0,
                escapeCharacterQuantity = 0,
                result = [],
                length = string.length;

            _.each(string, function(character, index) {
                if (character === escapeCharacter) {
                    escapeCharacterQuantity += 1;
                } else if (_.indexOf(delimiter, character) !== -1) {
                    if ((escapeCharacterQuantity === 0) || !this._isDelimiter(escapeCharacterQuantity)) {
                        escapeCharacterQuantity = 0;
                        result.push(string.slice(lastDelimiterIndex, index));
                        lastDelimiterIndex = index + 1;
                    } else {
                        escapeCharacterQuantity = 0;
                    }
                } else {
                    escapeCharacterQuantity = 0;
                }

                if (index + 1 === length) {
                    result.push(string.slice(lastDelimiterIndex, length));
                }
            }, this);

            return _.map(result, function(value) {
                return unescapeOutput ? self.unescape(value, escapeCharacter) : value;
            });
        },

        unescape: function(string, escapeCharacter) {
            var lastSeqIndex = 0,
                escapeSequence = "",
                length = string.length,
                resultString = "";

            _.each(string, function(character, index) {
                if (character === escapeCharacter) {
                    escapeSequence += escapeCharacter;
                } else {
                    if (escapeSequence.length) {
                        resultString += this._getReplacedSubString(string,
                            escapeSequence, escapeCharacter, index, lastSeqIndex);
                        lastSeqIndex = index;
                    }
                    escapeSequence = "";
                }

                if (index + 1 === length) {
                    if (escapeSequence.length) {
                        resultString += this._getReplacedSubString(string,
                            escapeSequence, escapeCharacter, index + 1, lastSeqIndex);
                    } else {
                        resultString += string.slice(lastSeqIndex, length);
                    }
                }
            }, this);

            return resultString;
        },

        _getReplacedSubString: function(string, escapeSequence, escapeCharacter, i, lastIndex) {
            var replaceSequence = this._getReplaceSequence(escapeSequence, escapeCharacter);
            return string.slice(lastIndex, i).replace(escapeSequence, replaceSequence);
        },

        _getReplaceSequence: function(escapeSequence, escapeCharacter) {
            var replaceSequence;

            if (escapeSequence.length % 2) {
                replaceSequence = (new Array(((escapeSequence.length - 1) / 2) + 1)).join(escapeCharacter);
            } else {
                var arraySize = escapeSequence.length / 2;

                arraySize = arraySize === 1 ? 2 : arraySize;

                replaceSequence = (new Array(arraySize)).join(escapeCharacter);
            }

            return replaceSequence;
        },

        join: function(path, escapeCharacter, separator) {
            return _.map(path, function(pathFragment) {
                return this.escape(pathFragment, escapeCharacter, [escapeCharacter, separator]);
            }, this).join(separator);
        },

        escape: function(string, escapeCharacter, characters) {
            var regexp = this._getEscapeRegexp(characters);

            return string === null ? '' : string.replace(regexp, function(match) {
                return escapeCharacter + match;
            });
        },

        _getEscapeRegexp: function(characters) {
            characters = characters.toString().replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&").split("\\,");
            return new RegExp(characters.join("|"), "g");
        }

    };
});