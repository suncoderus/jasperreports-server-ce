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
package com.jaspersoft.jasperserver.war.cascade.utils;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.handlers.ValuesLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ListOfValuesLoaderMock implements ValuesLoader{
    private List<ListOfValuesItem> values;
    private Set<String> masterDependencies;

    public List<ListOfValuesItem> getValues() {
        return values;
    }

    public void setValues(List<ListOfValuesItem> values) {
        this.values = values;
    }

    public Set<String> getMasterDependencies() {
        return masterDependencies;
    }

    public void setMasterDependencies(Set<String> masterDependencies) {
        this.masterDependencies = masterDependencies;
    }

    @Override
    public List<ListOfValuesItem> loadValues(InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, Map<String, Class<?>> parameterTypes, ReportInputControlInformation info) throws CascadeResourceNotFoundException {
        return values;
    }

    @Override
    public Set<String> getMasterDependencies(InputControl inputControl, ResourceReference dataSource) throws CascadeResourceNotFoundException {
        return masterDependencies;
    }
}
