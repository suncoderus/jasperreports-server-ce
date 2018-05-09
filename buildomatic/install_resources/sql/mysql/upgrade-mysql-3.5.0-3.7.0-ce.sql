
    create table JIAccessEvent (
        id bigint not null auto_increment,
        user_id bigint not null,
        event_date datetime not null,
        resource_id bigint not null,
        updating bit not null,
        primary key (id)
    ) type=InnoDB;

    create table JIAuditEvent (
        id bigint not null auto_increment,
        username varchar(100),
        tenant_id varchar(100),
        event_date datetime not null,
        resource_uri varchar(250),
        resource_type varchar(250),
        event_type varchar(100) not null,
        request_type varchar(100) not null,
        primary key (id)
    ) type=InnoDB;

    create table JIAuditEventArchive (
        id bigint not null auto_increment,
        username varchar(100),
        tenant_id varchar(100),
        event_date datetime not null,
        resource_uri varchar(250),
        resource_type varchar(250),
        event_type varchar(100) not null,
        request_type varchar(100) not null,
        primary key (id)
    ) type=InnoDB;

    create table JIAuditEventProperty (
        id bigint not null auto_increment,
        property_type varchar(100) not null,
        value varchar(250),
        clob_value mediumtext,
        audit_event_id bigint not null,
        primary key (id)
    ) type=InnoDB;

    create table JIAuditEventPropertyArchive (
        id bigint not null auto_increment,
        property_type varchar(100) not null,
        value varchar(250),
        clob_value mediumtext,
        audit_event_id bigint not null,
        primary key (id)
    ) type=InnoDB;

    alter table JIResource add
        update_date datetime;
    update JIResource set
        update_date = creation_date;
    alter table JIResource modify
        update_date datetime not null;

    alter table JIResourceFolder add
        update_date datetime;
    update JIResourceFolder set
        update_date = creation_date;
    alter table JIResourceFolder modify
        update_date datetime not null;

    alter table JITenant add
        tenantAlias varchar(100);
    update JITenant set
        tenantAlias = tenantId;
    alter table JITenant modify
        tenantAlias varchar(100) not null;

    create index access_upd_index on JIAccessEvent (updating);

    create index access_res_index on JIAccessEvent (resource_id);

    create index access_user_index on JIAccessEvent (user_id);

    create index access_date_index on JIAccessEvent (event_date);

    alter table JIAccessEvent 
        add index FK47FB3CD732282198 (user_id), 
        add constraint FK47FB3CD732282198 
        foreign key (user_id) 
        references JIUser (id);

    alter table JIAccessEvent 
        add index FK47FB3CD7F254B53E (resource_id), 
        add constraint FK47FB3CD7F254B53E 
        foreign key (resource_id) 
        references JIResource (id);

    create index resource_uri_index on JIAuditEvent (resource_uri);

    create index request_type_index on JIAuditEvent (request_type);

    create index resource_type_index on JIAuditEvent (resource_type);

    create index event_date_index on JIAuditEvent (event_date);

    create index tenant_id_index on JIAuditEvent (tenant_id);

    create index event_type_index on JIAuditEvent (event_type);

    create index username_index on JIAuditEvent (username);

    alter table JIAuditEventProperty 
        add index FK3429FE136F667020 (audit_event_id), 
        add constraint FK3429FE136F667020 
        foreign key (audit_event_id) 
        references JIAuditEvent (id);

    alter table JIAuditEventPropertyArchive 
        add index FKD2940F2F637AC28A (audit_event_id), 
        add constraint FKD2940F2F637AC28A 
        foreign key (audit_event_id) 
        references JIAuditEventArchive (id);

