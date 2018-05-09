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

package com.jaspersoft.jasperserver.dto.job.model;

import com.jaspersoft.jasperserver.dto.job.ClientJobFtpInfo;
import com.jaspersoft.jasperserver.dto.job.ClientJobRepositoryDestination;

/**
 * Contains attributes related inFolder the generation of repository resources
 * for thumbnail job output files.
 * Model is used in search/ update only.
 *
 * @author Tetiana Iefimenko
 * @version $Id$
 * @since 4.7
 */
public class ClientJobRepositoryDestinationModel extends ClientJobRepositoryDestination {

    private boolean isFolderURIModified = false;
    private boolean isSequentialFilenamesModified = false;
    private boolean isOverwriteFilesModified = false;
    private boolean isOutputDescriptionModified = false;
    private boolean isTimestampPatternModified = false;
    private boolean isSaveToRepositoryModified = false;
    private boolean isDefaultReportOutputFolderURIModified = false;
    private boolean isUsingDefaultReportOutputFolderURIModified = false;
    private boolean isOutputFTPInfoModified = false;
    private boolean isOutputLocalFolderModified = false;

    /**
     * Creates an empty object.
     */
    public ClientJobRepositoryDestinationModel() {
        super();
    }

    public ClientJobRepositoryDestinationModel(ClientJobRepositoryDestinationModel other) {
        super(other);
        this.isDefaultReportOutputFolderURIModified = other.isDefaultReportOutputFolderURIModified;
        this.isFolderURIModified = other.isFolderURIModified;
        this.isOutputDescriptionModified = other.isOutputDescriptionModified;
        this.isOutputFTPInfoModified = other.isOutputFTPInfoModified;
        this.isOutputLocalFolderModified = other.isOutputLocalFolderModified;
        this.isOverwriteFilesModified = other.isOverwriteFilesModified;
        this.isSaveToRepositoryModified = other.isSaveToRepositoryModified;
        this.isSequentialFilenamesModified = other.isSequentialFilenamesModified;
        this.isTimestampPatternModified = other.isTimestampPatternModified;
        this.isUsingDefaultReportOutputFolderURIModified = other.isUsingDefaultReportOutputFolderURIModified;
    }

    public boolean isDefaultReportOutputFolderURIModified() {
        return isDefaultReportOutputFolderURIModified;
    }

    public boolean isFolderURIModified() {
        return isFolderURIModified;
    }

    public boolean isOutputDescriptionModified() {
        return isOutputDescriptionModified;
    }

    public boolean isOutputFTPInfoModified() {
        return isOutputFTPInfoModified;
    }

    public boolean isOutputLocalFolderModified() {
        return isOutputLocalFolderModified;
    }

    public boolean isOverwriteFilesModified() {
        return isOverwriteFilesModified;
    }

    public boolean isSaveToRepositoryModified() {
        return isSaveToRepositoryModified;
    }

    public boolean isSequentialFilenamesModified() {
        return isSequentialFilenamesModified;
    }

    public boolean isTimestampPatternModified() {
        return isTimestampPatternModified;
    }

    public boolean isUsingDefaultReportOutputFolderURIModified() {
        return isUsingDefaultReportOutputFolderURIModified;
    }

    @Override
    public ClientJobRepositoryDestinationModel setFolderURI(String folderURI) {
        super.setFolderURI(folderURI);
        isFolderURIModified = true;
        return this;
    }

    @Override
    public ClientJobRepositoryDestinationModel setSequentialFilenames(Boolean sequentialFilenames) {
        super.setSequentialFilenames(sequentialFilenames);
        isSequentialFilenamesModified = true;
        return this;
    }

    @Override
    public ClientJobRepositoryDestinationModel setOverwriteFiles(Boolean overwriteFiles) {
        super.setOverwriteFiles(overwriteFiles);
        isOverwriteFilesModified = true;
        return this;
    }

    @Override
    public ClientJobRepositoryDestinationModel setOutputDescription(String outputDescription) {
        super.setOutputDescription(outputDescription);
        isOutputDescriptionModified = true;
        return this;
    }

    @Override
    public ClientJobRepositoryDestinationModel setTimestampPattern(String timestampPattern) {
        super.setTimestampPattern(timestampPattern);
        isTimestampPatternModified = true;
        return this;
    }

    @Override
    public ClientJobRepositoryDestinationModel setSaveToRepository(Boolean saveToRepository) {
        super.setSaveToRepository(saveToRepository);
        isSaveToRepositoryModified = true;
        return this;
    }

    @Override
    public ClientJobRepositoryDestinationModel setDefaultReportOutputFolderURI(String defaultReportOutputFolderURI) {
        super.setDefaultReportOutputFolderURI(defaultReportOutputFolderURI);
        isDefaultReportOutputFolderURIModified = true;
        return this;
    }

    @Override
    public ClientJobRepositoryDestinationModel setUsingDefaultReportOutputFolderURI(Boolean usingDefaultReportOutputFolderURI) {
        super.setUsingDefaultReportOutputFolderURI(usingDefaultReportOutputFolderURI);
        isUsingDefaultReportOutputFolderURIModified = true;
        return this;
    }

    @Override
    public ClientJobRepositoryDestinationModel setOutputFTPInfo(ClientJobFtpInfo outputFTPInfo) {
        super.setOutputFTPInfo(outputFTPInfo);
        isOutputFTPInfoModified = true;
        return this;
    }

    public ClientJobRepositoryDestinationModel setOutputFTPInfoModel(ClientJobFTPInfoModel outputFTPInfo) {
        this.setOutputFTPInfo(outputFTPInfo);
        return this;
    }

    @Override
    public ClientJobRepositoryDestinationModel setOutputLocalFolder(String outputLocalFolder) {
        super.setOutputLocalFolder(outputLocalFolder);
        isOutputLocalFolderModified = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobRepositoryDestinationModel)) return false;
        if (!super.equals(o)) return false;

        ClientJobRepositoryDestinationModel that = (ClientJobRepositoryDestinationModel) o;

        if (isFolderURIModified() != that.isFolderURIModified()) return false;
        if (isSequentialFilenamesModified() != that.isSequentialFilenamesModified()) return false;
        if (isOverwriteFilesModified() != that.isOverwriteFilesModified()) return false;
        if (isOutputDescriptionModified() != that.isOutputDescriptionModified()) return false;
        if (isTimestampPatternModified() != that.isTimestampPatternModified()) return false;
        if (isSaveToRepositoryModified() != that.isSaveToRepositoryModified()) return false;
        if (isDefaultReportOutputFolderURIModified() != that.isDefaultReportOutputFolderURIModified()) return false;
        if (isUsingDefaultReportOutputFolderURIModified() != that.isUsingDefaultReportOutputFolderURIModified())
            return false;
        if (isOutputFTPInfoModified() != that.isOutputFTPInfoModified()) return false;
        return isOutputLocalFolderModified() == that.isOutputLocalFolderModified();

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isFolderURIModified() ? 1 : 0);
        result = 31 * result + (isSequentialFilenamesModified() ? 1 : 0);
        result = 31 * result + (isOverwriteFilesModified() ? 1 : 0);
        result = 31 * result + (isOutputDescriptionModified() ? 1 : 0);
        result = 31 * result + (isTimestampPatternModified() ? 1 : 0);
        result = 31 * result + (isSaveToRepositoryModified() ? 1 : 0);
        result = 31 * result + (isDefaultReportOutputFolderURIModified() ? 1 : 0);
        result = 31 * result + (isUsingDefaultReportOutputFolderURIModified() ? 1 : 0);
        result = 31 * result + (isOutputFTPInfoModified() ? 1 : 0);
        result = 31 * result + (isOutputLocalFolderModified() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientJobRepositoryDestinationModel{" +
                "isDefaultReportOutputFolderURIModified=" + isDefaultReportOutputFolderURIModified +
                ", isFolderURIModified=" + isFolderURIModified +
                ", isSequentialFilenamesModified=" + isSequentialFilenamesModified +
                ", isOverwriteFilesModified=" + isOverwriteFilesModified +
                ", isOutputDescriptionModified=" + isOutputDescriptionModified +
                ", isTimestampPatternModified=" + isTimestampPatternModified +
                ", isSaveToRepositoryModified=" + isSaveToRepositoryModified +
                ", isUsingDefaultReportOutputFolderURIModified=" + isUsingDefaultReportOutputFolderURIModified +
                ", isOutputFTPInfoModified=" + isOutputFTPInfoModified +
                ", isOutputLocalFolderModified=" + isOutputLocalFolderModified +
                '}';
    }

    @Override
    public ClientJobRepositoryDestinationModel deepClone() {
        return new ClientJobRepositoryDestinationModel(this);
    }
}
