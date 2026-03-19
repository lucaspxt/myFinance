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

# Check if application-local.yml exists
Write-Host ""
Write-Host "[3/5] Checking backend configuration..." -ForegroundColor Yellow
if (Test-Path "api\src\main\resources\application-local.yml") {
    Write-Host "✓ Configuration file found" -ForegroundColor Green
} else {
    Write-Host "⚠ Warning: application-local.yml not found" -ForegroundColor Yellow
    Write-Host "  Creating from template..." -ForegroundColor Gray
    
    $configContent = @"
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/myfinance
    username: postgres
    password: postgres

langchain4j:
  open-ai:
    chat-model:
      api-key: your-openai-api-key-here
      model-name: gpt-4o-mini
      temperature: 0.7
    embedding-model:
      api-key: your-openai-api-key-here
      model-name: text-embedding-3-small
"@
    
    New-Item -Path "api\src\main\resources\application-local.yml" -ItemType File -Force | Out-Null
    Set-Content -Path "api\src\main\resources\application-local.yml" -Value $configContent
    Write-Host "✓ Configuration file created" -ForegroundColor Green
    Write-Host "  Please edit api/src/main/resources/application-local.yml and add your OpenAI API key" -ForegroundColor Yellow
}

# Check if we should start the API
Write-Host ""
Write-Host "[4/5] Backend API Setup" -ForegroundColor Yellow
$startApi = Read-Host "Do you want to start the backend API now? (y/n)"

if ($startApi -eq 'y' -or $startApi -eq 'Y') {
    Write-Host ""
    Write-Host "Starting backend API in background..." -ForegroundColor Yellow
    
    $apiJob = Start-Job -ScriptBlock {
        Set-Location $using:PWD
        Set-Location api
        mvn spring-boot:run -Dspring-boot.run.profiles=local
    }
    
    Write-Host "✓ Backend API starting (Job ID: $($apiJob.Id))" -ForegroundColor Green
    Write-Host "  Waiting for API to initialize..." -ForegroundColor Gray
    Start-Sleep -Seconds 15
    
    # Check if API is responding
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 5 -ErrorAction SilentlyContinue
        Write-Host "✓ Backend API is running on http://localhost:8080" -ForegroundColor Green
    } catch {
        Write-Host "⚠ Backend API may still be starting..." -ForegroundColor Yellow
    }
}

# Check if we should start the Frontend
Write-Host ""
Write-Host "[5/5] Frontend Setup" -ForegroundColor Yellow
$startFrontend = Read-Host "Do you want to start the frontend now? (y/n)"

if ($startFrontend -eq 'y' -or $startFrontend -eq 'Y') {
    Write-Host ""
    Write-Host "Starting Angular frontend..." -ForegroundColor Yellow
    Set-Location web
    
    # Check if node_modules exists
    if (-not (Test-Path "node_modules")) {
        Write-Host "Installing npm dependencies..." -ForegroundColor Yellow
        npm install
    }
    
    Write-Host "✓ Starting Angular dev server..." -ForegroundColor Green
    npm start
} else {
    Write-Host ""
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host "Setup Complete!" -ForegroundColor Green
    Write-Host "=====================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Services Status:" -ForegroundColor White
    Write-Host "  ✓ Database: Running on port 5433" -ForegroundColor Green
    if ($startApi -eq 'y' -or $startApi -eq 'Y') {
        Write-Host "  ✓ Backend API: Running on http://localhost:8080" -ForegroundColor Green
    } else {
        Write-Host "  ○ Backend API: Not started" -ForegroundColor Gray
    }
    Write-Host "  ○ Frontend: Not started" -ForegroundColor Gray
    Write-Host ""
    Write-Host "To start the backend API manually:" -ForegroundColor White
    Write-Host "  cd api" -ForegroundColor Gray
    Write-Host "  mvn spring-boot:run -Dspring-boot.run.profiles=local" -ForegroundColor Gray
    Write-Host ""
    Write-Host "To start the frontend manually:" -ForegroundColor White
    Write-Host "  cd web" -ForegroundColor Gray
    Write-Host "  npm start" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Access the application at: http://localhost:4200" -ForegroundColor Cyan
    Write-Host ""
}
