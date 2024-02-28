-------------------------------------
-- Billling records
-------------------------------------
INSERT INTO billing_record (id, category, approved, approved_by, created, modified, status, `type`)
VALUES	('71258e7d-5285-46ce-b9b2-877f8cad8edd', 'ACCESS_CARD', NULL, NULL, '2022-06-20 11:17:36.795', NULL, 'NEW', 'INTERNAL'),
		('1310ee8b-ecf9-4fe1-ab9d-f19153b19d06', 'ACCESS_CARD', '2022-06-30 08:52:25.112', 'JOE01DOE', '2022-06-25 16:43:12.553', '2022-06-30 08:52:25.112', 'APPROVED', 'INTERNAL'),
		('83e4d599-5b4d-431c-8ebc-81192e9401ee', 'SALARY_AND_PENSION', NULL, NULL, '2022-06-25 16:43:12.553', NULL, 'NEW', 'EXTERNAL');

-------------------------------------
-- Invoices
-------------------------------------
INSERT INTO invoice (id, customer_id, customer_reference, description, `date`, due_date, our_reference, reference_id, total_amount)
VALUES	('71258e7d-5285-46ce-b9b2-877f8cad8edd', '02', 'ROC01BAL', 'Passerkort utan foto för Rocky Balboa (ROC01BAL)', NULL, '2022-07-31', NULL, NULL, 150),
		('1310ee8b-ecf9-4fe1-ab9d-f19153b19d06', '02', 'IVA02DRA', 'Passerkort med foto för Ivan Drago (IVA02DRA)', NULL, '2022-07-31', NULL, NULL, 200),
		('83e4d599-5b4d-431c-8ebc-81192e9401ee', '16', 'YUI10KAR', 'Faktura för två Boxercise-pass', '2022-02-02', '2022-08-30', 'JOE01DOE', '1123-3241', 30000);

-------------------------------------
-- Invoice rows
-------------------------------------
INSERT INTO invoice_row (id, accural_key, activity, cost_center, counter_part, department, article, project, subaccount, cost_per_unit, quantity, total_amount, vat_code, invoice_id)
VALUES	(100, NULL, '5247000', '1620000', NULL, '910300', NULL, NULL, '936100', 150, 1, 150, '00', '71258e7d-5285-46ce-b9b2-877f8cad8edd'),
		(200, NULL, '5247000', '1620000', NULL, '910300', NULL, NULL, '936100', 200, 1, 200, '00', '1310ee8b-ecf9-4fe1-ab9d-f19153b19d06'),
		(300, NULL, '3022910', '1455000', 'YUI10KAR', '899700', 'Friskvårdssatsning', NULL, NULL, 15000, 2, 30000, '25', '83e4d599-5b4d-431c-8ebc-81192e9401ee');

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
		('Passerkort med foto', 'STANDARD', 200);

-------------------------------------
-- Recipients
-------------------------------------
INSERT INTO recipient (id, care_of, city, street, postal_code, first_name, last_name, organization_name, party_id, user_id)
VALUES	('83e4d599-5b4d-431c-8ebc-81192e9401ee', NULL, 'Louiseville', 'Beachland avenue 3308', 'KY 40211', 'Yuri', 'Karpov', NULL, '970cd619-51b0-40b9-9132-579e2f937c07', 'YUI10KAR');

-------------------------------------
-- Invoice file
-------------------------------------
INSERT INTO invoice_file (created, content, sent, name, `type`, status) 
VALUES 
	('2024-02-26 10:15:00.000000', 'File content', NULL, 'INVOICE_FILE_1.txt', 'INTERNAL', 'GENERATED'),
	('2024-02-25 09:30:00.000000', 'File content', '2024-02-25 09:35:00.000000', 'INVOICE_FILE_2.txt', 'INTERNAL', 'SEND_SUCCESSFUL'),
	('2024-02-24 14:45:00.000000', 'File content', NULL, 'INVOICE_FILE_3.txt', 'INTERNAL', 'GENERATED'),
	('2024-02-23 16:20:00.000000', 'File content', NULL, 'INVOICE_FILE_4.txt', 'INTERNAL', 'SEND_SUCCESSFUL'),
	('2024-02-22 11:10:00.000000', 'File content', '2024-02-22 11:15:00.000000', 'INVOICE_FILE_5.txt', 'EXTERNAL', 'SEND_SUCCESSFUL');


INSERT INTO file_configuration(type, category_tag, file_name_pattern)
VALUES
    ('type1', 'category_tag1', 'file_name_pattern1'),
    ('type1', 'category_tag2', 'file_name_pattern2'),
    ('type3', 'category_tag3', 'file_name_pattern3');