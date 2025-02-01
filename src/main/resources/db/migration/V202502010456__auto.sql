
    create table content (
        co_id bigint not null auto_increment,
        co_content varchar(255),
        co_input varchar(255),
        co_parent_id bigint,
        co_title varchar(255),
        co_type varchar(255) not null,
        dt_updated datetime(6),
        us_id bigint,
        primary key (co_id)
    ) engine=InnoDB;
