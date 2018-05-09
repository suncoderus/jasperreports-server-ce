--
--
-- 6.2.0 to 6.3.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--
-- Create a table for Azure SQL Data Source
    create table JIAzureSqlDatasource (
        id int8 not null,
        keyStore_id int8 not null,
        keyStorePassword varchar(100),
        keyStoreType varchar(25),
        subscriptionId varchar(100),
        serverName varchar(100) not null,
        dbName varchar(100) not null,
        primary key (id)
    );

    alter table JIAzureSqlDatasource 
        add constraint FKAFE22203C001BAEA 
        foreign key (keyStore_id) 
        references JIResource;

    alter table JIAzureSqlDatasource 
        add constraint FKAFE2220387E4472B 
        foreign key (id) 
        references JIJdbcDatasource;


-- Add SShPrivate key to the Report Job
    alter table JIReportJobRepoDest add column ssh_private_key int8;

    alter table JIReportJobRepoDest
        add constraint FKEA477EBE3C5B87D0
        foreign key (ssh_private_key)
        references JIResource;

