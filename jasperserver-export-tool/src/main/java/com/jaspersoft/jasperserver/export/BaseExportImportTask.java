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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class BaseExportImportTask {

	private Parameters parameters;
	private ExecutionContext executionContext;
	private List<WarningDescriptor> warnings;

	public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private ApplicationContext applicationContext;

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	public List<WarningDescriptor> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<WarningDescriptor> warnings) {
		this.warnings = warnings;
	}

}
