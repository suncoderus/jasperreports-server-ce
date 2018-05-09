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

package com.jaspersoft.jasperserver.war.tiles2;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class JasperViewResolver extends InternalResourceViewResolver implements Ordered {

    private List<String> excludes;

    /**
     * Overrides loadView in UrlBasedViewResolver to be able to make a difference when letting the other views
     * in the chaing take control of the request
     *
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#loadView(String,java.util.Locale)
     */
    protected View loadView(String viewName, Locale locale) throws Exception {
        AbstractUrlBasedView view = buildView(viewName);
        View viewObj = (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
        if (viewObj instanceof JstlView) {
            JstlView jv = (JstlView) viewObj;
            if (isExcluded(jv.getBeanName())) {
                return null;
            }
        }

        return viewObj;
    }

    private boolean isExcluded(String s) {
        for (String exclude : this.excludes) {
            Pattern p = Pattern.compile(exclude);
            Matcher m = p.matcher(s);

            if (m.lookingAt()) {
                return true;
            }
        }

        return false;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
}



