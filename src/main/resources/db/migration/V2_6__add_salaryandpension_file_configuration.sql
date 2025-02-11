-- Remove existing configuration if it exists
delete from file_configuration where category_tag = 'SALARY_AND_PENSION';

-- Add new file configurations for SALARY_AND_PENSION
insert into file_configuration (`type`,category_tag,file_name_pattern, creator_name, encoding) values
('EXTERNAL','SALARY_AND_PENSION','{yyMMddHHmm}_krlope.txt', 'ExternalSalaryAndPensionInvoiceCreator', 'ISO-8859-1'),
('INTERNAL','SALARY_AND_PENSION','{yyMMddHHmm}_ipklop.txt', 'InternalSalaryAndPensionInvoiceCreator', 'ISO-8859-1');