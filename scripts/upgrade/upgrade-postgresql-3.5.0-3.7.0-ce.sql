
    create table JIAccessEvent (
        id int8 not null,
        user_id int8 not null,
        event_date timestamp not null,
        resource_id int8 not null,
        updating bool not null,
        primary key (id)
    );

    create table JIAuditEvent (
        id int8 not null,
        username varchar(100),
        tenant_id varchar(100),
        event_date timestamp not null,
        resource_uri varchar(250),
        resource_type varchar(250),
        event_type varchar(100) not null,
        request_type varchar(100) not null,
        primary key (id)
    );

    create table JIAuditEventArchive (
        id int8 not null,
        username varchar(100),
        tenant_id varchar(100),
        event_date timestamp not null,
        resource_uri varchar(250),
        resource_type varchar(250),
        event_type varchar(100) not null,
        request_type varchar(100) not null,
        primary key (id)
    );

    create table JIAuditEventProperty (
        id int8 not null,
        property_type varchar(100) not null,
        value varchar(250),
        clob_value text,
        audit_event_id int8 not null,
        primary key (id)
    );

    create table JIAuditEventPropertyArchive (
        id int8 not null,
        property_type varchar(100) not null,
        value varchar(250),
        clob_value text,
        audit_event_id int8 not null,
        primary key (id)
    );


    alter table JIResource
        add column update_date timestamp;
    update JIResource
        set update_date = creation_date;
    alter table JIResource
        alter column update_date set not null;
        
    alter table JIResourceFolder
        add column update_date timestamp;
    update JIResourceFolder
        set update_date = creation_date;
    alter table JIResourceFolder
        alter column update_date set not null;

    alter table JITenant
        add column tenantAlias varchar(100);
    update JITenant
        set tenantAlias = tenantId;
    alter table JITenant
        alter column tenantAlias set not null;
        

    create index access_date_index on JIAccessEvent (event_date);

    create index access_upd_index on JIAccessEvent (updating);

    create index access_user_index on JIAccessEvent (user_id);

    create index access_res_index on JIAccessEvent (resource_id);

    alter table JIAccessEvent 
        add constraint FK47FB3CD732282198 
        foreign key (user_id) 
        references JIUser;

    alter table JIAccessEvent 
        add constraint FK47FB3CD7F254B53E 
        foreign key (resource_id) 
        references JIResource;


    create index res_type_index on JIAuditEvent (resource_type);

    create index event_type_index on JIAuditEvent (event_type);

    create index event_date_index on JIAuditEvent (event_date);

    create index tenant_id_index on JIAuditEvent (tenant_id);

    create index request_type_index on JIAuditEvent (request_type);

    create index resource_uri_index on JIAuditEvent (resource_uri);

    create index username_index on JIAuditEvent (username);

    alter table JIAuditEventProperty 
        add constraint FK3429FE136F667020 
        foreign key (audit_event_id) 
        references JIAuditEvent;

    alter table JIAuditEventPropertyArchive 
        add constraint FKD2940F2F637AC28A 
        foreign key (audit_event_id) 
        references JIAuditEventArchive;
