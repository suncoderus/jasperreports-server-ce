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
package com.jaspersoft.jasperserver.dto.authority.hypermedia;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id $
 */
public class Link {
    private String href;

    public Link() {
    }

    public Link(Link other) {
        this.href = other.getHref();
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public int hashCode() {
        return href == null ? 0 : href.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof Link) {
            Link other = (Link) obj;
            return href == other.href || (href != null && href.equals(other.href));
        }

        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "href='" + href + '\'' +
                '}';
    }
}
