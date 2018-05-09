--
-- Add tables and fields
--

    create table JICustomDatasource (
        id int8 not null,
        serviceClass varchar(250) not null,
        primary key (id)
    );

    create table JICustomDatasourceProperty (
        ds_id int8 not null,
        value varchar(1000),
        name varchar(100) not null,
        primary key (ds_id, name)
    );

    alter table JIOlapUnit 
        add column view_options oid;

    alter table JIReportJobMail 
        add column skip_empty bool;

    alter table JIReportUnit 
        add column promptcontrols bool;

    alter table JIReportUnit 
        add column controlslayout int2;

--
-- Add constraints
--

    alter table JICustomDatasource 
        add constraint FK2BBCEDF5A8BF376D 
        foreign key (id) 
        references JIResource;

    alter table JICustomDatasourceProperty 
        add constraint FKB8A66AEA858A89D1 
        foreign key (ds_id) 
        references JICustomDatasource;

--
-- Fix data
--

    update JIReportJobMail set skip_empty=TRUE where skip_empty is null;

    update JIReportUnit set promptcontrols=FALSE where promptcontrols is null;

    update JIReportUnit set controlslayout=1 where controlslayout is null;
--
-- Add 'not null' constraints
--

    alter table JIReportJobMail modify
        skip_empty bool not null;
