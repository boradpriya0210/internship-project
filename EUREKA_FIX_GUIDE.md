# Eureka Configuration Fix - Render Deployment

## Problem Fixed

**Issue**: "Cannot execute request on any known server"
- Clients trying to connect to `http://eureka-server-6n60:8761/eureka/` (internal Docker hostname)
- This URL only works in Docker Compose network, not on Render
- Render services can't resolve internal container hostnames

**Root Cause**: 
- Configuration was hardcoded for Docker Compose internal networking
- Clients used internal hostname instead of public Render URL
- HTTP used instead of HTTPS (Render requires SSL)

---

## What Was Fixed

### 1. Eureka Server Configuration (application-prod.yml)
```yaml
eureka:
  instance:
    hostname: eureka-server-6n60.onrender.com  # ✅ PUBLIC domain
    prefer-ip-address: false                    # ✅ Use hostname, not IP
    secure-port-enabled: true                   # ✅ HTTPS only
    non-secure-port-enabled: false              # ✅ No HTTP
    home-page-url: https://eureka-server-6n60.onrender.com/
    status-page-url: https://eureka-server-6n60.onrender.com/actuator/info
    health-check-url: https://eureka-server-6n60.onrender.com/actuator/health
```

### 2. All Client Configurations (API Gateway + Services)
```yaml
eureka:
  client:
    service-url:
      defaultZone: https://eureka-server-6n60.onrender.com/eureka/  # ✅ PUBLIC URL + HTTPS
  instance:
    prefer-ip-address: false                    # ✅ Use hostname
    hostname: ${HOSTNAME:}                      # ✅ Let Render set it
    non-secure-port-enabled: false
    secure-port-enabled: true
    secure-port: 443
```

### 3. render.yaml Updates
✅ All services now use: `EUREKA_SERVER_URL: "https://eureka-server-6n60.onrender.com/eureka/"`
✅ Removed broken `fromService` references
✅ Fixed malformed JAVA_TOOL_OPTIONS entries
✅ Increased JAVA memory from 256m to 512m for stability
✅ Added healthCheckInterval: 30

---

## Configuration Changes Explained

### Before (❌ Broken)
```yaml
# Config for Docker Compose
eureka:
  instance:
    hostname: eureka-server        # Internal hostname only
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/  # HTTP + internal
```

### After (✅ Fixed)
```yaml
# Config for Render Cloud
eureka:
  instance:
    hostname: eureka-server-6n60.onrender.com  # Public domain
    prefer-ip-address: false                   # Use hostname
    non-secure-port-enabled: false             # No HTTP
    secure-port-enabled: true                  # HTTPS only
    secure-port: 443                           # Standard HTTPS port
  client:
    service-url:
      defaultZone: https://eureka-server-6n60.onrender.com/eureka/  # HTTPS + public
```

---

## Files Updated

### Configuration Files
1. ✅ `eureka-server/src/main/resources/application-prod.yml`
   - Eureka Server config with public domain
   - HTTPS enabled
   - Proper instance URLs

2. ✅ `api-gateway/src/main/resources/application-prod.yml`
   - Uses HTTPS Eureka URL
   - Proper instance configuration

3. ✅ `user-service/src/main/resources/application-prod.yml`
   - Uses HTTPS Eureka URL
   - Proper instance configuration

4. ✅ `skill-assessment-service/src/main/resources/application-prod.yml`
   - Uses HTTPS Eureka URL
   - Proper instance configuration

5. ✅ `internship-service/src/main/resources/application-prod.yml`
   - Uses HTTPS Eureka URL
   - Proper instance configuration

6. ✅ `recommendation-service/src/main/resources/application-prod.yml`
   - Uses HTTPS Eureka URL
   - Proper instance configuration

7. ✅ `notification-service/src/main/resources/application-prod.yml`
   - Uses HTTPS Eureka URL
   - Proper instance configuration

### Deployment Configuration
8. ✅ `render.yaml` - ALL sections fixed:
   - Eureka Server: Correct domain + ports
   - All Services: HTTPS URLs, fixed malformed entries, increased memory
   - All healthCheckInterval set to 30 seconds

---

## Critical Configuration Points

### HTTPS is REQUIRED
```
❌ WRONG:  http://eureka-server-6n60.onrender.com/eureka/
✅ CORRECT: https://eureka-server-6n60.onrender.com/eureka/
```

Render provides automatic SSL certificates. All connections must use HTTPS.

### Instance Registration
When services register with Eureka, they advertise:
```
hostname: eureka-server-6n60.onrender.com (must be public, resolvable from internet)
secure-port: 443 (standard HTTPS port)
health-check-url: https://eureka-server-6n60.onrender.com/actuator/health
```

This allows:
- Other services to locate and call each other
- API Gateway to route requests
- Eureka Dashboard to link to services

### Environment Variables in render.yaml
```yaml
EUREKA_SERVER_URL: "https://eureka-server-6n60.onrender.com/eureka/"
SPRING_PROFILES_ACTIVE: prod
EUREKA_ENABLED: true
```

These tell Spring to:
1. Use the `application-prod.yml` profile
2. Connect to the public Eureka URL via HTTPS
3. Enable Eureka client registration

---

## Deployment Steps

### Step 1: Verify render.yaml
Check that all services have:
```yaml
SPRING_PROFILES_ACTIVE: prod
EUREKA_ENABLED: true
EUREKA_SERVER_URL: "https://eureka-server-6n60.onrender.com/eureka/"
```

### Step 2: Deploy Eureka Server First
1. Push changes: `git push`
2. Wait for Eureka Server to deploy
3. Check health: `curl https://eureka-server-6n60.onrender.com/actuator/health`
4. Wait for SSL certificate (5-10 minutes first time)

### Step 3: Deploy Other Services
1. User Service, Skill Assessment, etc.
2. They will auto-register with Eureka
3. Give 30-60 seconds for registration to complete

### Step 4: Deploy API Gateway
1. Last step (depends on other services)
2. Should auto-discover and route to services

---

## Verification Checklist

### 1. Eureka Server Running
```bash
curl https://eureka-server-6n60.onrender.com/actuator/health

# Expected:
# {"status":"UP"}
```

### 2. View Registered Services
```bash
curl https://eureka-server-6n60.onrender.com/eureka/apps

# Expected: XML showing all services in <applications>
```

### 3. API Gateway Health
```bash
curl https://api-gateway-YOUR-ID.onrender.com/actuator/health

# Expected:
# {"status":"UP","components":{"eureka":{"status":"UP",...}}}
```

### 4. Check Eureka Dashboard
Open browser: `https://eureka-server-6n60.onrender.com/`

Should show:
- ✅ Instances currently registered with Eureka
- ✅ All services showing as "UP"
- ✅ Instance URLs are HTTPS and public

### 5. Test API Gateway Routing
```bash
curl https://api-gateway-YOUR-ID.onrender.com/api/users/health

# Should route to user-service successfully
```

---

## Troubleshooting

### "Cannot execute request on any known server"
**Checklist**:
1. ✅ Is EUREKA_SERVER_URL using https:// (not http://)?
2. ✅ Is domain correct: eureka-server-6n60.onrender.com?
3. ✅ Is Eureka Server deployed and running?
4. ✅ Did you wait for SSL certificate to generate?
5. ✅ Check service logs for connection errors

**Fix**:
```bash
# Test connectivity
curl -v https://eureka-server-6n60.onrender.com/actuator/health

# Should see HTTP/2 200 and {"status":"UP"}
```

### Services Not Appearing in Eureka
**Wait 30-60 seconds**: Registration is not instant
**Check service logs for**:
```
"registered with eureka"
"Instance registered"
```

**If still not registered**:
1. Check EUREKA_ENABLED=true in environment
2. Check EUREKA_SERVER_URL in environment
3. View service logs for connection errors
4. Check health endpoint: `/actuator/health`

### SSL Certificate Issues
**If getting certificate errors**:
1. Render auto-generates SSL certs (takes ~5-10 min)
2. First deployment may show `untrusted_cert`
3. Re-deploy service or wait ~10 minutes

**Temporary workaround for testing**:
```bash
curl -k https://eureka-server-6n60.onrender.com/  # -k ignores cert errors
```

### Port/Network Issues
**If getting connection refused**:
1. Verify PORT env var is set (8761 for Eureka)
2. Verify healthCheckPath is correct: `/actuator/health`
3. Check Render service logs
4. Try restarting service from Render dashboard

---

## Configuration Files Summary

### Eureka Server
- **File**: `eureka-server/src/main/resources/application-prod.yml`
- **Key Settings**:
  - `hostname: eureka-server-6n60.onrender.com` (PUBLIC)
  - `secure-port-enabled: true`
  - `non-secure-port-enabled: false`
  - `home-page-url: https://...`

### Eureka Clients (All Services)
- **Files**: Each service's `application-prod.yml`
- **Key Settings**:
  - `defaultZone: https://eureka-server-6n60.onrender.com/eureka/` (HTTPS!)
  - `prefer-ip-address: false`
  - `hostname: ${HOSTNAME:}`
  - `secure-port-enabled: true`

### Render Deployment
- **File**: `render.yaml`
- **Key Settings**:
  - `EUREKA_ENABLED: true` (all services)
  - `EUREKA_SERVER_URL: https://eureka-server-6n60.onrender.com/eureka/` (all services)
  - `SPRING_PROFILES_ACTIVE: prod` (all services)
  - `healthCheckPath: /actuator/health` (all services)

---

## Common Mistakes to Avoid

❌ **DON'T** use internal hostname:
```yaml
EUREKA_SERVER_URL: http://eureka-server:8761/eureka/  # Won't work on Render!
```

❌ **DON'T** use HTTP:
```yaml
EUREKA_SERVER_URL: http://eureka-server-6n60.onrender.com/eureka/  # Render requires HTTPS
```

❌ **DON'T** use placeholder domain:
```yaml
EUREKA_HOSTNAME: eureka-server.onrender.com  # Should be YOUR actual domain
```

❌ **DON'T** mix profiles:
```yaml
SPRING_PROFILES_ACTIVE: docker,prod  # Use only ONE: prod
```

✅ **DO** use public HTTPS URL:
```yaml
EUREKA_SERVER_URL: https://eureka-server-6n60.onrender.com/eureka/  # Correct!
```

✅ **DO** wait for registration:
```
Service register → 30-60 seconds → appears in Eureka
```

✅ **DO** check all three places:
1. Application config file (application-prod.yml)
2. Environment variable (render.yaml)
3. Eureka dashboard (verify registration)

---

## Next Steps

1. ✅ Review all `*-prod.yml` files - check Eureka URLs use HTTPS
2. ✅ Verify render.yaml - check all EUREKA_SERVER_URLs are correct
3. ✅ Build: `mvn clean package -DskipTests`
4. ✅ Push: `git add -A && git commit -m "Fix Eureka config for Render" && git push`
5. ✅ Monitor: Watch Render Dashboard for deployments
6. ✅ Verify: Run checklist above after all services deployed

---

## Reference: Eureka URL Format

### Local Development
```yaml
EUREKA_SERVER_URL: http://localhost:8761/eureka/
```

### Docker Compose
```yaml
EUREKA_SERVER_URL: http://eureka-server:8761/eureka/  # Service name from docker-compose
```

### Render Cloud (YOUR SETUP)
```yaml
EUREKA_SERVER_URL: https://eureka-server-6n60.onrender.com/eureka/  # Public + HTTPS
```

The `application-prod.yml` files automatically use the environment variable!

---

**Your Eureka configuration is now ready for production on Render! 🚀**
