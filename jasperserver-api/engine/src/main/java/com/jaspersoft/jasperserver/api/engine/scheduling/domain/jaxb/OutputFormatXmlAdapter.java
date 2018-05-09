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
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashSet;
import java.util.Set;

/**
 * This adapter is used for ReportJob.outputFormats serialization.
 * ReportJobOutputFormatsWrapper is used for serialization because of no ability to use @XmlElementWrapper together with @XmlJavaTypeAdapter.
 * See http://java.net/jira/browse/JAXB-787
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class OutputFormatXmlAdapter extends XmlAdapter<ReportJobOutputFormatsWrapper, Set<Byte>> {

    private enum SupportedOutputFormat implements NamedPropertyHolder<Byte>{
        PDF(ReportJob.OUTPUT_FORMAT_PDF),
        HTML(ReportJob.OUTPUT_FORMAT_HTML),
        XLS(ReportJob.OUTPUT_FORMAT_XLS),
        RTF(ReportJob.OUTPUT_FORMAT_RTF),
        CSV(ReportJob.OUTPUT_FORMAT_CSV),
        ODT(ReportJob.OUTPUT_FORMAT_ODT),
        TXT(ReportJob.OUTPUT_FORMAT_TXT),
        DOCX(ReportJob.OUTPUT_FORMAT_DOCX),
        ODS(ReportJob.OUTPUT_FORMAT_ODS),
        XLSX(ReportJob.OUTPUT_FORMAT_XLSX),
        XLS_NOPAG(ReportJob.OUTPUT_FORMAT_XLS_NOPAG),
        XLSX_NOPAG(ReportJob.OUTPUT_FORMAT_XLSX_NOPAG),
        DATA_SNAPSHOT(ReportJob.OUTPUT_FORMAT_DATA_SNAPSHOT),
        PPTX(ReportJob.OUTPUT_FORMAT_PPTX),
        JSON(ReportJob.OUTPUT_FORMAT_JSON),
        PNG(ReportJob.OUTPUT_FORMAT_PNG);


        private final Byte byteValue;

        private SupportedOutputFormat(Byte byteValue) {
            this.byteValue = byteValue;
        }


        public Byte getProperty() {
            return this.byteValue;
        }
    }

    @Override
    public Set<Byte> unmarshal(ReportJobOutputFormatsWrapper v) throws Exception {
        Set<Byte> result = null;
        if (v != null && v.getFormats() != null && !v.getFormats().isEmpty()) {
            result = new HashSet<Byte>();
            SingleOutputFormatAdapter adapter = new SingleOutputFormatAdapter();
            for (String currentValue : v.getFormats()) {
                Byte currentByteValue = adapter.unmarshal(currentValue);
                if (currentByteValue != null)
                    result.add(currentByteValue);
            }
        }
        return result;
    }

    @Override
    public ReportJobOutputFormatsWrapper marshal(Set<Byte> v) throws Exception {
        ReportJobOutputFormatsWrapper result = null;
        if (v != null && !v.isEmpty()) {
            Set<String> set = new HashSet<String>();
            SingleOutputFormatAdapter adapter = new SingleOutputFormatAdapter();
            for (Byte currentValue : v) {
                String currentStringValue = adapter.marshal(currentValue);
                if (currentStringValue != null)
                    set.add(currentStringValue);
            }
            if(!set.isEmpty()){
                result = new ReportJobOutputFormatsWrapper();
                result.setFormats(set);
            }
        }
        return result;
    }

    private class SingleOutputFormatAdapter extends AbstractEnumXmlAdapter<Byte> {
        @Override
        protected NamedPropertyHolder<Byte>[] getEnumConstantsArray() {
            return SupportedOutputFormat.values();
        }
    }
}
