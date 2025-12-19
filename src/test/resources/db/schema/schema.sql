
    create table account_information (
        amount decimal(38,2),
        invoice_row_id bigint not null,
        accural_key varchar(255),
        activity varchar(255),
        article varchar(255),
        cost_center varchar(255),
        counter_part varchar(255),
        department varchar(255),
        project varchar(255),
        subaccount varchar(255)
    ) engine=InnoDB;

    create table billing_record (
        transfer_date date,
        approved datetime(6),
        created datetime(6),
        modified datetime(6),
        approved_by varchar(255),
        category varchar(255) not null,
        id varchar(255) not null,
        municipality_id varchar(255) not null,
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

    create table extra_parameter (
        billing_record_id varchar(255) not null,
        `key` varchar(255) not null,
        `value` varchar(255),
        primary key (billing_record_id, `key`)
    ) engine=InnoDB;

    create table file_configuration (
        id bigint not null auto_increment,
        category_tag varchar(255) not null,
        creator_name varchar(255) not null,
        encoding varchar(255) not null,
        file_name_pattern varchar(255) not null,
        type varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table invoice (
        `date` date,
        due_date date,
        total_amount decimal(38,2),
        customer_id varchar(255),
        customer_reference varchar(255),
        description varchar(255),
        id varchar(255) not null,
        our_reference varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table invoice_file (
        created datetime(6),
        id bigint not null auto_increment,
        sent datetime(6),
        encoding varchar(255),
        municipality_id varchar(255) not null,
        name varchar(255),
        status varchar(255),
        type varchar(255),
        content longtext,
        primary key (id)
    ) engine=InnoDB;

    create table invoice_row (
        cost_per_unit decimal(38,2),
        quantity decimal(38,2),
        total_amount decimal(38,2),
        id bigint not null auto_increment,
        `invoice_id` varchar(255) not null,
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

    create index idx_invoice_row_id 
       on account_information (invoice_row_id);

    create index idx_billing_record_category_status 
       on billing_record (category, status);

    create index idx_billing_record_municipality_id 
       on billing_record (municipality_id);

    create index idx_billing_record_status_municipalityId_transfer_date 
       on billing_record (status, municipality_id, transfer_date);

    create index idx_extra_parameter_key 
       on extra_parameter (`key`);

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

    create index idx_invoice_file_municipality_id 
       on invoice_file (municipality_id);

    alter table if exists invoice_file 
       add constraint uq_file_name unique (name);

    alter table if exists account_information 
       add constraint fk_invoice_row_id_account_information 
       foreign key (invoice_row_id) 
       references invoice_row (id);

    alter table if exists description 
       add constraint fk_invoice_row_id_description 
       foreign key (`invoice_row_id`) 
       references invoice_row (id);

    alter table if exists extra_parameter 
       add constraint fk_billing_record_id_extra_parameter 
       foreign key (billing_record_id) 
       references billing_record (id);

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
