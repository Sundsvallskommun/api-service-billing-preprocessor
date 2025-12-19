-------------------------------------
-- Billling records
-------------------------------------
INSERT INTO billing_record (id, category, approved, approved_by, created, modified, status, `type`, municipality_id, transfer_date)
VALUES	('71258e7d-5285-46ce-b9b2-877f8cad8edd', 'ISYCASE', NULL, NULL, '2022-06-20 11:17:36.795', NULL, 'NEW', 'INTERNAL', '2281', NULL),
		('1310ee8b-ecf9-4fe1-ab9d-f19153b19d06', 'ISYCASE', '2022-06-30 08:52:25.112', 'JOE01DOE', '2022-06-25 16:43:12.553', '2022-06-30 08:52:25.112', 'APPROVED', 'INTERNAL', '2281', '2022-07-15'),
		('83e4d599-5b4d-431c-8ebc-81192e9401ee', 'ISYCASE', NULL, NULL, '2022-06-25 16:43:12.553', NULL, 'NEW', 'EXTERNAL', '2281', NULL),
		('200ee4a6-f7bc-4d82-80cb-ecd2d610475c', 'ISYCASE', '2024-03-20 11:37:15.997', 'JOE01DOE', '2024-03-20 11:37:15.997', '2024-03-20 11:37:15.997', 'APPROVED', 'EXTERNAL', '2281', '2024-04-15'),
		('b4ee0334-33c3-4eff-984e-3dd5252760a9', 'ISYCASE', '2024-03-20 11:37:15.997', 'JOE01DOE', '2024-03-20 11:37:15.997', '2024-03-20 11:37:15.997', 'APPROVED', 'EXTERNAL', '2282', '2024-04-15'),
		('1abc9859-9141-43b0-91d9-43cd7d889690', 'UNKNOWN', '2024-03-20 11:37:15.997', 'JOE01DOE', '2024-03-20 11:37:15.997', '2024-03-20 11:37:15.997', 'APPROVED', 'EXTERNAL', '2281', '2024-04-15');

-------------------------------------
-- Invoices
-------------------------------------
INSERT INTO invoice (id, customer_id, customer_reference, description, `date`, due_date, our_reference, total_amount)
VALUES	('71258e7d-5285-46ce-b9b2-877f8cad8edd', '15', 'JAN00EDO', 'Passerkort utan foto för Rocky Balboa (ROC01BAL)', NULL, NULL, 'MIC00GOL',  150),
		('1310ee8b-ecf9-4fe1-ab9d-f19153b19d06', '15', 'JAN00EDO', 'Passerkort med foto för Ivan Drago (IVA02DRA)', NULL, NULL, 'MAN22VEG',  200),
		('83e4d599-5b4d-431c-8ebc-81192e9401ee', '02', 'YUI10KAR', 'Faktura för två Boxercise-pass', '2022-01-01', '2022-08-30', NULL, 30000),
		('200ee4a6-f7bc-4d82-80cb-ecd2d610475c', '02', 'YUI10KAR', 'Faktura för skadebehandling', '2024-03-18', '2024-05-31', 'MIC00GOL',  200000),
		('b4ee0334-33c3-4eff-984e-3dd5252760a9', '02', 'YUI10KAR', 'Faktura för skadebehandling', '2024-03-18', '2024-05-31', 'MIC00GOL',  200000),
		('1abc9859-9141-43b0-91d9-43cd7d889690', '02', 'YUI10KAR', 'En faktura som ej kan behandlas', '2024-03-18', '2024-05-31', 'MIC00GOL',  200000);

-------------------------------------
-- Invoice rows
-------------------------------------
INSERT INTO invoice_row (id, cost_per_unit, quantity, total_amount, vat_code, invoice_id)
VALUES	(100, 150, 1, 150, NULL, '71258e7d-5285-46ce-b9b2-877f8cad8edd'),
		(200, 200, 1, 200, NULL, '1310ee8b-ecf9-4fe1-ab9d-f19153b19d06'),
		(300, 15000, 2, 30000, '25', '83e4d599-5b4d-431c-8ebc-81192e9401ee'),
		(400, 100000, 2, 200000, '25', '200ee4a6-f7bc-4d82-80cb-ecd2d610475c'),
		(401, 100000, 2, 200000, '25', 'b4ee0334-33c3-4eff-984e-3dd5252760a9'),
		(500, 100000, 2, 200000, '25', '1abc9859-9141-43b0-91d9-43cd7d889690');

-- -----------------------------------
-- Account information
-- -----------------------------------
INSERT INTO account_information (invoice_row_id, accural_key, activity, cost_center, counter_part, department, article, project, subaccount, amount)
VALUES	(100, NULL, '5247000', '1620000', 'MIC00GOL', '910300', NULL, NULL, '936100', 150),
		(200, NULL, '5247000', '1620000', 'MAN22VEG', '910300', NULL, NULL, '936100', 200),
		(300, NULL, '3022910', '1455000', 'YUI10KAR', '899700', 'Lokalhyra', NULL, '485223', 22000),
		(300, NULL, '3022920', '1456000', 'YUI10KAR', '899710', 'Instruktör', NULL, '485223', 8000),
		(400, NULL, '3022910', '1455000', 'YUI10KAR', '899700', 'Sprutor', NULL, '485223', 190000),
		(400, NULL, '3022910', '1455010', 'YUI10KAR', '899710', 'Injektion', NULL, '485223', 10000),
		(401, NULL, '3022910', '1455000', 'YUI10KAR', '899700', 'Smärtstillande medicin', NULL, '485223', 200000),
		(500, NULL, '3022910', '1455000', 'YUI10KAR', '899700', 'Ej behandlingsbar rad', NULL, '485223', 200000);

-------------------------------------
-- Descriptions
-------------------------------------
INSERT INTO description (text, `type`, invoice_row_id)
VALUES	('Ordernummer: azi-330c-3fne-33', 'STANDARD', 100),
		('Beställare: MIC00GOL 22940338', 'DETAILED', 100),
		('Användare: Rocky Balboa ROC01BAL', 'DETAILED', 100),
		('Passerkort utan foto', 'DETAILED', 100),
		('Ordernummer: ewf-3fee-boe3-74', 'STANDARD', 200),
		('Beställare: MAN22VEG 4480296', 'DETAILED', 200),
		('Användare: Ivan Drago IVA02DRA', 'DETAILED', 200),
		('Passerkort med foto', 'DETAILED', 200),
		('Styrka: 100mg', 'STANDARD', 400);

-------------------------------------
-- Recipients
-------------------------------------
INSERT INTO recipient (id, care_of, city, street, postal_code, first_name, last_name, organization_name, party_id, user_id)
VALUES	('83e4d599-5b4d-431c-8ebc-81192e9401ee', NULL, 'Louiseville', 'Beachland avenue 3308', 'KY 40211', 'Yuri', 'Karpov', NULL, '970cd619-51b0-40b9-9132-579e2f937c07', NULL),
		('200ee4a6-f7bc-4d82-80cb-ecd2d610475c', NULL, 'Louiseville', 'Beachland avenue 3308', 'KY 40211', 'Yuri', 'Karpov', NULL, '970cd619-51b0-40b9-9132-579e2f937c07', NULL),
		('b4ee0334-33c3-4eff-984e-3dd5252760a9', NULL, 'Louiseville', 'Beachland avenue 3308', 'KY 40211', 'Yuri', 'Karpov', NULL, '970cd619-51b0-40b9-9132-579e2f937c07', NULL),
		('1abc9859-9141-43b0-91d9-43cd7d889690', NULL, 'Louiseville', 'Beachland avenue 3308', 'KY 40211', 'Yuri', 'Karpov', NULL, '970cd619-51b0-40b9-9132-579e2f937c07', NULL);

-------------------------------------
-- Extra parameters
-------------------------------------
INSERT INTO extra_parameter (billing_record_id, `key`, `value`)
VALUES
    ('71258e7d-5285-46ce-b9b2-877f8cad8edd', 'aKey', 'aValue'),
    ('1310ee8b-ecf9-4fe1-ab9d-f19153b19d06', 'aKey', 'anotherValue');
