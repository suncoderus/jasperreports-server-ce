--
--
-- 6.3.0 to 6.4.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--
-- adding index to help with search queries
create index JIResourceFolder_hidden_index on JIResourceFolder (hidden);
