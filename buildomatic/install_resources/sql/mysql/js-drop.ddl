
    DROP INDEX JIQuery_dataSource_index ON JIQuery;

    DROP INDEX idx23_olapClientConnection_idx ON JIOlapUnit;

    DROP INDEX idxA1_resource_id_idx on JICustomDatasourceResource;

    DROP INDEX idx17_reportDataSource_idx ON JIMondrianConnection;

    DROP INDEX idx16_mondrianSchema_idx ON JIMondrianConnection;

    DROP INDEX idx33_resource_id_idx ON JIReportUnitResource;

    DROP INDEX idx32_report_unit_id_idx ON JIReportUnitResource;

    DROP INDEX idx31_report_unit_id_idx ON JIReportUnitInputControl;

    DROP INDEX idx30_input_ctrl_id_idx ON JIReportUnitInputControl;

    DROP INDEX idx29_reportDataSource_idx ON JIReportUnit;

    DROP INDEX JIReportUnit_query_index ON JIReportUnit;

    DROP INDEX JIReportUnit_mainReport_index ON JIReportUnit;

    DROP INDEX idx15_input_ctrl_id_idx ON JIInputControlQueryColumn;

    DROP INDEX JIInputControl_list_query_idx ON JIInputControl;

    DROP INDEX JIInputCtrl_list_of_values_idx ON JIInputControl;

    DROP INDEX JIInputControl_data_type_index ON JIInputControl;

    DROP INDEX JIFileResource_reference_index ON JIFileResource;

    DROP INDEX idx20_mondrianConnection_idx ON JIMondrianXMLADefinition;

    DROP INDEX JIUser_tenantId_index ON JIUser;

    DROP INDEX JITenant_parentId_index ON JITenant;

    DROP INDEX JIUserRole_userId_index ON JIUserRole;

    DROP INDEX JIUserRole_roleId_index ON JIUserRole;

    DROP INDEX JIRole_tenantId_index ON JIRole;

    DROP INDEX idx22_recipientobjid_idx ON JIObjectPermission;

    DROP INDEX idx21_recipientobjclass_idx ON JIObjectPermission;

    DROP INDEX uri_index ON JIObjectPermission;

    DROP INDEX idx36_resource_id_idx ON JIVirtualDataSourceUriMap;

    DROP INDEX JIResource_parent_folder_index ON JIResource;

    DROP INDEX JIResource_childrenFolder_idx ON JIResource;

    DROP INDEX idx28_resource_id_idx ON JIReportThumbnail;

    DROP INDEX JIResourceFolder_version_index ON JIResourceFolder;

    DROP INDEX idx35_parent_folder_idx ON JIResourceFolder;

    DROP INDEX idx34_item_reference_idx ON JIRepositoryCache;

    DROP INDEX idx14_repodest_id_idx ON JIFTPInfoProperties;

    DROP INDEX idx27_destination_id_idx ON JIReportJobMailRecipient;

    DROP INDEX idx24_alert_id_idx ON JIReportAlertToAddress;

    DROP INDEX JIReportJob_owner_index ON JIReportJob;

    DROP INDEX idx26_mail_notification_idx ON JIReportJob;

    DROP INDEX JIReportJob_job_trigger_index ON JIReportJob;

    DROP INDEX idx25_content_destination_idx ON JIReportJob;

    DROP INDEX JIReportJob_alert_index ON JIReportJob;

    DROP INDEX JILogEvent_userId_index ON JILogEvent;

    alter table JIAccessEvent 
        drop 
        foreign key FK47FB3CD732282198;

    alter table JIAccessEvent 
        drop 
        foreign key FK47FB3CD7F254B53E;

    alter table JIAwsDatasource 
        drop 
        foreign key FK6085542387E4472B;

    alter table JIAzureSqlDatasource 
        drop 
        foreign key FKAFE22203C001BAEA;

    alter table JIAzureSqlDatasource 
        drop 
        foreign key FKAFE2220387E4472B;

    alter table JIBeanDatasource 
        drop 
        foreign key FK674BF34A8BF376D;

    alter table JIContentResource 
        drop 
        foreign key FKE466FC68A8BF376D;

    alter table JICustomDatasource 
        drop 
        foreign key FK2BBCEDF5A8BF376D;

    alter table JICustomDatasourceProperty 
        drop 
        foreign key FKB8A66AEA858A89D1;

    alter table JICustomDatasourceResource 
        drop 
        foreign key FKDF845123858A89D1;

    alter table JICustomDatasourceResource 
        drop 
        foreign key FKDF845123F254B53E;

    alter table JIDataSnapshotParameter 
        drop 
        foreign key id_fk_idx;

    alter table JIDataType 
        drop 
        foreign key FK533BCC63A8BF376D;

    alter table JIFTPInfoProperties 
        drop 
        foreign key FK6BD68B04D5FA3F0A;

    alter table JIFileResource 
        drop 
        foreign key FKF75B5889A8BF376D;

    alter table JIFileResource 
        drop 
        foreign key FKF75B58895A0C539;

    alter table JIInputControl 
        drop 
        foreign key FKCAC6A512120E06F7;

    alter table JIInputControl 
        drop 
        foreign key FKCAC6A512A8BF376D;

    alter table JIInputControl 
        drop 
        foreign key FKCAC6A51262A86F04;

    alter table JIInputControl 
        drop 
        foreign key FKCAC6A512B37DB6EB;

    alter table JIInputControlQueryColumn 
        drop 
        foreign key FKE436A5CCE7922149;

    alter table JIJNDIJdbcDatasource 
        drop 
        foreign key FK7F9DA248A8BF376D;

    alter table JIJdbcDatasource 
        drop 
        foreign key FKC8BDFCBFA8BF376D;

    alter table JIListOfValues 
        drop 
        foreign key FK4E86A776A8BF376D;

    alter table JIListOfValuesItem 
        drop 
        foreign key FKD37CEBA993F0E1F6;

    alter table JILogEvent 
        drop 
        foreign key FK5F32081591865AF;

    alter table JIMondrianConnection 
        drop 
        foreign key FK4FF53B191D51BFAD;

    alter table JIMondrianConnection 
        drop 
        foreign key FK4FF53B19324CFECB;

    alter table JIMondrianConnection 
        drop 
        foreign key FK4FF53B19C495A60B;

    alter table JIMondrianXMLADefinition 
        drop 
        foreign key FK313B2AB8A8BF376D;

    alter table JIMondrianXMLADefinition 
        drop 
        foreign key FK313B2AB8DC098B1;

    alter table JIOlapClientConnection 
        drop 
        foreign key FK3CA3B7D4A8BF376D;

    alter table JIOlapUnit 
        drop 
        foreign key FKF034DCCFA8BF376D;

    alter table JIOlapUnit 
        drop 
        foreign key FKF034DCCF8F542247;

    alter table JIQuery 
        drop 
        foreign key FKCBCB0EC9A8BF376D;

    alter table JIQuery 
        drop 
        foreign key FKCBCB0EC92B329A97;

    alter table JIReportAlertToAddress 
        drop 
        foreign key FKC4E3713022FA4CBA;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6AE4D73E35;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6AC83ABB38;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6A9EEC902C;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6A74D2696E;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6AFF0F459F;

    alter table JIReportJob 
        drop 
        foreign key FK156F5F6A4141263C;

    alter table JIReportJobCalendarTrigger 
        drop 
        foreign key FKC374C7D0D2B2EB53;

    alter table JIReportJobMailRecipient 
        drop 
        foreign key FKBB6DB6D880001AAE;

    alter table JIReportJobOutputFormat 
        drop 
        foreign key FKB42A5CE2C3389A8;

    alter table JIReportJobParameter 
        drop 
        foreign key FKEAC52B5F2EC643D;

    alter table JIReportJobRepoDest 
        drop 
        foreign key FKEA477EBE3C5B87D0;

    alter table JIReportJobSimpleTrigger 
        drop 
        foreign key FKB9337C5CD2B2EB53;

    alter table JIReportThumbnail 
        drop 
        foreign key FKFDB3DED932282198;

    alter table JIReportThumbnail 
        drop 
        foreign key FKFDB3DED9F254B53E;

    alter table JIReportUnit 
        drop 
        foreign key FK98818B77A8BF376D;

    alter table JIReportUnit 
        drop 
        foreign key FK98818B778FDA11CC;

    alter table JIReportUnit 
        drop 
        foreign key FK98818B77324CFECB;

    alter table JIReportUnit 
        drop 
        foreign key FK98818B778C8DF21B;

    alter table JIReportUnitInputControl 
        drop 
        foreign key FK5FBE934AE7922149;

    alter table JIReportUnitInputControl 
        drop 
        foreign key FK5FBE934AA6A48880;

    alter table JIReportUnitResource 
        drop 
        foreign key FK8B1C4CA5A6A48880;

    alter table JIReportUnitResource 
        drop 
        foreign key FK8B1C4CA5865B10DA;

    alter table JIRepositoryCache 
        drop 
        foreign key FKE7338B19E7C5A6;

    alter table JIResource 
        drop 
        foreign key FKD444826DA58002DF;

    alter table JIResource 
        drop 
        foreign key FKD444826DA08E2155;

    alter table JIResourceFolder 
        drop 
        foreign key FK7F24453BA08E2155;

    alter table JIRole 
        drop 
        foreign key FK82724655E415AC2D;

    alter table JITenant 
        drop 
        foreign key FKB1D7B2C97803CC2D;

    alter table JIUser 
        drop 
        foreign key FK8273B1AAE415AC2D;

    alter table JIUserRole 
        drop 
        foreign key FKD8B5C1403C31045;

    alter table JIUserRole 
        drop 
        foreign key FKD8B5C14091865AF;

    alter table JIVirtualDataSourceUriMap 
        drop 
        foreign key FK4A6CCE019E600E20;

    alter table JIVirtualDataSourceUriMap 
        drop 
        foreign key FK4A6CCE01F254B53E;

    alter table JIVirtualDatasource 
        drop 
        foreign key FK30E55631A8BF376D;

    alter table JIXMLAConnection 
        drop 
        foreign key FK94C688A71D51BFAD;

    drop table if exists JIAccessEvent;

    drop table if exists JIAwsDatasource;

    drop table if exists JIAzureSqlDatasource;

    drop table if exists JIBeanDatasource;

    drop table if exists JIContentResource;

    drop table if exists JICustomDatasource;

    drop table if exists JICustomDatasourceProperty;

    drop table if exists JICustomDatasourceResource;

    drop table if exists JIDataSnapshot;

    drop table if exists JIDataSnapshotContents;

    drop table if exists JIDataSnapshotParameter;

    drop table if exists JIDataType;

    drop table if exists JIFTPInfoProperties;

    drop table if exists JIFileResource;

    drop table if exists JIInputControl;

    drop table if exists JIInputControlQueryColumn;

    drop table if exists JIJNDIJdbcDatasource;

    drop table if exists JIJdbcDatasource;

    drop table if exists JIListOfValues;

    drop table if exists JIListOfValuesItem;

    drop table if exists JILogEvent;

    drop table if exists JIMondrianConnection;

    drop table if exists JIMondrianXMLADefinition;

    drop table if exists JIObjectPermission;

    drop table if exists JIOlapClientConnection;

    drop table if exists JIOlapUnit;

    drop table if exists JIProfileAttribute;

    drop table if exists JIQuery;

    drop table if exists JIReportAlertToAddress;

    drop table if exists JIReportJob;

    drop table if exists JIReportJobAlert;

    drop table if exists JIReportJobCalendarTrigger;

    drop table if exists JIReportJobMail;

    drop table if exists JIReportJobMailRecipient;

    drop table if exists JIReportJobOutputFormat;

    drop table if exists JIReportJobParameter;

    drop table if exists JIReportJobRepoDest;

    drop table if exists JIReportJobSimpleTrigger;

    drop table if exists JIReportJobTrigger;

    drop table if exists JIReportThumbnail;

    drop table if exists JIReportUnit;

    drop table if exists JIReportUnitInputControl;

    drop table if exists JIReportUnitResource;

    drop table if exists JIRepositoryCache;

    drop table if exists JIResource;

    drop table if exists JIResourceFolder;

    drop table if exists JIRole;

    drop table if exists JITenant;

    drop table if exists JIUser;

    drop table if exists JIUserRole;

    drop table if exists JIVirtualDataSourceUriMap;

    drop table if exists JIVirtualDatasource;

    drop table if exists JIXMLAConnection;
