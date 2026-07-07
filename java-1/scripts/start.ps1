# java-1 Campus Library - PowerShell Start Script
# Validates environment (JDK version, project files, port) and launches Spring Boot via Maven Wrapper
param(
    # Application port, default 8089 (matches application.properties)
    [int]$Port = 8089,
    # Skip port-in-use check (for debugging or concurrent startup)
    [switch]$SkipPortCheck
)

# Minimum required JDK version
$JDK_VERSION = "21"
# Script directory
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
# Project root directory (parent of script directory)
$PROJECT_DIR = Split-Path -Parent $SCRIPT_DIR
# pom.xml path, used to verify project integrity
$POM_FILE = Join-Path $PROJECT_DIR "pom.xml"

<#
.SYNOPSIS
    Log message with colored prefix
.DESCRIPTION
    Color and prefix are chosen by level: INFO / OK / WARN / ERROR
#>
function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    # Choose color by level
    $color = switch ($Level) {
        "INFO"  { "Cyan" }
        "OK"    { "Green" }
        "WARN"  { "Yellow" }
        "ERROR" { "Red" }
    }
    # Choose prefix by level
    $prefix = switch ($Level) {
        "INFO"  { "[INFO]" }
        "OK"    { "[OK]" }
        "WARN"  { "[WARN]" }
        "ERROR" { "[ERROR]" }
    }
    Write-Host "$prefix $Message" -ForegroundColor $color
}

<#
.SYNOPSIS
    Get current system JDK major version
.OUTPUTS
    Version string (e.g. "21"), or $null if not found
#>
function Get-JdkVersion {
    try {
        # Locate java command
        $javaCmd = Get-Command java -ErrorAction Stop
        # Run java -version and extract version number from output
        $versionOutput = & $javaCmd.Source -version 2>&1 | Out-String
        if ($versionOutput -match 'version "(\d+)') { return $matches[1] }
    } catch {}
    return $null
}

<#
.SYNOPSIS
    Check if a given port is in use
.OUTPUTS
    $true if port is listening, $false otherwise
#>
function Test-PortInUse {
    param([int]$PortNum)
    try {
        # Query TCP connections in Listen state on the given port
        $conn = Get-NetTCPConnection -LocalPort $PortNum -State Listen -ErrorAction Stop
        return $null -ne $conn
    } catch { return $false }
}

# ===== Startup flow begins =====
Write-Log "========================================"
Write-Log "  java-1 - Campus Library Management"
Write-Log "========================================"
Write-Log "Project: $PROJECT_DIR"
Write-Log "Port: $Port"
""

# Env check 1: JDK installed and version meets requirement
$jdkVer = Get-JdkVersion
if (-not $jdkVer) { Write-Log "JDK not installed" "ERROR"; Read-Host "Press Enter"; exit 1 }
if ([int]$jdkVer -lt [int]$JDK_VERSION) { Write-Log "JDK too old: $jdkVer, need $JDK_VERSION+" "ERROR"; Read-Host "Press Enter"; exit 1 }
Write-Log "JDK $jdkVer OK" "OK"
""

# Env check 2: pom.xml exists
if (-not (Test-Path $POM_FILE)) { Write-Log "pom.xml not found" "ERROR"; Read-Host "Press Enter"; exit 1 }
Write-Log "Project files OK" "OK"
""

# Env check 3: port is available (can be skipped via -SkipPortCheck)
if (-not $SkipPortCheck) {
    if (Test-PortInUse -PortNum $Port) { Write-Log "Port $Port in use" "ERROR"; Read-Host "Press Enter"; exit 1 }
    Write-Log "Port $Port available" "OK"
}
""

# Launch application
Write-Log "========================================"
Write-Log "  Starting at http://localhost:$Port/"
Write-Log "  H2 Console: http://localhost:$Port/h2-console"
Write-Log "========================================"
""

# Switch to project root and start Spring Boot via Maven Wrapper
Set-Location $PROJECT_DIR
& "$PROJECT_DIR\mvnw.cmd" spring-boot:run
