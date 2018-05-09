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
package com.jaspersoft.jasperserver.war.common;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
//import org.springframework.web.multipart.cos.CosMultipartResolver;

/**
 * @deprecated replaced by {@link com.jaspersoft.jasperserver.war.MultipartRequestWrapperFilter}
 */
public class UploadMultipartFilter implements Filter{  

	   public void doFilter(ServletRequest request, 
	                        ServletResponse response, 
	                        FilterChain chain)
	                throws IOException, ServletException {
	                    
	      HttpServletRequest hRequest = (HttpServletRequest)request;
	      //Check whether we're dealing with a multipart request
	      MultipartResolver resolver= new CommonsMultipartResolver();
              
	      // Giulio: If the getContentLength is -1, avoid to consider this
              // message like a multipart request
	      if(resolver.isMultipart(hRequest) && hRequest.getContentLength() != -1){
	      	
	      	  MultipartHttpServletRequest mreq = resolver.resolveMultipart(hRequest);
                  
                  if(mreq!=null){
	    		  Iterator iterator=mreq.getFileNames();
	    		  String fieldName=null;
	    		  while(iterator.hasNext()){
	    			  fieldName=(String)iterator.next();
	    			  // Assuming only 1 file is uploaded per page
	    			  // can be modified to handle mulit uploads per request
	    		  }
	    		  MultipartFile file=mreq.getFile(fieldName);
	    		  if(file!=null){
	    			  String fullName = file.getOriginalFilename();
	    			  if(fullName!=null && fullName.trim().length()!=0){
	    				  int lastIndex = fullName.lastIndexOf(".");
			              if(lastIndex!=-1){
			            	  String fileName=fullName.substring(0,lastIndex);
			                  String extension=fullName.substring(lastIndex+1);
			                  mreq.setAttribute(JasperServerConst.UPLOADED_FILE_NAME,fileName);
			                  mreq.setAttribute(JasperServerConst.UPLOADED_FILE_EXT,extension);  
			              }   
	    			  }
		          }
	    	  }
              chain.doFilter(mreq,response);
	      }else	    	  	    
               
	        chain.doFilter(request,response);
	   }

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub	
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
 
