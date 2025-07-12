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
    CONSTRAINT payments_payment_mode_check CHECK (payment_mode::text = ANY (ARRAY['PAYMENT_MODE_CASH'::character varying, 'PAYMENT_MODE_UPI'::character varying]::text[])),
    CONSTRAINT payments_payment_status_check CHECK (payment_status::text = ANY (ARRAY['PAYMENT_STATUS_INITIATED'::character varying, 'PAYMENT_STATUS_FAILED'::character varying, 'PAYMENT_STATUS_SUCCESS'::character varying, 'PAYMENT_STATUS_REFUND_INITIATED'::character varying, 'PAYMENT_STATUS_REFUNDED'::character varying]::text[]))
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.payments
    OWNER to root;