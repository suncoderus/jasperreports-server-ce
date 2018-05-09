    alter table JIBeanDatasource 
        drop 
        foreign key FK674BF34F4D66224;

    alter table JIJNDIJdbcDatasource 
        drop 
        foreign key FK7F9DA248F4D66224;

    alter table JIJdbcDatasource 
        drop 
        foreign key FKC8BDFCBFF4D66224;

    alter table JIMondrianConnection 
        drop 
        foreign key FK4FF53B197E642982;

    alter table JIQuery 
        drop 
        foreign key FKCBCB0EC97749C54E;

    alter table JIReportDataSource 
        drop 
        foreign key FK6A248C18A8BF376D;

    alter table JIReportUnit 
        drop 
        foreign key FK98818B777E642982;

    drop table JIReportDataSource;

    alter table JIBeanDatasource 
        add index FK674BF34A8BF376D (id), 
        add constraint FK674BF34A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIJNDIJdbcDatasource 
        add index FK7F9DA248A8BF376D (id), 
        add constraint FK7F9DA248A8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIJdbcDatasource 
        add index FKC8BDFCBFA8BF376D (id), 
        add constraint FKC8BDFCBFA8BF376D 
        foreign key (id) 
        references JIResource (id);

    alter table JIMondrianConnection 
        add index FK4FF53B19324CFECB (reportDataSource), 
        add constraint FK4FF53B19324CFECB 
        foreign key (reportDataSource) 
        references JIResource (id);

    alter table JIQuery 
        add index FKCBCB0EC92B329A97 (dataSource), 
        add constraint FKCBCB0EC92B329A97 
        foreign key (dataSource) 
        references JIResource (id);

    alter table JIReportUnit 
        add index FK98818B77324CFECB (reportDataSource), 
        add constraint FK98818B77324CFECB 
        foreign key (reportDataSource) 
        references JIResource (id);

    alter table JIInputControl
        add column visible bit default 1;

