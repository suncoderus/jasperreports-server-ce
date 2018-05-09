
update JIResource set label = 'SugarCRM Analysis Connection' 
where label = 'SugarCRM OLAP Connection';
update JIResource set label = 'Sample Foodmart Analysis View 1'
where label = 'Foodmart Sample Olap Unit 1';
update JIResource set label = 'Sample SugarCRM XMLA Analysis View 2'
where label = 'SugarCRM Sample XMLA Olap Unit 2';
update JIResource set label = 'Foodmart Sales Analysis Report'
where label = 'Foodmart OLAP Sales Report';

update JIResource set description = 'Foodmart Analysis Schema'
where description = 'OLAP Foodmart Schema';
update JIResource set description = 'Foodmart Analysis Connection'
where description = 'Foodmart Olap Connection';
update JIResource set description = 'SugarCRM Analysis Connection: only opportunities'
where description = 'SugarCRM OLAP Connection: only opportunities';
update JIResource set description = 'Sample Foodmart Analysis View 1: 1997 Q1 Unit Sales'
where description = 'Sample Olap Unit 1: 1997 Q1 Foodmart Unit Sales';
update JIResource set description = 'Sample SugarCRM Analysis View 1: Sales Performance by Industry/Account'
where description = 'SugarCRM Sample Olap Unit 1: Sales Performance by Industry/Account';
update JIResource set description = 'Sample SugarCRM Analysis View 2 (XMLA): Sales Performance by Industry/Account'
where description = 'Sample XMLA Olap Unit 2: 1997 Q1 SugarCRM Unit Sales';
update JIResource set description = 'Profile Analysis Schema'
where description = 'OLAP Profile Schema';
update JIResource set description = 'Analysis Views'
where description = 'OLAP Views';
update JIResource set description = 'Profile Analysis Connection'
where description = 'Profile Olap Connection';

update JIResourceFolder set uri = concat( '/analysis', substring( uri, 6 ) )
where uri like '/olap%';

update JIObjectPermission set uri = concat( '/analysis', substring( uri, 6 ) )
where uri like '/olap%';
update JIObjectPermission set uri = concat( 'repo:/analysis', substring( uri, 11 ) )
where uri like 'repo:/olap%';

update JIResourceFolder set name = 'analysis'
where name = 'olap';

update JIResourceFolder set label = 'Analysis Components'
where label = 'OLAP Components';
update JIResourceFolder set label = 'Analysis Connections'
where label = 'OLAP Connections';
update JIResourceFolder set label = 'Analysis Schemas'
where label = 'OLAP Schemas';
update JIResourceFolder set label = 'Analysis Data Sources'
where label = 'OLAP Data Sources';
update JIResourceFolder set label = 'Analysis Reports'
where label = 'OLAP reports';
update JIResourceFolder set label = 'Analysis Views'
where label = 'OLAP Views';
update JIResourceFolder set label = 'Foodmart Sales Analysis Report'
where label = 'Foodmart OLAP Sales Report';

update JIResourceFolder set description = 'Analysis Components'
where description = 'OLAP Components';
update JIResourceFolder set description = 'Analysis Connections'
where description = 'Connections used by OLAP';
update JIResourceFolder set description = 'Analysis Schemas'
where description = 'Schemas used by OLAP';
update JIResourceFolder set description = 'Analysis Data Sources'
where description = 'Data sources used by OLAP';
update JIResourceFolder set description = 'Analysis Views'
where description = 'OLAP Views';

select * from JIResource 
where upper(name) like '%OLAP%'
or upper(label) like '%OLAP%'
or upper(description) like '%OLAP%';

select * from JIResourceFolder
where upper(uri) like '%OLAP%'
or upper(name) like '%OLAP%'
or upper(label) like '%OLAP%'
or upper(description) like '%OLAP%';


