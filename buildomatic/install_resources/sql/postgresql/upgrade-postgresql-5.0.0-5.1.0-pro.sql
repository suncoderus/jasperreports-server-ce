--
-- Create new tables for aws data source
--

    create table JIAwsDatasource (
        id int8 not null,
        accessKey varchar(100),
        secretKey varchar(100),
        roleARN varchar(100),
        region varchar(100) not null,
        dbName varchar(100) not null,
        dbInstanceIdentifier varchar(100) not null,
        dbService varchar(100) not null,
        primary key (id)
    );

    alter table JIAwsDatasource
        add constraint FK6085542387E4472B
        foreign key (id)
        references JIJdbcDatasource;

