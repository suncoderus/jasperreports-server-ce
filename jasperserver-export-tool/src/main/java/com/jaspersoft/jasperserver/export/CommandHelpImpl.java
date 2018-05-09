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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.context.MessageSource;

import com.jaspersoft.jasperserver.export.util.CommandUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CommandHelpImpl implements CommandHelp {

	private static final Pattern LINE_SPLITTER_PATTERN = Pattern.compile("\\n", Pattern.MULTILINE);
	private static final char SPACE = ' ';
	
	private MessageSource messageSource;
	private String startMessage;
	private String argDescriptionMessagePrefix;
	private String argLinePrefix;
	private String argLineSeparator;
	private Set<String> resourceTypes;

	protected int computeMaxArgNameLength(CommandMetadata commandMeta) {
		int maxArgNameLength = 0;
		for (Iterator i = commandMeta.getArgumentNames().iterator(); i.hasNext();) {
			String argName = (String) i.next();
			if (argName.length() > maxArgNameLength) {
				maxArgNameLength = argName.length();
			}
		}
		return maxArgNameLength;
	}
	
	protected String computeDescContPrefix(int maxArgNameLength) {
		int length = 
			argLinePrefix.length() 
			+ CommandUtils.ARG_PREFIX.length() 
			+ maxArgNameLength 
			+ argLineSeparator.length();
		
		char[] c = new char[length];
		Arrays.fill(c, SPACE);
		
		String descContPrefix = new String(c);
		return descContPrefix;
	}

	public void printHelp(String command, CommandMetadata commandMeta, PrintStream out) {
		String header = messageSource.getMessage(startMessage, new String[]{command}, getLocale());
		out.println(header);
		
		int maxArgNameLength = computeMaxArgNameLength(commandMeta);
		String descContPrefix = computeDescContPrefix(maxArgNameLength);

		for (Iterator iter = commandMeta.getArgumentNames().iterator(); iter.hasNext();) {
			String argName = (String) iter.next();
			String argDescription;
			if (argName.equals("resource-types")) {
				String[] typesArg = new String[] {resourceTypes.toString()};
				argDescription = messageSource.getMessage(getArgDescriptionMessagePrefix() + argName, typesArg, getLocale());
			} else {
				argDescription = messageSource.getMessage(getArgDescriptionMessagePrefix() + argName, null, getLocale());
			}
			printArgumentHelp(out, argName, argDescription, maxArgNameLength, descContPrefix);
		}
		
		out.println();
	}

	protected Locale getLocale() {
		return Locale.getDefault();
	}

	protected void printArgumentHelp(PrintStream out, String argName, String argDescription,
			int maxArgNameLength, String descContPrefix) {
		out.print(argLinePrefix);
		out.print(CommandUtils.ARG_PREFIX);
		out.print(argName);
		for (int i = argName.length(); i < maxArgNameLength; ++i) {
			out.print(SPACE);
		}
		out.print(argLineSeparator);
		
		String[] descLines = LINE_SPLITTER_PATTERN.split(argDescription, -1);
		if (descLines.length > 0) {
			out.print(descLines[0]);
			for (int i = 1; i < descLines.length; ++i) {
				out.println();
				out.print(descContPrefix);
				out.print(descLines[i]);
			}
		}
		
		out.println();
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getArgDescriptionMessagePrefix() {
		return argDescriptionMessagePrefix;
	}

	public void setArgDescriptionMessagePrefix(String argDescriptionMessagePrefix) {
		this.argDescriptionMessagePrefix = argDescriptionMessagePrefix;
	}

	public String getArgLinePrefix() {
		return argLinePrefix;
	}

	public void setArgLinePrefix(String argLinePrefix) {
		this.argLinePrefix = argLinePrefix;
	}

	public String getArgLineSeparator() {
		return argLineSeparator;
	}

	public void setArgLineSeparator(String argLineSeparator) {
		this.argLineSeparator = argLineSeparator;
	}

	public String getStartMessage() {
		return startMessage;
	}

	public void setStartMessage(String startMessage) {
		this.startMessage = startMessage;
	}

	public void setResourceTypes(Set<String> resourceTypes) {
		this.resourceTypes = resourceTypes;
	}
}
