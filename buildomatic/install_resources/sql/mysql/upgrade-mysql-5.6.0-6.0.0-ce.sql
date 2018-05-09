--
-- 5.6.0 to 6.0.0
--
-- Created table JIReportThumbnail to store thumbnails of reports
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

