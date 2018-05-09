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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.QueryParameterDescriptor;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoReportDataSource;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 * 
 * @hibernate.joined-subclass table="JSQuery"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoQuery extends RepoResource {
    private static final ObjectMapper mapper = new ObjectMapper();

	// defined in RepoQuery.hbm.xml
	private static final int MAX_QUERY_TEXT = 3600;
	private RepoResource dataSource = null;
	private String language;
	private String sql;
    private String parameters;


	/**
	 * @hibernate.many-to-one
	 * 		column="reportDataSource"
	 */
	public RepoResource getDataSource()
	{
		return dataSource;
	}
	
	/**
	 * 
	 */
	public void setDataSource(RepoResource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	/**
	 * @hibernate.property
	 * 		column="sql_query" type="string" length="2000" not-null="true"
	 */
	public String getSql() {
		return sql;
	}


	public void setSql(String sql) {
		this.sql = sql;
	}

	protected Class getClientItf() {
		return Query.class;
	}


	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		
		Query query = (Query) clientRes;
		copyDataSource(referenceResolver, query);
		setLanguage(query.getLanguage());
		String queryText = query.getSql();
		if (queryText.length() > MAX_QUERY_TEXT) {
    		byte[] compressed;
			try {
				compressed = compress(queryText);
			} catch (IOException e) {
				throw new RuntimeException("unexpected i/o exception on compression", e);
			}
    		queryText = new BASE64Encoder().encode(compressed);
		}
		setSql(queryText);

        try {
            if (query.getParameters() != null){
                setParameters(mapper.writeValueAsString(new ParametersContainer(query.getParameters())));
            }
        } catch (IOException e) {
            throw new JSException(e.getMessage());
        }

    }

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		
		Query query = (Query) clientRes;
		query.setDataSource(getClientReference(getDataSource(), resourceFactory));
		query.setLanguage(getLanguage());
		String queryText = getSql();
		// try uncompressing, in case it's compressed
		// if it's not it will fail silently
    	try {
    		// normalize line breaks
    		String normalizedBuffer = queryText.replaceAll("\\s+", System.getProperty("line.separator"));
    		byte[] gzipped = new BASE64Decoder().decodeBuffer(normalizedBuffer);
    		queryText = uncompress(gzipped);
    	} catch (Exception e) {
    	}
		query.setSql(queryText);

        if (parameters != null) {
            try {
                query.setParameters(mapper.readValue(parameters, ParametersContainer.class).getParams());
            } catch (IOException e) {
                throw new JSException(e.getMessage());
            }
        }
    }


	public static String uncompress(byte[] compressed) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
		GZIPInputStream gzis = new GZIPInputStream(bais);
		BufferedReader br = new BufferedReader(new InputStreamReader(gzis));
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}

	public static byte[] compress(String string) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		OutputStreamWriter osw = new OutputStreamWriter(gzos);
		osw.write(string);
		osw.flush();
		osw.close();
		return baos.toByteArray();
	}

	private void copyDataSource(ReferenceResolver referenceResolver, Query query) {
		ResourceReference ds = query.getDataSource();
		if (ds != null) {
			RepoResource repoDS = getReference(ds, RepoReportDataSource.class, referenceResolver);
			if (repoDS != null && !(repoDS instanceof RepoReportDataSource)) {
				throw new JSException("jsexception.query.datasource.has.an.invalid.type", new Object[] {repoDS.getClass().getName()});
			}
			setDataSource(repoDS);
		} else {
			setDataSource(null);
		}
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    private static class ParametersContainer{
        private List<QueryParameterDescriptor> params;

        ParametersContainer() { }

        ParametersContainer(List<QueryParameterDescriptor> params){
            this.params = params;
        }

        public List<QueryParameterDescriptor> getParams() {
            return params;
        }

        public void setParams(List<QueryParameterDescriptor> params) {
            this.params = params;
        }
    }

}
