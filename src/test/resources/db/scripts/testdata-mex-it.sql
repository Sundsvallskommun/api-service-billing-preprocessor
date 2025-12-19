-- -----------------------------------
-- Billling records
-- -----------------------------------
INSERT INTO billing_record (id, category, approved, approved_by, created, modified, status, `type`, municipality_id, transfer_date)
VALUES ('e4ead2ae-daf7-4993-8f64-f9d24417b189', 'MEX_INVOICE', '2024-02-10 13:37:00.000001', 'MEX',
        '2024-02-09 13:37:00.000001', '2024-02-10 13:37:00.000001', 'APPROVED', 'EXTERNAL', '2281', '2024-02-15'),
       ('61b6f794-10c1-4e3b-8a3d-6d789582e5f9', 'MEX_INVOICE', '2024-02-10 13:37:00.000001', 'MEX',
        '2024-02-09 13:37:00.000001', '2024-02-10 13:37:00.000001', 'APPROVED', 'EXTERNAL', '2281', '2024-02-15'),
       ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3d', 'MEX_INVOICE', '2024-02-10 13:37:00.000001', 'MEX',
        '2024-02-09 13:37:00.000001', '2024-02-10 13:37:00.000001', 'APPROVED', 'INTERNAL', '2281', '2024-02-15'),
       ('8fe12401-ea81-cdfe-a239-76febb3101de', 'MEX_INVOICE', '2024-02-10 13:37:00.000001', 'MEX',
        '2024-02-09 13:37:00.000001', '2024-02-10 13:37:00.000001', 'APPROVED', 'INTERNAL', '2281', '2024-02-15');

-- -----------------------------------
-- Invoices
-- -----------------------------------
INSERT INTO invoice (id, customer_id, customer_reference, description, `date`, due_date, our_reference, total_amount)
VALUES ('e4ead2ae-daf7-4993-8f64-f9d24417b189', 'Seaview Middle School', 'PRI22LUG', 'Extra MEX utbetalning - Systemet',
        NULL, NULL, 'Hanna Montana', 376.38),
       ('61b6f794-10c1-4e3b-8a3d-6d789582e5f9', 'Acme Corporation', 'BOB99', 'MEX Utvecklingskostnad 2%', NULL, NULL,
        'Lola Macarola', 490.00),
       ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3d', '10', 'HAN99MON', 'Extra MEX utbetalning - Direktinsättning', NULL,
        NULL, 'Lola Luftnagle', 1530.0),
       ('8fe12401-ea81-cdfe-a239-76febb3101de', '20', 'ZXZ99YXY', 'Extra MEX utbetalning - Direktinsättning', NULL,
        NULL, 'John Doe', 306.0);

-- -----------------------------------
-- Invoice rows
-- -----------------------------------
INSERT INTO invoice_row (id, cost_per_unit, quantity, total_amount, vat_code, invoice_id)
VALUES (400, 306.0, 1.23, 376.38, '25', 'e4ead2ae-daf7-4993-8f64-f9d24417b189'),
       (401, 245.0, 2.00, 490.00, '25', '61b6f794-10c1-4e3b-8a3d-6d789582e5f9'),
       (500, 1500.0, 1.00, 1500.0, NULL, '9d7c72f4-7b18-4a4a-b2ee-4a7007cada3d'),
       (501, 30.0, 1.00, 30.0, NULL, '9d7c72f4-7b18-4a4a-b2ee-4a7007cada3d'),
       (600, 300.0, 1.00, 300.0, NULL, '8fe12401-ea81-cdfe-a239-76febb3101de'),
       (601, 6.0, 1.00, 6.0, NULL, '8fe12401-ea81-cdfe-a239-76febb3101de');

-- -----------------------------------
-- Account information
-- -----------------------------------
INSERT INTO account_information (invoice_row_id, accural_key, activity, cost_center, counter_part, department, article,
                                 project, subaccount, amount)
VALUES (400, NULL, '5757', '15800234', '510', '920360', NULL, NULL, '363000', 369.0),
       (401, NULL, '5757', '15800234', '510', '920360', NULL, '11041', '363000', 7.38),
       (500, NULL, '5756', '15810100', '110', '920360', NULL, NULL, '936300', 1500.0),
       (501, NULL, '5756', '15810100', '110', '920360', NULL, NULL, '936300', 30.0),
       (600, NULL, '5756', '15810100', '110', '920360', NULL, NULL, '936300', 300.0),
       (601, NULL, '5756', '15810100', '110', '920360', NULL, NULL, '936300', 6.0);

-- -----------------------------------
-- Descriptions
-- -----------------------------------
INSERT INTO description (text, `type`, invoice_row_id)
VALUES ('MEX Ärendenummer: MEX-25020125', 'STANDARD', 400),
       ('MEX Utvecklingskostnad 2%', 'STANDARD', 401),
       ('MEX Ärendenummer: MEX-25020123', 'STANDARD', 500),
       ('MEX Utvecklingskostnad 2%', 'STANDARD', 501),
       ('MEX Ärendenummer: MEX-21235020', 'STANDARD', 600),
       ('MEX Utvecklingskostnad 2%', 'STANDARD', 601);

-- -----------------------------------
-- Recipients
-- -----------------------------------
INSERT INTO recipient (id, care_of, city, street, postal_code, first_name, last_name, organization_name, party_id,
                       user_id)
VALUES ('e4ead2ae-daf7-4993-8f64-f9d24417b189', NULL, 'MEX CITY', 'MEX STREET 123', '90102', NULL, NULL,
        'Seaview Middle School', 'ac653c32-b26c-47e8-8c3d-4d18c1b5111c', NULL),
       ('61b6f794-10c1-4e3b-8a3d-6d789582e5f9', NULL, 'MEX TOWN', 'MEX BAR BAZ 860', '42012', NULL, NULL,
        'Acme Corporation', 'ac653c32-b26c-47e8-8c3d-4d18c1b5111c', NULL),
       ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3d', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
       ('8fe12401-ea81-cdfe-a239-76febb3101de', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-------------------------------------
-- Extra parameters
-------------------------------------
INSERT INTO extra_parameter (billing_record_id, `key`, `value`)
VALUES ('e4ead2ae-daf7-4993-8f64-f9d24417b189', 'errandId', 'fb19ecf5-a7c2-465e-22ac-c7ea9b4f1c15'),
       ('e4ead2ae-daf7-4993-8f64-f9d24417b189', 'errandNumber', 'MEX-25020125'),
       ('e4ead2ae-daf7-4993-8f64-f9d24417b189', 'referenceName', 'Principal Luger'),
       ('61b6f794-10c1-4e3b-8a3d-6d789582e5f9', 'errandId', 'fb19ecf5-a7c2-465e-22ac-c7ea9b4f1c15'),
       ('61b6f794-10c1-4e3b-8a3d-6d789582e5f9', 'errandNumber', 'MEX-25020125'),
       ('61b6f794-10c1-4e3b-8a3d-6d789582e5f9', 'referenceName', 'HR Rep. Bob'),
       ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3d', 'errandId', 'cd25a29e-3e58-4fbd-979a-ece33b528d12'),
       ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3d', 'errandNumber', 'MEX-25020123'),
       ('9d7c72f4-7b18-4a4a-b2ee-4a7007cada3d', 'referenceName', 'Hanna Montana'),
       ('8fe12401-ea81-cdfe-a239-76febb3101de', 'errandId', 'cd25a29e-3e58-4fbd-979a-ece33b528d12'),
       ('8fe12401-ea81-cdfe-a239-76febb3101de', 'errandNumber', 'MEX-21235020'),
       ('8fe12401-ea81-cdfe-a239-76febb3101de', 'referenceName', 'Lola Macarola');
