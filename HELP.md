# 💰 Wallet Service

This project is a digital wallet microservice, made with **Java 17 + Spring Boot**, which allows **deposit**, **withdrawal**, **transfer** operations, as well as checking the **current** and **historical** balance. It uses **PostgreSQL** and **Apache Kafka**, all running with **Docker Compose**.

---

## 🚀 How to run the project

### Requirements
- Docker + Docker Compose
- Java 21
- Maven 3.9+

### Step by step

```bash
# 1. Compile the project and generate the JAR
./mvnw clean package

# 2. Upload the complete environment
docker-compose up --build
```
---

# Kashflow - Resilience and Observability

This repository contains the code for the digital wallet service (Kashflow), which implements resilience and high availability, using technologies such as Kafka, PostgreSQL, Resilience4j, Prometheus and Grafana for monitoring. It is designed to be highly available and fault tolerant.

## Technologies Used
- **Spring Boot** (Backend)
- **PostgreSQL** (Database)
- **Apache Kafka** (Messaging)
- **Resilience4j** (Resilience)
- **Prometheus** (Monitoring)
- **Grafana** (Metrics Visualization)
- **Docker Compose** (Development Environment)

## Features
- **Resilience**: Implementation of Retry and Circuit Breaker with Resilience4j.
- **Dead Letter Topic (DLT)**: Messages that fail to be processed are directed to an error topic.
- **Health Check**: Endpoint `/actuator/health` to check the health of the service.
- **Monitoring with Prometheus**: Collection of health and performance metrics.
- **Visualization with Grafana**: Dashboard to monitor the service and Kafka.

## Prerequisites
Before running the service, make sure you have the following tools installed:
- Docker and Docker Compose to manage containers
- Maven or Gradle to build Spring Boot
- Java 11+

## Project Structure
```
kashflow/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/
│ │ │ └── bitwisebytes/
│ │ │ ├── api/
│ │ │ │ └── Audit/
│ │ │ ├── config/
│ │ │ │ ├── KafkaAuditConsumerConfig.java
│ │ │ │ └── KafkaErrorHandlerConfig.java
│ │ │ ├── dto/
│ │ │ │ ├── AuditTransactionDto.java
│ │ │ │ └── WalletTransactionEventDto.java
│ │ │ ├── entity/
│ │ │ │ └── AuditTransaction.java
│ │ │ ├── repository/
│ │ │ │ └── AuditTransactionRepository.java
│ │ │ ├── service/
│ │ │ │ ├── AuditService.java
│ │ │ │ ├── KafkaConsumerService.java
│ │ │ │ ├── KafkaProducerService.java
│ │ │ │ └── WalletTransactionConsumer.java
│ │ │ └── KashflowApplication.java
│ │ └── resources/
│ │ ├── static/
│ │ ├── templates/
│ │ ├── application.yml
│ │ ├── application-dev-docker.yml
│ │ ├── application-dev-local.yml
│ │ └── prometheus.yml
│ └── test/
└── docker-compose.yml
```

## How to Run the Project Locally

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/kashflow.git
cd kashflow
```

### 2. Docker Compose Configuration
Make sure the `docker-compose.yml` file is configured according to the environment. You can see an example of the `docker-compose.yml` already configured in this repository.

### 3. Start Containers with Docker Compose
Run the command below to start all the containers necessary for the service to work:
```bash
docker-compose up
```
This will bring up the following services:
- **PostgreSQL**: Database used by the service.
- **Zookeeper**: Required for Kafka to work.
- **Kafka**: Messaging server used for communication between services.
- **Prometheus**: For monitoring metrics.
- **Grafana**: To visualize metrics collected by Prometheus.

### 4. Spring Boot Configuration
Make sure that the Spring Boot `application.yml` or `application.properties` is configured correctly with the following details:
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

### 5. Starting the Service
After starting the containers, the Spring Boot service will start automatically and will be accessible at the URL `http://localhost:8080`.

### 6. Accessing the Health Check
The service has an endpoint `/actuator/health` that can be used to check if the system is working properly.