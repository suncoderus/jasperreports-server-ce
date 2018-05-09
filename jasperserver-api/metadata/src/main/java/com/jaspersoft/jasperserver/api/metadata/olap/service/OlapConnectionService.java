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
package com.jaspersoft.jasperserver.api.metadata.olap.service;

import javax.servlet.http.HttpSession;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

import com.tonbeller.jpivot.olap.model.OlapModel;
import mondrian.olap.Util;
import org.olap4j.OlapConnection;

/**
 * Olap connection management
 *
 * @author sbirney
 *
 */
@JasperServerAPI
public interface OlapConnectionService {

    /**
     * A newly constructed and configured OlapModel based on the given OLAPUnit.
     * initialize is not yet called
     * 
     * @param context JasperServer execution context
     * @param olapUnit JasperServer OLAP analysis view metadata
     * @return OlapModel JPivot OLAP model for use in display from OlapUnit metadata
     */
    public OlapModel createOlapModel( ExecutionContext context,
				      OlapUnit olapUnit );


    /**
     * Obtain an Olap4j OlapConnection to the specified Resource.
     *
     * @param context   JasperServer execution context
     * @param resourceName  
     * @return OlapConnection
     */
	  public OlapConnection getOlapConnection(ExecutionContext context,
                                            String resourceName);

    /**
     * Obtain an Olap4j OlapConnection to an unsaved Resource.
	 * @param context JasperServer execution context
	 * @param conn the actual resource
	 * @return OlapConnection
	 */
	  public OlapConnection getOlapConnection(ExecutionContext context, OlapClientConnection conn);  
      
    /**
     * test an XMLA connection and report back the error
     * @param context JasperServer execution context
     * @param xmlaConnection the actual resource
     * @return test result with proper error code, error message, and valid options
     */
	  public XMLATestResult testConnection(ExecutionContext context, XMLAConnection xmlaConnection);


    /**
     *
     * @param context JasperServer execution context
     * @param olapUnit JasperServer OLAP analysis view metadata
     * @param sess HTTP user session
     * @return OlapModel initialized JasperJPivot model
     */
    public OlapModel initializeOlapModel(ExecutionContext context, OlapUnit olapUnit, HttpSession sess);

    /**
     * Define a PropertyList that can be used to create a connection to Mondrian based on
     * a repository object
     *
     * @param context JasperServer execution context
     * @param conn MondrianConnection defining connection
     * @return Util.PropertyList Mondrian PropertyList to define a connection to Mondrian
     */
    public Util.PropertyList getMondrianConnectProperties( ExecutionContext context,
							   MondrianConnection conn );

    /**
     * Allow override of setting of connection properties. In default implementations,
     * getMondrianConnectProperties( ExecutionContext context, MondrianConnection conn )
     * calls this method with a null ReportDataSource.
     * 
     * @param context
     * @param conn MondrianConnection defining connection
     * @param dataSource JDBC or JNDI data source from repository
     * @return Util.PropertyList Mondrian PropertyList to define a connection to Mondrian
     * @see getMondrianConnectProperties( ExecutionContext context, MondrianConnection conn )
     */
    public Util.PropertyList getMondrianConnectProperties(ExecutionContext context,
                        MondrianConnection conn,
			ReportDataSource dataSource);

    /**
     * Validate OLAPUnit including MDX and schema
     *
     * @param context
     * @param unit
     * @return validationResult
     */
    public ValidationResult validate(ExecutionContext context,
				     OlapUnit unit);

    /**
     * Validate OlapUnit components as a group.
     *
     * @param context
     * @param unit OlapUnit
     * @param schema Mondrian schema
     * @param conn Olap Client Connection
     * @param dataSource report data source (metadata)
     * @return validationResult
     */
    public ValidationResult validate(ExecutionContext context,
				     OlapUnit unit,
				     FileResource schema,
				     OlapClientConnection conn,
				     ReportDataSource dataSource);
    
    /**
     * Save resource to repository
     *
     * @param context
     * @param path folder URI to save resource to
     * @param resource resource object to save
     */
    public void saveResource( ExecutionContext context,
			      String path,
			      Resource resource );

    /**
     * Get the resource pointed to by the reference from the repository
     *
     * @param context
     * @param ref Resource reference
     * @return resource
     */
    public Resource dereference( ExecutionContext context,
				 ResourceReference ref );

    /**
     * Get the contents of the given file resource
     *
     * @param context
     * @param file File resource
     * @return file contents
     */
    public String getFileResourceData(ExecutionContext context,
				      FileResource file);

}
