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
package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.common.util.FTPService;
import com.jaspersoft.jasperserver.api.engine.common.util.impl.FTPUtil;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.FTPInfo;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockHandle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.util.TrustManagerUtils;
import org.springframework.dao.DataAccessResourceFailureException;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class ReportExecutionJobFileSavingImpl implements ReportExecutionJobFileSaving {

    private static final Log log = LogFactory.getLog(ReportExecutionJobFileSavingImpl.class);
    protected static final String LOCK_NAME_CONTENT_RESOURCE = "reportSchedulerOutput";
    private FTPService ftpService = new FTPUtil();
    private boolean enableSaveToHostFS;

    public FTPService getFtpService() {
        return ftpService;
    }

    public void setFtpService(FTPService ftpService) {
        this.ftpService = ftpService;
    }


    public void save(ReportExecutionJob job, ReportOutput output, boolean useFolderHierarchy, ReportJob jobDetails) {
        if (jobDetails.getContentRepositoryDestination() == null) return;
        try {
            saveToRepository(job, output, jobDetails);
        }  catch (Exception e) {
            job.handleException(job.getMessage("report.scheduling.error.saving.to.repository", new Object[]{output.getFilename()}), e);
        }

        if (!isSaveToDisk(jobDetails) && !isSaveToFTPServer(jobDetails)) return;

        Map<String, byte[]> zipOutput = null;
        try {
            zipOutput = zipFile(job, output, useFolderHierarchy);
        }  catch (Exception e) {
            job.handleException(job.getMessage("report.scheduling.error.creating.zip.output", new Object[]{output.getFilename()}), e);
        }
        for (Map.Entry<String, byte[]> entry : zipOutput.entrySet()) {
            try {
                if (isEnableSaveToHostFS())
                    saveToDisk(job, entry.getKey(), entry.getValue(), jobDetails);
                else
                    log.debug("skipped saveToDisk since isEnableSaveToHostFS="+isEnableSaveToHostFS());
            }  catch (Exception e) {
                job.handleException(job.getMessage("report.scheduling.error.writing.to.disk", new Object[]{output.getFilename()}), e);
            }
            try {
                saveToFTPServer(job, entry.getKey(), entry.getValue(), jobDetails);
            }  catch (Exception e) {
                job.handleException(job.getMessage("report.scheduling.error.updating.to.server", new Object[]{output.getFilename()}), e);
            }
        }


    }

    private String getSSHKeyData(ReportExecutionJob job, String uri) {
        String sshKeyData = null;
        try {
            // get file data
            sshKeyData = new String(job.getRepository().getResourceData(job.executionContext, uri).getData());

            // decode if encrypted
            sshKeyData = PasswordCipherer.getInstance().decodePassword(sshKeyData);
        } catch (JSResourceNotFoundException e) {
            log.error("Failed to read the SSH Private Key from repository.");
        } catch (DataAccessResourceFailureException e) {
            log.warn("Failed to decrypt the SSH Private Key. Most likely reason is unencrypted data in db.");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return sshKeyData;
    }


    protected void saveToRepository(ReportExecutionJob job, ReportOutput output, ReportJob jobDetails) {
        RepositoryService repositoryService = job.getRepository();
		ReportJobRepositoryDestination repositoryDestination = jobDetails.getContentRepositoryDestination();
		if (!repositoryDestination.isSaveToRepository()) return;
		List children = output.getChildren();
		List childResources = new ArrayList(children.size());
		for (Iterator it = children.iterator(); it.hasNext();) {
			ReportOutput childOutput = (ReportOutput) it.next();

			ContentResource childRes = (ContentResource) repositoryService.newResource(job.executionContext, ContentResource.class);;
			childRes.setName(childOutput.getFilename());
			childRes.setLabel(childOutput.getFilename());
			childRes.setFileType(childOutput.getFileType());
			childRes.setDataContainer(childOutput.getPersistenceData());
			childResources.add(childRes);
		}

		ContentResource contentRes = null;
        String folderURI = null;
        if (repositoryDestination.isUsingDefaultReportOutputFolderURI() && (repositoryDestination.getDefaultReportOutputFolderURI() != null))
            folderURI = repositoryDestination.getDefaultReportOutputFolderURI();
        else folderURI = repositoryDestination.getFolderURI();

		String resURI = folderURI + Folder.SEPARATOR + output.getFilename();
		LockHandle lock = lockOutputResource(job, resURI);
		try {
			Resource existingRes = repositoryService.getResource(job.executionContext, resURI);
			if (repositoryDestination.isOverwriteFiles()) {
				if (existingRes != null) {
					if (!(existingRes instanceof ContentResource)) {
						String quotedResURI = "\"" + resURI + "\"";
						throw new JSException("jsexception.report.no.content.resource", new Object[] {quotedResURI});
					}
					contentRes = (ContentResource) existingRes;
				}
			} else if (existingRes != null) {
				throw new JSException("jsexception.report.resource.already.exists.no.overwrite", new Object[] {resURI});
			}

			if (contentRes == null) {
				contentRes = (ContentResource) repositoryService.newResource(job.executionContext, ContentResource.class);
				contentRes.setName(output.getFilename());
				contentRes.setLabel(output.getFilename());
				contentRes.setParentFolder(repositoryDestination.getFolderURI());
			}

            contentRes.setDescription(jobDetails.getContentRepositoryDestination().getOutputDescription());
            contentRes.setFileType(output.getFileType());
			contentRes.setDataContainer(output.getPersistenceData());
			contentRes.setResources(childResources);

            if (job.executionContext.getAttributes() == null) {
                job.executionContext.setAttributes(new ArrayList());
            }
            job.executionContext.getAttributes().add(RepositoryService.IS_OVERWRITING);
			repositoryService.saveResource(job.executionContext, contentRes);
			output.setRepositoryPath(resURI);
		} finally {
			unlock(job, lock);
		}
    }

    protected boolean isSaveToDisk(ReportJob jobDetails) {
        ReportJobRepositoryDestination repositoryDestination = jobDetails.getContentRepositoryDestination();
        return ((repositoryDestination != null) && (repositoryDestination.getOutputLocalFolder() != null));
    }

    protected void saveToDisk(ReportExecutionJob job, String fileName, byte[] data, ReportJob jobDetails) {
        if (!isSaveToDisk(jobDetails)) return;
         ReportJobRepositoryDestination repositoryDestination = jobDetails.getContentRepositoryDestination();
        String path = repositoryDestination.getOutputLocalFolder() + File.separator +  fileName;

        try {
            if (!repositoryDestination.isOverwriteFiles() && (new File(path)).exists()) {
                throw new JSException("jsexception.report.resource.already.exists.no.overwrite", new Object[] {path});
            }
            File dirFile = new File(repositoryDestination.getOutputLocalFolder());
            if (!dirFile.exists()) {
                 throw new JSException("jsexception.report.resource.output.local.folder.does.not.exist", new Object[] {repositoryDestination.getOutputLocalFolder()});
            }
            FileOutputStream outputStream= new FileOutputStream(path, false);
            copy(data, outputStream);
        } catch (IOException ex) {
            job.handleException(job.getMessage("report.scheduling.error.exporting.report.to.disk", new Object[]{path}), ex);
        }
    }

    protected boolean isSaveToFTPServer(ReportJob jobDetails) {
        ReportJobRepositoryDestination repositoryDestination = jobDetails.getContentRepositoryDestination();
        return (repositoryDestination != null) && (repositoryDestination.getOutputFTPInfo() != null) &&
            (repositoryDestination.getOutputFTPInfo().getServerName() != null);
    }

    protected void saveToFTPServer(ReportExecutionJob job, String fileName, byte[] data, ReportJob jobDetails) {
        if (!isSaveToFTPServer(jobDetails)) return;
        FTPInfo ftpInfo =  jobDetails.getContentRepositoryDestination().getOutputFTPInfo();
        try {
            FTPService.FTPServiceClient ftpServiceClient = null;
            if (ftpInfo.getType().equals(FTPInfo.TYPE_FTP))
                ftpServiceClient = ftpService.connectFTP(ftpInfo.getServerName(), ftpInfo.getPort(), ftpInfo.getUserName(), ftpInfo.getPassword());
            else if (ftpInfo.getType().equals(FTPInfo.TYPE_FTPS)) {
                ftpServiceClient = ftpService.connectFTPS(ftpInfo.getServerName(), ftpInfo.getPort(), ftpInfo.getProtocol(), ftpInfo.isImplicit(), ftpInfo.getPbsz(), ftpInfo.getProt(), ftpInfo.getUserName(), ftpInfo.getPassword(),
                        false, TrustManagerUtils.getAcceptAllTrustManager());
            } else if (ftpInfo.getType().equals(FTPInfo.TYPE_SFTP)) {

                // Read SSH Private Key data from repo file resource
                String sshKeyData = null;
                if (ftpInfo.getSshKey() != null) {
                    sshKeyData = getSSHKeyData(job, ftpInfo.getSshKey());
                }

                ftpServiceClient = ftpService.connectSFTP(ftpInfo.getServerName(), ftpInfo.getPort(), ftpInfo.getUserName(), ftpInfo.getPassword(), null, sshKeyData, ftpInfo.getSshPassphrase());
            }
            ftpServiceClient.changeDirectory(ftpInfo.getFolderPath());
            if (!jobDetails.getContentRepositoryDestination().isOverwriteFiles() && ftpServiceClient.exists(fileName)) {
               throw new JSException("jsexception.report.resource.already.exists.no.overwrite", new Object[] {ftpInfo.getServerName() + ftpInfo.getFolderPath() + "/" + fileName});
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            ftpServiceClient.putFile(fileName, inputStream);
            ftpServiceClient.disconnect();
        } catch (Exception ex) {
            job.handleException(job.getMessage("report.scheduling.error.upload.to.ftp.server", new Object[]{ftpInfo.getServerName() + ftpInfo.getFolderPath() + "/" + fileName}), ex);
        }
    }

    protected String getChildrenFolderName(ReportExecutionJob job, String resourceName) {
		return job.getRepository().getChildrenFolderName(resourceName);
	}

    protected LockHandle lockOutputResource(ReportExecutionJob job, String uri) {
		return job.getLockManager().lock(LOCK_NAME_CONTENT_RESOURCE, uri);
	}

	protected void unlock(ReportExecutionJob job, LockHandle lock) {
		job.getLockManager().unlock(lock);
	}

    private void copy(byte[] data, FileOutputStream os) throws IOException {
      os.write(data);
      os.flush();
      os.close();
    }

    private Map<String, byte[]> zipFile(ReportExecutionJob job, ReportOutput output, boolean useFolderHierarchy) throws IOException {
        String attachmentName;
		DataContainer attachmentData;

        if (output.getChildren().isEmpty()) {
			attachmentName = output.getFilename();
			attachmentData = output.getData();
		} else {  // use zip format
			attachmentData = job.createDataContainer();
			boolean close = true;
			ZipOutputStream zipOut = new ZipOutputStream(attachmentData.getOutputStream());
			try {
				zipOut.putNextEntry(new ZipEntry(output.getFilename()));
				DataContainerStreamUtil.pipeDataAndCloseInput(output.getData().getInputStream(), zipOut);
				zipOut.closeEntry();

				for (Iterator it = output.getChildren().iterator(); it.hasNext();) {
					ReportOutput child = (ReportOutput) it.next();
                    String childName = child.getFilename();
					if (useFolderHierarchy) childName = getChildrenFolderName(job, output.getFilename()) + '/' + childName;
					zipOut.putNextEntry(new ZipEntry(childName));
					DataContainerStreamUtil.pipeDataAndCloseInput(child.getData().getInputStream(), zipOut);
					zipOut.closeEntry();
				}

				zipOut.finish();
				zipOut.flush();

				close = false;
				zipOut.close();
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						zipOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
			attachmentName = output.getFilename() + ".zip";
		}

		//read the data from attachment and put them into a result map
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream inputStream = attachmentData.getInputStream();
		byte[] attachmentBuffer = new byte[4096];
		int read = 0;
		while (read != -1) {
			read = inputStream.read(attachmentBuffer);
			if (read > 0) {
				baos.write(attachmentBuffer, 0, read);
			}
		}

		Map result = new HashMap<String, InputStream>();
		result.put(attachmentName, baos.toByteArray());
		return result;
    }

//    in case the admin decided to block jobs from writing to the file system
    public boolean isEnableSaveToHostFS() {
        return enableSaveToHostFS;
    }

    public void setEnableSaveToHostFS(boolean enableSaveToHostFS) {
        this.enableSaveToHostFS = enableSaveToHostFS;
    }
}
