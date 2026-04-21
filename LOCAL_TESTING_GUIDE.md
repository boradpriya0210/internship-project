# Testing Locally Before Cloud Deployment

## 🐳 Docker Compose Setup (Recommended First Step)

Test the new configuration locally before deploying to Render.

### Step 1: Build All Services

```bash
# From project root
mvn clean package -DskipTests -f eureka-server/pom.xml
mvn clean package -DskipTests -f api-gateway/pom.xml
mvn clean package -DskipTests -f user-service/pom.xml
mvn clean package -DskipTests -f skill-assessment-service/pom.xml
mvn clean package -DskipTests -f internship-service/pom.xml
mvn clean package -DskipTests -f recommendation-service/pom.xml
mvn clean package -DskipTests -f notification-service/pom.xml
```

Or use single command:
```bash
mvn clean package -DskipTests
```

### Step 2: Start Docker Compose

```bash
docker-compose up -d

# Watch logs
docker-compose logs -f

# Check status
docker-compose ps
```

### Step 3: Verify Services (Docker Network)

```bash
# Eureka Dashboard (from host machine)
curl http://localhost:8761

# Eureka REST API
curl http://localhost:8761/eureka/apps

# API Gateway Health
curl http://localhost:8080/actuator/health

# Individual service health
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # Skill Assessment
curl http://localhost:8083/actuator/health  # Internship
curl http://localhost:8084/actuator/health  # Recommendation
curl http://localhost:8085/actuator/health  # Notification
```

### Step 4: Test API Gateway Routing

```bash
# Test routes through API Gateway
curl http://localhost:8080/api/users/health
curl http://localhost:8080/api/assessments/health
curl http://localhost:8080/api/internships/health
curl http://localhost:8080/api/recommendations/health
curl http://localhost:8080/api/notifications/health
```

### Step 5: Monitor & Troubleshoot

```bash
# View specific service logs
docker-compose logs eureka-server
docker-compose logs api-gateway
docker-compose logs user-service

# Enter service container
docker exec -it user-service bash

# Check network
docker network inspect internship_default

# Stop and clean up
docker-compose down
docker-compose down -v  # Also remove volumes
```

---

## 🔍 Testing Locally (Docker Profile)

The `application-docker.yml` configs are already set up for Docker Compose:

### Eureka Server (docker-compose uses http://)
```yaml
# application-docker.yml
eureka:
  instance:
    hostname: eureka-server  # Docker service name
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
```

### Services Register with Docker Eureka
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
```

**Note**: Docker Compose has `SPRING_PROFILES_ACTIVE=docker` in docker-compose.yml

---

## ✅ Local Testing Checklist

- [ ] All services build without errors
- [ ] Docker Compose starts all containers
- [ ] Eureka dashboard shows all 6 services as "UP"
- [ ] API Gateway health check returns status=UP
- [ ] Each service health check shows UP
- [ ] API Gateway routes requests to services
- [ ] Database queries work (check logs)
- [ ] No circuit breaker OPEN messages
- [ ] Logs show service registration: "Instance registered: ..."

---

## 🐛 Troubleshooting Local Setup

### Issue: "Cannot execute request on any known server"
**In Docker**: Check if Eureka service is running
```bash
docker-compose logs eureka-server
docker ps | grep eureka
```

### Issue: Services not appearing in Eureka
**Check**:
```bash
# Wait 30-60 seconds, then check
curl http://localhost:8761/eureka/apps

# If still missing, check service logs
docker-compose logs user-service | grep -i eureka
```

### Issue: Database connection failed
**Check**:
```bash
# Is MySQL running?
docker ps | grep mysql

# Check MySQL logs
docker-compose logs mysql

# Verify credentials
# user: root, password: root (from docker-compose.yml)
```

### Issue: Port already in use
```bash
# Kill process on port
lsof -i :8080  # Find process
kill -9 PID    # Kill it

# Or let Docker assign random port
# Edit docker-compose.yml: "8080" → "8080" (no static mapping)
```

### Issue: Out of memory
```bash
# Check Docker resources
docker stats

# Increase Docker's memory limit in Docker Desktop settings
```

---

## 📊 Verifying Eureka Registration

### From Eureka Dashboard (web)
```
Open browser: http://localhost:8761
Should show all services in "Instances currently registered with Eureka"
```

### From REST API (JSON)
```bash
curl http://localhost:8761/eureka/apps/list.json | jq

# Output should show:
{
  "applications": {
    "application": [
      {"name": "USER-SERVICE", "instance": [...]},
      {"name": "SKILL-ASSESSMENT-SERVICE", "instance": [...]},
      {"name": "INTERNSHIP-SERVICE", "instance": [...]},
      {"name": "RECOMMENDATION-SERVICE", "instance": [...]},
      {"name": "NOTIFICATION-SERVICE", "instance": [...]},
      {"name": "API-GATEWAY", "instance": [...]}
    ]
  }
}
```

### From Service Logs
```bash
docker-compose logs user-service | grep -i "registered"

# Expected output:
# Registering application USER-SERVICE with eureka server at http://eureka-server:8761/eureka/
# InstanceRegistry: Instance USER-SERVICE:... registered successfully
```

---

## 🔄 Transition: Local → Docker → Cloud

### 1. Development (application.yml)
- All localhost
- Easy debugging
- `mvn spring-boot:run`

### 2. Docker Compose (application-docker.yml)
- Docker service names
- Internal network
- `docker-compose up`

### 3. Production/Render (application-prod.yml)
- External/public URLs
- Environment variables
- Deployed on cloud

---

## 💡 What's Different Between Profiles?

| Aspect | Dev | Docker | Cloud (prod) |
|--------|-----|--------|-------------|
| Eureka URL | localhost:8761 | eureka-server:8761 | https://eureka-xxx.onrender.com |
| Database | localhost:3306 | mysql:3306 | Aiven cloud database |
| Network | localhost | internal network | public internet |
| Config | hardcoded | hardcoded | environment variables |
| SSL/TLS | no | no | yes (HTTPS) |
| Profile | default | docker | prod |

---

## 🧪 Integration Tests

After everything is running locally, test:

### 1. Service Discovery
```bash
# All services should return UP
for i in 8081 8082 8083 8084 8085; do
  echo "Service on port $i:"
  curl http://localhost:$i/actuator/health | jq .status
done
```

### 2. API Gateway Routing
```bash
# Test each route
curl http://localhost:8080/api/users/health \
  -H "Content-Type: application/json" \
  -H "Accept: application/json"
```

### 3. Load Balancing
```bash
# Multiple calls should hit different instances (if scaled)
for i in {1..5}; do
  curl http://localhost:8080/api/users/health
done
```

### 4. Graceful Degradation
```bash
# Stop a service
docker stop user-service

# Wait for health check to fail (30-60 seconds)

# Try accessing through gateway
curl http://localhost:8080/api/users/health
# Should return 503 (Service Unavailable) not timeout

# Restart service
docker start user-service

# Service auto-recovers
```

---

## 📈 Performance Testing

### Load Test API Gateway
```bash
# Using Apache Bench (ab)
ab -n 100 -c 10 http://localhost:8080/api/users/health

# Using curl loop
for i in {1..100}; do
  curl http://localhost:8080/api/users/health &
done
wait
```

### Monitor Resource Usage
```bash
# Watch Docker stats
docker stats --no-stream

# Watch specific service
docker stats user-service
```

---

## 🧹 Cleanup Commands

```bash
# Stop all containers
docker-compose stop

# Stop and remove (keep volumes)
docker-compose down

# Stop and remove everything (including volumes)
docker-compose down -v

# Remove specific container
docker rm user-service

# View all images
docker images | grep internship

# Remove image
docker rmi user-service:latest
```

---

## 📝 Docker Compose Reference

Your current `docker-compose.yml` has:
- MySQL database
- Eureka Server
- 5 microservices
- Network: internship_default
- Volumes: db_data

Each service has:
- `EUREKA_SERVER_URL=http://eureka-server:8761/eureka/` ← Docker network
- Database credentials: root/root
- Health checks for auto-restart

---

## ✨ After Local Testing

1. ✅ Verified everything works locally
2. ✅ Understood how services discover each other
3. ✅ Tested API Gateway routing
4. ✅ Confirmed circuit breaker behavior
5. ✅ Ready for Render deployment

**Next**: Follow RENDER_QUICKSTART.md for cloud deployment

---

## 🚀 One-Command Local Start

```bash
# Build and start everything
mvn clean package -DskipTests && docker-compose up -d

# Wait 30 seconds for services to register, then test
sleep 30 && curl http://localhost:8761/eureka/apps | head -20
```

---

For production deployment, see: **RENDER_QUICKSTART.md**
