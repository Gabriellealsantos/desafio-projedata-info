-- ============================================
-- V2: Seed data for testing
-- ============================================

-- Raw Materials (Matérias-Primas)
INSERT INTO raw_material (id, name, stock_quantity) VALUES (1, 'Steel Sheet', 500.0000);
INSERT INTO raw_material (id, name, stock_quantity) VALUES (2, 'Aluminum Bar', 300.0000);
INSERT INTO raw_material (id, name, stock_quantity) VALUES (3, 'Copper Wire', 200.0000);
INSERT INTO raw_material (id, name, stock_quantity) VALUES (4, 'Plastic Resin', 800.0000);
INSERT INTO raw_material (id, name, stock_quantity) VALUES (5, 'Rubber Seal', 1000.0000);
INSERT INTO raw_material (id, name, stock_quantity) VALUES (6, 'Glass Panel', 150.0000);
INSERT INTO raw_material (id, name, stock_quantity) VALUES (7, 'Electronic Circuit', 250.0000);
INSERT INTO raw_material (id, name, stock_quantity) VALUES (8, 'Screw Set', 5000.0000);
INSERT INTO raw_material (id, name, stock_quantity) VALUES (9, 'Paint (Liters)', 400.0000);
INSERT INTO raw_material (id, name, stock_quantity) VALUES (10, 'Foam Padding', 600.0000);

-- Products (Produtos)
INSERT INTO product (id, name, value) VALUES (1, 'Industrial Motor', 1500.00);
INSERT INTO product (id, name, value) VALUES (2, 'Control Panel', 2800.00);
INSERT INTO product (id, name, value) VALUES (3, 'Hydraulic Pump', 3500.00);
INSERT INTO product (id, name, value) VALUES (4, 'Conveyor Belt Module', 1200.00);
INSERT INTO product (id, name, value) VALUES (5, 'Electrical Cabinet', 4200.00);

-- Product Raw Materials (Composição dos Produtos)

-- Industrial Motor: Steel + Copper Wire + Rubber Seal + Screw Set + Paint
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (1, 1, 1, 10.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (2, 1, 3, 5.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (3, 1, 5, 4.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (4, 1, 8, 20.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (5, 1, 9, 2.0000);

-- Control Panel: Aluminum Bar + Electronic Circuit + Plastic Resin + Screw Set + Glass Panel
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (6, 2, 2, 8.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (7, 2, 7, 12.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (8, 2, 4, 6.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (9, 2, 8, 30.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (10, 2, 6, 3.0000);

-- Hydraulic Pump: Steel + Aluminum Bar + Rubber Seal + Copper Wire + Screw Set
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (11, 3, 1, 15.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (12, 3, 2, 5.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (13, 3, 5, 8.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (14, 3, 3, 3.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (15, 3, 8, 25.0000);

-- Conveyor Belt Module: Steel + Plastic Resin + Rubber Seal + Foam Padding + Screw Set
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (16, 4, 1, 20.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (17, 4, 4, 15.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (18, 4, 5, 10.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (19, 4, 10, 8.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (20, 4, 8, 40.0000);

-- Electrical Cabinet: Steel + Aluminum Bar + Electronic Circuit + Glass Panel + Copper Wire + Paint + Screw Set
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (21, 5, 1, 25.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (22, 5, 2, 10.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (23, 5, 7, 15.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (24, 5, 6, 5.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (25, 5, 3, 8.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (26, 5, 9, 5.0000);
INSERT INTO product_raw_material (id, product_id, raw_material_id, quantity) VALUES (27, 5, 8, 50.0000);
