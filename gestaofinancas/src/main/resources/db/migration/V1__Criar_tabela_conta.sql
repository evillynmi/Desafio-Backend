CREATE SEQUENCE IF NOT EXISTS contas_id_seq;

CREATE TABLE IF NOT EXISTS public.contas
(
    id integer NOT NULL DEFAULT nextval('contas_id_seq'::regclass),
    data_vencimento date,
    data_pagamento date,
    valor numeric(10,2),
    descricao character varying(255) COLLATE pg_catalog."default",
    situacao character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT contas_pkey PRIMARY KEY (id)
);