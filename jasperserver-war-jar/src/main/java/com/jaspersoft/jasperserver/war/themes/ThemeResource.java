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

package com.jaspersoft.jasperserver.war.themes;

import java.util.Date;

/**
 * The holder the a single theme resource. It keeps the content for CSS or image file.
 * It also keeps the last modified date which helps to control the browser caching.
 * @author asokolnikov
 */
public class ThemeResource {

    private Date lastModified;
    private byte[] content;

    public ThemeResource(Date lastModified, byte[] content) {
        this.lastModified = lastModified;
        this.content = content;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public byte[] getContent() {
        return content;
    }
}
