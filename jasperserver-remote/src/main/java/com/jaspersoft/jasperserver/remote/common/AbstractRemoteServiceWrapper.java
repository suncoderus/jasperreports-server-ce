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
package com.jaspersoft.jasperserver.remote.common;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public abstract class AbstractRemoteServiceWrapper<RemoteServiceType, TemplateType> {
    protected RemoteServiceType remoteService;

    protected RemoteServiceType getRemoteService(){
        return remoteService;
    }

    //Developer should care that template type should correspond to type of declared in CallTemplate annotation template.
    @SuppressWarnings("unchecked")
    protected TemplateType getTemplate() {
        CallTemplate callTemplateAnnotation = this.getClass().getAnnotation(CallTemplate.class);
        AbstractCallTemplate template;
        if (callTemplateAnnotation != null) {
            try {
                template = callTemplateAnnotation.value().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("Unable to instantiate remote service call template defined in @" + CallTemplate.class.getSimpleName() + " annotation. Template class: " + callTemplateAnnotation.value().getName(), e);
            }
        } else
            template = getDefaultTemplate();
        return (TemplateType) template;
    }

    protected abstract AbstractCallTemplate getDefaultTemplate();

    protected abstract class ConcreteCaller<ResponseType> implements RemoteServiceInTemplateCaller<ResponseType, RemoteServiceType>{
    }


}
