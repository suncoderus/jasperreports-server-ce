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
package com.jaspersoft.jasperserver.war.common;

import java.util.*;

import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class LocalesListImpl implements LocalesList {
	
	private final LocaleHelper localeHelper;
	private List<Locale> locales;
	
	public LocalesListImpl() {
		localeHelper = LocaleHelper.getInstance();
	}

	public List getLocales() {
		return locales;
	}

	public void setLocales(List locales) {
		this.locales = locales;
	}

	public UserLocale[] getUserLocales(Locale displayLocale) {
        UserLocale[] result;
        if (locales == null) {
            result = new UserLocale[1];
            result[0] = getUserLocale(displayLocale, displayLocale);
        } else {
            result = new UserLocale[locales.size()];
             for (int i = 0; i < result.length; i++) {
                 if ((locales.get(i).getLanguage().equals(displayLocale.getLanguage()) && locales.get(i).getCountry().equals(""))
                         || locales.get(i).equals(displayLocale)) {
                     result[i] = result[0];
                     result[0] = getUserLocale(displayLocale.getCountry().equals("") ? locales.get(i) : displayLocale, displayLocale);
                } else {
                    result[i] = getUserLocale(locales.get(i), displayLocale);
                }
            }
        }
        return result;
	}

	protected UserLocale getUserLocale(Locale locale, Locale displayLocale) {
		String code = localeHelper.getCode(locale);
		String name = locale.getDisplayName(displayLocale);
		return new UserLocale(code, name);
	}

}
