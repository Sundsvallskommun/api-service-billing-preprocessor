    create table invoice_file (
        created datetime(6),
        id bigint not null auto_increment,
        sent datetime(6),
        name varchar(255),
        type varchar(255),
        content tinytext,
        status enum ('GENERATED','SEND_SUCCESSFUL','SEND_FAILED'),
        primary key (id)
    ) engine=InnoDB;

    create index idx_invoice_file_status 
       on invoice_file (status);

    alter table if exists invoice_file 
       add constraint uq_name unique (name);
       