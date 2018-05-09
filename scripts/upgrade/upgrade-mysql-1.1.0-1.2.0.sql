alter table JIResource drop key childrenFolder;

alter table JIJNDIJdbcDatasource add column timezone varchar(100);

alter table JIJdbcDatasource add column timezone varchar(100);

alter table JIJdbcDatasource modify connectionURL varchar(200);
