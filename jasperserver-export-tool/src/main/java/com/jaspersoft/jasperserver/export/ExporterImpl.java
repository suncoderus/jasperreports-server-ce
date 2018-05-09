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

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.util.ExportRunMonitor;
import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;
import com.jaspersoft.jasperserver.export.io.ExportOutput;
import com.jaspersoft.jasperserver.export.modules.ExporterModule;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.ModuleRegister;
import com.jaspersoft.jasperserver.export.modules.common.ExportImportWarningCode;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */

public class ExporterImpl extends BaseExporterImporter implements Exporter {

	private static final Log log = LogFactory.getLog(ExporterImpl.class);

	protected class ModuleContextImpl implements ExporterModuleContext {
		
		private final String moduleId;
		
		public ModuleContextImpl(final String moduleId) {
			this.moduleId = moduleId;
		}

		public String getCharacterEncoding() {
			return ExporterImpl.this.getCharacterEncoding();
		}

		public ExportTask getExportTask() {
			return task;
		}

		public Element getModuleIndexElement() {
			return getIndexModuleElement(moduleId);
		}

		public ModuleRegister getModuleRegister() {
			return ExporterImpl.this.getModuleRegister();
		}
	}
	
	private String xmlVersion;
	
	protected ExportTask task;
	protected ExportOutput output;
	
	private Element indexRootElement;
	private Map moduleIndexElements;

	public String getXmlVersion() {
		return xmlVersion;
	}

	public void setXmlVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}

	public Element getIndexRootElement() {
		return indexRootElement;
	}

	protected Element getIndexModuleElement(String moduleId) {
		return (Element) moduleIndexElements.get(moduleId);
	}
	
	public ExportTask getExportTask() {
		return task;
	}
	
	public void setTask(ExportTask task) {
		this.task = task;
		this.output = task == null ? null :  task.getOutput();
	}
	
	public ExportOutput getOutput() {
		return output;
	}

	public void performExport() {
		try {
			output.open();
			boolean close = true;
			try {
				process();
				
				close = false;
				output.close();
			} finally {
				BaseExporterImporter.setTenant(null);
				if (close) {
					try {
						output.close();
					} catch (IOException ioe) {
						log.info("Error while closing output", ioe);
					}
				}
			}		
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected void process() {
		Document indexDocument = DocumentHelper.createDocument();
		indexRootElement = indexDocument.addElement(getIndexRootElementName());
		
		setOutputProperties();

		ExportRunMonitor.start();
		try {
			invokeModules();
		} finally {
			ExportRunMonitor.stop();
		}
		
		writeDocument(indexDocument, getIndexFilename());
		writeExportWarnings();
	}

	protected void setOutputProperties() {
		Properties properties = output.getOutputProperties();

		String organizationId = getExportTask().getParameters().getParameterValue(ImportExportServiceImpl.ORGANIZATION);
		if (StringUtils.isBlank(organizationId)) {
			properties.put(ImportExportService.ROOT_TENANT_ID, getAuthenticatedTenantId());
		} else {
			properties.put(ImportExportService.ROOT_TENANT_ID, organizationId);
		}

		for (Enumeration it = properties.propertyNames(); it.hasMoreElements();) {
			String property = (String) it.nextElement();
			String value = properties.getProperty(property);
			setOutputProperty(property, value);
		}
        String version = super.getJsVersion();
        if (version != null && !version.equals("")) {
            setOutputProperty(VERSION_ATTR,version);
        }
    }

	protected void setOutputProperty(String property, String value) {
		Element propElement = indexRootElement.addElement(getPropertyElementName());
		propElement.addAttribute(getPropertyNameAttribute(), property);
		if (value != null) {
			propElement.addAttribute(getPropertyValueAttribute(), value);
		}
	}

	protected void invokeModules() {
		List modules = getModuleRegister().getExporterModules();

		moduleIndexElements = new HashMap();
		for (Iterator it = modules.iterator(); it.hasNext();) {
            if (Thread.interrupted()){
                throw new RuntimeException("Cancelled");
            }
			ExporterModule module = (ExporterModule) it.next();
			ModuleContextImpl moduleContext = new ModuleContextImpl(module.getId());

			module.init(moduleContext);
			if (module.toProcess()) {
				commandOut.debug("Module " + module.getId() + " processing");
				
				createModuleElement(module);
				module.process();
			}			
		}
	}

	protected void createModuleElement(ExporterModule module) {		
		Element moduleElement = indexRootElement.addElement(getIndexModuleElementName());
		moduleElement.addAttribute(getIndexModuleIdAttributeName(), module.getId());
		moduleIndexElements.put(module.getId(), moduleElement);
	}

	private void writeDocument(Document document, String filename) {
		OutputStream output = getDocumentOutput(filename);
		boolean closeOutput = true;
		try {
			OutputFormat format = new OutputFormat();
			format.setEncoding(getCharacterEncoding());
			XMLWriter writer = new XMLWriter(output, format);
			writer.write(document);

			closeOutput = false;
			output.close();
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeOutput) {
				try {
					output.close();
				} catch (IOException e) {
					log.error("Error while closing output", e);
				}
			}
		}
	}

	private OutputStream getDocumentOutput(String filename) {
		try {
			return output.getFileOutputStream(filename);
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	private void writeExportWarnings() {
		if (task.getWarnings().isEmpty()) return;

		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement(getBrokenDependenceRootElementName());
		for (WarningDescriptor warningDescriptor : task.getWarnings()) {
			if (warningDescriptor.getCode().equals(ExportImportWarningCode.EXPORT_BROKEN_DEPENDENCY.toString())) {
				for (String str : warningDescriptor.getParameters()) {
					Element element = rootElement.addElement(getResourceElementName());
					element.addText(str);
				}
			}
		}
		writeDocument(document, getBrokenDependenceFilename());
	}
}
