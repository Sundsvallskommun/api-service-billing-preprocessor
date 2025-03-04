-------------------------------------
-- Billling records
-------------------------------------
INSERT INTO billing_record (id, category, approved, approved_by, created, modified, status, `type`, municipality_id)
VALUES	('71258e7d-5285-46ce-b9b2-877f8cad8edd', 'ACCESS_CARD', NULL, NULL, '2022-06-20 11:17:36.795', NULL, 'NEW', 'INTERNAL', '2281'),
		('1310ee8b-ecf9-4fe1-ab9d-f19153b19d06', 'ACCESS_CARD', '2022-06-30 08:52:25.112', 'JOE01DOE', '2022-06-25 16:43:12.553', '2022-06-30 08:52:25.112', 'APPROVED', 'INTERNAL', '2281'),
        ('83e4d599-5b4d-431c-8ebc-81192e9401ee', 'SALARY_AND_PENSION', NULL, NULL, '2022-06-25 16:43:12.553', NULL, 'NEW', 'EXTERNAL', '2281'),
		('389b847c-39e9-4321-ae5d-e736e0a5ff51', 'CUSTOMER_INVOICE', '2024-06-30 08:52:25.112', 'JAN02DOE', '2024-06-25 16:43:12.553', '2024-06-30 08:52:25.112', 'NEW', 'EXTERNAL', '2281'),
		('1c38bf5d-ed89-41ee-8090-37733f276ec9', 'CUSTOMER_INVOICE', '2024-07-30 08:52:25.112', 'JAN02DOE', '2024-07-25 16:43:12.553', '2024-07-30 08:52:25.112', 'APPROVED', 'INTERNAL', '2281');
		

-------------------------------------
-- Invoices
-------------------------------------
INSERT INTO invoice (id, customer_id, customer_reference, description, `date`, due_date, our_reference, total_amount)
VALUES	('71258e7d-5285-46ce-b9b2-877f8cad8edd', '02', 'ROC01BAL', 'Passerkort utan foto för Rocky Balboa (ROC01BAL)', NULL, '2022-07-31', NULL, 150),
		('1310ee8b-ecf9-4fe1-ab9d-f19153b19d06', '02', 'IVA02DRA', 'Passerkort med foto för Ivan Drago (IVA02DRA)', NULL, '2022-07-31', NULL, 200),
		('83e4d599-5b4d-431c-8ebc-81192e9401ee', '16', 'YUI10KAR', 'Faktura för två Boxercise-pass', '2022-02-02', '2022-08-30', 'JOE01DOE', 30000),
        ('389b847c-39e9-4321-ae5d-e736e0a5ff51', '12', 'SIL01SIL', 'Faktura för sill', NULL, '2024-07-31', NULL, 200),
        ('1c38bf5d-ed89-41ee-8090-37733f276ec9', '12', 'STR01STR', 'Faktura för strömming', NULL, '2024-07-31', NULL, 400);

-------------------------------------
-- Invoice rows
-------------------------------------
INSERT INTO invoice_row (id, cost_per_unit, quantity, total_amount, vat_code, invoice_id)
VALUES	(100, 150, 1, 150, '00', '71258e7d-5285-46ce-b9b2-877f8cad8edd'),
		(200, 200, 1, 200, '00', '1310ee8b-ecf9-4fe1-ab9d-f19153b19d06'),
		(300, 15000, 2, 30000, '25', '83e4d599-5b4d-431c-8ebc-81192e9401ee'),
		(400, 100, 2, 200, '25', '389b847c-39e9-4321-ae5d-e736e0a5ff51'),
		(500, 200, 2, 400, '25', '1c38bf5d-ed89-41ee-8090-37733f276ec9');

-- -----------------------------------
-- Account information
-- -----------------------------------
INSERT INTO account_information (invoice_row_id, accural_key, activity, cost_center, counter_part, department, article, project, subaccount)
VALUES	(100, NULL, '5247000', '1620000', NULL, '910300', NULL, NULL, '936100'),
		(200, NULL, '5247000', '1620000', NULL, '910300', NULL, NULL, '936100'),
		(300, NULL, '3022910', '1455000', 'YUI10KAR', '899700', 'Friskvårdssatsning', NULL, NULL),
		(400, NULL, '3022920', '1234000', 'SIL01SIL', '899700', 'Fisk', NULL, NULL),
		(500, NULL, '3022920', '1234000', 'YUI10KAR', '899700', 'Mer fisk', NULL, NULL);
		
-------------------------------------
-- Descriptions
-------------------------------------
INSERT INTO description (text, `type`, invoice_row_id)
VALUES	('Ordernummer: azi-330c-3fne-33', 'STANDARD', 100),
		('Beställare: MIC00GOL 22940338', 'STANDARD', 100),
		('Användare: Rocky Balboa ROC01BAL', 'STANDARD', 100),
		('Passerkort utan foto', 'STANDARD', 100),
		('Ordernummer: ewf-3fee-boe3-74', 'STANDARD', 200),
		('Beställare: MAN22VEG 4480296', 'STANDARD', 200),
		('Användare: Ivan Drago IVA02DRA', 'STANDARD', 200),
		('Passerkort med foto', 'STANDARD', 200),
		('Sill m.m.', 'STANDARD', 400),
		('Strömming m.m', 'STANDARD', 500);

-------------------------------------
-- Recipients
-------------------------------------
INSERT INTO recipient (id, care_of, city, street, postal_code, first_name, last_name, organization_name, party_id, user_id)
VALUES	('83e4d599-5b4d-431c-8ebc-81192e9401ee', NULL, 'Louiseville', 'Beachland avenue 3308', 'KY 40211', 'Yuri', 'Karpov', NULL, '970cd619-51b0-40b9-9132-579e2f937c07', 'YUI10KAR');

-------------------------------------
-- Invoice file
-------------------------------------
INSERT INTO invoice_file (created, content, encoding, sent, name, `type`, status, municipality_id)
VALUES 
	('2024-02-26 10:15:00.000000', 'File content', 'ISO-8859-1', NULL, 'INVOICE_FILE_1.txt', 'INTERNAL', 'GENERATED', '2281'),
	('2024-02-25 09:30:00.000000', 'File content', 'ISO-8859-1', '2024-02-25 09:35:00.000000', 'INVOICE_FILE_2.txt', 'INTERNAL', 'SEND_SUCCESSFUL', '2281'),
	('2024-02-24 14:45:00.000000', 'File content', 'ISO-8859-1', NULL, 'INVOICE_FILE_3.txt', 'INTERNAL', 'GENERATED', '2281'),
	('2024-02-23 16:20:00.000000', 'File content', 'ISO-8859-1', NULL, 'INVOICE_FILE_4.txt', 'INTERNAL', 'SEND_FAILED', '2281'),
	('2024-02-22 11:10:00.000000', 'File content', 'ISO-8859-1', '2024-02-22 11:15:00.000000', 'INVOICE_FILE_5.txt', 'EXTERNAL', 'SEND_SUCCESSFUL', '2281');


-------------------------------------
-- Invoice file configuration
-------------------------------------
INSERT INTO file_configuration(type, category_tag, file_name_pattern, encoding, creator_name)
VALUES
    ('type1', 'category_tag1', 'file_name_pattern1', 'encoding_1', 'creator_name_1'),
    ('type1', 'category_tag2', 'file_name_pattern2', 'encoding_2', 'creator_name_2'),
    ('type3', 'category_tag3', 'file_name_pattern3', 'encoding_3', 'creator_name_3');

-------------------------------------
-- Extra parameters
-------------------------------------
INSERT INTO extra_parameter (billing_record_id, `key`, `value`)
VALUES
    ('71258e7d-5285-46ce-b9b2-877f8cad8edd', 'aKey', 'aValue'),
    ('389b847c-39e9-4321-ae5d-e736e0a5ff51', 'aKey', 'anotherValue');
