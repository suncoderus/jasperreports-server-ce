--
--
-- 6.2.0 to 6.3.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--
-- Create a table for Azure SQL Data Source
    create table JIAzureSqlDatasource (
        id bigint not null,
        keyStore_id bigint not null,
        keyStorePassword varchar(100),
        keyStoreType varchar(25),
        subscriptionId varchar(100),
        serverName varchar(100) not null,
        dbName varchar(100) not null,
        primary key (id)
    ) ENGINE=InnoDB;

    alter table JIAzureSqlDatasource 
        add index FKAFE22203C001BAEA (keyStore_id), 
        add constraint FKAFE22203C001BAEA 
        foreign key (keyStore_id) 
        references JIResource (id);

    alter table JIAzureSqlDatasource 
        add index FKAFE2220387E4472B (id), 
        add constraint FKAFE2220387E4472B 
        foreign key (id) 
        references JIJdbcDatasource (id);


-- Add SShPrivate key to the Report Job
    alter table JIReportJobRepoDest add column ssh_private_key bigint;

    alter table JIReportJobRepoDest
        add index FKEA477EBE3C5B87D0 (ssh_private_key),
        add constraint FKEA477EBE3C5B87D0
        foreign key (ssh_private_key)
        references JIResource (id);
