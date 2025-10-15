create schema testcontainers;

create table if not exists testcontainers.customers (
    id bigserial not null,
    name varchar not null,
    email varchar not null,
    primary key (id)
);