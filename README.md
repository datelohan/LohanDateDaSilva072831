# Seletivo SEPLAG - Engenheiro da Computacao (Backend Java)

Projeto pratico para o seletivo de Desenvolvedor Back End Java Senior.  
API REST para gerenciamento de **artistas** e **albuns**, com seguranca JWT, upload de capas em MinIO, WebSocket, rate limit e sincronizacao de regionais.

---

## Índice rápido
- [Stack e tecnologias](#stack-e-tecnologias)
- [Pré-requisitos](#pre-requisitos)
- [Clonar o projeto](#clonar-o-projeto)
- [Passo a passo rápido (Docker)](#passo-a-passo-rapido-docker)
- [Execução detalhada](#como-executar-com-docker-compose)
- [Execução local sem Docker](#como-executar-local-sem-docker)
- [Autenticação (login/refresh)](#autenticacao)
- [Endpoints principais](#endpoints-principais)
- [Testes](#como-rodar-testes)
- [Troubleshooting](#troubleshooting)
- [Arquitetura](#arquitetura-adotada)
- [Observacoes finais](#observacoes-finais)

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

## Pre-requisitos
- Git
- Docker Desktop (ou Docker Engine + Docker Compose)
- JDK 21 instalado **somente se quiser rodar sem Docker** (o build via Docker usa a imagem do Maven e não precisa de JDK local)

## Clonar o projeto
```bash
git clone https://github.com/datelohan/LohanDateDaSilva072831.git
cd LohanDateDaSilva072831
```

## Execução do projeto (Docker)
1. `docker compose up --build`
2. Abrir Swagger: http://localhost:8080/swagger-ui/index.html
3. Fazer login em `/api/v1/auth/login` (admin / admin123)
4. Clicar em **Authorize** e colar o `accessToken`
5. Testar rotas de artistas/álbuns/capas.

### Comandos essenciais (Docker Compose)
- Subir tudo: `docker compose up --build`
- Parar: `docker compose down`
- Parar e limpar dados/volumes: `docker compose down -v && docker volume prune -f`

Serviços:
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- MinIO Console: http://localhost:9001 (user: minioadmin / pass: minioadmin)
- MySQL: localhost:3307 (user: seletivo / pass: seletivo123)

### Estrutura do projeto (resumo)
```
src/main/java/lohan/seletivo
  ├─ auth           # login/refresh
  ├─ security       # JWT, filtros, rate limit
  ├─ album          # controllers, services, repos e DTOs de álbuns
  ├─ artist         # controllers, services, repos e DTOs de artistas
  ├─ storage        # MinIO config/inicialização
  ├─ regional       # integração e sync de regionais
  ├─ websocket      # STOMP para novos álbuns
  ├─ health         # liveness/readiness
  └─ shared         # exceções, utilidades
src/main/resources/db/migration # Migrations Flyway
docker-compose.yml             # API + MySQL + MinIO
Dockerfile                     # build da imagem da API
```

Arquitetura adotada: **camadas** com agrupamento por domínio  
- Controller → Service → Repository/Model  
- Domínios separados (album, artist, regional) e módulos transversais (auth, security, storage, websocket)  
- Configurações e concerns comuns isolados (health, shared)

## Passo a passo rápido (Docker)
1) Instale Docker Desktop e deixe rodando.  
2) No diretório do projeto, execute: `docker compose up --build`  
3) Aguarde os containers subirem (API, DB, MinIO).  
4) Acesse o Swagger: `http://localhost:8080/swagger-ui/index.html`  
5) Faça login em `/api/v1/auth/login` (admin / admin123) e clique em **Authorize** com o access token.  
6) Use as rotas de artistas/álbuns normalmente.  
7) Para sair, `docker compose down` (use `-v` se quiser limpar volumes/dados).
   - Se já existirem volumes antigos e der erro de migration (tabelas já existem), use:
     ```
     docker compose down -v
     docker volume prune -f
     docker compose up --build
     ```
     Isso remove volumes/imagens antigos e recria o banco do zero.

---


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
- `refreshToken` (rotaciona a cada refresh; somente o mais recente é válido)

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

Exemplos rápidos (curl)
```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Refresh
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<token>"}'

# Upload de capa (album id=2)
curl -X POST http://localhost:8080/api/v1/albums/2/covers \
  -H "Authorization: Bearer <accessToken>" \
  -F "files=@/caminho/para/imagem.jpeg;type=image/jpeg"
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
- `AlbumServiceTest` (criar album + erro de artista inexistente)
- `ArtistServiceTest` (filtros por nome/tipo chamando o repositório correto)

Rodar testes dentro do container (sem dependências locais):
```bash
docker compose run --rm api ./mvnw test
```

---

## Troubleshooting
- **Porta 8080 em uso**: descubra o processo e libere ou mude a porta no `docker-compose.yml`.
  ```bash
  lsof -i :8080
  ```
- **Erro Flyway (tabelas já existem / schema sujo)**: limpar volumes e subir de novo.
  ```bash
  docker compose down -v
  docker volume prune -f
  docker compose up --build
  ```
- **URL de capa não abre fora do contêiner**: use `http://localhost:9000` no navegador (fora do Docker) e confirme `MINIO_PUBLIC_ENDPOINT` no compose.
- **Assinatura MinIO (SignatureDoesNotMatch)**: tokens presigned expiram em 30 min; gere nova URL com o `GET /api/v1/albums/{id}/covers`.
- **CORS bloqueando origem**: ajuste `security.allowed-origins` em `application.yaml`.

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
- Refresh: cada chamada a `/auth/refresh` gera um novo par de tokens; guarde o refresh mais recente.  
- Nao ha endpoints de DELETE porque o edital nao solicitou.  
- Se o endpoint de regionais estiver fora, a aplicacao **nao falha** no startup.

## Dados do candidato
- Nome: **Lohan Date Da Silva**
- Inscrição: **16433**
- Processo seletivo: **Seplag nº 001/2026**

## Arquitetura adotada
Camadas + separação por domínio:
```
Controller (REST / WebSocket)
   ↓
Service (regras de negócio, integração MinIO, rate limit)
   ↓
Repository (Spring Data JPA)
   ↓
MySQL (dados) | MinIO (capas) | WebSocket broker

Domínios: auth, artist, album, regional
Transversais: security, storage, websocket, health, shared
```
