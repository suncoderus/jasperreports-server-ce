-- Thanks to Patrick Lightbody for submitting this...
--
-- In your Quartz properties file, you'll need to set
-- org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.PostgreSQLDelegate

--
-- 2012-01-24 thorick chow:  separate quartz drop script for postgresql
--
--

DROP TABLE qrtz_fired_triggers;
DROP TABLE QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE QRTZ_SCHEDULER_STATE;
DROP TABLE QRTZ_LOCKS;
DROP TABLE qrtz_simple_triggers;
DROP TABLE qrtz_cron_triggers;
DROP TABLE qrtz_simprop_triggers;
DROP TABLE QRTZ_BLOB_TRIGGERS;
DROP TABLE qrtz_triggers;
DROP TABLE qrtz_job_details;
DROP TABLE qrtz_calendars;