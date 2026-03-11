# MyFinance - Development Setup Script

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   MyFinance Development Setup" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
Write-Host "[1/3] Checking Docker..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    Write-Host "✓ Docker is running" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker is not running. Please start Docker Desktop." -ForegroundColor Red
    exit 1
}

# Start database
Write-Host ""
Write-Host "[2/3] Starting PostgreSQL database..." -ForegroundColor Yellow
docker-compose up -d
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Database started successfully" -ForegroundColor Green
    Write-Host "  - PostgreSQL running on port 5433" -ForegroundColor Gray
} else {
    Write-Host "✗ Failed to start database" -ForegroundColor Red
    exit 1
}

# Wait for database to be ready
Write-Host ""
Write-Host "Waiting for database to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Check if we should start the API
Write-Host ""
Write-Host "[3/3] Backend API Setup" -ForegroundColor Yellow
$startApi = Read-Host "Do you want to start the backend API now? (y/n)"

if ($startApi -eq 'y' -or $startApi -eq 'Y') {
    Write-Host ""
    Write-Host "Starting backend API..." -ForegroundColor Yellow
    Set-Location api
    
    # Check for application-local.yml
    if (Test-Path "src\main\resources\application-local.yml") {
        Write-Host "✓ Configuration file found" -ForegroundColor Green
    } else {
        Write-Host "⚠ Warning: application-local.yml not found" -ForegroundColor Yellow
        Write-Host "  You may need to create it with your OpenAI API key" -ForegroundColor Gray
    }
    
    Write-Host ""
    Write-Host "Starting Spring Boot application..." -ForegroundColor Yellow
    mvn spring-boot:run
} else {
    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host "Setup Complete!" -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "To start the backend API:" -ForegroundColor White
    Write-Host "  cd api" -ForegroundColor Gray
    Write-Host "  mvn spring-boot:run" -ForegroundColor Gray
    Write-Host ""
    Write-Host "To start the frontend (once created):" -ForegroundColor White
    Write-Host "  cd web" -ForegroundColor Gray
    Write-Host "  ng serve" -ForegroundColor Gray
    Write-Host ""
    Write-Host "API will be available at: http://localhost:8080" -ForegroundColor White
    Write-Host "Web will be available at: http://localhost:4200" -ForegroundColor White
    Write-Host ""
}
