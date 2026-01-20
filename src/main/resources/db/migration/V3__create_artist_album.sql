CREATE TABLE artist_album (
                              artist_id BIGINT NOT NULL,
                              album_id BIGINT NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              PRIMARY KEY (artist_id, album_id),

                              KEY idx_artist_album_artist (artist_id),
                              KEY idx_artist_album_album (album_id),

                              CONSTRAINT fk_artist_album_artist
                                  FOREIGN KEY (artist_id) REFERENCES artists(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_artist_album_album
                                  FOREIGN KEY (album_id) REFERENCES albums(id)
                                      ON DELETE CASCADE
);
