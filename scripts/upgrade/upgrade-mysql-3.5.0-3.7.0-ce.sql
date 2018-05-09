    create table JIAccessEvent (
        id bigint not null auto_increment,
        user_id bigint not null,
        event_date datetime not null,
        resource_id bigint not null,
        updating bit not null,
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