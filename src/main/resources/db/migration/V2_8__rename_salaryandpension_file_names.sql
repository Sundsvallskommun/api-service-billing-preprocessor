update file_configuration
   set file_name_pattern = 'krlope_{yyMMddHHmm}.txt' 
 where category_tag = 'SALARY_AND_PENSION'
   and `type` = 'EXTERNAL'
   and file_name_pattern = '{yyMMddHHmm}_krlope.txt';

update file_configuration
   set file_name_pattern = 'ipklop_{yyMMddHHmm}.txt' 
 where category_tag = 'SALARY_AND_PENSION'
   and `type` = 'INTERNAL'
   and file_name_pattern = '{yyMMddHHmm}_ipklop.txt';
