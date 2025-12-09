
    create table personal_access_token (
        pa_id bigint not null auto_increment,
        pa_created datetime(6) not null,
        pa_expired datetime(6) not null,
        pa_name varchar(255) not null,
        pa_token varchar(255) not null,
        us_id bigint,
        primary key (pa_id)
    ) engine=InnoDB;
