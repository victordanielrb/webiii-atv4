# Autobots Automanager

REST API para gestão de uma loja de manutenção veicular.

## Requisitos

- Java 17
- MySQL 8.x rodando em `localhost:3306`

## Instalação

**1. Clone o repositório**
```bash
git clone <url-do-repo>
cd atviii-autobots-microservico-spring
```

**2. Crie o banco de dados**
```sql
CREATE DATABASE base;
```

**3. Configure as credenciais** em `automanager/src/main/resources/application.properties`
```properties
spring.datasource.username=root
spring.datasource.password=root
```

**4. Execute**
```bash
cd automanager
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Dados de exemplo são inseridos automaticamente na primeira execução.

## Docker

Sobe a API e o MySQL juntos, sem precisar instalar nada além do Docker.

```bash
docker compose up --build
```

Para parar:
```bash
docker compose down
```

> O banco é criado automaticamente. A API aguarda o MySQL estar pronto antes de iniciar.

## Como usar

**1. Faça login para obter o token:**
```bash
curl -X POST http://localhost:8080/autenticacao \
  -H "Content-Type: application/json" \
  -d '{"nomeUsuario":"admin","senha":"admin123"}'
```

**2. Use o token nas demais requisições:**
```bash
curl http://localhost:8080/usuario \
  -H "Authorization: Bearer <token>"
```

## Usuários disponíveis

| Usuário | Senha | Perfil |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `meuidolotemnomeeéflaviocaçarato` | `FlavioMouseHunter2011` | Gerente |
| `dompedrofornecedor` | `123456` | Vendedor |
| `dompedrocliente` | `123456` | Cliente |

## Rotas principais

| Método | Rota | Descrição |
|---|---|---|
| POST | `/autenticacao` | Login |
| GET | `/usuario` | Listar usuários |
| GET | `/empresa` | Listar empresas |
| GET | `/veiculo` | Listar veículos |
| GET | `/mercadoria` | Listar mercadorias |
| GET | `/servico` | Listar serviços |
| GET | `/venda` | Listar vendas |

## Stack

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.4 |
| Spring Security | 6.3.x |
| MySQL Connector | 8.x |
| JWT (jjwt) | 0.11.5 |
