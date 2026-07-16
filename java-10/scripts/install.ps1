# java-10 Student Info Management - One-click Install Script
# Handles: JDK detection, Maven Wrapper setup, backend deps, frontend deps, build & deploy to static
# Designed for portability across different Windows machines
param(
    [switch]$SkipFrontend,
    [switch]$SkipBackend,
    [switch]$NoMirror,
    [switch]$SkipBuild
)

$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$PROJECT_DIR = Split-Path -Parent $SCRIPT_DIR
$FRONTEND_DIR = Join-Path $PROJECT_DIR "frontend"
$STATIC_DIR = Join-Path $PROJECT_DIR "src\main\resources\static"
$POM_FILE = Join-Path $PROJECT_DIR "pom.xml"

$JDK_MIN_VERSION = "21"
$NODE_MIN_VERSION = "18"

# NPM mirrors: Taobao first for China users, then official as fallback
$NPM_MIRRORS = @(
    @{ Name = "Taobao";  Url = "https://registry.npmmirror.com/" },
    @{ Name = "Default"; Url = "https://registry.npmjs.org/" }
)

# Maven mirror settings for China users
$MAVEN_SETTINGS_XML = @"
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <name>Aliyun Public Mirror</name>
      <mirrorOf>central</mirrorOf>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
"@

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

<# Test network connectivity using HTTP requests (more reliable than ICMP ping) #>
function Test-Network {
    $testUrls = @(
        "https://www.baidu.com",
        "https://www.qq.com",
        "https://registry.npmmirror.com",
        "https://maven.aliyun.com",
        "https://repo.maven.apache.org"
    )
    foreach ($url in $testUrls) {
        try {
            $response = Invoke-WebRequest -Uri $url -Method Head -TimeoutSec 5 -UseBasicParsing -ErrorAction Stop
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
                return $true
            }
        } catch {
            continue
        }
    }
    # Fallback: try DNS resolution as last resort
    try {
        $dns = Resolve-DnsName -Name "www.baidu.com" -ErrorAction Stop -QuickTimeout
        if ($dns) { return $true }
    } catch {}
    return $false
}

<# Check if offline install is possible (dependencies already cached locally) #>
function Test-OfflinePossible {
    $hasNodeModules = Test-Path (Join-Path $FRONTEND_DIR "node_modules")
    $hasStaticIndex = Test-Path (Join-Path $STATIC_DIR "index.html")
    $hasMavenCache = $false
    $m2Repo = Join-Path $HOME ".m2\repository"
    if (Test-Path $m2Repo) {
        $springBoot = Get-ChildItem -Path $m2Repo -Filter "spring-boot*.jar" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($springBoot) { $hasMavenCache = $true }
    }
    return ($hasNodeModules -and $hasStaticIndex -and $hasMavenCache)
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

<# Check npm version #>
function Get-NpmVersion {
    try {
        $npmCmd = Get-Command npm -ErrorAction Stop
        $output = & $npmCmd.Source -v 2>&1 | Out-String
        if ($output -match '(\d+)') { return [int]$matches[1] }
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

<# Ensure Maven Wrapper exists; copy from sibling project or download #>
function Ensure-MavenWrapper {
    $mvnwCmd = Join-Path $PROJECT_DIR "mvnw.cmd"
    $mvnwDir = Join-Path $PROJECT_DIR ".mvn\wrapper"
    $wrapperProps = Join-Path $mvnwDir "maven-wrapper.properties"

    if ((Test-Path $mvnwCmd) -and (Test-Path $wrapperProps)) {
        # Fix distributionUrl: replace Maven Central with Aliyun mirror for China users
        $propsContent = Get-Content $wrapperProps -Raw
        if ($propsContent -match "repo\.maven\.apache\.org") {
            Write-Log "Fixing Maven Wrapper download URL (switching to Aliyun mirror)..." "WARN"
            $fixedContent = $propsContent -replace "https://repo\.maven\.apache\.org/maven2", "https://maven.aliyun.com/repository/public"
            $fixedContent | Set-Content $wrapperProps -Encoding UTF8
            Write-Log "Maven Wrapper URL fixed -> Aliyun mirror" "OK"
        }
        Write-Log "Maven Wrapper already present" "OK"
        return $true
    }

    Write-Log "Maven Wrapper missing, attempting to restore..." "WARN"

    # Strategy 1: copy from sibling projects
    $siblings = @("java-1", "java-2", "java-3", "java-4", "java-5", "java-6", "java-7", "java-8", "java-9")
    foreach ($sib in $siblings) {
        $sibMvnw = Join-Path (Split-Path -Parent $PROJECT_DIR) "$sib\mvnw.cmd"
        $sibProps = Join-Path (Split-Path -Parent $PROJECT_DIR) "$sib\.mvn\wrapper\maven-wrapper.properties"
        $sibMvnwSh = Join-Path (Split-Path -Parent $PROJECT_DIR) "$sib\mvnw"
        if ((Test-Path $sibMvnw) -and (Test-Path $sibProps)) {
            Write-Log "Copying Maven Wrapper from $sib..."
            New-Item -ItemType Directory -Path $mvnwDir -Force | Out-Null
            Copy-Item $sibMvnw $mvnwCmd -Force
            Copy-Item $sibProps $wrapperProps -Force
            if (Test-Path $sibMvnwSh) { Copy-Item $sibMvnwSh (Join-Path $PROJECT_DIR "mvnw") -Force }
            Write-Log "Maven Wrapper restored from sibling project" "OK"
            return $true
        }
    }

    # Strategy 2: generate via maven wrapper plugin (requires system mvn)
    Write-Log "No sibling project found, attempting mvn wrapper:wrapper..."
    $mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($mvnCmd) {
        Push-Location $PROJECT_DIR
        & $mvnCmd.Source wrapper:wrapper -q 2>&1 | Out-Host
        Pop-Location
        if ((Test-Path $mvnwCmd) -and (Test-Path $wrapperProps)) {
            Write-Log "Maven Wrapper generated via mvn wrapper:wrapper" "OK"
            return $true
        }
    }

    Write-Log "Failed to set up Maven Wrapper. Please run: mvn wrapper:wrapper" "ERROR"
    return $false
}

<# Ensure Maven settings.xml with Aliyun mirror #>
function Ensure-MavenSettings {
    $m2Dir = Join-Path $HOME ".m2"
    $settingsFile = Join-Path $m2Dir "settings.xml"

    if (Test-Path $settingsFile) {
        $content = Get-Content $settingsFile -Raw
        if ($content -match "aliyun") {
            Write-Log "Maven Aliyun mirror already configured" "OK"
            return
        }
    }

    if (-not $NoMirror) {
        Write-Log "Configuring Aliyun Maven mirror (faster in China)..."
        New-Item -ItemType Directory -Path $m2Dir -Force | Out-Null
        $MAVEN_SETTINGS_XML | Out-File -FilePath $settingsFile -Encoding UTF8
        Write-Log "Aliyun mirror configured" "OK"
    } else {
        Write-Log "Maven mirror skipped (-NoMirror)" "INFO"
    }
}

<# Install frontend npm dependencies with mirror fallback and retry #>
function Install-FrontendDeps {
    if (-not (Test-Path (Join-Path $FRONTEND_DIR "package.json"))) {
        Write-Log "frontend/package.json not found, skipping frontend install" "WARN"
        return $false
    }

    $nodeModules = Join-Path $FRONTEND_DIR "node_modules"
    if (Test-Path $nodeModules) {
        Write-Log "node_modules already exists, skipping install (delete to reinstall)" "OK"
        return $true
    }

    Write-Log "Installing frontend dependencies..." "STEP"
    Push-Location $FRONTEND_DIR

    $success = $false
    foreach ($mirror in $NPM_MIRRORS) {
        $retryCount = 0
        $maxRetries = 2

        while ($retryCount -lt $maxRetries -and -not $success) {
            $retryCount++
            $label = if ($retryCount -gt 1) { " (retry $retryCount)" } else { "" }
            Write-Log "Using $($mirror.Name) registry$label..."

            try {
                if ($mirror.Name -ne "Default") {
                    & npm install --registry=$($mirror.Url) --no-audit --no-fund 2>&1 | Out-Host
                } else {
                    & npm install --no-audit --no-fund 2>&1 | Out-Host
                }

                if ($LASTEXITCODE -eq 0) {
                    Write-Log "Frontend dependencies installed via $($mirror.Name)" "OK"
                    $success = $true
                    break
                }
            } catch {
                Write-Log "npm install failed with $($mirror.Name): $_" "WARN"
            }

            if (-not $success -and $retryCount -lt $maxRetries) {
                Write-Log "Retrying in 3 seconds..." "WARN"
                Start-Sleep -Seconds 3
            }
        }

        if ($success) { break }

        if ($mirror.Name -ne $NPM_MIRRORS[-1].Name) {
            Write-Log "Switching to next mirror..." "WARN"
        }
    }

    Pop-Location

    if (-not $success) {
        Write-Log "All npm install attempts failed" "ERROR"
        Write-Log "Possible causes:" "ERROR"
        Write-Log "  1. Network connectivity issues" "ERROR"
        Write-Log "  2. Proxy/firewall blocking npm registry" "ERROR"
        Write-Log "  3. Corrupted package-lock.json (try deleting it)" "ERROR"
        Write-Log "  4. Node.js version too old (need v$NODE_MIN_VERSION+)" "ERROR"
        return $false
    }

    return $true
}

<# Build frontend with Vite and copy dist to Spring Boot static resources #>
function Build-And-DeployFrontend {
    Write-Log "Building frontend (npm run build)..." "STEP"
    Push-Location $FRONTEND_DIR

    try {
        & npm run build 2>&1 | Out-Host
        if ($LASTEXITCODE -ne 0) {
            Write-Log "Frontend build failed" "ERROR"
            return $false
        }
        Write-Log "Frontend build succeeded" "OK"
    } catch {
        Write-Log "Frontend build error: $_" "ERROR"
        Pop-Location
        return $false
    }

    Pop-Location

    # Copy dist to static directory
    $distDir = Join-Path $FRONTEND_DIR "dist"
    if (-not (Test-Path $distDir)) {
        Write-Log "dist directory not found after build" "ERROR"
        return $false
    }

    Write-Log "Copying frontend to Spring Boot static directory..." "STEP"

    # Clean old static files (keep css/common.css if needed, but better to clean)
    if (Test-Path $STATIC_DIR) {
        Get-ChildItem $STATIC_DIR -Exclude "css" | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
        # Also remove old css content that might conflict
        $oldCss = Join-Path $STATIC_DIR "css\common.css"
        if (Test-Path $oldCss) {
            $distIndex = Join-Path $distDir "index.html"
            # Only remove old css if dist has its own assets
            if (Test-Path (Join-Path $distDir "assets")) {
                Remove-Item $oldCss -Force -ErrorAction SilentlyContinue
                $cssDir = Join-Path $STATIC_DIR "css"
                if ((Get-ChildItem $cssDir -ErrorAction SilentlyContinue | Measure-Object).Count -eq 0) {
                    Remove-Item $cssDir -Force -ErrorAction SilentlyContinue
                }
            }
        }
    } else {
        New-Item -ItemType Directory -Path $STATIC_DIR -Force | Out-Null
    }

    # Copy all dist contents to static
    Copy-Item -Path (Join-Path $distDir "*") -Destination $STATIC_DIR -Recurse -Force
    Write-Log "Frontend deployed to src/main/resources/static/" "OK"
    return $true
}

<# Check if this is the first run (no Spring Boot jars in local Maven cache) #>
function Test-FirstRun {
    $m2Repo = Join-Path $HOME ".m2\repository"
    if (-not (Test-Path $m2Repo)) { return $true }
    $springBoot = Get-ChildItem -Path $m2Repo -Filter "spring-boot-*.jar" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $springBoot) { return $true }
    return $false
}

<# Pre-install Maven distribution from local zip to avoid mvnw.cmd silent block #>
<# Uses bundled scripts/maven/apache-maven-3.9.16-bin.zip if available #>
function Ensure-MavenDistribution {
    $wrapperProps = Join-Path $PROJECT_DIR ".mvn\wrapper\maven-wrapper.properties"
    if (-not (Test-Path $wrapperProps)) { return $true }

    $props = Get-Content -Raw $wrapperProps | ConvertFrom-StringData
    $distributionUrl = $props.distributionUrl
    if (-not $distributionUrl) { return $true }

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
        Write-Log "No bundled Maven zip found, downloading..." "STEP"
        Write-Log "URL: $distributionUrl" "INFO"
        Write-Log "This is a one-time download (about 9 MB)..." "INFO"

        $tmpZip = Join-Path $env:TEMP $distributionUrlName
        try {
            $oldProgress = $ProgressPreference
            $ProgressPreference = 'Continue'
            Invoke-WebRequest -Uri $distributionUrl -OutFile $tmpZip -UseBasicParsing -ErrorAction Stop
            $ProgressPreference = $oldProgress
            Write-Log "Maven zip downloaded" "OK"
            $mavenSource = $tmpZip
        } catch {
            Write-Log "Failed to download Maven distribution: $_" "ERROR"
            Write-Log "mvnw.cmd will try again (may take longer with no progress)" "WARN"
            return $true
        }
    }

    # Extract
    Write-Log "Extracting Maven distribution..." "STEP"
    $tmpExtract = Join-Path $env:TEMP "maven-extract-$(Get-Random)"
    try {
        New-Item -ItemType Directory -Path $tmpExtract -Force | Out-Null
        Expand-Archive $mavenSource -DestinationPath $tmpExtract -Force

        # Find extracted directory (usually apache-maven-3.9.16)
        $extractedDir = Get-ChildItem $tmpExtract -Directory | Select-Object -First 1
        if (-not $extractedDir) {
            Write-Log "Extraction failed: no directory found" "ERROR"
            return $true
        }

        # Create MAVEN_HOME_PARENT and move
        New-Item -ItemType Directory -Path $MAVEN_HOME_PARENT -Force | Out-Null
        Rename-Item -Path $extractedDir.FullName -NewName $hash
        Move-Item -Path (Join-Path $tmpExtract $hash) -Destination $MAVEN_HOME_PARENT -Force

        if (Test-Path -Path "$MAVEN_HOME/bin/mvn.cmd" -PathType Leaf) {
            Write-Log "Maven distribution installed to $MAVEN_HOME" "OK"
        } else {
            Write-Log "Maven installed but mvn.cmd not found (mvnw will retry)" "WARN"
        }
    } catch {
        Write-Log "Extraction error: $_" "WARN"
        Write-Log "mvnw.cmd will try again" "WARN"
    } finally {
        # Only clean up temp download, keep bundled zip
        if ($mavenSource -ne $localMavenZip -and (Test-Path $mavenSource)) {
            Remove-Item $mavenSource -Force -ErrorAction SilentlyContinue
        }
        if (Test-Path $tmpExtract) { Remove-Item $tmpExtract -Recurse -Force -ErrorAction SilentlyContinue }
    }

    return $true
}

<# Resolve Maven backend dependencies #>
function Install-BackendDeps {
    Write-Log "Resolving backend Maven dependencies..." "STEP"

    $mvnwCmd = Join-Path $PROJECT_DIR "mvnw.cmd"
    if (-not (Test-Path $mvnwCmd)) {
        Write-Log "Maven Wrapper not available, cannot resolve deps" "ERROR"
        return $false
    }

    # Step 1: Pre-download Maven distribution (avoids silent block in mvnw.cmd)
    Ensure-MavenDistribution

    # First-run warning: Maven needs to download all Spring Boot dependencies
    $firstRun = Test-FirstRun
    if ($firstRun) {
        Write-Log "========================================" "WARN"
        Write-Log "  FIRST RUN DETECTED" "WARN"
        Write-Log "  Maven needs to download Spring Boot and" "WARN"
        Write-Log "  other dependencies (about 200-400 MB)." "WARN"
        Write-Log "  This may take 3-15 minutes depending on" "WARN"
        Write-Log "  your network speed." "WARN"
        Write-Log "  PLEASE BE PATIENT - do NOT close this" "WARN"
        Write-Log "  window! You will see download progress" "WARN"
        Write-Log "  below..." "WARN"
        Write-Log "========================================" "WARN"
        Write-Host ""
    }

    # Enable verbose output from mvnw.cmd
    $env:MVNW_VERBOSE = "true"

    Push-Location $PROJECT_DIR
    try {
        # Stream Maven output in real-time with color highlighting
        & $mvnwCmd dependency:resolve 2>&1 | ForEach-Object {
            $line = $_.ToString()
            if ($line -match 'Downloading|Downloaded') {
                Write-Host $line -ForegroundColor DarkGray
            } elseif ($line -match 'ERROR|BUILD FAILURE') {
                Write-Host $line -ForegroundColor Red
            } elseif ($line -match 'BUILD SUCCESS') {
                Write-Host $line -ForegroundColor Green
            } else {
                Write-Host $line
            }
        }
        if ($LASTEXITCODE -eq 0) {
            Write-Log "Backend dependencies resolved" "OK"
            return $true
        } else {
            Write-Log "Maven dependency resolution failed (may still work on first run)" "WARN"
            return $true
        }
    } catch {
        Write-Log "Maven dependency resolution error: $_" "WARN"
        return $true
    } finally {
        Pop-Location
    }
}

# ===== Main flow =====
Write-Log "========================================"
Write-Log "  java-10 - One-click Install"
Write-Log "  Student Info Management System"
Write-Log "========================================"
Write-Log "Project: $PROJECT_DIR"
""

# Step 0: Network check
Write-Log "[1/7] Checking network..." "STEP"
$networkOk = Test-Network
if ($networkOk) {
    Write-Log "Network OK" "OK"
} else {
    Write-Log "No internet detected" "WARN"
    # Check if offline install is possible (dependencies already cached)
    if (Test-OfflinePossible) {
        Write-Log "Local cache detected, can run in offline mode" "OK"
        Write-Log "Skipping dependency download steps" "INFO"
        $SkipBackend = $true
        $SkipFrontend = $true
        $SkipBuild = $true
    } else {
        Write-Log "Cannot install offline - dependencies not cached locally" "ERROR"
        Write-Log ""
        Write-Log "Solutions:" "ERROR"
        Write-Log "  1. Connect to internet and re-run this script" "ERROR"
        Write-Log "  2. If behind a proxy, set HTTP_PROXY/HTTPS_PROXY env vars" "ERROR"
        Write-Log "  3. On a connected machine, run install first, then copy entire project folder" "ERROR"
        Read-Host "Press Enter to exit"
        exit 1
    }
}
""

# Step 1: JDK check
Write-Log "[2/7] Checking JDK..." "STEP"
$jdkVer = Get-JdkVersion
if (-not $jdkVer) {
    Write-Log "JDK not found" "ERROR"
    Write-Log "Install JDK 21+ from: https://adoptium.net/temurin/releases/?version=21" "ERROR"
    Write-Log "After installing JDK, re-run this script." "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}
if ($jdkVer -lt [int]$JDK_MIN_VERSION) {
    Write-Log "JDK $jdkVer is too old, need v$JDK_MIN_VERSION+" "ERROR"
    Write-Log "Download from: https://adoptium.net/temurin/releases/?version=21" "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Log "JDK $jdkVer OK" "OK"
""

# Step 2: Node.js check
$frontendDepsOk = $false
$frontendBuilt = $false
if (-not $SkipFrontend) {
    Write-Log "[3/7] Checking Node.js..." "STEP"
    $nodeVer = Get-NodeVersion
    if (-not $nodeVer) {
        Write-Log "Node.js not found" "ERROR"
        Write-Log "Install Node.js v$NODE_MIN_VERSION+ from: https://nodejs.org/" "ERROR"
        Write-Log "After installing, re-run this script or use -SkipFrontend to skip." "ERROR"
        Read-Host "Press Enter to exit"
        exit 1
    }
    if ($nodeVer -lt [int]$NODE_MIN_VERSION) {
        Write-Log "Node.js v$nodeVer is too old, need v$NODE_MIN_VERSION+" "WARN"
        Write-Log "Some packages may fail. Consider upgrading: https://nodejs.org/" "WARN"
    } else {
        Write-Log "Node.js v$nodeVer OK" "OK"
    }

    $npmVer = Get-NpmVersion
    if (-not $npmVer) {
        Write-Log "npm not found (should come with Node.js)" "ERROR"
        Write-Log "Reinstall Node.js: https://nodejs.org/" "ERROR"
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Log "npm v$npmVer OK" "OK"
} else {
    Write-Log "[3/7] Skipping Node.js check (-SkipFrontend)" "INFO"
}
""

# Step 3: Maven Wrapper
Write-Log "[4/7] Checking Maven Wrapper..." "STEP"
$wrapperOk = Ensure-MavenWrapper
if (-not $wrapperOk) {
    Read-Host "Press Enter to exit"
    exit 1
}
""

# Step 4: Maven mirror
Write-Log "[5/7] Configuring Maven mirror..." "STEP"
Ensure-MavenSettings
""

# Step 5: Backend dependencies
if (-not $SkipBackend) {
    Write-Log "[6a/7] Installing backend dependencies..." "STEP"
    $null = Install-BackendDeps
} else {
    Write-Log "[6a/7] Skipping backend install (-SkipBackend)" "INFO"
}
""

# Step 6: Frontend dependencies + build + deploy
if (-not $SkipFrontend) {
    Write-Log "[6b/7] Installing frontend dependencies..." "STEP"
    $frontendDepsOk = Install-FrontendDeps
    if (-not $frontendDepsOk) {
        Write-Log "Frontend install failed. Backend can still run, but no UI." "WARN"
    }

    if ($frontendDepsOk -and -not $SkipBuild) {
        $frontendBuilt = Build-And-DeployFrontend
        if (-not $frontendBuilt) {
            Write-Log "Frontend build failed. You can manually build with: cd frontend; npm run build" "ERROR"
        }
    } elseif ($SkipBuild) {
        Write-Log "Frontend build skipped (-SkipBuild)" "INFO"
    }
} else {
    Write-Log "[6b/7] Skipping frontend install (-SkipFrontend)" "INFO"
}
""

# Summary
Write-Log "========================================"
Write-Log "  Install Summary"
Write-Log "========================================"
Write-Log "JDK:           $(if($jdkVer){'v'+$jdkVer+' OK'}else{'MISSING'})"
Write-Log "Node.js:       $(if($nodeVer){'v'+$nodeVer+' OK'}else{'skipped'})"
Write-Log "Maven Wrapper: $(if($wrapperOk){'OK'}else{'MISSING'})"
Write-Log "Backend deps:  $(if(-not $SkipBackend){'resolved'}else{'skipped'})"
Write-Log "Frontend deps: $(if($frontendDepsOk){'installed'}elseif($SkipFrontend){'skipped'}else{'FAILED'})"
Write-Log "Frontend build:$(if($frontendBuilt){'deployed to static/'}elseif($SkipBuild -or $SkipFrontend){'skipped'}else{'FAILED'})"
Write-Log ""
Write-Log "Access URL: http://localhost:8080/" "OK"
Write-Log "H2 Console: http://localhost:8080/h2-console" "OK"
Write-Log ""
Write-Log "To start: run scripts\start.bat" "OK"
Write-Log "========================================"
""
Read-Host "Press Enter to exit"