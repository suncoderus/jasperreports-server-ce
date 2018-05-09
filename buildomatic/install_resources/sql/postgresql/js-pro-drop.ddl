
    DROP INDEX idx17_reportDataSource_idx ON JIMondrianConnection;

    DROP INDEX idx16_mondrianSchema_idx ON JIMondrianConnection;

    DROP INDEX idx5_adhocStateId_idx ON JIAdhocReportUnit;

    DROP INDEX idx33_resource_id_idx ON JIReportUnitResource;

    DROP INDEX idx32_report_unit_id_idx ON JIReportUnitResource;

    DROP INDEX idx31_report_unit_id_idx ON JIReportUnitInputControl;

    DROP INDEX idx30_input_ctrl_id_idx ON JIReportUnitInputControl;

    DROP INDEX idx29_reportDataSource_idx ON JIReportUnit;

    DROP INDEX JIReportUnit_query_index ON JIReportUnit;

    DROP INDEX JIReportUnit_mainReport_index ON JIReportUnit;

    DROP INDEX idxA1_resource_id_idx on JICustomDatasourceResource;

    DROP INDEX JIFileResource_reference_index ON JIFileResource;

    DROP INDEX idx15_input_ctrl_id_idx ON JIInputControlQueryColumn;

    DROP INDEX JIInputControl_list_query_idx ON JIInputControl;

    DROP INDEX JIInputCtrl_list_of_values_idx ON JIInputControl;

    DROP INDEX JIInputControl_data_type_index ON JIInputControl;

    DROP INDEX idx23_olapClientConnection_idx ON JIOlapUnit;

    DROP INDEX idx19_mondrianConnectionId_idx ON JIMondrianConnectionGrant;

    DROP INDEX idx18_accessGrant_idx ON JIMondrianConnectionGrant;

    DROP INDEX JIQuery_dataSource_index ON JIQuery;

    DROP INDEX idx13_ref_id_idx ON JIDomainDatasourceDSRef;

    DROP INDEX idx12_bundle_id_idx ON JIDomainDatasourceBundle;

    DROP INDEX idx20_mondrianConnection_idx ON JIMondrianXMLADefinition;

    DROP INDEX ProfilingRecord_parent_id_idx ON ProfilingRecord;

    DROP INDEX JIReportOptions_report_id_idx ON JIReportOptions;

    DROP INDEX idx8_audit_event_id_idx ON JIAuditEventPropertyArchive;

    DROP INDEX idx7_audit_event_id_idx ON JIAuditEventProperty;

    DROP INDEX idxA2_resource_id_idx on JIDashboardModelResource;

    DROP INDEX idx11_security_id_idx ON JIDomainDatasource;

    DROP INDEX idx10_schema_id_idx ON JIDomainDatasource;

    DROP INDEX idx9_resource_id_idx ON JIDashboardResource;

    DROP INDEX JIDashboard_adhocStateId_index ON JIDashboard;

    DROP INDEX idx6_state_id_idx ON JIAdhocStateProperty;

    DROP INDEX idxA3_report_id_idx on JIAdhocDataViewBasedReports;

    DROP INDEX idx4_resource_id_idx ON JIAdhocDataViewResource;

    DROP INDEX idx3_input_ctrl_id_idx ON JIAdhocDataViewInputControl;

    DROP INDEX idx2_reportDataSource_idx ON JIAdhocDataView;

    DROP INDEX idx1_adhocStateId_idx ON JIAdhocDataView;

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

    DROP INDEX JIResourceFolder_hidden_index ON JIResourceFolder;

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
        drop constraint FK47FB3CD732282198;

    alter table JIAccessEvent 
        drop constraint FK47FB3CD7F254B53E;

    alter table JIAdhocDataView 
        drop constraint FK200A2AC931211827;

    alter table JIAdhocDataView 
        drop constraint FK200A2AC9324CFECB;

    alter table JIAdhocDataView 
        drop constraint FK200A2AC9A8BF376D;

    alter table JIAdhocDataViewBasedReports 
        drop constraint FKFFD9AFF5830BA6DB;

    alter table JIAdhocDataViewBasedReports 
        drop constraint FKFFD9AFF5B22FF3B2;

    alter table JIAdhocDataViewInputControl 
        drop constraint FKA248C79CE7922149;

    alter table JIAdhocDataViewInputControl 
        drop constraint FKA248C79CB22FF3B2;

    alter table JIAdhocDataViewResource 
        drop constraint FK98179F7865B10DA;

    alter table JIAdhocDataViewResource 
        drop constraint FK98179F7B22FF3B2;

    alter table JIAdhocReportUnit 
        drop constraint FK68AE6BB231211827;

    alter table JIAdhocReportUnit 
        drop constraint FK68AE6BB2981B13F0;

    alter table JIAdhocStateProperty 
        drop constraint FK2C7E3C6C298B519D;

    alter table JIAuditEventProperty 
        drop constraint FK3429FE136F667020;

    alter table JIAuditEventPropertyArchive 
        drop constraint FKD2940F2F637AC28A;

    alter table JIAwsDatasource 
        drop constraint FK6085542387E4472B;

    alter table JIAzureSqlDatasource 
        drop constraint FKAFE22203C001BAEA;

    alter table JIAzureSqlDatasource 
        drop constraint FKAFE2220387E4472B;

    alter table JIBeanDatasource 
        drop constraint FK674BF34A8BF376D;

    alter table JIContentResource 
        drop constraint FKE466FC68A8BF376D;

    alter table JICustomDatasource 
        drop constraint FK2BBCEDF5A8BF376D;

    alter table JICustomDatasourceProperty 
        drop constraint FKB8A66AEA858A89D1;

    alter table JICustomDatasourceResource 
        drop constraint FKDF845123F254B53E;

    alter table JICustomDatasourceResource 
        drop constraint FKDF845123858A89D1;

    alter table JIDashboard 
        drop constraint FKEC09F81531211827;

    alter table JIDashboard 
        drop constraint FKEC09F815A8BF376D;

    alter table JIDashboardFrameProperty 
        drop constraint FK679EF04DFA08F0B4;

    alter table JIDashboardModel 
        drop constraint FK8BB7D814A8BF376D;

    alter table JIDashboardModelResource 
        drop constraint FK273EAC42F254B53E;

    alter table JIDashboardModelResource 
        drop constraint FK273EAC4230711005;

    alter table JIDashboardResource 
        drop constraint FK37B53B43F254B53E;

    alter table JIDashboardResource 
        drop constraint FK37B53B43326276AC;

    alter table JIDataDefinerUnit 
        drop constraint FK1EC11AF2981B13F0;

    alter table JIDataSnapshotParameter 
        drop constraint id_fk_idx;

    alter table JIDataType 
        drop constraint FK533BCC63A8BF376D;

    alter table JIDomainDatasource 
        drop constraint FK59F8EB8833A6D267;

    alter table JIDomainDatasource 
        drop constraint FK59F8EB88A8BF376D;

    alter table JIDomainDatasource 
        drop constraint FK59F8EB88992A3868;

    alter table JIDomainDatasourceBundle 
        drop constraint FKE9F0422AE494DFE6;

    alter table JIDomainDatasourceBundle 
        drop constraint FKE9F0422ACB906E03;

    alter table JIDomainDatasourceDSRef 
        drop constraint FKFDA42FC7106B699;

    alter table JIDomainDatasourceDSRef 
        drop constraint FKFDA42FCCB906E03;

    alter table JIFTPInfoProperties 
        drop constraint FK6BD68B04D5FA3F0A;

    alter table JIFileResource 
        drop constraint FKF75B58895A0C539;

    alter table JIFileResource 
        drop constraint FKF75B5889A8BF376D;

    alter table JIInputControl 
        drop constraint FKCAC6A512B37DB6EB;

    alter table JIInputControl 
        drop constraint FKCAC6A512120E06F7;

    alter table JIInputControl 
        drop constraint FKCAC6A512A8BF376D;

    alter table JIInputControl 
        drop constraint FKCAC6A51262A86F04;

    alter table JIInputControlQueryColumn 
        drop constraint FKE436A5CCE7922149;

    alter table JIJNDIJdbcDatasource 
        drop constraint FK7F9DA248A8BF376D;

    alter table JIJdbcDatasource 
        drop constraint FKC8BDFCBFA8BF376D;

    alter table JIListOfValues 
        drop constraint FK4E86A776A8BF376D;

    alter table JIListOfValuesItem 
        drop constraint FKD37CEBA993F0E1F6;

    alter table JILogEvent 
        drop constraint FK5F32081591865AF;

    alter table JIMondrianConnection 
        drop constraint FK4FF53B19324CFECB;

    alter table JIMondrianConnection 
        drop constraint FK4FF53B191D51BFAD;

    alter table JIMondrianConnection 
        drop constraint FK4FF53B19C495A60B;

    alter table JIMondrianConnectionGrant 
        drop constraint FK3DDE9D8346D80AD2;

    alter table JIMondrianConnectionGrant 
        drop constraint FK3DDE9D83FFAC5026;

    alter table JIMondrianXMLADefinition 
        drop constraint FK313B2AB8A8BF376D;

    alter table JIMondrianXMLADefinition 
        drop constraint FK313B2AB8801D6C37;

    alter table JIOlapClientConnection 
        drop constraint FK3CA3B7D4A8BF376D;

    alter table JIOlapUnit 
        drop constraint FKF034DCCF8F542247;

    alter table JIOlapUnit 
        drop constraint FKF034DCCFA8BF376D;

    alter table JIQuery 
        drop constraint FKCBCB0EC92B329A97;

    alter table JIQuery 
        drop constraint FKCBCB0EC9A8BF376D;

    alter table JIReportAlertToAddress 
        drop constraint FKC4E3713022FA4CBA;

    alter table JIReportJob 
        drop constraint FK156F5F6A4141263C;

    alter table JIReportJob 
        drop constraint FK156F5F6A9EEC902C;

    alter table JIReportJob 
        drop constraint FK156F5F6AE4D73E35;

    alter table JIReportJob 
        drop constraint FK156F5F6AC83ABB38;

    alter table JIReportJob 
        drop constraint FK156F5F6A74D2696E;

    alter table JIReportJob 
        drop constraint FK156F5F6AFF0F459F;

    alter table JIReportJobCalendarTrigger 
        drop constraint FKC374C7D0D2B2EB53;

    alter table JIReportJobMailRecipient 
        drop constraint FKBB6DB6D880001AAE;

    alter table JIReportJobOutputFormat 
        drop constraint FKB42A5CE2C3389A8;

    alter table JIReportJobParameter 
        drop constraint FKEAC52B5F2EC643D;

    alter table JIReportJobRepoDest 
        drop constraint FKEA477EBE3C5B87D0;

    alter table JIReportJobSimpleTrigger 
        drop constraint FKB9337C5CD2B2EB53;

    alter table JIReportOptions 
        drop constraint report_fk;

    alter table JIReportOptions 
        drop constraint resource_id;

    alter table JIReportOptionsInput 
        drop constraint options_fk;

    alter table JIReportThumbnail 
        drop constraint FKFDB3DED932282198;

    alter table JIReportThumbnail 
        drop constraint FKFDB3DED9F254B53E;

    alter table JIReportUnit 
        drop constraint FK98818B77324CFECB;

    alter table JIReportUnit 
        drop constraint FK98818B778C8DF21B;

    alter table JIReportUnit 
        drop constraint FK98818B778FDA11CC;

    alter table JIReportUnit 
        drop constraint FK98818B77A8BF376D;

    alter table JIReportUnitInputControl 
        drop constraint FK5FBE934AA6A48880;

    alter table JIReportUnitInputControl 
        drop constraint FK5FBE934AE7922149;

    alter table JIReportUnitResource 
        drop constraint FK8B1C4CA5A6A48880;

    alter table JIReportUnitResource 
        drop constraint FK8B1C4CA5865B10DA;

    alter table JIRepositoryCache 
        drop constraint FKE7338B19E7C5A6;

    alter table JIResource 
        drop constraint FKD444826DA08E2155;

    alter table JIResource 
        drop constraint FKD444826DA58002DF;

    alter table JIResourceFolder 
        drop constraint FK7F24453BA08E2155;

    alter table JIRole 
        drop constraint FK82724655E415AC2D;

    alter table JITenant 
        drop constraint FKB1D7B2C97803CC2D;

    alter table JIUser 
        drop constraint FK8273B1AAE415AC2D;

    alter table JIUserRole 
        drop constraint FKD8B5C1403C31045;

    alter table JIUserRole 
        drop constraint FKD8B5C14091865AF;

    alter table JIVirtualDataSourceUriMap 
        drop constraint FK4A6CCE01F254B53E;

    alter table JIVirtualDataSourceUriMap 
        drop constraint FK4A6CCE019E600E20;

    alter table JIVirtualDatasource 
        drop constraint FK30E55631A8BF376D;

    alter table JIXMLAConnection 
        drop constraint FK94C688A71D51BFAD;

    alter table ProfilingRecord 
        drop constraint FK92D5BBF7DACDD6DA;

    drop table JIAccessEvent;

    drop table JIAdhocDataView;

    drop table JIAdhocDataViewBasedReports;

    drop table JIAdhocDataViewInputControl;

    drop table JIAdhocDataViewResource;

    drop table JIAdhocReportUnit;

    drop table JIAdhocState;

    drop table JIAdhocStateProperty;

    drop table JIAuditEvent;

    drop table JIAuditEventArchive;

    drop table JIAuditEventProperty;

    drop table JIAuditEventPropertyArchive;

    drop table JIAwsDatasource;

    drop table JIAzureSqlDatasource;

    drop table JIBeanDatasource;

    drop table JIContentResource;

    drop table JICustomDatasource;

    drop table JICustomDatasourceProperty;

    drop table JICustomDatasourceResource;

    drop table JIDashboard;

    drop table JIDashboardFrameProperty;

    drop table JIDashboardModel;

    drop table JIDashboardModelResource;

    drop table JIDashboardResource;

    drop table JIDataDefinerUnit;

    drop table JIDataSnapshot;

    drop table JIDataSnapshotContents;

    drop table JIDataSnapshotParameter;

    drop table JIDataType;

    drop table JIDomainDatasource;

    drop table JIDomainDatasourceBundle;

    drop table JIDomainDatasourceDSRef;

    drop table JIFTPInfoProperties;

    drop table JIFileResource;

    drop table JIInputControl;

    drop table JIInputControlQueryColumn;

    drop table JIJNDIJdbcDatasource;

    drop table JIJdbcDatasource;

    drop table JIListOfValues;

    drop table JIListOfValuesItem;

    drop table JILogEvent;

    drop table JIMondrianConnection;

    drop table JIMondrianConnectionGrant;

    drop table JIMondrianXMLADefinition;

    drop table JIObjectPermission;

    drop table JIOlapClientConnection;

    drop table JIOlapUnit;

    drop table JIProfileAttribute;

    drop table JIQuery;

    drop table JIReportAlertToAddress;

    drop table JIReportJob;

    drop table JIReportJobAlert;

    drop table JIReportJobCalendarTrigger;

    drop table JIReportJobMail;

    drop table JIReportJobMailRecipient;

    drop table JIReportJobOutputFormat;

    drop table JIReportJobParameter;

    drop table JIReportJobRepoDest;

    drop table JIReportJobSimpleTrigger;

    drop table JIReportJobTrigger;

    drop table JIReportMonitoringFact;

    drop table JIReportOptions;

    drop table JIReportOptionsInput;

    drop table JIReportThumbnail;

    drop table JIReportUnit;

    drop table JIReportUnitInputControl;

    drop table JIReportUnitResource;

    drop table JIRepositoryCache;

    drop table JIResource;

    drop table JIResourceFolder;

    drop table JIRole;

    drop table JITenant;

    drop table JIUser;

    drop table JIUserRole;

    drop table JIVirtualDataSourceUriMap;

    drop table JIVirtualDatasource;

    drop table JIXMLAConnection;

    drop table ProfilingRecord;

    drop sequence hibernate_sequence;
