# Internship Management System - Microservices

A production-ready microservices architecture built with Spring Boot, Spring Cloud, and MySQL.

## 🏗️ Architecture
- **Eureka Server**: Service Discovery
- **API Gateway**: Central entry point (Port 8080)
- **User Service**: Profile & Account management
- **Internship Service**: Listings & Management
- **Skill Assessment Service**: Adaptive testing
- **Recommendation Service**: Weighted matching algorithm
- **Notification Service**: Alerts & Comms

## 🚀 Quick Start (Docker)

To run the entire system locally:

1.  **Build and Deploy**:
    ```powershell
    ./deploy.ps1
    ```
    This script will:
    - Build all Maven projects (skipping tests)
    - Initialize MySQL with all required databases
    - Start all 8 containers in the correct order

2.  **Access Dashboards**:
    - **Eureka Status**: [http://localhost:8761](http://localhost:8761)
    - **API Gateway**: [http://localhost:8080](http://localhost:8080)

## 🐳 Manual Deployment
If you prefer manual commands:
```bash
# Build
mvn clean package -DskipTests

# Run
docker-compose up --build -d
```

## 🛠️ Configuration
The system is configured to use environment variables for easy deployment to cloud platforms like Render, AWS, or Azure.
- `EUREKA_SERVER_URL`: Defaults to `http://localhost:8761/eureka/`
- `DATABASE_URL`: JDBC connection string
- `DATABASE_USERNAME` / `DATABASE_PASSWORD`: DB credentials

## 📈 Next Steps
- [ ] **Security**: Implement JWT in API Gateway.
- [ ] **Frontend**: Build a React/Angular UI.
- [ ] **Messaging**: Integrate RabbitMQ or Kafka for async communications.
- [ ] **Tracing**: Add Micrometer/Zipkin for distributed tracing.
