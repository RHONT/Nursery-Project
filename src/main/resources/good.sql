create table "outer"
(
    id_outer serial,
    temp     int,
    shraf int,
    primary key (id_outer)
);

create table "inner"
(
    id_outer bigint,
    id_inner serial,
    temp     int,
    primary key (id_inner),
    foreign key(id_outer) references "outer"(id_outer)
);

update "outer"
set temp=30-(select count(id_inner) from "inner" where "inner".id_outer="outer".id_outer);


update "outer"
set shraf=(select count(*) from "inner" where "inner".id_outer="outer".id_outer and "inner".temp IS NULL);


select count(*) from "inner" where temp IS NULL