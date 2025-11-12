# 📚 Sistema de Gestão de Biblioteca

Sistema completo de gestão de biblioteca com recomendações de livros, integração com Google Books API, notificações por email e verificação automática de empréstimos atrasados.

## 🚀 Tecnologias

- **Java 21** com Virtual Threads
- **Spring Boot 3.5**
- **PostgreSQL 16**
- **RabbitMQ 3.13**
- **Flyway** para migrations
- **MapStruct** para mapeamento de objetos
- **Docker & Docker Compose**
- **Thymeleaf** para templates de email
- **Google Books API**

---

## 📋 Pré-requisitos

- **Git**
- **Docker** e **Docker Compose**
- **Conta Google Cloud** (para API Key do Google Books)
- **Conta Gmail** (para envio de emails)

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

### 2️⃣ Configurar Senha de App do Gmail

1. Acesse [Conta Google](https://myaccount.google.com/)
2. Vá em **Segurança** → **Verificação em duas etapas** (ative se necessário)
3. Role até **Senhas de app**
4. Selecione **App**: Email, **Dispositivo**: Outro (personalizado)
5. Digite "Sistema Biblioteca" e clique em **Gerar**
6. Copie a senha gerada (16 caracteres sem espaços)

---

## 📥 Instalação

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/biblioteca-api.git
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

#### Opção B: Passando variáveis diretamente no comando

```bash
docker-compose run \
  -e GOOGLE_BOOKS_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxx \
  -d backend
```

#### Opção D: Usando `set` no Windows (CMD)

```cmd
set GOOGLE_BOOKS_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
set EMAIL_USERNAME=seu-email@gmail.com
set EMAIL_PASSWORD=abcd efgh ijkl mnop

docker-compose up -d
```

### 3. Verificar Status dos Containers

```bash
docker-compose ps
```

Você deve ver 3 containers rodando:
- `biblioteca-backend` (porta 8080)
- `biblioteca-postgres` (porta 5432)
- `biblioteca-rabbitmq` (portas 5672, 15672)

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
| **Actuator Health** | http://localhost:8080/actuator/health | - |
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
