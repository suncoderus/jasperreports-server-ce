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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.Locale;
import java.util.Set;

import net.sf.jasperreports.engine.util.MessageProvider;
import net.sf.jasperreports.engine.util.MessageProviderFactory;

import org.springframework.context.MessageSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JRHtmlExportUtils.java 19932 2010-12-11 15:24:29Z tmatyashovsky $
 */
public class MessageSourceMessageProviderFactory implements MessageProviderFactory
{

	private Set<String> baseNames;
	private MessageProvider messageProvider;
	private MessageProviderCodeMapper codeMapper = new MessageProviderCodeIdentityMapper();
	
	public void setBaseNames(Set<String> baseNames)
	{
		this.baseNames = baseNames;
	}
	
	public void setMessageSource(final MessageSource messageSource)
	{
		this.messageProvider = new MessageProvider() {
			
			@Override
			public String getMessage(String code, Object[] args, Locale locale) {
				String messgeSourceCode = getCodeMapper().getMessgeSourceCode(code);
				return messageSource.getMessage(messgeSourceCode, args, locale);
			}
			
		};
	}
	
	@Override
	public MessageProvider getMessageProvider(String name) 
	{
		if (baseNames != null && baseNames.contains(name))
		{
			return messageProvider;
		}
		return null;
	}

	public MessageProviderCodeMapper getCodeMapper() {
		return codeMapper;
	}

	public void setCodeMapper(MessageProviderCodeMapper codeMapper) {
		this.codeMapper = codeMapper;
	}

		
}
