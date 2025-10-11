create schema testcontainers;

create table if not exists testcontainers.customers (
    id bigserial not null,
    name varchar not null,
    email varchar not null,
    primary key (id)
);

alter table testcontainers.customers replica identity full;

insert into testcontainers.customers (name, email) values 
	('Sarah', 'sarah@mail.com'),
	('Mike','mike@mail.com'); 