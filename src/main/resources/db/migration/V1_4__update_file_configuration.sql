UPDATE file_configuration
SET file_name_pattern = 'KRISYCASE_{yyMMddHHmm}.txt'
WHERE type = 'EXTERNAL';

UPDATE file_configuration
SET file_name_pattern = 'IPKISYCASE_{yyMMddHHmm}.txt'
WHERE type = 'INTERNAL';