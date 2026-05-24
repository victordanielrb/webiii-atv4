# Autobots Automanager

REST API para gestão de uma loja de manutenção veicular. Permite cadastrar empresas, usuários, veículos, serviços, mercadorias e vendas, com navegação via HATEOAS.

## Dependências

| Dependência | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 2.6.7 |
| Spring Web | — |
| Spring Data JPA | — |
| Spring HATEOAS | — |
| MySQL Connector | — |
| Lombok | — |

## Pré-requisitos

- Java 17+
- Maven 3.6+
- MySQL rodando em `localhost:3306`

## Configuração do banco de dados

Crie o schema no MySQL:

```sql
CREATE DATABASE base;
```

As credenciais padrão estão em `automanager/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/base
spring.datasource.username=root
spring.datasource.password=root
```

Altere usuário e senha conforme sua instalação local.

> A propriedade `spring.jpa.hibernate.ddl-auto=create` recria as tabelas a cada inicialização. Troque por `update` para preservar os dados entre execuções.

## Como executar

```bash
cd automanager
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

Na inicialização, dados de exemplo são inseridos automaticamente (1 empresa, 3 usuários, 1 veículo e 2 vendas).

## Endpoints principais

| Recurso | Base URL |
|---|---|
| Empresas | `GET/POST /empresa` |
| Usuários | `GET/POST /usuario` |
| Usuários por empresa | `GET/POST /empresa/{id}/usuario` |
| Veículos | `GET/POST /veiculo` |
| Veículos por usuário | `GET/POST /usuario/{id}/veiculo` |
| Vendas | `GET/POST /venda` |
| Serviços | `GET/POST /servico` |
| Serviços por empresa | `POST /servico/empresa/{id}` |
| Mercadorias | `GET/POST /mercadoria` |
| Mercadorias por empresa | `POST /mercadoria/empresa/{id}` |
| Credenciais | `GET/POST /usuario/{id}/credencial/senha` |

Todas as respostas incluem `_links` HATEOAS para navegação entre recursos.
# webiii-atv4
