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

package com.jaspersoft.jasperserver.war.httpheaders;

import org.apache.http.Header;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.UrlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.isEmpty;

/**
 *
 * @author Stas Chubar
 */
public class HeadersRule {
    public static String[] HTTP_METHODS;

    private String method;
    private Pattern urlPattern;
    private List<Header> headers;

    static {
        HttpMethod[] methods = HttpMethod.values();
        int i = 0;

        HTTP_METHODS = new String[methods.length];
        for (HttpMethod method: methods) {
            HTTP_METHODS[i++] = method.toString();
        }
        Arrays.sort(HTTP_METHODS);
    }

    public HeadersRule() {
    }

    public HeadersRule(String method, Pattern urlPattern, List<Header> headers) {
        this.method = method;
        this.urlPattern = urlPattern;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Pattern getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(Pattern urlPattern) {
        this.urlPattern = urlPattern;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public boolean isValid() {
        return !isNullOrEmpty(this.method) && this.urlPattern != null && !isEmpty(this.headers)
                && Arrays.binarySearch(HTTP_METHODS, this.method) > -1;
    }

    public boolean matches(HttpServletRequest request) {
        String resourceUrl = getResourceUrl(request);
        if (resourceUrl != null && this.isValid() && this.method.equals(request.getMethod())) {
            Matcher matcher = this.urlPattern.matcher(resourceUrl);

            return matcher.matches();
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeadersRule that = (HeadersRule) o;

        if (method != null ? !method.equals(that.method) : that.method != null) return false;
        if (urlPattern != null ? !urlPattern.pattern().equals(that.urlPattern.pattern()) : that.urlPattern != null)
            return false;
        return !(headers != null ? !headers.equals(that.headers) : that.headers != null);

    }

    @Override
    public int hashCode() {
        int result = method != null ? method.hashCode() : 0;
        result = 31 * result + (urlPattern != null ? urlPattern.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        return result;
    }

    protected String getResourceUrl(HttpServletRequest httpRequest) {
        return UrlUtils.buildRequestUrl(httpRequest);
    }

}
