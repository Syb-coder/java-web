# java-10 一键安装前后端依赖脚本
# 后端：通过 Maven Wrapper 预下载 Spring Boot 依赖
# 前端：通过 npm install 安装 Vue3/Vite/TypeScript 依赖
param(
    [switch]$SkipFrontend,
    [switch]$SkipBackend
)

$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$PROJECT_DIR = Split-Path -Parent $SCRIPT_DIR
$FRONTEND_DIR = Join-Path $PROJECT_DIR "frontend"
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

Write-Log "========================================"
Write-Log "  java-10 - Dependency Installer"
Write-Log "========================================"
Write-Log "Project: $PROJECT_DIR"
""

# ===== 环境检查 =====
$hasError = $false

# 检查 JDK
$jdkVer = Get-JdkVersion
if (-not $jdkVer) {
    Write-Log "JDK not found" "ERROR"
    $hasError = $true
} elseif ([int]$jdkVer -lt 21) {
    Write-Log "JDK too old: $jdkVer, need 21+" "ERROR"
    $hasError = $true
} else {
    Write-Log "JDK $jdkVer OK" "OK"
}

# 检查 Node.js（前端依赖）
if (-not $SkipFrontend) {
    try {
        $nodeVer = (Get-Command node -ErrorAction Stop).Source
        $nodeVersion = & node -v 2>&1
        Write-Log "Node.js $nodeVersion OK" "OK"
    } catch {
        Write-Log "Node.js not found, frontend dependencies will be skipped" "WARN"
        $SkipFrontend = $true
    }
}

if ($hasError) { Read-Host "Press Enter"; exit 1 }
""

# ===== 后端依赖安装 =====
if (-not $SkipBackend) {
    Write-Log "-------- Backend (Maven) --------"
    if (-not (Test-Path $POM_FILE)) {
        Write-Log "pom.xml not found, skipping backend" "WARN"
    } else {
        Write-Log "Pre-downloading Maven dependencies..."
        Set-Location $PROJECT_DIR
        & "$PROJECT_DIR\mvnw.cmd" dependency:resolve -q
        if ($LASTEXITCODE -eq 0) {
            Write-Log "Backend dependencies installed" "OK"
        } else {
            Write-Log "Backend dependency install failed (may still work on