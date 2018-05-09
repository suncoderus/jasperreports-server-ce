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
package com.jaspersoft.jasperserver.dto.executions;

import com.jaspersoft.jasperserver.dto.adhoc.dataset.ClientFlatDataset;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 11.02.2016
 */
@XmlRootElement(name = "resultData")
public class ClientFlatQueryResultData extends ClientQueryResultData<ClientFlatQueryResultData, ClientFlatDataset,
        Integer> {

    private ClientFlatDataset dataSet;
    private Integer totalCounts;

    public ClientFlatQueryResultData() {
    }

    public ClientFlatQueryResultData(ClientFlatQueryResultData clientFlatQueryResultData) {
        super(clientFlatQueryResultData);
        setDataSet(new ClientFlatDataset(clientFlatQueryResultData.getDataSet()));
        setTotalCounts(clientFlatQueryResultData.getTotalCounts());
    }

    @Override
    @XmlElement(name = "totalCounts")
    public Integer getTotalCounts() {
        return totalCounts;
    }

    @Override
    public ClientFlatQueryResultData setTotalCounts(Integer totalCounts) {
        this.totalCounts = totalCounts;
        return this;
    }

    @Override
    @XmlElement(name = "dataset")
    public ClientFlatDataset getDataSet() {
        return dataSet;
    }

    @Override
    public ClientFlatQueryResultData setDataSet(ClientFlatDataset dataset) {
        this.dataSet = dataset;
        return this;
    }

    public ClientFlatQueryResultData(ClientFlatDataset dataSet) {
        setDataSet(dataSet);
    }

    @Override
    public String toString() {
        return "ClientFlatQueryResultData{" +
                "dataSet=" + dataSet +
                ", totalCounts=" + totalCounts +
                ", queryParams=" + getQueryParams() +
                '}';
    }
}
