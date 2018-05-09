--
-- 6.0.0 to 6.1.0
--
-- This is a placeholder file for the js-upgrade-samedb.sh/bat script
--

    create table JICustomDatasourceResource (
        ds_id bigint not null,
        resource_id bigint not null,
        name varchar(100) not null,
        primary key (ds_id, name)
    ) ENGINE=InnoDB;

    alter table JICustomDatasourceResource
        add index FKDF845123858A89D1 (ds_id),
        add constraint FKDF845123858A89D1
        foreign key (ds_id)
        references JICustomDatasource (id);

    alter table JICustomDatasourceResource
        add index FKDF845123F254B53E (resource_id),
        add constraint FKDF845123F254B53E
        foreign key (resource_id)
        references JIResource (id);

ALTER TABLE JIProfileAttribute ADD COLUMN description varchar(255);
ALTER TABLE JIProfileAttribute MODIFY COLUMN attrValue varchar(2000) NULL;
ALTER TABLE JIProfileAttribute ADD COLUMN owner varchar(255);

ALTER TABLE JICustomDatasourceProperty MODIFY COLUMN name varchar(200) NOT NULL;

ALTER TABLE JICustomDatasourceResource MODIFY COLUMN name varchar(200) NOT NULL;

ALTER TABLE JIResource MODIFY COLUMN name varchar(200) NOT NULL;
ALTER TABLE JIResource MODIFY COLUMN label varchar(200) NOT NULL;

ALTER TABLE JIResourceFolder MODIFY COLUMN name varchar(200) NOT NULL;
ALTER TABLE JIResourceFolder MODIFY COLUMN label varchar(200) NOT NULL;

ALTER TABLE JIVirtualDataSourceUriMap MODIFY COLUMN data_source_name varchar(200) NOT NULL;

--
-- new indexes on foreign keys
--    

    create index idx14_repodest_id_idx on JIFTPInfoProperties (repodest_id);
    create index JIFileResource_reference_index on JIFileResource (reference);
    create index JIInputControl_data_type_index on JIInputControl (data_type);
    create index JIInputCtrl_list_of_values_idx on JIInputControl (list_of_values);
    create index JIInputControl_list_query_idx on JIInputControl (list_query);
    create index idx15_input_ctrl_id_idx on JIInputControlQueryColumn (input_control_id);
    create index JILogEvent_userId_index on JILogEvent (userId);
    create index idx16_mondrianSchema_idx on JIMondrianConnection (mondrianSchema);
    create index idx17_reportDataSource_idx on JIMondrianConnection (reportDataSource);
    create index idx20_mondrianConnection_idx on JIMondrianXMLADefinition (mondrianConnection);
    create index idx21_recipientobjclass_idx on JIObjectPermission (recipientobjectclass);
    create index idx22_recipientobjid_idx on JIObjectPermission (recipientobjectid);
    create index idx23_olapClientConnection_idx on JIOlapUnit (olapClientConnection);
    create index JIQuery_dataSource_index on JIQuery (dataSource);
    create index idx24_alert_id_idx on JIReportAlertToAddress (alert_id);
    create index JIReportJob_alert_index on JIReportJob (alert);
    create index idx25_content_destination_idx on JIReportJob (content_destination);
    create index JIReportJob_job_trigger_index on JIReportJob (job_trigger);
    create index idx26_mail_notification_idx on JIReportJob (mail_notification);
    create index JIReportJob_owner_index on JIReportJob (owner);
    create index idx27_destination_id_idx on JIReportJobMailRecipient (destination_id);
    create index idx28_resource_id_idx on JIReportThumbnail (resource_id);
    create index JIReportUnit_mainReport_index on JIReportUnit (mainReport);
    create index JIReportUnit_query_index on JIReportUnit (query);
    create index idx29_reportDataSource_idx on JIReportUnit (reportDataSource);
    create index idx30_input_ctrl_id_idx on JIReportUnitInputControl (input_control_id);
    create index idx31_report_unit_id_idx on JIReportUnitInputControl (report_unit_id);
    create index idx32_report_unit_id_idx on JIReportUnitResource (report_unit_id);
    create index idx33_resource_id_idx on JIReportUnitResource (resource_id);
    create index idx34_item_reference_idx on JIRepositoryCache (item_reference);
    create index JIResource_childrenFolder_idx on JIResource (childrenFolder);
    create index JIResource_parent_folder_index on JIResource (parent_folder);
    create index idx35_parent_folder_idx on JIResourceFolder (parent_folder);
    create index JIResourceFolder_version_index on JIResourceFolder (version);
    create index JIRole_tenantId_index on JIRole (tenantId);
    create index JITenant_parentId_index on JITenant (parentId);
    create index JIUser_tenantId_index on JIUser (tenantId);
    create index JIUserRole_roleId_index on JIUserRole (roleId);
    create index JIUserRole_userId_index on JIUserRole (userId);
    create index idx36_resource_id_idx on JIVirtualDataSourceUriMap (resource_id);
 
create index idxA1_resource_id_idx on JICustomDatasourceResource (resource_id);
