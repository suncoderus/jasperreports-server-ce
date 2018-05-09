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

package com.jaspersoft.jasperserver.api.security.externalAuth.db;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalUserDetailsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class loads authorities and other details from an external database.
 *
 * @author Dmitriy Litvak
 * Extends {@link JdbcDaoImpl}
 */
public class ExternalJDBCUserDetailsService extends JdbcDaoImpl implements ExternalUserDetailsService {
	public static final Logger logger = LogManager.getLogger(ExternalJDBCUserDetailsService.class);

	private String detailsQuery;
	private UserDetailsQueryMapping userDetailsQueryMapping;
	private Integer numberDetailsQueryParams;

	public String getDetailsQuery() {
		return detailsQuery;
	}

	public void setDetailsQuery(String detailsQuery) {
		this.detailsQuery = detailsQuery != null ? detailsQuery.trim().toLowerCase() : detailsQuery;
	}

	public ExternalJDBCUserDetailsService() {
		super();
	}


	protected void initDao() throws ApplicationContextException {
		super.initDao();

		if (detailsQuery != null) {
			logger.debug("User details query configured in xml: " + detailsQuery);
			numberDetailsQueryParams = StringUtils.countOccurrencesOf(detailsQuery, "?");
			userDetailsQueryMapping = new UserDetailsQueryMapping(getDataSource());
		}

	}

    /**
     * {@link JdbcDaoImpl}
     */
	@Override
	public List<GrantedAuthority> loadAuthoritiesByUsername(String username) throws DataAccessException {
		logger.debug("Loading external roles via JDBC.");
		return loadUserAuthorities(username);
	}

	@Override
	public List<Map<String, Object>> loadDetails(String... params) throws DataAccessException {
		if (userDetailsQueryMapping == null) {
			logger.debug("User details query is NOT configured in app context xml.");
			return Collections.emptyList();
		}

		//TODO test that numberDetailsQueryParams in app context config matches params.length (unit/integration tests)
		if (params.length != numberDetailsQueryParams) {
			final JSException jsException = new JSException("Either " + getClass() + " was mis-configured in the application context xml or " +
					"the code implementation has not been adjusted for the query");
		    logger.error(jsException);
			throw jsException;
		}

		logger.debug("Loading external user details via JDBC.");
		List<Map<String, Object>> userDetails = userDetailsQueryMapping.execute(params);

		final int udSize = userDetails.size();
		logger.debug("Loaded " + udSize + " details.");
		if (udSize == 0)
			return Collections.emptyList();

		return Collections.unmodifiableList(userDetails);
	}

	/**
	 * Query object to extract additional user details.
	 */
	private class UserDetailsQueryMapping extends MappingSqlQuery<Map<String, Object>> {
		protected UserDetailsQueryMapping(DataSource ds) {
			super(ds, detailsQuery);

			if (numberDetailsQueryParams == null)
				numberDetailsQueryParams = StringUtils.countOccurrencesOf(detailsQuery, "?");
			for (int k = 0; k < numberDetailsQueryParams; ++k)
				declareParameter(new SqlParameter(Types.VARCHAR));
			compile();

			if (logger.isDebugEnabled())
				logger.debug("detailsQuery: " + detailsQuery);
		}

		protected Map<String, Object> mapRow(ResultSet rs, int rownum) throws SQLException {
			final Map<String, Object> returnDetailsMap = new HashMap<String, Object>();

			ResultSetMetaData metaData = rs.getMetaData();
			int numbColumns = metaData.getColumnCount();

			if (logger.isDebugEnabled())
				logger.debug("row: numb columns returned- " + numbColumns);

			for (int c=1; c <= numbColumns; ++c) {
				String retColumnName = metaData.getColumnLabel(c);
				retColumnName = retColumnName == null ? metaData.getColumnName(c) : retColumnName;
				retColumnName = retColumnName.toLowerCase();
				returnDetailsMap.put(retColumnName, rs.getObject(c));

				if (logger.isDebugEnabled())
					logger.debug("column returned: name- " + retColumnName + ", val- " + returnDetailsMap.get(retColumnName));
			}

			return Collections.unmodifiableMap(returnDetailsMap);
		}
	}

}
