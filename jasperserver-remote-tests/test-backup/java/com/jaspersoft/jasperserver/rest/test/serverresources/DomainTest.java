package com.jaspersoft.jasperserver.rest.test.serverresources;

import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.LOCAL_RESOURCE_DESCRIPTOR_PATH;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.RESOURCE;

import java.io.File;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.junit.Assert;
import org.junit.Test;

import com.jaspersoft.jasperserver.rest.test.RESTTest;
import com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts;

public class DomainTest extends RESTTest {
	@Test  
	public void PUT_200_Resource_DomainDataComplexSource() throws Exception{

		putSampleFolder();
		
		String domainURI="/JUNIT_NEW_FOLDER/supermartDomain";
		
		String schemaURI="/JUNIT_NEW_FOLDER/supermartDomain_files/supermartDomain_domain_schema";
		String propertiesURI="/JUNIT_NEW_FOLDER/supermartDomain_files/supermart_domain.properties";
		String propertiesEnURI="/JUNIT_NEW_FOLDER/supermartDomain_files/supermart_domain_en_US.properties";
		String securityFileURI="/JUNIT_NEW_FOLDER/supermartDomain_files/supermartDomain_domain_security";
		String JNDIURI="/analysis/datasources/SugarCRMDataSourceJNDI";
		
		String schemaBinFilePath = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/domain/supermartDomain_domain_schema.xml";
		String propertiesBin = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/domain/supermart_domain.properties";
		String propertiesEnBin = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/domain/supermart_domain_en_US.properties";
		String securityFileBin = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/domain/supermartDomain_domain_security.xml";
		 
		
		String fileRd = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/domain/supermartDomain_DataSource.xml";
		FileBody bin;
		
    	//building the body
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		//appending the file descriptor from a file 
		reqEntity.addPart(REQUEST_PARAMENTER_RD, new StringBody(RESTTestUtilsAndConsts.getResourceDescriptor(new File(fileRd))));
		
		//appending the binaries to the request body
		bin = new FileBody(new File(schemaBinFilePath));
		reqEntity.addPart(schemaURI, bin );
		
		//appending the binaries to the request body
		bin = new FileBody(new File(propertiesBin));
		reqEntity.addPart(propertiesURI, bin );
		
		//appending the binaries to the request body
		bin = new FileBody(new File(propertiesEnBin));
		reqEntity.addPart(propertiesEnURI, bin );
		
		//appending the binaries to the request body
		bin = new FileBody(new File(securityFileBin));
		reqEntity.addPart(securityFileURI, bin );
		
		((HttpEntityEnclosingRequestBase)httpReqPro).setEntity(reqEntity);
		
		//executing the request
		httpRes = sendRequestPRO(httpReqPro, RESOURCE+"/JUNIT_NEW_FOLDER");
		Assert.assertTrue("basic response check did not pass", isValidResposnse(201));
		
//		deleteSampleFolder();
		
	}

}
