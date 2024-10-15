-- Add new file configurations for CUSTOMER_INVOICE
insert into file_configuration (`type`,category_tag,file_name_pattern, creator_name, encoding) values
('EXTERNAL','CUSTOMER_INVOICE','{yyyyMMdd}_kpform.txt', 'ExternalCustomerInvoiceCreator', 'ISO-8859-1'),
('INTERNAL','CUSTOMER_INVOICE','{yyyyMMdd}_ipkfor.txt', 'InternalCustomerInvoiceCreator', 'ISO-8859-1');