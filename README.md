# Seletivo SEPLAG - Engenheiro da Computacao (Backend Java)

Projeto pratico para o seletivo de Desenvolvedor Back End Java Senior.  
API REST para gerenciamento de **artistas** e **albuns**, com seguranca JWT, upload de capas em MinIO, WebSocket, rate limit e sincronizacao de regionais.

---

## Stack e tecnologias
- Java 21 + Spring Boot 4
- Spring Data JPA + MySQL 8.4
- Flyway Migrations
- Spring Security + JWT (access 5 min, refresh com rotacao)
- MinIO (API S3) + presigned URLs (30 min)
- WebSocket (STOMP) para eventos de novos albuns
- OpenAPI/Swagger
- Docker + Docker Compose
- Testes unitarios (JUnit + Mockito)

---

## Como executar (com Docker Compose)

```bash
docker compose up --build
```

Servicos:
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- MinIO Console: http://localhost:9001 (user: minioadmin / pass: minioadmin)
- MySQL: localhost:3307 (user: seletivo / pass: seletivo123)

---

## Como executar local (sem Docker)

1) Suba MySQL e MinIO (pode usar apenas os containers de `docker compose` para isso)  
2) Rode a API:

```bash
./mvnw spring-boot:run
```

---

## Autenticacao

Login:
```
POST /api/v1/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

Resposta:
- `accessToken` (expira em 5 min)
- `refreshToken` (rotaciona a cada refresh)

Refresh:
```
POST /api/v1/auth/refresh
{
  "refreshToken": "..."
}
```

Para consumir rotas protegidas, envie:
```
Authorization: Bearer <accessToken>
```

---

## Endpoints principais 

### Artistas
- `POST /api/v1/artists` – cria artista
- `PUT /api/v1/artists/{id}` – atualiza artista
- `GET /api/v1/artists` – lista com filtros e paginacao  
  Filtros: `q` (nome), `type` (CANTOR/BANDA)  
  Ex: `/api/v1/artists?page=0&size=10&sort=name,asc&type=CANTOR`
- `GET /api/v1/artists/{id}` – busca artista simples
- `GET /api/v1/artists/details` – lista artistas com albuns + capas
- `GET /api/v1/artists/details/{id}` – detalhe com relacionamentos

### Albuns
- `POST /api/v1/albums` – cria album
- `PUT /api/v1/albums/{id}` – atualiza album
- `GET /api/v1/albums` – lista simples com filtros e paginacao  
  Filtros: `titulo`, `tipo` (CANTOR/BANDA)  
  Ex: `/api/v1/albums?page=0&size=10&sort=title,asc&tipo=CANTOR`
- `GET /api/v1/albums/{id}` – busca album simples
- `GET /api/v1/albums/details` – lista detalhada (inclui capas)
- `GET /api/v1/albums/details/{id}` – detalhe (artistas + capas)

### Capas (upload MinIO)
- `POST /api/v1/albums/{id}/covers` (multipart/form-data, campo `files`)
- `GET /api/v1/albums/{id}/covers`

### WebSocket (notificacao de novos albuns)
Endpoint: `ws://localhost:8080/ws`  
Topico: `/topic/albums`

### Regionais (endpoint externo)
- `POST /api/v1/regionais/sync` – sincroniza com o endpoint oficial  
- `GET /api/v1/regionais?ativo=true|false` – lista filtrando por status

### Health checks
- `GET /api/v1/health/liveness`
- `GET /api/v1/health/readiness`

---

## Regras importantes atendidas
- **Seguranca JWT** com access token de 5 min e refresh token com rotacao.
- **CORS** por lista de origens permitidas.
- **Rate limit**: 10 req/min por usuario.
- **Relacionamento N:N** (artista ↔ album).
- **Paginacao e filtros** nas listagens.
- **Upload MinIO** + presigned URL (expira em 30 min).
- **WebSocket** ao cadastrar album.
- **Regionais**: inserir novo, inativar ausente, recriar se alterado.
- **Endpoints versionados** (`/api/v1/...`).
- **Swagger** com exemplos e autenticação bearer.

---

## Como rodar testes

```bash
./mvnw test
```

Testes incluidos:
- `ArtistTypeTest` (conversao de CANTOR/BANDA)
- `AlbumServiceTest` (criar album + erro de artista inexistente)

---

## Migrations (Flyway)
Arquivos em `src/main/resources/db/migration`:
- Tabelas base (artistas, albuns, relacionamentos)
- Seeds iniciais
- Usuarios e refresh tokens
- Capas (album_images)
- Regionais

---

## Observacoes finais
- O Swagger esta aberto sem autenticacao para facilitar testes.  
- O endpoint `/api/v1/health/**` tambem eh publico.  
- Se o endpoint de regionais estiver fora, a aplicacao **nao falha** no startup.

Se algo ficar pendente, explicarei aqui (priorizacao e motivo).

