ALTER TABLE description MODIFY COLUMN `type` VARCHAR(8) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL NULL;

create table invoice_file (
    created datetime(6),
    id bigint not null auto_increment,
    sent datetime(6),
    name varchar(255),
    status varchar(255),
    type varchar(255),
    content longtext,
    primary key (id)
) engine=InnoDB;

create index idx_invoice_file_status
   on invoice_file (status);

    alter table if exists invoice_file 
       add constraint uq_file_name unique (name);
       