# Finwise API Gateway

The **Finwise API Gateway** is the unified entry point for all Finwise microservices. It routes client requests to appropriate backend services, handles cross-cutting concerns like authentication, rate-limiting, and CORS, and simplifies client communication.

## 🧭 Overview

- Built on **Spring Cloud Gateway**
- Central routing layer for Finwise microservices
- Dynamically discovers services via Eureka (Discovery Server)
- Supports route-based filtering, security, and load balancing

## 🚀 Features

- Intelligent request routing to internal microservices
- Eureka-based service discovery
- Integrated with Spring Cloud Config
- Support for CORS, rate-limiting, and custom filters
- Future-ready for OAuth2 / JWT-based authentication

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Docker & Docker Compose

## 📦 Clean the project

Use Maven to clean the project:

```bash
./mvn clean install
```

## 📦 Build the Project

Use Maven to build the project:

```bash
./mvn clean package
```
## 🐳 Docker 

Build Docker Image

```bash
docker build -t finwise-api-gateway:latest .
```

Run with Docker Compose
```bash
docker compose up -d
```
