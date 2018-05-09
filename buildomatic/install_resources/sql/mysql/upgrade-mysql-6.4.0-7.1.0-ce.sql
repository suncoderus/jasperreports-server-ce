--
--
-- 6.4.0 to 7.1.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

-- Change column type from "varchar(100)" to "varchar(255)"
ALTER TABLE JIReportJobParameter MODIFY COLUMN parameter_name varchar(255) NOT NULL;
