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
package com.jaspersoft.jasperserver.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

/**
 * @author tkavanagh
 *
 * Delete extra resources created earlier in the test suite run 
 */
public class DeleteExtraTestResourcesTestNG extends BaseServiceSetupTestNG {

    protected final Log m_logger = LogFactory.getLog(DeleteExtraTestResourcesTestNG.class);

	protected void onSetUp() throws Exception {
		m_logger.info("onSetUp() called");
	}

	public void onTearDown() {
		m_logger.info("onTearDown() called");
	}

    /*
    * Entry point for starting the deletion of extra resources used of testing
    */
    @Test()
	public void deleteExtraTestResources() throws Exception {
        m_logger.info("deleteExtraTestResources() called");

        // delete reports in /reports/samples
        deleteAllCascading();
        deleteAllCharts();
        deleteParamMany();
        deleteEmployees();
        deleteEmployeeAccounts();
        deleteSalesByMonth();
        deleteAllAccounts(); 
        deleteDepartmentReport();

        // delete samples folder
        getUnsecureRepositoryService().deleteFolder(null, "/reports/samples");


    }

    private void deleteAllCascading() {
        m_logger.info("deleteAllCascading() => deleting /reports/samples/PermissionsOfUsersWithARoleSharedByLoggedIn");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/PermissionsOfUsersWithARoleSharedByLoggedIn");

        m_logger.info("deleteAllCascading() => deleting /reports/samples/Cascading_multi_select_report");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/Cascading_multi_select_report");
    }

    private void deleteAllCharts() {
        m_logger.info("deleteAllCharts() => deleting /reports/samples/StandardChartsEyeCandyReport");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/StandardChartsEyeCandyReport");

        m_logger.info("deleteAllCharts() => deleting /reports/samples/StandardChartsAegeanReport");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/StandardChartsAegeanReport");

        m_logger.info("deleteAllCharts() => deleting /reports/samples/StandardChartsReport");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/StandardChartsReport");
    }

    private void deleteParamMany() {
        m_logger.info("deleteParamMany() => deleting /reports/samples/Freight");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/Freight");
    }

    private void deleteEmployees() {
        m_logger.info("deleteEmployees() => deleting /reports/samples/Employees");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/Employees");
    }

    private void deleteEmployeeAccounts() {
        m_logger.info("deleteEmployeeAccounts() => deleting /reports/samples/EmployeeAccounts");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/EmployeeAccounts");
    }

    private void deleteTableModelDSReport() {
        m_logger.info("deleteTableModelDSReport() => deleting /reports/samples/DataSourceTableModel");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/DataSourceTableModel");
    }

    private void deleteSalesByMonth() {
        // NOTE: This delete method for the SalesByMonth report does not look symmetrical to
        // the create method due to an artifact of how Hibernate works - by simply deleting
        // the main REPORT UNIT resource, all the associated resources are auto-magically
        // deleted by Hibernate - this is not obvious, but this is how it works...
        m_logger.info("deleteSalesByMonth() => deleting /reports/samples/SalesByMonth");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/SalesByMonth");
        deleteDateDatatype();
    }

    private void deleteAllAccounts() {
        // NOTE: This delete method for the AllAccounts report does not look symmetrical to
        // the create method due to an artifact of how Hibernate works - by simply deleting
        // the main REPORT UNIT resource, all the associated resources are auto-magically
        // deleted by Hibernate - this is not obvious, but this is how it works...
        m_logger.info("deleteAllAccounts() => deleting /reports/samples/AllAccounts");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/AllAccounts");
    }

    private void deleteDepartmentReport() {
        m_logger.info("deleteDepartmentReport() => deleting /reports/samples/Department");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/Department");
    }

    private void deleteDateDatatype() {
        getUnsecureRepositoryService().deleteResource(null, "/datatypes/date");
        getUnsecureRepositoryService().deleteFolder(null, "/datatypes");
    }

}
