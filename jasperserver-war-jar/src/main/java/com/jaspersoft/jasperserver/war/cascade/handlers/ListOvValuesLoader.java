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
package com.jaspersoft.jasperserver.war.cascade.handlers;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class ListOvValuesLoader implements ValuesLoader{
    @Resource
    private CachedRepositoryService cachedRepositoryService;
    @Override
    public List<ListOfValuesItem> loadValues(InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, Map<String, Class<?>> parameterTypes, ReportInputControlInformation info) throws CascadeResourceNotFoundException {
        final ListOfValues listOfValues = cachedRepositoryService.getResource(ListOfValues.class, inputControl.getListOfValues());
        return listOfValues != null ? Arrays.asList(listOfValues.getValues()) : null;
    }

    @Override
    public Set<String> getMasterDependencies(InputControl inputControl, ResourceReference dataSource) {
        // no master dependencies for this type
        return null;
    }
}
