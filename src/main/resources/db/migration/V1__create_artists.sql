CREATE TABLE artists (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         name VARCHAR(200) NOT NULL,
                         type VARCHAR(30) NOT NULL DEFAULT 'SINGER',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NULL DEFAULT NULL,
                         PRIMARY KEY (id),
                         UNIQUE KEY uk_artists_name (name)
);