-- Add column with amount information to table
alter table account_information
	add column amount float(23);
	
-- Calculate amount for existing rows
update account_information ai,
       invoice_row ir
set ai.amount = ir.cost_per_unit * ir.quantity
where ai.invoice_row_id  = ir.id
  and ai.amount is null;
