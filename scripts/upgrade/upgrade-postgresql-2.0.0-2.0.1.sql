--
-- Add tables and fields
--

    create table JIProfileAttribute (
        id int8 not null,
        attrName varchar(255) not null,
        attrValue varchar(255) not null,
        principalobjectclass varchar(255) not null,
        principalobjectid int8 not null,
        primary key (id)
    );

--
-- Add constraints
--


--
-- Fix data
--

--
-- Add 'not null' constraints
--

