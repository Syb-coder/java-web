param(
    [int]$Port = 8089,
    [switch]$SkipPortCheck
)
$JDK_VERSION = "21"
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$PROJECT_DIR = Split-Path -Parent $SCRIPT_DIR
$POM_FILE = Join-Path $PROJECT_DIR "pom.xml"

function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    $color = switch ($Level) {
        "INFO"  { "Cyan" }
        "OK"    { "Green" }
        "WARN"  { "Yellow" }
        "ERROR" { "Red" }
    }
    $prefix = switch ($Level) {
        "INFO"  { "[INFO]" }
        "OK"    { "[OK]" }
        "WARN"  { "[WARN]" }
        "ERROR" { "[ERROR]" }
    }
    Write-Host "$prefix $Message" -ForegroundColor $color
}

function Get-JdkVersion {
    try {
        $javaCmd = Get-Command java -ErrorAction Stop
        $versionOutput = & $javaCmd.Source -version 2>&1 | Out-String
        if ($versionOutput -match 'version "(\d+)') { return $matches[1] }
    } catch {}
    return $null
}

function Test-PortInUse {
    param([int]$PortNum)
    try {
        $conn = Get-NetTCPConnection -LocalPort $PortNum -State Listen -ErrorAction Stop
        return $null -ne $conn
    } catch { return $false }
}

Write-Log "========================================"
Write-Log "  java-11 - ACG Discussion Website"
Write-Log "========================================"
Write-Log "Project: $PROJECT_DIR"
Write-Log "Port: $Port"
""

$jdkVer = Get-JdkVersion
if (-not $jdkVer) {
    Write-Log "JDK not found in PATH." "ERROR"
    Write-Log "Please install JDK 21+ and add it to PATH, then retry." "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}
if ([int]$jdkVer -lt [int]$JDK_VERSION) {
    Write-Log "JDK version too old: $jdkVer, requires $JDK_VERSION+" "ERROR"
    Write-Log "Please install JDK 21+ and add it to PATH, then retry." "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Log "JDK $jdkVer OK" "OK"
""

if (-not (Test-Path $POM_FILE)) {
    Write-Log "pom.xml not found at: $POM_FILE" "ERROR"
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Log "Project files OK" "OK"
""

if (-not $SkipPortCheck) {
    if (Test-PortInUse -PortNum $Port) {
        Write-Log "Port $Port is already in use. Close other app or change port." "ERROR"
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Log "Port $Port available" "OK"
}
""

Write-Log "========================================"
Write-Log "  Starting at http://localhost:$Port/"
Write-Log "  Admin:    http://localhost:$Port/admin.html"
Write-Log "========================================"
""

$javaPath = (Get-Command java -ErrorAction SilentlyContinue).Source
if ($javaPath) {
    $jdkDir = Split-Path (Split-Path $javaPath)
    $env:JAVA_HOME = $jdkDir
    Write-Log "JAVA_HOME = $jdkDir" "INFO"
}

Set-Location $PROJECT_DIR
Write-Log "Running: mvnw.cmd spring-boot:run" "INFO"
Write-Log "First run may download dependencies, please be patient..." "INFO"
""
& "$PROJECT_DIR\mvnw.cmd" spring-boot:run
