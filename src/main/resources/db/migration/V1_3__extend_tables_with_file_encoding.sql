alter table file_configuration
	add column encoding varchar(255) not null default 'ISO-8859-1';
    
alter table file_configuration
	alter column encoding drop default;

alter table invoice_file
	add column encoding varchar(255) not null default 'UTF-8';

alter table invoice_file
	alter column encoding drop default;
