# 🚀 Getting Started - Render Deployment

**Read this first!** 5-minute quick start to get your microservices deployed.

---

## 📊 Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     RENDER CLOUD                             │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           API Gateway (Port 8080)                    │  │
│  │  ├─ Routes /api/users/** → User Service             │  │
│  │  ├─ Routes /api/assessments/** → Skill Assessment   │  │
│  │  ├─ Routes /api/internships/** → Internship Service │  │
│  │  ├─ Routes /api/recommendations/** → Recommendation │  │
│  │  └─ Routes /api/notifications/** → Notification     │  │
│  └─────────────────┬────────────────────────────────────┘  │
│                    │                                         │
│     ┌──────────────┼──────────────────┬──────────────┐      │
│     ▼              ▼                  ▼              ▼       │
│  ┌─────────┐ ┌──────────────┐ ┌────────────┐ ┌─────────┐  │
│  │Eureka   │ │User Service  │ │Skill Assess│ │Internsh │  │
│  │Server   │ │(+ Database)  │ │(+ Database)│ │(+ DB)   │  │
│  │Port     │ │Port 8081     │ │Port 8082   │ │Port 8083│  │
│  │8761     │ │              │ │            │ │         │  │
│  └────┬────┘ └──────┬───────┘ └─────┬──────┘ └────┬────┘  │
│       │             │               │              │        │
│       └──────────────┼───────────────┼──────────────┘        │
│              Service Discovery     │                         │
│         (Auto-registration)         │                         │
│                                     ▼                         │
│                         ┌─────────────────────┐              │
│                         │  Aiven MySQL Cloud  │              │
│                         │  (External Database)│              │
│                         └─────────────────────┘              │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## ⚡ Quick Start (5 Steps)

### Step 1: Read the Documentation (2 min)
- **RENDER_QUICKSTART.md** ← Start here!
- Files are in your project root

### Step 2: Build Your Project (3 min)
```bash
mvn clean package -DskipTests
```

### Step 3: Deploy to Render (Follow render.yaml)
- Update Eureka URL after first deployment
- Deploy in order: Eureka → Services → API Gateway

### Step 4: Verify Deployment (2 min)
```bash
curl https://eureka-server-YOUR-ID.onrender.com/actuator/health
curl https://api-gateway-YOUR-ID.onrender.com/actuator/health
```

### Step 5: Test APIs (1 min)
```bash
curl https://api-gateway-YOUR-ID.onrender.com/api/users/health
```

---

## 📚 Documentation Guide

### For Different Use Cases

**I want to deploy RIGHT NOW:**
→ Read: `RENDER_QUICKSTART.md` (5-minute checklist)

**I want to understand what changed:**
→ Read: `CHANGES_SUMMARY.md` (explains all fixes)

**I want detailed explanations:**
→ Read: `DEPLOYMENT_GUIDE.md` (comprehensive guide)

**I want to test locally first:**
→ Read: `LOCAL_TESTING_GUIDE.md` (Docker Compose setup)

**I want to optimize Dockerfiles:**
→ Read: `DOCKERFILE_OPTIMIZATION.md` (image size reduction)

**I want a .env template:**
→ Read: `.env.example` (all variables explained)

---

## 🎯 What Was Fixed

### ❌ Before (Broken on Render)
```
API Gateway tries to connect: http://eureka-server:8761/eureka/
↓
"eureka-server" hostname doesn't exist on Render
↓
"Cannot execute request on any known server"
↓
❌ DEPLOYMENT FAILS
```

### ✅ After (Works on Render)
```
API Gateway uses environment variable: ${EUREKA_SERVER_URL}
↓
render.yaml sets: EUREKA_SERVER_URL=https://eureka-server-xxx.onrender.com/eureka/
↓
Uses actual public URL (works from internet)
↓
✅ DEPLOYMENT SUCCEEDS
✅ Circuit breaker fallback if service down
✅ Optional Eureka (works without it too)
```

---

## 🔧 What Files Were Changed

### Created (NEW)
```
✨ eureka-server/src/main/resources/application-prod.yml
✨ eureka-server/src/main/resources/application-docker.yml
✨ api-gateway/src/main/resources/application-prod.yml
✨ api-gateway/src/main/java/com/internship/apigateway/FallbackController.java
✨ user-service/src/main/resources/application-prod.yml
✨ skill-assessment-service/src/main/resources/application-prod.yml
✨ internship-service/src/main/resources/application-prod.yml
✨ recommendation-service/src/main/resources/application-prod.yml
✨ notification-service/src/main/resources/application-prod.yml

✨ RENDER_QUICKSTART.md (Start here!)
✨ DEPLOYMENT_GUIDE.md
✨ LOCAL_TESTING_GUIDE.md
✨ DOCKERFILE_OPTIMIZATION.md
✨ CHANGES_SUMMARY.md
✨ .env.example
```

### Modified (UPDATED)
```
📝 api-gateway/pom.xml (added Resilience4j for circuit breaker)
📝 render.yaml (updated environment variables)
```

### Unchanged
```
✓ All Java code unchanged
✓ All application.yml files for dev/docker untouched
✓ Database schema unchanged
✓ API endpoints unchanged
```

---

## 💡 Key Concepts

### 1. Configuration Profiles
- **application.yml** → Development (localhost)
- **application-docker.yml** → Docker Compose (internal)
- **application-prod.yml** → Render Cloud (external URLs)

**Activated by:** `SPRING_PROFILES_ACTIVE=prod` environment variable

### 2. Service Discovery (Eureka)
- Services auto-register on startup
- API Gateway discovers services via Eureka
- Can be disabled if needed

### 3. Circuit Breaker (Resilience4j)
- Protects against cascading failures
- Returns 503 instead of timeout
- Auto-recovers when service comes back

### 4. Environment Variables
All URLs/credentials externalized:
```
EUREKA_SERVER_URL=https://...
DATABASE_URL=jdbc:mysql://...
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
```

---

## ✅ Pre-Deployment Checklist

- [ ] Read RENDER_QUICKSTART.md (understanding)
- [ ] Run `mvn clean package -DskipTests` (builds successfully)
- [ ] Update render.yaml with correct ports
- [ ] Have Aiven database credentials ready
- [ ] Know your Render.com login
- [ ] Have git configured (for deployment)

---

## 🚀 Deployment Order (Important!)

**Deploy in this order or services won't register:**

1. **Database** (if not already configured)
2. **Eureka Server** (wait for health check GREEN ✓)
3. **User Service** (should show UP in Eureka)
4. **Skill Assessment** (should show UP in Eureka)
5. **Internship Service** (should show UP in Eureka)
6. **Recommendation Service** (should show UP in Eureka)
7. **Notification Service** (should show UP in Eureka)
8. **API Gateway** (depends on all others)

**⚠️ If you deploy out of order, services won't work!**

---

## 🔍 Verify Each Step

After each deployment, check:

```bash
# 1. After Eureka Server deployed:
curl https://eureka-server-xxx.onrender.com/actuator/health
# Should return: {"status":"UP"}

# 2. After each microservice:
curl https://eureka-server-xxx.onrender.com/eureka/apps
# Should show service in JSON response

# 3. After API Gateway:
curl https://api-gateway-xxx.onrender.com/actuator/health
# Should return: {"status":"UP"}

# 4. Test routing:
curl https://api-gateway-xxx.onrender.com/api/users/health
# Should route to user service
```

---

## 🎓 What You Should Know

### About Environment Variables
- Set in Render Dashboard → Service → Environment
- For passwords, use "Secrets" (masked in logs)
- Changes require service restart
- URL format: `https://` (not `http://`)

### About Eureka
- Services take 30-60 seconds to appear
- Auto-refreshes every 30 seconds
- Dashboard at: `https://eureka-server-xxx.onrender.com/`
- Can be disabled if causing issues

### About Fallback
- If service is down, API Gateway returns 503
- Not a crash, not a timeout
- Client gets clear error message
- Service auto-recovers when back up

### About Databases
- Use external database (Aiven, RDS, etc.)
- Render containers are ephemeral
- SSL required for production
- Connection pooling optimized

---

## 🆘 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "Cannot execute request" | Check EUREKA_SERVER_URL (must start with `https://`) |
| Services not in Eureka | Wait 1-2 minutes + verify `EUREKA_ENABLED=true` |
| 503 Service Unavailable | Check downstream service logs + health endpoint |
| Connection timeout | Verify DATABASE_URL has `?useSSL=true&serverTimezone=UTC` |
| Port conflicts | Ensure each service has unique PORT in Render env |
| Memory errors | Reduce heap size or restart service |

---

## 📞 Need Help?

### Check These First
1. Service logs in Render Dashboard
2. Health endpoints via curl
3. This guide's troubleshooting section

### Review Documentation
- DEPLOYMENT_GUIDE.md (comprehensive)
- RENDER_QUICKSTART.md (quick reference)
- LOCAL_TESTING_GUIDE.md (test locally first)

### External Resources
- Spring Cloud: https://spring.io/projects/spring-cloud
- Render Docs: https://render.com/docs
- Eureka: https://github.com/Netflix/eureka/wiki

---

## 🎯 Success Indicators

After deployment, you should see:

✅ All services show "UP" in Eureka dashboard  
✅ API Gateway health check returns UP  
✅ Services auto-register with Eureka  
✅ API Gateway routes requests correctly  
✅ No circuit breaker OPEN messages  
✅ Database queries work  
✅ Services restart without issues  

---

## 📊 Next Steps

1. **Right Now**: Read RENDER_QUICKSTART.md (5 min)
2. **In 10 min**: Build project with `mvn clean package -DskipTests`
3. **In 20 min**: Deploy Eureka Server to Render
4. **In 30 min**: Deploy all services
5. **In 40 min**: Run verification checklist

**Total time to production: ~45 minutes**

---

## 🎉 Congratulations!

You're about to deploy production-ready microservices! 

The changes made ensure:
- ✅ Services work on cloud (not just locally)
- ✅ Graceful degradation if service fails
- ✅ Easy configuration via environment variables
- ✅ Proper service discovery
- ✅ Production-best practices applied

**Let's get it deployed! → RENDER_QUICKSTART.md**

---

## 📋 Files You'll Need

| File | Purpose | When |
|------|---------|------|
| RENDER_QUICKSTART.md | 5-min deployment guide | Start here! |
| DEPLOYMENT_GUIDE.md | Deep dive explanations | For understanding |
| LOCAL_TESTING_GUIDE.md | Test locally first | Optional |
| DOCKERFILE_OPTIMIZATION.md | Optimize images | After initial deploy |
| .env.example | Environment template | Reference |

---

**Ready? 👉 Start with RENDER_QUICKSTART.md** 🚀
