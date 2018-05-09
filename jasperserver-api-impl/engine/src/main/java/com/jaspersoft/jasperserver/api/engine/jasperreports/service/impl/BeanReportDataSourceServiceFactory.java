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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author swood
 *
 */
public class BeanReportDataSourceServiceFactory implements ReportDataSourceServiceFactory, ApplicationContextAware {

	ApplicationContext ctx;

    private MessageSource messageSource;
	
	/**
	 * 
	 */
	public BeanReportDataSourceServiceFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		ctx = arg0;
	}

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /* (non-Javadoc)
         * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory#createService(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
         */
	public ReportDataSourceService createService(ReportDataSource reportDataSource) {
		if (!(reportDataSource instanceof BeanReportDataSource)) {
			throw new JSException(getErrorMessage("jsexception.invalid.bean.datasource", new String[] {reportDataSource.getClass().toString()}));
		}
		BeanReportDataSource beanDataSource = (BeanReportDataSource) reportDataSource;
	
		Object bean = ctx.getBean(beanDataSource.getBeanName());
		
		if (bean == null) {
			throw new JSException(getErrorMessage("jsexception.bean.no.name", new String[] {beanDataSource.getBeanName()}));
		}
		
		if (beanDataSource.getBeanMethod() == null) {
			// The bean had better be a ReportDataSourceService
			if (!(bean instanceof ReportDataSourceService)) {
				throw new JSException(getErrorMessage("jsexception.bean.not.a.ReportDataSourceService", new String[] {beanDataSource.getBeanName()}));
			} else {
				return (ReportDataSourceService) bean;
			}
		} else {
			// The method on this bean returns a ReportDataSourceService
			Method serviceMethod;
			try {
				serviceMethod = bean.getClass().getMethod(beanDataSource.getBeanMethod(), null);
				return (ReportDataSourceService) serviceMethod.invoke(bean, null);
			} catch (SecurityException e) {
				throw new JSExceptionWrapper(e);
			} catch (NoSuchMethodException e) {
				throw new JSException(getErrorMessage("jsexception.bean.has.no.method", new String[] {beanDataSource.getBeanName(), beanDataSource.getBeanMethod()}));
			} catch (IllegalArgumentException e) {
				throw new JSExceptionWrapper(e);
			} catch (IllegalAccessException e) {
				throw new JSExceptionWrapper(e);
			} catch (InvocationTargetException e) {
				throw new JSExceptionWrapper(e);
			}
		}
	}

    private String getErrorMessage(String key, String[] params) {
        return messageSource.getMessage(key, params, LocaleContextHolder.getLocale());
    }

}
