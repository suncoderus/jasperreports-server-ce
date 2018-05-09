--
-- Create new tables for virtual data source
--

    create table JIVirtualDataSourceUriMap (
        virtualDS_id bigint not null,
        resource_id bigint not null,
        data_source_name varchar(100) not null,
        primary key (virtualDS_id, data_source_name)
    ) ENGINE=InnoDB;

    create table JIVirtualDatasource (
        id bigint not null,
        timezone varchar(100),
        primary key (id)
    ) ENGINE=InnoDB;
	
    alter table JIVirtualDataSourceUriMap 
        add index FK4A6CCE019E600E20 (virtualDS_id), 
        add constraint FK4A6CCE019E600E20 
        foreign key (virtualDS_id) 
        references JIVirtualDatasource (id);

    alter table JIVirtualDataSourceUriMap 
        add index FK4A6CCE01F254B53E (resource_id), 
        add constraint FK4A6CCE01F254B53E 
        foreign key (resource_id) 
        references JIResource (id);

    alter table JIVirtualDatasource 
        add index FK30E55631A8BF376D (id), 
        add constraint FK30E55631A8BF376D 
        foreign key (id) 
        references JIResource (id);


--
-- Create table and indices for Monitoring
--

    create table JIReportMonitoringFact (
        id bigint not null auto_increment,
        date_year smallint not null,
        date_month tinyint not null,
        date_day tinyint not null,
        time_hour tinyint not null,
        time_minute tinyint not null,
        event_context varchar(255) not null,
        user_organization varchar(255),
        user_name varchar(255),
        event_type varchar(255) not null,
        report_uri varchar(255),
        editing_action varchar(255),
        query_execution_time integer not null,
        report_rendering_time integer not null,
        total_report_execution_time integer not null,
        time_stamp datetime not null,
        primary key (id)
    ) ENGINE=InnoDB;


    create index time_stamp_index on JIReportMonitoringFact (time_stamp);

    create index user_name_index on JIReportMonitoringFact (user_name);

    create index query_execution_time_index on JIReportMonitoringFact (query_execution_time);

    create index time_minute_index on JIReportMonitoringFact (time_minute);

    create index user_organization_index on JIReportMonitoringFact (user_organization);

    create index date_day_index on JIReportMonitoringFact (date_day);

    create index date_year_index on JIReportMonitoringFact (date_year);

    create index total_report_exec_time_index on JIReportMonitoringFact (total_report_execution_time);

    create index time_hour_index on JIReportMonitoringFact (time_hour);

    create index date_month_index on JIReportMonitoringFact (date_month);

    create index report_uri_index on JIReportMonitoringFact (report_uri);

    create index editing_action_index on JIReportMonitoringFact (editing_action);

    create index event_context_index on JIReportMonitoringFact (event_context);

    create index report_rendering_time_index on JIReportMonitoringFact (report_rendering_time);

    create index event_type_index_2 on JIReportMonitoringFact (event_type);

