@echo off
echo =======================================================
echo Starting all microservices for the adaptive assessment platform
echo =======================================================
echo.

echo 1. Starting Eureka Server...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"

echo Waiting for Eureka Server to initialize (15 seconds)...
ping 127.0.0.1 -n 16 > nul

echo.
echo 2. Starting backend microservices...

start "User Service" cmd /k "cd user-service && mvn spring-boot:run"
start "Internship Service" cmd /k "cd internship-service && mvn spring-boot:run"
start "Notification Service" cmd /k "cd notification-service && mvn spring-boot:run"
start "Recommendation Service" cmd /k "cd recommendation-service && mvn spring-boot:run"
start "Skill Assessment Service" cmd /k "cd skill-assessment-service && mvn spring-boot:run"

echo.
echo Waiting for backend services to initialize (15 seconds)...
ping 127.0.0.1 -n 16 > nul

echo.
echo 3. Starting API Gateway...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"

echo.
echo =======================================================
echo All services have been launched in separate terminal windows!
echo If a service fails to start, its terminal window will remain 
echo open so you can see the error message.
echo =======================================================
pause
