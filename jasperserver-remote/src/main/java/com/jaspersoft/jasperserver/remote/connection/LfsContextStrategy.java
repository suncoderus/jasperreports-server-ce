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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.dto.connection.LfsConnection;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class LfsContextStrategy implements ContextManagementStrategy<LfsConnection, LfsConnection> {
    @Override
    public LfsConnection createContext(LfsConnection contextDescription, Map<String, Object> data) {
        final String path = contextDescription.getPath();
        File file = new File(path);
        if (!file.exists() || !file.isDirectory() || !canWriteToDirectory(path)) {
            throw new AccessDeniedException(path);
        }
        return contextDescription;
    }

    protected boolean canWriteToDirectory(String path) {
        boolean canWrite = false;
        final File testFile = new File(path + File.separator + "JRS_canWriteFile.tmp");
        try {
            if (testFile.exists()) {
                testFile.delete();
            }
            canWrite = testFile.createNewFile();
        } catch (IOException e) {
            // do nothing. Directory isn't writable
        } catch (SecurityException e) {
            // do nothing. Directory isn't writable
        } finally {
            if (testFile.exists()) {
                try {
                    testFile.delete();
                } catch (SecurityException e) {
                    // no delete permissions. Let it be...
                }
            }
        }
        return canWrite;
    }

    @Override
    public void deleteContext(LfsConnection contextDescription, Map<String, Object> data) {

    }

    @Override
    public LfsConnection getContextForClient(LfsConnection contextDescription, Map<String, Object> data) {
        return contextDescription;
    }
}
