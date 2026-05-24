# Autobots Automanager

REST API para gestão de uma loja de manutenção veicular. Permite cadastrar empresas, usuários, veículos, serviços, mercadorias e vendas, com navegação via HATEOAS e autenticação JWT.

## Stack

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.4 |
| Spring Security | 6.3.x |
| jjwt | 0.11.5 |
| Spring HATEOAS | via Boot 3.3.4 |
| Spring Data JPA | via Boot 3.3.4 |
| MySQL Connector | `com.mysql:mysql-connector-j` |
| Lombok | via Boot 3.3.4 |

## Pré-requisitos

- Java 17 (`/usr/lib/jvm/java-17-openjdk-amd64`)
- MySQL 8.x rodando em `localhost:3306`

## Configuração do banco de dados

```sql
CREATE DATABASE base;
```

Credenciais em `automanager/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/base
spring.datasource.username=root
spring.datasource.password=root
```

> `ddl-auto=create` recria as tabelas a cada inicialização. Troque por `update` para preservar dados entre execuções.

## Como executar

```bash
cd automanager
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. Na inicialização, seed data é inserido automaticamente (1 empresa, 4 usuários, 1 veículo, 2 vendas).

## Autenticação

Todas as rotas (exceto `/autenticacao`) exigem JWT no header.

```bash
# 1. Login
curl -X POST http://localhost:8080/autenticacao \
  -H "Content-Type: application/json" \
  -d '{"nomeUsuario":"admin","senha":"admin123"}'

# Resposta: { "token": "Bearer eyJhbGci..." }

# 2. Usar o token
curl http://localhost:8080/usuario \
  -H "Authorization: Bearer eyJhbGci..."
```

### Usuários seed

| Username | Senha | Role |
|---|---|---|
| `admin` | `admin123` | ADMINISTRADOR |
| `meuidolotemnomeeéflaviocaçarato` | `FlavioMouseHunter2011` | GERENTE |
| `dompedrofornecedor` | `123456` | VENDEDOR |
| `dompedrocliente` | `123456` | CLIENTE |

## Hierarquia de roles

```
ADMINISTRADOR
  └── GERENTE
        └── VENDEDOR
              └── CLIENTE
```

Role superior herda as permissões de todas abaixo.

## Permissões por rota

| Rota | Método | Role mínima |
|---|---|---|
| `/autenticacao` | POST | público |
| `/usuario` | GET | GERENTE |
| `/empresa/{id}/usuario` | POST | GERENTE |
| `/usuario/{id}` | PUT / DELETE | GERENTE |
| `/usuario/{id}/perfil` | POST / DELETE | ADMINISTRADOR |
| `/usuario/{id}/empresa/{empresaId}` | PUT | ADMINISTRADOR |
| `/empresa` | GET | autenticado |
| `/empresa` | POST / PUT / DELETE | ADMINISTRADOR |
| `/veiculo` | GET | autenticado |
| `/usuario/{id}/veiculo` | POST | VENDEDOR |
| `/veiculo/{id}` | PUT / DELETE | VENDEDOR |
| `/venda` | GET (all) | GERENTE |
| `/venda/{id}` / `/usuario/{id}/venda` | GET | VENDEDOR |
| `/venda` | POST | VENDEDOR |
| `/venda/{id}` | PUT / DELETE | GERENTE |
| `/mercadoria` | GET | autenticado |
| `/mercadoria/empresa/{id}` | POST | GERENTE |
| `/mercadoria/{id}` | PUT / DELETE | GERENTE |
| `/servico` | GET | autenticado |
| `/servico/empresa/{id}` | POST | GERENTE |
| `/servico/{id}` | PUT / DELETE | GERENTE |

## Fluxo de segurança

```
Request
  → JwtAuthenticationFilter       # valida token, popula SecurityContext
  → Spring Security filter chain  # .anyRequest().authenticated() → 401 se sem token
  → Controller method
      → @PreAuthorize(...)         # checa role → 403 se sem permissão
          → executa ou nega
```

Todas as respostas incluem `_links` HATEOAS para navegação entre recursos.
# webiii-atv4
