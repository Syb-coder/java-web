<#
.SYNOPSIS
    国家网络安全宣传官网（java-6）依赖安装脚本

.DESCRIPTION
    自动检查并安装以下依赖：
      1. JDK 25（Eclipse Temurin 发行版，ZIP 免安装）
      2. Apache Maven 3.9.16（ZIP 解压）
    并配置系统环境变量：JAVA_HOME、MAVEN_HOME、PATH

.PARAMETER InstallDir
    依赖安装根目录，默认 C:\010
    JDK 将安装到 $InstallDir\java
    Maven 将安装到 $InstallDir\apache-maven-3.9.16

.PARAMETER Force
    跳过已安装检查，强制重新安装

.EXAMPLE
    # 默认安装到 C:\010
    .\install-deps.ps1

.EXAMPLE
    # 指定安装目录并强制重装
    .\install-deps.ps1 -InstallDir D:\dev -Force

.NOTES
    需要管理员权限以写入系统级环境变量。
    若无管理员权限，将自动降级为用户级环境变量。
#>
param(
    [string]$InstallDir = "C:\010",
    [switch]$Force
)

# ===== 全局常量 =====
$JDK_VERSION = "25"
$MAVEN_VERSION = "3.9.16"
$JDK_DIR = Join-Path $InstallDir "java"
$MAVEN_DIR = Join-Path $InstallDir "apache-maven-$MAVEN_VERSION"

# Eclipse Temurin JDK 25 最新 GA 版本下载地址（Adoptium API 自动重定向到最新包）
$JDK_DOWNLOAD_URL = "https://api.adoptium.net/v3/binary/latest/$JDK_VERSION/ga/windows/x64/jdk/hotspot/normal/eclipse"
# Apache Maven 3.9.16 归档下载地址
$MAVEN_DOWNLOAD_URL = "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"

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

# 检查当前是否以管理员身份运行
function Test-Administrator {
    $currentPrincipal = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
    return $currentPrincipal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

# 检查 JDK 25 是否已安装并可用
function Test-JdkInstalled {
    try {
        $javaCmd = Get-Command java -ErrorAction Stop
        $versionOutput = & $javaCmd.Source -version 2>&1 | Out-String
        if ($versionOutput -match "version ""$JDK_VERSION(\.|""") {
            return $true
        }
    } catch {
        # java 命令不存在
    }
    return $false
}

# 检查 Maven 是否已安装并可用
function Test-MavenInstalled {
    try {
        $mvnCmd = Get-Command mvn -ErrorAction Stop
        $versionOutput = & $mvnCmd.Source -version 2>&1 | Out-String
        if ($versionOutput -match "Apache Maven $MAVEN_VERSION") {
            return $true
        }
    } catch {
        # mvn 命令不存在
    }
    return $false
}

# 下载文件
function Download-File {
    param([string]$Url, [string]$OutFile)
    Write-Log "下载: $Url"
    Write-Log "保存到: $OutFile"
    try {
        # 启用 TLS 1.2 以兼容部分 HTTPS 下载源
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        $wc = New-Object System.Net.WebClient
        $wc.DownloadFile($Url, $OutFile)
        Write-Log "下载完成" "OK"
        return $true
    } catch {
        Write-Log "下载失败: $($_.Exception.Message)" "ERROR"
        return $false
    }
}

# 设置环境变量（自动选择 Machine 或 User 级别）
function Set-EnvVar {
    param([string]$Name, [string]$Value)
    $scope = if (Test-Administrator) { "Machine" } else { "User" }
    [Environment]::SetEnvironmentVariable($Name, $Value, $scope)
    # 同步到当前进程
    Set-Item -Path "Env:$Name" -Value $Value
    Write-Log "设置环境变量 $Name = $Value （$scope 级别）"
}

# 将路径追加到 PATH 环境变量（避免重复）
function Add-ToPath {
    param([string]$PathToAdd)
    $scope = if (Test-Administrator) { "Machine" } else { "User" }
    $currentPath = [Environment]::GetEnvironmentVariable("PATH", $scope)
    if (($currentPath -split ";") -contains $PathToAdd) {
        Write-Log "PATH 已包含 $PathToAdd，跳过"
        return
    }
    $newPath = if ($currentPath.EndsWith(";")) { "$currentPath$PathToAdd" } else { "$currentPath;$PathToAdd" }
    [Environment]::SetEnvironmentVariable("PATH", $newPath, $scope)
    # 同步到当前进程
    $env:PATH += ";$PathToAdd"
    Write-Log "PATH 追加 $PathToAdd （$scope 级别）"
}

# 安装 JDK 25
function Install-Jdk25 {
    Write-Log "===== 开始安装 JDK 25 (Eclipse Temurin) ====="

    # 创建临时下载目录
    $tempDir = Join-Path $env:TEMP "java6-install"
    if (-not (Test-Path $tempDir)) { New-Item -ItemType Directory -Path $tempDir -Force | Out-Null }
    $zipFile = Join-Path $tempDir "jdk-$JDK_VERSION.zip"

    # 下载
    if (-not (Download-File -Url $JDK_DOWNLOAD_URL -OutFile $zipFile)) {
        Write-Log "JDK 下载失败，请检查网络或手动下载" "ERROR"
        exit 1
    }

    # 解压
    Write-Log "解压 JDK 到 $InstallDir"
    Expand-Archive -Path $zipFile -DestinationPath $InstallDir -Force

    # Adoptium 解压后的目录名形如 jdk-25+36，需重命名为 java
    $extractedDir = Get-ChildItem -Path $InstallDir -Directory | Where-Object { $_.Name -like "jdk-$JDK_VERSION*" } | Select-Object -First 1
    if (-not $extractedDir) {
        Write-Log "未找到解压后的 JDK 目录" "ERROR"
        exit 1
    }
    $extractedPath = $extractedDir.FullName
    if (Test-Path $JDK_DIR) {
        Write-Log "已存在 $JDK_DIR，删除旧的"
        Remove-Item -Path $JDK_DIR -Recurse -Force
    }
    Rename-Item -Path $extractedPath -NewName "java"
    Write-Log "JDK 安装到 $JDK_DIR" "OK"

    # 配置环境变量
    Set-EnvVar -Name "JAVA_HOME" -Value $JDK_DIR
    Add-ToPath -PathToAdd $JDK_DIR

    # 清理临时文件
    Remove-Item -Path $zipFile -Force -ErrorAction SilentlyContinue

    # 验证
    $env:JAVA_HOME = $JDK_DIR
    $env:PATH += ";$JDK_DIR"
    $verify = & "$JDK_DIR\bin\java.exe" -version 2>&1 | Out-String
    if ($verify -match "version ""$JDK_VERSION") {
        Write-Log "JDK 25 安装验证通过" "OK"
        Write-Log $verify
    } else {
        Write-Log "JDK 25 安装后验证失败" "ERROR"
        exit 1
    }
}

# 安装 Maven
function Install-Maven {
    Write-Log "===== 开始安装 Apache Maven $MAVEN_VERSION ====="

    $tempDir = Join-Path $env:TEMP "java6-install"
    if (-not (Test-Path $tempDir)) { New-Item -ItemType Directory -Path $tempDir -Force | Out-Null }
    $zipFile = Join-Path $tempDir "apache-maven-$MAVEN_VERSION-bin.zip"

    if (-not (Download-File -Url $MAVEN_DOWNLOAD_URL -OutFile $zipFile)) {
        Write-Log "Maven 下载失败" "ERROR"
        exit 1
    }

    Write-Log "解压 Maven 到 $InstallDir"
    Expand-Archive -Path $zipFile -DestinationPath $InstallDir -Force
    Write-Log "Maven 安装到 $MAVEN_DIR" "OK"

    # 配置环境变量
    Set-EnvVar -Name "MAVEN_HOME" -Value $MAVEN_DIR
    Add-ToPath -PathToAdd (Join-Path $MAVEN_DIR "bin")

    # 清理临时文件
    Remove-Item -Path $zipFile -Force -ErrorAction SilentlyContinue

    # 验证
    $env:MAVEN_HOME = $MAVEN_DIR
    $env:PATH += ";$(Join-Path $MAVEN_DIR 'bin')"
    $verify = & "$MAVEN_DIR\bin\mvn.cmd" -version 2>&1 | Out-String
    if ($verify -match "Apache Maven $MAVEN_VERSION") {
        Write-Log "Maven $MAVEN_VERSION 安装验证通过" "OK"
        Write-Log $verify
    } else {
        Write-Log "Maven 安装后验证失败" "ERROR"
        exit 1
    }
}

# ===== 主流程 =====

Write-Log "========================================"
Write-Log "  国家网络安全宣传官网 - 依赖安装脚本"
Write-Log "========================================"
Write-Log "安装目录: $InstallDir"
Write-Log "JDK 版本: $JDK_VERSION (Eclipse Temurin)"
Write-Log "Maven 版本: $MAVEN_VERSION"
$isAdmin = Test-Administrator
Write-Log "管理员权限: $(if ($isAdmin) {'是（写入系统级环境变量）'} else {'否（写入用户级环境变量）'})"
Write-Log ""

# 确保安装目录存在
if (-not (Test-Path $InstallDir)) {
    Write-Log "创建安装目录 $InstallDir"
    New-Item -ItemType Directory -Path $InstallDir -Force | Out-Null
}

# 1. 检查 / 安装 JDK 25
if ((Test-JdkInstalled) -and -not $Force) {
    Write-Log "JDK 25 已安装并可用，跳过安装（使用 -Force 强制重装）" "OK"
} else {
    Install-Jdk25
}
Write-Log ""

# 2. 检查 / 安装 Maven
if ((Test-MavenInstalled) -and -not $Force) {
    Write-Log "Maven $MAVEN_VERSION 已安装并可用，跳过安装（使用 -Force 强制重装）" "OK"
} else {
    Install-Maven
}
Write-Log ""

# 3. 最终验证
Write-Log "===== 依赖检查最终结果 ====="
$jdkOk = Test-JdkInstalled
$mvnOk = Test-MavenInstalled
if ($jdkOk) {
    Write-Log "JDK 25:  已安装" "OK"
} else {
    Write-Log "JDK 25:  未安装" "ERROR"
}
if ($mvnOk) {
    Write-Log "Maven:   已安装" "OK"
} else {
    Write-Log "Maven:   未安装" "ERROR"
}

if ($jdkOk -and $mvnOk) {
    Write-Log ""
    Write-Log "所有依赖已就绪，可以运行 start.ps1 启动项目" "OK"
    Write-Log "提示：新开终端窗口后环境变量才会生效" "WARN"
} else {
    Write-Log ""
    Write-Log "部分依赖未安装成功，请检查错误信息" "ERROR"
    exit 1
}
