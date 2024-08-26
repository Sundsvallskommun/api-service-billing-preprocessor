alter table billing_record
	add column municipality_id varchar(255) not null default '2281';
alter table invoice_file
	add column municipality_id varchar(255) not null default '2281';