# 📚 Sistema de Gestão de Biblioteca

Sistema completo de gestão de biblioteca com recomendações de livros, integração com Google Books API, notificações por email e verificação automática de empréstimos atrasados.

## 🚀 Tecnologias

- **Java 21** com Virtual Threads
- **Spring Boot 3.5**
- **PostgreSQL 16**
- **Flyway** para migrations
- **MapStruct** para mapeamento de objetos
- **Docker & Docker Compose**
- **Google Books API**

---

## 📋 Pré-requisitos

- **Git**
- **Docker** e **Docker Compose**
- **Conta Google Cloud** (para API Key do Google Books)

---

## 🔧 Configuração

### 1️⃣ Obter API Key do Google Books

1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um novo projeto (ou use existente)
3. Ative a **Books API**:
   - Menu lateral → **APIs & Services** → **Library**
   - Busque por "Books API"
   - Clique em **Enable**
4. Crie credenciais:
   - Menu lateral → **APIs & Services** → **Credentials**
   - Clique em **Create Credentials** → **API Key**
   - Copie a chave gerada

---

## 📥 Instalação

### 1. Clonar o Repositório

```bash
git clone https://github.com/rhz23/libraryManager-backend.git
cd biblioteca-api
```

### 2. Executar com Docker Compose (Recomendado)

#### Opção A: Usando arquivo `.env` (Recomendado)

Crie um arquivo `.env` na raiz do projeto:

```bash
# .env
GOOGLE_BOOKS_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

Execute o Docker Compose:

```bash
docker-compose up -d
```

Se for utilizar backend na IDE (Intellij)

```
docker-compose up -d postgres
```
* não é necessário rodar subir o rabbitMQ pois não foi implementado o envio de mensagens async

#### Opção B: Passando variáveis diretamente no comando

```bash
docker-compose run \
  -e GOOGLE_BOOKS_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxx \
  -d backend
```

#### Opção D: Usando `set` no Windows (CMD)

```cmd
set GOOGLE_BOOKS_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

docker-compose up -d
```

### 3. Verificar Status dos Containers

```bash
docker-compose ps
```

Você deve ver 3 containers rodando:
- `biblioteca-backend` (porta 8080)
- `biblioteca-postgres` (porta 5432)
- `biblioteca-rabbitmq` (portas 5672, 15672) *não é necessário

### 4. Acompanhar Logs

```bash
# Todos os containers
docker-compose logs -f

# Apenas backend
docker-compose logs -f backend

# Apenas RabbitMQ
docker-compose logs -f rabbitmq
```

---

## 🌐 Acessar o Sistema

Após iniciar os containers, acesse:

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| **API Backend** | http://localhost:8080 | - |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - |
| **PostgreSQL** | localhost:5432 | postgres/postgres |

---

## 🗄️ Acessar Banco de Dados

### Via psql (Docker)
```bash
docker exec -it biblioteca-postgres psql -U postgres -d biblioteca_dev
```

### Via DBeaver/pgAdmin
- **Host**: localhost
- **Port**: 5432
- **Database**: biblioteca_dev
- **Username**: postgres
- **Password**: postgres

---

## 🛑 Parar e Remover Containers

```bash
# Parar containers
docker-compose stop

# Parar e remover containers
docker-compose down

# Parar, remover containers E volumes (APAGA DADOS)
docker-compose down -v
```
