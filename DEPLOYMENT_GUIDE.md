# Spring Boot Microservices - Render Cloud Deployment Guide

## Problem Analysis

Your deployment on Render failed due to:

1. **Internal Hostname Resolution**: Eureka Server URL configured as `http://eureka-server:8761/eureka/` only works in Docker Compose (same network). On Render, each service gets its own isolated URL.

2. **No Fallback Mechanism**: If Eureka is unavailable or unreachable, the entire API Gateway fails - it can't route requests.

3. **Load-Balanced Routes Dependency**: The API Gateway uses `lb://user-service` which requires Eureka to resolve service names. Without it, requests fail.

4. **Single Configuration**: Same config used for local development, Docker Compose, and production - not cloud-optimized.

---

## Solution Overview

### 1. **Profile-Based Configuration**
Created environment-specific configs:
- `application.yml` - Default/dev
- `application-docker.yml` - Docker Compose
- `application-prod.yml` - Render Cloud

### 2. **Optional Eureka**
Made Eureka optional with `EUREKA_ENABLED` environment variable:
- If Eureka is disabled, API Gateway can still route via environment variables
- Services still try to register, but won't fail if Eureka is unreachable

### 3. **Circuit Breaker Pattern**
Added Resilience4j for fault tolerance:
- If a service is down, requests get a graceful fallback response
- Not a crash, not a timeout - just a 503 with error message

### 4. **Environment Variable Support**
All critical URLs and settings are configurable:
- Eureka URL
- Service URLs
- Database connections
- Eureka enabled/disabled flag

---

## Configuration Changes Explained

### Eureka Server (`application-prod.yml`)

```yaml
eureka:
  instance:
    prefer-ip-address: true  # Use IP instead of hostname
    hostname: ${EUREKA_HOSTNAME:${eureka.instance.ip-address}}  # Configurable
    ip-address: ${EUREKA_IP_ADDRESS:}  # Can be set via env var
```

**Changes:**
- ✅ Uses IP addresses (cloud-friendly)
- ✅ Hostname overridable via environment
- ✅ Longer heartbeat intervals (60s vs 4s) for cloud stability
- ✅ Self-preservation enabled (recommended for production)

### API Gateway (`application-prod.yml`)

```yaml
eureka:
  client:
    enabled: ${EUREKA_ENABLED:true}  # Can disable Eureka
    service-url:
      defaultZone: ${EUREKA_SERVER_URL:}  # Cloud URL

resilience4j:
  circuitbreaker:
    # Auto-fallback after 5 failures in 10 calls
    instances:
      userServiceCircuitBreaker:
        failureRateThreshold: 50
```

**Changes:**
- ✅ Eureka optional (enabled via env var)
- ✅ Circuit breaker fallback for each service
- ✅ Direct service URLs if Eureka disabled

### Microservices (`application-prod.yml`)

```yaml
eureka:
  client:
    enabled: ${EUREKA_ENABLED:true}
    service-url:
      defaultZone: ${EUREKA_SERVER_URL:}
  instance:
    instance-id: ${spring.application.name}:${server.port}:${random.uuid}
```

**Changes:**
- ✅ Eureka optional
- ✅ Random UUID in instance ID to support restarts
- ✅ Connection pooling optimized for cloud

---

## Deployment on Render

### Step 1: Update render.yaml

```yaml
envVars:
  - key: SPRING_PROFILES_ACTIVE
    value: prod  # ← Use prod profile
  - key: EUREKA_SERVER_URL
    value: https://eureka-server-xxx.onrender.com/eureka/  # ← Replace with your URL
  - key: EUREKA_ENABLED
    value: "true"
  - key: DATABASE_URL
    value: jdbc:mysql://your-db-host:port/db?useSSL=true&serverTimezone=UTC
```

### Step 2: Get Your Eureka Server URL

After deploying Eureka Server to Render:
1. Go to Render Dashboard
2. Click on `eureka-server` service
3. Copy the URL from "Render" section
4. Should look like: `https://eureka-server-xyz.onrender.com`

### Step 3: Update All Services

Replace Eureka URL in each service's render.yaml:
- User Service: `EUREKA_SERVER_URL=https://eureka-server-xyz.onrender.com/eureka/`
- Skill Assessment Service: `EUREKA_SERVER_URL=https://eureka-server-xyz.onrender.com/eureka/`
- Internship Service: `EUREKA_SERVER_URL=https://eureka-server-xyz.onrender.com/eureka/`
- (Same for others)

### Step 4: Deploy Order

1. **Database** (if not already deployed)
2. **Eureka Server** (must be first) - Wait for health check to pass
3. **Microservices** (User, Skill Assessment, Internship, Recommendation, Notification)
4. **API Gateway** (last)

---

## Alternative: Disable Eureka for Simpler Deployment

If Eureka causes issues, you can disable it entirely:

```yaml
envVars:
  - key: EUREKA_ENABLED
    value: "false"  # ← Disable Eureka
  - key: USER_SERVICE_URL
    value: https://user-service-xxx.onrender.com  # Direct URLs
  - key: SKILL_ASSESSMENT_SERVICE_URL
    value: https://skill-assessment-xxx.onrender.com
  - key: INTERNSHIP_SERVICE_URL
    value: https://internship-service-xxx.onrender.com
  - key: RECOMMENDATION_SERVICE_URL
    value: https://recommendation-service-xxx.onrender.com
  - key: NOTIFICATION_SERVICE_URL
    value: https://notification-service-xxx.onrender.com
```

**In API Gateway `application-prod.yml`:**
```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: ${EUREKA_ENABLED:false}  # Disable discovery
      routes:
        - id: user-service
          uri: ${USER_SERVICE_URL:lb://user-service}  # Direct URL
```

---

## FallbackController

Added new controller for circuit breaker fallbacks:

```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/**")
    public ResponseEntity<Map<String, Object>> fallback() {
        // Returns 503 with error message
        // Gracefully informs client service is unavailable
    }
}
```

**Behavior:**
- Service A → (fails) → Circuit Breaker opens → Fallback triggered → Returns 503
- Client gets clear error instead of timeout/crash

---

## Database Configuration for Cloud

### Current (from render.yaml):
```
DATABASE_URL=jdbc:mysql://mysql-1de121cc-priyaborad56-f021.i.aivencloud.com:21279/user_service_db
```

**Important Security Notes:**
⚠️ **Never commit credentials to git!**

1. **For Aiven MySQL (your setup):**
   - URL: `jdbc:mysql://host:port/db?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true`
   - Add `useSSL=true` for encrypted connections
   - Credentials in Render environment variables (not in code)

2. **Connection Pooling:**
   ```yaml
   hikari:
     maximum-pool-size: 10        # Cloud limit (not 20)
     connection-timeout: 20000    # More time to connect
     idle-timeout: 300000         # 5 minutes
     max-lifetime: 1200000        # 20 minutes
   ```

---

## Monitoring & Debugging

### Health Endpoints
Each service exposes health status:
```
GET https://api-gateway-xxx.onrender.com/actuator/health
GET https://eureka-server-xxx.onrender.com/actuator/health
GET https://user-service-xxx.onrender.com/actuator/health
```

Returns:
```json
{
  "status": "UP",
  "components": {
    "eureka": {
      "status": "UP",
      "details": {
        "registered-instances": 5
      }
    }
  }
}
```

### Logs
Monitor in Render Dashboard:
- Service → Logs tab
- Search for `ERROR` or `WARN`
- Check connection issues, timeouts, etc.

### Common Issues

| Issue | Solution |
|-------|----------|
| `Cannot execute request on any known server` | Eureka URL in env var is wrong |
| `Circuit breaker is OPEN` | Service is down - check its logs |
| `Connection timeout to database` | Check DATABASE_URL, SSL settings |
| `Service registered but not found` | Wait 30-60s for Eureka refresh |
| Port conflicts | Ensure PORT env var is set correctly |

---

## Best Practices Applied

✅ **Environment Profiles**: Separate configs for dev/docker/prod  
✅ **Environment Variables**: All URLs/passwords externalized  
✅ **Eureka Optional**: Works with or without service discovery  
✅ **Circuit Breakers**: Graceful fallbacks instead of crashes  
✅ **Health Checks**: Render can detect failed services  
✅ **Connection Pooling**: Optimized for cloud resources  
✅ **Logging Levels**: Info for prod (not debug spam)  
✅ **Instance ID Randomization**: Supports service restarts  
✅ **SSL/TLS**: Database connections encrypted  
✅ **Resource Limits**: Memory/CPU appropriate for free tier  

---

## Next Steps

1. **Build & Deploy:**
   ```bash
   mvn clean package -DskipTests
   git push  # Trigger Render deployment
   ```

2. **Test Eureka:**
   ```bash
   curl https://eureka-server-xxx.onrender.com/eureka/apps
   ```

3. **Test API Gateway:**
   ```bash
   curl https://api-gateway-xxx.onrender.com/api/users/health
   ```

4. **Monitor Logs:**
   - Check each service's logs in Render Dashboard
   - Look for "registered" messages from services
   - Ensure no "Cannot execute request" errors

5. **Verify Services:**
   ```bash
   curl https://eureka-server-xxx.onrender.com/actuator/health
   ```

---

## Files Changed

| File | Change |
|------|--------|
| `eureka-server/src/main/resources/application-prod.yml` | NEW - Cloud config |
| `eureka-server/src/main/resources/application-docker.yml` | NEW - Docker config |
| `api-gateway/src/main/resources/application-prod.yml` | NEW - With circuit breaker |
| `api-gateway/pom.xml` | Added resilience4j dependencies |
| `api-gateway/src/main/java/.../FallbackController.java` | NEW - Fallback handler |
| `user-service/src/main/resources/application-prod.yml` | NEW - Cloud config |
| `skill-assessment-service/src/main/resources/application-prod.yml` | NEW - Cloud config |
| `internship-service/src/main/resources/application-prod.yml` | NEW - Cloud config |
| `recommendation-service/src/main/resources/application-prod.yml` | NEW - Cloud config |
| `notification-service/src/main/resources/application-prod.yml` | NEW - Cloud config |
| `render.yaml` | Updated env vars with proper URLs |

---

## Support

For additional help:
- **Spring Cloud Documentation**: https://spring.io/projects/spring-cloud
- **Render Documentation**: https://render.com/docs
- **Eureka Configuration**: https://cloud.spring.io/spring-cloud-netflix/
- **Resilience4j**: https://resilience4j.readme.io/
