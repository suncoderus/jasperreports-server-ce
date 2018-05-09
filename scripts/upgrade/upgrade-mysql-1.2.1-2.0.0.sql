--
-- Add tables and fields
--

    create table JICustomDatasource (
        id bigint not null,
        serviceClass varchar(250) not null,
        primary key (id)
    );

    create table JICustomDatasourceProperty (
        ds_id bigint not null,
        value text,
        name varchar(100) not null,
        primary key (ds_id, name)
    );

    alter table JIOlapUnit 
        add column view_options longblob;

    alter table JIReportJobMail
        add column skip_empty bit;

    alter table JIReportUnit 
        add column promptcontrols bit;

    alter table JIReportUnit 
        add column controlslayout tinyint;

--
-- Add constraints
--

    alter table JICustomDatasource 
        add index FK2BBCEDF5A8BF376D (id), 
        add constraint FK2BBCEDF5A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JICustomDatasourceProperty 
        add index FKB8A66AEA858A89D1 (ds_id), 
        add constraint FKB8A66AEA858A89D1 
        foreign key (ds_id) 
        references JICustomDatasource (id);

--
-- Fix data
--

    update JIReportJobMail set skip_empty=1 where skip_empty is null;

    update JIReportUnit set promptcontrols=0 where promptcontrols is null;

    update JIReportUnit set controlslayout=1 where controlslayout is null;

--
-- Add 'not null' constraints
--

    alter table JIReportJobMail modify
        skip_empty bit not null;
