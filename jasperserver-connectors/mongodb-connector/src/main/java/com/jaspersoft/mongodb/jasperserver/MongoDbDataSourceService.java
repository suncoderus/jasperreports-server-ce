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

package com.jaspersoft.mongodb.jasperserver;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.CustomReportDataSourceService;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class MongoDbDataSourceService extends MongoDbDataSourceService45 implements CustomReportDataSourceService {
    private final static Logger logger = Logger.getLogger(MongoDbDataSourceService.class);

    @Override
    public boolean testConnection() throws JRException {
        if (logger.isDebugEnabled()) {
            logger.debug("Testing connection");
        }
        try {
            createConnection();
            if (connection == null) {
                return false;
            }
            connection.test();
            return true;
        } finally {
            closeConnection();
        }
    }

}
