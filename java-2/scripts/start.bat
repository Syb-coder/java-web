@echo off
REM 关闭命令回显，仅显示命令输出结果
setlocal enabledelayedexpansion
REM 启用延迟变量扩展，允许在 for/if 块内使用 !VAR! 实时读取变量

REM 第一步：检测系统 PATH 中是否已有 java 命令
where java >nul 2>&1
REM ERRORLEVEL 为 0 表示找到 java，直接跳转到运行阶段
if !ERRORLEVEL! EQU 0 goto :RUN

REM 第二步：未在 PATH 中找到 java，遍历常见 JDK 安装路径
for %%d in ("C:\Program Files\Eclipse Adoptium\jdk-21*" "C:\Program Files\Java\jdk-21*" "C:\Program Files\Microsoft\jdk-21*" "C:\010\java" "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-21*") do (
    REM 判断该目录下是否存在 bin\java.exe
    if exist "%%~d\bin\java.exe" (
        set "JAVA_HOME=%%~d"
        goto :FOUND_JDK
    )
)

REM 第三步：所有候选路径均未找到 JDK，提示并退出
echo [ERROR] JDK 21 not found.
echo Please install JDK 21 from: https://adoptium.net/temurin/releases/?version=21
pause
exit /b 1

:FOUND_JDK
REM 第四步：找到 JDK，输出路径并将 bin 目录前置到 PATH
echo [INFO] JDK 21 found at !JAVA_HOME!
set "PATH=!JAVA_HOME!\bin;%PATH%"
set "JAVA_HOME=!JAVA_HOME!"

:RUN
REM 第五步：调用同目录下的 PowerShell 启动脚本（核心逻辑由 ps1 完成）
echo [INFO] Starting application...
powershell -ExecutionPolicy Bypass -Command "& '%~dp0start.ps1'"
REM %~dp0 表示当前 bat 文件所在目录（含尾部反斜杠）

pause
REM 等待用户按键，避免窗口闪退
endlocal
