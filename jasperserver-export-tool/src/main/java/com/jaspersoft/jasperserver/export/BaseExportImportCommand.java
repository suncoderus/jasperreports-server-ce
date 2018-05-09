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
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import com.jaspersoft.jasperserver.export.util.CommandUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class BaseExportImportCommand {

	protected static final Log log = LogFactory.getLog(BaseExportImportCommand.class);

	protected static final CommandOut commandOut = CommandOut.getInstance();

	public static final String ARG_CONFIG_FILES = "configFiles";
	public static final String ARG_CONFIG_RESOURCES = "configResources";
	public static final String ARG_COMMAND_BEAN = "commandBean";
	public static final String ARG_HELP = "help";

	public static final String HELP_BEAN_NAME = "helpPrintBean";

	private final String defaultBeanName;
	private final String commandMetaBeanName;

	protected BaseExportImportCommand(String defaultBeanName, String commandMetaBeanName) {
		this.defaultBeanName = defaultBeanName;
		this.commandMetaBeanName = commandMetaBeanName;
	}

	protected static void debugArgs(String[] args) {
		if (log.isDebugEnabled()) {
			for (int i = 0; i < args.length; ++i) {
				log.debug("arg #" + i + " = \"" + args[i] + "\"");
			}
		}
	}

	protected boolean process(String[] args) throws IOException {
		Parameters exportParameters = parseArgs(args);
		ConfigurableApplicationContext ctx;
        if (exportParameters.hasParameter(ARG_HELP)){
            ctx = createSpringContext(exportParameters, "helpApplicationContext-export-import*.xml");
        } else {
            ctx= createSpringContext(exportParameters, "applicationContext*.xml");
        }
		try {
			boolean success = true;
			CommandMetadata metadataBean = getCommandMetadataBean(ctx);
			if (exportParameters.hasParameter(ARG_HELP)) {
				CommandHelp helpBean = getHelpBean(ctx);
				helpBean.printHelp(args[0], metadataBean, System.out);
			} else {
				CommandBean commandBean = getCommandBean(exportParameters, ctx);
				try {
					metadataBean.validateParameters(exportParameters);
					commandBean.process(exportParameters);
				} catch (JSExceptionWrapper e) {
					throw e;
				} catch (JSException e) {
					success = false;
					if (log.isInfoEnabled()) {
						log.info(e.getMessage(), e);
					}

					String message = ctx.getMessage(e.getMessage(), e.getArgs(), Locale.getDefault());
					System.err.println(message);
				}
			}
			return success;
		} finally {
			ctx.close();
		}
	}

	protected Parameters parseArgs(String[] args) {
		Parameters exportParameters = CommandUtils.parse(args);
		return exportParameters;
	}

	protected ConfigurableApplicationContext createSpringContext(Parameters exportParameters, String resourceFileName) throws IOException {
		GenericApplicationContext ctx = new GenericApplicationContext();
		XmlBeanDefinitionReader configReader = new XmlBeanDefinitionReader(ctx);
		Resource[] resources = ctx.getResources(resourceFileName);
        commandOut.info("First resource path:" + resources[0].getFile().getAbsolutePath());
		Arrays.sort(resources, new ResourceComparator());
        commandOut.info("Started to load resources");
		for (Resource resource : resources) {
            commandOut.info("Resource name: " + resource.getFilename());
			configReader.loadBeanDefinitions(resource);
		}
		ctx.refresh();
		return ctx;
	}

	public static class ResourceComparator implements Comparator<Resource> {
		private final static String NAME_SUFFIX = ".xml";
		public int compare(Resource r1, Resource r2) {
			String name1 = r1.getFilename();
			if (name1.endsWith(NAME_SUFFIX)) {
				name1 = name1.substring(0, name1.length() - NAME_SUFFIX.length());
			}

			String name2 = r2.getFilename();
			if (name2.endsWith(NAME_SUFFIX)) {
				name2 = name2.substring(0, name2.length() - NAME_SUFFIX.length());
			}

			return name1.compareTo(name2);
		}
	}
	
	protected static interface SpringResourceFactory {
		Resource create(String location);
	}

	protected static final SpringResourceFactory fileSystemResourceFactory = new SpringResourceFactory() {
		public Resource create(String location) {
			commandOut.debug("Loading Spring configuration file " + location);
			return new FileSystemResource(location);
		}
	};

	protected static final SpringResourceFactory classPathResourceFactory = new SpringResourceFactory() {
		public Resource create(String location) {
			commandOut.debug("Loading Spring configuration classpath resource " + location);
			return new ClassPathResource(location);
		}
	};

	protected void registerConfig(String[] locations, XmlBeanDefinitionReader reader, SpringResourceFactory resourceFactory) {
		if (locations != null && locations.length > 0) {
			for (int i = 0; i < locations.length; i++) {
				String location = locations[i];
				Resource resource = resourceFactory.create(location);
				reader.loadBeanDefinitions(resource);
			}
		}
	}

	protected String getConfigSeparator() {
		return System.getProperty("path.separator");
	}

	protected CommandBean getCommandBean(Parameters exportParameters, ApplicationContext ctx) {
		String beanName = getCommandBeanName(exportParameters);
		return (CommandBean) ctx.getBean(beanName, CommandBean.class);
	}

	protected CommandHelp getHelpBean(ApplicationContext ctx) {
		return (CommandHelp) ctx.getBean(HELP_BEAN_NAME, CommandHelp.class);
	}

	protected CommandMetadata getCommandMetadataBean(ApplicationContext ctx) {
		return (CommandMetadata) ctx.getBean(commandMetaBeanName, CommandMetadata.class);
	}

	protected String getCommandBeanName(Parameters exportParameters) {
		String beanName = exportParameters.getParameterValue(ARG_COMMAND_BEAN);

		if (beanName == null) {
			commandOut.debug("Using default " + defaultBeanName + " command bean");

			beanName = defaultBeanName;
		} else {
			commandOut.debug("Using " + beanName + " command bean");
		}

		return beanName;
	}

}
