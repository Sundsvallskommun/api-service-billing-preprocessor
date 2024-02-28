create table file_configuration (
    id bigint not null auto_increment,
    type varchar(255) not null,
    category_tag varchar(255) not null,
    file_name_pattern varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create index idx_file_configuration_type_category_tag on file_configuration (type, category_tag);
alter table file_configuration add constraint uq_type_category_tag unique(type, category_tag);