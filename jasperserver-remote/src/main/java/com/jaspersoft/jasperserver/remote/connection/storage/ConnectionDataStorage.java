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
package com.jaspersoft.jasperserver.remote.connection.storage;

import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class ConnectionDataStorage {
    @Resource(name = "connectionsCache")
    private Cache cache;

    public UUID save(ConnectionDataPair item){
        final UUID uuid = UUID.randomUUID();
        cache.put(new Element(uuid, item));
        return uuid;
    }

    public ConnectionDataPair get(UUID uuid){
        final Element element = cache.get(uuid);
        if (element == null) throw new ResourceNotFoundException(uuid.toString());
        return (ConnectionDataPair) element.getObjectValue();
    }

    public void delete(UUID uuid){
        cache.remove(uuid);
    }

    public void update(UUID uuid, ConnectionDataPair item){
        cache.replace(new Element(uuid, item));
    }

}
