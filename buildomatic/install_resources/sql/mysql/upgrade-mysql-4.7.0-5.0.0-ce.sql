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
	
	
	