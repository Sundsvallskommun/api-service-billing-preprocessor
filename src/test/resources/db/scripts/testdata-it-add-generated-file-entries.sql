-------------------------------------
-- Invoice files
-------------------------------------
INSERT INTO invoice_file (created, name, status, `type`, content, encoding)
VALUES ('2024-03-21 13:17:00.000', 'EXTERNAL_FILE_20240321.txt', 'GENERATED', 'EXTERNAL', 'External content', 'ISO-8859-1'),
       ('2024-03-22 13:37:00.000', 'INTERNAL_FILE_20240322.txt', 'GENERATED', 'INTERNAL', 'Internal content', 'ISO-8859-1');
