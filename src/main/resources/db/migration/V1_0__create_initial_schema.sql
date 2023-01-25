
    create table billing_record (
       id varchar(255) not null,
        category varchar(255) not null,
        certified datetime(6),
        certified_by varchar(255),
        created datetime(6),
        modified datetime(6),
        status varchar(255) not null,
        type varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table description (
       id bigint not null auto_increment,
        text varchar(255),
        type ENUM('DETAILED', 'STANDARD'),
        invoice_row_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table invoice (
       id varchar(255) not null,
        customer_id varchar(255),
        customer_reference varchar(255),
        description varchar(255),
        due_date date,
        our_reference varchar(255),
        reference_id varchar(255),
        total_amount float,
        primary key (id)
    ) engine=InnoDB;

    create table invoice_row (
       id bigint not null auto_increment,
        accural_key varchar(255),
        activity varchar(255),
        article varchar(255),
        cost_center varchar(255),
        counter_part varchar(255),
        department varchar(255),
        project varchar(255),
        subaccount varchar(255),
        cost_per_unit float,
        quantity integer,
        total_amount float,
        vat_code varchar(255),
        invoice_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table issuer (
       id varchar(255) not null,
        care_of varchar(255),
        city varchar(255),
        postal_code varchar(255),
        street varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        organization_name varchar(255),
        party_id varchar(255) not null,
        user_id varchar(255),
        primary key (id)
    ) engine=InnoDB;
create index idx_billing_record_category_status on billing_record (category, status);

    alter table description 
       add constraint fk_invoice_row_id_description 
       foreign key (invoice_row_id) 
       references invoice_row (id);

    alter table invoice 
       add constraint fk_billing_record_id_invoice 
       foreign key (id) 
       references billing_record (id);

    alter table invoice_row 
       add constraint fk_invoice_id_invoice_row 
       foreign key (invoice_id) 
       references invoice (id);

    alter table issuer 
       add constraint fk_billing_record_id_issuer 
       foreign key (id) 
       references billing_record (id);
