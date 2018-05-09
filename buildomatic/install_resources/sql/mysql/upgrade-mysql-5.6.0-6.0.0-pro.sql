--
-- 5.6.0 to 6.0.0
--
-- Added new dashboard resource
-- Created table JIReportThumbnail to store thumbnails of reports
-- Bug 23374 - [case #20392] Large Dashboard elements breaks Postgres
-- Bug 37364 - Platform WAS 7.0.0.31 + Oracle 11g + IBM JDK 1.6: getting an error saving option of chart report
-- Bug 37405 - JBoss 7.1.1 + DB2: Error while saving report options
--

   create table JIReportThumbnail (
       id bigint not null auto_increment,
       user_id bigint not null,
       resource_id bigint not null,
       thumbnail longblob not null,
       primary key (id),
       unique (user_id, resource_id)
   ) ENGINE=InnoDB;

   alter table JIReportThumbnail
       add index FKFDB3DED932282198 (user_id),
       add constraint FKFDB3DED932282198
       foreign key (user_id)
       references JIUser (id)
       on delete cascade;

   alter table JIReportThumbnail
       add index FKFDB3DED9F254B53E (resource_id),
       add constraint FKFDB3DED9F254B53E
       foreign key (resource_id)
       references JIResource (id)
       on delete cascade;

    create table JIAdhocDataViewBasedReports (
        adhoc_data_view_id bigint not null,
        report_id bigint not null,
        report_index integer not null,
        primary key (adhoc_data_view_id, report_index)
    ) ENGINE=InnoDB;

    create table JIDashboardModel (
        id bigint not null,
        foundationsString text,
        resourcesString text,
        defaultFoundation integer,
        primary key (id)
    ) ENGINE=InnoDB;

    create table JIDashboardModelResource (
        dashboard_id bigint not null,
        resource_id bigint not null,
        resource_index integer not null,
        primary key (dashboard_id, resource_index)
    ) ENGINE=InnoDB;

    alter table JIAdhocDataViewBasedReports
        add index FKFFD9AFF5B22FF3B2 (adhoc_data_view_id),
        add constraint FKFFD9AFF5B22FF3B2
        foreign key (adhoc_data_view_id)
        references JIAdhocDataView (id);


-- change column type from varchar(1000) to mediumtext
ALTER TABLE JIDashboardFrameProperty MODIFY COLUMN propertyValue mediumtext;

-- change column type from blob to longblob
ALTER TABLE JIReportOptionsInput MODIFY COLUMN input_value longblob;

-- drop tables that are no longer used
DROP TABLE IF EXISTS JIAdhocChartMeasure;
DROP TABLE IF EXISTS JIAdhocColumn;
DROP TABLE IF EXISTS JIAdhocGroup;
DROP TABLE IF EXISTS JIAdhocTableSortField;
DROP TABLE IF EXISTS JIAdhocXTabColumnGroup;
DROP TABLE IF EXISTS JIAdhocXTabMeasure;
DROP TABLE IF EXISTS JIAdhocXTabRowGroup;

    alter table JIAdhocDataViewBasedReports
        add index FKFFD9AFF5830BA6DB (report_id),
        add constraint FKFFD9AFF5830BA6DB
        foreign key (report_id)
        references JIReportUnit (id);

    alter table JIDashboardModel
        add index FK8BB7D814A8BF376D (id),
        add constraint FK8BB7D814A8BF376D
        foreign key (id)
        references JIResource (id);

    alter table JIDashboardModelResource
        add index FK273EAC4230711005 (dashboard_id),
        add constraint FK273EAC4230711005
        foreign key (dashboard_id)
        references JIDashboardModel (id);

    alter table JIDashboardModelResource
        add index FK273EAC42F254B53E (resource_id),
        add constraint FK273EAC42F254B53E
        foreign key (resource_id)
        references JIResource (id);
