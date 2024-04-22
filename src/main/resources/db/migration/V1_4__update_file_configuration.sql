UPDATE file_configuration
SET file_name_pattern = 'KRISYC_{yyMMddHHmm}.txt'
WHERE type = 'EXTERNAL';

UPDATE file_configuration
SET file_name_pattern = 'IPKISYC_{yyMMddHHmm}.txt'
WHERE type = 'INTERNAL';