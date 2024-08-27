alter table billing_record
	add column municipality_id varchar(255) not null default '2281';
create index idx_billing_record_municipality_id
       on billing_record (municipality_id);

alter table invoice_file
	add column municipality_id varchar(255) not null default '2281';
create index idx_invoice_file_municipality_id
       on invoice_file (municipality_id);