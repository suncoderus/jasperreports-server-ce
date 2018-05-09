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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;

import com.jaspersoft.jasperserver.export.BaseExportImportCommand.SpringResourceFactory;

import java.util.List;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;


import com.jaspersoft.jasperserver.export.util.CommandOut;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * @author achan
 *
 */
public class RemoveDuplicatedDisplayName implements CommandBean {
	
	private String rootUri = null;
	private RepositoryService repository;
	protected static final CommandOut commandOut = CommandOut.getInstance();
	private static OutputStreamWriter osw;
	private static CharacterEncodingProvider encodingProvider;	
	private static boolean updateRepo = false;

	
	
	public static void main(String[] args) {
	
		Parameters params = null;
		boolean success = false;
		try {
			



			
			GenericApplicationContext ctx = new GenericApplicationContext();
			XmlBeanDefinitionReader configReader = new XmlBeanDefinitionReader(ctx);
			List resourceXML = getPaths(args[2]);	
			if (args != null && args.length > 0) {
				for (int i = 0; i < resourceXML.size(); i++) {					
					org.springframework.core.io.Resource resource = classPathResourceFactory.create((String)resourceXML.get(i));
					configReader.loadBeanDefinitions(resource);			
				}
			}
			ctx.refresh();
			if (args.length > 3) {
				if ("UPDATE".equals(args[3])) {
					updateRepo = true;
				}
			}
			
            // write to file
			//
			
			try {
				CommandBean commandBean = (CommandBean) ctx.getBean("removeDuplicateDisplayName", CommandBean.class);
				Charset encoding = Charset.forName(((RemoveDuplicatedDisplayName)commandBean).getEncodingProvider().getCharacterEncoding()); 
				osw = new OutputStreamWriter(new FileOutputStream("remove_duplicated_display_name_report.txt"), encoding);
				commandBean.process(params);

			} finally {
				osw.close();
			}					
			success = true;
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		System.exit(success ? 0 : -1);
	}	
	
	
	
	public CharacterEncodingProvider getEncodingProvider() {
		return encodingProvider;
	}



	public void setEncodingProvider(CharacterEncodingProvider encodingProvider) {
		this.encodingProvider = encodingProvider;
	}



	private static List getPaths(String listOfXML) {
		
		ArrayList lst = new ArrayList();
		StringTokenizer str = new StringTokenizer(listOfXML, ",");
		while (str.hasMoreElements()) {
			lst.add(str.nextElement());
		}	
		return lst;
	}
	
	protected static final SpringResourceFactory classPathResourceFactory = new SpringResourceFactory() {
		public org.springframework.core.io.Resource create(String location) {
			commandOut.debug("Loading Spring configuration classpath resource " + location);
			return new ClassPathResource(location);
		}
	};	
	
	
	
	
	public RepositoryService getRepository() {
		return repository;
	}



	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}



	public RemoveDuplicatedDisplayName() {
		 this("/");
	}
	
	public RemoveDuplicatedDisplayName(String rtUri) {
		 this.rootUri = rtUri;
	     this.repository = new HibernateRepositoryServiceImpl();
	}

	public String getRootUri() {
		return rootUri;
	}

	public void setRootUri(String rootUri) {
		this.rootUri = rootUri;
	}
	
	public void process(Parameters param) {
//System.out.println("repo======" + repository);		
	   remove(rootUri);
	}
	
	public boolean remove(String parentFolderUri) {
        
		
		// get list of subfolders
		List subFolders = repository.getSubFolders(null, parentFolderUri);
		// clean up current directory first
		cleanUpDisplayNames(parentFolderUri, subFolders);
		
		// for each sub directory, clean up
		for (int i=0; i<subFolders.size(); i++) {
			remove(((Resource)subFolders.get(i)).getURIString());
		}

		return true;
	}
	
	private boolean cleanUpDisplayNames(String parentFolderUri, List subFolders) {
		
		// clean up resources
		// get list of resources	
		FilterCriteria criteria = FilterCriteria.createFilter();
		criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentFolderUri));
		List resources = repository.loadResourcesList(null, criteria); 
		List allObjects = new ArrayList();
		//allObjects.add(repository.getFolder(null, parentFolderUri));
		allObjects.addAll(subFolders);
		allObjects.addAll(resources);
		hashNames(allObjects);
		
		return true;
	}
	
	private boolean hashNames(List objects) {
		Hashtable ht = new Hashtable();
		for (int i=0; i<objects.size(); i++) {
			String curDisplayName = ((Resource)objects.get(i)).getLabel();
			if (!ht.containsKey(curDisplayName)) {
				ArrayList checkList = new ArrayList();
				checkList.add(objects.get(i));
				ht.put(curDisplayName, checkList);	
			} else {
				((List)(ht.get(curDisplayName))).add(objects.get(i));				
			}		
		}		
		updateDuplicateNames(ht);
		return true;
	}
	
	private boolean updateDuplicateNames(Hashtable ht) {
		Enumeration enu = ht.keys();
		while (enu.hasMoreElements()) {
			List valueList = (List)ht.get((String)enu.nextElement());
			if (valueList.size() > 1) {
				int curValue = 2;
				for (int i=1; i<valueList.size(); i++) {
					try {
						osw.write("\r\n");
						osw.write("Parent Folder: " + ((Resource)valueList.get(i)).getParentURI());
						osw.write("\r\n");
						osw.write("Modified Object Name(ID): " + ((Resource)valueList.get(i)).getURIString());
						osw.write("\r\n");
						osw.write("Modified Object Old Display Name: " + ((Resource)valueList.get(i)).getLabel());
						osw.write("\r\n");
					} catch (IOException e) {}
					
					// need to find 2, 3, 4.. etc 
					int newNumber = findUniqueDisplayNameNumber(ht, curValue, ((Resource)valueList.get(i)).getLabel());
					String newLabel = ((Resource)valueList.get(i)).getLabel() + " (" + newNumber + ")";		
					// update loaded values
				    ((Resource)valueList.get(i)).setLabel(newLabel);
					if (valueList.get(i) instanceof Folder) {
						if (updateRepo) {
						   repository.saveFolder(null, (Folder)valueList.get(i));
						}
						try {
							//osw.write("Modified Object New Display Name: " + ((Folder)valueList.get(i)).getLabel());
							osw.write("Modified Object New Display Name: " + newLabel);
							osw.write("\r\n");
							osw.write("Modified Object Type: Folder");
							osw.write("\r\n");	
							osw.write("\r\n");
						} catch (IOException e) {}
					} else {
                        // get the actual resource
						if (updateRepo) {
						   Resource res = repository.getResource(null, ((Resource)valueList.get(i)).getURIString());
						   res.setLabel(newLabel);
						   repository.saveResource(null, res);
						}
						try {
							//osw.write("Modified Object New Display Name: " + ((Resource)valueList.get(i)).getLabel());
							osw.write("Modified Object New Display Name: " + newLabel);
							osw.write("\r\n");
							osw.write("Modified Object Type: " + ((Resource)valueList.get(i)).getResourceType());
							osw.write("\r\n");
							osw.write("\r\n");
						} catch (IOException e) {}
					}
					curValue = newNumber + 1;
				}
			}
		}
		
		return true;
	}
	
	private int findUniqueDisplayNameNumber(Hashtable ht, int appValue, String name) {
		String returnDisplayName = name;
		int tempValue = appValue;		
	    while(ht.containsKey(returnDisplayName + " (" + tempValue + ")")) {	    	
	    	tempValue++;
	    }
	    
		return tempValue;
	}
	
	
}
