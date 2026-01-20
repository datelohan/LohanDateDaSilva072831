CREATE TABLE albums (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        title VARCHAR(200) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (id),
                        UNIQUE KEY uk_albums_title (title)
);
