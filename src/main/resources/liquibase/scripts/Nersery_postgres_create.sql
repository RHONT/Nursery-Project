-- liquibase formatted sql
-- changeset Evgeniy:1


CREATE TABLE "visitors" (
	"chat_id" bigint NOT NULL,
	CONSTRAINT "visitors_pk" PRIMARY KEY ("chat_id")
);

CREATE TABLE "person" (
	"id_person" serial NOT NULL,
	"id_chat" bigint,
	"name" varchar(60),
	"phone" TEXT,
	CONSTRAINT "person_pk" PRIMARY KEY ("id_person")
);

CREATE TABLE "report" (
	"id_report" serial NOT NULL,
	"id_person" bigint,
	"forteit" bigint default '0',
	"day_report" bigint DEFAULT '30',
	CONSTRAINT "report_pk" PRIMARY KEY ("id_report")
);

CREATE TABLE "nursary" (
	"id_nursary" serial NOT NULL,
	"name_nursary" TEXT,
	"about" TEXT,
	"infrastructure" TEXT,
	"accident_prevention" TEXT,
	"list_document" TEXT,
	CONSTRAINT "nursary_pk" PRIMARY KEY ("id_nursary")
);

CREATE TABLE "pet" (
	"id_nursary" bigint NOT NULL,
	"id_pet" serial NOT NULL,
	"nickname" varchar(50),
	"foto" bytea,
	"diet" TEXT,
	"dating_rule" TEXT,
	"transport_rule" TEXT,
	"house_recomend" TEXT,
	"cynologist_advice" TEXT,
	"cynologist_advice_up" TEXT,
	"reasons_refusal" TEXT,
	"person_id" bigint,
	CONSTRAINT "pet_pk" PRIMARY KEY ("id_pet")
);

CREATE TABLE "data_report" (
	"id_report" bigint NOT NULL,
	"date_datas" DATE NOT NULL,
	"foto" bytea,
	"diet" TEXT,
	"health" TEXT,
	"demeanor" TEXT,
	CONSTRAINT "data_report_pk" PRIMARY KEY ("date_datas")
);

ALTER TABLE "report" ADD CONSTRAINT "report_fk0" FOREIGN KEY ("id_person") REFERENCES "person"("id_person");

ALTER TABLE "pet" ADD CONSTRAINT "pet_fk0" FOREIGN KEY ("id_nursary") REFERENCES "nursary"("id_nursary");
ALTER TABLE "pet" ADD CONSTRAINT "pet_fk1" FOREIGN KEY ("person_id") REFERENCES "person"("id_person");

ALTER TABLE "data_report" ADD CONSTRAINT "data_report_fk0" FOREIGN KEY ("id_report") REFERENCES "report"("id_report");


insert into nursary(name_nursary, about,infrastructure,accident_prevention,list_document)
values
    ('Кошки','О приюте','Схема проезда','Правила поведения','Снилс, Паспорт'),
    ('Собаки','О приюте','Схема проезда','Правила поведения','Снилс, Паспорт');







