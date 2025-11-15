create schema testcontainers;

create table if not exists testcontainers.customers (
    id BIGINT not null,
    name VARCHAR not null,
    email VARCHAR not null,
	updated_at TIMESTAMPTZ NOT NULL,
    primary key (id)
);