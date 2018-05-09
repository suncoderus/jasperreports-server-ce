/*
* jCryption JavaScript data encryption v1.2
* http://www.jcryption.org/
*
* Copyright (c) 2010 Daniel Griesser
* Dual licensed under the MIT and GPL licenses.
* http://www.opensource.org/licenses/mit-license.php
* http://www.opensource.org/licenses/gpl-2.0.php
*
* If you need any further information about this plugin please
* visit my homepage or contact me under daniel.griesser@jcryption.org
*/

/*
 * This file is used by Jaspersoft under the MIT license. 
 *
 * The function in this file is based on a function from jquery.jcryption.js
 * and has Jaspersoft modifications.  
 *
*/

/**
 * @version: $Id: jQueryjCryptionExtensions.js 812 2015-01-27 11:01:30Z psavushchik $
 */

/*global BigInt, biToHex, biToString */

define(function(require){
    "use strict";

    var $ = require("jquery.jcryption");

    /**
     * The function was modified from jquery.jcryption.js  $.jCryption.encryptKey version in order NOT
     * to include redundancy/checksum bytes.
     *
     * @param {string} string The AES key
     * @param {keypair} keyPair The RSA keypair to use
     * @param {function} callback The function to call when the encryption has finished
     */
    $.jCryption.encryptKeyWithoutRedundancy = function(string, keyPair, callback) {
        if (string === '') {
            if($.isFunction(callback)) {
                callback(string);
                return;
            } else {
                return string;
            }
        }

        var charSum = 0;
        for(var i = 0; i < string.length; i++){
            charSum += string.charCodeAt(i);
        }

        /*		var tag = '0123456789abcdef';
         var hex = '';
         hex += tag.charAt((charSum & 0xF0) >> 4) + tag.charAt(charSum & 0x0F);

         var taggedString = hex + string;*/

        var encrypt = [];
        var j = 0;

        /*
         while (j < taggedString.length) {
         encrypt[j] = taggedString.charCodeAt(j);
         j++;
         }
         */

        while (j < string.length) {
            encrypt[j] = string.charCodeAt(j);
            j++;
        }

        while (encrypt.length % keyPair.chunkSize !== 0) {
            encrypt[j++] = 0;
        }

        function encryption(encryptObject) {
            var charCounter = 0;
            var j, block;
            var encrypted = "";
            function encryptChar() {
                block = new BigInt();
                j = 0;
                for (var k = charCounter; k < charCounter+keyPair.chunkSize; ++j) {
                    block.digits[j] = encryptObject[k++];
                    block.digits[j] += encryptObject[k++] << 8;
                }
                var crypt = keyPair.barrett.powMod(block, keyPair.e);
                var text = keyPair.radix == 16 ? biToHex(crypt) : biToString(crypt, keyPair.radix);
                encrypted += text + " ";
                charCounter += keyPair.chunkSize;
                if (charCounter < encryptObject.length) {
                    setTimeout(encryptChar, 1)
                } else {
                    var encryptedString = encrypted.substring(0, encrypted.length - 1);
                    if($.isFunction(callback)) {
                        callback(encryptedString);
                    } else {
                        return encryptedString;
                    }

                }
            }
            setTimeout(encryptChar, 1);
        }

        encryption(encrypt);
    };

    return $;
});

