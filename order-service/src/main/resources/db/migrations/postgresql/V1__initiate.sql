-- Table: public.orders

-- DROP TABLE IF EXISTS public.orders;

CREATE TABLE IF NOT EXISTS public.orders
(
    id bigint NOT NULL,
    order_number character varying(255) COLLATE pg_catalog."default",
    payment_status character varying(255) COLLATE pg_catalog."default",
    status character varying(255) COLLATE pg_catalog."default",
    total_amount numeric(38,2),
    CONSTRAINT orders_pkey PRIMARY KEY (id),
    CONSTRAINT orders_payment_status_check CHECK (payment_status::text = ANY (ARRAY['ORDER_PAYMENT_STATUS_PENDING'::character varying, 'ORDER_PAYMENT_STATUS_INITIATED'::character varying, 'ORDER_PAYMENT_STATUS_COMPLETED'::character varying, 'ORDER_PAYMENT_STATUS_FAILED'::character varying, 'ORDER_PAYMENT_STATUS_REFUND_INITIATED'::character varying, 'ORDER_PAYMENT_STATUS_REFUNDED'::character varying]::text[])),
    CONSTRAINT orders_status_check CHECK (status::text = ANY (ARRAY['ORDER_STATUS_CREATED'::character varying, 'ORDER_STATUS_CANCELLED'::character varying, 'ORDER_STATUS_COMPLETE'::character varying]::text[]))
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.orders
    OWNER to root;


-- Table: public.order_line_items

-- DROP TABLE IF EXISTS public.order_line_items;

CREATE TABLE IF NOT EXISTS public.order_line_items
(
    id bigint NOT NULL,
    inventory_claim_id bigint,
    inventory_id bigint NOT NULL,
    quantity integer,
    total_amount numeric(38,2),
    order_id bigint,
    CONSTRAINT order_line_items_pkey PRIMARY KEY (id),
    CONSTRAINT fkdjnh2emxm9tt6mrpfabdvbs0c FOREIGN KEY (order_id)
        REFERENCES public.orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.order_line_items
    OWNER to root;