
INSERT INTO artists (name, type) VALUES
  ('Serj Tankian', 'CANTOR'),
  ('Mike Shinoda', 'CANTOR'),
  ('Michel Teló', 'CANTOR'),
  ('Guns N'' Roses', 'BANDA'),
  ('Foo Fighters', 'BANDA'),
  ('Michael Jackson', 'CANTOR'),
  ('Roberto Carlos', 'CANTOR'),
  ('Linkin Park', 'BANDA'),
  ('Blink-182', 'BANDA'),
  ('Evanescence', 'BANDA'),
  ('Gusttavo Lima', 'CANTOR'),
  ('Anitta', 'CANTOR');


INSERT INTO albums (title) VALUES
('Harakiri'),
('Post Traumatic'),
('Bem Sertanejo'),
('Appetite for Destruction'),
('Wasting Light'),
('Evolve');


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

INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id
FROM artists a
         JOIN albums al
WHERE a.name = 'Foo Fighters' AND al.title = 'Wasting Light';

INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id
FROM artists a
         JOIN albums al
WHERE a.name = 'Linkin Park' AND al.title = 'Evolve';

INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id
FROM artists a
         JOIN albums al
WHERE a.name = 'Evanescence' AND al.title = 'Evolve';

INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id
FROM artists a
         JOIN albums al
WHERE a.name = 'Anitta' AND al.title = 'Bem Sertanejo';
