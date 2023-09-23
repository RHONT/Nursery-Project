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
    "id_nursery" bigint not null,
    "id_person"  serial NOT NULL,
    "id_chat"    bigint,
    "name"       varchar(60),
    "phone"      varchar,
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
    "invalid"    bool default 'false',
    "person_id"  bigint,
    CONSTRAINT "pet_pk" PRIMARY KEY ("id_pet")
);

CREATE TABLE "data_report"
(
    "id_data_report" serial NOT NULL,
    "id_report"      bigint NOT NULL,
    "date_report"    DATE   NOT NULL,
    "foto"           oid,
    "file_size"      bigint,
    "media_type"     varchar,
    "message_person" varchar,
    "check_message"          boolean default false,
    unique (id_report, date_report)
);


ALTER TABLE "report"
    ADD CONSTRAINT "report_fk0" FOREIGN KEY ("id_person") REFERENCES "person" ("id_person");

ALTER TABLE "person"
    ADD CONSTRAINT "person.fk2" FOREIGN KEY ("id_nursery") REFERENCES "nursery" ("id_nursery");

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
    "volunteer_id"      serial primary key,
    "volunteer_chat_id" bigint,
    "volunteer_name"    text,
    "phone"             text,
    "telegram_name"     varchar,
    "busy"              boolean default true
)

-- changeset Alexander:2
insert into person (id_nursery, id_person, name, phone)
values ('1','1','Маргарита','111111111'),
       ('1','2','Евгений','222222222'),
       ('2','3','Александр','333333333');
insert into pet (id_nursery, id_pet, nickname, birthday, invalid, person_id)
values ('1','1','Картошка','2021-11-11','false',null),
       ('1','2','Батон','2022-12-22','false',null),
       ('1','3','Вертолет','2020-12-12','true',null),
       ('2','4','Портос','2021-02-11','false',null),
       ('2','5','Спартак','2019-02-21','false',null),
       ('2','6','Тыква','2021-02-11','true',null);
insert into visitors (chat_id, name_nursery)
values ('123','Кошки'),
       ('234','Собаки'),
       ('345','Собаки'),
       ('456','Кошки'),
       ('567','Собаки');
insert into volunteers (volunteer_id, volunteer_chat_id, volunteer_name, phone, telegram_name)
values ('111','111','Гриша','444444444',''),
       ('222','222','Маша','555555555',''),
       ('333','333','Оля','666666666','');

-- changeset Alexander:3
UPDATE nursery
SET how_get_pet = 'Для того, что бы взять животное, вы должны прийти в приют для беседы и принести с собой необходимый пакет документов.',
    dating_rule = 'Прийти в часы посещения, предупредив волонтеров о приходе. Быть готовым слушать указания сотрудников приюта.',
    transport_rule = 'Перевозить животное следует в специальной переноске для кошек.',
    house_recommend_baby = 'Котятам рекомендуется дом, где они могли бы быть в безопасности. Не рекомендуется присутствие детей и предметов, что могут им повредить.',
    house_recommend_adult = 'Взрослые кошки должны содержаться в доме оборудованном специальными предметами для кошек: когтеточка, лоток, домик.',
    house_recommend_invalid = 'Кошки с ограничениями имеют особые сложности в содержании - вам потребуется индивидуальна консультация с сотрудником приюта.',
    reasons_refusal = 'Любые судимости за жестокое обращение с животными.Отсутствие дома или работы у усыновителя.'
WHERE id_nursery = 1;
UPDATE nursery
SET how_get_pet = 'Для того, что бы взять животное, вы должны прийти в приют для беседы и принести с собой необходимый пакет документов.',
    dating_rule = 'Прийти в часы посещения, предупредив волонтеров о приходе. Быть готовым слушать указания сотрудников приюта.',
    transport_rule = 'Перевозить животное следует в специальной переноске или на поводке, в зависимости от конкретного животного.',
    house_recommend_baby = 'Котятам рекомендуется дом, где они могли бы быть в безопасности. Не рекомендуется присутствие детей и предметов, что могут им повредить, особенно химических веществ.',
    house_recommend_adult = 'Подходящий для конкретной собаки размер дома и наличие необходимых для собаки предметов',
    house_recommend_invalid = 'Собаки с ограничениями имеют особые сложности в содержании - вам потребуется индивидуальна консультация с сотрудником приюта.',
    reasons_refusal = 'Любые судимости за жестокое обращение с животными.Отсутствие дома или работы у усыновителя.',
    cynologist_advice = 'Держать хвост по ветру)',
    cynologist_advice_up= 'И уши торчком.'
WHERE id_nursery = 2;






