
    alter table JITenant add
        theme varchar(250);
    update JITenant set
        theme = 'default';
    delete from JIRepositoryCache;
