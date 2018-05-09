-- # Thanks to Patrick Lightbody for submitting this...
-- #
-- # In your Quartz properties file, you'll need to set
-- # org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.PostreSQLDelegate


-- # The CREATE LANGUAGE statement below may be commented out if plpgsql is already enabled.
-- # This is the case in a default install of PostgreSQL 8.2 on Windows.
-- # But it can remain in the script with no ill effect if plpgsql is already enabled.

-- START of CREATE LANGUAGE
CREATE OR REPLACE FUNCTION create_plpgsql_language ()
  RETURNS TEXT
  AS ' CREATE LANGUAGE plpgsql; SELECT ''language plpgsql created''::TEXT; '
  LANGUAGE 'sql';

SELECT
  CASE WHEN (SELECT true::BOOLEAN FROM pg_language WHERE lanname='plpgsql')
  THEN
    (SELECT 'language already installed'::TEXT)
  ELSE
    (SELECT create_plpgsql_language())
  END;

DROP FUNCTION create_plpgsql_language ();

-- For some reason unknown to me, I had to have this on one line.  Something about unterminated strings...
CREATE OR REPLACE FUNCTION drop_table_if_exists(text) RETURNS bool AS
'DECLARE rec record;BEGIN SELECT INTO rec oid FROM pg_class WHERE relname = $1::name;IF FOUND THEN EXECUTE ''DROP TABLE '' || $1 || '' CASCADE'';RETURN true;END IF;RETURN FALSE;END;' LANGUAGE 'plpgsql'
;
-- END of CREATE LANGUAGE

SELECT drop_table_if_exists('qrtz_job_listeners');
SELECT drop_table_if_exists('qrtz_trigger_listeners');
SELECT drop_table_if_exists('qrtz_fired_triggers');
SELECT drop_table_if_exists('qrtz_paused_trigger_grps');
SELECT drop_table_if_exists('qrtz_scheduler_state');
SELECT drop_table_if_exists('qrtz_locks');
SELECT drop_table_if_exists('qrtz_simple_triggers');
SELECT drop_table_if_exists('qrtz_cron_triggers');
SELECT drop_table_if_exists('qrtz_blob_triggers');
SELECT drop_table_if_exists('qrtz_triggers');
SELECT drop_table_if_exists('qrtz_job_details');
SELECT drop_table_if_exists('qrtz_calendars');


DROP FUNCTION drop_table_if_exists(text);

CREATE TABLE qrtz_job_details
  (
    JOB_NAME  VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    JOB_CLASS_NAME   VARCHAR(128) NOT NULL,
    IS_DURABLE BOOL NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    IS_STATEFUL BOOL NOT NULL,
    REQUESTS_RECOVERY BOOL NOT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
);

CREATE TABLE qrtz_job_listeners
  (
    JOB_NAME  VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    JOB_LISTENER VARCHAR(80) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);

CREATE TABLE qrtz_triggers
  (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    JOB_NAME  VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    NEXT_FIRE_TIME BIGINT NULL,
    PREV_FIRE_TIME BIGINT NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT NOT NULL,
    END_TIME BIGINT NULL,
    CALENDAR_NAME VARCHAR(80) NULL,
    MISFIRE_INSTR SMALLINT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);

CREATE TABLE qrtz_simple_triggers
  (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    REPEAT_COUNT BIGINT NOT NULL,
    REPEAT_INTERVAL BIGINT NOT NULL,
    TIMES_TRIGGERED BIGINT NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE qrtz_cron_triggers
  (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    CRON_EXPRESSION VARCHAR(80) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE qrtz_blob_triggers
  (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    BLOB_DATA BYTEA NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE qrtz_trigger_listeners
  (
    TRIGGER_NAME  VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    TRIGGER_LISTENER VARCHAR(80) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);


CREATE TABLE qrtz_calendars
  (
    CALENDAR_NAME  VARCHAR(80) NOT NULL,
    CALENDAR BYTEA NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);


CREATE TABLE qrtz_paused_trigger_grps
  (
    TRIGGER_GROUP  VARCHAR(80) NOT NULL,
    PRIMARY KEY (TRIGGER_GROUP)
);

CREATE TABLE qrtz_fired_triggers
  (
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    FIRED_TIME BIGINT NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(80) NULL,
    JOB_GROUP VARCHAR(80) NULL,
    IS_STATEFUL BOOL NULL,
    REQUESTS_RECOVERY BOOL NULL,
    PRIMARY KEY (ENTRY_ID)
);

CREATE TABLE qrtz_scheduler_state
  (
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    LAST_CHECKIN_TIME BIGINT NOT NULL,
    CHECKIN_INTERVAL BIGINT NOT NULL,
    RECOVERER VARCHAR(80) NULL,
    PRIMARY KEY (INSTANCE_NAME)
);

CREATE TABLE qrtz_locks
  (
    LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (LOCK_NAME)
);


INSERT INTO qrtz_locks values('TRIGGER_ACCESS');
INSERT INTO qrtz_locks values('JOB_ACCESS');
INSERT INTO qrtz_locks values('CALENDAR_ACCESS');
INSERT INTO qrtz_locks values('STATE_ACCESS');
INSERT INTO qrtz_locks values('MISFIRE_ACCESS');

commit;

