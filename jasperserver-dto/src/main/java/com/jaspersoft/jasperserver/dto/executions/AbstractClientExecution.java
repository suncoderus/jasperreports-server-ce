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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 13.01.2016
 */
public abstract class AbstractClientExecution<T extends AbstractClientExecution<T>> {
    @Valid
    private ClientQueryParams params;
    @NotNull
    private String dataSourceUri;

    public AbstractClientExecution() {}

    public AbstractClientExecution(AbstractClientExecution<T> clientExecution) {
        dataSourceUri = clientExecution.getDataSourceUri();
        if (clientExecution.getParams() != null) {
            setParams(new ClientQueryParams(clientExecution.getParams()));
        }
    }

    public ClientQueryParams getParams() {
        return params;
    }

    // safety of the unchecked cast to T is assured by the rule of usage T generic parameter.
    @SuppressWarnings("unchecked")
    public T setParams(ClientQueryParams params) {
        this.params = params;
        return (T) this;
    }

    public String getDataSourceUri() {
        return dataSourceUri;
    }

    // safety of the unchecked cast to T is assured by the rule of usage T generic parameter.
    @SuppressWarnings("unchecked")
    protected T setDataSourceUri(String dataSourceUri) {
        this.dataSourceUri = dataSourceUri;
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractClientExecution clientExecution = (AbstractClientExecution) o;

        return !(params != null ? !params.equals(clientExecution.params) : clientExecution.params != null);
    }

    @Override
    public int hashCode() {
        int result = params != null ? params.hashCode() : 0;
        result = 31 * result + (dataSourceUri != null ? dataSourceUri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AbstractClientExecution{" +
                "params=" + params +
                ", dataSourceUri='" + dataSourceUri + '\'' +
                '}';
    }

}