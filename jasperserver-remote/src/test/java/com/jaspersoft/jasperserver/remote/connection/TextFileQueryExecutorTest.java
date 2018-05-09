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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.DataAdapterDefinition;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.CustomReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.connection.query.TextFileCastConversionRule;
import com.jaspersoft.jasperserver.dto.connection.query.TextFileConvert;
import com.jaspersoft.jasperserver.dto.connection.query.TextFileQuery;
import com.jaspersoft.jasperserver.dto.connection.query.TextFileSelect;
import com.jaspersoft.jasperserver.dto.resources.ClientCustomDataSource;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.converters.CustomDataSourceResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import com.jaspersoft.jasperserver.war.cascade.handlers.converters.DataConverterService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class TextFileQueryExecutorTest {
    @InjectMocks
    private TextFileQueryExecutor queryExecutor = new TextFileQueryExecutor();
    @Mock
    private CustomReportDataSourceServiceFactory customDataSourceFactory;
    @Mock
    private CustomDataSourceResourceConverter customDataSourceResourceConverter;
    @Mock
    private DataConverterService dataConverterService;

    private ClientCustomDataSource connection = new ClientCustomDataSource();
    private CustomReportDataSourceImpl serverObject = new CustomReportDataSourceImpl();
    private DataAdapterDefinition dataAdapterDefinitionMock = mock(DataAdapterDefinition.class);
    private JRDataSourceStub jrDataSourceStub;

    private List<Map<String, String>> data = new ArrayList<Map<String, String>>(){{
        add(new HashMap<String, String>(){{
            put("field0", "field0#row0");
            put("field1", "field1#row0");
            put("field2", "field2#row0");
        }});
        add(new HashMap<String, String>(){{
            put("field0", "field0#row1");
            put("field1", "field1#row1");
            put("field2", "field2#row1");
        }});
        add(new HashMap<String, String>(){{
            put("field0", "field0#row2");
            put("field1", "field1#row2");
            put("field2", "field2#row2");
        }});
        add(new HashMap<String, String>(){{
            put("field0", "field0#row3");
            put("field1", "field1#row3");
            put("field2", "field2#row3");
        }});
        add(new HashMap<String, String>(){{
            put("field0", "field0#row4");
            put("field1", "field1#row4");
            put("field2", "field2#row4");
        }});
        add(new HashMap<String, String>(){{
            put("field0", "field0#row5");
            put("field1", "field1#row5");
            put("field2", "field2#row5");
        }});
        add(new HashMap<String, String>(){{
            put("field0", "field0#row6");
            put("field1", "field1#row6");
            put("field2", "field2#row6");
        }});
    }};

    private static class JRDataSourceStub extends JRCsvDataSource{
        private List<JRField> requestedFields = new ArrayList<JRField>();
        private final Iterator<Map<String, String>> iterator;
        private Map<String, String> currentItem;
        public JRDataSourceStub(List<Map<String, String>> data) throws JRException {
            super(new ByteArrayInputStream("ttt".getBytes()));
            this.iterator = data.iterator();
        }

        @Override
        public boolean next() throws JRException {
            currentItem = iterator.hasNext() ? iterator.next() : null;
            return currentItem != null;
        }

        @Override
        public Object getFieldValue(JRField jrField) throws JRException {
            requestedFields.add(jrField);
            return currentItem != null && jrField != null ? currentItem.get(jrField.getName()) : null;
        }

        public List<JRField> getRequestedFields(){
            return requestedFields;
        }
    }

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void refresh() throws Exception{
        reset(customDataSourceResourceConverter, dataAdapterDefinitionMock, customDataSourceFactory, dataConverterService);
        when(customDataSourceResourceConverter.toServer(same(connection), any(ToServerConversionOptions.class)))
                .thenReturn(serverObject);
        when(customDataSourceFactory.getDefinition(serverObject)).thenReturn(dataAdapterDefinitionMock);
        when(dataAdapterDefinitionMock.getJRDataSource(serverObject)).thenAnswer(new Answer<JRCsvDataSource>() {
            @Override
            public JRCsvDataSource answer(InvocationOnMock invocationOnMock) throws Throwable {
                jrDataSourceStub = new JRDataSourceStub(data);
                return jrDataSourceStub;
            }
        });
        when(dataConverterService.formatSingleValue(any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object value = invocationOnMock.getArguments()[0];
                return value != null ? value.toString() : value;
            }
        });
    }

    @Test
    public void executeQuery_nullSafety(){
        assertNull(queryExecutor.executeQuery(null, new ClientCustomDataSource(), null, null));
        assertNull(queryExecutor.executeQuery(new TextFileQuery(), null, null, null));
        assertNull(queryExecutor.executeQuery(null, null, null, null));
    }

    @Test
    public void executeQuery_allData(){
        final TextFileQuery query = new TextFileQuery().setSelect(new TextFileSelect().addColumn("field0", "field1", "field2"));
        final List<List<String>> resultSet = (List<List<String>>) queryExecutor.executeQuery(query, connection, null, null);
        assertResultSet(resultSet, query);
    }

    @Test
    public void executeQuery_paginationByTwoRows(){
        final TextFileQuery query = new TextFileQuery().setSelect(new TextFileSelect().addColumn("field0", "field1", "field2")).setLimit(2);
        assertResultSet((List<List<String>>) queryExecutor.executeQuery(query, connection, null, null), query);
        query.setOffset(2);
        assertResultSet((List<List<String>>) queryExecutor.executeQuery(query, connection, null, null), query);
        query.setOffset(4);
        assertResultSet((List<List<String>>) queryExecutor.executeQuery(query, connection, null, null), query);
        query.setOffset(6);
        assertResultSet((List<List<String>>) queryExecutor.executeQuery(query, connection, null, null), query);
    }

    @Test
    public void executeQuery_allData_randomColumns(){
        final TextFileQuery query = new TextFileQuery().setSelect(new TextFileSelect().addColumn("field2", "field0", "field1"));
        final List<List<String>> resultSet = (List<List<String>>) queryExecutor.executeQuery(query, connection, null, null);
        assertResultSet(resultSet, query);
    }

    @Test
    public void executeQuery_allData_twoColumns(){
        final TextFileQuery query = new TextFileQuery().setSelect(new TextFileSelect().addColumn("field2", "field0"));
        final List<List<String>> resultSet = (List<List<String>>) queryExecutor.executeQuery(query, connection, null, null);
        assertResultSet(resultSet, query);
    }

    @Test(expectedExceptions = MandatoryParameterNotFoundException.class)
    public void executeQuery_convert_ruleFieldsValidationForNull_column(){
        final TextFileQuery query = new TextFileQuery().setSelect(new TextFileSelect().addColumn("field2", "field0"))
                .setConvert(new TextFileConvert().addRule(new TextFileCastConversionRule().setType(String.class.getName())));
        queryExecutor.executeQuery(query, connection, null, null);
    }

    @Test(expectedExceptions = MandatoryParameterNotFoundException.class)
    public void executeQuery_convert_ruleFieldsValidationForNull_type(){
        final TextFileQuery query = new TextFileQuery().setSelect(new TextFileSelect().addColumn("field2", "field0"))
                .setConvert(new TextFileConvert().addRule(new TextFileCastConversionRule().setColumn("someColumn")));
        queryExecutor.executeQuery(query, connection, null, null);
    }

    @Test
    public void executeQuery_jrFieldsAreRequestedWithCorrectType(){
        final TextFileQuery query = new TextFileQuery().setSelect(new TextFileSelect().addColumn("field2", "field0"))
                .setConvert(new TextFileConvert()
                        .addRule(new TextFileCastConversionRule().setColumn("field0").setType(Integer.class.getName()))
                        .addRule(new TextFileCastConversionRule().setColumn("field2").setType(BigDecimal.class.getName()))
                );
        queryExecutor.executeQuery(query, connection, null, null);
        assertNotNull(jrDataSourceStub);
        final List<JRField> requestedFields = jrDataSourceStub.getRequestedFields();
        assertNotNull(requestedFields);
        assertFalse(requestedFields.isEmpty());
        for(JRField field : requestedFields){
            final String name = field.getName();
            final String valueClassName = field.getValueClassName();
            if("field0".equals(name)){
                assertEquals(Integer.class.getName(), valueClassName);
            } else if("field2".equals(name)){
                assertEquals(BigDecimal.class.getName(), valueClassName);
            } else {
                fail();
            }
        }
        verify(dataConverterService, atLeastOnce()).formatSingleValue(any());
    }

    protected void assertResultSet(List<List<String>> resultSet, TextFileQuery query){
        if(query.getLimit() != null){
            assertTrue(resultSet.size() <= query.getLimit());
        } else {
            assertEquals(resultSet.size(), data.size());
        }
        int offset = query.getOffset() != null ? query.getOffset() : 0;
        for(int i = 0; i < resultSet.size(); i++){
            final Map<String, String> dataRow = data.get(i + offset);
            final List<String> currentRow = resultSet.get(i);
            final List<String> columns = query.getSelect().getColumns();
            assertEquals(currentRow.size(), columns.size());
            for(int j = 0; j < currentRow.size(); j++){
                final String currentValue = currentRow.get(j);
                assertNotNull(currentValue);
                assertEquals(currentValue, dataRow.get(columns.get(j)));
            }
        }

    }
}
