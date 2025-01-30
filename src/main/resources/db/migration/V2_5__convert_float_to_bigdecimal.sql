-- Change column type from float to big decimal
alter table account_information
	modify amount decimal(38,2);

alter table invoice
	modify total_amount decimal(38,2);

alter table invoice_row
	modify cost_per_unit decimal(38,2),
	modify quantity decimal(38,2),
	modify total_amount decimal(38,2);
	