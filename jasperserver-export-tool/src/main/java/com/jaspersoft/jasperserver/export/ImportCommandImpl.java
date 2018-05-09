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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.dto.common.BrokenDependenciesStrategy;
import com.jaspersoft.jasperserver.export.io.ExportImportIOFactory;
import com.jaspersoft.jasperserver.export.io.ImportInput;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.ImportFailedException;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Set;

import static com.jaspersoft.jasperserver.export.service.ImportExportService.ERROR_CODE_IMPORT_ROOT_INTO_ORGANIZATION;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ImportCommandImpl implements CommandBean, ApplicationContextAware {
	
	private static final CommandOut commandOut = CommandOut.getInstance();
	private static final PrintStream COMMAND_OUTPUT = System.out;
	private static final InputStream COMMAND_INPUT = System.in;

	private static final String ERROR_CODE_BROKEN_DEPENDENCIES_OPTIONS = "import.broken.dependencies.options";
	private static final String ERROR_CODE_ORGANIZATIONS_NOT_MATCH_QUESTION = "import.organizations.not.match.question.message";

	private ApplicationContext ctx;
	
	private ExportImportIOFactory exportImportIOFactory;
	private String importerPrototypeBeanName;

	private MessageSource messageSource;

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
	
	public void process(Parameters parameters) {
		try {
			ImportTask task = createTask(parameters);
			Importer importer = createPrototypeImporter(parameters);
			importer.setTask(task);

			if (checkImportTaskParameters(task)) {
				importer.performImport();
			}
		} catch (ImportFailedException e) {
			commandOut.error(e.getMessage(), e);
		}
	}

	private boolean checkImportTaskParameters(ImportTask task) {
		Parameters parameters = task.getParameters();
		Locale locale = task.getExecutionContext().getLocale();
		ImportInputMetadata fileMetadata = task.getInputMetadata();
		//Root Tenant from import input
		String sourceTenantId = fileMetadata.getProperty(ImportExportService.ROOT_TENANT_ID);
		//Tenant id we import into
		String destinationTenantId = parameters.getParameterValue(ImportExportServiceImpl.ORGANIZATION);
        //TODO: Thinking About removing this checkes
		if (StringUtils.isNotBlank(destinationTenantId) && !destinationTenantId.equals(TenantService.ORGANIZATIONS)) {
			if (StringUtils.isBlank(sourceTenantId) || sourceTenantId.equals(TenantService.ORGANIZATIONS)) {
				throw new JSException(localize(ERROR_CODE_IMPORT_ROOT_INTO_ORGANIZATION, locale));
			}

			// it is tenant merging // check for tenant ids
			if (!sourceTenantId.equals(destinationTenantId)) {
				if (!parameters.hasParameter(ImportExportService.MERGE_ORGANIZATION)) {
					String message = localize(
							ERROR_CODE_ORGANIZATIONS_NOT_MATCH_QUESTION,
							new String[]{destinationTenantId},
							locale);
					if (!checkUserResponseIfTenantsNotMatch(task, message)) {
						return false;
					}
				}
			}
		} else if (StringUtils.isNotBlank(sourceTenantId) && !sourceTenantId.equals(TenantService.ORGANIZATIONS)) {
			//TODO: Handle exceptions properly
			throw new JSException(localize(ImportExportService.ERROR_CODE_IMPORT_ORGANIZATION_INTO_ROOT, locale));
		}

		BrokenDependenciesStrategy dependenciesStrategy =
				BrokenDependenciesStrategy.parseString(
						parameters.getParameterValue(ImportExportService.BROKEN_DEPENDENCIES));

		// check for broken dependencies
		Set<String> brokenDependencies = fileMetadata.getBrokenDependencies();
		if (brokenDependencies != null && !brokenDependencies.isEmpty()) {
			if (BrokenDependenciesStrategy.FAIL.equals(dependenciesStrategy)) {
				if (!checkUserResponseIfBrokenDependencies(task, brokenDependencies)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean checkUserResponseIfTenantsNotMatch(ImportTask task, String message) {
		COMMAND_OUTPUT.println(message);
		String answer = getUserAnswer();
		if ("y".equalsIgnoreCase(answer == null ? "" : answer.trim())) {
			task.getParameters().addParameter(ImportExportService.MERGE_ORGANIZATION);
			return true;
		}
		return false;
	}

	private boolean checkUserResponseIfBrokenDependencies(ImportTask task, Set<String> brokenDependencies) {
		Parameters parameters = task.getParameters();
		Locale locale = task.getExecutionContext().getLocale();

		COMMAND_OUTPUT.println(localize(ImportExportService.ERROR_CODE_IMPORT_BROKEN_DEPENDENCIES, locale));
		for (String dep : brokenDependencies) {
			COMMAND_OUTPUT.println(dep);
		}
		COMMAND_OUTPUT.println(localize(ERROR_CODE_BROKEN_DEPENDENCIES_OPTIONS, locale));
		String answer = getUserAnswer();
		if ("s".equalsIgnoreCase(answer == null ? "" : answer.trim())) {
			parameters.setParameterValue(ImportExportService.BROKEN_DEPENDENCIES,
					BrokenDependenciesStrategy.SKIP.getLabel());
			return true;
		} else if ("i".equalsIgnoreCase(answer == null ? "" : answer.trim())) {
			parameters.setParameterValue(ImportExportService.BROKEN_DEPENDENCIES,
					BrokenDependenciesStrategy.INCLUDE.getLabel());
			return true;
		}
		return false;
	}

	private String getUserAnswer() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(COMMAND_INPUT));

		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			commandOut.error("Error occurred in input stream: ", e);
		}

		return line;
	}

	protected ImportTask createTask(Parameters parameters) {
		ImportTaskImpl task = new ImportTaskImpl();
		task.setParameters(parameters);
		task.setExecutionContext(getExecutionContext(parameters));
		task.setInput(getImportInput(parameters));
        task.setApplicationContext(this.ctx);
		return task;
	}

	protected ImportInput getImportInput(Parameters parameters) {
		return getExportImportIOFactory().createInput(parameters);
	}

	protected ExecutionContext getExecutionContext(Parameters parameters) {
		ExecutionContextImpl context = new ExecutionContextImpl();
		context.setLocale(getLocale(parameters));
        if(ObjectPermissionService.PRIVILEGED_OPERATION.equals(parameters.getParameterValue(ObjectPermissionService.PRIVILEGED_OPERATION))){
            context.getAttributes().add(ObjectPermissionService.PRIVILEGED_OPERATION);
        }
		return context;
	}

	protected Locale getLocale(Parameters parameters) {
		return LocaleContextHolder.getLocale();
	}

	protected Importer createPrototypeImporter(Parameters parameters) {
		String importerBeanName = getImporterPrototypeBeanName(parameters);
		
		commandOut.debug("Using " + importerBeanName + " importer prototype bean.");
		
		return (Importer) ctx.getBean(importerBeanName, Importer.class);
	}

	protected String getImporterPrototypeBeanName(Parameters parameters) {
		return getImporterPrototypeBeanName();
	}

	public String getImporterPrototypeBeanName() {
		return importerPrototypeBeanName;
	}

	public void setImporterPrototypeBeanName(String importerPrototypeBeanName) {
		this.importerPrototypeBeanName = importerPrototypeBeanName;
	}

	public ExportImportIOFactory getExportImportIOFactory() {
		return exportImportIOFactory;
	}

	public void setExportImportIOFactory(ExportImportIOFactory exportImportIOFactory) {
		this.exportImportIOFactory = exportImportIOFactory;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private String localize(String key, Locale locale){
		return messageSource.getMessage(key, null, key, locale);
	}

	private String localize(String key, Object[] args, Locale locale){
		return messageSource.getMessage(key, args, key, locale);
	}
}
