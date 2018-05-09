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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;

import java.util.List;

/**
 * A ReportUnit is a Resource object in the JasperServer repository which contains the JRXML
 * representing a JasperReport, a {@link com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource ReportDataSource} object
 * that it uses to obtain a JRDataSource, and any other resources needed to run the report on JasperServer.
 * Possible resources can include:
 * 
 * <ul>
 * <li>{@link InputControl} instances associated with report parameters
 * <li>A {@link com.jaspersoft.jasperserver.api.metadata.common.domain.Query Query} repository resource 
 * <li>Resource bundles to provide localized strings used in the JRXML
 * <li>An optional scriptlet
 * </ul>
 * 
 * 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */


@JasperServerAPI
public interface ReportUnit extends Resource, ResourceContainer, InputControlsContainer {

    public static final byte LAYOUT_POPUP_SCREEN = 1;
    public static final byte LAYOUT_SEPARATE_PAGE = 2;
    public static final byte LAYOUT_TOP_OF_PAGE = 3;
    public static final byte LAYOUT_IN_PAGE = 4;

	/**
	 * Returns the reference to the
	 * {@link com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource data source}
	 * used by this report unit.
	 *
	 * @return a reference to the data source used by this report unit
	 */
	public ResourceReference getDataSource();

	/**
	 * Set the 
	 * {@link ReportDataSource} used by this report unit using a {@link ResourceReference}.
	 *
	 * @param dataSourceReference a reference to a data source
	 */
	public void setDataSource(ResourceReference dataSourceReference);

	/**
	 * Set the 
	 * {@link ReportDataSource} used by this report unit.
	 *
	 * @param dataSource a repository data source
	 */
	public void setDataSource(ReportDataSource dataSource);

	/**
	 * Set the 
	 * {@link ReportDataSource} used by this report unit using the URI of a ReportDataSource in the repository.
	 *
	 * @param referenceURI the URI of a ReportDataSource in the repository
	 */
	public void setDataSourceReference(String referenceURI);

	/**
	 * Returns the reference to the
	 * {@link com.jaspersoft.jasperserver.api.metadata.common.domain.Query query}
	 * used by this report unit.
	 *
	 * @return a reference to the query used by this report unit
	 */
	public ResourceReference getQuery();

	/**
	 * Set the 
	 *
	 * @param queryReference a reference to a Query
	 */
	public void setQuery(ResourceReference queryReference);


    /**
	 * Return an {@link InputControl} associated with the parameter name
	 * @param name name of a parameter in the report
	 * @return
	 */
	public InputControl getInputControl(String name);

    /**
	 * add an input control to the report unit
	 * @param inputControl
	 */
	public void addInputControl(InputControl inputControl);

	/**
	 * add a reference to an input control to the report unit
	 * @param inputControlReference
	 */
	public void addInputControl(ResourceReference inputControlReference);

	/**
	 * add an {@link InputControl} to the report unit using its URI
	 * @param referenceURI
	 */
	public void addInputControlReference(String referenceURI);

	/**
	 * Remove the input control at the given index.
	 * @param index index of the input control to be removed
	 * @return removed input control
	 */
	public ResourceReference removeInputControl(int index);

	/**
	 * remove an {@link InputControl} to the report unit using its URI
	 * @param referenceURI
	 */
	public boolean removeInputControlReference(String referenceURI);

	/**
	 * remove a local {@link InputControl} resource from the report unit using its name
	 * @param name
	 * @return removed input control
	 */
	public InputControl removeInputControlLocal(String name);

	/**
	 * Returns the reference to the {@link FileResource JRXML resource}
	 * used by this report unit as master report
	 *
	 * @return a reference to the master report of this report unit
	 */
	public ResourceReference getMainReport();

	/**
	 * Sets the master report for this report unit using a reference to the {@link FileResource JRXML resource}
	 *
	 * @param reportReference a reference to a FileResource containing the JRXML to be used as the main report
	 */
	public void setMainReport(ResourceReference reportReference);

	/**
	 * Sets the master report for this report unit using a {@link FileResource}
	 *
	 * @param report a FileResource containing the JRXML to be used as the main report
	 */
	public void setMainReport(FileResource report);

	/**
	 * Sets the master report for this report unit using a repository URI
	 *
	 * @param referenceURI the repository URI of a JRXML FileResource 
	 */
	public void setMainReportReference(String referenceURI);

	/**
	 * Gets a list of {@link ResourceReference} objects for each of the resources contained by the ReportUnit
	 * @return list of report unit resources
	 */
	public List getResources();

	/**
	 * get the local resource with the given name
	 * @param name name of the resource within the report unit
	 * @return a local resource
	 */
	public FileResource getResourceLocal(String name);

	/**
	 * set the list of resources belonging to this report unit. The list should
	 * contain ResourceReference objects referring to the actual resources.
	 * @param resources list of ResourceReferences
	 */
	public void setResources(List resources);

	/**
	 * add a FileResource to the report unit
	 * @param resource resource to be added
	 */
	public void addResource(FileResource resource);

	/**
	 * add a FileResource to the report unit as a ResourceReference
	 * @param resourceReference reference to a FileResource in the repository
	 */
	public void addResource(ResourceReference resourceReference);

	/**
	 * add a FileResource to the report unit using its URI within the repository
	 * @param referenceURI URI of a FileResource in the repository
	 */
	public void addResourceReference(String referenceURI);

	/**
	 * Remove the resource at the given index.
	 * @param index index of the resource to be removed
	 * @return removed resource
	 */
	public ResourceReference removeResource(int index);

	/**
	 * remove a local {@link FileResource} from the report unit using its name
	 * @param name name of resource within report unit
	 * @return removed resource
	 */
	public FileResource removeResourceLocal(String name);

	/**
	 * remove a {@link FileResource} reference from the report unit using its URI
	 * @param referenceURI repository URI of resource
	 * @return removed resource
	 */
	public boolean removeResourceReference(String referenceURI);

	/**
	 * Set the name of a JSP in JasperServer to be used to display input controls instead 
	 * of the default JSP (DefaultParameters.jsp).
	 * @param viewName path of a JSP within the JasperServer webapp
	 */
	public void setInputControlRenderingView(String viewName);
	/**
	 * Get the name of a JSP in JasperServer to be used to display input controls instead 
	 * of the default JSP (DefaultParameters.jsp).
	 * @return path of a JSP within the JasperServer webapp, or null if the default will be used
	 */
	public String getInputControlRenderingView();

	/**
	 * Set the name of a JSP in JasperServer to be used to display the report instead 
	 * of the default JSP.
	 * @param viewName path of a JSP within the JasperServer webapp
	 */
	public void setReportRenderingView(String viewName);
	/**
	 * Get the name of a JSP in JasperServer to be used to display the report instead 
	 * of the default JSP.
	 * @return path of a JSP within the JasperServer webapp
	 */
	public String getReportRenderingView();

	/**
	 * Set whether input controls will be shown before the report is displayed
	 * @param alwaysPromptControls if true, input controls will be popped up before the report is run
	 */
    public void setAlwaysPromptControls(boolean alwaysPromptControls);
	/**
	 * Get whether input controls will be shown before the report is displayed
	 * @return if true, input controls will be popped up before the report is run
	 */
    public boolean isAlwaysPromptControls();

    /**
     * Set the desired positioning of input controls when the report is run; available values are:
     * <dl>
     * <dt>ReportUnit.LAYOUT_POPUP_SCREEN (1)
     * <dd>Input controls are in a popup
     * <dt>ReportUnit.LAYOUT_SEPARATE_PAGE (2)
     * <dd>Input controls are shown by themselves before the report is displayed
     * <dt>ReportUnit.LAYOUT_TOP_OF_PAGE (3)
     * <dd>Input controls are shown at the top of the report page
     * <dt>ReportUnit.LAYOUT_IN_PAGE (4)
     * <dd>Input controls are shown in left part of the report page
     * </dl>
     * @param controlsLayout value for input control layout
     */
    public void setControlsLayout(byte controlsLayout);
    /**
     * get the desired positioning of input controls
     * @return value for input control layout
     */
    public byte getControlsLayout();

	/**
	 * Validate the components of the ReportUnit and return a ValidationResult containing
	 * a list of ValidationDetail objects
	 * @return result of validation process
	 */
	public ValidationResult validate();
	
	/**
	 * Replace the input control matching referenceURI with a reference to a new InputControl
	 * @param referenceURI URI of the old input control reference
	 * @param inputControlReference reference to the new input control
	 */
	public void replaceInputControlReference(String referenceURI, ResourceReference inputControlReference);

	/**
	 * Replace the input control matching referenceURI with a reference to a new InputControl
	 * @param referenceURI URI of the old input control reference
	 * @param newReferenceURI URI of the new input control
	 */
	public void replaceInputControlReference(String referenceURI, String newReferenceURI);

	/**
	 * Replace the input control matching referenceURI with a new InputControl
	 * @param referenceURI URI of the old input control reference
	 * @param inputControl the new input control
	 */
	public void replaceInputControlReference(String referenceURI, InputControl inputControl);

	/**
	 * Replace the named local input control with a reference to a new InputControl
	 * @param name name of the old input control
	 * @param inputControlReference reference to the new input control
	 */
	public void replaceInputControlLocal(String name, ResourceReference inputControlReference);

	/**
	 * Replace the named local input control with a reference to a new InputControl
	 * @param name name of the old input control
	 * @param newReferenceURI URI of the new input control
	 */
	public void replaceInputControlLocal(String name, String newReferenceURI);

	/**
	 * Replace the named local input control with a new InputControl
	 * @param name name of the old input control
	 * @param inputControl the new input control
	 */
	public void replaceInputControlLocal(String name, InputControl inputControl);
	
	public Long getDataSnapshotId();
	
	public void setDataSnapshotId(Long id);

}
