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

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValuesInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlValueInformationImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.ReportInputControlValuesInformationImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import org.springframework.context.MessageSource;

import java.util.ResourceBundle;

/**
 * Utility class for getting localized info for list of values
 *
 * @author Sergey Prilukin
 */
public class ReportInputControlValuesInformationLoader {

    public static ReportInputControlValuesInformation getReportInputControlValuesInformation(
            ListOfValues listOfValues, ResourceBundle resourceBundle, MessageSource messageSource) {

        ReportInputControlValuesInformationImpl valuesInformation = null;

        if (listOfValues != null){
            valuesInformation = new ReportInputControlValuesInformationImpl();

            ListOfValuesItem[] listOfValuesItem = listOfValues.getValues();
            for (ListOfValuesItem valuesItem : listOfValuesItem) {
                ReportInputControlValueInformationImpl valueInformation = new ReportInputControlValueInformationImpl();
                valueInformation.setPromptLabel(InputControlLabelResolver.resolve(valuesItem.getLabel(), resourceBundle, messageSource));
                valueInformation.setDefaultValue(valuesItem.getValue());
                valuesInformation.setInputControlValueInformation(valuesItem.getLabel(), valueInformation);
            }

        }

        return valuesInformation;
    }

}
