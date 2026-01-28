CREATE TABLE album_images (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               album_id BIGINT NOT NULL,
                               object_key VARCHAR(300) NOT NULL,
                               original_name VARCHAR(255) NOT NULL,
                               content_type VARCHAR(120) NOT NULL,
                               size_bytes BIGINT NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (id),
                               KEY idx_album_images_album (album_id),
                               CONSTRAINT fk_album_images_album
                                   FOREIGN KEY (album_id) REFERENCES albums(id)
                                       ON DELETE CASCADE
);
