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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: dlitvak
 * Date: 2/27/12
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Base64Coder {
    private static final Logger logger = LogManager.getLogger(Base64Coder.class);
    private static final String BASE64_CODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    private static final String BASE64_CODE_PADDING = "===";
    public static final String UNICODE_CHARSET = "UTF-16";

    public static String decode16BitBase64(String b64Str) {
        try {
            String decodedBase64 = "";

            for (int c = 0, b=0; c < b64Str.length(); c+=6, b+=4) {
                // unpack 5 hexets and 2-bit into 4 octets
                int h1 = BASE64_CODE.indexOf(b64Str.charAt(c));
                int h2 = BASE64_CODE.indexOf(b64Str.charAt(c + 1));
                int h3 = BASE64_CODE.indexOf(b64Str.charAt(c + 2));
                int h4 = BASE64_CODE.indexOf(b64Str.charAt(c + 3));
                int h5 = BASE64_CODE.indexOf(b64Str.charAt(c + 4));
                int d6 = BASE64_CODE.indexOf(b64Str.charAt(c + 5));

                int bits =  h1<<26 |  h2<<20 |  h3<<14 | h4<<8 | h5<<2 | (d6 & 0x3);
                byte o1 = (byte) (bits>>>24 & 0xff);
                byte o2 = (byte) (bits>>>16 & 0xff);
                byte o3 = (byte) (bits>>>8 & 0xff);
                byte o4 = (byte) (bits & 0xff);

                String partialDecodedStr = new String(new byte[] {o1, o2}, UNICODE_CHARSET);

                //take care of padding
                // - if BASE64_CODE.indexOf(b64Str.charAt(c + 3)) == 64 OR b64Str.charAt(c + 3) == '='
                if (h4 == 0x40) {
                    //This 16-bit char was padding: get rid of it
                }
                else
                    partialDecodedStr = partialDecodedStr.concat(new String(new byte[] {o3, o4}, UNICODE_CHARSET));

                decodedBase64 = decodedBase64.concat( partialDecodedStr);
            }

            return decodedBase64;
        }
        catch (UnsupportedEncodingException e) {
            //TODO log4j
            logger.error(UNICODE_CHARSET + " is not supported: " + e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException("Unable to decode base64.");
        }
    }

    /**
     *
     * @param plainStr
     * @return
     */
    public static String encode16BitBase64(String plainStr) {
        String encodedBase64Str = "";

        String padding = "";
        int c = plainStr.length() % 2;  // pad string to length of multiple of 2
        if (c > 0) { while (c++ < 2) { padding += BASE64_CODE_PADDING; plainStr += '\0'; } }
        // note: doing padding here saves us doing special-case packing for trailing 1 or 2 chars

        for (c = 0; c < plainStr.length(); c+=2) {
            int hd1 = (int) plainStr.charAt(c);
            int hd2 = (int) plainStr.charAt(c+1);

            int bits = hd1<<16 | hd2;

            int h1 = bits>>26 & 0x3f;
            int h2 = bits>>20 & 0x3f;
            int h3 = bits>>14 & 0x3f;
            int h4 = bits>>8 & 0x3f;
            int h5 = bits>>2 & 0x3f;
            int d6 = bits & 0x3;

            // use hextets to index into code string
            encodedBase64Str = encodedBase64Str.concat(new String(new char[]{BASE64_CODE.charAt(h1), BASE64_CODE.charAt(h2), BASE64_CODE.charAt(h3),
                    BASE64_CODE.charAt(h4), BASE64_CODE.charAt(h5), BASE64_CODE.charAt(d6)}));
        }

        // replace 'A's from padded nulls with '='s
        encodedBase64Str = encodedBase64Str.substring(0, encodedBase64Str.length() - padding.length()) + padding;

        return encodedBase64Str;
    }
}
