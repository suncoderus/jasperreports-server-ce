--
-- upgrade PostgreSQL 2.1.0 -> 3.0.0
--

alter table JIInputControl 
    alter column query_value_column type varchar(200);

alter table JIInputControlQueryColumn 
    alter column query_column type varchar(200);

alter table JIJdbcDatasource 
    alter column connectionUrl type text;    

alter table JILogEvent 
    alter column event_text type text,
    alter column event_data type oid;

alter table JIReportJobRepoDest 
    add column output_description varchar(100), 
    add column timestamp_pattern varchar(250);

alter table jiuser 
    add column previousPasswordChangeTime timestamp without time zone;
