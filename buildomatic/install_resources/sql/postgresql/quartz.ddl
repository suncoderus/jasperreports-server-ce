-- # Comments from the original PostgreSQL quartz file: 
-- #
-- # Thanks to Patrick Lightbody for submitting this...
-- #
-- # In your Quartz properties file, you'll need to set 
-- # org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.PostreSQLDelegate
--
-- 
-- Comments from Jaspersoft:
--
--
-- 2012-05-23: tkavanagh:
--             In the switch from quartz 1.5 to 2.1.2, we have to handle the
--             table and column size changes carefully because of existing
--             JRS customers who will be upgrading from older JRS (and thus
--             older quartz). If column sizes were made larger in quartz
--             2.1.2, we are ignoring this and leaving the col size the same.
--             So, for instance, if qrtz_job_details.JOB_NAME was increased
--             to (200) in 2.1.2, I explicitly set it to (80) which is what it
--             was in quartz 1.5. JRS 4.7.0 is upgraded to use quartz 2.1.2.
--
-- 2012-01-24: thorick: 
--             The stuff between these lines below is left over from the 
--             quartz 1.5.1 quartz.ddl script.
--
--
-- # The CREATE LANGUAGE statement below can be commented out if plpgsql is 
-- # already enabled. This seems to be the case on Windows PostgreSLQ 8.2

-- CREATE LANGUAGE 'plpgsql';

-- For some reason unknown to me, I had to have this on one line.  Something about unterminated strings...
-- CREATE OR REPLACE FUNCTION drop_table_if_exists(text) RETURNS bool AS
-- 'DECLARE rec record;BEGIN SELECT INTO rec oid FROM pg_class WHERE relname = $1::name;IF FOUND THEN EXECUTE ''DROP TABLE '' || $1 || '' CASCADE'';RETURN true;END IF;RETURN FALSE;END;' LANGUAGE 'plpgsql'
-- ;
--


-- SELECT drop_table_if_exists('qrtz_job_listeners');
-- SELECT drop_table_if_exists('qrtz_trigger_listeners');
-- SELECT drop_table_if_exists('qrtz_fired_triggers');
-- SELECT drop_table_if_exists('qrtz_paused_trigger_grps');
-- SELECT drop_table_if_exists('qrtz_scheduler_state');
-- SELECT drop_table_if_exists('qrtz_locks');
-- SELECT drop_table_if_exists('qrtz_simple_triggers');
-- SELECT drop_table_if_exists('qrtz_cron_triggers');
-- SELECT drop_table_if_exists('qrtz_blob_triggers');
-- SELECT drop_table_if_exists('qrtz_triggers');
-- SELECT drop_table_if_exists('qrtz_job_details');
-- SELECT drop_table_if_exists('qrtz_calendars');


-- DROP FUNCTION drop_table_if_exists(text);

-- DROP LANGUAGE 'plpgsql';
------------------------------------------------------------------------------

CREATE TABLE qrtz_job_details
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    JOB_NAME  VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    JOB_CLASS_NAME   VARCHAR(128) NOT NULL,
    IS_DURABLE BOOL NOT NULL,
    IS_NONCONCURRENT BOOL NOT NULL,
    IS_UPDATE_DATA BOOL NOT NULL,
    REQUESTS_RECOVERY BOOL NOT NULL,
    JOB_DATA BYTEA NULL,
    CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE qrtz_triggers
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    JOB_NAME  VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    NEXT_FIRE_TIME BIGINT NULL,
    PREV_FIRE_TIME BIGINT NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT NOT NULL,
    END_TIME BIGINT NULL,
    CALENDAR_NAME VARCHAR(80) NULL,
    MISFIRE_INSTR SMALLINT NULL,
    JOB_DATA BYTEA NULL,
    CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qrtz_triggers_fkey FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE qrtz_simple_triggers
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    REPEAT_COUNT BIGINT NOT NULL,
    REPEAT_INTERVAL BIGINT NOT NULL,
    TIMES_TRIGGERED BIGINT NOT NULL,
    CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qrtz_simple_triggers_fkey FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE qrtz_cron_triggers
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    CRON_EXPRESSION VARCHAR(80) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qrtz_cron_triggers_fkey FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

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

CREATE TABLE qrtz_blob_triggers
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    BLOB_DATA BYTEA NULL,
    CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT qrtz_blob_triggers_fkey FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE qrtz_calendars
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    CALENDAR_NAME  VARCHAR(80) NOT NULL,
    CALENDAR BYTEA NOT NULL,
    CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);


CREATE TABLE qrtz_paused_trigger_grps
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    TRIGGER_GROUP  VARCHAR(80) NOT NULL,
    CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE qrtz_fired_triggers
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    FIRED_TIME BIGINT NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(80) NULL,
    JOB_GROUP VARCHAR(80) NULL,
    IS_NONCONCURRENT BOOL NULL,
    REQUESTS_RECOVERY BOOL NULL,
    CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE qrtz_scheduler_state
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    LAST_CHECKIN_TIME BIGINT NOT NULL,
    CHECKIN_INTERVAL BIGINT NOT NULL,
    CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE qrtz_locks
  (
    SCHED_NAME VARCHAR(100) NOT NULL,
    LOCK_NAME  VARCHAR(40) NOT NULL,
    CONSTRAINT qrtz_locks_pkey PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

create index idx_qrtz_j_req_recovery on qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_j_grp on qrtz_job_details(SCHED_NAME,JOB_GROUP);

create index idx_qrtz_t_j on qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_t_jg on qrtz_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_t_c on qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
create index idx_qrtz_t_g on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
create index idx_qrtz_t_state on qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
create index idx_qrtz_t_n_state on qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_n_g_state on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_next_fire_time on qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st on qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_nft_st_misfire_grp on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

create index idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index idx_qrtz_ft_inst_job_req_rcvry on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_ft_j_g on qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_ft_jg on qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_ft_t_g on qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_tg on qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);


commit;
