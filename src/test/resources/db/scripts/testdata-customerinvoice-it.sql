-- -----------------------------------
-- Billling records
-- -----------------------------------
INSERT INTO billing_record (id, category, approved, approved_by, created, modified, status, `type`, municipality_id)
VALUES	  ('23af2045-c96e-44d6-b0da-d2f8a38b9711', 'CUSTOMER_INVOICE', '2024-11-13 13:41:07.818882', 'E_SERVICE', '2024-11-13 13:41:07.819000', NULL, 'APPROVED', 'EXTERNAL', '2281'),
          ('6b92f728-8698-47aa-b8c7-c67585df373e', 'CUSTOMER_INVOICE', '2024-11-13 13:41:15.757000', 'E_SERVICE', '2024-11-13 13:41:15.757000', NULL, 'APPROVED', 'INTERNAL', '2281'),
          ('6d762c41-6ecb-4306-9594-316240e01a68', 'CUSTOMER_INVOICE', '2024-11-13 13:40:59.473763', 'E_SERVICE', '2024-11-13 13:40:59.479000', NULL, 'APPROVED', 'EXTERNAL', '2281');

-- ---------------------------
-- Invoices
-- -----------------------------------
INSERT INTO invoice (id, customer_id, customer_reference, description, `date`, due_date, our_reference, reference_id, total_amount)
VALUES	  ('23af2045-c96e-44d6-b0da-d2f8a38b9711', '20', '123', 'Kundfaktura 1', NULL, NULL, 'Joakim von Anka', '192021', 2281),
          ('6b92f728-8698-47aa-b8c7-c67585df373e', '20', '25Mil', 'Kundfaktura 2', NULL, NULL, 'Joakim von Anka', '192021', 2281),
          ('6d762c41-6ecb-4306-9594-316240e01a68', '199001012385', 'Kalle Anka', 'Kundfaktura 3', NULL, NULL, 'kalle anka', '185375', 2281);

-- -----------------------------------
-- Invoice rows
-- -----------------------------------
INSERT INTO invoice_row (id, cost_per_unit, quantity, total_amount, vat_code, invoice_id)
VALUES	  (600, '700', '3', '2100', '00', '6d762c41-6ecb-4306-9594-316240e01a68'),
          (650, '1080', '1', '1080', '25', '23af2045-c96e-44d6-b0da-d2f8a38b9711'),
          (700, '5650', '1', '5650', '25', '23af2045-c96e-44d6-b0da-d2f8a38b9711'),
          (750, '300', '14', '4200', NULL, '6b92f728-8698-47aa-b8c7-c67585df373e');

-- -----------------------------------
-- Account information
-- -----------------------------------
INSERT INTO account_information (invoice_row_id, accural_key, activity, cost_center, counter_part, department, article, project, subaccount, amount)
VALUES	  (600, NULL, '4165', '', '43200000', '86000000', '3452000 - KNATTE', NULL, '345000', 700),
          (600, NULL, '4166', '', '43200000', '86000000', '3452000 - FNATTE', NULL, '345000', 700),
          (600, NULL, '4167', '', '43200000', '86000000', '3452000 - TJATTE', NULL, '345000', 700),
          (650, NULL, NULL, '', '85000000', '340200', '41499999', NULL, '344100', 1080),
          (700, NULL, NULL, '', '85000000', '69420', '41499999', NULL, '313200', 5650),
          (750, NULL, '1234', '', '120', '350200', '41499999', NULL, '934100', 4200);

-- -----------------------------------
-- Descriptions
-- -----------------------------------
INSERT INTO description (text, `type`, invoice_row_id)
VALUES	  ('Användare: Kalle anka', 'STANDARD', 600),
          ('Passerkort utan foto', 'STANDARD', 650),
          ('Plåster', 'STANDARD', 700),
          ('Kaffefilter', 'STANDARD', 750);

-- -----------------------------------
-- Recipients
-- -----------------------------------
INSERT INTO recipient (id, care_of, city, street, postal_code, first_name, last_name, organization_name, party_id, user_id)
VALUES	  ('23af2045-c96e-44d6-b0da-d2f8a38b9711', NULL, 'ANKEBORG', 'Ankeborgsvägen 123', '123 45', NULL, NULL, 'Ankeborg AB', '970cd619-51b0-40b9-9132-579e2f937c07', NULL),
          ('6b92f728-8698-47aa-b8c7-c67585df373e', NULL, NULL, NULL, 'KY 40211', NULL, NULL, NULL, NULL, NULL),
          ('6d762c41-6ecb-4306-9594-316240e01a68', NULL, 'ANKEBORG', 'Ankeborg 160', '123 45', 'Kalle', 'Anka', NULL, '970cd619-51b0-40b9-9132-579e2f937c07', NULL);

-------------------------------------
-- Extra parameters
-------------------------------------
INSERT INTO extra_parameter (billing_record_id, `key`, `value`)
VALUES
    ('23af2045-c96e-44d6-b0da-d2f8a38b9711', 'aKey', 'aValue'),
    ('6b92f728-8698-47aa-b8c7-c67585df373e', 'aKey', 'anotherValue');
