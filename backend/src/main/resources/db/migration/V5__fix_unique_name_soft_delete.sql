-- Ajuste na restrição de unicidade para suportar o soft delete.
-- Permite que nomes sejam reutilizados caso o registro anterior esteja inativo (active = 0).

ALTER TABLE product DROP CONSTRAINT uk_product_name;

CREATE UNIQUE INDEX uk_product_name_active
ON product (CASE WHEN active = 1 THEN name ELSE NULL END);