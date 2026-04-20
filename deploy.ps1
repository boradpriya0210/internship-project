# Build all microservices and start with Docker Compose

Write-Host "🔨 Building all microservices..." -ForegroundColor Cyan

$services = @("eureka-server", "api-gateway", "user-service", "internship-service", "notification-service", "recommendation-service", "skill-assessment-service")

foreach ($service in $services) {
    Write-Host "📦 Building $service..." -ForegroundColor Yellow
    Push-Location $service
    mvn clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error building $service" -ForegroundColor Red
        Pop-Location
        exit $LASTEXITCODE
    }
    Pop-Location
}

Write-Host "🚀 Starting Docker containers..." -ForegroundColor Green
docker-compose up --build -d

Write-Host "✅ System is starting up!" -ForegroundColor Green
Write-Host "   - API Gateway: http://localhost:8080"
Write-Host "   - Eureka Dashboard: http://localhost:8761"
