# 💰 Wallet Service (MVP)

Este projeto é um microserviço de carteira digital, feito com **Java 17 + Spring Boot**, que permite operações de **depósito**, **saque**, **transferência**, além de consultar o **saldo atual** e **histórico**. Utiliza **PostgreSQL** e **Apache Kafka**, tudo rodando com **Docker Compose**.

---

## 🚀 Como rodar o projeto

### Requisitos
- Docker + Docker Compose
- Java 21
- Maven 3.9+

### Passo a passo

```bash
# 1. Compile o projeto e gere o JAR
./mvnw clean package

# 2. Suba o ambiente completo
docker-compose up --build
