-- liquibase formatted sql
-- changeset Evgeniy:1

CREATE TABLE "visitors"
(
-- чат пользователя
    "chat_id"      bigint NOT NULL,
-- 	заносим номер питомника, переменная нужна для быстрого определения с каким питомником мы будем работать.
    "name_nursery" varchar,
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
    "forteit"    bigint default '0',  -- 	Штраф за плохое заполнения отчета.
    "day_report" int    DEFAULT '30', -- 	Штраф за плохое заполнения отчета.
    CONSTRAINT "report_pk" PRIMARY KEY ("id_report")
);

CREATE TABLE "nursery"
(
    "id_nursery"              serial NOT NULL,
    "name_nursery"            varchar, -- имя приюта
    "about"                   varchar, -- о приюте
    "infrastructure"          varchar, -- Все что касается заезда, проезда, охраны и т.д.
    "accident_prevention"     varchar, -- правила поведения на территории
    "how_get_pet"             varchar, -- Процедура, как взять животное из этого приюта
    "list_document"           varchar, -- нужные документы для опекунства
    "dating_rule"             varchar, -- правило как нужно себя вести при первом знакомстве с животным
    "transport_rule"          varchar, -- правила перевозки животного
    "house_recommend_baby"    varchar, -- рекомендации по обустройству мелких
    "house_recommend_adult"   varchar,-- рекомендации по обустройству взрослых
    "house_recommend_invalid" varchar, -- рекомендации по обустройству дома для животного с ограничениями
    "cynologist_advice"       varchar, -- первоначальные советы кинологи (для кошек не надо)
    "cynologist_advice_up"    varchar, -- продвинутые советы кинологи (для кошек не надо)
    "reasons_refusal"         varchar, -- почему нельзя взять


    CONSTRAINT "nursary_pk" PRIMARY KEY ("id_nursery")
);

CREATE TABLE "pet"
(
    "id_nursery" bigint NOT NULL,
    "id_pet"     serial NOT NULL,
    "nickname"   varchar(50),
    "birthday"   date,
    "foto" bytea,
    "invalid"    bool default 'false',
    "person_id"  bigint,
    CONSTRAINT "pet_pk" PRIMARY KEY ("id_pet")
);

CREATE TABLE "data_report"
(
    "id_report"      bigint NOT NULL,
    "date_report"    DATE   NOT NULL,
    "foto" bytea,
    "message_person" varchar,
    CONSTRAINT "data_report_pk" PRIMARY KEY ("date_report")
);


ALTER TABLE "report"
    ADD CONSTRAINT "report_fk0" FOREIGN KEY ("id_person") REFERENCES "person" ("id_person");

ALTER TABLE "pet"
    ADD CONSTRAINT "pet_fk0" FOREIGN KEY ("id_nursery") REFERENCES "nursery" ("id_nursery");
ALTER TABLE "pet"
    ADD CONSTRAINT "pet_fk1" FOREIGN KEY ("person_id") REFERENCES "person" ("id_person");

ALTER TABLE "data_report"
    ADD CONSTRAINT "data_report_fk0" FOREIGN KEY ("id_report") REFERENCES "report" ("id_report");


insert into nursery(name_nursery, about, infrastructure, accident_prevention, list_document)
values ('Кошки', 'О приюте', 'Схема проезда', 'Правила поведения', 'Снилс, Паспорт'),
       ('Собаки', 'О приюте', 'Схема проезда', 'Правила поведения', 'Снилс, Паспорт');

-- changeset Alexander:1
CREATE TABLE "volunteers"
(
    "volunteer_id" serial primary key ,
    "volunteer_name" text,
    "phone" text,
    "telegram_name" varchar,
    "free" boolean
)








