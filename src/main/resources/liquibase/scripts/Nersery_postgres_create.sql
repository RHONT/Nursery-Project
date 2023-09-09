-- liquibase formatted sql
-- changeset Evgeniy:1

CREATE TABLE "visitors"
(
-- чат пользователя
    "chat_id"        bigint NOT NULL,
-- 	заносим номер питомника, переменная нужна для быстрого определения с каким питомником мы будем работать.
    "name_nursary" varchar,
    CONSTRAINT "visitors_pk" PRIMARY KEY ("chat_id")
);

CREATE TABLE "person"
(
    "id_person" serial NOT NULL,
    "id_chat"   bigint,
    "name"      varchar(60),
    "phone"     varchar,
    CONSTRAINT "person_pk" PRIMARY KEY ("id_person")
);


CREATE TABLE "report"
(
    "id_report"  serial NOT NULL,
    "id_person"  bigint,
    "forteit"    bigint default '0', -- 	Штраф за плохое заполнения отчета.
    "day_report" int    DEFAULT '30',
    CONSTRAINT "report_pk" PRIMARY KEY ("id_report")
);

CREATE TABLE "nursary"
(
    "id_nursary"          serial NOT NULL,
    "name_nursary"        varchar,
    "about"               varchar,
    "infrastructure"      varchar, -- Все что касается заезда, проезда, охраны и т.д.
    "accident_prevention" varchar, -- правила поведения на территории
    "how_get_pet"         varchar, -- Процедура, как взять животное из этого приюта
    "list_document"       varchar,
    CONSTRAINT "nursary_pk" PRIMARY KEY ("id_nursary")
);

CREATE TABLE "pet"
(
    "id_nursary"           bigint NOT NULL,
    "id_pet"               serial NOT NULL,
    "nickname"             varchar(50),
    "age"                  int,
    "foto"                 bytea,
    "diet"                 varchar,
    "dating_rule"          varchar,
    "transport_rule"       varchar,
    "house_recomend"       varchar,
    "cynologist_advice"    varchar,
    "cynologist_advice_up" varchar,
    "reasons_refusal"      varchar, -- почему нельзя взять
    "invalid"              bool default 'false',
    "person_id"            bigint,
    CONSTRAINT "pet_pk" PRIMARY KEY ("id_pet")
);

CREATE TABLE "data_report"
(
    "id_report"  bigint NOT NULL,
    "date_datas" DATE   NOT NULL,
    "foto"       bytea,
    "diet"       varchar,
    "health"     varchar,
    "demeanor"   varchar, -- поведение животного
    CONSTRAINT "data_report_pk" PRIMARY KEY ("date_datas")
);

CREATE TABLE "house_recommend_variant"
(
    "id_house_recommend_variant" serial,
    "type_animal"                varchar(15),
    "age"                        int,
    "description"                varchar,
    CONSTRAINT "id_house_recommend_variant_pk" PRIMARY KEY ("id_house_recommend_variant")
);

ALTER TABLE "report"
    ADD CONSTRAINT "report_fk0" FOREIGN KEY ("id_person") REFERENCES "person" ("id_person");

ALTER TABLE "pet"
    ADD CONSTRAINT "pet_fk0" FOREIGN KEY ("id_nursary") REFERENCES "nursary" ("id_nursary");
ALTER TABLE "pet"
    ADD CONSTRAINT "pet_fk1" FOREIGN KEY ("person_id") REFERENCES "person" ("id_person");

ALTER TABLE "data_report"
    ADD CONSTRAINT "data_report_fk0" FOREIGN KEY ("id_report") REFERENCES "report" ("id_report");


insert into nursary(name_nursary, about, infrastructure, accident_prevention, list_document)
values ('Кошки', 'О приюте', 'Схема проезда', 'Правила поведения', 'Снилс, Паспорт'),
       ('Собаки', 'О приюте', 'Схема проезда', 'Правила поведения', 'Снилс, Паспорт');







