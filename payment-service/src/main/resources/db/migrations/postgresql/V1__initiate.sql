-- Table: public.payments

-- DROP TABLE IF EXISTS public.payments;

CREATE TABLE IF NOT EXISTS public.payments
(
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    order_id bigint,
    payment_mode character varying(255) COLLATE pg_catalog."default",
    payment_status character varying(255) COLLATE pg_catalog."default",
    updated_at timestamp(6) without time zone,
    CONSTRAINT payments_pkey PRIMARY KEY (id),
    CONSTRAINT payments_payment_mode_check CHECK (payment_mode::text = ANY (ARRAY['PaymentModeCash'::character varying, 'PaymentModeUPI'::character varying]::text[])),
    CONSTRAINT payments_payment_status_check CHECK (payment_status::text = ANY (ARRAY['PaymentStatusInitiated'::character varying, 'PaymentStatusFailed'::character varying, 'PaymentStatusSuccess'::character varying, 'PaymentStatusRefundInitiated'::character varying, 'PaymentStatusRefunded'::character varying]::text[]))
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.payments
    OWNER to root;