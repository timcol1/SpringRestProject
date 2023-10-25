create table tasks
(
    id        uuid primary key,
    details   varchar,
    completed boolean not null default false
)