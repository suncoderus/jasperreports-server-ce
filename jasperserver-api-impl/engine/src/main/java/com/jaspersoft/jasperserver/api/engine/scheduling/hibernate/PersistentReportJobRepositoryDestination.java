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
package com.jaspersoft.jasperserver.api.engine.scheduling.hibernate;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.FTPInfoModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobRepositoryDestinationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

import java.util.Iterator;
import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PersistentReportJobRepositoryDestination {
	
	private long id;
	private int version;
	private String folderURI;
	private boolean sequentialFilenames;
	private boolean overwriteFiles;
	private String outputDescription;
	private String timestampPattern;
    private boolean saveToRepository = true;
    private static String DEFAULT_REPORT_OUTPUT_FOLDER_URI = "DEFAULT_REPORT_OUTPUT_FOLDER_URI";
    private boolean usingDefaultReportOutputFolderURI = true;
    private String outputLocalFolder = null;
    private PersistentFTPInfo outputFTPInfo = null;

	public PersistentReportJobRepositoryDestination() {
		version = ReportJob.VERSION_NEW;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getFolderURI() {
		return folderURI;
	}

	public void setFolderURI(String folder) {
		this.folderURI = folder;
	}
	
	public boolean isSequentialFilenames() {
		return sequentialFilenames;
	}

	public void setSequentialFilenames(boolean sequentialFilenames) {
		this.sequentialFilenames = sequentialFilenames;
	}

	public boolean isOverwriteFiles() {
		return overwriteFiles;
	}

	public void setOverwriteFiles(boolean overwriteFiles) {
		this.overwriteFiles = overwriteFiles;
	}

    public boolean isSaveToRepository() {
		return saveToRepository;
	}

	public void setSaveToRepository(boolean saveToRepository) {
		this.saveToRepository = saveToRepository;
	}

	public boolean isUsingDefaultReportOutputFolderURI() {
		return usingDefaultReportOutputFolderURI;
	}

	public void setUsingDefaultReportOutputFolderURI(boolean usingDefaultReportOutputFolderURI) {
		this.usingDefaultReportOutputFolderURI = usingDefaultReportOutputFolderURI;
	}

    public String getOutputLocalFolder() {
        return outputLocalFolder;
    }

    public void setOutputLocalFolder(String outputLocalFolder) {
        this.outputLocalFolder = outputLocalFolder;
    }

    public PersistentFTPInfo getOutputFTPInfo() {
        return outputFTPInfo;
    }

    public void setOutputFTPInfo(PersistentFTPInfo outputFTPInfo) {
        this.outputFTPInfo = outputFTPInfo;
    }

	public void copyFrom(ReportJobRepositoryDestination repositoryDestination, List unusedEntities, ProfileAttributeService profileAttributeService,
                         RepoUser user, ExecutionContext context, HibernateRepositoryService referenceResolver) {
		setFolderURI(repositoryDestination.getFolderURI());
		setSequentialFilenames(repositoryDestination.isSequentialFilenames());
		setOverwriteFiles(repositoryDestination.isOverwriteFiles());
		setOutputDescription(repositoryDestination.getOutputDescription());
		setTimestampPattern(repositoryDestination.getTimestampPattern());
        setSaveToRepository(repositoryDestination.isSaveToRepository());
        if (repositoryDestination.getDefaultReportOutputFolderURI() != null)
            setDefaultScheduledReportOutputFolderURI(repositoryDestination.getDefaultReportOutputFolderURI(), profileAttributeService, user, context);
        setUsingDefaultReportOutputFolderURI(repositoryDestination.isUsingDefaultReportOutputFolderURI());
        setOutputLocalFolder(repositoryDestination.getOutputLocalFolder());
        copyFTPInfo(repositoryDestination, referenceResolver);

	}

    public void copyFrom(ReportJobRepositoryDestinationModel repositoryDestination, List unusedEntities, ProfileAttributeService profileAttributeService,
                         RepoUser user, ExecutionContext context, HibernateRepositoryService referenceResolver) {
		if (repositoryDestination.isFolderURIModified()) setFolderURI(repositoryDestination.getFolderURI());
		if (repositoryDestination.isSequentialFilenames()) setSequentialFilenames(repositoryDestination.isSequentialFilenames());
		if (repositoryDestination.isOverwriteFilesModified()) setOverwriteFiles(repositoryDestination.isOverwriteFiles());
		if (repositoryDestination.isOutputDescriptionsModified()) setOutputDescription(repositoryDestination.getOutputDescription());
		if (repositoryDestination.isTimestampPatternModified()) setTimestampPattern(repositoryDestination.getTimestampPattern());
        if (repositoryDestination.isSaveToRepositoryModified()) setSaveToRepository(repositoryDestination.isSaveToRepository());
        if (repositoryDestination.isDefaultReportOutputFolderURIModified() && repositoryDestination.getDefaultReportOutputFolderURI() != null)
            setDefaultScheduledReportOutputFolderURI(repositoryDestination.getDefaultReportOutputFolderURI(), profileAttributeService, user, context);
        if (repositoryDestination.isUsingDefaultReportOutputFolderURIModified())
            setUsingDefaultReportOutputFolderURI(repositoryDestination.isUsingDefaultReportOutputFolderURI());
        if (repositoryDestination.isOutputLocalFolderModified()) setOutputLocalFolder(repositoryDestination.getOutputLocalFolder());
        if (repositoryDestination.isOutputFTPInfoModified()) copyFTPInfo(repositoryDestination.getOutputFTPInfoModel(), referenceResolver);
	}

	public ReportJobRepositoryDestination toClient(ProfileAttributeService profileAttributeService, RepoUser user, ExecutionContext context) {
		ReportJobRepositoryDestination repositoryDestination = new ReportJobRepositoryDestination();
		repositoryDestination.setId(getId());
		repositoryDestination.setVersion(getVersion());
		repositoryDestination.setFolderURI(getFolderURI());
		repositoryDestination.setSequentialFilenames(isSequentialFilenames());
		repositoryDestination.setOverwriteFiles(isOverwriteFiles());
		repositoryDestination.setOutputDescription(getOutputDescription());
		repositoryDestination.setTimestampPattern(getTimestampPattern());
        repositoryDestination.setSaveToRepository(isSaveToRepository());
        repositoryDestination.setDefaultReportOutputFolderURI(getDefaultScheduledReportOutputFolderURI(profileAttributeService, user, context));
        repositoryDestination.setUsingDefaultReportOutputFolderURI(isUsingDefaultReportOutputFolderURI());
        repositoryDestination.setOutputLocalFolder(getOutputLocalFolder());
        if (getOutputFTPInfo() != null) repositoryDestination.setOutputFTPInfo(getOutputFTPInfo().toClient());
		return repositoryDestination;
	}

    public String getDefaultScheduledReportOutputFolderURI(ProfileAttributeService profileAttributeService, RepoUser user, ExecutionContext context) {
        List attrList = profileAttributeService.getProfileAttributesForPrincipal( context, user);
        if (attrList == null) return null;
        for (Iterator it = attrList.iterator(); !attrList.isEmpty() && it.hasNext();) {
		    ProfileAttribute elem = (ProfileAttribute) it.next();
		    if (DEFAULT_REPORT_OUTPUT_FOLDER_URI.equals(elem.getAttrName())) {
                return elem.getAttrValue();
            }
		}
        return null;
    }

    public void setDefaultScheduledReportOutputFolderURI(String folderURI, ProfileAttributeService profileAttributeService, RepoUser user, ExecutionContext context) {
        String originalValue = getDefaultScheduledReportOutputFolderURI(profileAttributeService, user, context);
        if ((originalValue == null) && (folderURI == null)) return;
        if ((originalValue != null) && originalValue.equalsIgnoreCase(folderURI)) return;
        ProfileAttribute attr = profileAttributeService.newProfileAttribute( context );
	    attr.setPrincipal(user);
	    attr.setAttrName(DEFAULT_REPORT_OUTPUT_FOLDER_URI);
	    attr.setAttrValue(folderURI);
        profileAttributeService.putProfileAttribute(context, attr);
    }

	protected void copyFTPInfo(ReportJobRepositoryDestination destination, HibernateRepositoryService referenceResolver) {
		if (getOutputFTPInfo() == null) {
            setOutputFTPInfo(new PersistentFTPInfo());
            if (destination.getOutputFTPInfo() == null) return;
        }
        if (destination.getOutputFTPInfo() != null) getOutputFTPInfo().copyFrom(destination.getOutputFTPInfo(), referenceResolver);
        else  setOutputFTPInfo(new PersistentFTPInfo());
	}

    protected void copyFTPInfo(FTPInfoModel jobFTPInfo, HibernateRepositoryService referenceResolver) {
        if (getOutputFTPInfo()== null) {
            setOutputFTPInfo(new PersistentFTPInfo());
            if (jobFTPInfo == null) return;
        }
        if (jobFTPInfo != null) getOutputFTPInfo().copyFrom(jobFTPInfo);
        else setOutputFTPInfo(new PersistentFTPInfo());
    }

	public boolean isNew() {
		return getVersion() == ReportJob.VERSION_NEW;
	}

	public String getOutputDescription() {
		return outputDescription;
	}

	public void setOutputDescription(String outputDescription) {
		this.outputDescription = outputDescription;
	}

	public String getTimestampPattern() {
		return timestampPattern;
	}

	public void setTimestampPattern(String timestampPattern) {
		this.timestampPattern = timestampPattern;
	}

}
