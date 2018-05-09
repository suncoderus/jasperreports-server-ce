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

package com.jaspersoft.jasperserver.war.webflow;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.executor.FlowExecutorImpl;
import org.springframework.webflow.execution.repository.impl.DefaultFlowExecutionRepository;
import org.springframework.webflow.conversation.impl.SessionBindingConversationManager;

/**
 */
public class WebFlowConfigurer implements BeanFactoryPostProcessor {
    private String flowExecutorBeanName;
    private int lockTimeoutSeconds;

    public void setFlowExecutorBeanName(String flowExecutorBeanName) {
        this.flowExecutorBeanName = flowExecutorBeanName;
    }

    public void setLockTimeoutSeconds(int lockTimeoutSeconds) {
        this.lockTimeoutSeconds = lockTimeoutSeconds;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        FlowExecutorImpl flowExecutor = getFlowExecutor(beanFactory);

        if (flowExecutor != null) {
            getConversationManager(flowExecutor).setLockTimeoutSeconds(lockTimeoutSeconds);
        }
    }

    private FlowExecutorImpl getFlowExecutor(ConfigurableListableBeanFactory beanFactory) {
        return (FlowExecutorImpl) beanFactory.getBean(flowExecutorBeanName);
    }

    public DefaultFlowExecutionRepository getExecutionRepository(FlowExecutorImpl flowExecutor) {
        return ((DefaultFlowExecutionRepository) flowExecutor.getExecutionRepository());
    }

    public SessionBindingConversationManager getConversationManager(FlowExecutorImpl flowExecutor) {
        return ((SessionBindingConversationManager) getExecutionRepository(flowExecutor).getConversationManager());
    }
}

