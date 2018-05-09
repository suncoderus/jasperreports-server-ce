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

package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import mondrian.olap.MondrianProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * @author sbirney (sbirney@users.sourceforge.net)
 * @version $Id: EditMondrianPropertiesAction.java 8410 2007-05-29 23:34:07Z melih $
 */
public class EditMondrianPropertiesAction extends MultiAction {

    private static final Log log = LogFactory.getLog(EditMondrianPropertiesAction.class);

    private static final String OPTION_NAME = "name";
    private static final String OPTION_VALUE = "value";

    protected PropertiesManagementService propertiesManagementService;

    private void createAuditEvent() {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                auditContext.createAuditEvent("updateAnalysisOptions");
            }
        });
    }

    private void addParamToAuditEvent(final Object param) {
        auditContext.doInAuditContext("updateAnalysisOptions", new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.addPropertyToAuditEvent("analysisOption", param, auditEvent);
            }
        });
    }

    private void closeAuditEvent() {
        auditContext.doInAuditContext("updateAnalysisOptions", new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.closeAuditEvent(auditEvent);
            }
        });
    }


    public Event saveSingleProperty(RequestContext context) throws Exception {
        log.info("Saving OLAP Server Property");
        String key = context.getRequestParameters().get(OPTION_NAME);
        String value = context.getRequestParameters().get(OPTION_VALUE);

        String res = "";
        String error = validate(key, value);

        if (error == null) {
            createAuditEvent();
            saveSingleProperty(key, value);
            addParamToAuditEvent(new Object[]{key, value});
            closeAuditEvent();
            res = "{\"result\":\"JAM_056_UPDATED\",\"optionName\":\"" + key + "\"}";
        } else {
            res = "{\"error\":\"" + error + "\",\"optionName\":\"" + key + "\"}";
        }
        context.getRequestScope().put("ajaxResponseModel", res);
        return success();
    }

    protected void saveSingleProperty(String key, String value){
        getPropertiesManagementService().setProperty(key, value);
    }


    private String validate(String option, String value) {
        Integer test;

        if (option.equals("mondrian.query.limit")
                || option.equals("mondrian.rolap.evaluate.MaxEvalDepth")
                || option.equals("mondrian.result.highCardChunkSize")
                || option.equals("mondrian.rolap.SparseSegmentValueThreshold")
                || option.equals("mondrian.rolap.maxConstraints")
                || option.equals("adhoc.olap.maxFilterValues")
                || option.equals("mondrian.rolap.maxQueryThreads")
                || option.equals("mondrian.rolap.evaluate.MaxEvalDepth")
                || option.equals("mondrian.result.highCardChunkSize")
                || option.equals("mondrian.rolap.SparseSegmentValueThreshold")
                || option.equals("mondrian.rolap.maxCacheThreads")
                || option.equals("mondrian.rolap.maxSqlThreads")
                || option.equals("mondrian.rolap.maxConstraints")
                || option.equals("mondrian.server.monitor.executionHistorySize")
                ) {
            try {
                test = new Integer(value);
                if (test.intValue() < 1) {
                    return "JAM_049_ONE_OR_GREATER";
                }
            } catch (Exception e) {
                log.debug(option + " invalid input");
                return "JAM_019_WHOLE_NUMBER_ERROR";
            }

        } else if (option.equals("mondrian.rolap.cellBatchSize")) {
            try {
                test = new Integer(value);
            } catch (Exception e) {
                log.debug(option + " invalid input");
                return "JAM_019_WHOLE_NUMBER_ERROR";
            }
        } else if (option.equals("mondrian.xmla.drillthroughMaxRows")
                || option.equals("mondrian.result.limit")
                || option.equals("mondrian.rolap.iterationLimit")
                || option.equals("mondrian.olap.fun.crossjoin.optimizer.size")
                || option.equals("mondrian.rolap.queryTimeout")
                ) {
            try {
                test = new Integer(value);
                if (test.intValue() < 0) {
                    return "JAM_048_ZERO_OR_GREATER";
                }
            } catch (Exception e) {
                log.debug(option + " invalid input");
                return "JAM_019_WHOLE_NUMBER_ERROR";
            }

        } else if (option.equals("mondrian.rolap.SparseSegmentDensityThreshold")) {
            // TODO: Check if this parameter is still valid
            try {
                Double d = new Double(value);
                if (d.doubleValue() > 1.0 || d.doubleValue() < 0.0) {
                    log.debug(option + " invalid input");
                    return "JAM_020_RATIO_NUMBER_ERROR";
                }
            } catch (Exception e) {
                log.debug(option + " invalid input");
                return "JAM_020_RATIO_NUMBER_ERROR";
            }

        } else if (option.equals("mondrian.util.memoryMonitor.percentage.threshold")) {
            try {
                test = new Integer(value);
                if (test.intValue() < 1 || test.intValue() > 99) {
                    return "JAM_050_ONE_TO_99";
                }
            } catch (Exception e) {
                log.debug(option + " invalid input");
                return "JAM_019_WHOLE_NUMBER_ERROR";
            }

        } else if ((option.equals("mondrian.rolap.aggregates.jdbcFactoryClass")
                || option.equals("mondrian.util.MemoryMonitor.class")
                || option.equals("mondrian.calc.ExpCompiler.class")
        ) && !value.trim().equals("null")) {
            try {
                Class.forName(value);
            } catch (Exception e) {
                log.debug(option + " invalid input");
                return "JAM_051_INVALID_CLASS";
            }
        }

        return null;
    }

    private RepositoryService repository;

    public RepositoryService getRepository() {
        return repository;
    }

    public void setRepository(RepositoryService repository) {
        this.repository = repository;
    }

    private AuditContext auditContext;

    public AuditContext getAuditContext() {
        return auditContext;
    }

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }
    
	public PropertiesManagementService getPropertiesManagementService() {
		return propertiesManagementService;
	}

	public void setPropertiesManagementService(
			PropertiesManagementService propertiesManagementService) {
		this.propertiesManagementService = propertiesManagementService;
	}

}

