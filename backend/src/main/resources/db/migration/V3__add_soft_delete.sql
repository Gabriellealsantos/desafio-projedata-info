-- Implementação de suporte para exclusão lógica (soft delete) nas tabelas principais.
-- Adiciona a coluna 'active' e índices de performance para otimizar filtros de busca.

-- Adicionando coluna active nas 3 tabelas
ALTER TABLE product ADD active NUMBER(1) DEFAULT 1 NOT NULL;
ALTER TABLE raw_material ADD active NUMBER(1) DEFAULT 1 NOT NULL;
ALTER TABLE product_raw_material ADD active NUMBER(1) DEFAULT 1 NOT NULL;

-- Criando índices para performance, já que filtraremos sempre por 'active'
CREATE INDEX idx_product_active ON product(active);
CREATE INDEX idx_raw_material_active ON raw_material(active);