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

package com.jaspersoft.jasperserver.war.themes;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;

/**
 * ThemeSource implementation which serves theme file requests directly from webapp files.
 * It doesn't require a message catalog, which you have to use for ResourceBundleThemeSource,
 * because it uses a MessageSource which figures out the resource path from the theme name
 * and the name of the requested resource.
 * It's currently hardcoded to use the convention "themes/[themeName]/[resourceName]".
 * 
 * To enable:
 * - comment out the beans "themeSource", "themeResolver", and "jsThemeResolver"
 * - add this class as "themeSource"
 *       <bean id="themeSource" class="com.jaspersoft.jasperserver.war.themes.SimplerFileThemeSource"/>
 * - uncomment the "themeResolver" bean using FixedThemeResolver
 *  
 * This will set a fixed theme based on whatever you put in the fixed theme resolver.
 * However, I don't see why you couldn't leave in the normal themeResolver bean.
 * 
 * @author rtinsman
 *
 */
public class SimplerFileThemeSource implements ThemeSource { 

	@Override
	public Theme getTheme(String name) {

		return new BasicTheme(name);
	}
	
	public class BasicTheme implements Theme {
		private String name;
		
		public BasicTheme(String name) {
			this.name = name;
		}

		@Override
		public MessageSource getMessageSource() {
			return new FileConventionMessageSource(name);
		}

		@Override
		public String getName() {
			return name;
		}
	}
	/* from applicationContext-themes.xml:
	 * 
	 *         0. this mode is convenient for designing/debugging a theme
	           1. in this mode you cannot change themes dynamically
	           2. you must have a directory under /webapp/themes/[themeName] with a name that matches the value for the property 'defaultTheme'
	           3. you must have the file /main/webappAdditions/classes/themes/[themeName].properties where there is a correct name/value pair
	              for each required theme file, e.g. theme.css=themes/[themeName]/theme.css

	 */
	
	public class FileConventionMessageSource implements MessageSource {
		private String name;

		public FileConventionMessageSource(String name) {
			this.name = name;
		}

		@Override
		public String getMessage(MessageSourceResolvable arg0, Locale arg1) throws NoSuchMessageException {
			// avoid figuring this one out if possible
			return null;
		}

		@Override
		public String getMessage(String msgName, Object[] args, Locale locale) throws NoSuchMessageException {
			// I don't give a shit about anything else, this is a dev environment
			return "themes/" + name + "/" + msgName;
		}

		@Override
		public String getMessage(String msgName, Object[] args, String someArg, Locale locale) {
			// I don't give a shit about anything else, this is a dev environment
			return "themes/" + name + "/" + msgName;
		}
		
	}
}
