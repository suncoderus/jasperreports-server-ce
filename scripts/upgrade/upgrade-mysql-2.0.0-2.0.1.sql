--
-- Add tables and fields
--

    create table JIProfileAttribute (
        id bigint not null auto_increment,
        attrName varchar(255) not null,
        attrValue varchar(255) not null,
        principalobjectclass varchar(255) not null,
        principalobjectid bigint not null,
        primary key (id)
    ) type=InnoDB;


--
-- Add constraints
--


--
-- Fix data
--


--
-- Add 'not null' constraints
--

