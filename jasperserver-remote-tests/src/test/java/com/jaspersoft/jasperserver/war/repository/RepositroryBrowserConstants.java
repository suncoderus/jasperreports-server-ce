/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war.repository;

import java.util.HashMap;
import java.util.Map;

import com.jaspersoft.jasperserver.war.JasperServerConstants;

public interface RepositroryBrowserConstants {

	 String homePageUrl = JasperServerConstants.instance().BASE_URL +
	     "/jasperserver/home.html";
	 String  repositoryLink = "REPOSITORY";
	 String  repositoryPageText1 = "Repository Management";
	 String  repositoryPageText2 = "Path:";
	 String  dataTypePageText = "Locate Data Type";
	 String  locateDataPageText = "Minimum value";
	 String  repoPageText = "Please enter the JDBC Data Source description information";
	 String  pageText1  = "Folder";
	 String  pageText2  = "Parent folder";
	 String  addPageText1 = "Name cannot be empty";
	 String  addPageText2 = "Label cannot be empty";
	 String  dataSrcPageText = "Please enter the datasource Name";
	 String	 dataTypepageText = "Edit Data Type";
	 String  addFileText = "File Resource";
	 String  successText = "successfully uploaded";
	 String	 rootLink = "[root]";
	 String	 datasourceLink = "datasources";
	 String	 editLinkText = "edit";
	 String  repFormText = "frm";
	 String  addNewFormText = "fmEditFolder";
	 String  validConfFormText = "fmCRValidConf";
	 String  jndiPropFormText ="fmJNDIProps";
	 String  dataTypeFormText ="fmDataType";
	 String  addNewBtnText = "_eventId_Add";
	 String  nextBtnText = "_eventId_Next";
	 String  removeBtnText = "_eventId_Remove";
	 String  editBtnText = "_eventId_Edit";
	 String  dataSrcFormText = "fmCRChTyp";
	 String  dataDescFormText = "aqRsrRsrNm";
	 String  inputCtrlDescFormText = "fmCRContNam";
	 String  locateDataPageFormText = "fmCRContLov";
	 String  uploadResourceFormText = "fmCRPopJrxml_1";
	 String  saveBtnText = "_eventId_save";
	 String  dataSaveBtnText = "_eventId_Save";
	 String  dataSourceValueText = "com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource";
	 String  dataTypeValueText = 	 "com.jaspersoft.jasperserver.api.metadata.common.domain.DataType";
	 String	 fileSrcTypeValueText = "com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource";
	 String	 fileInputControlText = "com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl";
	 String  jndiPageText = "Please enter the JNDI Data Source description information:";

	 Map resourceMap = new HashMap(9);
}
