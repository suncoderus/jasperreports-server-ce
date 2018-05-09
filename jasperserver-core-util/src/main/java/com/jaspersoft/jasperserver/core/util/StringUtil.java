/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.core.util;

import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class StringUtil {
    private static final Logger logger = LogManager.getLogger(StringUtil.class);

    private static final CharSequenceTranslator ESCAPE_GROOVY =
            new LookupTranslator(
                    new String[][] {
                            {"\'", "\\\'"},
                            {"\"", "\\\""},
                            {"\\", "\\\\"},
                    }).with(
                    new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE())
            ).with(
                    UnicodeEscaper.outsideOf(32, 0x7f)
            );

    /**
     * This method is used to escape single quotes
     * @param str
     * @return escaped string
     */
    public static String escapeSingleQuotes(String str) {
        if (str==null) {
            return null;
        }
        return str.replace("'", "\\'");
    }

    /**
     * helper routine for cache admin jsp (better than putting the code in jsp)
     * Print a millisecond interval in mm:ss format...didn't find anything that does this!
     */
    public static String printInterval(long ms) {
        long totalSecs = ms / 1000;
        long mins = totalSecs / 60;
        long secs = totalSecs % 60;
        return mins + ":" + (secs < 10 ? "0" : "") + secs;
    }

    public static String replace(String s, String find, String replace) {

        StringBuilder sbuilder = new StringBuilder(s.length());

        sbuilder.append(s);

        replace(sbuilder, 0, find, replace);

        return sbuilder.toString();
    }

    /**
     * Replaces all occurrences of a string in a buffer with another.
     *
     * @param buf String buffer to act on
     * @param start Ordinal within <code>find</code> to start searching
     * @param find String to find
     * @param replace String to replace it with
     * @return The string buffer
     */
    public static StringBuilder replace(
            StringBuilder buf,
            int start,
            String find, String replace) {
        // Search and replace from the end towards the start, to avoid O(n ^ 2)
        // copying if the string occurs very commonly.
        int findLength = find.length();
        if (findLength == 0) {
            // Special case where the seek string is empty.
            for (int j = buf.length(); j >= 0; --j) {
                buf.insert(j, replace);
            }
            return buf;
        }
        int k = buf.length();
        while (k > 0) {
            int i = buf.lastIndexOf(find, k);
            if (i < start) {
                break;
            }
            buf.replace(i, i + find.length(), replace);
            // Step back far enough to ensure that the beginning of the section
            // we just replaced does not cause a match.
            k = i - findLength;
        }
        return buf;
    }

    public static String upper(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String lower(String name) {
        return name.substring(0, 1).toLowerCase() +	name.substring(1);
    }

    /**
     * Check that none of the inputStrings are null or are composed of space chars or are empty.
     *
     *  @param inputStrings
     *  @return true if all input strings have some non-space characters.
     */
    public static boolean checkAllInputStringsNonEmpty(String ... inputStrings) {
        for (String s : inputStrings) {
            if (s == null || s.trim().length() == 0)
                return false;
        }

        return true;
    }

    /*
    * if the value has been encoded, then decode it.
    *
    * @return original value if decoding fails
    *
    * Examples for UTF-8:
    *   %2F decodes to /
    *   %20 decodes to space character
    *   %3C and %3E decode to < and >, respectively
    */
    private static String getDecodedValue(String value, String encoding, String errorMessage) {
        try {
            value = URLDecoder.decode(value, encoding);
        }
        catch (UnsupportedEncodingException e) {
            logger.warn((errorMessage != null && errorMessage.trim().length() > 0 ? errorMessage + ": " : "") + e.getMessage());
        }
        catch (IllegalArgumentException iae) {
            logger.warn("Decoded value contained illegal characters (eg. %).  Decoding was aborted and original value was used. Encoding: " + encoding + ", Original Input: " + value.replaceAll("\n", ""));
        }
        catch (Exception e) {
            logger.error(errorMessage, e);
        }

        return value;
    }

    /**
     * Decode request parameter Map, keys and values.
     * Calls on {@link #getDecodedValue(String, String, String)} to decode paramMap
     *
     * @param paramMap
     * @return
     */
    public static Map<String, String[]> getDecodedMap(Map<String, String[]> paramMap, String encoding, String errorMessage) {
        Map<String, String[]> decodedMap = new HashMap<String, String[]>();
        for (Map.Entry<String, String[]> paramEntry : paramMap.entrySet()) {
            String[] decodedValues = new String[paramEntry.getValue().length];
            for (int i = 0; i < paramEntry.getValue().length; i++) {
                decodedValues[i] = getDecodedValue(paramEntry.getValue()[i], encoding, errorMessage);
            }
            decodedMap.put(getDecodedValue(paramEntry.getKey(), encoding, errorMessage), decodedValues);
        }
        return decodedMap;
    }

    /**
     * Convert byteArr to hex sting.
     * @param byteArr
     * @return
     */
    public static String byteArrayToHexString(byte[] byteArr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteArr.length; i++) {
            byte b = byteArr[i];
            int high = (b & 0xF0) >> 4;
            int low = b & 0x0F;

            sb.append(Character.forDigit(high, 16));
            sb.append(Character.forDigit(low, 16));
        }
        return sb.toString();
    }

    /**
     * Convert hex string to byte array
     *
     * @param data input string data
     * @return bytes
     */
    public static byte[] hexStringToByteArray(String data) {
        int k = 0;
        byte[] results = new byte[data.length() / 2];
        for (int i = 0; i < data.length(); ) {
            final int firstHalf = Character.digit(data.charAt(i++), 16);
            final int secondHalf = Character.digit(data.charAt(i++), 16);

            if (firstHalf == -1 || secondHalf == -1) {
                // Not a hexadecimal string
                throw new RuntimeException("The string passed for decryption was not hexadecimal.");
            }

            results[k] = (byte) (firstHalf << 4);
            results[k] += (byte) (secondHalf);
            k++;
        }
        return results;
    }

    /**
     * Unlike Java escaping should escape "'", and unlike JavaScript escaping should NOT escape "/"
     *
     * @param src
     * @return
     */
    public static String escapeGroovy(String src) {
        return ESCAPE_GROOVY.translate(src);
    }
}
