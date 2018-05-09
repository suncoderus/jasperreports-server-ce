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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb;

import com.jaspersoft.jasperserver.api.common.domain.jaxb.AbstractEnumXmlAdapter;
import com.jaspersoft.jasperserver.api.common.domain.jaxb.NamedPropertyHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ReportJobSendTypeXmlAdapter extends AbstractEnumXmlAdapter<Byte> {
    @Override
    protected NamedPropertyHolder<Byte>[] getEnumConstantsArray() {
        return SendType.values();
    }

    public enum SendType implements NamedPropertyHolder<Byte> {
        SEND(ReportJobMailNotification.RESULT_SEND),
        SEND_ATTACHMENT(ReportJobMailNotification.RESULT_SEND_ATTACHMENT),
        SEND_ATTACHMENT_NOZIP(ReportJobMailNotification.RESULT_SEND_ATTACHMENT_NOZIP),
        SEND_EMBED(ReportJobMailNotification.RESULT_SEND_EMBED),
        SEND_ATTACHMENT_ZIP_ALL(ReportJobMailNotification.RESULT_SEND_ATTACHMENT_ZIP_ALL),
        SEND_EMBED_ZIP_ALL_OTHERS(ReportJobMailNotification.RESULT_SEND_EMBED_ZIP_ALL_OTHERS);

        private final Byte byteValue;

        private SendType(Byte byteValue) {
            this.byteValue = byteValue;
        }
         public Byte getProperty(){
             return this.byteValue;
         }
    }




}
