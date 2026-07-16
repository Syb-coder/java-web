# java-12 Novel Forum - PowerShell Start Script
# Validates environment, checks dependencies, and launches backend (Spring Boot) + frontend (Vite dev server)
#
# Maven strategy (same as java-10):
#   - Uses Maven Wrapper (mvnw.cmd) with bundled local zip for offline install
#   - Falls back to global mvn in PATH if wrapper missing
#   - Frontend runs independently on port 5173 (no static/ deployment)
param(
    # Backend port, default 8090 (matches application.properties)
    [int]$Port = 8090,
    # Frontend Vite dev server port, default 5173 (matches vite.config.ts)
    [int]$FrontendPort = 5173,
    # Skip port-in-use check (for debugging or concurrent startup)
    [switch]$SkipPortCheck,
    # Skip frontend startup (API-only mode)
    [switch]$SkipFrontend,
    # Skip frontend dependency check (use when node_modules already installed)
    [switch]$SkipFrontendCheck,
    # Force offline mode (use local Maven cache only, no network)
    [switch]$Offline
)

# Minimum required JDK version
$JDK_MIN_VERSION = "21"
# Minimum required Node.js version
$NODE_MIN_VERSION = "18"
# Script directory
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
# Project root directory (parent of script directory)
$PROJECT_DIR = Split-Path -Parent $SCRIPT_DIR
# Frontend directory
$FRONTEND_DIR = Join-Path $PROJECT_DIR "frontend"
# pom.xml path, used to verify project integrity
$POM_FILE = Join-Path $PROJECT_DIR "pom.xml"
# Maven Wrapper cmd
$MVNW_CMD = Join-Path $PROJECT_DIR "mvnw.cmd"

<#
.SYNOPSIS
    Log message with colored prefix
#>
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

<#
.SYNOPSIS
    Get current system JDK major version
.OUTPUTS
    Version number as int (e.g. 21), or $null if not found
#>
function Get-JdkVersion {
    try {
        $javaCmd = Get-Command java -ErrorAction Stop
        $output = & $javaCmd.Source -version 2>&1 | Out-String
        if ($output -match 'version "(\d+)') { return [int]$matches[1] }
    } catch {}
    return $null
}

<#
.SYNOPSIS
    Get current system Node.js major version
.OUTPUTS
    Version number as int (e.g. 20), or $null if not found
#>
function Get-NodeVersion {
    try {
        $nodeCmd = Get-Command node -ErrorAction Stop
        $output = & $nodeCmd.Source -v 2>&1 | Out-String
        if ($output -match 'v(\d+)') { return [int]$matches[1] }
    } catch {}
    return $null
}

<#
.SYNOPSIS
    Check if a TCP port is in use
#>
function Test-PortInUse {
    param([int]$PortNum)
    try {
        $conn = Get-NetTCPConnection -LocalPort $PortNum -State Listen -ErrorAction Stop
        return $null -ne $conn
    } catch { return $false }
}

<#
.SYNOPSIS
    Check if Maven Wrapper exists
#>
function Test-MavenWrapper {
    $wrapperProps = Join-Path $PROJECT_DIR ".mvn\wrapper\maven-wrapper.properties"
    return ((Test-Path $MVNW_CMD) -and (Test-Path $wrapperProps))
}

<#
.SYNOPSIS
    Check if Maven local cache has spring-boot jars (offline run possible)
#>
function Test-MavenCache {
    $m2Repo = Join-Path $HOME ".m2\repository"
    if (-not (Test-Path $m2Repo)) { return $false }
    $springBootJar = Get-ChildItem -Path $m2Repo -Filter "spring-boot-*.jar" -Recurse -ErrorAction SilentlyContinue |
                     Select-Object -First 1
    return ($null -ne $springBootJar)
}

<#
.SYNOPSIS
    Pre-install Maven distribution from local zip to avoid mvnw.cmd silent block
.DESCRIPTION
    Uses bundled scripts/maven/apache-maven-*.zip if available, otherwise downloads from mirror.
    Fixes Maven Central URL to Aliyun mirror for China users.
#>
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
        Write-Log "Maven distribution already installed" "OK"
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

<#
.SYNOPSIS
    Locate Maven executable (mvnw.cmd or global mvn)
.DESCRIPTION
    Search order:
      1. Maven Wrapper (mvnw.cmd) in project root - preferred, auto-installs Maven
      2. mvn in PATH
      3. C:\maven\apache-maven-3.9.9\bin\mvn.cmd (known global install)
      4. C:\maven\apache-maven-3.9.*\bin\mvn.cmd (wildcard fallback)
.OUTPUTS
    Full path to mvn.cmd, or $null if not found
#>
function Find-MavenCmd {
    # 1. Maven Wrapper (preferred)
    if (Test-MavenWrapper) {
        return $MVNW_CMD
    }

    # 2. mvn in PATH
    try {
        $mvnCmd = Get-Command mvn -ErrorAction Stop
        return $mvnCmd.Source
    } catch {}

    # 3. Known absolute path
    $knownPath = "C:\maven\apache-maven-3.9.9\bin\mvn.cmd"
    if (Test-Path $knownPath) { return $knownPath }

    # 4. Wildcard fallback in C:\maven\
    if (Test-Path "C:\maven") {
        $candidates = Get-ChildItem -Path "C:\maven" -Directory -Filter "apache-maven-*" -ErrorAction SilentlyContinue |
                      Sort-Object Name -Descending |
                      Select-Object -First 5
        foreach ($dir in $candidates) {
            $candidateMvn = Join-Path $dir.FullName "bin\mvn.cmd"
            if (Test-Path $candidateMvn) { return $candidateMvn }
        }
    }

    return $null
}

# ===== Startup flow begins =====
Write-Log "========================================"
Write-Log "  java-12 - Novel Forum"
Write-Log "========================================"
Write-Log "Project:  $PROJECT_DIR"
Write-Log "Backend:  http://localhost:$Port/        (Spring Boot)"
Write-Log "Frontend: http://localhost:$FrontendPort/  (Vite dev server)"
Write-Host ""

# --- Env check 1: JDK ---
Write-Log "[1/5] Checking JDK..." "STEP"
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

# --- Env check 2: Maven + pom.xml ---
Write-Log "[2/5] Checking Maven..." "STEP"
if (-not (Test-Path $POM_FILE)) {
    Write-Log "pom.xml not found" "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Log "pom.xml OK" "OK"

$mvnCmd = Find-MavenCmd
if (-not $mvnCmd) {
    Write-Log "Maven not found" "ERROR"
    Write-Log "Searched locations:" "ERROR"
    Write-Log "  1. Maven Wrapper (mvnw.cmd in project root)" "ERROR"
    Write-Log "  2. PATH (mvn command)" "ERROR"
    Write-Log "  3. C:\maven\apache-maven-3.9.9\bin\mvn.cmd" "ERROR"
    Write-Log "  4. C:\maven\apache-maven-*\bin\mvn.cmd (wildcard)" "ERROR"
    Write-Log "Please install Maven from: https://maven.apache.org/download.cgi" "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}

# Determine if using Maven Wrapper
$usingWrapper = ($mvnCmd -eq $MVNW_CMD)
if ($usingWrapper) {
    Write-Log "Using Maven Wrapper: mvnw.cmd" "OK"
    # Pre-install Maven distribution from bundled zip (avoids mvnw.cmd silent block)
    Ensure-MavenDistribution
} else {
    Write-Log "Using global Maven: $mvnCmd" "OK"
}

# Hint: if Maven cache is empty, first run will download dependencies
if (-not (Test-MavenCache)) {
    Write-Log "First run detected: Maven needs to download dependencies (may take 3-10 min)" "WARN"
    Write-Log "Please wait... Do NOT close this window!" "WARN"
}
Write-Host ""

# --- Env check 3: Ports (backend + frontend) ---
Write-Log "[3/5] Checking ports..." "STEP"
if (-not $SkipPortCheck) {
    if (Test-PortInUse -PortNum $Port) {
        Write-Log "Backend port $Port is in use" "ERROR"
        Write-Log "Possible causes:" "ERROR"
        Write-Log "  1. Previous instance still running (check Task Manager for java.exe)" "ERROR"
        Write-Log "  2. Another application using port $Port" "ERROR"
        Write-Log "  3. H2 database file lock (delete data/*.lock.db file)" "ERROR"
        Write-Log ""
        Write-Log "To skip: scripts\start.bat -SkipPortCheck" "WARN"
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Log "Backend port $Port available" "OK"

    if (-not $SkipFrontend) {
        if (Test-PortInUse -PortNum $FrontendPort) {
            Write-Log "Frontend port $FrontendPort is in use" "ERROR"
            Write-Log "To skip frontend: scripts\start.bat -SkipFrontend" "WARN"
            Read-Host "Press Enter to exit"
            exit 1
        }
        Write-Log "Frontend port $FrontendPort available" "OK"
    }
} else {
    Write-Log "Port check skipped" "WARN"
}
Write-Host ""

# --- Env check 4: Node.js ---
Write-Log "[4/5] Checking Node.js..." "STEP"
if (-not $SkipFrontend) {
    $nodeVer = Get-NodeVersion
    if (-not $nodeVer) {
        Write-Log "Node.js not found" "ERROR"
        Write-Log "Install Node.js v$NODE_MIN_VERSION+ from: https://nodejs.org/" "ERROR"
        Write-Log "Or run with -SkipFrontend for API-only mode" "WARN"
        Read-Host "Press Enter to exit"
        exit 1
    }
    if ($nodeVer -lt [int]$NODE_MIN_VERSION) {
        Write-Log "Node.js v$nodeVer too old, need v$NODE_MIN_VERSION+" "ERROR"
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Log "Node.js v$nodeVer OK" "OK"
} else {
    Write-Log "Skipped (-SkipFrontend)" "INFO"
}
Write-Host ""

# --- Env check 5: Frontend dependencies ---
Write-Log "[5/5] Checking frontend..." "STEP"
if (-not $SkipFrontend) {
    if (-not (Test-Path (Join-Path $FRONTEND_DIR "package.json"))) {
        Write-Log "Frontend package.json not found at $FRONTEND_DIR" "ERROR"
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Log "package.json OK" "OK"

    if (-not $SkipFrontendCheck) {
        $nodeModules = Join-Path $FRONTEND_DIR "node_modules"
        if (-not (Test-Path $nodeModules)) {
            Write-Log "node_modules not found. Installing dependencies..." "WARN"
            Push-Location $FRONTEND_DIR
            try {
                & npm install 2>&1 | Out-Host
                if ($LASTEXITCODE -ne 0) {
                    Write-Log "npm install failed" "ERROR"
                    Pop-Location
                    Read-Host "Press Enter to exit"
                    exit 1
                }
                Write-Log "Frontend dependencies installed" "OK"
            } catch {
                Write-Log "npm install error: $_" "ERROR"
                Pop-Location
                Read-Host "Press Enter to exit"
                exit 1
            }
            Pop-Location
        } else {
            Write-Log "node_modules OK" "OK"
        }
    } else {
        Write-Log "Frontend check skipped (-SkipFrontendCheck)" "INFO"
    }
} else {
    Write-Log "Skipped (-SkipFrontend)" "INFO"
}
Write-Host ""

# --- Launch summary ---
Write-Log "========================================"
Write-Log "  Application starting..."
Write-Log "  Backend URL:    http://localhost:$Port/"
Write-Log "  H2 Console:     http://localhost:$Port/h2-console"
if (-not $SkipFrontend) {
    Write-Log "  Frontend URL:   http://localhost:$FrontendPort/"
    Write-Log "  API proxy:      http://localhost:$FrontendPort/api -> http://localhost:$Port/api"
}
Write-Log "  Login:          admin / admin123"
Write-Log "========================================"
Write-Host ""

# --- Start frontend (background job) ---
$frontendJob = $null
if (-not $SkipFrontend) {
    Write-Log "Starting Vite dev server..." "STEP"
    $frontendJob = Start-Job -ScriptBlock {
        param($Dir, $Port)
        Set-Location $Dir
        $env:PORT = $Port
        & npm run dev
    } -ArgumentList $FRONTEND_DIR, $FrontendPort

    # Wait briefly for Vite to start
    Start-Sleep -Seconds 2
    if ($frontendJob.State -eq "Failed") {
        Write-Log "Vite dev server failed to start" "ERROR"
        Stop-Job $frontendJob -ErrorAction SilentlyContinue
        Remove-Job $frontendJob -ErrorAction SilentlyContinue
        $frontendJob = $null
    } else {
        Write-Log "Vite dev server starting on http://localhost:$FrontendPort/" "OK"
    }
    Write-Host ""
}

# --- Start backend (foreground) ---
Write-Log "Starting Spring Boot backend..." "STEP"
Set-Location $PROJECT_DIR
try {
    $mavenArgs = @("spring-boot:run")
    if ($Offline) { $mavenArgs += "-o" }
    & $mvnCmd @mavenArgs 2>&1 | ForEach-Object {
        $line = $_.ToString()
        if ($line -match 'Downloading|Downloaded') {
            Write-Host $line -ForegroundColor DarkGray
        } elseif ($line -match 'ERROR|BUILD FAILURE') {
            Write-Host $line -ForegroundColor Red
        } elseif ($line -match 'Started |Tomcat started|Application started') {
            Write-Host $line -ForegroundColor Green
        } else {
            Write-Host $line
        }
    }
} catch {
    Write-Log "Backend exited with error: $_" "ERROR"
} finally {
    # Cleanup: stop frontend job when backend exits
    if ($frontendJob) {
        Write-Log ""
        Write-Log "Stopping Vite dev server..." "INFO"
        Stop-Job $frontendJob -ErrorAction SilentlyContinue
        Remove-Job $frontendJob -ErrorAction SilentlyContinue
    }
}
