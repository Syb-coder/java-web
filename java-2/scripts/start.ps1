# ===== 脚本参数定义 =====
# Port：服务端口，默认 8088（与其他项目 8085~8087 错开）
# SkipPortCheck：跳过端口占用检查（用于调试或并发启动场景）
param(
    [int]$Port = 8088,
    [switch]$SkipPortCheck
)

# ===== 常量定义 =====
$JDK_VERSION = "21"                                    # 最低要求的 JDK 主版本号
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path   # 当前 ps1 文件所在目录
$PROJECT_DIR = Split-Path -Parent $SCRIPT_DIR          # 上一级即项目根目录
$POM_FILE = Join-Path $PROJECT_DIR "pom.xml"           # Maven 构建文件路径

# ===== 日志输出函数：按级别着色显示 =====
function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    # 根据日志级别选择前景色
    $color = switch ($Level) {
        "INFO"  { "Cyan" }    # 普通信息：青色
        "OK"    { "Green" }   # 成功信息：绿色
        "WARN"  { "Yellow" }  # 警告信息：黄色
        "ERROR" { "Red" }     # 错误信息：红色
    }
    # 根据日志级别选择前缀标签
    $prefix = switch ($Level) {
        "INFO"  { "[INFO]" }
        "OK"    { "[OK]" }
        "WARN"  { "[WARN]" }
        "ERROR" { "[ERROR]" }
    }
    Write-Host "$prefix $Message" -ForegroundColor $color
}

# ===== 获取当前 JDK 主版本号 =====
function Get-JdkVersion {
    try {
        # 查找 java 命令路径
        $javaCmd = Get-Command java -ErrorAction Stop
        # 执行 java -version，输出到 stderr，需 2>&1 合并捕获
        $versionOutput = & $javaCmd.Source -version 2>&1 | Out-String
        # 用正则提取 version "XX" 中的主版本号
        if ($versionOutput -match 'version "(\d+)') { return $matches[1] }
    } catch {}
    return $null
}

# ===== 端口占用检测 =====
function Test-PortInUse {
    param([int]$PortNum)
    try {
        # 查询处于 Listen 状态的 TCP 连接
        $conn = Get-NetTCPConnection -LocalPort $PortNum -State Listen -ErrorAction Stop
        return $null -ne $conn
    } catch { return $false }
}

# ===== 启动流程开始：打印横幅 =====
Write-Log "========================================"
Write-Log "  java-2 - Start Script"
Write-Log "========================================"
Write-Log "Project: $PROJECT_DIR"
Write-Log "Port: $Port"
""

# ===== 环境检查 1：JDK 版本 =====
$jdkVer = Get-JdkVersion
if (-not $jdkVer) { Write-Log "JDK not installed" "ERROR"; Read-Host "Press Enter"; exit 1 }
# JDK 版本过低则退出（如 JDK 17 < 21）
if ([int]$jdkVer -lt [int]$JDK_VERSION) { Write-Log "JDK too old: $jdkVer, need $JDK_VERSION+" "ERROR"; Read-Host "Press Enter"; exit 1 }
Write-Log "JDK $jdkVer OK" "OK"
""

# ===== 环境检查 2：项目文件完整性 =====
if (-not (Test-Path $POM_FILE)) { Write-Log "pom.xml not found" "ERROR"; Read-Host "Press Enter"; exit 1 }
Write-Log "Project files OK" "OK"
""

# ===== 环境检查 3：端口占用（可跳过） =====
if (-not $SkipPortCheck) {
    if (Test-PortInUse -PortNum $Port) { Write-Log "Port $Port in use" "ERROR"; Read-Host "Press Enter"; exit 1 }
    Write-Log "Port $Port available" "OK"
}
""

# ===== 正式启动 =====
Write-Log "========================================"
Write-Log "  Starting at http://localhost:$Port/"
Write-Log "========================================"
""

# 切换到项目根目录，确保 mvnw.cmd 能正确读取 pom.xml
Set-Location $PROJECT_DIR
# 调用 Maven Wrapper 启动 Spring Boot（spring-boot:run 目标）
& "$PROJECT_DIR\mvnw.cmd" spring-boot:run
