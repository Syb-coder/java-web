# ================================================================
# java-5 依赖安装脚本
# 职责：自动安装 JDK 25 和 Maven 3.9+
# 策略：优先 winget，失败回退下载压缩包解压
# 使用：右键“用 PowerShell 运行”或 .\install-deps.ps1
# ================================================================

# ===== 脚本全局配置 =====
$ErrorActionPreference = "Stop"  # 遇错即停，便于排查
$InstallJdkPath   = "C:\010\java"                    # JDK 安装目录（与现有环境一致）
$InstallMavenPath = "C:\maven\apache-maven-3.9.9"    # Maven 安装目录
$MavenVersion     = "3.9.9"                          # Maven 版本
$TempDir          = "$env:TEMP\java5-deps"           # 临时下载目录

# ===== 辅助函数：带颜色的日志输出 =====
function Write-Log {
    param(
        [string]$Message,   # 日志内容
        [string]$Level = "INFO"  # 级别：INFO/WARN/ERROR/SUCCESS
    )
    $color = switch ($Level) {
        "INFO"    { "Cyan" }
        "WARN"    { "Yellow" }
        "ERROR"   { "Red" }
        "SUCCESS" { "Green" }
        default   { "White" }
    }
    $time = Get-Date -Format "HH:mm:ss"
    Write-Host "[$time] [$Level] $Message" -ForegroundColor $color
}

# ===== 辅助函数：检查管理员权限 =====
function Test-Administrator {
    $currentPrincipal = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
    return $currentPrincipal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

# ===== 辅助函数：检查 winget 是否可用 =====
function Test-WingetAvailable {
    $winget = Get-Command winget -ErrorAction SilentlyContinue
    return $null -ne $winget
}

# ===== 辅助函数：检查 JDK 是否已安装且版本为 25 =====
function Get-InstalledJdkVersion {
    # 注意：java -version 输出到 stderr，PowerShell 会把 stderr 当作 ErrorRecord 触发异常
    # 用 cmd /c 包装让 cmd 合并 2>&1，PowerShell 只看到 stdout 字符串
    $output = cmd /c "java -version 2>&1" | Out-String
    # 输出格式如：'openjdk version "25.0.1"' 或 'java version "25.0.1"'
    # 正则用 \D+ 匹配非数字字符，避免单引号字符串内嵌双引号的解析问题
    if ($output -match 'version\D+(\d+)') {
        return $matches[1]
    }
    return $null
}

# ===== 辅助函数：检查 Maven 是否已安装 =====
function Test-MavenInstalled {
    $mvn = Get-Command mvn -ErrorAction SilentlyContinue
    return $null -ne $mvn
}

# ===== 安装 JDK 25 =====
function Install-Jdk25 {
    Write-Log "开始安装 JDK 25..."

    # 步骤 1：检查是否已安装
    $installedVersion = Get-InstalledJdkVersion
    if ($installedVersion -eq "25") {
        Write-Log "JDK 25 已安装，跳过" "SUCCESS"
        return $true
    }
    if ($installedVersion) {
        Write-Log "检测到 JDK $installedVersion，但需要 JDK 25" "WARN"
    }

    # 步骤 2：优先尝试 winget
    if (Test-WingetAvailable) {
        Write-Log "尝试通过 winget 安装 Eclipse Temurin JDK 25..."
        try {
            # winget 包 ID：EclipseAdoptium.Temurin.25.JDK
            & winget install --id EclipseAdoptium.Temurin.25.JDK --accept-package-agreements --accept-source-agreements --silent 2>&1 | Out-Null
            # 刷新当前会话 PATH，让 java 命令立即可用
            $env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "User")
            $installedVersion = Get-InstalledJdkVersion
            if ($installedVersion -eq "25") {
                Write-Log "winget 安装 JDK 25 成功" "SUCCESS"
                return $true
            }
            Write-Log "winget 安装后未检测到 JDK 25，回退到压缩包方式" "WARN"
        } catch {
            Write-Log "winget 安装失败：$_ ，回退到压缩包方式" "WARN"
        }
    } else {
        Write-Log "winget 不可用，使用压缩包方式安装" "WARN"
    }

    # 步骤 3：回退方案 - 下载 Temurin JDK 25 压缩包
    Write-Log "从 Adoptium 下载 Temurin JDK 25 压缩包..."
    if (-not (Test-Path $TempDir)) {
        New-Item -ItemType Directory -Path $TempDir -Force | Out-Null
    }

    # Adoptium API：自动获取 JDK 25 最新 GA 版本
    $apiUrl = "https://api.adoptium.net/v3/binary/latest/25/ga/windows/x64/jdk/hotspot/normal/eclipse"
    $zipPath = "$TempDir\jdk25.zip"

    try {
        # 下载（Invoke-WebRequest 自动跟随重定向）
        Write-Log "下载地址：$apiUrl"
        Invoke-WebRequest -Uri $apiUrl -OutFile $zipPath -UseBasicParsing
        Write-Log "下载完成，开始解压..."
    } catch {
        Write-Log "下载 JDK 失败：$_" "ERROR"
        Write-Log "请手动下载 JDK 25 并解压到 $InstallJdkPath" "ERROR"
        return $false
    }

    # 步骤 4：解压到目标目录
    if (Test-Path $InstallJdkPath) {
        Write-Log "目标目录已存在，先清理：$InstallJdkPath"
        Remove-Item $InstallJdkPath -Recurse -Force
    }
    $parentDir = Split-Path $InstallJdkPath -Parent
    if (-not (Test-Path $parentDir)) {
        New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
    }

    try {
        # 解压到临时目录，再移动到目标路径（避免 zip 内多层目录问题）
        $extractTemp = "$TempDir\jdk-extract"
        if (Test-Path $extractTemp) { Remove-Item $extractTemp -Recurse -Force }
        Expand-Archive -Path $zipPath -DestinationPath $extractTemp -Force
        # zip 内通常有一层 jdk-25.x.x 目录，取其内容移动
        $innerDir = Get-ChildItem $extractTemp -Directory | Select-Object -First 1
        if ($innerDir) {
            Move-Item $innerDir.FullName $InstallJdkPath -Force
        } else {
            Move-Item $extractTemp $InstallJdkPath -Force
        }
        Write-Log "JDK 解压完成：$InstallJdkPath" "SUCCESS"
    } catch {
        Write-Log "解压 JDK 失败：$_" "ERROR"
        return $false
    }

    # 步骤 5：设置环境变量
    Write-Log "设置环境变量 JAVA_HOME=$InstallJdkPath..."
    [System.Environment]::SetEnvironmentVariable("JAVA_HOME", $InstallJdkPath, "Machine")
    $currentPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
    if ($currentPath -notlike "*$InstallJdkPath\bin*") {
        [System.Environment]::SetEnvironmentVariable("Path", "$currentPath;$InstallJdkPath\bin", "Machine")
    }
    # 刷新当前会话
    $env:JAVA_HOME = $InstallJdkPath
    $env:Path = "$env:Path;$InstallJdkPath\bin"

    # 步骤 6：验证
    $installedVersion = Get-InstalledJdkVersion
    if ($installedVersion -eq "25") {
        Write-Log "JDK 25 安装成功" "SUCCESS"
        return $true
    } else {
        Write-Log "JDK 安装后验证失败，版本：$installedVersion" "ERROR"
        return $false
    }
}

# ===== 安装 Maven =====
function Install-Maven {
    Write-Log "开始安装 Maven $MavenVersion..."

    # 步骤 1：检查是否已安装
    if (Test-MavenInstalled) {
        Write-Log "Maven 已安装，跳过" "SUCCESS"
        return $true
    }

    # 步骤 2：优先尝试 winget
    if (Test-WingetAvailable) {
        Write-Log "尝试通过 winget 安装 Apache Maven..."
        try {
            & winget install --id Apache.Maven --accept-package-agreements --accept-source-agreements --silent 2>&1 | Out-Null
            $env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "User")
            if (Test-MavenInstalled) {
                Write-Log "winget 安装 Maven 成功" "SUCCESS"
                return $true
            }
            Write-Log "winget 安装后未检测到 Maven，回退到压缩包方式" "WARN"
        } catch {
            Write-Log "winget 安装 Maven 失败：$_ ，回退到压缩包方式" "WARN"
        }
    }

    # 步骤 3：下载 Apache Maven 压缩包
    Write-Log "从 Apache 官方下载 Maven $MavenVersion..."
    if (-not (Test-Path $TempDir)) {
        New-Item -ItemType Directory -Path $TempDir -Force | Out-Null
    }

    $downloadUrl = "https://dlcdn.apache.org/maven/maven-3/$MavenVersion/binaries/apache-maven-$MavenVersion-bin.zip"
    $zipPath = "$TempDir\maven.zip"

    try {
        Write-Log "下载地址：$downloadUrl"
        Invoke-WebRequest -Uri $downloadUrl -OutFile $zipPath -UseBasicParsing
        Write-Log "下载完成，开始解压..."
    } catch {
        Write-Log "下载 Maven 失败：$_" "ERROR"
        Write-Log "请手动下载 Maven 并解压到 $InstallMavenPath" "ERROR"
        return $false
    }

    # 步骤 4：解压
    $mavenParent = Split-Path $InstallMavenPath -Parent
    if (-not (Test-Path $mavenParent)) {
        New-Item -ItemType Directory -Path $mavenParent -Force | Out-Null
    }
    if (Test-Path $InstallMavenPath) {
        Write-Log "目标目录已存在，先清理：$InstallMavenPath"
        Remove-Item $InstallMavenPath -Recurse -Force
    }

    try {
        Expand-Archive -Path $zipPath -DestinationPath $mavenParent -Force
        # zip 解压后目录名应为 apache-maven-3.9.9，若不一致则重命名
        $extractedDir = Join-Path $mavenParent "apache-maven-$MavenVersion"
        if ((Test-Path $extractedDir) -and ($extractedDir -ne $InstallMavenPath)) {
            Move-Item $extractedDir $InstallMavenPath -Force
        }
        Write-Log "Maven 解压完成：$InstallMavenPath" "SUCCESS"
    } catch {
        Write-Log "解压 Maven 失败：$_" "ERROR"
        return $false
    }

    # 步骤 5：设置环境变量
    Write-Log "设置环境变量 MAVEN_HOME=$InstallMavenPath..."
    [System.Environment]::SetEnvironmentVariable("MAVEN_HOME", $InstallMavenPath, "Machine")
    $currentPath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
    if ($currentPath -notlike "*$InstallMavenPath\bin*") {
        [System.Environment]::SetEnvironmentVariable("Path", "$currentPath;$InstallMavenPath\bin", "Machine")
    }
    $env:MAVEN_HOME = $InstallMavenPath
    $env:Path = "$env:Path;$InstallMavenPath\bin"

    # 步骤 6：验证
    if (Test-MavenInstalled) {
        Write-Log "Maven 安装成功" "SUCCESS"
        return $true
    } else {
        Write-Log "Maven 安装后验证失败" "ERROR"
        return $false
    }
}

# ===== 配置系统环境变量 =====
function Configure-EnvironmentVariables {
    Write-Log "开始配置系统环境变量..."

    $needRefresh = $false  # 标记是否需要刷新当前会话

    # ----- JAVA_HOME -----
    $currentJavaHome = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
    if ($currentJavaHome -ne $InstallJdkPath) {
        if ($currentJavaHome) {
            Write-Log "JAVA_HOME 当前值为 $currentJavaHome，将更新为 $InstallJdkPath" "WARN"
        } else {
            Write-Log "JAVA_HOME 未设置，将设置为 $InstallJdkPath"
        }
        [System.Environment]::SetEnvironmentVariable("JAVA_HOME", $InstallJdkPath, "Machine")
        $env:JAVA_HOME = $InstallJdkPath
        $needRefresh = $true
        Write-Log "JAVA_HOME 已设置" "SUCCESS"
    } else {
        Write-Log "JAVA_HOME 已正确配置，跳过"
    }

    # ----- MAVEN_HOME -----
    $currentMavenHome = [System.Environment]::GetEnvironmentVariable("MAVEN_HOME", "Machine")
    if ($currentMavenHome -ne $InstallMavenPath) {
        if ($currentMavenHome) {
            Write-Log "MAVEN_HOME 当前值为 $currentMavenHome，将更新为 $InstallMavenPath" "WARN"
        } else {
            Write-Log "MAVEN_HOME 未设置，将设置为 $InstallMavenPath"
        }
        [System.Environment]::SetEnvironmentVariable("MAVEN_HOME", $InstallMavenPath, "Machine")
        $env:MAVEN_HOME = $InstallMavenPath
        $needRefresh = $true
        Write-Log "MAVEN_HOME 已设置" "SUCCESS"
    } else {
        Write-Log "MAVEN_HOME 已正确配置，跳过"
    }

    # ----- Path 追加 JDK\bin -----
    $machinePath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
    $jdkBinPath = "$InstallJdkPath\bin"
    if ($machinePath -notlike "*$jdkBinPath*") {
        Write-Log "Path 中未包含 $jdkBinPath，将追加"
        [System.Environment]::SetEnvironmentVariable("Path", "$machinePath;$jdkBinPath", "Machine")
        $needRefresh = $true
        Write-Log "JDK\bin 已加入 Path" "SUCCESS"
    } else {
        Write-Log "Path 已包含 JDK\bin，跳过"
    }

    # ----- Path 追加 MAVEN\bin -----
    $machinePath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
    $mavenBinPath = "$InstallMavenPath\bin"
    if ($machinePath -notlike "*$mavenBinPath*") {
        Write-Log "Path 中未包含 $mavenBinPath，将追加"
        [System.Environment]::SetEnvironmentVariable("Path", "$machinePath;$mavenBinPath", "Machine")
        $needRefresh = $true
        Write-Log "MAVEN\bin 已加入 Path" "SUCCESS"
    } else {
        Write-Log "Path 已包含 MAVEN\bin，跳过"
    }

    # 刷新当前会话的 Path，让本会话立即可用
    if ($needRefresh) {
        $env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "User")
        Write-Log "当前会话环境变量已刷新" "SUCCESS"
    }

    Write-Log "环境变量配置完成" "SUCCESS"
}

# ===== 清理临时文件 =====
function Remove-TempFiles {
    if (Test-Path $TempDir) {
        Write-Log "清理临时文件：$TempDir"
        Remove-Item $TempDir -Recurse -Force -ErrorAction SilentlyContinue
    }
}

# ===== 主流程 =====
function Main {
    Write-Log "========================================" "INFO"
    Write-Log "java-5 依赖安装脚本" "INFO"
    Write-Log "========================================" "INFO"

    # 检查管理员权限（设置系统环境变量需要）
    if (-not (Test-Administrator)) {
        Write-Log "请以管理员身份运行此脚本！" "ERROR"
        Write-Log "右键 PowerShell -> 以管理员身份运行，然后执行 .\install-deps.ps1" "WARN"
        exit 1
    }

    # 安装 JDK 25
    $jdkOk = Install-Jdk25
    if (-not $jdkOk) {
        Write-Log "JDK 25 安装失败，请手动安装后重试" "ERROR"
        Remove-TempFiles
        exit 1
    }

    # 安装 Maven
    $mavenOk = Install-Maven
    if (-not $mavenOk) {
        Write-Log "Maven 安装失败，请手动安装后重试" "ERROR"
        Remove-TempFiles
        exit 1
    }

    # 清理临时文件
    Remove-TempFiles

    # 配置环境变量（无论依赖是新装还是已存在，都确保环境变量正确）
    Configure-EnvironmentVariables

    # 最终验证
    Write-Log "========================================" "INFO"
    Write-Log "依赖安装完成" "SUCCESS"
    Write-Log "========================================" "INFO"
    Write-Log "请重新打开终端窗口，让环境变量生效" "WARN"
    Write-Log "然后运行 .\start.ps1 启动 java-5" "INFO"

    # 打印版本信息
    Write-Log ""
    Write-Log "版本信息："
    & java -version
    Write-Log ""
    & mvn -version
}

# 执行主流程
Main
