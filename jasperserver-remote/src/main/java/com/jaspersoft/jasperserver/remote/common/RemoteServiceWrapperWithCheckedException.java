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

import com.jaspersoft.jasperserver.remote.exception.RemoteException;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class RemoteServiceWrapperWithCheckedException<RemoteServiceType, ExceptionType extends Exception> extends AbstractRemoteServiceWrapper<RemoteServiceType, RemoteServiceCheckedExceptionCallTemplate<RemoteServiceType, ExceptionType>>{
     /**
     * This method is used to call remote service within the same template.
     * Default template can be changed via @CallTemplate annotation.
     *
     * @param caller contain concrete remote service call logic
     * @param <ResponseType> - concrete type of caller response
     * @return caller response
     * @throws ExceptionType - concrete checked exception
     */
    protected final <ResponseType> ResponseType callRemoteService(ConcreteCaller<ResponseType> caller) throws ExceptionType{
        return getTemplate().callRemoteService(caller, getRemoteService());
    }

    @Override
    protected AbstractCallTemplate getDefaultTemplate() {
        return new RemoteServiceCheckedExceptionCallTemplate<RemoteServiceType, ExceptionType>() {
            public <ResponseType> ResponseType callRemoteService(RemoteServiceInTemplateCaller<ResponseType, RemoteServiceType> responseTypeRemoteServiceTypeRemoteServiceInTemplateCaller, RemoteServiceType service) throws ExceptionType {
                try {
                    return responseTypeRemoteServiceTypeRemoteServiceInTemplateCaller.call(service);
                } catch (RemoteException e) {
                    throw new RuntimeException("Unexpected exception occurs", e);
                }
            }
        };
    }
}
