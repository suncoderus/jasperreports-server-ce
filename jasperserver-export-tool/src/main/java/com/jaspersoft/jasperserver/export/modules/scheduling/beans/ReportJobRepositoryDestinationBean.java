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
package com.jaspersoft.jasperserver.export.modules.scheduling.beans;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.FTPInfo;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;


/**
 * @author tkavanagh
 * @version $Id$
 */
public class ReportJobRepositoryDestinationBean {

	private long id;
	private int version;
	private String folderURI;
	private boolean sequentialFilenames;
	private boolean overwriteFiles;
	private String outputDescription;
	private String timestampPattern;
    private boolean saveToRepository = true;
    private String defaultReportOutputFolderURI;
    private boolean usingDefaultReportOutputFolderURI = false;
    private String outputLocalFolder = null;
    private FTPInfoBean outputFTPInfo = null;

	public void copyFrom(ReportJobRepositoryDestination dest) {
		setId(dest.getId());
		setVersion(dest.getVersion());
		setFolderURI(dest.getFolderURI());
		setSequentialFilenames(dest.isSequentialFilenames());
		setOverwriteFiles(dest.isOverwriteFiles());
		setOutputDescription(dest.getOutputDescription());
		setTimestampPattern(dest.getTimestampPattern());
        setSaveToRepository(dest.isSaveToRepository());
        setDefaultReportOutputFolderURI(dest.getDefaultReportOutputFolderURI());
        setUsingDefaultReportOutputFolderURI(dest.isUsingDefaultReportOutputFolderURI());
        setOutputLocalFolder(dest.getOutputLocalFolder());
        setOutputFTPInfo(copyFTPInfoFrom(dest));
	}

	public void copyTo(ReportJobRepositoryDestination dest, ImporterModuleContext importContext) {
		dest.setFolderURI(getFolderURI());
		dest.setSequentialFilenames(isSequentialFilenames());
		dest.setOverwriteFiles(isOverwriteFiles());
		dest.setOutputDescription(getOutputDescription());
		dest.setTimestampPattern(getTimestampPattern());
        dest.setSaveToRepository(isSaveToRepository());
        dest.setDefaultReportOutputFolderURI(getDefaultReportOutputFolderURI());
        dest.setUsingDefaultReportOutputFolderURI(isUsingDefaultReportOutputFolderURI());
        dest.setOutputLocalFolder(getOutputLocalFolder());
        dest.setOutputFTPInfo(copyFTPInfoTo());

		if (!importContext.getNewGeneratedTenantIds().isEmpty()) {
			dest.setFolderURI(TenantStrHolderPattern.TENANT_FOLDER_URI
					.replaceWithNewTenantIds(importContext.getNewGeneratedTenantIds(), dest.getFolderURI()));
		}
	}

	protected FTPInfoBean copyFTPInfoFrom(ReportJobRepositoryDestination dest) {
		FTPInfoBean ftpInfoBean;
		FTPInfo ftpInfo = dest.getOutputFTPInfo();
		if (ftpInfo == null) {
			ftpInfoBean = null;
		} else {
			ftpInfoBean = new FTPInfoBean();
			ftpInfoBean.copyFrom(ftpInfo);
		}
		return ftpInfoBean;
	}

	protected FTPInfo copyFTPInfoTo() {
		FTPInfo ftpInfo;
		if (outputFTPInfo == null) {
			ftpInfo = null;
		} else {
			ftpInfo = new FTPInfo();
			outputFTPInfo.copyTo(ftpInfo);
		}
		return ftpInfo;
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
	
	public void setFolderURI(String folderURI) {
		this.folderURI = folderURI;
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

    public boolean isSaveToRepository() {
        return saveToRepository;
    }

    public void setSaveToRepository(boolean saveToRepository) {
        this.saveToRepository = saveToRepository;
    }

    public String getDefaultReportOutputFolderURI() {
        return defaultReportOutputFolderURI;
    }

    public void setDefaultReportOutputFolderURI(String defaultReportOutputFolderURI) {
        this.defaultReportOutputFolderURI = defaultReportOutputFolderURI;
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

    public FTPInfoBean getOutputFTPInfo() {
        return outputFTPInfo;
    }

    public void setOutputFTPInfo(FTPInfoBean outputFTPInfo) {
        this.outputFTPInfo = outputFTPInfo;
    }
}
