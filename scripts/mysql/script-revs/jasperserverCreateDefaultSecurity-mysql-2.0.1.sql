        

INSERT INTO `JIRole` (`rolename`,`externallyDefined`) VALUES 
 ('ROLE_USER',0),
 ('ROLE_ADMINISTRATOR',0),
 ('ROLE_ANONYMOUS',0);

INSERT INTO `JIUser` (`username`,`fullname`,`emailAddress`,`password`,`externallyDefined`,`enabled`) VALUES
 ('anonymousUser','Anonymous User',NULL,'',0,1);
 
INSERT INTO `JIUserRole` (`userId`,`roleId`)
 select u.id, r.id from JIUser u, JIRole r where u.username = 'anonymousUser' and r.roleName = 'ROLE_ANONYMOUS';

INSERT INTO `JIUser` (`username`,`fullname`,`emailAddress`,`password`,`externallyDefined`,`enabled`) VALUES
 ('jasperadmin','Jasper Administrator',NULL,'password',0,1);

INSERT INTO `JIUserRole` (`userId`,`roleId`)
select u.id, r.id from JIUser u, JIRole r where u.username = 'jasperadmin' and r.roleName = 'ROLE_ADMINISTRATOR';

INSERT INTO JIUserRole (userId,roleId)
select u.id, r.id from JIUser u, JIRole r where u.username = 'jasperadmin' and r.roleName = 'ROLE_USER';

INSERT INTO `JIResourceFolder` (`id`,`version`,`uri`,`hidden`,`name`,`label`,`description`,`parent_folder`,`creation_date`) VALUES 
 (1,0,'/',0,'/','root','Root of the folder hierarchy',NULL,NOW());

INSERT INTO `JIObjectPermission` (`uri`,`recipientobjectclass`,`permissionMask`,`recipientobjectid`)
select 'repo:/','com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole', 1, r.id
from `JIRole` r where rolename = 'ROLE_ADMINISTRATOR';

INSERT INTO `JIObjectPermission` (`uri`,`recipientobjectclass`,`permissionMask`,`recipientobjectid`)
select 'repo:/','com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole', 2, r.id
from `JIRole` r where rolename = 'ROLE_USER';

