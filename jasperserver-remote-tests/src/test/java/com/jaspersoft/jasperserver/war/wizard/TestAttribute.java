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

import java.util.Map;


public class TestAttribute {
	public static String    DSTYPE_JDBC     = "JDBC Data Source";
	public static String    DSTYPE_JNDI     = "JNDI Data Source";
	private String          jrxml;
	private Map             jrmlResources;
	private String          publishFolder;
	private DataSourceAttrb dataSourceAttrb;
	private String          reportName;
	private String          dataSourceReportName;
	private String          dataTypeReportName;
	private String          jrxmlReportName;
	private String          label;
	private String          serviceName;
	private String			inputControlReportName;

	/**
	 * @return Returns the label.
	 **/
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param label The label to set.
	 **/
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}	
	
	
	/**
	 * @return Returns the label.
	 **/
	public String getLabel() {
		return label;
	}

	/**
	 * @param label The label to set.
	 **/
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return Returns the reportName.
	 **/
	public String getJRXMLReportName() {
		return jrxmlReportName;
	}

	/**
	 * @param reportName The reportName to set.
	 **/
	public void setJRXMLReportName(String jrxmlReportName) {
		this.jrxmlReportName = jrxmlReportName;
	}
	
	
	
	/**
	 * @return Returns the reportName.
	 **/
	public String getDataSourceReportName() {
		return dataSourceReportName;
	}

	/**
	 * @param reportName The reportName to set.
	 **/
	public void setDataSourceReportName(String dataSourceReportName) {
		this.dataSourceReportName = dataSourceReportName;
	}
	
	/**
	 * @return Returns the reportName.
	 **/
	public String getDataTypeReportName() {
		return dataTypeReportName;
	}

	/**
	 * @param reportName The reportName to set.
	 **/
	public void setDataTypeReportName(String dataTypeReportName) {
		this.dataTypeReportName = dataTypeReportName;
	}
	
	/**
	 * @return Returns the reportName.
	 **/
	public String getReportName() {
		return reportName;
	}

	/**
	 * @param reportName The reportName to set.
	 **/
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * @return Returns the dataSourceAttrb.
	 **/
	public DataSourceAttrb getDataSourceAttrb() {
		return dataSourceAttrb;
	}

	/**
	 * @param dataSourceAttrb The dataSourceAttrb to set.
	 **/
	public void setDataSourceAttrb(DataSourceAttrb dataSourceAttrb) {
		this.dataSourceAttrb = dataSourceAttrb;
	}

	/**
	 * @return Returns the jrmlResources.
	 **/
	public Map getJrmlResources() {
		return jrmlResources;
	}

	/**
	 * @param jrmlResources The jrmlResources to set.
	 **/
	public void setJrmlResources(Map jrmlResources) {
		this.jrmlResources = jrmlResources;
	}

	/**
	 * @return Returns the jrxml.
	 **/
	public String getJrxml() {
		return jrxml;
	}

	/**
	 * @param jrxml The jrxml to set.
	 **/
	public void setJrxml(String jrxml) {
		this.jrxml = jrxml;
	}

	/**
	 * @return Returns the publishFolder.
	 **/
	public String getPublishFolder() {
		return publishFolder;
	}

	/**
	 * @param publishFolder The publishFolder to set.
	 **/
	public void setPublishFolder(String publishFolder) {
		this.publishFolder = publishFolder;
	}

	public class DataSourceAttrb {
		private String dataSourceType;
		private String name;
		private String label;
		private String driver;
		private String url;
		private String username;
		private String password;
		private String serviceName;

		/**
		 * @return Returns the dataSourceType.
		 **/
		public String getDataSourceType() {
			return dataSourceType;
		}

		/**
		 * @param dataSourceType The dataSourceType to set.
		 **/
		public void setDataSourceType(String dataSourceType) {
			this.dataSourceType = dataSourceType;
		}

		/**
		 * @return Returns the driver.
		 **/
		public String getDriver() {
			return driver;
		}

		/**
		 * @param driver The driver to set.
		 **/
		public void setDriver(String driver) {
			this.driver = driver;
		}

		/**
		 * @return Returns the label.
		 **/
		public String getLabel() {
			return label;
		}

		/**
		 * @param label The label to set.
		 **/
		public void setLabel(String label) {
			this.label = label;
		}

		/**
		 * @return Returns the name.
		 **/
		public String getName() {
			return name;
		}

		/**
		 * @param name The name to set.
		 **/
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return Returns the password.
		 **/
		public String getPassword() {
			return password;
		}

		/**
		 * @param password The password to set.
		 **/
		public void setPassword(String password) {
			this.password = password;
		}

		/**
		 * @return Returns the serviceName.
		 **/
		public String getServiceName() {
			return serviceName;
		}

		/**
		 * @param serviceName The serviceName to set.
		 **/
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		/**
		 * @return Returns the url.
		 **/
		public String getUrl() {
			return url;
		}

		/**
		 * @param url The url to set.
		 **/
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * @return Returns the username.
		 **/
		public String getUsername() {
			return username;
		}

		/**
		 * @param username The username to set.
		 **/
		public void setUsername(String username) {
			this.username = username;
		}
	}

	public String getInputControlReportName() {
		return inputControlReportName;
	}

	public void setInputControlReportName(String inputControlReportName) {
		this.inputControlReportName = inputControlReportName;
	}
}
