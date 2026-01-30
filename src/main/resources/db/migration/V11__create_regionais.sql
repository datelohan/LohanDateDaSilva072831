CREATE TABLE regionais (
    seq_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id INT NOT NULL,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_regionais_id_ativo ON regionais (id, ativo);
