/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
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
 * @author: Dima Gorbenko <dgorbenko@jaspersoft.com>
 * @version: $Id$
 */

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/*  Encryption class: JCryption based                                                                 */
/*  Depends on jCryption 2.0                                                     */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

define(function (require) {
    "use strict";

    var $ = require("common/extension/jQueryjCryptionExtensions");

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
    /*  Base64 class: Base 64 encoding                                                                 */
    /*    Stolen and rewritten from jCryption 2.0                                                     */
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

    var Base64Coder = {};  // Base64 namespace

    Base64Coder.code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    /**
     * encode 16 bit char string inot Base64 format
     * @param {String} str The string to be encoded as base-64
     * @returns {String} Base64-encoded string
     */
    Base64Coder.encode16BitString = function(str) {
        var hd1, hd2, bits, h1, h2, h3, h4, h5, d6, e=[], pad = '', c, plain, coded;
        var b64 = Base64Coder.code;

        plain = str;

        c = plain.length % 2;  // pad string to length of multiple of 2
        if (c > 0) { while (c++ < 2) { pad += '==='; plain += '\0'; } }
        // note: doing padding here saves us doing special-case packing for trailing 1 or 2 chars

        for (c=0; c<plain.length; c+=2) {  // pack 2 hexadecets into 6 hexets
            hd1 = plain.charCodeAt(c);
            hd2 = plain.charCodeAt(c+1);

            bits = hd1<<16 | hd2;

            h1 = bits>>26 & 0x3f;
            h2 = bits>>20 & 0x3f;
            h3 = bits>>14 & 0x3f;
            h4 = bits>>8 & 0x3f;
            h5 = bits>>2 & 0x3f;
            d6 = bits & 0x3;

            // use hextets to index into code string
            e[c/2] = b64.charAt(h1) + b64.charAt(h2) + b64.charAt(h3) + b64.charAt(h4)
                + b64.charAt(h5) + b64.charAt(d6);
        }
        coded = e.join('');  // join() is far faster than repeated string concatenation in IE

        // replace 'A's from padded nulls with '='s
        coded = coded.slice(0, coded.length-pad.length) + pad;

        return coded;
    }

    /**
     * Decode a 16bit char word from Base64 encoding.
     *
     * @param {String} str The string to be decoded from base-64
     * @returns {String} decoded string
     */
    Base64Coder.decode16BitString = function(str) {
        var o1, o2, o3, o4, h1, h2, h3, h4, h5, d6, bits, d=[], plain, coded;
        var b64 = Base64Coder.code;

        coded = str;

        for (var c=0; c<coded.length; c+=6) {  // unpack four hexets into three octets
            // unpack 5 hexets and 2-bit into 4 octets
            h1 = b64.indexOf(coded.charAt(c));
            h2 = b64.indexOf(coded.charAt(c+1));
            h3 = b64.indexOf(coded.charAt(c+2));
            h4 = b64.indexOf(coded.charAt(c+3));
            h5 = b64.indexOf(coded.charAt(c+4));
            d6 = b64.indexOf(coded.charAt(c+5));

            bits = h1<<26 | h2<<20 | h3<<14 | h4<<8 | h5<<2 | (d6 & 0x3);

            o1 = bits>>>24 & 0xff;
            o2 = bits>>>16 & 0xff;
            o3 = bits>>>8 & 0xff;
            o4 = bits & 0xff;

            d[c/6] = String.fromCharCode(o1<<8 | o2, o3<<8 | o4);
            // check for padding
            // - if b64.indexOf(coded.charAt(c + 3)) == 64 OR b64.charAt(c + 3) == '='
            if (h4 == 0x40) d[c/6] = d[c/6] = String.fromCharCode(o1<<8 | o2);
        }
        plain = d.join('');  // join() is far faster than repeated string concatenation in IE

        return plain;
    }

    var JSEncrypter = {
        encryptData: function(dataObj, callbackFunc) {
            if (!dataObj) {
                callbackFunc();  //call callback function in any case in order to simplify code calling encryptData().  Callback function should handle undefined data.
                return;
            }

            $.jCryption.getKeys("GetEncryptionKey", function(receivedKey) {
                var pubKey = receivedKey;
                var encDataObj = {};

                var keyArr = [];
                for (var i in dataObj)
                    keyArr.push(i);

                var encData =  {};
                JSEncrypter._encryptDataRecursive(dataObj, keyArr, 0, pubKey, encData, callbackFunc);
            });
        },
        /* private method to be used internally by JSEncrypter */
        _encryptDataRecursive: function(dataObj, keyArr, index,  pubKey, encDataObj, callbackFunc) {
            if (!keyArr || keyArr.length == index)
                return;

            // encodeURIComponent(usrVal) converts encrypted param to utf-8: needed for foreign chars.
            // Reverse() call is needed because jCryption algo reverses the parameter internally.
            // In order to deal with un-reversed encrypted parameters on the server (and not to dig too deep into jCryption algo), we
            // have reversed the enc. parameter here before passing it onto jCryption.
            var dataToEncrypt = encodeURIComponent(dataObj[keyArr[index]]);
            var reversedDataToEncrypt = dataToEncrypt.split("").reverse().join("");
            $.jCryption.encryptKeyWithoutRedundancy(reversedDataToEncrypt, pubKey,
                function(receivedEncrypted) {
                    encDataObj[keyArr[index]] = receivedEncrypted;

                    if (keyArr.length == index+1)
                        callbackFunc(encDataObj);
                    else
                        JSEncrypter._encryptDataRecursive(dataObj, keyArr, index+1,  pubKey, encDataObj, callbackFunc);
                }
            );
        }
    }

    return JSEncrypter;
});