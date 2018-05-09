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

package com.jaspersoft.jasperserver.remote.services.async;

import com.jaspersoft.jasperserver.dto.common.BrokenDependenciesStrategy;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author  inesterenko
 */

abstract public class BaseImportExportTaskRunnable<T> implements TaskRunnable {

    protected volatile Map<String, Boolean> parameters;
    protected File file;
    protected final State state;

    private MessageSource messageSource;
    protected ImportExportService service;
    protected Locale locale;

    protected volatile String organizationId;
    protected volatile String brokenDependenciesStrategy;
    protected Date taskCompletionDate;

    public BaseImportExportTaskRunnable(State state) {
        this.state = state;
    }

    public File getFile(){
            return file;
        }

    public State getState(){
        return state;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ImportExportService getService() {
        return service;
    }

    public void setService(ImportExportService service) {
        this.service = service;
    }

    protected String localize(String key){
        return messageSource.getMessage(key, null, key, locale);
    }

    abstract public void prepare();

    @Override
    public String getOrganizationId() {
        return this.organizationId;
    }

    @Override
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String getBrokenDependenciesStrategy() {
        return StringUtils.isBlank(brokenDependenciesStrategy)
                ? BrokenDependenciesStrategy.FAIL.getLabel() : brokenDependenciesStrategy;
    }

    @Override
    public void setBrokenDependenciesStrategy(String value) {
        this.brokenDependenciesStrategy = BrokenDependenciesStrategy.parseString(value).getLabel();
    }

    @Override
    public Map<String, Boolean> getParameters() {
        if (this.parameters == null) {
            return null;
        } else {
            return new HashMap<String, Boolean>(this.parameters);
        }
    }

    @Override
    public void setParameters(Map parameters) {
        if (parameters == null) {
            this.parameters = null;
        } else {
            Map<String, Boolean> map = new HashMap<String, Boolean>(parameters.size());
            for (Object key : parameters.keySet()) {
                Object value = parameters.get(key);
                if (key instanceof String) {
                    if (value instanceof Boolean) {
                        map.put((String) key, (Boolean) value);
                    } else if (value instanceof String) {
                        map.put((String) key, Boolean.parseBoolean((String) value));
                    }
                }
            }
            if (map.isEmpty()) {
                this.parameters = null;
            } else {
                this.parameters = map;
            }
        }
    }

    protected Date getTaskCompletionDate() {
        return taskCompletionDate;
    }
}
