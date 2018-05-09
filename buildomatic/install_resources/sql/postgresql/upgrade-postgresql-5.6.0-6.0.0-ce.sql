--
-- 5.6.0 to 6.0.0
--
-- Created table JIReportThumbnail to store thumbnails of reports
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

