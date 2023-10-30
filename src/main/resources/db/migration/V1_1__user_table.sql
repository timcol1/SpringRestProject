create table application_user (
    id  uuid primary key,
    username varchar not null,
    password varchar
);

create unique index idx_application_user_username on application_user(username);

alter table tasks
    add column id_application_user uuid not null references application_user(id);

create index idx_task_application_user on tasks(id_application_user);