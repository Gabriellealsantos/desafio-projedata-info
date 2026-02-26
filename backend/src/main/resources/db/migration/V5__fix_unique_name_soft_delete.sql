ALTER TABLE product DROP CONSTRAINT uk_product_name;

CREATE UNIQUE INDEX uk_product_name_active
ON product (CASE WHEN active = 1 THEN name ELSE NULL END);