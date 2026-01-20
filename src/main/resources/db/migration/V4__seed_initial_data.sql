-- Artists
INSERT INTO artists (name) VALUES
                               ('Serj Tankian'),
                               ('Mike Shinoda'),
                               ('Michel Teló'),
                               ('Guns N'' Roses');

-- Albums
INSERT INTO albums (title) VALUES
                               ('Harakiri'),
                               ('Post Traumatic'),
                               ('Bem Sertanejo'),
                               ('Appetite for Destruction');

-- Relationships (N:N) using SELECT to avoid relying on IDs
INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id
FROM artists a
         JOIN albums al
WHERE a.name = 'Serj Tankian' AND al.title = 'Harakiri';

INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id
FROM artists a
         JOIN albums al
WHERE a.name = 'Mike Shinoda' AND al.title = 'Post Traumatic';

INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id
FROM artists a
         JOIN albums al
WHERE a.name = 'Michel Teló' AND al.title = 'Bem Sertanejo';

INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id
FROM artists a
         JOIN albums al
WHERE a.name = 'Guns N'' Roses' AND al.title = 'Appetite for Destruction';
