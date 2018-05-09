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
package com.jaspersoft.jasperserver.war.wizard;

import java.util.HashMap;
import java.util.Map;

import com.jaspersoft.jasperserver.war.JasperServerConstants;

public interface  WizardScreenConstants { 
	
    String 		 homePageUrl 	 = JasperServerConstants.instance().BASE_URL+"/jasperserver/home.html";
	String 		 createPageLink	 = "[CREATE REPORT]";
	String     	 repNameFormText = "fmCRPopNLD";
	String     	 formText = "fmCRPopJrxml_1";
	String     	 listPageFormText = "fmCRExtRsrCh";
	String 		 errorFormText = "fmJsErrPage";
	String     	 sourceSellFormText = "fmCRContQd";
	String     	 dataSrcPageFormText = "fmCRChTyp";
	String     	 propertyPageFormText = "fmJNDIProps";
	String     	 descriotionFormText = "fmJNDINaming";
	String     	 resDescriptiomFormText = "aqRsrRsrNm";
	String     	 ResourceFormText = "fmCRExtRsrCh";
	String     	 descriptionFormText = "fmCRContNam";
	String     	 datadescFormText = "fmCRContLov";
	String     	 dataTypeFormText = "fmDataType";
	String     	 testFormText = "fmCRValidConf";
	String		 testViewFormText = "fmCRTstVw";
	String		 publishFormText  = "fmCRWCrConf";
	String		 floderSelFormText  = "fmCRUsrRepoOpt";
	String     	 nameText		 = "Name:";
	String     	 pageText		 = "Report Wizard - Naming";
	String		 reportListPageText    = "Report Wizard - Resources List";
	String       locateInputControlText = "Locate Input Control";
	String		 reportInputControlText = "Report Wizard - Input Control";
	String		 reportFileResPageText  = "Report Wizard - File Resource";
	String		 fileResourcePageText = "File Resource";
	String		 createReportPageText = "CREATE REPORT WIZARD";
	String		 addControlPageText  = "newFile";
	String		 resourceFileText = "ResourceFile";
	String		 fileAddedText = "Added";
	String   	 errMsgServiceNameText1 = "Please enter the JNDI name";
	String     	 errMsgText2= "Please enter the datasource Name";
	String		 errorMsgText3 = "Please enter an authorised username";
	String     	 errorMsgRepPageText1  = "Report name must not be empty";
	String     	 errorMsgRepPageText2  = "Report label must not be empty";
	String     	 errorMsgLoadPageText  = "Select a valid JRXML file";
	String     	 errMsgInvalidFileText  = "Error Message";
	String     	 errMsgServiceNameText = "Please enter the service name";
	String     	 errMsgDescriptionText2 = "Please enter the datasource label";
	String		 successJNDIMsgText	 = "The JNDI Service entry was successfully created.";
	String 		 successLoadMsgText  = "successful";
	String		 validationPageText = "The report was successfully validated.";
	String		 testPageText    = "Test view of the report will be here";
	String		 publishPageText = "You may now Publish your Report.";
	String		 loadPageText1   = "Report Wizard - Main JRXML";
	String		 dataSrcPageText1 = "Report Wizard - Data Source Type";
	String		 extResourcePageText1 = "Are there additional external Resources the report needs";
	String		 inputControlPageText2 = "Does the report need any Input Controls ?";
	String		 testPageText1 = "Below is the list of Report Resources.";
	String		 folderSelPageText = "Choose a repository folder to Publish the Report into:";
	String		 publishCnfrmPageText = "The Report was published successfully to";
	String		 sourceSelectionPageText = "Report Wizard - Locate Data Source";
	String		 cancelBtnText   = "_eventId_Cancel";
	String		 backBtnText     = "_eventId_Back";
	String		 nextBtnText = "_eventId_Next";
	String		 savetBtnText = "_eventId_save";
	String		 srartAgnBtnText = "_eventId_Cancel";
	String		 viewRepBtnText = "_eventId_TestView";
	String		 publishRepBtnText = "_eventId_Publish";
	String		 saveNewBtnText = "_eventId_NewFolder";
	String		 saveSelectedBtnText = "_eventId_Save"; 
	String		 addCtrlBtnText  = "_eventId_AddControl";
	String		 addResourceBtnText  = "_eventId_AddResource";
	String		 finishButtonText  = "_eventId_Close";
	String		 fileFieldText	 = "data";
	String		 ViewReportPageText  = "Repository Browser";
	String 		 name = "testName";
	String 		 label = "testLabel";
	String 		 serviceName = "testServiceName";
	String 		 driver = "com.test";
	String 		 url = "http://localhost:8080";
	String 		 username = "userName";
	String 		 password = "Password";	
	 String  repositoryLink = "REPOSITORY";
	 String  repositoryPageText1 = "Repository Browser";
	 String  repositoryPageText2 = "Path:";
	 String  dataTypePageText = "Data Type";
	 String	rootLink = "[root]";
	 String  repFormText = "frm";	
	 String  addNewBtnText = "_eventId_Add";
	 String  repositoryUnitValueText = "com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit";
	
		
	Map resourceMap = new HashMap(6);
	
}
 