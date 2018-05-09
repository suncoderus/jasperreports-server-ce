--
-- add resourceType column to jiresource table, popolate with data and create index
--

ALTER TABLE JIResource ADD COLUMN resourceType varchar(255);

UPDATE JIResource, (
SELECT id, min(resourceType) rtype FROM (
      SELECT id, 'B001_com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource' AS resourceType FROM JIBeanDatasource
UNION SELECT id, 'B002_com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource' AS resourceType FROM JICustomDatasource
UNION SELECT id, 'B003_com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource' AS resourceType FROM JIJdbcDatasource
UNION SELECT id, 'B004_com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource' AS resourceType FROM JIJNDIJdbcDatasource
UNION SELECT id, 'B005_com.jaspersoft.commons.semantic.datasource.SemanticLayerDataSource' AS resourceType FROM JIDomainDatasource
UNION SELECT id, 'B011_com.jaspersoft.ji.ja.security.domain.SecureMondrianConnection' AS resourceType FROM JIMondrianConnection
UNION SELECT id, 'B012_com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection' AS resourceType FROM JIXMLAConnection
UNION SELECT id, 'B021_com.jaspersoft.ji.adhoc.AdhocReportUnit' AS resourceType FROM JIAdhocReportUnit
UNION SELECT id, 'B022_com.jaspersoft.commons.semantic.DataDefinerUnit' AS resourceType FROM JIDataDefinerUnit
UNION SELECT id, 'C011_com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource' AS resourceType FROM JIContentResource
UNION SELECT id, 'C012_com.jaspersoft.ji.adhoc.DashboardResource' AS resourceType FROM JIDashboard
UNION SELECT id, 'C013_com.jaspersoft.jasperserver.api.metadata.common.domain.DataType' AS resourceType FROM JIDataType
UNION SELECT id, 'C014_com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource' AS resourceType FROM JIFileResource
UNION SELECT id, 'C015_com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl' AS resourceType FROM JIInputControl
UNION SELECT id, 'C016_com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues' AS resourceType FROM JIListOfValues
UNION SELECT id, 'C017_com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition' AS resourceType FROM JIMondrianXMLADefinition
UNION SELECT id, 'C018_com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit' AS resourceType FROM JIOlapUnit
UNION SELECT id, 'C019_com.jaspersoft.jasperserver.api.metadata.common.domain.Query' AS resourceType FROM JIQuery
UNION SELECT id, 'C020_com.jaspersoft.ji.report.options.metadata.ReportOptions' AS resourceType FROM JIReportOptions
UNION SELECT id, 'C021_com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit' AS resourceType FROM JIReportUnit
) ids GROUP BY id ) data
SET resourceType = substring(rtype,6)
WHERE JIResource.id = data.id;

-- add resourceType column to jiresource table and create index
ALTER TABLE JIResource MODIFY resourceType varchar(255) NOT NULL;
CREATE INDEX resource_type_index ON JIResource (resourceType);


--
-- alter tables and add data to columns if required
--

-- add and populate creation_date column to JIReportJob table
ALTER TABLE JIReportJob ADD COLUMN creation_date timestamp;
UPDATE JIReportJob SET creation_date = CURRENT_DATE;

-- add alert column to JIReportJob table
ALTER TABLE JIReportJob ADD COLUMN alert int8;

-- add data_snapshot_id column to JIReportUnit table
ALTER TABLE JIReportUnit ADD COLUMN data_snapshot_id int8;

-- add and populate new columns for JIReportJobMail table 
ALTER TABLE JIReportJobMail ADD COLUMN message_text_when_job_fails varchar(2000);
ALTER TABLE JIReportJobMail ADD COLUMN inc_stktrc_when_job_fails bit NOT NULL;
ALTER TABLE JIReportJobMail ADD COLUMN skip_notif_when_job_fails bit NOT NULL;
UPDATE JIReportJobMail SET inc_stktrc_when_job_fails = false;
UPDATE JIReportJobMail SET skip_notif_when_job_fails = false;

-- add and populate new columns for JIReportJobRepoDest table 
ALTER TABLE JIReportJobRepoDest ADD COLUMN save_to_repository bit NOT NULL;
ALTER TABLE JIReportJobRepoDest ADD COLUMN using_def_rpt_opt_folder_uri bit NOT NULL;
ALTER TABLE JIReportJobRepoDest ADD COLUMN output_local_folder varchar(250);
ALTER TABLE JIReportJobRepoDest ADD COLUMN user_name varchar(50);
ALTER TABLE JIReportJobRepoDest ADD COLUMN password varchar(50);
ALTER TABLE JIReportJobRepoDest ADD COLUMN server_name varchar(150);
ALTER TABLE JIReportJobRepoDest ADD COLUMN folder_path varchar(250);
ALTER TABLE JIReportJobRepoDest MODIFY folder_uri varchar(250);
UPDATE JIReportJobRepoDest SET save_to_repository = true;
UPDATE JIReportJobRepoDest SET using_def_rpt_opt_folder_uri = false;

-- add new columns for JIReportJobTrigger table 
ALTER TABLE JIReportJobTrigger ADD COLUMN calendar_name varchar(50);
ALTER TABLE JIReportJobTrigger ADD COLUMN misfire_instruction integer NOT NULL;
UPDATE JIReportJobTrigger SET misfire_instruction = 0; 


--
-- ReportJob tables
--

    create table JIFTPInfoProperties (
        repodest_id bigint not null,
        property_value varchar(250),
        property_name varchar(100) not null,
        primary key (repodest_id, property_name)
    ) ENGINE=InnoDB;

    create table JIReportAlertToAddress (
        alert_id bigint not null,
        to_address varchar(100) not null,
        to_address_idx integer not null,
        primary key (alert_id, to_address_idx)
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

    alter table JIFTPInfoProperties
        add index FK6BD68B04D5FA3F0A (repodest_id),
        add constraint FK6BD68B04D5FA3F0A
        foreign key (repodest_id)
        references JIReportJobRepoDest (id);

    alter table JIReportAlertToAddress
        add index FKC4E3713022FA4CBA (alert_id),
        add constraint FKC4E3713022FA4CBA
        foreign key (alert_id)
        references JIReportJobAlert (id);

    alter table JIReportJob
        add index FK156F5F6AC83ABB38 (alert),
        add constraint FK156F5F6AC83ABB38
        foreign key (alert)
        references JIReportJobAlert (id);

--
-- Snapshot tables
--

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

    alter table JIDataSnapshotParameter
        add index id_fk_idx (id),
        add constraint id_fk_idx
        foreign key (id)
        references JIDataSnapshot (id);

--
-- Adhoc tables
--

    create table JIAdhocDataView (
        id bigint not null,
        adhocStateId bigint,
        reportDataSource bigint,
        promptcontrols bit,
        controlslayout tinyint,
        controlrenderer varchar(100),
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIAdhocDataViewInputControl (
        adhoc_data_view_id bigint not null,
        input_control_id bigint not null,
        control_index integer not null,
        primary key (adhoc_data_view_id, control_index)
    ) ENGINE=InnoDB;

    create table JIAdhocDataViewResource (
        adhoc_data_view_id bigint not null,
        resource_id bigint not null,
        resource_index integer not null,
        primary key (adhoc_data_view_id, resource_index)
    ) ENGINE=InnoDB;

    alter table JIAdhocDataView
        add index FK200A2AC9A8BF376D (id),
        add constraint FK200A2AC9A8BF376D
        foreign key (id)
        references JIResource (id);

    alter table JIAdhocDataView
        add index FK200A2AC9324CFECB (reportDataSource),
        add constraint FK200A2AC9324CFECB
        foreign key (reportDataSource)
        references JIResource (id);

    alter table JIAdhocDataView
        add index FK200A2AC931211827 (adhocStateId),
        add constraint FK200A2AC931211827
        foreign key (adhocStateId)
        references JIAdhocState (id);

    alter table JIAdhocDataViewInputControl
        add index FKA248C79CB22FF3B2 (adhoc_data_view_id),
        add constraint FKA248C79CB22FF3B2
        foreign key (adhoc_data_view_id)
        references JIAdhocDataView (id);

    alter table JIAdhocDataViewInputControl
        add index FKA248C79CE7922149 (input_control_id),
        add constraint FKA248C79CE7922149
        foreign key (input_control_id)
        references JIInputControl (id);

    alter table JIAdhocDataViewResource
        add index FK98179F7B22FF3B2 (adhoc_data_view_id),
        add constraint FK98179F7B22FF3B2
        foreign key (adhoc_data_view_id)
        references JIAdhocDataView (id);

    alter table JIAdhocDataViewResource
        add index FK98179F7865B10DA (resource_id),
        add constraint FK98179F7865B10DA
        foreign key (resource_id)
        references JIFileResource (id);

        
-- 
-- quartz tables
--
-- drop tables that are no longer used
-- 
DROP TABLE IF EXISTS QRTZ_JOB_LISTENERS;
DROP TABLE IF EXISTS QRTZ_TRIGGER_LISTENERS;
-- 
--  drop columns that are no longer used
-- 
ALTER TABLE QRTZ_JOB_DETAILS DROP COLUMN IS_VOLATILE;
ALTER TABLE QRTZ_TRIGGERS DROP COLUMN IS_VOLATILE;
ALTER TABLE QRTZ_FIRED_TRIGGERS DROP COLUMN IS_VOLATILE;
-- 
--  add new columns and columns that replace 'IS_STATEFUL'
-- 
ALTER TABLE QRTZ_JOB_DETAILS ADD COLUMN IS_NONCONCURRENT bool NOT NULL;
ALTER TABLE QRTZ_JOB_DETAILS ADD COLUMN IS_UPDATE_DATA bool NOT NULL;
update QRTZ_JOB_DETAILS SET IS_NONCONCURRENT = IS_STATEFUL;
update QRTZ_JOB_DETAILS SET IS_UPDATE_DATA = IS_STATEFUL;
ALTER TABLE QRTZ_JOB_DETAILS DROP COLUMN IS_STATEFUL;
ALTER TABLE QRTZ_FIRED_TRIGGERS ADD COLUMN IS_NONCONCURRENT bool;
ALTER TABLE QRTZ_FIRED_TRIGGERS ADD COLUMN IS_UPDATE_DATA bool;
update QRTZ_FIRED_TRIGGERS SET IS_NONCONCURRENT = IS_STATEFUL;
update QRTZ_FIRED_TRIGGERS SET IS_UPDATE_DATA = IS_STATEFUL;
ALTER TABLE QRTZ_FIRED_TRIGGERS DROP COLUMN IS_STATEFUL;
ALTER TABLE QRTZ_TRIGGERS ADD COLUMN PRIORITY integer NULL;

-- 2012-03-22 thorick chow: we set the default value of PRIORITY according to docs at:
-- http://quartz-scheduler.org/api/2.0.0/org/quartz/Trigger.html#DEFAULT_PRIORITY

ALTER TABLE QRTZ_FIRED_TRIGGERS ADD COLUMN PRIORITY integer NOT NULL;
update QRTZ_FIRED_TRIGGERS SET PRIORITY = 5;

-- 
--  add new 'sched_name' column to all tables
-- 
ALTER TABLE QRTZ_BLOB_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
ALTER TABLE QRTZ_CALENDARS ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
ALTER TABLE QRTZ_CRON_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
ALTER TABLE QRTZ_FIRED_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
ALTER TABLE QRTZ_JOB_DETAILS ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
ALTER TABLE QRTZ_LOCKS ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
ALTER TABLE QRTZ_PAUSED_TRIGGER_GRPS ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
ALTER TABLE QRTZ_SCHEDULER_STATE ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
ALTER TABLE QRTZ_SIMPLE_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
ALTER TABLE QRTZ_TRIGGERS ADD COLUMN SCHED_NAME VARCHAR(100) NOT NULL DEFAULT 'TestScheduler' FIRST;
-- 
--  drop all primary and foreign key constraints, so that we can define new ones
-- 

-- bug 29193: dropping both upper and lower case key names
--            This might cause an error message but it can be ignored
-- 
ALTER TABLE QRTZ_BLOB_TRIGGERS DROP FOREIGN KEY QRTZ_BLOB_TRIGGERS_ibfk_1;
ALTER TABLE QRTZ_BLOB_TRIGGERS DROP FOREIGN KEY qrtz_blob_triggers_ibfk_1;
ALTER TABLE QRTZ_BLOB_TRIGGERS DROP INDEX TRIGGER_NAME;
ALTER TABLE QRTZ_BLOB_TRIGGERS DROP PRIMARY KEY;
ALTER TABLE QRTZ_SIMPLE_TRIGGERS DROP FOREIGN KEY QRTZ_SIMPLE_TRIGGERS_ibfk_1; 
ALTER TABLE QRTZ_SIMPLE_TRIGGERS DROP FOREIGN KEY qrtz_simple_triggers_ibfk_1;
ALTER TABLE QRTZ_SIMPLE_TRIGGERS DROP INDEX TRIGGER_NAME;
ALTER TABLE QRTZ_SIMPLE_TRIGGERS DROP PRIMARY KEY;
ALTER TABLE QRTZ_CRON_TRIGGERS DROP FOREIGN KEY QRTZ_CRON_TRIGGERS_ibfk_1; 
ALTER TABLE QRTZ_CRON_TRIGGERS DROP FOREIGN KEY qrtz_cron_triggers_ibfk_1;
ALTER TABLE QRTZ_CRON_TRIGGERS DROP INDEX TRIGGER_NAME;
ALTER TABLE QRTZ_CRON_TRIGGERS DROP PRIMARY KEY;
ALTER TABLE QRTZ_TRIGGERS DROP FOREIGN KEY QRTZ_TRIGGERS_ibfk_1;
ALTER TABLE QRTZ_TRIGGERS DROP FOREIGN KEY qrtz_triggers_ibfk_1;


ALTER TABLE QRTZ_TRIGGERS DROP INDEX JOB_NAME;
ALTER TABLE QRTZ_TRIGGERS DROP PRIMARY KEY;
ALTER TABLE QRTZ_JOB_DETAILS DROP PRIMARY KEY;
ALTER TABLE QRTZ_FIRED_TRIGGERS DROP PRIMARY KEY;
ALTER TABLE QRTZ_CALENDARS DROP PRIMARY KEY;
ALTER TABLE QRTZ_LOCKS DROP PRIMARY KEY;
ALTER TABLE QRTZ_PAUSED_TRIGGER_GRPS DROP PRIMARY KEY;
ALTER TABLE QRTZ_SCHEDULER_STATE DROP PRIMARY KEY;
-- 
--  add all primary and foreign key constraints, based on new columns
-- 
ALTER TABLE QRTZ_JOB_DETAILS ADD CONSTRAINT QRTZ_JOB_DETAILS_PKEY PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP);
ALTER TABLE QRTZ_TRIGGERS ADD CONSTRAINT QRTZ_TRIGGERS_PKEY PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_TRIGGERS ADD CONSTRAINT QRTZ_TRIGGERS_FKEY FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP) REFERENCES  QRTZ_JOB_DETAILS(SCHED_NAME, JOB_NAME, JOB_GROUP);
ALTER TABLE QRTZ_BLOB_TRIGGERS ADD CONSTRAINT QRTZ_BLOB_TRIGGERS_PKEY PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_BLOB_TRIGGERS ADD CONSTRAINT QRTZ_BLOB_TRIGGERS_FKEY FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES  QRTZ_TRIGGERS(SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_CRON_TRIGGERS ADD CONSTRAINT QRTZ_CRON_TRIGGERS_PKEY PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_CRON_TRIGGERS ADD CONSTRAINT QRTZ_CRON_TRIGGERS_FKEY FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES  QRTZ_TRIGGERS(SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_SIMPLE_TRIGGERS ADD CONSTRAINT QRTZ_SIMPLE_TRIGGERS_PKEY PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_SIMPLE_TRIGGERS ADD CONSTRAINT QRTZ_SIMPLE_TRIGGERS_FKEY FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) REFERENCES  QRTZ_TRIGGERS(SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_FIRED_TRIGGERS ADD CONSTRAINT QRTZ_FIRED_TRIGGERS_PKEY PRIMARY KEY (SCHED_NAME, ENTRY_ID);
ALTER TABLE QRTZ_CALENDARS ADD CONSTRAINT QRTZ_CALENDARS_PKEY PRIMARY KEY (SCHED_NAME, CALENDAR_NAME);
ALTER TABLE QRTZ_LOCKS ADD CONSTRAINT QRTZ_LOCKS_PKEY PRIMARY KEY (SCHED_NAME, LOCK_NAME);
ALTER TABLE QRTZ_PAUSED_TRIGGER_GRPS ADD CONSTRAINT QRTZ_PAUSED_TRIGGER_GRPS_PKEY PRIMARY KEY (SCHED_NAME, TRIGGER_GROUP);
ALTER TABLE QRTZ_SCHEDULER_STATE ADD CONSTRAINT QRTZ_SCHEDULER_STATE_PKEY PRIMARY KEY (SCHED_NAME, INSTANCE_NAME);
-- 
--  add new simprop_triggers table
-- 
CREATE TABLE QRTZ_SIMPROP_TRIGGERS
 (          
    SCHED_NAME VARCHAR(100) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 BOOL NULL,
    BOOL_PROP_2 BOOL NULL,
    CONSTRAINT QRTZ_SIMPROP_TRIGGERS_PKEY PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_SIMPROP_TRIGGERS_FKEY FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
) ENGINE=InnoDB;
-- 
--  CREATE INDEXes for faster queries
-- 
CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
