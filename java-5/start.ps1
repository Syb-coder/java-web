# ================================================================
# java-5 启动脚本
# 职责：检查依赖 -> 检查端口 -> 清理残留进程 -> 启动应用
# 使用：.\start.ps1
# ================================================================

# ===== 脚本全局配置 =====
$ErrorActionPreference = "Stop"  # 遇错即停
$ProjectName    = "java-5"                           # 子项目名
$ProjectDir     = Split-Path -Parent $MyInvocation.MyCommand.Path  # 脚本所在目录即项目目录
$ServerPort     = 8084                               # 应用监听端口
$RequiredJdkVer = "25"                               # 要求的 JDK 主版本

# ===== 辅助函数：带颜色的日志输出 =====
function Write-Log {
    param(
        [string]$Message,
        [string]$Level = "INFO"
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

# ===== 检查 JDK 是否安装且版本符合要求 =====
function Test-Jdk {
    Write-Log "检查 JDK $RequiredJdkVer..."

    # 优先检查 JAVA_HOME 环境变量
    $javaHome = $env:JAVA_HOME
    if (-not $javaHome) {
        # 也检查系统级环境变量（防止当前会话未继承）
        $javaHome = [System.Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")
    }

    if (-not $javaHome) {
        Write-Log "JAVA_HOME 未设置" "ERROR"
        Write-Log "请先运行 .\install-deps.ps1 安装 JDK 并配置环境变量" "WARN"
        return $false
    }

    Write-Log "JAVA_HOME = $javaHome"

    # 检查 java 命令是否可用
    $javaCmd = Get-Command java -ErrorAction SilentlyContinue
    if (-not $javaCmd) {
        Write-Log "java 命令不可用，PATH 可能未配置" "ERROR"
        Write-Log "请重新打开终端让环境变量生效，或运行 .\install-deps.ps1" "WARN"
        return $false
    }

    # 检查版本号
    # 注意：java -version 输出到 stderr，PowerShell 会把 stderr 当作 ErrorRecord 触发异常
    # 用 cmd /c 包装让 cmd 合并 2>&1，PowerShell 只看到 stdout 字符串
    $output = cmd /c "java -version 2>&1" | Out-String
    if ($output -match 'version\D+(\d+)') {
        $majorVersion = $matches[1]
        if ($majorVersion -ne $RequiredJdkVer) {
            Write-Log "JDK 版本不匹配：当前 $majorVersion，需要 $RequiredJdkVer" "ERROR"
            Write-Log "请运行 .\install-deps.ps1 重新安装 JDK $RequiredJdkVer" "WARN"
            return $false
        }
        Write-Log "JDK $majorVersion 检测通过" "SUCCESS"
        return $true
    }

    Write-Log "无法解析 JDK 版本" "ERROR"
    return $false
}

# ===== 检查 Maven 是否安装 =====
function Test-Maven {
    Write-Log "检查 Maven..."

    # 检查 MAVEN_HOME 环境变量
    $mavenHome = $env:MAVEN_HOME
    if (-not $mavenHome) {
        $mavenHome = [System.Environment]::GetEnvironmentVariable("MAVEN_HOME", "Machine")
    }

    if (-not $mavenHome) {
        Write-Log "MAVEN_HOME 未设置" "WARN"
    } else {
        Write-Log "MAVEN_HOME = $mavenHome"
    }

    # 检查 mvn 命令是否可用
    $mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
    if (-not $mvnCmd) {
        Write-Log "mvn 命令不可用，PATH 可能未配置" "ERROR"
        Write-Log "请重新打开终端让环境变量生效，或运行 .\install-deps.ps1" "WARN"
        return $false
    }

    # 输出 Maven 版本信息便于确认
    # mvn -version 部分输出在 stderr，用 cmd /c 包装避免 PowerShell 误判
    $output = cmd /c "mvn -version 2>&1" | Out-String
    if ($output -match 'Apache Maven (\d+\.\d+\.\d+)') {
        Write-Log "Maven $($matches[1]) 检测通过" "SUCCESS"
        return $true
    }

    Write-Log "无法解析 Maven 版本" "ERROR"
    return $false
}

# ===== 检查项目目录是否存在 =====
function Test-ProjectDir {
    Write-Log "检查项目目录：$ProjectDir"
    if (-not (Test-Path $ProjectDir)) {
        Write-Log "项目目录不存在：$ProjectDir" "ERROR"
        return $false
    }

    # 检查 pom.xml 是否存在
    $pomFile = Join-Path $ProjectDir "pom.xml"
    if (-not (Test-Path $pomFile)) {
        Write-Log "pom.xml 不存在：$pomFile" "ERROR"
        return $false
    }

    Write-Log "项目目录检查通过" "SUCCESS"
    return $true
}

# ===== 检查端口是否被占用 =====
function Test-PortAvailable {
    param([int]$Port)
    Write-Log "检查端口 $Port 是否被占用..."

    # 用 NetTCPConnection 检查端口监听状态
    $connection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($connection) {
        $procId = $connection.OwningProcess | Select-Object -First 1
        $process = Get-Process -Id $procId -ErrorAction SilentlyContinue
        $procName = if ($process) { $process.ProcessName } else { "未知" }
        Write-Log "端口 $Port 已被占用（PID: $procId, 进程: $procName）" "WARN"
        return @{ Available = $false; ProcessId = $procId; ProcessName = $procName }
    }

    Write-Log "端口 $Port 可用" "SUCCESS"
    return @{ Available = $true }
}

# ===== 清理可能残留的 java 进程（占用数据库锁的元凶） =====
function Remove-ResidualProcesses {
    Write-Log "检查残留 java 进程..."

    # 查找占用 java-5 数据目录的 java 进程
    $javaProcesses = Get-Process java -ErrorAction SilentlyContinue
    if (-not $javaProcesses) {
        Write-Log "无残留 java 进程"
        return
    }

    Write-Log "发现 $($javaProcesses.Count) 个 java 进程：" "WARN"
    $javaProcesses | ForEach-Object {
        Write-Log "  PID: $($_.Id), 启动时间: $($_.StartTime)"
    }

    # 询问用户是否终止
    $choice = Read-Host "是否终止这些 java 进程？(y/N)"
    if ($choice -eq "y" -or $choice -eq "Y") {
        $javaProcesses | ForEach-Object {
            try {
                Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
                Write-Log "已终止 PID: $($_.Id)" "SUCCESS"
            } catch {
                Write-Log "终止 PID: $($_.Id) 失败：$_" "WARN"
            }
        }
        Start-Sleep -Seconds 2  # 等待进程完全退出释放文件锁
    } else {
        Write-Log "用户取消终止，可能导致 H2 数据库锁定" "WARN"
    }
}

# ===== 启动应用 =====
function Start-Application {
    Write-Log "========================================" "INFO"
    Write-Log "启动 $ProjectName..." "INFO"
    Write-Log "项目目录：$ProjectDir"
    Write-Log "监听端口：$ServerPort"
    Write-Log "========================================" "INFO"

    # 切换到项目目录执行 Maven 启动命令
    # 为什么用 mvn spring-boot:run：直接调用 Spring Boot Maven 插件，无需先打包
    # 为什么用 Push-Location：执行后能切回原目录，不污染调用者工作目录
    Push-Location $ProjectDir
    try {
        # 设置 MAVEN_OPTS 消除 JDK 25 的 native access 警告
        $env:MAVEN_OPTS = "--enable-native-access=ALL-UNNAMED"
        Write-Log "执行命令：mvn spring-boot:run"
        Write-Log "按 Ctrl+C 可停止应用"
        Write-Log "----------------------------------------" "INFO"
        & mvn spring-boot:run
    } finally {
        Pop-Location
        # 清理 MAVEN_OPTS，避免污染当前会话
        Remove-Item Env:MAVEN_OPTS -ErrorAction SilentlyContinue
    }
}

# ===== 主流程 =====
function Main {
    Write-Log "========================================" "INFO"
    Write-Log "$ProjectName 启动脚本"
    Write-Log "========================================" "INFO"

    # 步骤 1：检查 JDK
    if (-not (Test-Jdk)) {
        Write-Log "JDK 检查失败，启动终止" "ERROR"
        exit 1
    }

    # 步骤 2：检查 Maven
    if (-not (Test-Maven)) {
        Write-Log "Maven 检查失败，启动终止" "ERROR"
        exit 1
    }

    # 步骤 3：检查项目目录
    if (-not (Test-ProjectDir)) {
        Write-Log "项目目录检查失败，启动终止" "ERROR"
        exit 1
    }

    # 步骤 4：检查端口占用
    $portCheck = Test-PortAvailable -Port $ServerPort
    if (-not $portCheck.Available) {
        Write-Log "端口 $ServerPort 被占用，可能是上次启动的进程未退出" "WARN"
        $choice = Read-Host "是否终止占用进程 $($portCheck.ProcessId)？(y/N)"
        if ($choice -eq "y" -or $choice -eq "Y") {
            try {
                Stop-Process -Id $portCheck.ProcessId -Force -ErrorAction SilentlyContinue
                Write-Log "已终止进程 $($portCheck.ProcessId)" "SUCCESS"
                Start-Sleep -Seconds 2  # 等待端口释放
            } catch {
                Write-Log "终止进程失败：$_" "ERROR"
                exit 1
            }
        } else {
            Write-Log "用户取消终止，启动终止" "WARN"
            exit 1
        }
    }

    # 步骤 5：清理残留 java 进程（防止 H2 数据库锁）
    Remove-ResidualProcesses

    # 步骤 6：启动应用
    Start-Application
}

# 执行主流程
Main
