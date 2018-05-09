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

package com.jaspersoft.jasperserver.war.action;

import com.jaspersoft.jasperserver.api.engine.common.service.ActionModelSupport;
import com.jaspersoft.jasperserver.api.engine.common.service.impl.ActionModel;
import com.jaspersoft.jasperserver.api.engine.common.service.impl.ActionModelServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Supports menu generation - privides static method to get JSON actionModel by name
 *
 */
public class GenericActionModelBuilder implements InitializingBean {
    protected final static Log log = LogFactory.getLog(GenericActionModelSupport.class);
    private static GenericActionModelBuilder actionModelBuilder;
    @Autowired
    @Qualifier("messageSource")
    private MessageSource messages;

    private boolean proVersion;

    public void afterPropertiesSet() throws Exception {
        GenericActionModelBuilder.actionModelBuilder = this;
    }

    public boolean isProVersion() {
        return proVersion;
    }

    public void setProVersion(boolean proVersion) {
        this.proVersion = proVersion;
    }

    public static final class GenericActionModelSupport implements ActionModelSupport {
        private String modelName;
        private MessageSource messages;

        public GenericActionModelSupport(String modelName, MessageSource messages) {
            this.modelName = modelName;
            this.messages = messages;
        }

        public String getClientActionModelDocument() throws Exception {
            Document document =  ActionModelServiceImpl.getInstance().getActionModelMenu(modelName);
            String data;
            try {
                data = ActionModel.getInstance().generateClientActionModel(this, document);
            } catch (Exception e) {
                log.error("Unable to create menu");
                data = "";
            }
            return data;
        }

        public String getMessage(String label) {
            return messages.getMessage(label, null, LocaleContextHolder.getLocale());
        }
    }


    /**
     * Same as GenericActionModelSupport.getClientActionModelDocument but removes leasing '{' and trailing '}' and escapes the string for JavaScript,
     * so that it could be used as part of complicated menu-generation sequence like in DefaultJasperViewerState.jsp
     * @return
     * @throws Exception
     */
    public static String getEmbeddableActionModelDocument(String modelName) throws Exception{
        GenericActionModelSupport support = new GenericActionModelSupport(modelName, GenericActionModelBuilder.actionModelBuilder.messages);
        String data = support.getClientActionModelDocument();
        if(StringUtils.isEmpty(data))
            return data;
        data = StringUtils.strip(data, "{}");
        return data;
    }
    
}
