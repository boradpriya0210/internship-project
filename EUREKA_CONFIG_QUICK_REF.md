# Quick Configuration Reference

## The Fix in One Chart

| Component | OLD (❌ Broken) | NEW (✅ Fixed) |
|-----------|-----------------|-----------------|
| **Eureka Server URL** | `http://eureka-server:8761/eureka/` | `https://eureka-server-6n60.onrender.com/eureka/` |
| **Protocol** | HTTP (internal only) | HTTPS (public internet) |
| **Hostname** | Internal Docker name | Public Render domain |
| **Client Hostname** | `prefer-ip-address: true` | `prefer-ip-address: false` |
| **Ports** | All enabled (8761) | Secure only (443 HTTPS) |
| **Health Check** | HTTP endpoint | HTTPS endpoint |

---

## Eureka Server - application-prod.yml

```yaml
server:
  port: ${PORT:8761}

spring:
  application:
    name: eureka-server

eureka:
  instance:
    # CRITICAL: Use public domain for Render
    hostname: eureka-server-6n60.onrender.com  # THIS IS THE FIX
    prefer-ip-address: false
    instance-id: ${spring.application.name}:${hostname}:${server.port}
    # Public HTTPS URLs
    home-page-url: https://eureka-server-6n60.onrender.com/
    status-page-url: https://eureka-server-6n60.onrender.com/actuator/info
    health-check-url: https://eureka-server-6n60.onrender.com/actuator/health
    health-check-url-path: /actuator/health
    # HTTPS only
    non-secure-port-enabled: false
    secure-port-enabled: true
    secure-port: 443
  
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:${server.port}/eureka/
  
  server:
    enable-self-preservation: true
    eviction-interval-timer-in-ms: 60000
    renewal-percent-threshold: 0.85
    expected-client-renewal-interval-seconds: 30

management:
  endpoints:
    web:
      exposure:
        include: health,info,env
  endpoint:
    health:
      show-details: always
```

**KEY POINTS**:
- `hostname: eureka-server-6n60.onrender.com` - YOUR PUBLIC DOMAIN
- `secure-port: 443` - HTTPS port
- `non-secure-port-enabled: false` - No HTTP
- `home-page-url: https://...` - Must use HTTPS

---

## Eureka Client - application-prod.yml (For All Services)

```yaml
eureka:
  client:
    enabled: ${EUREKA_ENABLED:true}
    service-url:
      # CRITICAL: Use HTTPS and public domain
      defaultZone: ${EUREKA_SERVER_URL:https://eureka-server-6n60.onrender.com/eureka/}
    fetch-registry: true  # or false for API Gateway
    register-with-eureka: true
    registry-fetch-interval-seconds: 30
    instance-info-replication-interval-seconds: 30
  
  instance:
    # CRITICAL: For cloud deployment
    prefer-ip-address: false
    hostname: ${HOSTNAME:}  # Render sets this
    instance-id: ${spring.application.name}:${server.port}:${random.uuid}
    # HTTPS only
    non-secure-port-enabled: false
    secure-port-enabled: true
    secure-port: 443
    health-check-url-path: /actuator/health
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

**KEY POINTS**:
- `defaultZone: ${EUREKA_SERVER_URL:https://...}` - ENVIRONMENT VARIABLE (falls back to HTTPS)
- `prefer-ip-address: false` - Use hostname
- `hostname: ${HOSTNAME:}` - Let Render set it
- `secure-port: 443` - HTTPS only
- `non-secure-port-enabled: false` - No HTTP

---

## render.yaml - Key Sections

### Eureka Server
```yaml
- type: web
  name: eureka-server
  env: docker
  healthCheckPath: /actuator/health
  envVars:
    - key: PORT
      value: "8761"
    - key: SPRING_PROFILES_ACTIVE
      value: prod
    - key: EUREKA_HOSTNAME
      value: eureka-server-6n60.onrender.com  # YOUR DOMAIN
    - key: JAVA_TOOL_OPTIONS
      value: "-Xmx512m -Xss512k -XX:+UseG1GC"
```

### Each Service (User, Skill Assessment, etc.)
```yaml
- type: web
  name: user-service
  env: docker
  healthCheckPath: /actuator/health
  envVars:
    - key: PORT
      value: "8081"
    - key: SPRING_PROFILES_ACTIVE
      value: prod
    - key: EUREKA_ENABLED
      value: "true"
    - key: EUREKA_SERVER_URL
      value: "https://eureka-server-6n60.onrender.com/eureka/"  # HTTPS!
    - key: JAVA_TOOL_OPTIONS
      value: "-Xmx512m -Xss512k -XX:+UseG1GC"
    # ... other env vars (DATABASE_URL, etc.)
```

### API Gateway
```yaml
- type: web
  name: api-gateway
  env: docker
  healthCheckPath: /actuator/health
  envVars:
    - key: PORT
      value: "8080"
    - key: SPRING_PROFILES_ACTIVE
      value: prod
    - key: EUREKA_ENABLED
      value: "true"
    - key: EUREKA_SERVER_URL
      value: "https://eureka-server-6n60.onrender.com/eureka/"  # HTTPS!
    - key: JAVA_TOOL_OPTIONS
      value: "-Xmx512m -Xss512k -XX:+UseG1GC"
```

**PATTERN FOR ALL SERVICES**:
```yaml
- key: SPRING_PROFILES_ACTIVE
  value: prod
- key: EUREKA_ENABLED
  value: "true"
- key: EUREKA_SERVER_URL
  value: "https://eureka-server-6n60.onrender.com/eureka/"
```

---

## Testing Commands

### 1. Check Eureka Server Health
```bash
curl https://eureka-server-6n60.onrender.com/actuator/health
```

Expected: `{"status":"UP"}`

### 2. View Registered Services
```bash
curl https://eureka-server-6n60.onrender.com/eureka/apps
```

Expected: XML listing all services

### 3. Check Service Registration
```bash
curl https://eureka-server-6n60.onrender.com/eureka/apps/USER-SERVICE
```

Expected: XML with user-service instance details

### 4. View Eureka Dashboard
```
https://eureka-server-6n60.onrender.com/
```

Should show all services as "UP"

### 5. Test API Gateway
```bash
curl https://api-gateway-YOUR-ID.onrender.com/api/users/health
```

Expected: Route to user-service successfully

---

## Variable Substitution

Spring Boot automatically substitutes environment variables in configuration files:

### In application-prod.yml
```yaml
# This will use the EUREKA_SERVER_URL environment variable
defaultZone: ${EUREKA_SERVER_URL:https://eureka-server-6n60.onrender.com/eureka/}
#              └─ env var ─┘   └─ fallback default ─┘
```

### In render.yaml
```yaml
envVars:
  - key: EUREKA_SERVER_URL
    value: "https://eureka-server-6n60.onrender.com/eureka/"
```

Spring will read this and substitute into the config file!

---

## Files to Update

All these files have been updated with the correct Eureka configuration:

1. ✅ `eureka-server/src/main/resources/application-prod.yml`
2. ✅ `api-gateway/src/main/resources/application-prod.yml`
3. ✅ `user-service/src/main/resources/application-prod.yml`
4. ✅ `skill-assessment-service/src/main/resources/application-prod.yml`
5. ✅ `internship-service/src/main/resources/application-prod.yml`
6. ✅ `recommendation-service/src/main/resources/application-prod.yml`
7. ✅ `notification-service/src/main/resources/application-prod.yml`
8. ✅ `render.yaml`

---

## The Critical Changes

### Before
```yaml
eureka:
  instance:
    hostname: localhost
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
```
❌ Uses internal hostname, HTTP only

### After
```yaml
eureka:
  instance:
    hostname: eureka-server-6n60.onrender.com  # Public domain
    prefer-ip-address: false
    secure-port-enabled: true
    non-secure-port-enabled: false
  client:
    service-url:
      defaultZone: https://eureka-server-6n60.onrender.com/eureka/
```
✅ Uses public domain, HTTPS only

---

## Deployment Checklist

- [ ] All `*-prod.yml` files use Eureka URLs with `https://` (not `http://`)
- [ ] `render.yaml` has `EUREKA_SERVER_URL: "https://eureka-server-6n60.onrender.com/eureka/"`
- [ ] All services have `SPRING_PROFILES_ACTIVE: prod`
- [ ] All services have `EUREKA_ENABLED: "true"`
- [ ] Build: `mvn clean package -DskipTests`
- [ ] Commit: `git add -A && git commit -m "Fix Eureka for Render" && git push`
- [ ] Deploy Eureka Server first, wait for health check
- [ ] Deploy other services (they auto-register)
- [ ] Deploy API Gateway last
- [ ] Verify all services appear in Eureka dashboard

---

## Success Indicators

✅ Eureka Server shows "UP" in health check  
✅ All services registered in Eureka (takes 30-60 seconds)  
✅ All services showing as "UP" in dashboard  
✅ API Gateway can route requests  
✅ No "Cannot execute request" errors in logs  
✅ Services auto-discover each other  

---

## Remember

- **HTTPS Required**: Render enforces HTTPS
- **Public Domain**: Use your actual Render domain, not internal hostname
- **Wait 30-60 seconds**: Service registration is not instant
- **One Profile**: Use only `prod`, not combined profiles
- **Environment Variables**: Let render.yaml supply the URLs

**Your Eureka fix is ready! 🚀**
