--
-- upgrade mysql 2.1.0 -> 3.0.0
--

alter table JIInputControl 
    modify query_value_column varchar(200);


alter table JIInputControlQueryColumn 
    modify query_column varchar(200);


alter table JIJdbcDatasource
    modify column connectionUrl text;
    

alter table JILogEvent
    modify column event_text mediumtext;

alter table JIReportJobRepoDest
    add column output_description varchar(250),
    add column timestamp_pattern varchar(250);


alter table JIUser
    add column previousPasswordChangeTime datetime;

update JIUser 
    set password='jasperadmin' 
    where username='jasperadmin';
    
--
-- clean out cached reports
-- 
update JIRepositoryCache set item_reference = null;
delete from JIRepositoryCache;

