@echo off
setlocal enabledelayedexpansion

REM Check if java is in PATH
where java >nul 2>&1
if !ERRORLEVEL! EQU 0 goto :RUN

REM Search common JDK 21 installation directories
set "JAVA_HOME="
for %%d in (
    "C:\Program Files\Eclipse Adoptium\jdk-21*"
    "C:\Program Files\Java\jdk-21*"
    "C:\Program Files\Microsoft\jdk-21*"
    "C:\010\java"
    "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-21*"
) do (
    if exist "%%~d\bin\java.exe" (
        set "JAVA_HOME=%%~d"
        goto :FOUND_JDK
    )
)

echo [ERROR] JDK 21 not found.
echo Please install JDK 21 from: https://adoptium.net/temurin/releases/?version=21
echo Or place it in C:\010\java\ and re-run.
pause
exit /b 1

:FOUND_JDK
echo [INFO] JDK 21 found at !JAVA_HOME!
set "PATH=!JAVA_HOME!\bin;%PATH%"
set "JAVA_HOME=!JAVA_HOME!"

:RUN
echo [INFO] Starting application...
powershell -ExecutionPolicy Bypass -Command "& '%~dp0start.ps1'"
pause
endlocal