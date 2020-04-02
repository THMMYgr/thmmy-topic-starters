create table topic_starter
(
    id                varchar(255) not null
        constraint topic_starter_id_pkey primary key,
    topic_id          bigint       not null,
    topic_url         varchar(255) not null,
    starter_username  varchar(255) not null,
    starter_url       varchar(255) not null,
    starter_id        bigint       not null,
    board_title       varchar(255) not null,
    board_url         varchar(255) not null,
    board_id          bigint       not null,
    topic_subject     varchar(255) not null,
    number_of_replies bigint       not null,
    number_of_views   bigint       not null
);
