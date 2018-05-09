
    create table JIAccessEvent (
        id bigint not null auto_increment,
        user_id bigint not null,
        event_date datetime not null,
        resource_id bigint not null,
        updating bit not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIAwsDatasource (
        id bigint not null,
        accessKey varchar(100),
        secretKey varchar(100),
        roleARN varchar(100),
        region varchar(100) not null,
        dbName varchar(100) not null,
        dbInstanceIdentifier varchar(100) not null,
        dbService varchar(100) not null,
        primary key (id)
    ) ENGINE=InnoDB;

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

    create table JIBeanDatasource (
        id bigint not null,
        beanName varchar(100) not null,
        beanMethod varchar(100),
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIContentResource (
        id bigint not null,
        data longblob,
        file_type varchar(20),
        primary key (id)
    ) ENGINE=InnoDB;

    create table JICustomDatasource (
        id bigint not null,
        serviceClass varchar(250) not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JICustomDatasourceProperty (
        ds_id bigint not null,
        value varchar(1000),
        name varchar(200) not null,
        primary key (ds_id, name)
    ) ENGINE=InnoDB;

    create table JICustomDatasourceResource (
        ds_id bigint not null,
        resource_id bigint not null,
        name varchar(200) not null,
        primary key (ds_id, name)
    ) ENGINE=InnoDB;

    create table JIDataSnapshot (
        id bigint not null auto_increment,
        version integer not null,
        snapshot_date datetime,
        contents_id bigint not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIDataSnapshotContents (
        id bigint not null auto_increment,
        data longblob not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIDataSnapshotParameter (
        id bigint not null,
        parameter_value longblob,
        parameter_name varchar(100) not null,
        primary key (id, parameter_name)
    ) ENGINE=InnoDB;

    create table JIDataType (
        id bigint not null,
        type tinyint,
        maxLength integer,
        decimals integer,
        regularExpr varchar(255),
        minValue blob,
        max_value blob,
        strictMin bit,
        strictMax bit,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIFTPInfoProperties (
        repodest_id bigint not null,
        property_value varchar(250),
        property_name varchar(100) not null,
        primary key (repodest_id, property_name)
    ) ENGINE=InnoDB;

    create table JIFileResource (
        id bigint not null,
        data longblob,
        file_type varchar(20),
        reference bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIInputControl (
        id bigint not null,
        type tinyint,
        mandatory bit,
        readOnly bit,
        visible bit,
        data_type bigint,
        list_of_values bigint,
        list_query bigint,
        query_value_column varchar(200),
        defaultValue tinyblob,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIInputControlQueryColumn (
        input_control_id bigint not null,
        query_column varchar(200) not null,
        column_index integer not null,
        primary key (input_control_id, column_index)
    ) ENGINE=InnoDB;

    create table JIJNDIJdbcDatasource (
        id bigint not null,
        jndiName varchar(100) not null,
        timezone varchar(100),
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIJdbcDatasource (
        id bigint not null,
        driver varchar(100) not null,
        password varchar(250),
        connectionUrl varchar(500),
        username varchar(100),
        timezone varchar(100),
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIListOfValues (
        id bigint not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIListOfValuesItem (
        id bigint not null,
        label varchar(255),
        value longblob,
        idx integer not null,
        primary key (id, idx)
    ) ENGINE=InnoDB;

    create table JILogEvent (
        id bigint not null auto_increment,
        occurrence_date datetime not null,
        event_type tinyint not null,
        component varchar(100),
        message varchar(250) not null,
        resource_uri varchar(250),
        event_text mediumtext,
        event_data mediumblob,
        event_state tinyint,
        userId bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIMondrianConnection (
        id bigint not null,
        reportDataSource bigint,
        mondrianSchema bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIMondrianXMLADefinition (
        id bigint not null,
        catalog varchar(100) not null,
        mondrianConnection bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIObjectPermission (
        id bigint not null auto_increment,
        uri varchar(1000) not null,
        recipientobjectclass varchar(250),
        recipientobjectid bigint,
        permissionMask integer not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIOlapClientConnection (
        id bigint not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIOlapUnit (
        id bigint not null,
        olapClientConnection bigint,
        mdx_query mediumtext not null,
        view_options longblob,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIProfileAttribute (
        id bigint not null auto_increment,
        attrName varchar(255) not null,
        attrValue varchar(2000),
        description varchar(255),
        owner varchar(255),
        principalobjectclass varchar(255) not null,
        principalobjectid bigint not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIQuery (
        id bigint not null,
        dataSource bigint,
        query_language varchar(40) not null,
        sql_query mediumtext not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIReportAlertToAddress (
        alert_id bigint not null,
        to_address varchar(100) not null,
        to_address_idx integer not null,
        primary key (alert_id, to_address_idx)
    ) ENGINE=InnoDB;

    create table JIReportJob (
        id bigint not null auto_increment,
        version integer not null,
        owner bigint not null,
        label varchar(100) not null,
        description varchar(2000),
        creation_date datetime,
        report_unit_uri varchar(250) not null,
        scheduledResource bigint not null,
        job_trigger bigint not null,
        base_output_name varchar(100) not null,
        output_locale varchar(20),
        content_destination bigint,
        mail_notification bigint,
        alert bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIReportJobAlert (
        id bigint not null auto_increment,
        version integer not null,
        recipient tinyint not null,
        subject varchar(100),
        message_text varchar(2000),
        message_text_when_job_fails varchar(2000),
        job_state tinyint not null,
        including_stack_trace bit not null,
        including_report_job_info bit not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIReportJobCalendarTrigger (
        id bigint not null,
        minutes varchar(200) not null,
        hours varchar(80) not null,
        days_type tinyint not null,
        week_days varchar(20),
        month_days varchar(100),
        months varchar(40) not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIReportJobMail (
        id bigint not null auto_increment,
        version integer not null,
        subject varchar(100) not null,
        message varchar(2000),
        send_type tinyint not null,
        skip_empty bit not null,
        message_text_when_job_fails varchar(2000),
        inc_stktrc_when_job_fails bit not null,
        skip_notif_when_job_fails bit not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIReportJobMailRecipient (
        destination_id bigint not null,
        recipient_type tinyint not null,
        address varchar(100) not null,
        recipient_idx integer not null,
        primary key (destination_id, recipient_idx)
    ) ENGINE=InnoDB;

    create table JIReportJobOutputFormat (
        report_job_id bigint not null,
        output_format tinyint not null,
        primary key (report_job_id, output_format)
    ) ENGINE=InnoDB;

    create table JIReportJobParameter (
        job_id bigint not null,
        parameter_value longblob,
        parameter_name varchar(255) not null,
        primary key (job_id, parameter_name)
    ) ENGINE=InnoDB;

    create table JIReportJobRepoDest (
        id bigint not null auto_increment,
        version integer not null,
        folder_uri varchar(250),
        sequential_filenames bit not null,
        overwrite_files bit not null,
        save_to_repository bit not null,
        output_description varchar(250),
        timestamp_pattern varchar(250),
        using_def_rpt_opt_folder_uri bit not null,
        output_local_folder varchar(250),
        user_name varchar(50),
        password varchar(250),
        server_name varchar(150),
        folder_path varchar(250),
        ssh_private_key bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIReportJobSimpleTrigger (
        id bigint not null,
        occurrence_count integer not null,
        recurrence_interval integer,
        recurrence_interval_unit tinyint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIReportJobTrigger (
        id bigint not null auto_increment,
        version integer not null,
        timezone varchar(40),
        start_type tinyint not null,
        start_date datetime,
        end_date datetime,
        calendar_name varchar(50),
        misfire_instruction integer not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIReportThumbnail (
        id bigint not null auto_increment,
        user_id bigint not null,
        resource_id bigint not null,
        thumbnail longblob not null,
        primary key (id),
        unique (user_id, resource_id)
    ) ENGINE=InnoDB;

    create table JIReportUnit (
        id bigint not null,
        reportDataSource bigint,
        query bigint,
        mainReport bigint,
        controlrenderer varchar(100),
        reportrenderer varchar(100),
        promptcontrols bit,
        controlslayout tinyint,
        data_snapshot_id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIReportUnitInputControl (
        report_unit_id bigint not null,
        input_control_id bigint not null,
        control_index integer not null,
        primary key (report_unit_id, control_index)
    ) ENGINE=InnoDB;

    create table JIReportUnitResource (
        report_unit_id bigint not null,
        resource_id bigint not null,
        resource_index integer not null,
        primary key (report_unit_id, resource_index)
    ) ENGINE=InnoDB;

    create table JIRepositoryCache (
        id bigint not null auto_increment,
        uri varchar(250) not null,
        cache_name varchar(20) not null,
        data longblob,
        version integer not null,
        version_date datetime not null,
        item_reference bigint,
        primary key (id),
        unique (uri, cache_name)
    ) ENGINE=InnoDB;

    create table JIResource (
        id bigint not null auto_increment,
        version integer not null,
        name varchar(200) not null,
        parent_folder bigint not null,
        childrenFolder bigint,
        label varchar(200) not null,
        description varchar(250),
        resourceType varchar(255) not null,
        creation_date datetime not null,
        update_date datetime not null,
        primary key (id),
        unique (name, parent_folder)
    ) ENGINE=InnoDB;

    create table JIResourceFolder (
        id bigint not null auto_increment,
        version integer not null,
        uri varchar(250) not null,
        hidden bit,
        name varchar(200) not null,
        label varchar(200) not null,
        description varchar(250),
        parent_folder bigint,
        creation_date datetime not null,
        update_date datetime not null,
        primary key (id),
        unique (uri)
    ) ENGINE=InnoDB;

    create table JIRole (
        id bigint not null auto_increment,
        rolename varchar(100) not null,
        tenantId bigint not null,
        externallyDefined bit,
        primary key (id),
        unique (rolename, tenantId)
    ) ENGINE=InnoDB;

    create table JITenant (
        id bigint not null auto_increment,
        tenantId varchar(100) not null,
        tenantAlias varchar(100) not null,
        parentId bigint,
        tenantName varchar(100) not null,
        tenantDesc varchar(250),
        tenantNote varchar(250),
        tenantUri varchar(250) not null,
        tenantFolderUri varchar(250) not null,
        theme varchar(250),
        primary key (id),
        unique (tenantId)
    ) ENGINE=InnoDB;

    create table JIUser (
        id bigint not null auto_increment,
        username varchar(100) not null,
        tenantId bigint not null,
        fullname varchar(100) not null,
        emailAddress varchar(100),
        password varchar(250),
        externallyDefined bit,
        enabled bit,
        previousPasswordChangeTime datetime,
        primary key (id),
        unique (username, tenantId)
    ) ENGINE=InnoDB;

    create table JIUserRole (
        roleId bigint not null,
        userId bigint not null,
        primary key (userId, roleId)
    ) ENGINE=InnoDB;

    create table JIVirtualDataSourceUriMap (
        virtualDS_id bigint not null,
        resource_id bigint not null,
        data_source_name varchar(200) not null,
        primary key (virtualDS_id, data_source_name)
    ) ENGINE=InnoDB;

    create table JIVirtualDatasource (
        id bigint not null,
        timezone varchar(100),
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIXMLAConnection (
        id bigint not null,
        catalog varchar(100) not null,
        username varchar(100) not null,
        password varchar(250) not null,
        datasource varchar(100) not null,
        uri varchar(100) not null,
        primary key (id)
    ) ENGINE=InnoDB;

    create index access_date_index on JIAccessEvent (event_date);

    create index access_upd_index on JIAccessEvent (updating);

    create index access_user_index on JIAccessEvent (user_id);

    create index access_res_index on JIAccessEvent (resource_id);

    alter table JIAccessEvent 
        add index FK47FB3CD732282198 (user_id), 
        add constraint FK47FB3CD732282198 
        foreign key (user_id) 
        references JIUser (id);

    alter table JIAccessEvent 
        add index FK47FB3CD7F254B53E (resource_id), 
        add constraint FK47FB3CD7F254B53E 
        foreign key (resource_id) 
        references JIResource (id) 
        on delete cascade;

    alter table JIAwsDatasource 
        add index FK6085542387E4472B (id), 
        add constraint FK6085542387E4472B 
        foreign key (id) 
        references JIJdbcDatasource (id);

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

    alter table JIBeanDatasource 
        add index FK674BF34A8BF376D (id), 
        add constraint FK674BF34A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIContentResource 
        add index FKE466FC68A8BF376D (id), 
        add constraint FKE466FC68A8BF376D 
        foreign key (id) 
        references JIResource (id);

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

    alter table JICustomDatasourceResource 
        add index FKDF845123858A89D1 (ds_id), 
        add constraint FKDF845123858A89D1 
        foreign key (ds_id) 
        references JICustomDatasource (id);

    alter table JICustomDatasourceResource 
        add index FKDF845123F254B53E (resource_id), 
        add constraint FKDF845123F254B53E 
        foreign key (resource_id) 
        references JIResource (id);

    alter table JIDataSnapshotParameter 
        add index id_fk_idx (id), 
        add constraint id_fk_idx 
        foreign key (id) 
        references JIDataSnapshot (id);

    alter table JIDataType 
        add index FK533BCC63A8BF376D (id), 
        add constraint FK533BCC63A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIFTPInfoProperties 
        add index FK6BD68B04D5FA3F0A (repodest_id), 
        add constraint FK6BD68B04D5FA3F0A 
        foreign key (repodest_id) 
        references JIReportJobRepoDest (id);

    alter table JIFileResource 
        add index FKF75B5889A8BF376D (id), 
        add constraint FKF75B5889A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIFileResource 
        add index FKF75B58895A0C539 (reference), 
        add constraint FKF75B58895A0C539 
        foreign key (reference) 
        references JIFileResource (id);

    alter table JIInputControl 
        add index FKCAC6A512120E06F7 (data_type), 
        add constraint FKCAC6A512120E06F7 
        foreign key (data_type) 
        references JIDataType (id);

    alter table JIInputControl 
        add index FKCAC6A512A8BF376D (id), 
        add constraint FKCAC6A512A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIInputControl 
        add index FKCAC6A51262A86F04 (list_of_values), 
        add constraint FKCAC6A51262A86F04 
        foreign key (list_of_values) 
        references JIListOfValues (id);

    alter table JIInputControl 
        add index FKCAC6A512B37DB6EB (list_query), 
        add constraint FKCAC6A512B37DB6EB 
        foreign key (list_query) 
        references JIQuery (id);

    alter table JIInputControlQueryColumn 
        add index FKE436A5CCE7922149 (input_control_id), 
        add constraint FKE436A5CCE7922149 
        foreign key (input_control_id) 
        references JIInputControl (id);

    alter table JIJNDIJdbcDatasource 
        add index FK7F9DA248A8BF376D (id), 
        add constraint FK7F9DA248A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIJdbcDatasource 
        add index FKC8BDFCBFA8BF376D (id), 
        add constraint FKC8BDFCBFA8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIListOfValues 
        add index FK4E86A776A8BF376D (id), 
        add constraint FK4E86A776A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIListOfValuesItem 
        add index FKD37CEBA993F0E1F6 (id), 
        add constraint FKD37CEBA993F0E1F6 
        foreign key (id) 
        references JIListOfValues (id);

    alter table JILogEvent 
        add index FK5F32081591865AF (userId), 
        add constraint FK5F32081591865AF 
        foreign key (userId) 
        references JIUser (id);

    alter table JIMondrianConnection 
        add index FK4FF53B191D51BFAD (id), 
        add constraint FK4FF53B191D51BFAD 
        foreign key (id) 
        references JIOlapClientConnection (id);

    alter table JIMondrianConnection 
        add index FK4FF53B19324CFECB (reportDataSource), 
        add constraint FK4FF53B19324CFECB 
        foreign key (reportDataSource) 
        references JIResource (id);

    alter table JIMondrianConnection 
        add index FK4FF53B19C495A60B (mondrianSchema), 
        add constraint FK4FF53B19C495A60B 
        foreign key (mondrianSchema) 
        references JIFileResource (id);

    alter table JIMondrianXMLADefinition 
        add index FK313B2AB8A8BF376D (id), 
        add constraint FK313B2AB8A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIMondrianXMLADefinition 
        add index FK313B2AB8DC098B1 (mondrianConnection), 
        add constraint FK313B2AB8DC098B1 
        foreign key (mondrianConnection) 
        references JIMondrianConnection (id);

    alter table JIOlapClientConnection 
        add index FK3CA3B7D4A8BF376D (id), 
        add constraint FK3CA3B7D4A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIOlapUnit 
        add index FKF034DCCFA8BF376D (id), 
        add constraint FKF034DCCFA8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIOlapUnit 
        add index FKF034DCCF8F542247 (olapClientConnection), 
        add constraint FKF034DCCF8F542247 
        foreign key (olapClientConnection) 
        references JIOlapClientConnection (id);

    alter table JIQuery 
        add index FKCBCB0EC9A8BF376D (id), 
        add constraint FKCBCB0EC9A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIQuery 
        add index FKCBCB0EC92B329A97 (dataSource), 
        add constraint FKCBCB0EC92B329A97 
        foreign key (dataSource) 
        references JIResource (id);

    alter table JIReportAlertToAddress 
        add index FKC4E3713022FA4CBA (alert_id), 
        add constraint FKC4E3713022FA4CBA 
        foreign key (alert_id) 
        references JIReportJobAlert (id);

    alter table JIReportJob 
        add index FK156F5F6AE4D73E35 (mail_notification), 
        add constraint FK156F5F6AE4D73E35 
        foreign key (mail_notification) 
        references JIReportJobMail (id);

    alter table JIReportJob 
        add index FK156F5F6AC83ABB38 (alert), 
        add constraint FK156F5F6AC83ABB38 
        foreign key (alert) 
        references JIReportJobAlert (id);

    alter table JIReportJob 
        add index FK156F5F6A9EEC902C (content_destination), 
        add constraint FK156F5F6A9EEC902C 
        foreign key (content_destination) 
        references JIReportJobRepoDest (id);

    alter table JIReportJob 
        add index FK156F5F6A74D2696E (job_trigger), 
        add constraint FK156F5F6A74D2696E 
        foreign key (job_trigger) 
        references JIReportJobTrigger (id);

    alter table JIReportJob 
        add index FK156F5F6AFF0F459F (scheduledResource), 
        add constraint FK156F5F6AFF0F459F 
        foreign key (scheduledResource) 
        references JIResource (id);

    alter table JIReportJob 
        add index FK156F5F6A4141263C (owner), 
        add constraint FK156F5F6A4141263C 
        foreign key (owner) 
        references JIUser (id);

    alter table JIReportJobCalendarTrigger 
        add index FKC374C7D0D2B2EB53 (id), 
        add constraint FKC374C7D0D2B2EB53 
        foreign key (id) 
        references JIReportJobTrigger (id);

    alter table JIReportJobMailRecipient 
        add index FKBB6DB6D880001AAE (destination_id), 
        add constraint FKBB6DB6D880001AAE 
        foreign key (destination_id) 
        references JIReportJobMail (id);

    alter table JIReportJobOutputFormat 
        add index FKB42A5CE2C3389A8 (report_job_id), 
        add constraint FKB42A5CE2C3389A8 
        foreign key (report_job_id) 
        references JIReportJob (id);

    alter table JIReportJobParameter 
        add index FKEAC52B5F2EC643D (job_id), 
        add constraint FKEAC52B5F2EC643D 
        foreign key (job_id) 
        references JIReportJob (id);

    alter table JIReportJobRepoDest 
        add index FKEA477EBE3C5B87D0 (ssh_private_key), 
        add constraint FKEA477EBE3C5B87D0 
        foreign key (ssh_private_key) 
        references JIResource (id);

    alter table JIReportJobSimpleTrigger 
        add index FKB9337C5CD2B2EB53 (id), 
        add constraint FKB9337C5CD2B2EB53 
        foreign key (id) 
        references JIReportJobTrigger (id);

    alter table JIReportThumbnail 
        add index FKFDB3DED932282198 (user_id), 
        add constraint FKFDB3DED932282198 
        foreign key (user_id) 
        references JIUser (id) 
        on delete cascade;

    alter table JIReportThumbnail 
        add index FKFDB3DED9F254B53E (resource_id), 
        add constraint FKFDB3DED9F254B53E 
        foreign key (resource_id) 
        references JIResource (id) 
        on delete cascade;

    alter table JIReportUnit 
        add index FK98818B77A8BF376D (id), 
        add constraint FK98818B77A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIReportUnit 
        add index FK98818B778FDA11CC (query), 
        add constraint FK98818B778FDA11CC 
        foreign key (query) 
        references JIQuery (id);

    alter table JIReportUnit 
        add index FK98818B77324CFECB (reportDataSource), 
        add constraint FK98818B77324CFECB 
        foreign key (reportDataSource) 
        references JIResource (id);

    alter table JIReportUnit 
        add index FK98818B778C8DF21B (mainReport), 
        add constraint FK98818B778C8DF21B 
        foreign key (mainReport) 
        references JIFileResource (id);

    alter table JIReportUnitInputControl 
        add index FK5FBE934AE7922149 (input_control_id), 
        add constraint FK5FBE934AE7922149 
        foreign key (input_control_id) 
        references JIInputControl (id);

    alter table JIReportUnitInputControl 
        add index FK5FBE934AA6A48880 (report_unit_id), 
        add constraint FK5FBE934AA6A48880 
        foreign key (report_unit_id) 
        references JIReportUnit (id);

    alter table JIReportUnitResource 
        add index FK8B1C4CA5A6A48880 (report_unit_id), 
        add constraint FK8B1C4CA5A6A48880 
        foreign key (report_unit_id) 
        references JIReportUnit (id);

    alter table JIReportUnitResource 
        add index FK8B1C4CA5865B10DA (resource_id), 
        add constraint FK8B1C4CA5865B10DA 
        foreign key (resource_id) 
        references JIFileResource (id);

    alter table JIRepositoryCache 
        add index FKE7338B19E7C5A6 (item_reference), 
        add constraint FKE7338B19E7C5A6 
        foreign key (item_reference) 
        references JIRepositoryCache (id);

    create index resource_type_index on JIResource (resourceType);

    alter table JIResource 
        add index FKD444826DA58002DF (childrenFolder), 
        add constraint FKD444826DA58002DF 
        foreign key (childrenFolder) 
        references JIResourceFolder (id);

    alter table JIResource 
        add index FKD444826DA08E2155 (parent_folder), 
        add constraint FKD444826DA08E2155 
        foreign key (parent_folder) 
        references JIResourceFolder (id);

    alter table JIResourceFolder 
        add index FK7F24453BA08E2155 (parent_folder), 
        add constraint FK7F24453BA08E2155 
        foreign key (parent_folder) 
        references JIResourceFolder (id);

    alter table JIRole 
        add index FK82724655E415AC2D (tenantId), 
        add constraint FK82724655E415AC2D 
        foreign key (tenantId) 
        references JITenant (id);

    alter table JITenant 
        add index FKB1D7B2C97803CC2D (parentId), 
        add constraint FKB1D7B2C97803CC2D 
        foreign key (parentId) 
        references JITenant (id);

    alter table JIUser 
        add index FK8273B1AAE415AC2D (tenantId), 
        add constraint FK8273B1AAE415AC2D 
        foreign key (tenantId) 
        references JITenant (id);

    alter table JIUserRole 
        add index FKD8B5C1403C31045 (roleId), 
        add constraint FKD8B5C1403C31045 
        foreign key (roleId) 
        references JIRole (id);

    alter table JIUserRole 
        add index FKD8B5C14091865AF (userId), 
        add constraint FKD8B5C14091865AF 
        foreign key (userId) 
        references JIUser (id);

    alter table JIVirtualDataSourceUriMap 
        add index FK4A6CCE019E600E20 (virtualDS_id), 
        add constraint FK4A6CCE019E600E20 
        foreign key (virtualDS_id) 
        references JIVirtualDatasource (id);

    alter table JIVirtualDataSourceUriMap 
        add index FK4A6CCE01F254B53E (resource_id), 
        add constraint FK4A6CCE01F254B53E 
        foreign key (resource_id) 
        references JIResource (id);

    alter table JIVirtualDatasource 
        add index FK30E55631A8BF376D (id), 
        add constraint FK30E55631A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIXMLAConnection 
        add index FK94C688A71D51BFAD (id), 
        add constraint FK94C688A71D51BFAD 
        foreign key (id) 
        references JIOlapClientConnection (id);

    create index JILogEvent_userId_index on JILogEvent (userId);

    create index JIReportJob_alert_index on JIReportJob (alert);

    create index idx25_content_destination_idx on JIReportJob (content_destination);

    create index JIReportJob_job_trigger_index on JIReportJob (job_trigger);

    create index idx26_mail_notification_idx on JIReportJob (mail_notification);

    create index JIReportJob_owner_index on JIReportJob (owner);

    create index idx24_alert_id_idx on JIReportAlertToAddress (alert_id);

    create index idx27_destination_id_idx on JIReportJobMailRecipient (destination_id);

    create index idx14_repodest_id_idx on JIFTPInfoProperties (repodest_id);

    create index idx34_item_reference_idx on JIRepositoryCache (item_reference);

    create index idx35_parent_folder_idx on JIResourceFolder (parent_folder);

    create index JIResourceFolder_version_index on JIResourceFolder (version);

    create index idx28_resource_id_idx on JIReportThumbnail (resource_id);

    create index JIResource_childrenFolder_idx on JIResource (childrenFolder);

    create index JIResource_parent_folder_index on JIResource (parent_folder);

    create index idx36_resource_id_idx on JIVirtualDataSourceUriMap (resource_id);

    create index uri_index on JIObjectPermission (uri(255));

    create index idx21_recipientobjclass_idx on JIObjectPermission (recipientobjectclass);

    create index idx22_recipientobjid_idx on JIObjectPermission (recipientobjectid);

    create index JIRole_tenantId_index on JIRole (tenantId);

    create index JIUserRole_roleId_index on JIUserRole (roleId);

    create index JIUserRole_userId_index on JIUserRole (userId);

    create index JITenant_parentId_index on JITenant (parentId);

    create index JIUser_tenantId_index on JIUser (tenantId);

    create index JIInputControl_data_type_index on JIInputControl (data_type);

    create index JIInputCtrl_list_of_values_idx on JIInputControl (list_of_values);

    create index JIInputControl_list_query_idx on JIInputControl (list_query);

    create index idx15_input_ctrl_id_idx on JIInputControlQueryColumn (input_control_id);

    create index JIFileResource_reference_index on JIFileResource (reference);

    create index JIQuery_dataSource_index on JIQuery (dataSource);

    create index JIReportUnit_mainReport_index on JIReportUnit (mainReport);

    create index JIReportUnit_query_index on JIReportUnit (query);

    create index idx29_reportDataSource_idx on JIReportUnit (reportDataSource);

    create index idx30_input_ctrl_id_idx on JIReportUnitInputControl (input_control_id);

    create index idx31_report_unit_id_idx on JIReportUnitInputControl (report_unit_id);

    create index idx32_report_unit_id_idx on JIReportUnitResource (report_unit_id);

    create index idx33_resource_id_idx on JIReportUnitResource (resource_id);

    create index idx16_mondrianSchema_idx on JIMondrianConnection (mondrianSchema);

    create index idx17_reportDataSource_idx on JIMondrianConnection (reportDataSource);

    create index idx23_olapClientConnection_idx on JIOlapUnit (olapClientConnection);

    create index idx20_mondrianConnection_idx on JIMondrianXMLADefinition (mondrianConnection);

    create index idxA1_resource_id_idx on JICustomDatasourceResource (resource_id);
