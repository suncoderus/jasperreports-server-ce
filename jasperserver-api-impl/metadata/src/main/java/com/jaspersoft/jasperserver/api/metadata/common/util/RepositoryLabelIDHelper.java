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

package com.jaspersoft.jasperserver.api.metadata.common.util;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElementDisjunction;

import java.util.List;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.LikeEscapeAwareExpression.ESCAPE_CHAR;
import static com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.LikeEscapeAwareExpression.escape;
import static com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria.createPropertyLikeFilter;
import static org.hibernate.criterion.MatchMode.START;
import static org.apache.commons.lang3.StringUtils.isEmpty;


/**
 * @author Alex Chan (achan@jaspersoft.com)
 * @version $Id: RepositoryLableIDHelper.java$
 *
 * */
public class RepositoryLabelIDHelper {

	protected static final Pattern RESOURCE_ID_INVALID_CHAR =
		Pattern.compile("[^\\p{L}\\p{N}]");

	protected static final String RESOURCE_ID_CHAR_REPLACEMENT = "_";
	
	/**
	 * @author Alex Chan (achan@jaspersoft.com)
	 * 
	 *
	 * @param repository repository service for accessing the repository
	 * @param parentFolder Full Parent Folder URI for the resource
	 * @param label The display label for the resource
	 * @return returns a generated ID converting non-alphanumberic characters into underscore, if the id already exists, _1, _2 so on and so forth will be appended until it's unique.
	 */
	public static String generateIdBasedOnLabel(RepositoryService repository, String parentFolder, String label)  {
		
		// validation
    	String inputLabel = ((label == null) ? "" : label.trim());
    	if ("".equals(inputLabel)) {
    		return "";
    	}

		// replace any non-alphanumeric characters into underscore
		String id = generateValidRepositoryIdByLabel(inputLabel);
    	
    	// get list of resource id(name) in the parent folder, so make sure the generated id(name) will be unique
		FilterCriteria criteria = FilterCriteria.createFilter();
		criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentFolder));
		FilterElementDisjunction disjunction = criteria.addDisjunction();
		disjunction.addFilterElement(createPropertyLikeFilter("name", escape(id, ESCAPE_CHAR), ESCAPE_CHAR, true));
		disjunction.addFilterElement(createPropertyLikeFilter("name", escape(id.concat("_"), ESCAPE_CHAR), START, ESCAPE_CHAR, true));
        List listOfResources = repository.loadResourcesList(null, criteria);    
        
        List repoFolderList = repository.getSubFolders(null, parentFolder);
        listOfResources.addAll(repoFolderList);
    	
        String newId = id;
        boolean doesInternalNameExist = true;
        int i = 0;
        // if the same generated id already exists, append _1, _2, so on and so forth until it's unique.
        while (doesInternalNameExist) {
        	doesInternalNameExist = false;
        	for (int j=0; j<listOfResources.size(); j++) {
            	String curInternalName = ((Resource)listOfResources.get(j)).getName();
            	if (curInternalName == null) {
            		curInternalName = "";
             	}
            	if (curInternalName.equalsIgnoreCase(newId)) {           		
            		doesInternalNameExist = true;
            		break;
            	}
        	} 
        	if (doesInternalNameExist) {
               i++;
        	   newId = id + "_" + i;
        	}
        }
    	return newId;
	}

	public static String generateValidRepositoryIdByLabel(String label) {
		if (isEmpty(label)) {
			return "";
		}
		// replace any non-alphanumeric characters into underscore
		return RESOURCE_ID_INVALID_CHAR.matcher(label).replaceAll(
				RESOURCE_ID_CHAR_REPLACEMENT);

	}
	
}
