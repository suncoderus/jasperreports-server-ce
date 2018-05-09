--
-- WARNING WARNING WARNING:
-- ------------------------
--
-- The only recommended procedure for upgrading from 3.1 to 3.5 is by 
-- following the steps described in the JasperServer Install Guide PDF 
-- section 7.0 "Upgrade from JasperServer 3.0 or 3.1 to 3.5".
-- 
-- The buildomatic upgrade procedure uses the new "buildomatic" ant based scripts.
-- The buildomatic upgrade procedure handles password encryption automatically.
--
-- If the script below is used, then there are additional manual steps and
-- configurations that must be carried out in order to complete the upgrade.
-- See "Procedure for "Hardcoded" Update of Password Columns" below.
--
--
-- Notes on Database Changes:
-- --------------------------
--
-- For 3.5, password encryption is turned on by default to enhance security. 
-- The following fields are now encrypted:
--
--     User passwords (JIUser.password)
-- 
--     Datasource passwords (JIJdbcDatasource.password)
--
--     XML/A connection (JIXMLAConnection.password)
--
--
-- Procedure for "Hardcoded" Update of Password Columns:
-- -----------------------------------------------------
--
-- Passwords can be set to have a value of "password" (in ciphertext):
-- 
--     1) Uncomment out the update SQL statements below.
--
--     2) Run this upgrade script.
--
--     3) Manually update each datasource (JDBC and XMLA) via the UI.
--
--     4) Enable User password updating on the Login page via config
--        file (see below). Make the config change. Restart JasperServer.
--
--     5) Each user can now login using "password", and re-set to original
--        or new values.
--
--
-- Enable user password updating:
-- ------------------------------
--
-- The JS 3.5.0 spring configuration file WEB-INF/jasperserver-servlet.xml 
-- can be updated to enable the "Change Password" link on the Login page.
-- To enable Change Password functionality, edit the file:
--
--     WEB-INF/jasperserver-servlet.xml 
--
-- Set configuration:
--
--     "allowUserPasswordChange" to "true"
--
-- This will allow users to change back to their original password values 
-- or set new ones via the Login Screen.
--
-- Alternatively, the administrative user can set user passwords to desired 
-- values from the Manage/Users page. Edit each user and set the password.
--
--
-- Example ciphertext:
-- -------------------
--
-- Example of default encryption value:
--
--     plain text = "password". 
--     ciphertext = "33DBCB07706FCA297077043CE39DC696"
--
--
-- UNCOMMENT THESE LINES TO RE-SET PASSWORD COLUMNS:
-- -------------------------------------------------
--
-- update JIUser set password = '33DBCB07706FCA297077043CE39DC696' where externallyDefined != true and username != 'anonymousUser';
-- update JIJdbcDatasource set password = '33DBCB07706FCA297077043CE39DC696';
-- update JIXMLAConnection set password = '33DBCB07706FCA297077043CE39DC696';
--



--
-- 3.1.0 -> 3.5.0 schema updates
--

alter table JILogEvent 
    add userId bigint;

update JILogEvent le
    set le.userId = (select u.id from JIUser u where u.username = le.username);

alter table JILogEvent 
    drop username;


alter table JIObjectPermission 
    modify uri varchar(250) not null;

alter table JIObjectPermission 
    modify recipientobjectclass varchar(250);


alter table JIReportJob 
    add owner bigint not null;

update JIReportJob rj
    set rj.owner = (select u.id from JIUser u where u.username = rj.username);

alter table JIReportJob 
    drop username;

alter table JIReportJob 
    modify report_unit_uri varchar(250) not null;


alter table JIReportJobMail 
    modify message text;


alter table JIReportJobRepoDest 
    modify folder_uri varchar(250) not null;

alter table JIReportJobRepoDest 
    modify output_description varchar(250);


alter table JIRepositoryCache 
    modify uri varchar(250) not null;


create table JITenant (
    id bigint not null auto_increment,
    tenantId varchar(100) not null,
    parentId bigint,
    tenantName varchar(100) not null,
    tenantDesc varchar(250),
    tenantNote varchar(250),
    tenantUri varchar(250) not null,
    tenantFolderUri varchar(250) not null,
    primary key (id),
    unique (tenantId)
);

insert into JITenant 
    values (1, 'organizations', null, 'root', 'organizations', ' ', '/', '/');


alter table JIRole
    modify rolename varchar(100) not null;

alter table JIRole
    drop index rolename;

alter table JIRole
    add tenantId bigint not null;

update JIRole
    set tenantId = 1;

alter table JIRole
    add unique (rolename, tenantId);


alter table JIUser
    modify username varchar(100) not null;

alter table JIUser
    drop index username;

alter table JIUser
    add tenantId bigint not null;

update JIUser
    set tenantId = 1;

alter table JIUser
    add unique (username, tenantId);


alter table JILogEvent 
    add index FK5F32081591865AF (userId), 
    add constraint FK5F32081591865AF 
    foreign key (userId) 
    references JIUser (id);


create index uri_index on JIObjectPermission (uri);


alter table JIReportJob 
    add index FK156F5F6A4141263C (owner), 
    add constraint FK156F5F6A4141263C 
    foreign key (owner) 
    references JIUser (id);


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


