# Eureka Server Configuration for Render Cloud Deployment

## Overview
This configuration properly sets up Eureka Server and Eureka Client for Render cloud environment.

## Key Points for Render Deployment

### 1. Public vs Private URLs
- **Eureka Server exposes**: https://eureka-server-6n60.onrender.com
- **Clients connect to**: https://eureka-server-6n60.onrender.com/eureka/
- **HTTPS is REQUIRED** (Render provides SSL automatically)
- **NOT http://** (internal Docker naming)

### 2. Instance Registration
When a service registers with Eureka, it provides:
- `hostname`: Public domain (eureka-server-6n60.onrender.com)
- `home-page-url`: https://eureka-server-6n60.onrender.com
- `health-check-url`: https://eureka-server-6n60.onrender.com/actuator/health
- `status-page-url`: https://eureka-server-6n60.onrender.com/actuator/info

These URLs are used by:
- Other services to locate instances
- API Gateway to route requests
- Eureka Dashboard to link to services

### 3. Configuration Files

#### Eureka Server (application-prod.yml)
```yaml
eureka:
  instance:
    hostname: eureka-server-6n60.onrender.com  # PUBLIC domain
    prefer-ip-address: false                    # Use hostname
    secure-port-enabled: true                   # HTTPS only
    non-secure-port-enabled: false              # No HTTP
    secure-port: 443                            # HTTPS port
    home-page-url: https://eureka-server-6n60.onrender.com/
    status-page-url: https://eureka-server-6n60.onrender.com/actuator/info
    health-check-url: https://eureka-server-6n60.onrender.com/actuator/health
  client:
    register-with-eureka: false  # Eureka doesn't register itself
    fetch-registry: false
```

#### Eureka Clients (Services - application-prod.yml)
```yaml
eureka:
  client:
    enabled: true
    service-url:
      defaultZone: https://eureka-server-6n60.onrender.com/eureka/  # HTTPS!
    fetch-registry: true        # Download registry
    register-with-eureka: true  # Register self
    registry-fetch-interval-seconds: 30
  instance:
    prefer-ip-address: false    # Use hostname
    instance-id: ${spring.application.name}:${HOSTNAME:localhost}:${server.port}
    hostname: ${HOSTNAME:}      # Let Render set hostname
    home-page-url: https://${HOSTNAME}:${server.port}/
    health-check-url: https://${HOSTNAME}:${server.port}/actuator/health
    health-check-url-path: /actuator/health
    non-secure-port-enabled: false
    secure-port-enabled: true
    secure-port: 443
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

## Environment Variables Required

### Eureka Server
```
SPRING_PROFILES_ACTIVE=prod
PORT=8761
EUREKA_HOSTNAME=eureka-server-6n60.onrender.com  # Your actual domain
JAVA_TOOL_OPTIONS=-Xmx512m -Xss512k -XX:+UseG1GC
```

### Services (API Gateway, User Service, etc.)
```
SPRING_PROFILES_ACTIVE=prod
PORT=8080  (or 8081, 8082, etc.)
EUREKA_ENABLED=true
EUREKA_SERVER_URL=https://eureka-server-6n60.onrender.com/eureka/
JAVA_TOOL_OPTIONS=-Xmx512m -Xss512k -XX:+UseG1GC
```

## Render Configuration (render.yaml)

```yaml
services:
  # Eureka Server
  - type: web
    name: eureka-server
    env: docker
    healthCheckPath: /actuator/health
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: PORT
        value: "8761"
      - key: EUREKA_HOSTNAME
        value: eureka-server-6n60.onrender.com
      - key: JAVA_TOOL_OPTIONS
        value: "-Xmx512m -Xss512k -XX:+UseG1GC"

  # API Gateway (Example)
  - type: web
    name: api-gateway
    env: docker
    healthCheckPath: /actuator/health
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: PORT
        value: "8080"
      - key: EUREKA_ENABLED
        value: "true"
      - key: EUREKA_SERVER_URL
        value: https://eureka-server-6n60.onrender.com/eureka/
      - key: JAVA_TOOL_OPTIONS
        value: "-Xmx512m -Xss512k -XX:+UseG1GC"

  # Services (User, Skill Assessment, etc.)
  - type: web
    name: user-service
    env: docker
    healthCheckPath: /actuator/health
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: PORT
        value: "8081"
      - key: EUREKA_ENABLED
        value: "true"
      - key: EUREKA_SERVER_URL
        value: https://eureka-server-6n60.onrender.com/eureka/
      - key: JAVA_TOOL_OPTIONS
        value: "-Xmx512m -Xss512k -XX:+UseG1GC"
```

## Common Mistakes to Avoid

❌ **WRONG** - Using internal Docker hostname:
```yaml
EUREKA_SERVER_URL: http://eureka-server:8761/eureka/
```
→ This only works in Docker Compose network

❌ **WRONG** - Using http:// on Render:
```yaml
EUREKA_SERVER_URL: http://eureka-server-6n60.onrender.com/eureka/
```
→ Render requires HTTPS

❌ **WRONG** - Using placeholder domain:
```yaml
EUREKA_HOSTNAME: eureka-server.onrender.com
```
→ Should be your actual domain: eureka-server-6n60.onrender.com

✅ **CORRECT** - Public HTTPS URL:
```yaml
EUREKA_SERVER_URL: https://eureka-server-6n60.onrender.com/eureka/
```

## Troubleshooting

### "Cannot execute request on any known server"
**Cause**: Clients can't reach Eureka Server  
**Solution**: 
1. Check EUREKA_SERVER_URL uses https:// (not http://)
2. Check domain is correct (eureka-server-6n60.onrender.com)
3. Verify Eureka Server is deployed and running
4. Check health: https://eureka-server-6n60.onrender.com/actuator/health

### Services not appearing in Eureka
**Cause**: Registration failed or delayed  
**Solution**:
1. Wait 30-60 seconds (registration takes time)
2. Check service logs for "registered with eureka"
3. Verify EUREKA_SERVER_URL in service environment
4. Verify EUREKA_ENABLED=true

### SSL/Certificate errors
**Cause**: Render HTTPS not working  
**Solution**:
1. Verify HTTPS URL (not HTTP)
2. Wait for Render to generate certificate (takes ~5 min after deploy)
3. Check Render dashboard: service should show green checkmark

### Eureka Dashboard not loading
**Cause**: Network/certificate issues  
**Solution**:
1. Try: curl -k https://eureka-server-6n60.onrender.com/
2. Check service logs for errors
3. Verify health endpoint: /actuator/health
4. Restart service from Render dashboard

## Verification Steps

### 1. Check Eureka Server is running
```bash
curl https://eureka-server-6n60.onrender.com/actuator/health
# Should return: {"status":"UP"}
```

### 2. Check services are registered
```bash
curl https://eureka-server-6n60.onrender.com/eureka/apps
# Should show XML with all registered services
```

### 3. Check API Gateway can route
```bash
curl https://api-gateway-YOUR-ID.onrender.com/api/users/health
# Should route to user service successfully
```

### 4. View Eureka Dashboard
```
https://eureka-server-6n60.onrender.com/
# Should show all services marked as UP
```

## Important Notes

1. **HTTPS Required**: Render provides automatic SSL certificates
2. **Port 443**: All connections use standard HTTPS port
3. **Instance ID**: Should include service name + port
4. **Health Checks**: Eureka periodically checks service health
5. **Registration Time**: 30-60 seconds for services to appear
6. **Lease Renewal**: Services send heartbeat every 10 seconds

## Migration from Docker Compose to Render

### Docker Compose (LOCAL)
```yaml
EUREKA_SERVER_URL: http://eureka-server:8761/eureka/  # Internal hostname
# Uses docker-compose network resolution
```

### Render (CLOUD)
```yaml
EUREKA_SERVER_URL: https://eureka-server-6n60.onrender.com/eureka/  # Public URL
# Uses internet DNS resolution
```

The configuration files handle this with `SPRING_PROFILES_ACTIVE`:
- `application.yml` → Local development (localhost)
- `application-docker.yml` → Docker Compose (internal)
- `application-prod.yml` → Render Cloud (public HTTPS)

## Files to Update

1. **Eureka Server**: `eureka-server/src/main/resources/application-prod.yml`
2. **API Gateway**: `api-gateway/src/main/resources/application-prod.yml`
3. **Each Service**: `{service}/src/main/resources/application-prod.yml`
4. **Render Config**: `render.yaml`

All should use:
```yaml
eureka:
  client:
    service-url:
      defaultZone: https://eureka-server-6n60.onrender.com/eureka/
```
