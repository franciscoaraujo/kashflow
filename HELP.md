```markdown
This project is a digital wallet microservice, built with **Java 17 + Spring Boot**, that allows operations such as **deposit**, **withdrawal**, **transfer**, as well as checking the **current balance** and **transaction history**. It uses **PostgreSQL** and **Apache Kafka**, all running with **Docker Compose**.
```

```markdown
This repository contains the code for the digital wallet service (Kashflow), which implements resilience and high availability, using technologies such as Kafka, PostgreSQL, Resilience4j, Prometheus, and Grafana for monitoring. It is designed to be highly available and fault-tolerant.
```

```markdown
- **Resilience**: Implementation of Retry and Circuit Breaker with Resilience4j.
- **Dead Letter Topic (DLT)**: Messages that fail processing are sent to an error topic.
- **Health Check**: `/actuator/health` endpoint to check the service's health.
- **Monitoring with Prometheus**: Collects health and performance metrics.
- **Visualization with Grafana**: Dashboard to monitor the service and Kafka.
``` 

```markdown
## How to Run the Project Locally
```

```markdown
Make sure the `docker-compose.yml` file is configured according to the environment. You can see an example of the configured `docker-compose.yml` in this repository.
```

```markdown
Run the command below to start all the containers required for the service to work:
```

```markdown
This will start the following services:
- **PostgreSQL**: Database used by the service.
- **Zookeeper**: Required for Kafka to function.
- **Kafka**: Messaging server used for communication between services.
- **Prometheus**: For metrics monitoring.
- **Grafana**: For visualizing the metrics collected by Prometheus.
```

```markdown
### 4. Spring Boot Configuration
```

```markdown
After starting the containers, the Spring Boot service will start automatically and will be accessible at the URL `http://localhost:8080`.
```

```markdown
### 6. Accessing the Health Check
```

```markdown
The service has a `/actuator/health` endpoint that can be used to verify if the system is working correctly. Access the health endpoint:
```

```markdown
## Monitoring with Prometheus and Grafana
```

```markdown
After starting Docker Compose, access Prometheus in the browser:
```

```markdown
### 3. Accessing Grafana
```

```markdown
Grafana will be accessible at the URL:
```

```markdown
The default credentials are:
```

```markdown
After logging in, add Prometheus as a Data Source:
``` 

```markdown
## Kafka Topics
```

```markdown
The service uses Kafka to audit transactions. When a transaction is performed, an audit message is published to a topic. Failed messages are sent to a Dead Letter Topic (DLT).
```

```markdown
### Topics Created in Kafka
- **kashflow-transactions**: Topic where transactions are published.
- **kashflow-transactions.DLT**: Dead Letter Topic, where failed messages are sent.
```