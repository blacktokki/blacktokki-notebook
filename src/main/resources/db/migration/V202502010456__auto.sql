
    create table content (
        co_id bigint not null auto_increment,
        co_deleted datetime(6),
        co_description LONGTEXT,
        co_input varchar(255),
        co_order integer not null,
        co_parent_id bigint,
        co_title varchar(255),
        co_type varchar(255) not null,
        co_updated datetime(6),
        us_id bigint,
        primary key (co_id)
    ) engine=InnoDB;
