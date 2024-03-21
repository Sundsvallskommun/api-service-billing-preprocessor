
    create table billing_record (
        approved datetime(6),
        created datetime(6),
        modified datetime(6),
        approved_by varchar(255),
        category varchar(255) not null,
        id varchar(255) not null,
        status varchar(255) not null,
        type varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table description (
        id bigint not null auto_increment,
        `invoice_row_id` bigint not null,
        text varchar(255),
        type ENUM('DETAILED', 'STANDARD'),
        primary key (id)
    ) engine=InnoDB;

    create table file_configuration (
            id bigint not null auto_increment,
            category_tag varchar(255),
            creator_name varchar(255),
            file_name_pattern varchar(255),
            type varchar(255),
            primary key (id)
    ) engine=InnoDB;

    create table invoice (
        `date` date,
        due_date date,
        total_amount float(23),
        customer_id varchar(255),
        customer_reference varchar(255),
        description varchar(255),
        id varchar(255) not null,
        our_reference varchar(255),
        reference_id varchar(255),
        primary key (id)
    ) engine=InnoDB;

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

    create table invoice_row (
        cost_per_unit float(23),
        quantity integer,
        total_amount float(23),
        id bigint not null auto_increment,
        accural_key varchar(255),
        activity varchar(255),
        article varchar(255),
        cost_center varchar(255),
        counter_part varchar(255),
        department varchar(255),
        `invoice_id` varchar(255) not null,
        project varchar(255),
        subaccount varchar(255),
        vat_code varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table recipient (
        care_of varchar(255),
        city varchar(255),
        first_name varchar(255),
        id varchar(255) not null,
        last_name varchar(255),
        legal_id varchar(255),
        organization_name varchar(255),
        party_id varchar(255),
        postal_code varchar(255),
        street varchar(255),
        user_id varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create index idx_billing_record_category_status 
       on billing_record (category, status);

    create index idx_file_configuration_type_category_tag
       on file_configuration (type, category_tag);

    create index idx_file_configuration_creator_name
       on file_configuration (creator_name);
       
    alter table if exists file_configuration
       add constraint uq_type_category_tag unique (type, category_tag);

    alter table if exists file_configuration
       add constraint uq_creator_name unique (creator_name);
       
    create index idx_invoice_file_status 
       on invoice_file (status);

    alter table if exists invoice_file 
       add constraint uq_file_name unique (name);

    alter table if exists description 
       add constraint fk_invoice_row_id_description 
       foreign key (`invoice_row_id`) 
       references invoice_row (id);

    alter table if exists invoice 
       add constraint fk_billing_record_id_invoice 
       foreign key (id) 
       references billing_record (id);

    alter table if exists invoice_row 
       add constraint fk_invoice_id_invoice_row 
       foreign key (`invoice_id`) 
       references invoice (id);

    alter table if exists recipient 
       add constraint fk_billing_record_id_recipient 
       foreign key (id) 
       references billing_record (id);
