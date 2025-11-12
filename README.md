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
EMAIL_USERNAME=seu-email@gmail.com
EMAIL_PASSWORD=abcd efgh ijkl mnop
```

Execute o Docker Compose:

```bash
docker-compose up -d
```

#### Opção B: Passando variáveis diretamente no comando

```bash
docker-compose run \
  -e GOOGLE_BOOKS_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxx \
  -e EMAIL_USERNAME=seu-email@gmail.com \
  -e EMAIL_PASSWORD="abcd efgh ijkl mnop" \
  -d backend
```

#### Opção C: Usando `export` no Linux/Mac

```bash
export GOOGLE_BOOKS_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
export EMAIL_USERNAME=seu-email@gmail.com
export EMAIL_PASSWORD="abcd efgh ijkl mnop"

docker-compose up -d
```

#### Opção D: Usando `set` no Windows (CMD)

```cmd
set GOOGLE_BOOKS_API_KEY=AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
set EMAIL_USERNAME=seu-email@gmail.com
set EMAIL_PASSWORD=abcd efgh ijkl mnop

docker-compose up -d
```

#### Opção E: Usando `$env` no Windows (PowerShell)

```powershell
$env:GOOGLE_BOOKS_API_KEY="AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
$env:EMAIL_USERNAME="seu-email@gmail.com"
$env:EMAIL_PASSWORD="abcd efgh ijkl mnop"

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
| **RabbitMQ Management** | http://localhost:15672 | guest/guest |
| **PostgreSQL** | localhost:5432 | postgres/postgres |

---

## 📖 Testando o Sistema

### 1. Via Swagger UI

Acesse http://localhost:8080/swagger-ui.html e teste os endpoints.

### 2. Via cURL

#### Criar Usuário
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "telefone": "11999999999"
  }'
```

#### Criar Livro
```bash
curl -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Clean Code",
    "autor": "Robert Martin",
    "isbn": "9780132350884",
    "dataPublicacao": "2008-08-01",
    "categoria": "Tecnologia"
  }'
```

#### Buscar Livro por ISBN (com importação automática)
```bash
curl http://localhost:8080/api/livros/isbn/9780132350884
```

Se o livro não existir, o sistema automaticamente:
1. Busca no Google Books
2. Importa para o catálogo
3. Retorna erro informativo

#### Realizar Empréstimo
```bash
curl -X POST http://localhost:8080/api/emprestimos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "livroId": 1
  }'
```

#### Realizar Devolução
```bash
curl -X PUT http://localhost:8080/api/emprestimos/1/devolucao
```

---

## 🕒 Scheduler Automático

O sistema possui um **scheduler** que executa **diariamente às 8h da manhã**:

- ✅ Verifica empréstimos com mais de 14 dias
- ✅ Marca como `ATRASADO`
- ✅ Envia email automático para o usuário
- ✅ Registra na fila RabbitMQ

### Testar o Scheduler Manualmente

Para não esperar até às 8h, você pode ajustar o cron no arquivo:

**`src/main/java/com/biblioteca/scheduler/EmprestimoScheduler.java`**

```java
// Mudar de:
@Scheduled(cron = "0 0 8 * * ?") // 8h da manhã

// Para (exemplo - a cada minuto):
@Scheduled(cron = "0 * * * * ?") // A cada minuto

// Ou (a cada 30 segundos para testes):
@Scheduled(fixedRate = 30000)
```

Recompile e reinicie o container:
```bash
docker-compose down
mvn clean package
docker-compose up -d --build
```

---

## 🐰 Monitorar RabbitMQ

1. Acesse http://localhost:15672
2. Login: `guest` / `guest`
3. Vá em **Queues** para ver as filas:
   - `emprestimo.atrasado.queue`
   - `livro.buscar.google.queue`
   - `livro.importado.queue`

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

---

## 🔧 Troubleshooting

### Erro: "Address already in use"

Alguma porta já está sendo usada. Verifique:

```bash
# Linux/Mac
lsof -i :8080
lsof -i :5432
lsof -i :5672

# Windows
netstat -ano | findstr :8080
```

Pare o processo ou altere a porta no `docker-compose.yml`.

### Erro: "Google Books API returned 403"

Sua API Key pode estar inválida ou sem permissão. Verifique:
1. API Key está correta
2. Books API está habilitada no projeto
3. Não excedeu a cota gratuita (1000 requisições/dia)

### Erro: "Failed to send email"

Verifique:
1. Email e senha de app estão corretos
2. Verificação em duas etapas está ativada
3. Senha é de APP, não a senha normal do Gmail

### Container não sobe

```bash
# Ver logs detalhados
docker-compose logs backend

# Recriar containers
docker-compose down
docker-compose up -d --build
```

---

## 📊 Estrutura do Banco de Dados

```sql
-- Principais tabelas
usuarios (id, nome, email, telefone, data_cadastro, ativo)
livros (id, titulo, autor, isbn, data_publicacao, categoria, disponivel, google_books_id)
emprestimos (id, usuario_id, livro_id, data_emprestimo, data_devolucao, status)
```

**Relacionamentos:**
- Usuario (1) → (N) Emprestimo
- Livro (1) → (N) Emprestimo

---

## 🔄 Fluxo de Integração

### 1. Busca de Livro Inexistente
```
Usuario busca ISBN não cadastrado
    ↓
API retorna 404 e envia para fila
    ↓
Worker consome fila "livro.buscar.google"
    ↓
Busca no Google Books API
    ↓
Importa livro para o banco
    ↓
Publica evento "livro.importado"
    ↓
Notificação (opcional)
```

### 2. Verificação de Atraso
```
Scheduler roda às 8h diariamente
    ↓
Busca empréstimos ativos > 14 dias
    ↓
Marca status como ATRASADO
    ↓
Publica na fila "emprestimo.atrasado"
    ↓
Worker consome fila
    ↓
Envia email para usuário
```

---

## 📝 Variáveis de Ambiente

| Variável | Descrição | Obrigatório | Exemplo |
|----------|-----------|-------------|---------|
| `GOOGLE_BOOKS_API_KEY` | Chave da API do Google Books | ✅ Sim | `AIzaSyD...` |
| `EMAIL_USERNAME` | Email Gmail para envio | ✅ Sim | `sistema@gmail.com` |
| `EMAIL_PASSWORD` | Senha de App do Gmail | ✅ Sim | `abcd efgh ijkl` |
| `SPRING_PROFILES_ACTIVE` | Profile Spring | ❌ Não | `dev` (padrão) |

---

## 🧪 Testes

### Executar testes
```bash
# Localmente
mvn test

# No Docker
docker-compose run backend mvn test
```

---

## 📄 Licença

Este projeto está sob a licença MIT.

---

## 👥 Contribuindo

1. Fork o projeto
2. Crie uma branch: `git checkout -b feature/nova-funcionalidade`
3. Commit suas mudanças: `git commit -m 'Adiciona nova funcionalidade'`
4. Push para a branch: `git push origin feature/nova-funcionalidade`
5. Abra um Pull Request

---

## 📞 Suporte

Para dúvidas ou problemas:
- Abra uma [Issue](https://github.com/seu-usuario/biblioteca-api/issues)
- Email: contato@biblioteca.com

---

## ⭐ Features

- ✅ CRUD completo de Usuários, Livros e Empréstimos
- ✅ Integração automática com Google Books API
- ✅ Importação assíncrona de livros via RabbitMQ
- ✅ Verificação automática de empréstimos atrasados
- ✅ Envio de emails com templates HTML profissionais
- ✅ Sistema de recomendações baseado em histórico
- ✅ Virtual Threads do Java 21 para alta performance
- ✅ Migrations com Flyway
- ✅ Documentação Swagger/OpenAPI
- ✅ Health checks e métricas com Actuator
- ✅ Docker Compose para fácil setup

---

**Desenvolvido com ❤️ usando Spring Boot e Java 21**
