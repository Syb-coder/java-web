# java-10 Student Info Management - PowerShell Start Script
# Validates environment, checks dependencies, builds frontend if needed, and launches Spring Boot
param(
    [int]$Port = 8080,
    [switch]$SkipPortCheck,
    [switch]$SkipFrontend,
    [switch]$Dev,
    [switch]$Offline
)

$JDK_MIN_VERSION = "21"
$NODE_MIN_VERSION = "18"
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$PROJECT_DIR = Split-Path -Parent $SCRIPT_DIR
$FRONTEND_DIR = Join-Path $PROJECT_DIR "frontend"
$STATIC_DIR = Join-Path $PROJECT_DIR "src\main\resources\static"
$STATIC_INDEX = Join-Path $STATIC_DIR "index.html"
$POM_FILE = Join-Path $PROJECT_DIR "pom.xml"
$MVNW_CMD = Join-Path $PROJECT_DIR "mvnw.cmd"

<# Log with colored prefix #>
function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    $color = switch ($Level) {
        "INFO"  { "Cyan" }
        "OK"    { "Green" }
        "WARN"  { "Yellow" }
        "ERROR" { "Red" }
        "STEP"  { "Magenta" }
    }
    $prefix = switch ($Level) {
        "INFO"  { "[INFO]" }
        "OK"    { "[OK]" }
        "WARN"  { "[WARN]" }
        "ERROR" { "[ERROR]" }
        "STEP"  { "[STEP]" }
    }
    Write-Host "$prefix $Message" -ForegroundColor $color
}

<# Extract JDK major version #>
function Get-JdkVersion {
    try {
        $javaCmd = Get-Command java -ErrorAction Stop
        $output = & $javaCmd.Source -version 2>&1 | Out-String
        if ($output -match 'version "(\d+)') { return [int]$matches[1] }
    } catch {}
    return $null
}

<# Extract Node.js major version #>
function Get-NodeVersion {
    try {
        $nodeCmd = Get-Command node -ErrorAction Stop
        $output = & $nodeCmd.Source -v 2>&1 | Out-String
        if ($output -match 'v(\d+)') { return [int]$matches[1] }
    } catch {}
    return $null
}

<# Check if a TCP port is in use #>
function Test-PortInUse {
    param([int]$PortNum)
    try {
        $conn = Get-NetTCPConnection -LocalPort $PortNum -State Listen -ErrorAction Stop
        return $null -ne $conn
    } catch { return $false }
}

<# Check if Maven Wrapper exists #>
function Test-MavenWrapper {
    $wrapperProps = Join-Path $PROJECT_DIR ".mvn\wrapper\maven-wrapper.properties"
    return ((Test-Path $MVNW_CMD) -and (Test-Path $wrapperProps))
}

<# Pre-install Maven distribution from local zip to avoid mvnw.cmd silent block #>
<# Uses bundled scripts/maven/apache-maven-3.9.16-bin.zip if available #>
function Ensure-MavenDistribution {
    $wrapperProps = Join-Path $PROJECT_DIR ".mvn\wrapper\maven-wrapper.properties"
    if (-not (Test-Path $wrapperProps)) { return $true }

    $props = Get-Content -Raw $wrapperProps | ConvertFrom-StringData
    $distributionUrl = $props.distributionUrl
    if (-not $distributionUrl) { return $true }

    # Fix: replace Maven Central with Aliyun mirror for China users
    if ($distributionUrl -match "repo\.maven\.apache\.org") {
        $distributionUrl = $distributionUrl -replace "https://repo\.maven\.apache\.org/maven2", "https://maven.aliyun.com/repository/public"
        $props.distributionUrl = $distributionUrl
        $props | Out-File $wrapperProps -Encoding UTF8
        Write-Log "Fixed Maven Wrapper URL -> Aliyun mirror" "OK"
    }

    $distributionUrlName = $distributionUrl -replace '^.*/',''
    $distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''

    # Calculate MAVEN_HOME path (must match mvnw.cmd logic exactly)
    $MAVEN_M2_PATH = "$HOME/.m2"
    $MAVEN_WRAPPER_DISTS = "$MAVEN_M2_PATH/wrapper/dists"
    $MAVEN_HOME_PARENT = "$MAVEN_WRAPPER_DISTS/$distributionUrlNameMain"
    $hash = ([System.Security.Cryptography.SHA256]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
    $MAVEN_HOME = "$MAVEN_HOME_PARENT/$hash"

    # Already downloaded and extracted
    if (Test-Path -Path "$MAVEN_HOME/bin/mvn.cmd" -PathType Leaf) {
        return $true
    }

    # Check for bundled local Maven zip (no internet needed!)
    $localMavenZip = Join-Path $SCRIPT_DIR "maven\$distributionUrlName"
    $mavenSource = ""

    if (Test-Path $localMavenZip) {
        Write-Log "Using bundled Maven zip: scripts\maven\$distributionUrlName" "OK"
        $mavenSource = $localMavenZip
    } else {
        # No local zip, try downloading from mirror
        Write-Log "No bundled Maven zip, downloading (about 9 MB)..." "WARN"
        $tmpZip = Join-Path $env:TEMP $distributionUrlName
        try {
            $oldProgress = $ProgressPreference
            $ProgressPreference = 'Continue'
            Invoke-WebRequest -Uri $distributionUrl -OutFile $tmpZip -UseBasicParsing -ErrorAction Stop
            $ProgressPreference = $oldProgress
            $mavenSource = $tmpZip
        } catch {
            Write-Log "Pre-download failed: $_" "ERROR"
            Write-Log "mvnw.cmd will try (may block with no progress)" "WARN"
            return $true
        }
    }

    $tmpExtract = Join-Path $env:TEMP "maven-extract-$(Get-Random)"
    try {
        New-Item -ItemType Directory -Path $tmpExtract -Force | Out-Null
        Expand-Archive $mavenSource -DestinationPath $tmpExtract -Force
        $extractedDir = Get-ChildItem $tmpExtract -Directory | Select-Object -First 1
        if ($extractedDir) {
            New-Item -ItemType Directory -Path $MAVEN_HOME_PARENT -Force | Out-Null
            Rename-Item -Path $extractedDir.FullName -NewName $hash
            Move-Item -Path (Join-Path $tmpExtract $hash) -Destination $MAVEN_HOME_PARENT -Force
            Write-Log "Maven distribution installed" "OK"
        }
    } catch {
        Write-Log "Extraction error: $_" "WARN"
    } finally {
        if ($mavenSource -ne $localMavenZip -and (Test-Path $mavenSource)) {
            Remove-Item $mavenSource -Force -ErrorAction SilentlyContinue
        }
        if (Test-Path $tmpExtract) { Remove-Item $tmpExtract -Recurse -Force -ErrorAction SilentlyContinue }
    }
    return $true
}

<# Test network connectivity using HTTP requests #>
function Test-NetworkHttp {
    $testUrls = @(
        "https://www.baidu.com",
        "https://maven.aliyun.com",
        "https://repo.maven.apache.org"
    )
    foreach ($url in $testUrls) {
        try {
            $response = Invoke-WebRequest -Uri $url -Method Head -TimeoutSec 5 -UseBasicParsing -ErrorAction Stop
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
                return $true
            }
        } catch { continue }
    }
    try {
        $dns = Resolve-DnsName -Name "www.baidu.com" -ErrorAction Stop -QuickTimeout
        if ($dns) { return $true }
    } catch {}
    return $false
}

<# Check if Maven local cache has spring-boot jars (offline run possible) #>
function Test-MavenCache {
    $m2Repo = Join-Path $HOME ".m2\repository"
    if (-not (Test-Path $m2Repo)) { return $false }
    $springBootJar = Get-ChildItem -Path $m2Repo -Filter "spring-boot-*.jar" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
    return ($null -ne $springBootJar)
}

<# Build frontend and copy to static if not already built #>
function Ensure-FrontendBuilt {
    if ($SkipFrontend) {
        Write-Log "Frontend check skipped (-SkipFrontend)" "INFO"
        return $false
    }

    # If static/index.html exists, frontend is already deployed - no need to build
    if (Test-Path $STATIC_INDEX) {
        Write-Log "Frontend already built (static/index.html exists)" "OK"
        return $true
    }

    # No built frontend; check if we can build (need node_modules)
    Write-Log "Frontend not built (static/index.html missing)" "WARN"

    if (-not (Test-Path (Join-Path $FRONTEND_DIR "package.json"))) {
        Write-Log "Frontend source not found. Running in API-only mode." "WARN"
        return $false
    }

    $nodeModules = Join-Path $FRONTEND_DIR "node_modules"
    if (-not (Test-Path $nodeModules)) {
        if ($Offline) {
            Write-Log "node_modules not found and offline mode: cannot build frontend" "ERROR"
            return $false
        }
        Write-Log "node_modules not found. Run scripts\install.bat first to install dependencies." "ERROR"
        $choice = Read-Host "Run install now? (Y/N)"
        if ($choice -eq 'Y' -or $choice -eq 'y') {
            $installScript = Join-Path $SCRIPT_DIR "install.ps1"
            if (Test-Path $installScript) {
                & powershell -ExecutionPolicy Bypass -File $installScript -SkipBackend
            } else {
                Write-Log "install.ps1 not found" "ERROR"
                Read-Host "Press Enter to exit"
                exit 1
            }
        } else {
            Read-Host "Press Enter to exit"
            exit 1
        }
    }

    # Build frontend
    Write-Log "Building frontend..." "STEP"
    Push-Location $FRONTEND_DIR
    try {
        & npm run build 2>&1 | Out-Host
        if ($LASTEXITCODE -ne 0) {
            Write-Log "Frontend build failed" "ERROR"
            Pop-Location
            return $false
        }
    } catch {
        Write-Log "Frontend build error: $_" "ERROR"
        Pop-Location
        return $false
    }
    Pop-Location

    # Copy dist to static
    $distDir = Join-Path $FRONTEND_DIR "dist"
    if (-not (Test-Path $distDir)) {
        Write-Log "dist directory not found after build" "ERROR"
        return $false
    }

    Write-Log "Copying frontend to static directory..." "STEP"
    if (-not (Test-Path $STATIC_DIR)) {
        New-Item -ItemType Directory -Path $STATIC_DIR -Force | Out-Null
    }
    Get-ChildItem $STATIC_DIR -Exclude "css" | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
    Copy-Item -Path (Join-Path $distDir "*") -Destination $STATIC_DIR -Recurse -Force
    Write-Log "Frontend deployed to static/" "OK"
    return (Test-Path $STATIC_INDEX)
}

# ===== Startup flow begins =====
Write-Log "========================================"
Write-Log "  java-10 - Student Info Management"
Write-Log "========================================"
Write-Log "Project: $PROJECT_DIR"
Write-Log "Port: $Port"
Write-Host ""

# --- Env check 1: JDK ---
Write-Log "[1/6] Checking JDK..." "STEP"
$jdkVer = Get-JdkVersion
if (-not $jdkVer) {
    Write-Log "JDK not found" "ERROR"
    Write-Log "Install JDK 21+ from: https://adoptium.net/temurin/releases/?version=21" "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}
if ($jdkVer -lt [int]$JDK_MIN_VERSION) {
    Write-Log "JDK $jdkVer too old, need v$JDK_MIN_VERSION+" "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Log "JDK $jdkVer OK" "OK"
Write-Host ""

# --- Env check 2: Maven Wrapper + pom.xml ---
Write-Log "[2/6] Checking project files..." "STEP"
if (-not (Test-Path $POM_FILE)) {
    Write-Log "pom.xml not found" "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}
if (-not (Test-MavenWrapper)) {
    Write-Log "Maven Wrapper missing" "WARN"
    if (Test-MavenCache) {
        Write-Log "Maven cache found, can run in offline mode" "OK"
        $Offline = $true
    } else {
        Write-Log "Run scripts\install.bat first to set up dependencies." "WARN"
        $choice = Read-Host "Run install now? (Y/N)"
        if ($choice -eq 'Y' -or $choice -eq 'y') {
            $installScript = Join-Path $SCRIPT_DIR "install.ps1"
            if (Test-Path $installScript) {
                & powershell -ExecutionPolicy Bypass -File $installScript
            } else {
                Write-Log "install.ps1 not found" "ERROR"
                Read-Host "Press Enter to exit"
                exit 1
            }
        } else {
            Write-Log "Cannot start without Maven Wrapper" "ERROR"
            Read-Host "Press Enter to exit"
            exit 1
        }
    }
}
Write-Log "Maven Wrapper OK" "OK"
Write-Log "pom.xml OK" "OK"
Write-Host ""

# --- Env check 3: Network (auto-detect offline) ---
Write-Log "[3/6] Checking network..." "STEP"
if (-not $Offline) {
    $networkOk = Test-NetworkHttp
    if ($networkOk) {
        Write-Log "Network OK" "OK"
    } else {
        Write-Log "No internet detected" "WARN"
        if (Test-MavenCache -and (Test-Path $STATIC_INDEX)) {
            Write-Log "Local cache detected, switching to offline mode" "OK"
            $Offline = $true
        } else {
            Write-Log "No network and local cache incomplete. Connect to internet or run install.bat first." "ERROR"
            Read-Host "Press Enter to exit"
            exit 1
        }
    }
} else {
    Write-Log "Offline mode (forced)" "INFO"
}
Write-Host ""

# --- Env check 4: Port ---
Write-Log "[4/6] Checking port..." "STEP"
if (-not $SkipPortCheck) {
    if (Test-PortInUse -PortNum $Port) {
        Write-Log "Port $Port is in use" "ERROR"
        Write-Log "Possible causes:" "ERROR"
        Write-Log "  1. Previous instance still running (check Task Manager for java.exe)" "ERROR"
        Write-Log "  2. Another application using port $Port" "ERROR"
        Write-Log "  3. H2 database file lock (delete .h2.lock file in data/ folder)" "ERROR"
        Write-Log ""
        Write-Log "To skip: scripts\start.bat -SkipPortCheck" "WARN"
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Log "Port $Port available" "OK"
} else {
    Write-Log "Port check skipped" "WARN"
}
Write-Host ""

# --- Env check 5: Node.js ---
Write-Log "[5/6] Checking Node.js..." "STEP"
if (-not $SkipFrontend) {
    $nodeVer = Get-NodeVersion
    if (-not $nodeVer) {
        if (-not (Test-Path $STATIC_INDEX)) {
            Write-Log "Node.js not found and frontend not built" "WARN"
        } else {
            Write-Log "Node.js not found (frontend already built, OK for production)" "OK"
        }
    } elseif ($nodeVer -lt [int]$NODE_MIN_VERSION) {
        Write-Log "Node.js v$nodeVer too old, need v$NODE_MIN_VERSION+" "WARN"
    } else {
        Write-Log "Node.js v$nodeVer OK" "OK"
    }
} else {
    Write-Log "Skipped (-SkipFrontend)" "INFO"
}
Write-Host ""

# --- Env check 6: Frontend built ---
Write-Log "[6/6] Checking frontend..." "STEP"
$frontendReady = Ensure-FrontendBuilt
if (-not $frontendReady -and -not $SkipFrontend) {
    Write-Log "Frontend not available, running in API-only mode" "WARN"
}
Write-Host ""

# --- Dev mode: start Vite dev server ---
$viteJob = $null
if ($Dev -and $frontendReady -and (Get-NodeVersion) -ge [int]$NODE_MIN_VERSION) {
    Write-Log "Starting Vite dev server (dev mode)..." "STEP"
    $viteJob = Start-Job -ScriptBlock {
        param($Dir)
        Set-Location $Dir
        & npm run dev
    } -ArgumentList $FRONTEND_DIR
    Write-Log "Vite dev server starting on http://localhost:5173/" "OK"
    Write-Host ""
}

# --- Launch ---
$frontendUrl = if ($Dev) { "http://localhost:5173/" } else { "http://localhost:$Port/" }
Write-Log "========================================"
Write-Log "  Application starting..."
Write-Log "  URL:        $frontendUrl"
Write-Log "  H2 Console: http://localhost:$Port/h2-console"
if ($Offline) {
    Write-Log "  Mode:       Offline (using local cache)"
}
if ($Dev) {
    Write-Log "  API proxy:  http://localhost:5173/api -> http://localhost:$Port/api"
}
Write-Log "  Login:      admin / admin123"
Write-Log "========================================"
Write-Host ""

# First run hint: Maven may need to download dependencies
if (-not $Offline) {
    $m2Repo = Join-Path $HOME ".m2\repository"
    $hasSpringBoot = $false
    if (Test-Path $m2Repo) {
        $jar = Get-ChildItem -Path $m2Repo -Filter "spring-boot-4*.jar" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($jar) { $hasSpringBoot = $true }
    }
    if (-not $hasSpringBoot) {
        Write-Log "First run detected: Maven needs to download dependencies (may take 3-10 min)" "WARN"
        Write-Log "Please wait... Do NOT close this window!" "WARN"
        Write-Host ""
    }
}

# Pre-install Maven distribution from bundled zip (avoids mvnw.cmd silent block)
Ensure-MavenDistribution

Set-Location $PROJECT_DIR
try {
    if ($Offline) {
        & $MVNW_CMD spring-boot:run -o 2>&1 | ForEach-Object {
            $line = $_.ToString()
            if ($line -match 'Downloading|Downloaded') {
                Write-Host $line -ForegroundColor DarkGray
            } elseif ($line -match 'ERROR|BUILD FAILURE') {
                Write-Host $line -ForegroundColor Red
            } elseif ($line -match 'Started |Tomcat started') {
                Write-Host $line -ForegroundColor Green
            } else {
                Write-Host $line
            }
        }
    } else {
        & $MVNW_CMD spring-boot:run 2>&1 | ForEach-Object {
            $line = $_.ToString()
            if ($line -match 'Downloading|Downloaded') {
                Write-Host $line -ForegroundColor DarkGray
            } elseif ($line -match 'ERROR|BUILD FAILURE') {
                Write-Host $line -ForegroundColor Red
            } elseif ($line -match 'Started |Tomcat started') {
                Write-Host $line -ForegroundColor Green
            } else {
                Write-Host $line
            }
        }
    }
} catch {
    Write-Log "Application exited with error: $_" "ERROR"
} finally {
    if ($viteJob) {
        Write-Log "Stopping Vite dev server..." "INFO"
        Stop-Job $viteJob -ErrorAction SilentlyContinue
        Remove-Job $viteJob -ErrorAction SilentlyContinue
    }
}