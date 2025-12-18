-- Remove existing configuration if it exists
delete
from file_configuration
where category_tag = 'MEX_INVOICE';

-- Add new file configurations for MEX_INVOICE with updated file names and import paths
insert into file_configuration (`type`, category_tag, file_name_pattern, creator_name, encoding)
values ('EXTERNAL', 'MEX_INVOICE', 'KRPMEX_{yyMMdd}.txt', 'ExternalMexInvoiceCreator',
        'ISO-8859-1'),
       ('INTERNAL', 'MEX_INVOICE', 'IPKMEX_{yyMMdd}.txt', 'InternalMexInvoiceCreator',
        'ISO-8859-1');
