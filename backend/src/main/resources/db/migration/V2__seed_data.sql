-- Carga inicial de dados (Seed) para fins de teste e demonstração.
-- Inclui insumos industriais, produtos acabados e suas respectivas fichas técnicas.

-- Raw Materials (Matérias-Primas)
INSERT INTO raw_material (name, stock_quantity) VALUES ('Steel Sheet', 500.0000);
INSERT INTO raw_material (name, stock_quantity) VALUES ('Aluminum Bar', 300.0000);
INSERT INTO raw_material (name, stock_quantity) VALUES ('Copper Wire', 200.0000);
INSERT INTO raw_material (name, stock_quantity) VALUES ('Plastic Resin', 800.0000);
INSERT INTO raw_material (name, stock_quantity) VALUES ('Rubber Seal', 1000.0000);
INSERT INTO raw_material (name, stock_quantity) VALUES ('Glass Panel', 150.0000);
INSERT INTO raw_material (name, stock_quantity) VALUES ('Electronic Circuit', 250.0000);
INSERT INTO raw_material (name, stock_quantity) VALUES ('Screw Set', 5000.0000);
INSERT INTO raw_material (name, stock_quantity) VALUES ('Paint (Liters)', 400.0000);
INSERT INTO raw_material (name, stock_quantity) VALUES ('Foam Padding', 600.0000);

-- Products (Produtos)
INSERT INTO product (name, value) VALUES ('Industrial Motor', 1500.00);
INSERT INTO product (name, value) VALUES ('Control Panel', 2800.00);
INSERT INTO product (name, value) VALUES ('Hydraulic Pump', 3500.00);
INSERT INTO product (name, value) VALUES ('Conveyor Belt Module', 1200.00);
INSERT INTO product (name, value) VALUES ('Electrical Cabinet', 4200.00);

-- Product Raw Materials (Composição dos Produtos)

-- Industrial Motor: Steel + Copper Wire + Rubber Seal + Screw Set + Paint
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (1, 1, 10.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (1, 3, 5.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (1, 5, 4.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (1, 8, 20.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (1, 9, 2.0000);

-- Control Panel: Aluminum Bar + Electronic Circuit + Plastic Resin + Screw Set + Glass Panel
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (2, 2, 8.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (2, 7, 12.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (2, 4, 6.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (2, 8, 30.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (2, 6, 3.0000);

-- Hydraulic Pump: Steel + Aluminum Bar + Rubber Seal + Copper Wire + Screw Set
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (3, 1, 15.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (3, 2, 5.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (3, 5, 8.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (3, 3, 3.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (3, 8, 25.0000);

-- Conveyor Belt Module: Steel + Plastic Resin + Rubber Seal + Foam Padding + Screw Set
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (4, 1, 20.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (4, 4, 15.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (4, 5, 10.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (4, 10, 8.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (4, 8, 40.0000);

-- Electrical Cabinet: Steel + Aluminum Bar + Electronic Circuit + Glass Panel + Copper Wire + Paint + Screw Set
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (5, 1, 25.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (5, 2, 10.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (5, 7, 15.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (5, 6, 5.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (5, 3, 8.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (5, 9, 5.0000);
INSERT INTO product_raw_material (product_id, raw_material_id, quantity) VALUES (5, 8, 50.0000);
