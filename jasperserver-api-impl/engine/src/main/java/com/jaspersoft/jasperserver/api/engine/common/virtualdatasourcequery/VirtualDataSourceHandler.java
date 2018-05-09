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

package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.ConnectionFactory;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.VirtualDataSourceQueryService;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JdbcDataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JndiDataSource;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl.CustomDataSourceImpl;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl.JdbcDataSourceImpl;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl.JndiDataSourceImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceAcessDeniedException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomJdbcReportDataSourceProvider;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.springframework.security.access.AccessDeniedException;

import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class VirtualDataSourceHandler {

    RepositoryService repositoryService;
    VirtualDataSourceQueryService virtualDataSourceQueryService;
    private ProfileAttributesResolver profileAttributesResolver;
    private CustomReportDataSourceServiceFactory customReportDataSourceServiceFactory;
    private static String dataSourceSchemaSeparator = "_";

    /*
     * get the separator between data source id and schema name
     */
    public static String getDataSourceSchemaSeparator() {
        return dataSourceSchemaSeparator;
    }

    /*
     * set the separator between data source id and schema name
     */
    public void setDataSourceSchemaSeparator(String dataSourceSchemaSeparator) {
        this.dataSourceSchemaSeparator = dataSourceSchemaSeparator;
    }

    /*
     * get a handle of virtual data source query service
     */
    public VirtualDataSourceQueryService getVirtualDataSourceQueryService() {
        return virtualDataSourceQueryService;
    }


    /*
     * a hook to replace teiid with other virtual data source service (like JDBCUnity)
     * set it through spring injection
     */
    public void setVirtualDataSourceQueryService(VirtualDataSourceQueryService virtualDataSourceQueryService) {
        this.virtualDataSourceQueryService = virtualDataSourceQueryService;
    }

    /*
     * get a handle of repository service
     */
    public RepositoryService getRepositoryService()	{
		return repositoryService;
	}

     /*
     * set repository service
     */
	public void setRepositoryService(RepositoryService repository) {
		this.repositoryService = repository;
	}

    public void setProfileAttributesResolver(ProfileAttributesResolver profileAttributesResolver) {
        this.profileAttributesResolver = profileAttributesResolver;
    }

    public void setCustomReportDataSourceServiceFactory(CustomReportDataSourceServiceFactory customReportDataSourceServiceFactory) {
        this.customReportDataSourceServiceFactory = customReportDataSourceServiceFactory;
    }

    /*
    * generate sql data source for virtual data source
    */
    public javax.sql.DataSource getSqlDataSource(ExecutionContext context, VirtualReportDataSource jsDataSource) throws Exception {
        Collection<DataSource> subDataSourceList = new ArrayList<DataSource>();

        for (Map.Entry<String, ResourceReference> entry : jsDataSource.getDataSourceUriMap().entrySet()) {
            try {
                Object reportDataSource = getResource(context, entry.getValue());
                // create sub data source list
                if (reportDataSource instanceof  ReportDataSource) {
                    reportDataSource = resolveReportDataSourceAttributes((ReportDataSource)reportDataSource);
                }
                if (reportDataSource instanceof JdbcReportDataSource) {
                    JdbcDataSource jdbcDataSource = new JdbcDataSourceImpl((JdbcReportDataSource) reportDataSource, findSchemas(jsDataSource.getSchemas(), entry.getKey()), entry.getKey(),
                            jsDataSource);
                    subDataSourceList.add(jdbcDataSource);
                }
                if (reportDataSource instanceof JndiJdbcReportDataSource) {
                    JndiDataSource jndiDataSource = new JndiDataSourceImpl((JndiJdbcReportDataSource) reportDataSource, findSchemas(jsDataSource.getSchemas(), entry.getKey()), entry.getKey(),
                            jsDataSource);
                    subDataSourceList.add(jndiDataSource);
                }
                if (reportDataSource instanceof CustomReportDataSource) {
                    subDataSourceList.add(createVDSSubDataSourceFromCustomReportDataSource((CustomReportDataSource) reportDataSource,
                            findSchemas(jsDataSource.getSchemas(), entry.getKey()), entry.getKey(), jsDataSource));
                }

            } catch (AccessDeniedException accessDeniedEx) {
                throw new JSResourceAcessDeniedException(entry.getValue(), "Data Source Access Denied: " + entry.getValue().getReferenceURI());
            }
        }
        // get the connection factory using virtual data source query service
        ConnectionFactory connectionFactory = virtualDataSourceQueryService.getConnectionFactory(subDataSourceList, jsDataSource.getURIString());
        // create javax.sql.DataSource from connection factory
        VirtualSQLDataSource virtualSQLDataSource = new VirtualSQLDataSource(connectionFactory);
        return virtualSQLDataSource;
    }

    public  DataSource createVDSSubDataSourceFromCustomReportDataSource(CustomReportDataSource customReportDataSource, Set<String> schemas, String dataSourceName, VirtualReportDataSource parentDataSource) {

        CustomDataSourceDefinition dsDef = customReportDataSourceServiceFactory.getDefinition(customReportDataSource);
		// does it have its own factory? if so, delegate to it
		if ((dsDef.getCustomFactory() != null) && (dsDef.getCustomFactory() instanceof CustomJdbcReportDataSourceProvider))
        {
			ReportDataSource wrappedReportDataSource = ((CustomJdbcReportDataSourceProvider) dsDef.getCustomFactory()).getWrappedReportDataSource(customReportDataSource);
            if (wrappedReportDataSource instanceof  JdbcReportDataSource) {
                return new JdbcDataSourceImpl((JdbcReportDataSource)wrappedReportDataSource, schemas, dataSourceName, parentDataSource);
            } else if (wrappedReportDataSource instanceof VirtualReportDataSource) {
                // return custom data source impl instead.  Join in VDS in parent level
            }
		}
        return new CustomDataSourceImpl(customReportDataSource, schemas,  dataSourceName, parentDataSource);
    }


    //We should additionally resolve attributes for Report Data Source there because connection service for it is not
    // handled via implementation of ReportDataSourceServiceFactory.createService(which is intercepted by ProfileAttributesResolverAspect).
    private ReportDataSource resolveReportDataSourceAttributes(ReportDataSource reportDataSource) {
        return profileAttributesResolver.mergeResource(reportDataSource);
    }

    private Object getResource(ExecutionContext context, ResourceReference resourceReference) {
        if (resourceReference.isLocal()) return resourceReference.getLocalResource();
         return repositoryService.getResource(context, resourceReference.getReferenceURI());
    }

    /*
     * get the schema list for specific sub data source
     */
    private Set<String> findSchemas(Set<String> virtualSchemaList, String dataSourceName) {
        if (virtualSchemaList == null) return null;
        Set<String> schemaList = new LinkedHashSet<String>();
        String schemaPrefix = (dataSourceName + getDataSourceSchemaSeparator()).toLowerCase();
        int beginIndex = schemaPrefix.length();
        // loop through all the schema in virtual data source
        for (String schemaName : virtualSchemaList) {
            // only search for schema with specific data source id
            if (schemaName.toLowerCase().startsWith(schemaPrefix)) schemaList.add(schemaName.substring(beginIndex));
        }
        return schemaList;
    }


}
