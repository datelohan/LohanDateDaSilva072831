CREATE TABLE artista_album (
  artista_id BIGINT NOT NULL,
  album_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (artista_id, album_id),

  KEY idx_artista_album_artista (artista_id),
  KEY idx_artista_album_album (album_id),

  CONSTRAINT fk_artista_album_artista
    FOREIGN KEY (artista_id) REFERENCES artista(id)
    ON DELETE CASCADE,

  CONSTRAINT fk_artista_album_album
    FOREIGN KEY (album_id) REFERENCES album(id)
    ON DELETE CASCADE
);
