--
-- 5.6.0 to 6.0.0
--
--
-- Added new dashboard resource
-- Created table JIReportThumbnail to store thumbnails of reports
-- Bug 23374 - [case #20392] Large Dashboard elements breaks Postgres
--

   create table JIReportThumbnail (
        id int8 not null,
        user_id int8 not null,
        resource_id int8 not null,
        thumbnail bytea not null,
        primary key (id),
        unique (user_id, resource_id)
    );

   alter table JIReportThumbnail
        add constraint FKFDB3DED932282198
        foreign key (user_id)
        references JIUser
        on delete cascade;

    alter table JIReportThumbnail
        add constraint FKFDB3DED9F254B53E
        foreign key (resource_id)
        references JIResource
        on delete cascade;


-- change column type from varchar(1000) to text
ALTER TABLE JIDashboardFrameProperty ALTER COLUMN propertyValue TYPE text;

-- drop tables that are no longer used
DROP TABLE IF EXISTS JIAdhocChartMeasure;
DROP TABLE IF EXISTS JIAdhocColumn;
DROP TABLE IF EXISTS JIAdhocGroup;
DROP TABLE IF EXISTS JIAdhocTableSortField;
DROP TABLE IF EXISTS JIAdhocXTabColumnGroup;
DROP TABLE IF EXISTS JIAdhocXTabMeasure;
DROP TABLE IF EXISTS JIAdhocXTabRowGroup;

  
    create table JIAdhocDataViewBasedReports (
        adhoc_data_view_id int8 not null,
        report_id int8 not null,
        report_index int4 not null,
        primary key (adhoc_data_view_id, report_index)
    );

    create table JIDashboardModel (
        id int8 not null,
        foundationsString text,
        resourcesString text,
        defaultFoundation int4,
        primary key (id)
    );

    create table JIDashboardModelResource (
        dashboard_id int8 not null,
        resource_id int8 not null,
        resource_index int4 not null,
        primary key (dashboard_id, resource_index)
    );

    alter table JIAdhocDataViewBasedReports
        add constraint FKFFD9AFF5B22FF3B2
        foreign key (adhoc_data_view_id)
        references JIAdhocDataView;

    alter table JIAdhocDataViewBasedReports
        add constraint FKFFD9AFF5830BA6DB
        foreign key (report_id)
        references JIReportUnit;

    alter table JIDashboardModel
        add constraint FK8BB7D814A8BF376D
        foreign key (id)
        references JIResource;

    alter table JIDashboardModelResource
        add constraint FK273EAC4230711005
        foreign key (dashboard_id)
        references JIDashboardModel;

    alter table JIDashboardModelResource
        add constraint FK273EAC42F254B53E
        foreign key (resource_id)
        references JIResource;
