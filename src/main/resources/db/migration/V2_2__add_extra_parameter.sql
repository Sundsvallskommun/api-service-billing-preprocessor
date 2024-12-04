create table extra_parameter (
    billing_record_id varchar(255) not null,
    `key`             varchar(255) not null,
    `value`             varchar(255),
    primary key (billing_record_id, `key`)
) engine = InnoDB;

alter table if exists extra_parameter
    add constraint fk_billing_record_id_extra_parameter
    foreign key (billing_record_id)
    references billing_record (id);

create index idx_extra_parameter_key
    on extra_parameter (`key`);
