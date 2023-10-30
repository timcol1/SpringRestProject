create table application_user (
    id  uuid primary key,
    username varchar not null,
    password varchar
);

create unique index idx_application_user_username on application_user(username)