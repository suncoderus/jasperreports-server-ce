--
-- add resourceType column to jiresource table, popolate with data and create index
--

ALTER TABLE JIResource ADD COLUMN resourceType varchar(255);

UPDATE JIResource SET resourceType = substring ( rtype from 6 )
FROM (
SELECT id, min(resourceType) rtype FROM (
      SELECT id, 'B001_com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource' AS resourceType FROM JIBeanDataSource
UNION SELECT id, 'B002_com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource' AS resourceType FROM JICustomDataSource
UNION SELECT id, 'B003_com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource' AS resourceType FROM JIJdbcDataSource
UNION SELECT id, 'B004_com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource' AS resourceType FROM JIJndiJdbcDataSource
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
WHERE JIResource.id = data.id;
ALTER TABLE JIResource ALTER COLUMN resourceType SET NOT NULL;
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
ALTER TABLE JIReportJobMail ADD COLUMN inc_stktrc_when_job_fails bool NOT NULL DEFAULT 'f';
ALTER TABLE JIReportJobMail ADD COLUMN skip_notif_when_job_fails bool NOT NULL DEFAULT 'f';
UPDATE JIReportJobMail SET inc_stktrc_when_job_fails = 'f';
UPDATE JIReportJobMail SET skip_notif_when_job_fails = 'f';

-- add and populate new columns for JIReportJobRepoDest table 
ALTER TABLE JIReportJobRepoDest ADD COLUMN save_to_repository bool NOT NULL DEFAULT 't';
ALTER TABLE JIReportJobRepoDest ADD COLUMN using_def_rpt_opt_folder_uri bool NOT NULL DEFAULT 'f';
ALTER TABLE JIReportJobRepoDest ADD COLUMN output_local_folder varchar(250);
ALTER TABLE JIReportJobRepoDest ADD COLUMN user_name varchar(50);
ALTER TABLE JIReportJobRepoDest ADD COLUMN password varchar(50);
ALTER TABLE JIReportJobRepoDest ADD COLUMN server_name varchar(150);
ALTER TABLE JIReportJobRepoDest ADD COLUMN folder_path varchar(250);
ALTER TABLE JIReportJobRepoDest ALTER COLUMN folder_uri DROP NOT NULL;
UPDATE JIReportJobRepoDest SET save_to_repository = 't';
UPDATE JIReportJobRepoDest SET using_def_rpt_opt_folder_uri = 'f';

-- add new columns for JIReportJobTrigger table 
ALTER TABLE JIReportJobTrigger ADD COLUMN calendar_name varchar(50);
ALTER TABLE JIReportJobTrigger ADD COLUMN misfire_instruction int4 NOT NULL DEFAULT 0;
UPDATE JIReportJobTrigger SET misfire_instruction = 0;


--
-- ReportJob tables
--
    create table JIFTPInfoProperties (
        repodest_id int8 not null,
        property_value varchar(250),
        property_name varchar(100) not null,
        primary key (repodest_id, property_name)
    );

    create table JIReportAlertToAddress (
        alert_id int8 not null,
        to_address varchar(100) not null,
        to_address_idx int4 not null,
        primary key (alert_id, to_address_idx)
    );

    create table JIReportJobAlert (
        id int8 not null,
        version int4 not null,
        recipient int2 not null,
        subject varchar(100),
        message_text varchar(2000),
        message_text_when_job_fails varchar(2000),
        job_state int2 not null,
        including_stack_trace bool not null,
        including_report_job_info bool not null,
        primary key (id)
    );

    alter table JIFTPInfoProperties
        add constraint FK6BD68B04D5FA3F0A
        foreign key (repodest_id)
        references JIReportJobRepoDest;

    alter table JIReportAlertToAddress
        add constraint FKC4E3713022FA4CBA
        foreign key (alert_id)
        references JIReportJobAlert;

    alter table JIReportJob
        add constraint FK156F5F6AC83ABB38
        foreign key (alert)
        references JIReportJobAlert;

--
-- Snapshot tables
--

    create table JIDataSnapshot (
        id int8 not null,
        version int4 not null,
        snapshot_date timestamp,
        contents_id int8 not null,
        primary key (id)
    );

    create table JIDataSnapshotContents (
        id int8 not null,
        data bytea not null,
        primary key (id)
    );

    create table JIDataSnapshotParameter (
        id int8 not null,
        parameter_value bytea,
        parameter_name varchar(100) not null,
        primary key (id, parameter_name)
    );

    alter table JIDataSnapshotParameter
        add constraint id_fk_idx
        foreign key (id)
        references JIDataSnapshot;
        
--
-- Adhoc tables
--
   create table JIAdhocDataView (
        id int8 not null,
        adhocStateId int8,
        reportDataSource int8,
        promptcontrols bool,
        controlslayout int2,
        controlrenderer varchar(100),
        primary key (id)
    );

    create table JIAdhocDataViewInputControl (
        adhoc_data_view_id int8 not null,
        input_control_id int8 not null,
        control_index int4 not null,
        primary key (adhoc_data_view_id, control_index)
    );

    create table JIAdhocDataViewResource (
        adhoc_data_view_id int8 not null,
        resource_id int8 not null,
        resource_index int4 not null,
        primary key (adhoc_data_view_id, resource_index)
    );


    alter table JIAdhocDataView
        add constraint FK200A2AC9A8BF376D
        foreign key (id)
        references JIResource;

    alter table JIAdhocDataView
        add constraint FK200A2AC9324CFECB
        foreign key (reportDataSource)
        references JIResource;

    alter table JIAdhocDataView
        add constraint FK200A2AC931211827
        foreign key (adhocStateId)
        references JIAdhocState;

    alter table JIAdhocDataViewInputControl
        add constraint FKA248C79CB22FF3B2
        foreign key (adhoc_data_view_id)
        references JIAdhocDataView;

    alter table JIAdhocDataViewInputControl
        add constraint FKA248C79CE7922149
        foreign key (input_control_id)
        references JIInputControl;

    alter table JIAdhocDataViewResource
        add constraint FK98179F7B22FF3B2
        foreign key (adhoc_data_view_id)
        references JIAdhocDataView;

    alter table JIAdhocDataViewResource
        add constraint FK98179F7865B10DA
        foreign key (resource_id)
        references JIFileResource;
        

--
--  2012-03-29 thorick chow:  adopted for PostgreSQL
--

--
-- drop tables that are no longer used
-- 
DROP TABLE IF EXISTS qrtz_job_listeners;
DROP TABLE IF EXISTS qrtz_trigger_listeners;
-- 
--  drop columns that are no longer used
-- 
ALTER TABLE qrtz_job_details DROP COLUMN is_volatile;
ALTER TABLE qrtz_triggers DROP COLUMN is_volatile;
ALTER TABLE qrtz_fired_triggers DROP COLUMN is_volatile;
-- 
--  add new columns and columns that replace 'is_stateful'
--
ALTER TABLE qrtz_job_details ADD COLUMN is_nonconcurrent bool NOT NULL DEFAULT 'f';
ALTER TABLE qrtz_job_details ADD COLUMN is_update_data bool NOT NULL DEFAULT 'f';
update qrtz_job_details SET is_nonconcurrent = is_stateful;
update qrtz_job_details SET is_update_data = is_stateful;
ALTER TABLE qrtz_job_details DROP COLUMN is_stateful;
ALTER TABLE qrtz_fired_triggers ADD COLUMN is_nonconcurrent bool;
ALTER TABLE qrtz_fired_triggers ADD COLUMN is_update_data bool;
update qrtz_fired_triggers SET is_nonconcurrent = is_stateful;
update qrtz_fired_triggers SET is_update_data = is_stateful;
ALTER TABLE qrtz_fired_triggers DROP COLUMN is_stateful;
ALTER TABLE qrtz_triggers ADD COLUMN PRIORITY integer NULL;

-- 2012-03-22 thorick chow: we set the default value od PRIORITY according to docs at:
-- http://quartz-scheduler.org/api/2.0.0/org/quartz/Trigger.html#DEFAULT_PRIORITY
ALTER TABLE qrtz_fired_triggers ADD COLUMN PRIORITY integer NOT NULL DEFAULT 5;
update qrtz_fired_triggers SET PRIORITY = 5;
-- 
--  add new 'sched_name' column to all tables
-- 
ALTER TABLE qrtz_blob_triggers ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';
ALTER TABLE qrtz_calendars ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';
ALTER TABLE qrtz_cron_triggers ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';
ALTER TABLE qrtz_fired_triggers ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';
ALTER TABLE qrtz_job_details ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';
ALTER TABLE qrtz_locks ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';
ALTER TABLE qrtz_paused_trigger_grps ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';
ALTER TABLE qrtz_scheduler_state ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';
ALTER TABLE qrtz_simple_triggers ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';
ALTER TABLE qrtz_triggers ADD COLUMN SCHED_NAME varchar(100) NOT NULL DEFAULT 'TestScheduler';


-- 
--  drop all primary and foreign key constraints, so that we can define new ones
-- 

ALTER TABLE qrtz_blob_triggers DROP CONSTRAINT qrtz_blob_triggers_trigger_name_fkey;
-- postgres table has no index
--ALTER TABLE qrtz_blob_triggers drop index trigger_name;
ALTER TABLE qrtz_blob_triggers DROP CONSTRAINT qrtz_blob_triggers_pkey;

ALTER TABLE qrtz_simple_triggers DROP CONSTRAINT qrtz_simple_triggers_trigger_name_fkey;
-- postgres table has no index
--ALTER TABLE qrtz_simple_triggers drop index trigger_name;
ALTER TABLE qrtz_simple_triggers DROP CONSTRAINT qrtz_simple_triggers_pkey;

ALTER TABLE qrtz_cron_triggers DROP CONSTRAINT qrtz_cron_triggers_trigger_name_fkey;
-- postgres table has no index
--ALTER TABLE qrtz_cron_triggers drop index trigger_name;
ALTER TABLE qrtz_cron_triggers DROP CONSTRAINT qrtz_cron_triggers_pkey;

ALTER TABLE qrtz_triggers DROP CONSTRAINT qrtz_triggers_job_name_fkey;
-- postgres table has no index
--ALTER TABLE qrtz_triggers drop index job_name;
ALTER TABLE qrtz_triggers DROP CONSTRAINT qrtz_triggers_pkey;

ALTER TABLE qrtz_job_details DROP CONSTRAINT qrtz_job_details_pkey;
ALTER TABLE qrtz_fired_triggers DROP CONSTRAINT qrtz_fired_triggers_pkey;
ALTER TABLE qrtz_calendars DROP CONSTRAINT qrtz_calendars_pkey;
ALTER TABLE qrtz_locks DROP CONSTRAINT qrtz_locks_pkey;
ALTER TABLE qrtz_paused_trigger_grps DROP CONSTRAINT qrtz_paused_trigger_grps_pkey;
ALTER TABLE qrtz_scheduler_state DROP CONSTRAINT qrtz_scheduler_state_pkey;



-- 
--  add all primary and foreign key constraints, based on new columns
-- 
ALTER TABLE qrtz_job_details ADD CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (sched_name, job_name, job_group);
ALTER TABLE qrtz_triggers ADD CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);
ALTER TABLE qrtz_triggers ADD CONSTRAINT qrtz_triggers_fkey FOREIGN KEY (sched_name, job_name, job_group) REFERENCES  qrtz_job_details(sched_name, job_name, job_group);
ALTER TABLE qrtz_blob_triggers ADD CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);
ALTER TABLE qrtz_blob_triggers ADD CONSTRAINT qrtz_blob_triggers_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES  qrtz_triggers(sched_name, trigger_name, trigger_group);
ALTER TABLE qrtz_cron_triggers ADD CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);
ALTER TABLE qrtz_cron_triggers ADD CONSTRAINT qrtz_cron_triggers_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES  qrtz_triggers(sched_name, trigger_name, trigger_group);
ALTER TABLE qrtz_simple_triggers ADD CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);
ALTER TABLE qrtz_simple_triggers ADD CONSTRAINT qrtz_simple_triggers_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES  qrtz_triggers(sched_name, trigger_name, trigger_group);
ALTER TABLE qrtz_fired_triggers ADD CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (sched_name, entry_id);
ALTER TABLE qrtz_calendars ADD CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (sched_name, calendar_name);
ALTER TABLE qrtz_locks ADD CONSTRAINT qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name);
ALTER TABLE qrtz_paused_trigger_grps ADD CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (sched_name, trigger_group);
ALTER TABLE qrtz_scheduler_state ADD CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (sched_name, instance_name);
-- 
--  add new simprop_triggers table
-- 
CREATE TABLE qrtz_simprop_triggers
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
    CONSTRAINT qrtz_simprop_triggers_pkey PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qrtz_simprop_triggers_fkey FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
-- 
--  CREATE INDEX es for faster queries
-- 
CREATE INDEX  idx_qrtz_j_req_recovery on qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX  idx_qrtz_j_grp on qrtz_job_details(SCHED_NAME,JOB_GROUP);
CREATE INDEX  idx_qrtz_t_j on qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX  idx_qrtz_t_jg on qrtz_triggers(SCHED_NAME,JOB_GROUP);
CREATE INDEX  idx_qrtz_t_c on qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX  idx_qrtz_t_g on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX  idx_qrtz_t_state on qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX  idx_qrtz_t_n_state on qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX  idx_qrtz_t_n_g_state on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX  idx_qrtz_t_next_fire_time on qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX  idx_qrtz_t_nft_st on qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX  idx_qrtz_t_nft_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX  idx_qrtz_t_nft_st_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX  idx_qrtz_t_nft_st_misfire_grp on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX  idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX  idx_qrtz_ft_inst_job_req_rcvry on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX  idx_qrtz_ft_j_g on qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX  idx_qrtz_ft_jg on qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
CREATE INDEX  idx_qrtz_ft_t_g on qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX  idx_qrtz_ft_tg on qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);
