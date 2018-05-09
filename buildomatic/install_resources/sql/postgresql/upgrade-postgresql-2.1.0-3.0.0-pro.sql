--
-- upgrade PostgreSQL 2.1.0 -> 3.0.0 Pro
--

    create table JIDashboard (
        id int8 not null,
        adhocStateId int8,
        primary key (id)
    );

    create table JIDashboardFrameProperty (
        id int8 not null,
        frameName varchar(255) not null,
        frameClassName varchar(255) not null,
        propertyName varchar(255) not null,
        propertyValue varchar(1000),
        idx int4 not null,
        primary key (id, idx)
    );

    create table JIDataDefinerUnit (
        id int8 not null,
        primary key (id)
    );

    create table JIDomainDatasource (
        id int8 not null,
        schema_id int8 not null,
        security_id int8,
        primary key (id)
    );

    create table JIDomainDatasourceBundle (
        slds_id int8 not null,
        locale varchar(20),
        bundle_id int8 not null,
        idx int4 not null,
        primary key (slds_id, idx)
    );

    create table JIDomainDatasourceDSRef (
        slds_id int8 not null,
        ref_id int8 not null,
        alias varchar(100) not null,
        primary key (slds_id, alias)
    );


alter table JIInputControl 
    alter column query_value_column type varchar(200);

alter table JIInputControlQueryColumn 
    alter column query_column type varchar(200);
    
alter table JIJdbcDatasource 
    alter column connectionUrl type varchar(500);
    
alter table JILogEvent 
    alter column event_text type text;
    
alter table JIProfileAttribute 
    alter column principalobjectclass type varchar(255),
    alter column principalobjectclass  set not null;
    
alter table JIProfileAttribute 
    alter column principalobjectid type int8,
    alter column principalobjectid set not null;

alter table JIReportJobRepoDest 
    add column output_description varchar(250);
    
alter table JIReportJobRepoDest 
    add column timestamp_pattern varchar(250);
    
alter table JIReportOptions 
    alter column options_name type varchar(210),
    alter column options_name set not null;
    
alter table JIUser 
    add column previousPasswordChangeTime timestamp;    

alter table JIDashboard 
        add constraint FKEC09F81531211827 
        foreign key (adhocStateId) 
        references JIAdhocState;

alter table JIDashboard 
        add constraint FKEC09F815A8BF376D 
        foreign key (id) 
        references JIResource;

alter table JIDashboardFrameProperty 
        add constraint FK679EF04DFA08F0B4 
        foreign key (id) 
        references JIAdhocState;

alter table JIDataDefinerUnit 
        add constraint FK1EC11AF2981B13F0 
        foreign key (id) 
        references JIReportUnit;


alter table JIDomainDatasource 
        add constraint FK59F8EB8833A6D267 
        foreign key (schema_id) 
        references JIFileResource;

alter table JIDomainDatasource 
        add constraint FK59F8EB88992A3868 
        foreign key (security_id) 
        references JIFileResource;

alter table JIDomainDatasource 
        add constraint FK59F8EB88A8BF376D 
        foreign key (id) 
        references JIResource;

alter table JIDomainDatasourceBundle 
        add constraint FKE9F0422AE494DFE6 
        foreign key (bundle_id) 
        references JIFileResource;

alter table JIDomainDatasourceBundle 
        add constraint FKE9F0422ACB906E03 
        foreign key (slds_id) 
        references JIDomainDatasource;

alter table JIDomainDatasourceDSRef 
        add constraint FKFDA42FC7106B699 
        foreign key (ref_id) 
        references JIResource;

alter table JIDomainDatasourceDSRef 
        add constraint FKFDA42FCCB906E03 
        foreign key (slds_id) 
        references JIDomainDatasource;


-- update JIUser set password='jasperadmin' where username='jasperadmin';


--
-- clean out cached reports
-- 
update JIRepositoryCache set item_reference = null;
delete from JIRepositoryCache;

-- There are four tables containing "oid" types that we are converting to bytea.
-- JIRepositoryCache doesn't require data to be preserved, so let's do it first.

alter table JIRepositoryCache
    drop data,
    add column data bytea;
  
-- create function to read blobs and convert into bytea

CREATE OR REPLACE FUNCTION lo_readall(oid) RETURNS bytea
    AS $_$

SELECT loread(q3.fd, q3.filesize + q3.must_exec) FROM
    (SELECT q2.fd, q2.filesize, lo_lseek(q2.fd, 0, 0) AS must_exec FROM
        (SELECT q1.fd, lo_lseek(q1.fd, 0, 2) AS filesize FROM
            (SELECT lo_open($1, 262144) AS fd)
        AS q1)
    AS q2)
AS q3

$_$ LANGUAGE sql STRICT;

alter table jicontentresource
    alter column data type bytea
    using lo_readall(data);

alter table jifileresource
    alter column data type bytea
    using lo_readall(data);

alter table jiolapunit
    alter column view_options type bytea
    using lo_readall(view_options);

DROP FUNCTION lo_readall(oid);

