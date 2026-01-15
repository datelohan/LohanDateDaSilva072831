-- Artistas
INSERT INTO artista (nome) VALUES
('Serj Tankian'),
('Mike Shinoda'),
('Michel Teló'),
('Guns N'' Roses');

-- Álbuns
INSERT INTO album (titulo) VALUES
('Harakiri'),
('Post Traumatic'),
('Bem Sertanejo'),
('Appetite for Destruction');

-- Relações (N:N) usando SELECT pra evitar depender de IDs
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id
FROM artista a
JOIN album al
WHERE a.nome = 'Serj Tankian' AND al.titulo = 'Harakiri';

INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id
FROM artista a
JOIN album al
WHERE a.nome = 'Mike Shinoda' AND al.titulo = 'Post Traumatic';

INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id
FROM artista a
JOIN album al
WHERE a.nome = 'Michel Teló' AND al.titulo = 'Bem Sertanejo';

INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id
FROM artista a
JOIN album al
WHERE a.nome = 'Guns N'' Roses' AND al.titulo = 'Appetite for Destruction';
