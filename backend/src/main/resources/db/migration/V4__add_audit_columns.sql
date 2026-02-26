
-- Adiciona nas matérias-primas
ALTER TABLE raw_material ADD created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE raw_material ADD updated_at TIMESTAMP(6) WITH TIME ZONE;

-- Adiciona nos produtos
ALTER TABLE product ADD created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE product ADD updated_at TIMESTAMP(6) WITH TIME ZONE;

-- Adiciona na tabela associativa
ALTER TABLE product_raw_material ADD created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE product_raw_material ADD updated_at TIMESTAMP(6) WITH TIME ZONE;