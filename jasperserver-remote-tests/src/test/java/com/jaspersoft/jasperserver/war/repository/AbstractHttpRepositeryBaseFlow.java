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

import java.io.File;

import com.jaspersoft.jasperserver.war.HttpUnitBaseTestCase;
import com.jaspersoft.jasperserver.war.wizard.TestAttribute;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.UploadFileSpec;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;

public abstract class AbstractHttpRepositeryBaseFlow 
	extends HttpUnitBaseTestCase 
	implements RepositroryBrowserConstants{
	
	private static WebForm addNewForm;
	private static WebForm repositoryForm; 
	private static SubmitButton saveButton; 
	private static SubmitButton addNewButton;
	
	
	/**
	 * Constructor
	 *
	 * @param s
	 **/
	public AbstractHttpRepositeryBaseFlow(String s) {
		super(s);
		
		resourceMap.put("Jrxml1", "AllAccounts.jrxml");
		resourceMap.put("Jrxml2", "SalesByMonth.jrxml");
		resourceMap.put("Jrxml3", "SalesByMonthDetail.jrxml");
		resourceMap.put("Img1", "logo.jpg");
		resourceMap.put("Img2", "report.jpg");
		resourceMap.put("text", "test.txt");
		resourceMap.put("font", "arial.ttf");
		resourceMap.put("jar","httpunit.jar");
		resourceMap.put("resourceBundle","jndi.properties");
	}

	/**
	 * Checks through commonLoginFunction for logging in in at first time 
	 *
	 * @throws Exception if fails
	 **/
	public void setUp()
	  throws Exception {
			wResponse = commonLoginFunction(homePageUrl);
		}
			
	
	
	//****--------------------------------------------------------------------------*****/
	//*				HttpUnit test cases		                                            */
	//****--------------------------------------------------------------------------*****/	

		
	public void repositoryFlow(TestAttribute attrib) throws Exception {
		
			
		//		step 1:Getting the repository browser page
		checkRepositoryPage();
		//		step 2:Adding New report to root folder
		checkAddNamePage(attrib);
		//		step 3:Adding file to Data Source folder
		addForDataSource(attrib);
		//		step 4:Adding file to Data Type folder
		addForDataType(attrib);
		//		step 5: Add different files
		addFile(attrib);
		//		step 6: Add Input Control files
		addInputControlFile(attrib);
		
	}	
	
	
	//step 1:Getting the repository browser page
	private void checkRepositoryPage() throws Exception {
		//this is response for home page
		assertNotNull(" Home Page Response is Null ", wResponse);
		
		//checks for the Repository link on the home page
		WebLink link = wResponse.getLinkWith(repositoryLink);
		assertNotNull(repositoryLink+" Link is Null ", link);

		wResponse = link.click();
		
		//checking if the link exist on the page
		assertNotNull("Link element is not on the page",wResponse.getElementsWithName(rootLink));
		
		//checking for the text on the Repositery Browser page
		String opt = wResponse.getText();
		if ((opt == null) || (opt.trim().length() == 0)) {
			fail("Text not found in response");
		}

		assertTrue((opt.indexOf(repositoryPageText1) != -1) && (opt.indexOf(repositoryPageText2) != -1));	
		
		//createing new report
		repositoryForm = wResponse.getFormWithName(repFormText);
		assertNotNull(" Form is Null ", repositoryForm);
		
		addNewButton = repositoryForm.getSubmitButton(addNewBtnText);
		assertNotNull(" Button is Null ", addNewButton);
		
		wResponse = repositoryForm.submit(addNewButton);
		
		String string = wResponse.getText();
		assertNotNull(" page text is Null ", string);
		
		if ((string == null) || (string.trim().length() == 0)) {
			fail("Text not found in response");
		}
		
		assertTrue((string.indexOf(pageText1) != -1) && (string.indexOf(pageText2) != -1));	
	}
	
	
	
	//step 2:Adding New report
	private void checkAddNamePage(TestAttribute attrib) throws Exception {
		
		//this is response for add new file page
		assertNotNull(" add new button Response is Null ", wResponse);
		
		addNewForm = wResponse.getFormWithName(addNewFormText);
		assertNotNull(" Form is Null ", addNewForm);
		
		saveButton = addNewForm.getSubmitButton(saveBtnText);
		assertNotNull(" button is Null ", saveButton);
		
		//saving file without giving name and label
		wResponse = addNewForm.submit(saveButton);
		
		String string = wResponse.getText();
		assertNotNull(" page text is Null ", string);
		
		if(string == null || string.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(string.indexOf(addPageText1)!= -1 && string.indexOf(addPageText2)!= -1);
		
		//saving file with proper details
		addNewForm.setParameter("actualFolder.name",attrib.getDataSourceReportName());
		addNewForm.setParameter("actualFolder.label",attrib.getLabel());
		
		wResponse = addNewForm.submit(saveButton);
		
	}
	
	
	//step 3:Adding file to Data Source folder
	private void addForDataSource(TestAttribute attrib) throws Exception {
		
		assertNotNull(" add new button Response is Null ", wResponse);
		
		repositoryForm = wResponse.getFormWithName(repFormText);
		assertNotNull(" Form is Null ", repositoryForm);
		
		addNewButton = repositoryForm.getSubmitButton(addNewBtnText);
		assertNotNull(" Button is Null ", addNewButton);
		
		repositoryForm.setParameter("cmbResourceType",dataSourceValueText);
		
		wResponse = repositoryForm.submit(addNewButton);
		
		dataSourcePage(attrib);
	}
	
	
	// Get Data source Configuration page
	private void dataSourcePage(TestAttribute attrib)throws Exception {
		
		assertNotNull(" data source sellection page is Null ", wResponse);
		
		WebForm dataSrcForm = wResponse.getFormWithName(dataSrcFormText);
		assertNotNull(" Form is Null ", dataSrcForm);
		
		SubmitButton nextButton = dataSrcForm.getSubmitButton(nextBtnText);
		assertNotNull(" button is Null ", nextButton);
		
		dataSrcForm.setParameter("type",attrib.getDataSourceAttrb().getDataSourceType());
		
		wResponse = dataSrcForm.submit(nextButton);
		
		dataDescriptionPage(attrib);
	}
	
	
	// Data description page
	private void dataDescriptionPage(TestAttribute attrib) throws Exception {
		
		assertNotNull(" data source sellection page is Null ", wResponse);
		
		WebForm form = wResponse.getFormWithName(jndiPropFormText);
		assertNotNull(" Form is Null ", form);
		
		SubmitButton saveButton = form.getSubmitButton(dataSaveBtnText);
		assertNotNull(" button is Null ", saveButton);
		
		//checking save button with error message
		wResponse = form.submit(saveButton);
		
		String str = wResponse.getText();
		assertNotNull(" page text is Null ", str);
		
		if(str == null || str.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(str.indexOf(dataSrcPageText)!= -1);
		
		//checking save button with proper response
		form.setParameter("reportDataSource.name",attrib.getDataSourceReportName());
		form.setParameter("reportDataSource.label",attrib.getLabel());
		form.setParameter("reportDataSource.jndiName",attrib.getServiceName());
		
		wResponse = form.submit(saveButton);
		
		String text = wResponse.getText();
		assertNotNull(" page text is Null ", text);
		
		if(text == null || text.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(text.indexOf("TestJNDI")!= -1);		
		
	}
	
	
	//step 4: Adding file to Data type folder
	private void addForDataType(TestAttribute attrib)throws Exception {
		
		assertNotNull(" repository browser sellection page is Null ", wResponse);
		
		//createing new report
		repositoryForm = wResponse.getFormWithName(repFormText);
		assertNotNull(" Form is Null ", repositoryForm);
		
		addNewButton = repositoryForm.getSubmitButton(addNewBtnText);
		assertNotNull(" button is Null ", addNewButton);
		
		repositoryForm.setParameter("cmbResourceType",dataTypeValueText);
		
		wResponse = repositoryForm.submit(addNewButton);
		
		String string = wResponse.getText();
		assertNotNull(" page text is Null ", string);
		
		if(string==null || string.trim().length()==0)
			fail("there is no text on the page");
		
		assertTrue(string.indexOf(dataTypepageText)!= -1);
		
		editDataTypePage(attrib);
	}
	
	private void editDataTypePage(TestAttribute attrib) throws Exception {
		
		assertNotNull(" repository browser sellection page is Null ", wResponse);
		
		WebForm dataTypeForm = wResponse.getFormWithName(dataTypeFormText);
		assertNotNull(" Form is Null ", dataTypeForm);
		
		SubmitButton saveButton = dataTypeForm.getSubmitButton(saveBtnText);
		assertNotNull(" button is Null ", saveButton);
		
		dataTypeForm.setParameter("dataType.name",attrib.getDataTypeReportName());
		dataTypeForm.setParameter("dataType.label",attrib.getLabel());
		
		wResponse = dataTypeForm.submit(saveButton);		
	}
	
	//step 5: Add file
	private void addFile(TestAttribute attrib)throws Exception {
		
		assertNotNull(" repository browser sellection page is Null ", wResponse);
		
		//createing new report
		repositoryForm = wResponse.getFormWithName(repFormText);
		assertNotNull(" Form is Null ", repositoryForm);
		
		addNewButton = repositoryForm.getSubmitButton(addNewBtnText);
		assertNotNull(" button is Null ", addNewButton);
		
		repositoryForm.setParameter("cmbResourceType",fileSrcTypeValueText);
		
		wResponse = repositoryForm.submit(addNewButton);
			
		String str = wResponse.getText();
		assertNotNull(" page text is Null ", str);
		
		if(str == null || str.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(str.indexOf(addFileText)!= -1);
		
		uploadResourceFileText(attrib);
	}
	
	private void uploadResourceFileText(TestAttribute attrib) throws Exception{
		
		assertNotNull(" repository browser sellection page is Null ", wResponse);
		
		WebForm uploadForm = wResponse.getFormWithName(uploadResourceFormText);
		assertNotNull(" Form is Null ", uploadForm);
		
		SubmitButton nextButton = uploadForm.getSubmitButton(nextBtnText);
		assertNotNull(" button is Null ", nextButton);
		
		File             file      = new File(getJrxmlFileResourceURL(attrib.getJrxml()));
		UploadFileSpec[] uploadFile = new UploadFileSpec[] { new UploadFileSpec(file) };
		
		uploadForm.setParameter("newData", uploadFile);
		
		wResponse = uploadForm.submit(nextButton);
		
		String str = wResponse.getText();
		assertNotNull(" page text is Null ", str);
		
		if(str == null || str.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(str.indexOf(successText)!= -1);
		
		
		resourceDescriptionPage(attrib);
	}
	
	private void resourceDescriptionPage(TestAttribute attrib) throws Exception {
		
		assertNotNull(" repository browser sellection page is Null ", wResponse);
		
		WebForm descriptionForm = wResponse.getFormWithName(dataDescFormText);
		assertNotNull(" Form is Null ", descriptionForm);
		
		SubmitButton saveButton = descriptionForm.getSubmitButton(dataSaveBtnText);
		assertNotNull(" button is Null ", saveButton);
		
		descriptionForm.setParameter("fileResource.name",attrib.getJRXMLReportName());
		descriptionForm.setParameter("fileResource.label",attrib.getLabel());
		
		wResponse = descriptionForm.submit(saveButton);
		
		String str = wResponse.getText();
		assertNotNull(" page text is Null ", str);
		
		if(str == null || str.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(str.indexOf(repositoryPageText1)!= -1);
	}	

	
	//	step 6: Add Input Control file
	private void addInputControlFile(TestAttribute attrib)throws Exception {
		
		assertNotNull(" repository browser sellection page is Null ", wResponse);
	
		//createing new report
		repositoryForm = wResponse.getFormWithName(repFormText);
		assertNotNull(" Form is Null ", repositoryForm);
		
		addNewButton = repositoryForm.getSubmitButton(addNewBtnText);
		assertNotNull(" button is Null ", addNewButton);
		
		repositoryForm.setParameter("cmbResourceType",fileInputControlText);
		
		wResponse = repositoryForm.submit(addNewButton);
			
		String str = wResponse.getText();
		assertNotNull(" page text is Null ", str);
		
		if(str == null || str.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(str.indexOf("Input Control")!= -1);
	
		inputControlDescriptionPage(attrib);
	}
	
	private void inputControlDescriptionPage(TestAttribute attrib) throws Exception{
		
		assertNotNull(" Add Input File sellection page is Null ", wResponse);
		
		WebForm descriptionForm = wResponse.getFormWithName(inputCtrlDescFormText);
		assertNotNull(" Form is Null ", descriptionForm);
		
		SubmitButton nextButton = descriptionForm.getSubmitButton(nextBtnText);
		assertNotNull(" button is Null ", nextButton);
		
		descriptionForm.setParameter("inputControl.name",attrib.getInputControlReportName());
		descriptionForm.setParameter("inputControl.label",attrib.getLabel());
		
		wResponse = descriptionForm.submit(nextButton);
		
		String str = wResponse.getText();
		assertNotNull(" page text is Null ", str);
		
		if(str == null || str.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(str.indexOf(dataTypePageText)!= -1);
		
		locateDataPage(attrib);
	}
	
	private void locateDataPage(TestAttribute attrib)throws Exception{
		
		assertNotNull(" File Description page is Null ", wResponse);
		
		WebForm locateDataForm = wResponse.getFormWithName(locateDataPageFormText);
		assertNotNull(" Form is Null ", locateDataForm);
		
		SubmitButton nextButton = locateDataForm.getSubmitButton(nextBtnText);
		assertNotNull(" button is Null ", nextButton);
		
		locateDataForm.setParameter("source","LOCAL");
		
		wResponse = locateDataForm.submit(nextButton);
		
		String str = wResponse.getText();
		assertNotNull(" page text is Null ", str);
		
		if(str == null || str.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(str.indexOf(locateDataPageText)!= -1);
		
		dataTypeDescriptionPage(attrib);
	}
	
	
	private void dataTypeDescriptionPage(TestAttribute attrib) throws Exception{
		
		assertNotNull(" Locate Data page is Null ", wResponse);
				
		WebForm dataDescriptionForm = wResponse.getFormWithName(dataTypeFormText);
		assertNotNull(" Form is Null ", dataDescriptionForm);
		
		SubmitButton saveButton = dataDescriptionForm.getSubmitButton(saveBtnText);
		assertNotNull(" button is Null ", saveButton);
		
		dataDescriptionForm.setParameter("dataType.name",attrib.getInputControlReportName());
		dataDescriptionForm.setParameter("dataType.label",attrib.getLabel());
		
		wResponse = dataDescriptionForm.submit(saveButton);
	
		String str = wResponse.getText();
		assertNotNull(" page text is Null ", str);
		
		if(str == null || str.trim().length()==0)
			fail("There is no text on the page");
		
		assertTrue(str.indexOf(repositoryPageText1)!= -1);
	}
	
	
	private String getJrxmlFileResourceURL(String jrxml) {
		//return resourcePath+File.separator+jrxml;
		return  getClass().getClassLoader().getResource(jrxml).getPath();
	}
	

	/**
	 * Form bean for Resource nameing
	 *	 
	 **/
	public class Description {
		private String fileName;
		private String fileLabel;
		private String fileDescription;
		private String fileType;

		public Description() {
			super();
		}

		public Description(String name,
		                   String label,
		                   String description,
		                   String type) {
			super();
			this.fileName        = name;
			this.fileLabel       = label;
			this.fileDescription = description;
			this.fileType        = type;
		}

		public String getFileDescription() {
			return fileDescription;
		}

		public void setFileDescription(String fileDescription) {
			this.fileDescription = fileDescription;
		}

		public String getFileLabel() {
			return fileLabel;
		}

		public void setFileLabel(String fileLabel) {
			this.fileLabel = fileLabel;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
	}
}
 