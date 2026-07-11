param(
    [int]$Port = 8090,
    [switch]$SkipPortCheck
)
$JDK_VERSION = "21"
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$PROJECT_DIR = Split-Path -Parent $SCRIPT_DIR
$POM_FILE = Join-Path $PROJECT_DIR "pom.xml"
$MVNW_CMD = Join-Path $PROJECT_DIR "mvnw.cmd"

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
    param([string]$JavaPath)
    try {
        $versionOutput = & $JavaPath -version 2>&1 | Out-String
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
Write-Log "  java-4 - Start Script"
Write-Log "========================================"
Write-Log "Project: $PROJECT_DIR"
Write-Log "Port: $Port"
""

# Find java from JAVA_HOME or PATH
$javaExe = $null
if ($env:JAVA_HOME) {
    $candidate = Join-Path $env:JAVA_HOME "bin\java.exe"
    if (Test-Path $candidate) { $javaExe = $candidate }
}
if (-not $javaExe) {
    try { $javaExe = (Get-Command java -ErrorAction Stop).Source } catch {}
}
if (-not $javaExe) { Write-Log "JDK not found" "ERROR"; Read-Host "Press Enter"; exit 1 }

$jdkVer = Get-JdkVersion -JavaPath $javaExe
if (-not $jdkVer) { Write-Log "Cannot detect JDK version" "ERROR"; Read-Host "Press Enter"; exit 1 }
if ([int]$jdkVer -lt 17) { Write-Log "JDK too old: $jdkVer, need 17+" "ERROR"; Read-Host "Press Enter"; exit 1 }
Write-Log "JDK $jdkVer OK ($javaExe)" "OK"
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

$javaPath = (Get-Command java -ErrorAction SilentlyContinue).Source
if ($javaPath) {
    $jdkDir = Split-Path (Split-Path $javaPath)
    $env:JAVA_HOME = $jdkDir
    Write-Log "JAVA_HOME = $jdkDir" "INFO"
}

& $MVNW_CMD spring-boot:run
