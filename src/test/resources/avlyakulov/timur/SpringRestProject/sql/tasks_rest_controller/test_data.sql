insert into application_user (id, username, password)
values ('de3cdc05-9aea-49e8-a6be-10ca2b44ef6a', 'user1', '{noop}password1'),
       ('ab9384c0-bf63-4ad1-a53a-164f0554a7c1', 'user2', '{noop}password2');
-- {noop} - обозначает то что пароль не шифруется


insert into tasks (id, details, completed)
values ('f69d7733-558d-46e3-90e1-e20ab7a3dd44', 'Первая задача', false),
       ('276340f3-bd41-45ab-adc6-2254f9650517', 'Вторая задача', true);