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

package com.jaspersoft.jasperserver.dto.thumbnails;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Grant Bacon <gbacon@jaspersoft.com>
 * @version $Id: $
 */

@XmlRootElement
public class ResourceThumbnail {

    private String uri;
    private String thumbnailData;

    public ResourceThumbnail() {}

    public ResourceThumbnail(ResourceThumbnail source) {
        uri = source.getUri();
        thumbnailData = source.getThumbnailData();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnailData(String thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + ((thumbnailData != null) ? thumbnailData.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceThumbnail that = (ResourceThumbnail) o;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        if (thumbnailData != null ? !thumbnailData.equals(that.thumbnailData) : that.thumbnailData != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Thumbnail{" +
                "uri='" + uri + '\'' +
                ", thumbnailData='" + thumbnailData + '\'' +
                "} " + super.toString();
    }

}
