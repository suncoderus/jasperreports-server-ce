
    create table JIBeanDatasource (
        id bigint not null,
        beanName varchar(100) not null,
        beanMethod varchar(100),
        primary key (id)
    ) type=InnoDB;

    create table JIContentResource (
        id bigint not null,
        data longblob,
        file_type varchar(20),
        primary key (id)
    ) type=InnoDB;

    create table JICustomDatasource (
        id bigint not null,
        serviceClass varchar(250) not null,
        primary key (id)
    ) type=InnoDB;

    create table JICustomDatasourceProperty (
        ds_id bigint not null,
        value text,
        name varchar(100) not null,
        primary key (ds_id, name)
    ) type=InnoDB;

    create table JIDataType (
        id bigint not null,
        type tinyint,
        maxLength integer,
        decimals integer,
        regularExpr varchar(255),
        minValue tinyblob,
        maxValue tinyblob,
        strictMin bit,
        strictMax bit,
        primary key (id)
    ) type=InnoDB;

    create table JIFileResource (
        id bigint not null,
        data longblob,
        file_type varchar(20),
        reference bigint,
        primary key (id)
    ) type=InnoDB;

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
    ) type=InnoDB;

    create table JIInputControlQueryColumn (
        input_control_id bigint not null,
        query_column varchar(200) not null,
        column_index integer not null,
        primary key (input_control_id, column_index)
    ) type=InnoDB;

    create table JIJNDIJdbcDatasource (
        id bigint not null,
        jndiName varchar(100) not null,
        timezone varchar(100),
        primary key (id)
    ) type=InnoDB;

    create table JIJdbcDatasource (
        id bigint not null,
        driver varchar(100) not null,
        password varchar(100),
        connectionUrl text,
        username varchar(100),
        timezone varchar(100),
        primary key (id)
    ) type=InnoDB;

    create table JIListOfValues (
        id bigint not null,
        primary key (id)
    ) type=InnoDB;

    create table JIListOfValuesItem (
        id bigint not null,
        label varchar(255),
        value tinyblob,
        idx integer not null,
        primary key (id, idx)
    ) type=InnoDB;

    create table JILogEvent (
        id bigint not null auto_increment,
        occurrence_date datetime not null,
        event_type tinyint not null,
        username varchar(100),
        component varchar(100),
        message varchar(250) not null,
        resource_uri varchar(250),
        event_text mediumtext,
        event_data mediumblob,
        event_state tinyint,
        primary key (id)
    ) type=InnoDB;

    create table JIMondrianConnection (
        id bigint not null,
        reportDataSource bigint,
        mondrianSchema bigint,
        primary key (id)
    ) type=InnoDB;

    create table JIMondrianXMLADefinition (
        id bigint not null,
        catalog varchar(100) not null,
        mondrianConnection bigint,
        primary key (id)
    ) type=InnoDB;

    create table JIObjectPermission (
        id bigint not null auto_increment,
        uri varchar(255) not null,
        recipientobjectclass varchar(100),
        recipientobjectid bigint,
        permissionMask integer not null,
        primary key (id)
    ) type=InnoDB;

    create table JIOlapClientConnection (
        id bigint not null,
        primary key (id)
    ) type=InnoDB;

    create table JIOlapUnit (
        id bigint not null,
        olapClientConnection bigint,
        mdx_query text not null,
        view_options longblob,
        primary key (id)
    ) type=InnoDB;

    create table JIProfileAttribute (
        id bigint not null auto_increment,
        attrName varchar(255) not null,
        attrValue varchar(255) not null,
        principalobjectclass varchar(255) not null,
        principalobjectid bigint not null,
        primary key (id)
    ) type=InnoDB;

    create table JIQuery (
        id bigint not null,
        dataSource bigint,
        query_language varchar(40) not null,
        sql_query text not null,
        primary key (id)
    ) type=InnoDB;

    create table JIReportJob (
        id bigint not null auto_increment,
        version integer not null,
        username varchar(100) not null,
        label varchar(100) not null,
        description text,
        report_unit_uri varchar(200) not null,
        job_trigger bigint not null,
        base_output_name varchar(100) not null,
        output_locale varchar(20),
        content_destination bigint,
        mail_notification bigint,
        primary key (id)
    ) type=InnoDB;

    create table JIReportJobCalendarTrigger (
        id bigint not null,
        minutes varchar(200) not null,
        hours varchar(80) not null,
        days_type tinyint not null,
        week_days varchar(20),
        month_days varchar(100),
        months varchar(40) not null,
        primary key (id)
    ) type=InnoDB;

    create table JIReportJobMail (
        id bigint not null auto_increment,
        version integer not null,
        subject varchar(100) not null,
        message text not null,
        send_type tinyint not null,
        skip_empty bit not null,
        primary key (id)
    ) type=InnoDB;

    create table JIReportJobMailRecipient (
        destination_id bigint not null,
        recipient_type tinyint not null,
        address varchar(100) not null,
        recipient_idx integer not null,
        primary key (destination_id, recipient_idx)
    ) type=InnoDB;

    create table JIReportJobOutputFormat (
        report_job_id bigint not null,
        output_format tinyint not null,
        primary key (report_job_id, output_format)
    ) type=InnoDB;

    create table JIReportJobParameter (
        job_id bigint not null,
        parameter_value blob,
        parameter_name varchar(100) not null,
        primary key (job_id, parameter_name)
    ) type=InnoDB;

    create table JIReportJobRepoDest (
        id bigint not null auto_increment,
        version integer not null,
        folder_uri varchar(200) not null,
        sequential_filenames bit not null,
        overwrite_files bit not null,
        output_description varchar(100),
        timestamp_pattern varchar(250),
        primary key (id)
    ) type=InnoDB;

    create table JIReportJobSimpleTrigger (
        id bigint not null,
        occurrence_count integer not null,
        recurrence_interval integer,
        recurrence_interval_unit tinyint,
        primary key (id)
    ) type=InnoDB;

    create table JIReportJobTrigger (
        id bigint not null auto_increment,
        version integer not null,
        timezone varchar(40),
        start_type tinyint not null,
        start_date datetime,
        end_date datetime,
        primary key (id)
    ) type=InnoDB;

    create table JIReportUnit (
        id bigint not null,
        reportDataSource bigint,
        query bigint,
        mainReport bigint,
        controlrenderer varchar(100),
        reportrenderer varchar(100),
        promptcontrols bit,
        controlslayout tinyint,
        primary key (id)
    ) type=InnoDB;

    create table JIReportUnitInputControl (
        report_unit_id bigint not null,
        input_control_id bigint not null,
        control_index integer not null,
        primary key (report_unit_id, control_index)
    ) type=InnoDB;

    create table JIReportUnitResource (
        report_unit_id bigint not null,
        resource_id bigint not null,
        resource_index integer not null,
        primary key (report_unit_id, resource_index)
    ) type=InnoDB;

    create table JIRepositoryCache (
        id bigint not null auto_increment,
        uri varchar(200) not null,
        cache_name varchar(20) not null,
        data longblob,
        version integer not null,
        version_date datetime not null,
        item_reference bigint,
        primary key (id),
        unique (uri, cache_name)
    ) type=InnoDB;

    create table JIResource (
        id bigint not null auto_increment,
        version integer not null,
        name varchar(100) not null,
        parent_folder bigint not null,
        childrenFolder bigint,
        label varchar(100) not null,
        description varchar(250),
        creation_date datetime not null,
        primary key (id),
        unique (name, parent_folder)
    ) type=InnoDB;

    create table JIResourceFolder (
        id bigint not null auto_increment,
        version integer not null,
        uri varchar(250) not null,
        hidden bit,
        name varchar(100) not null,
        label varchar(100) not null,
        description varchar(250),
        parent_folder bigint,
        creation_date datetime not null,
        primary key (id),
        unique (uri)
    ) type=InnoDB;

    create table JIRole (
        id bigint not null auto_increment,
        rolename varchar(100) not null unique,
        externallyDefined bit,
        primary key (id)
    ) type=InnoDB;

    create table JIUser (
        id bigint not null auto_increment,
        username varchar(100) not null unique,
        fullname varchar(100) not null,
        emailAddress varchar(100),
        password varchar(100),
        externallyDefined bit,
        enabled bit,
        previousPasswordChangeTime datetime,
        primary key (id)
    ) type=InnoDB;

    create table JIUserRole (
        roleId bigint not null,
        userId bigint not null,
        primary key (userId, roleId)
    ) type=InnoDB;

    create table JIXMLAConnection (
        id bigint not null,
        catalog varchar(100) not null,
        username varchar(100) not null,
        password varchar(100) not null,
        datasource varchar(100) not null,
        uri varchar(100) not null,
        primary key (id)
    ) type=InnoDB;

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

    alter table JIDataType 
        add index FK533BCC63A8BF376D (id), 
        add constraint FK533BCC63A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIFileResource 
        add index FKF75B58895A0C539 (reference), 
        add constraint FKF75B58895A0C539 
        foreign key (reference) 
        references JIFileResource (id);

    alter table JIFileResource 
        add index FKF75B5889A8BF376D (id), 
        add constraint FKF75B5889A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIInputControl 
        add index FKCAC6A512120E06F7 (data_type), 
        add constraint FKCAC6A512120E06F7 
        foreign key (data_type) 
        references JIDataType (id);

    alter table JIInputControl 
        add index FKCAC6A512B37DB6EB (list_query), 
        add constraint FKCAC6A512B37DB6EB 
        foreign key (list_query) 
        references JIQuery (id);

    alter table JIInputControl 
        add index FKCAC6A51262A86F04 (list_of_values), 
        add constraint FKCAC6A51262A86F04 
        foreign key (list_of_values) 
        references JIListOfValues (id);

    alter table JIInputControl 
        add index FKCAC6A512A8BF376D (id), 
        add constraint FKCAC6A512A8BF376D 
        foreign key (id) 
        references JIResource (id);

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

    alter table JIMondrianConnection 
        add index FK4FF53B19C495A60B (mondrianSchema), 
        add constraint FK4FF53B19C495A60B 
        foreign key (mondrianSchema) 
        references JIFileResource (id);

    alter table JIMondrianConnection 
        add index FK4FF53B19324CFECB (reportDataSource), 
        add constraint FK4FF53B19324CFECB 
        foreign key (reportDataSource) 
        references JIResource (id);

    alter table JIMondrianConnection 
        add index FK4FF53B191D51BFAD (id), 
        add constraint FK4FF53B191D51BFAD 
        foreign key (id) 
        references JIOlapClientConnection (id);

    alter table JIMondrianXMLADefinition 
        add index FK313B2AB8DC098B1 (mondrianConnection), 
        add constraint FK313B2AB8DC098B1 
        foreign key (mondrianConnection) 
        references JIMondrianConnection (id);

    alter table JIMondrianXMLADefinition 
        add index FK313B2AB8A8BF376D (id), 
        add constraint FK313B2AB8A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIOlapClientConnection 
        add index FK3CA3B7D4A8BF376D (id), 
        add constraint FK3CA3B7D4A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIOlapUnit 
        add index FKF034DCCF8F542247 (olapClientConnection), 
        add constraint FKF034DCCF8F542247 
        foreign key (olapClientConnection) 
        references JIOlapClientConnection (id);

    alter table JIOlapUnit 
        add index FKF034DCCFA8BF376D (id), 
        add constraint FKF034DCCFA8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIQuery 
        add index FKCBCB0EC92B329A97 (dataSource), 
        add constraint FKCBCB0EC92B329A97 
        foreign key (dataSource) 
        references JIResource (id);

    alter table JIQuery 
        add index FKCBCB0EC9A8BF376D (id), 
        add constraint FKCBCB0EC9A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIReportJob 
        add index FK156F5F6AE4D73E35 (mail_notification), 
        add constraint FK156F5F6AE4D73E35 
        foreign key (mail_notification) 
        references JIReportJobMail (id);

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

    alter table JIReportJobSimpleTrigger 
        add index FKB9337C5CD2B2EB53 (id), 
        add constraint FKB9337C5CD2B2EB53 
        foreign key (id) 
        references JIReportJobTrigger (id);

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

    alter table JIReportUnit 
        add index FK98818B77A8BF376D (id), 
        add constraint FK98818B77A8BF376D 
        foreign key (id) 
        references JIResource (id);

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
        add index FK8B1C4CA5865B10DA (resource_id), 
        add constraint FK8B1C4CA5865B10DA 
        foreign key (resource_id) 
        references JIFileResource (id);

    alter table JIReportUnitResource 
        add index FK8B1C4CA5A6A48880 (report_unit_id), 
        add constraint FK8B1C4CA5A6A48880 
        foreign key (report_unit_id) 
        references JIReportUnit (id);

    alter table JIRepositoryCache 
        add index FKE7338B19E7C5A6 (item_reference), 
        add constraint FKE7338B19E7C5A6 
        foreign key (item_reference) 
        references JIRepositoryCache (id);

    alter table JIResource 
        add index FKD444826DA08E2155 (parent_folder), 
        add constraint FKD444826DA08E2155 
        foreign key (parent_folder) 
        references JIResourceFolder (id);

    alter table JIResource 
        add index FKD444826DA58002DF (childrenFolder), 
        add constraint FKD444826DA58002DF 
        foreign key (childrenFolder) 
        references JIResourceFolder (id);

    alter table JIResourceFolder 
        add index FK7F24453BA08E2155 (parent_folder), 
        add constraint FK7F24453BA08E2155 
        foreign key (parent_folder) 
        references JIResourceFolder (id);

    alter table JIUserRole 
        add index FKD8B5C14091865AF (userId), 
        add constraint FKD8B5C14091865AF 
        foreign key (userId) 
        references JIUser (id);

    alter table JIUserRole 
        add index FKD8B5C1403C31045 (roleId), 
        add constraint FKD8B5C1403C31045 
        foreign key (roleId) 
        references JIRole (id);

    alter table JIXMLAConnection 
        add index FK94C688A71D51BFAD (id), 
        add constraint FK94C688A71D51BFAD 
        foreign key (id) 
        references JIOlapClientConnection (id);


-- 
-- Quartz Tables
--
    
    CREATE TABLE QRTZ_JOB_DETAILS(
    JOB_NAME VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    JOB_CLASS_NAME VARCHAR(128) NOT NULL,
    IS_DURABLE VARCHAR(1) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    IS_STATEFUL VARCHAR(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_JOB_LISTENERS (
    JOB_NAME VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    JOB_LISTENER VARCHAR(80) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    INDEX (JOB_NAME, JOB_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_TRIGGERS (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    JOB_NAME VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT(13) NOT NULL,
    END_TIME BIGINT(13) NULL,
    CALENDAR_NAME VARCHAR(80) NULL,
    MISFIRE_INSTR SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    INDEX (JOB_NAME, JOB_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    REPEAT_COUNT BIGINT(7) NOT NULL,
    REPEAT_INTERVAL BIGINT(12) NOT NULL,
    TIMES_TRIGGERED BIGINT(7) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    INDEX (TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_CRON_TRIGGERS (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    CRON_EXPRESSION VARCHAR(80) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    INDEX (TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_BLOB_TRIGGERS (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    INDEX (TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_TRIGGER_LISTENERS (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    TRIGGER_LISTENER VARCHAR(80) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    INDEX (TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_CALENDARS (
    CALENDAR_NAME VARCHAR(80) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (CALENDAR_NAME))
    TYPE=InnoDB;
    CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    PRIMARY KEY (TRIGGER_GROUP))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_FIRED_TRIGGERS (
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    IS_VOLATILE VARCHAR(1) NOT NULL,
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    FIRED_TIME BIGINT(13) NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(80) NULL,
    JOB_GROUP VARCHAR(80) NULL,
    IS_STATEFUL VARCHAR(1) NULL,
    REQUESTS_RECOVERY VARCHAR(1) NULL,
    PRIMARY KEY (ENTRY_ID))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_SCHEDULER_STATE (
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
    CHECKIN_INTERVAL BIGINT(13) NOT NULL,
    RECOVERER VARCHAR(80) NULL,
    PRIMARY KEY (INSTANCE_NAME))
    TYPE=InnoDB;
    
    CREATE TABLE QRTZ_LOCKS (
    LOCK_NAME VARCHAR(40) NOT NULL,
    PRIMARY KEY (LOCK_NAME))
    TYPE=InnoDB;
    
    INSERT INTO QRTZ_LOCKS values('TRIGGER_ACCESS');
    INSERT INTO QRTZ_LOCKS values('JOB_ACCESS');
    INSERT INTO QRTZ_LOCKS values('CALENDAR_ACCESS');
    INSERT INTO QRTZ_LOCKS values('STATE_ACCESS');
    INSERT INTO QRTZ_LOCKS values('MISFIRE_ACCESS');
