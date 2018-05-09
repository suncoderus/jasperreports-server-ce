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

package com.jaspersoft.jasperserver.war.cascade;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlsInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.impl.QueryParameterInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlWithoutParameterInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlsInformationImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.QueryParameterDescriptor;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Control logic used for single input control.
 *
 * @author Zakhar Tomchenko
 * @version $Id$
 */
@Component
public class SingleInputControlLogic extends GenericInputControlLogic<InputControlImpl> {

    @Override
    protected ReportInputControlsInformation getReportInputControlsInformation(InputControlImpl container) throws CascadeResourceNotFoundException {
        ReportInputControlsInformationImpl info = new ReportInputControlsInformationImpl();

        info.setInputControlInformation(container.getName(), new ReportInputControlWithoutParameterInformation(container.getName(), container.getLabel(), container.getDescription(), null));

        if (container.getQuery() != null) {
            Query query = cachedRepositoryService.getResource(Query.class, container.getQuery());

            if (query.getParameters() != null) {
                for (QueryParameterDescriptor param : query.getParameters()) {
                    try {
                        info.setInputControlInformation(param.getParameterName(), new QueryParameterInformation(param));
                    } catch (ClassNotFoundException e) {
                        throw new JSException(e);
                    }
                }
            }
        }

        return info;
    }

    @Override
    protected ResourceReference getMainDataSource(InputControlImpl container) throws CascadeResourceNotFoundException {
        ResourceReference dataSource = null;
        if (container.getQuery() != null){
            Query query = cachedRepositoryService.getResource(Query.class, container.getQuery());
            if (query.getDataSource() == null){
                throw new CascadeResourceNotFoundException(query.getURI() + ".dataSource", "Query Data Source");
            }

            dataSource = query.getDataSource();
        }

        return dataSource;
    }

    @Override
    protected List<InputControl> getAllInputControls(InputControlImpl container) throws CascadeResourceNotFoundException {
        List<InputControl> res = new ArrayList<InputControl>(1);
        res.add(container);
        return res;
    }

    @Override
    protected Map<String, Object> getTypedParameters(List<InputControl> inputControls, Map<String, String[]> requestParameters, ReportInputControlsInformation infos, ValidationErrors validationErrors) throws CascadeResourceNotFoundException {
        Map<String, Object> res = super.getTypedParameters(inputControls, requestParameters, infos, validationErrors);

        Set<String> noControl = requestParameters.keySet();
        noControl.removeAll(res.keySet());

        InputControl dummy = new InputControlImpl();
        for (String key : noControl) {
            ReportInputControlInformation information = infos.getInputControlInformation(key);
            try {
                res.put(key, getHandlerForQueryControl(information).convertParameterValueFromRawData(requestParameters.get(key), dummy, information));
            } catch (InputControlValidationException e) {
                throw new CascadeResourceNotFoundException(key, "parameter");
            } catch (NullPointerException e){
                throw new CascadeResourceNotFoundException(key, "parameter");
            }
        }

        return res;
    }
}
