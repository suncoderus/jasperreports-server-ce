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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.util.FTPService;
import com.jaspersoft.jasperserver.api.engine.common.util.impl.FTPUtil;
import net.sf.jasperreports.repo.InputStreamResource;
import net.sf.jasperreports.repo.Resource;
import net.sf.jasperreports.repo.StreamRepositoryService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class FtpRepositoryService implements StreamRepositoryService {
    @Override
    public InputStream getInputStream(String uri) {
        InputStream inputStream = null;
        final Matcher matcher = Pattern.compile("ftps://(([^:]*)(:([^@]*))?@)?([^:/]*)(:([^/]*))?((/[^/]+)*)/(.*)").matcher(uri);
        if (matcher.find()) {
            final FTPService.FTPServiceClient ftpServiceClient;
            try {
                ftpServiceClient = new FTPUtil().connectFTPS(matcher.group(5),
                        Integer.valueOf(matcher.group(7)), null, true, 0, null, matcher.group(2), matcher.group(4));
                ftpServiceClient.changeDirectory(matcher.group(8));
                inputStream = ftpServiceClient.getFile(matcher.group(10));
            } catch (Exception e) {
                throw new JSException(e);
            }
        }
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream(String uri) {
        throw new UnsupportedOperationException("FTP output stream isn't supported");
    }

    @Override
    public Resource getResource(String uri) {
        return getResource(uri, Resource.class);
    }

    @Override
    public void saveResource(String uri, Resource resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K extends Resource> K getResource(String uri, Class<K> resourceType) {
        InputStreamResource inputStreamResource = null;
        final InputStream inputStream = getInputStream(uri);
        if(inputStream != null) {
            inputStreamResource = new InputStreamResource();
            inputStreamResource.setInputStream(inputStream);
        }
        return (K) inputStreamResource;
    }
}
