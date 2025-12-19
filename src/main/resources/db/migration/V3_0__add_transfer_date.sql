-- Add transfer_date column to billing_record table
alter table billing_record
    add column transfer_date date null;

-- Create index for transfer_date to optimize queries
create index idx_billing_record_status_municipalityId_transfer_date
    on billing_record (status, municipality_id, transfer_date);
