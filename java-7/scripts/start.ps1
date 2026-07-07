param(
    [int]$Port = 8086,
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
Write-Log "  java-7 - Start Script"
Write-Log "========================================"
Write-Log "Project: $PROJECT_DIR"
Write-Log "Port: $Port"
""

$jdkVer = Get-JdkVersion
if (-not $jdkVer) { Write-Log "JDK not installed" "ERROR"; Read-Host "Press Enter"; exit 1 }
if ([int]$jdkVer -lt [int]$JDK_VERSION) { Write-Log "JDK too old: $jdkVer, need $JDK_VERSION+" "ERROR"; Read-Host "Press Enter"; exit 1 }
Write-Log "JDK $jdkVer OK" "OK"
""

if (-not (Test-Path $POM_FILE)) { Write-Log "pom.xml not found" "ERROR"; Read-Host "Press Enter"; exit 1 }
Write-Log "Project files OK" "OK"
""

if (-not $SkipPortCheck) {
    if (Test-PortInUse -PortNum $Port) { Write-Log "Port $Port in use" "ERROR"; Read-Host "Press Enter"; exit 1 }
    Write-Log "Port $Port available" "OK"
}
""

Write-Log "========================================"
Write-Log "  Starting at http://localhost:$Port/"
Write-Log "========================================"
""
Set-Location $PROJECT_DIR
& "$PROJECT_DIR\mvnw.cmd" spring-boot:run
