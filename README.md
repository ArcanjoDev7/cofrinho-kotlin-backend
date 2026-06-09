# Cofrinho Kotlin Backend

Backend simples em Kotlin para guardar dinheiro em um cofrinho virtual.

## O que ele faz

- Registra depositos com descricao, valor e data.
- Lista o extrato do cofrinho.
- Calcula saldo e total economizado.
- Permite retirar dinheiro quando houver saldo suficiente.
- Mantem os dados em memoria para ser simples de estudar e evoluir.

## Tecnologias

- Kotlin 2
- Ktor 3
- Kotlinx Serialization
- Java 17
- Gradle

## Arquitetura

O projeto segue uma Clean Architecture simples:

- `domain`: entidades, value objects, contratos e regras puras.
- `application`: casos de uso e excecoes de aplicacao.
- `infra`: implementacoes tecnicas, como repositorio em memoria.
- `delivery`: entrada HTTP com Ktor, DTOs e mapeamentos.

## Como rodar

Com Java 17 e Gradle instalados, execute:

```bash
gradle run
```

A API sobe em:

```text
http://localhost:8080
```

## Endpoints

### Health check

```bash
curl http://localhost:8080/health
```

### Ver resumo do cofrinho

```bash
curl http://localhost:8080/api/cofrinho
```

### Fazer um deposito

```bash
curl -X POST http://localhost:8080/api/depositos \
  -H "Content-Type: application/json" \
  -d "{\"descricao\":\"Guardar para viagem\",\"valor\":50.00}"
```

### Fazer um saque

```bash
curl -X POST http://localhost:8080/api/saques \
  -H "Content-Type: application/json" \
  -d "{\"descricao\":\"Comprar material\",\"valor\":20.00}"
```

### Remover uma movimentacao

```bash
curl -X DELETE http://localhost:8080/api/movimentacoes/ID_DA_MOVIMENTACAO
```

## Proximos passos sugeridos

- Persistir os dados em PostgreSQL ou H2.
- Criar usuarios e autenticacao.
- Criar metas de economia.
- Adicionar Docker.
