<#
.SYNOPSIS
    国家网络安全宣传官网（java-6）启动脚本

.DESCRIPTION
    启动前依次检查：
      1. JDK 25 是否安装并可用
      2. Maven 是否安装并可用
      3. 项目目录与 pom.xml 是否存在
      4. 端口 8085 是否被占用
    检查通过后执行 mvn spring-boot:run 启动应用。
    任何一项检查失败将提示原因并退出，不会尝试启动。

.PARAMETER Port
    应用端口，默认 8085（与 application.properties 一致）

.PARAMETER SkipPortCheck
    跳过端口占用检查

.EXAMPLE
    # 默认启动
    .\start.ps1

.EXAMPLE
    # 跳过端口检查
    .\start.ps1 -SkipPortCheck

.NOTES
    若依赖未安装，脚本会提示运行 install-deps.ps1。
#>
param(
    [int]$Port = 8085,
    [switch]$SkipPortCheck
)

# ===== 全局常量 =====
$JDK_VERSION = "25"
$EXPECTED_MAVEN = "3.9.16"
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$PROJECT_DIR = Split-Path -Parent $SCRIPT_DIR
$POM_FILE = Join-Path $PROJECT_DIR "pom.xml"

# ===== 工具函数 =====

# 输出带颜色的日志信息
function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    $color = switch ($Level) {
        "INFO"  { "Cyan" }
        "OK"    { "Green" }
        "WARN"  { "Yellow" }
        "ERROR" { "Red" }
        default { "White" }
    }
    $prefix = switch ($Level) {
        "INFO"  { "[INFO]" }
        "OK"    { "[OK]" }
        "WARN"  { "[WARN]" }
        "ERROR" { "[ERROR]" }
        default { "[*]" }
    }
    Write-Host "$prefix $Message" -ForegroundColor $color
}

# 检查 JDK 是否安装并返回版本字符串
function Get-JdkVersion {
    try {
        $javaCmd = Get-Command java -ErrorAction Stop
        $versionOutput = & $javaCmd.Source -version 2>&1 | Out-String
        if ($versionOutput -match 'version "(\d+)') {
            return $matches[1]
        }
    } catch {
        # java 命令不存在
    }
    return $null
}

# 检查 Maven 是否安装并返回版本字符串
function Get-MavenVersion {
    try {
        $mvnCmd = Get-Command mvn -ErrorAction Stop
        $versionOutput = & $mvnCmd.Source -version 2>&1 | Out-String
        if ($versionOutput -match "Apache Maven (\d+\.\d+\.\d+)") {
            return $matches[1]
        }
    } catch {
        # mvn 命令不存在
    }
    return $null
}

# 检查端口是否被占用
function Test-PortInUse {
    param([int]$PortNum)
    try {
        $conn = Get-NetTCPConnection -LocalPort $PortNum -State Listen -ErrorAction Stop
        return $null -ne $conn
    } catch {
        # 没有连接即未占用
        return $false
    }
}

# ===== 主流程 =====

Write-Log "========================================"
Write-Log "  国家网络安全宣传官网 - 启动脚本"
Write-Log "========================================"
Write-Log "项目目录: $PROJECT_DIR"
Write-Log "预期端口: $Port"
Write-Log ""

# 1. 检查 JDK 25
Write-Log "===== 步骤 1/4：检查 JDK 25 ====="
$jdkVer = Get-JdkVersion
if (-not $jdkVer) {
    Write-Log "JDK 未安装" "ERROR"
    Write-Log "请先运行依赖安装脚本：.\scripts\install-deps.ps1" "WARN"
    exit 1
}
if ($jdkVer -ne $JDK_VERSION) {
    Write-Log "JDK 版本不匹配：当前 $jdkVer，需要 $JDK_VERSION" "ERROR"
    Write-Log "请运行 .\scripts\install-deps.ps1 -Force 重新安装" "WARN"
    exit 1
}
Write-Log "JDK 25 已安装（版本 $jdkVer）" "OK"
Write-Log ""

# 2. 检查 Maven
Write-Log "===== 步骤 2/4：检查 Maven ====="
$mvnVer = Get-MavenVersion
if (-not $mvnVer) {
    Write-Log "Maven 未安装" "ERROR"
    Write-Log "请先运行依赖安装脚本：.\scripts\install-deps.ps1" "WARN"
    exit 1
}
if ($mvnVer -ne $EXPECTED_MAVEN) {
    Write-Log "Maven 版本不匹配：当前 $mvnVer，建议 $EXPECTED_MAVEN" "WARN"
    Write-Log "继续启动（版本差异通常不影响运行）"
} else {
    Write-Log "Maven 已安装（版本 $mvnVer）" "OK"
}
Write-Log ""

# 3. 检查项目目录与 pom.xml
Write-Log "===== 步骤 3/4：检查项目文件 ====="
if (-not (Test-Path $PROJECT_DIR)) {
    Write-Log "项目目录不存在: $PROJECT_DIR" "ERROR"
    exit 1
}
if (-not (Test-Path $POM_FILE)) {
    Write-Log "pom.xml 不存在: $POM_FILE" "ERROR"
    exit 1
}
Write-Log "项目文件检查通过" "OK"
Write-Log ""

# 4. 检查端口占用
Write-Log "===== 步骤 4/4：检查端口 $Port ====="
if (-not $SkipPortCheck) {
    if (Test-PortInUse -PortNum $Port) {
        Write-Log "端口 $Port 已被占用" "ERROR"
        Write-Log "请先停止占用端口的进程，或使用 -SkipPortCheck 跳过检查" "WARN"
        Write-Log "查找占用进程：Get-NetTCPConnection -LocalPort $Port -State Listen | Select-Object OwningProcess"
        exit 1
    }
    Write-Log "端口 $Port 可用" "OK"
} else {
    Write-Log "已跳过端口检查" "WARN"
}
Write-Log ""

# ===== 启动应用 =====
Write-Log "========================================"
Write-Log "  所有检查通过，启动应用..."
Write-Log "  访问地址：http://localhost:$Port/"
Write-Log "  后台管理：http://localhost:$Port/admin.html"
Write-Log "  H2 控制台：http://localhost:$Port/h2-console"
Write-Log "  按 Ctrl+C 停止应用"
Write-Log "========================================"
Write-Log ""

# 切换到项目目录并启动
Set-Location $PROJECT_DIR
& mvn spring-boot:run
