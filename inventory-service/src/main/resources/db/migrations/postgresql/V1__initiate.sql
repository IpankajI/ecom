-- Table: public.inventories

-- DROP TABLE IF EXISTS public.inventories;

CREATE TABLE IF NOT EXISTS public.inventories
(
    id bigint NOT NULL,
    product_id character varying(255) COLLATE pg_catalog."default",
    quantity integer,
    sku_code character varying(255) COLLATE pg_catalog."default",
    store_id bigint,
    CONSTRAINT inventories_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.inventories
    OWNER to root;


-- Table: public.inventory_operations

-- DROP TABLE IF EXISTS public.inventory_operations;

CREATE TABLE IF NOT EXISTS public.inventory_operations
(
    operation_id bigint NOT NULL,
    create_at timestamp(6) without time zone,
    inventory_id bigint,
    inventory_operation_status character varying(255) COLLATE pg_catalog."default",
    inventory_operation_type character varying(255) COLLATE pg_catalog."default",
    quantity integer,
    updated_at timestamp(6) without time zone,
    CONSTRAINT inventory_operations_pkey PRIMARY KEY (operation_id),
    CONSTRAINT inventory_operations_inventory_operation_status_check CHECK (inventory_operation_status::text = ANY (ARRAY['INVENTORY_OPERATION_STATUS_INITIATED'::character varying, 'INVENTORY_OPERATION_STATUS_COMPLETED'::character varying, 'INVENTORY_OPERATION_STATUS_EXPIRED'::character varying]::text[])),
    CONSTRAINT inventory_operations_inventory_operation_type_check CHECK (inventory_operation_type::text = ANY (ARRAY['INVENTORY_OPERATION_TYPE_ADD'::character varying, 'INVENTORY_OPERATION_TYPE_CLAIM'::character varying]::text[]))
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.inventory_operations
    OWNER to root;