# Dockerfile Optimization for Production

## Current Dockerfile Issue

Your Dockerfiles likely use `java:17` or similar basic image. For Render's free tier with 512MB RAM limit, we need:

## ✅ Production-Ready Dockerfile

Each service (Eureka, User Service, etc.) should use this pattern:

```dockerfile
# Multi-stage build to minimize image size
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests

# Production runtime image - lightweight
FROM eclipse-temurin:17-jre-alpine

# Security: Run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher \
      -Dmanagement.endpoints.web.exposure.include=health \
      -Dserver.port=8080 \
      org.springframework.boot.health.HealthCheck

# Use exec form to handle signals properly
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Key Improvements

### 1. Multi-Stage Build
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder  # Large, for building
# ...build steps...

FROM eclipse-temurin:17-jre-alpine          # Tiny, for runtime
# ...runtime...
```

**Benefits:**
- Final image ~150MB instead of 800MB+
- Only runtime needed, no build tools
- Faster deployment to Render

### 2. Alpine Linux
```dockerfile
FROM eclipse-temurin:17-jre-alpine
```

**Benefits:**
- ~50MB instead of 300MB+ for base image
- Perfect for Render free tier
- Minimal attack surface

### 3. Non-Root User
```dockerfile
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
```

**Benefits:**
- Security best practice
- Prevents accidental root access in container
- Complies with security policies

### 4. Health Check
```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --retries=3
```

**Benefits:**
- Render detects failed containers
- Auto-restart if unhealthy
- Prevents zombie services

---

## Optimized Dockerfile by Service Type

### For Eureka Server
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY eureka-server/pom.xml .
RUN mvn dependency:resolve
COPY eureka-server/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar
USER appuser
EXPOSE 8761
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8761/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### For Microservices (Generic)
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar
USER appuser
# Health check will use the PORT from environment
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### For API Gateway (With extra memory settings)
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY api-gateway/pom.xml .
RUN mvn dependency:resolve
COPY api-gateway/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar
USER appuser
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
```

---

## Docker Build Commands

### Build Single Service
```bash
docker build -t user-service:1.0.0 -f user-service/Dockerfile .
docker build -t eureka-server:1.0.0 -f eureka-server/Dockerfile .
```

### Build All Services
```bash
docker build -t eureka-server:1.0.0 -f eureka-server/Dockerfile .
docker build -t api-gateway:1.0.0 -f api-gateway/Dockerfile .
docker build -t user-service:1.0.0 -f user-service/Dockerfile .
docker build -t skill-assessment-service:1.0.0 -f skill-assessment-service/Dockerfile .
docker build -t internship-service:1.0.0 -f internship-service/Dockerfile .
docker build -t recommendation-service:1.0.0 -f recommendation-service/Dockerfile .
docker build -t notification-service:1.0.0 -f notification-service/Dockerfile .
```

### Check Image Sizes
```bash
docker images | grep -E "eureka|api-gateway|user-service|skill|internship|recommendation|notification"
```

**Expected sizes:**
- Old (single-stage): 800MB-1GB
- New (multi-stage): 150-200MB each

---

## .dockerignore File

Create `.dockerignore` in each service directory to exclude unnecessary files:

```
# .dockerignore
target/
.git/
.gitignore
README.md
HELP.md
.env
*.log
.idea/
*.iml
.DS_Store
node_modules/
build/
```

---

## Testing New Dockerfile

```bash
# Build locally
docker build -t user-service:test -f user-service/Dockerfile .

# Run locally
docker run -e EUREKA_ENABLED=false \
           -e DATABASE_URL=jdbc:mysql://localhost:3306/user_service_db \
           -p 8081:8081 \
           user-service:test

# Test health
curl http://localhost:8081/actuator/health

# Check logs
docker logs container-id

# Stop
docker stop container-id
```

---

## Render-Specific Dockerfile Tips

### 1. Use Alpine Linux
```dockerfile
FROM eclipse-temurin:17-jre-alpine  # ~100MB
# NOT: FROM openjdk:17               # ~600MB+
```

### 2. Explicit Health Check
Render uses this to detect failures:
```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1
```

### 3. Non-Root User
Security best practice:
```dockerfile
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
```

### 4. Efficient Caching
```dockerfile
COPY pom.xml .           # Cache dependencies
RUN mvn dependency:resolve
COPY src ./src           # Only rebuild when code changes
RUN mvn clean package
```

### 5. Signal Handling
```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]  # exec form
# NOT: ENTRYPOINT java -jar app.jar      # shell form
```

---

## Current vs. Optimized Comparison

### Current Dockerfile (Typical)
```dockerfile
FROM maven:3.9-eclipse-temurin-17
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/*.jar"]
```

**Problems:**
- ❌ 1GB+ image size (times 6 services = 6GB+)
- ❌ Contains build tools (maven, compiler)
- ❌ No health check
- ❌ Runs as root
- ❌ Slow deploys to Render

### Optimized Dockerfile (Recommended)
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
USER appuser
HEALTHCHECK --interval=30s CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Benefits:**
- ✅ 150MB image (90% smaller!)
- ✅ Only runtime dependencies
- ✅ Health check for auto-restart
- ✅ Runs as non-root
- ✅ 5x faster deploys

---

## Updating Your Dockerfiles

Choose one:

### Option A: Update all at once
1. Replace each service's Dockerfile with optimized version
2. Test locally: `docker build -t service:test .`
3. Git push to trigger Render rebuild

### Option B: One at a time (safer)
1. Update Eureka Server Dockerfile
2. Deploy & test on Render
3. Update other services once confirmed working

---

## Troubleshooting Dockerfile Issues

### Build fails: "Cannot find symbol"
→ Ensure pom.xml is copied correctly

### Image too large (>500MB)
→ Using wrong base image, switch to alpine

### Health check failing
→ Ensure port matches (PORT env var or hardcoded)

### "No such file" errors
→ Check COPY paths (use .dockerignore to exclude)

### Build takes too long
→ Maven downloading dependencies, first build is slow

---

## Production Checklist

- [ ] Using multi-stage builds
- [ ] Using alpine Linux base image
- [ ] Non-root user configured
- [ ] Health check configured
- [ ] Signal handling correct (exec form)
- [ ] .dockerignore file exists
- [ ] Docker images <200MB each
- [ ] All JAR dependencies resolved
- [ ] No hardcoded credentials in image
- [ ] Environment variables externalized

---

## Resources

- **Eclipse Temurin**: https://hub.docker.com/_/eclipse-temurin
- **Docker Best Practices**: https://docs.docker.com/develop/dev-best-practices/
- **Multi-stage Builds**: https://docs.docker.com/build/building/multi-stage/
- **HEALTHCHECK**: https://docs.docker.com/engine/reference/builder/#healthcheck
