    -- Add account information table
    create table account_information (
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

    -- Add indexes and constraint for account information table
    create index idx_invoice_row_id 
       on account_information (invoice_row_id);

    alter table if exists account_information 
       add constraint fk_invoice_row_id_account_information 
       foreign key (invoice_row_id) 
       references invoice_row (id);
       
    -- Migrate data
    insert into account_information (
       select id,
              accural_key,
              activity,
              article,
              cost_center,
              counter_part,
              department,
              project,
              subaccount
       from invoice_row
    );

    -- Remove columns that have been moved to account information table
    alter table invoice_row drop column accural_key;
    alter table invoice_row drop column activity;
    alter table invoice_row drop column article;
    alter table invoice_row drop column cost_center;
    alter table invoice_row drop column counter_part;
    alter table invoice_row drop column department;
    alter table invoice_row drop column project;
    alter table invoice_row drop column subaccount;
