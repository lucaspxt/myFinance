# MyFinance - Development Setup Script

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "   MyFinance Development Setup" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Function to load environment variables from .env file
function Read-EnvFile {
    param([string]$envFilePath)
    
    if (Test-Path $envFilePath) {
        Write-Host "✓ Loading environment variables from .env file" -ForegroundColor Green
        Get-Content $envFilePath | ForEach-Object {
            if ($_ -match '^\s*([^#][^=]*)\s*=\s*(.*)$') {
                $name = $matches[1].Trim()
                $value = $matches[2].Trim()
                # Remove quotes if present
                $value = $value -replace '^["'']|["'']$', ''
                [Environment]::SetEnvironmentVariable($name, $value, "Process")
                Write-Host "  Loaded: $name" -ForegroundColor Gray
            }
        }
        return $true
    }
    return $false
}

# Check and load .env file
Write-Host "[1/6] Checking environment configuration..." -ForegroundColor Yellow
if (-not (Test-Path ".env")) {
    Write-Host "⚠ Warning: .env file not found" -ForegroundColor Yellow
    Write-Host "  Creating .env from .env.example..." -ForegroundColor Gray
    
    if (Test-Path ".env.example") {
        Copy-Item ".env.example" ".env"
        Write-Host "✓ Created .env file" -ForegroundColor Green
        Write-Host ""
        Write-Host "IMPORTANT: Please edit the .env file and configure:" -ForegroundColor Yellow
        Write-Host "  - POSTGRES_PASSWORD: Set a secure database password" -ForegroundColor Yellow
        Write-Host "  - OPENAI_API_KEY: Add your OpenAI API key" -ForegroundColor Yellow
        Write-Host ""
        $continue = Read-Host "Press Enter after editing .env, or 'q' to quit"
        if ($continue -eq 'q') {
            exit 0
        }
    } else {
        Write-Host "✗ Error: .env.example not found" -ForegroundColor Red
        Write-Host "  Please create a .env file with required variables" -ForegroundColor Red
        exit 1
    }
}

# Load environment variables
if (Read-EnvFile ".env") {
    Write-Host "✓ Environment variables loaded" -ForegroundColor Green
} else {
    Write-Host "✗ Failed to load .env file" -ForegroundColor Red
    exit 1
}

# Validate required variables
Write-Host ""
Write-Host "Validating configuration..." -ForegroundColor Yellow
$requiredVars = @("POSTGRES_USER", "POSTGRES_PASSWORD", "POSTGRES_DB", "OPENAI_API_KEY")
$missingVars = @()

foreach ($var in $requiredVars) {
    $value = [Environment]::GetEnvironmentVariable($var, "Process")
    if ([string]::IsNullOrWhiteSpace($value) -or $value -like "*your-*-here*") {
        $missingVars += $var
    }
}

if ($missingVars.Count -gt 0) {
    Write-Host "✗ Missing or invalid configuration:" -ForegroundColor Red
    foreach ($var in $missingVars) {
        Write-Host "  - $var" -ForegroundColor Red
    }
    Write-Host ""
    Write-Host "Please edit the .env file and set these variables" -ForegroundColor Yellow
    exit 1
}
Write-Host "✓ All required variables configured" -ForegroundColor Green

# Check if Docker is running
Write-Host ""
Write-Host "[2/6] Checking Docker..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    Write-Host "✓ Docker is running" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker is not running. Please start Docker Desktop." -ForegroundColor Red
    exit 1
}

# Start database
Write-Host ""
Write-Host "[3/6] Starting PostgreSQL database..." -ForegroundColor Yellow
docker-compose up -d
if ($LASTEXITCODE -eq 0) {
    $dbPort = [Environment]::GetEnvironmentVariable("DB_PORT", "Process")
    if ([string]::IsNullOrWhiteSpace($dbPort)) { $dbPort = "5433" }
    Write-Host "✓ Database started successfully" -ForegroundColor Green
    Write-Host "  - PostgreSQL running on port $dbPort" -ForegroundColor Gray
    Write-Host "  - Database: $([Environment]::GetEnvironmentVariable('POSTGRES_DB', 'Process'))" -ForegroundColor Gray
    Write-Host "  - User: $([Environment]::GetEnvironmentVariable('POSTGRES_USER', 'Process'))" -ForegroundColor Gray
} else {
    Write-Host "✗ Failed to start database" -ForegroundColor Red
    exit 1
}

# Wait for database to be ready
Write-Host ""
Write-Host "Waiting for database to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Check if application-local.yml exists (optional now, as we use env vars)
Write-Host ""
Write-Host "[4/6] Checking backend configuration..." -ForegroundColor Yellow
Write-Host "✓ Using environment variables from .env file" -ForegroundColor Green
Write-Host "  Note: You can still use application-local.yml to override settings" -ForegroundColor Gray

# Check if we should start the API
Write-Host ""
Write-Host "[5/6] Backend API Setup" -ForegroundColor Yellow
$startApi = Read-Host "Do you want to start the backend API now? (y/n)"

if ($startApi -eq 'y' -or $startApi -eq 'Y') {
    Write-Host ""
    Write-Host "Starting backend API in background..." -ForegroundColor Yellow
    
    # Get env vars to pass to the job
    $envVars = @{
        POSTGRES_USER = [Environment]::GetEnvironmentVariable("POSTGRES_USER", "Process")
        POSTGRES_PASSWORD = [Environment]::GetEnvironmentVariable("POSTGRES_PASSWORD", "Process")
        POSTGRES_DB = [Environment]::GetEnvironmentVariable("POSTGRES_DB", "Process")
        DB_HOST = [Environment]::GetEnvironmentVariable("DB_HOST", "Process")
        DB_PORT = [Environment]::GetEnvironmentVariable("DB_PORT", "Process")
        OPENAI_API_KEY = [Environment]::GetEnvironmentVariable("OPENAI_API_KEY", "Process")
        OPENAI_MODEL = [Environment]::GetEnvironmentVariable("OPENAI_MODEL", "Process")
        OPENAI_EMBEDDING_MODEL = [Environment]::GetEnvironmentVariable("OPENAI_EMBEDDING_MODEL", "Process")
        CORS_ALLOWED_ORIGINS = [Environment]::GetEnvironmentVariable("CORS_ALLOWED_ORIGINS", "Process")
    }
    
    $apiJob = Start-Job -ScriptBlock {
        param($workDir, $envs)
        Set-Location $workDir
        Set-Location api
        
        # Set environment variables in the job
        foreach ($key in $envs.Keys) {
            if (-not [string]::IsNullOrWhiteSpace($envs[$key])) {
                [Environment]::SetEnvironmentVariable($key, $envs[$key], "Process")
            }
        }
        
        mvn spring-boot:run -Dspring-boot.run.profiles=local
    } -ArgumentList $PWD, $envVars
    
    Write-Host "✓ Backend API starting (Job ID: $($apiJob.Id))" -ForegroundColor Green
    Write-Host "  Waiting for API to initialize..." -ForegroundColor Gray
    Start-Sleep -Seconds 15
    
    # Check if API is responding
    try {
        $null = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 5 -ErrorAction SilentlyContinue
        Write-Host "✓ Backend API is running on http://localhost:8080" -ForegroundColor Green
    } catch {
        Write-Host "⚠ Backend API may still be starting..." -ForegroundColor Yellow
    }
}

# Check if we should start the Frontend
Write-Host ""
Write-Host "[6/6] Frontend Setup" -ForegroundColor Yellow
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
    Write-Host "  (Environment variables will be loaded from .env)" -ForegroundColor Gray
    Write-Host ""
    Write-Host "To start the frontend manually:" -ForegroundColor White
    Write-Host "  cd web" -ForegroundColor Gray
    Write-Host "  npm start" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Manage environment variables:" -ForegroundColor White
    Write-Host "  Edit the .env file in the root directory" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Access the application at: http://localhost:4200" -ForegroundColor Cyan
    Write-Host ""
}