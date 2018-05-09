--
--
-- 5.2.0 to 5.5.0
--
-- Fix for bug 33491 - REST2: Resources: Delete often fails with constraint violation error on jiaccessevent table
--

    alter table JIAccessEvent
        drop constraint FK47FB3CD7F254B53E;

    alter table JIAccessEvent
        add constraint FK47FB3CD7F254B53E
        foreign key (resource_id)
        references JIResource
        on delete cascade;
