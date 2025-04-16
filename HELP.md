# 💰 Wallet Service

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
```
---

# Kashflow - Resiliência e Observabilidade

Este repositório contém o código do serviço de carteira digital (Kashflow), que implementa resiliência e alta disponibilidade, utilizando tecnologias como Kafka, PostgreSQL, Resilience4j, Prometheus e Grafana para monitoramento. Ele é projetado para ser altamente disponível e com tolerância a falhas.

## Tecnologias Usadas
- **Spring Boot** (Backend)
- **PostgreSQL** (Banco de dados)
- **Apache Kafka** (Mensageria)
- **Resilience4j** (Resiliência)
- **Prometheus** (Monitoramento)
- **Grafana** (Visualização de métricas)
- **Docker Compose** (Ambiente de desenvolvimento)

## Funcionalidades
- **Resiliência**: Implementação de Retry e Circuit Breaker com Resilience4j.
- **Dead Letter Topic (DLT)**: Mensagens que falham no processamento são direcionadas para um tópico de erro.
- **Health Check**: Endpoint `/actuator/health` para verificar a saúde do serviço.
- **Monitoramento com Prometheus**: Coleta de métricas de saúde e desempenho.
- **Visualização com Grafana**: Dashboard para monitorar o serviço e Kafka.

## Pré-requisitos
Antes de executar o serviço, certifique-se de que você tem as seguintes ferramentas instaladas:
- Docker e Docker Compose para gerenciar containers
- Maven ou Gradle para build do Spring Boot
- Java 11+

## Estrutura do Projeto
```
kashflow/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── bitwisebytes/
│   │   │           ├── api/
│   │   │           │   └── Audit/
│   │   │           ├── config/
│   │   │           │   ├── KafkaAuditConsumerConfig.java
│   │   │           │   └── KafkaErrorHandlerConfig.java
│   │   │           ├── dto/
│   │   │           │   ├── AuditTransactionDto.java
│   │   │           │   └── WalletTransactionEventDto.java
│   │   │           ├── entity/
│   │   │           │   └── AuditTransaction.java
│   │   │           ├── repository/
│   │   │           │   └── AuditTransactionRepository.java
│   │   │           ├── service/
│   │   │           │   ├── AuditService.java
│   │   │           │   ├── KafkaConsumerService.java
│   │   │           │   ├── KafkaProducerService.java
│   │   │           │   └── WalletTransactionConsumer.java
│   │   │           └── KashflowApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       ├── application.yml
│   │       ├── application-dev-docker.yml
│   │       ├── application-dev-local.yml
│   │       └── prometheus.yml
│   └── test/
└── docker-compose.yml
```

## Como Executar o Projeto Localmente

### 1. Clonar o Repositório
```bash
git clone https://github.com/seu-usuario/kashflow.git
cd kashflow
```

### 2. Configuração do Docker Compose
Certifique-se de que o arquivo `docker-compose.yml` está configurado conforme o ambiente. Você pode ver um exemplo do `docker-compose.yml` já configurado neste repositório.

### 3. Iniciar os Containers com Docker Compose
Execute o comando abaixo para iniciar todos os containers necessários para o serviço funcionar:
```bash
docker-compose up
```
Isso irá subir os seguintes serviços:
- **PostgreSQL**: Banco de dados utilizado pelo serviço.
- **Zookeeper**: Necessário para o funcionamento do Kafka.
- **Kafka**: Servidor de mensageria utilizado para comunicação entre serviços.
- **Prometheus**: Para monitoramento de métricas.
- **Grafana**: Para visualização das métricas coletadas pelo Prometheus.

### 4. Configuração do Spring Boot
Certifique-se de que o `application.yml` ou `application.properties` do Spring Boot está configurado corretamente com os seguintes detalhes:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://kashflow_postgres:5432/kashflow_db
    username: kashflow_user
    password: kashflow_pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
  kafka:
    bootstrap-servers: kashflow_kafka:9092
    consumer:
      group-id: kashflow-consumer-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      listener:
        ack-mode: manual
```

### 5. Iniciar o Serviço
Após iniciar os containers, o serviço Spring Boot será iniciado automaticamente e estará acessível na URL `http://localhost:8080`.

### 6. Acessando o Health Check
O serviço tem um endpoint `/actuator/health` que pode be usado para verificar se o sistema está funcionando corretamente. Acesse o endpoint de saúde:
```bash
curl http://localhost:8080/actuator/health
```
A resposta será algo como:
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499796832256,
        "free": 304804125696,
        "threshold": 10485760
      }
    },
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "hello": "Hello from PostgreSQL"
      }
    },
    "kafka": {
      "status": "UP",
      "details": {
        "brokers": ["kashflow_kafka:9092"]
      }
    }
  }
}
```

## Monitoramento com Prometheus e Grafana

### 1. Configuração do Prometheus
O Prometheus é configurado para coletar métricas a partir do Spring Boot Actuator. Para isso, crie o arquivo `prometheus.yml` como mostrado abaixo:
```yaml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'kashflow'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['kashflow_app:8080']
```
Este arquivo já deve estar configurado no `docker-compose.yml`.

### 2. Acessando o Prometheus
Após iniciar o Docker Compose, acesse o Prometheus no navegador:
```
http://localhost:9090
```
Use a interface do Prometheus para visualizar as métricas coletadas do serviço.

### 3. Acessando o Grafana
O Grafana estará acessível na URL:
```
http://localhost:3000
```
As credenciais padrão são:
- **Usuário**: admin
- **Senha**: admin

Após o login, adicione o Prometheus como Data Source:
1. Clique em "Configuration" (ícone de engrenagem) -> "Data Sources" -> "Add Data Source".
2. Selecione "Prometheus".
3. Em URL, coloque `http://prometheus:9090`.
4. Clique em "Save & Test" para verificar se a conexão está funcionando.

Agora, você pode criar dashboards personalizados para monitorar as métricas do serviço e do Kafka.

## Tópicos Kafka
O serviço utiliza o Kafka para auditar transações. Quando uma transação é realizada, uma mensagem de auditoria é publicada em um tópico. As mensagens falhas são enviadas para um Dead Letter Topic (DLT).

### Tópicos Criados no Kafka
- **kashflow-transactions**: Tópico onde as transações são publicadas.
- **kashflow-transactions.DLT**: Tópico de Dead Letter, onde as mensagens falhas são enviadas.

---

This README reflects the renamed project "Kashflow" and includes the directory structure you provided, while maintaining the original content's intent and details. Let me know if you'd like further adjustments!