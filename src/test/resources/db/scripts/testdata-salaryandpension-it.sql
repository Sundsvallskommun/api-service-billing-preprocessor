-- -----------------------------------
-- Billling records
-- -----------------------------------
INSERT INTO billing_record (id, category, approved, approved_by, created, modified, status, `type`, municipality_id, transfer_date)
VALUES	  ('e4ead2ae-daf7-4993-8f64-f9d24417b188', 'SALARY_AND_PENSION', '2024-02-10 13:37:00.000001', 'L&P', '2024-02-09 13:37:00.000001', '2024-02-10 13:37:00.000001', 'APPROVED', 'EXTERNAL', '2281', '2024-02-15'),
          ('61b6f794-10c1-4e3b-8a3d-6d789582e5f8', 'SALARY_AND_PENSION', '2024-02-10 13:37:00.000001', 'L&P', '2024-02-09 13:37:00.000001', '2024-02-10 13:37:00.000001', 'APPROVED', 'EXTERNAL', '2281', '2024-02-15'),
          ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3c', 'SALARY_AND_PENSION', '2024-02-10 13:37:00.000001', 'L&P', '2024-02-09 13:37:00.000001', '2024-02-10 13:37:00.000001', 'APPROVED', 'INTERNAL', '2281', '2024-02-15'),
          ('8fe12401-ea81-cdfe-a239-76febb3101dd', 'SALARY_AND_PENSION', '2024-02-10 13:37:00.000001', 'L&P', '2024-02-09 13:37:00.000001', '2024-02-10 13:37:00.000001', 'APPROVED', 'INTERNAL', '2281', '2024-02-15');

-- -----------------------------------
-- Invoices
-- -----------------------------------
INSERT INTO invoice (id, customer_id, customer_reference, description, `date`, due_date, our_reference, total_amount)
VALUES	  ('e4ead2ae-daf7-4993-8f64-f9d24417b188', 'Seaview Middle School', 'PRI22LUG', 'Extra löneutbetalning - Systemet', NULL, NULL, 'Hanna Montana',  376.38),
          ('61b6f794-10c1-4e3b-8a3d-6d789582e5f8', 'Acme Corporation', 'BOB99', 'Utvecklingskostnad 2%', NULL, NULL, 'Lola Macarola',  490.00),
          ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3c', '10', 'HAN99MON', 'Extra löneutbetalning - Direktinsättning', NULL, NULL, 'Lola Luftnagle', 1530.0),
          ('8fe12401-ea81-cdfe-a239-76febb3101dd', '20', 'ZXZ99YXY', 'Extra löneutbetalning - Direktinsättning', NULL, NULL, 'John Doe', 306.0);

-- -----------------------------------
-- Invoice rows
-- -----------------------------------
INSERT INTO invoice_row (id, cost_per_unit, quantity, total_amount, vat_code, invoice_id)
VALUES	  (100, 306.0,  1.23, 376.38, '25', 'e4ead2ae-daf7-4993-8f64-f9d24417b188'),
          (101, 245.0,  2.00, 490.00, '25', '61b6f794-10c1-4e3b-8a3d-6d789582e5f8'),
          (200, 1500.0, 1.00, 1500.0, NULL, '9d7c72f4-7b18-4a4a-b2ee-4a7007cada3c'),
          (201, 30.0, 1.00, 30.0, NULL, '9d7c72f4-7b18-4a4a-b2ee-4a7007cada3c'),
          (300, 300.0, 1.00, 300.0, NULL, '8fe12401-ea81-cdfe-a239-76febb3101dd'),
          (301, 6.0, 1.00, 6.0, NULL, '8fe12401-ea81-cdfe-a239-76febb3101dd');

-- -----------------------------------
-- Account information
-- -----------------------------------
INSERT INTO account_information (invoice_row_id, accural_key, activity, cost_center, counter_part, department, article, project, subaccount, amount)
VALUES	  (100, NULL, '5757', '15800234', '510', '920360', NULL, NULL, '363000', 369.0),
          (101, NULL, '5757', '15800234', '510', '920360', NULL, '11041', '363000', 7.38),
          (200, NULL, '5756', '15810100', '110', '920360', NULL, NULL, '936300', 1500.0),
          (201, NULL, '5756', '15810100', '110', '920360', NULL, NULL, '936300', 30.0),
          (300, NULL, '5756', '15810100', '110', '920360', NULL, NULL, '936300', 300.0),
          (301, NULL, '5756', '15810100', '110', '920360', NULL, NULL, '936300', 6.0);

-- -----------------------------------
-- Descriptions
-- -----------------------------------
INSERT INTO description (text, `type`, invoice_row_id)
VALUES	  ('Ärendenummer: LoP-25020125', 'STANDARD', 100),
          ('Utvecklingskostnad 2%', 'STANDARD', 101),
          ('Ärendenummer: LoP-25020123', 'STANDARD', 200),
          ('Utvecklingskostnad 2%', 'STANDARD', 201),
          ('Ärendenummer: LoP-21235020', 'STANDARD', 300),
          ('Utvecklingskostnad 2%', 'STANDARD', 301);

-- -----------------------------------
-- Recipients
-- -----------------------------------
INSERT INTO recipient (id, care_of, city, street, postal_code, first_name, last_name, organization_name, party_id, user_id)
VALUES	  ('e4ead2ae-daf7-4993-8f64-f9d24417b188', NULL, 'HANZALAND', 'MIDDLE SCHOOL STREET 123', '90102', NULL, NULL, 'Seaview Middle School', 'ac653c32-b26c-47e8-8c3d-4d18c1b5111c', NULL),
          ('61b6f794-10c1-4e3b-8a3d-6d789582e5f8', NULL, 'FOOTOWN', 'FOO BAR BAZ 860', '42012', NULL, NULL, 'Acme Corporation', 'ac653c32-b26c-47e8-8c3d-4d18c1b5111c', NULL),
          ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3c', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
          ('8fe12401-ea81-cdfe-a239-76febb3101dd', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-------------------------------------
-- Extra parameters
-------------------------------------
INSERT INTO extra_parameter (billing_record_id, `key`, `value`)
VALUES
    ('e4ead2ae-daf7-4993-8f64-f9d24417b188', 'errandId', 'fb19ecf5-a7c2-465e-22ac-c7ea9b4f1c15'),
    ('e4ead2ae-daf7-4993-8f64-f9d24417b188', 'errandNumber', 'LoP-25020125'),
    ('e4ead2ae-daf7-4993-8f64-f9d24417b188', 'referenceName', 'Principal Luger'),
    ('61b6f794-10c1-4e3b-8a3d-6d789582e5f8', 'errandId', 'fb19ecf5-a7c2-465e-22ac-c7ea9b4f1c15'),
    ('61b6f794-10c1-4e3b-8a3d-6d789582e5f8', 'errandNumber', 'LoP-25020125'),
    ('61b6f794-10c1-4e3b-8a3d-6d789582e5f8', 'referenceName', 'HR Rep. Bob'),
    ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3c', 'errandId', 'cd25a29e-3e58-4fbd-979a-ece33b528d12'),
    ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3c', 'errandNumber', 'LoP-25020123'),
    ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3c', 'referenceName', 'Hanna Montana'),
    ('8fe12401-ea81-cdfe-a239-76febb3101dd', 'errandId', 'cd25a29e-3e58-4fbd-979a-ece33b528d12'),
    ('8fe12401-ea81-cdfe-a239-76febb3101dd', 'errandNumber', 'LoP-21235020'),
    ('8fe12401-ea81-cdfe-a239-76febb3101dd', 'referenceName', 'Lola Macarola');
