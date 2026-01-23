-- Atualiza registros existentes para a nomenclatura PT-BR
UPDATE artists SET type = 'CANTOR' WHERE UPPER(type) IN ('SINGER', 'CANTOR');
UPDATE artists SET type = 'BANDA' WHERE UPPER(type) IN ('BAND', 'BANDA');

-- Ajusta o default da coluna para novos registros
ALTER TABLE artists MODIFY COLUMN type VARCHAR(30) NOT NULL DEFAULT 'CANTOR';
