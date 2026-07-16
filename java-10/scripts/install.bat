@echo off
REM java-10 Student Info Management - One-click Install Entry (pure ASCII)
REM Detects JDK, then launches PowerShell install script
setlocal enabledelayedexpansion

REM === JDK detection (same logic as start.bat) ===
where java >nul 2>&1
if !ERRORLEVEL! EQU 0 goto :RUN

for %%d in (
    "C:\Program Files\Eclipse Adoptium\jdk-21*"
    "C:\Program Files\Eclipse Adoptium\jdk-25*"
    "C:\Program Files\Java\jdk-21*"
    "C:\Program Files\Java\jdk-25*"
    "C:\Program Files\Microsoft\jdk-21*"
    "C:\Program Files\Microsoft\jdk-25*"
    "C:\010\java"
    "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-21*"
    "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-25*"
) do (
    if exist "%%~d\bin\java.exe" (
        set "JAVA_HOME=%%~d"
        goto :FOUND_JDK
    )
)

echo [ERROR] JDK 21 not found.
echo Please install JDK 21 from: https://adoptium.net/temurin/releases/?version=21
echo After installing JDK, re-run this script.
pause
exit /b 1

:FOUND_JDK
echo [INFO] JDK 21 found at !JAVA_HOME!
set "PATH=!JAVA_HOME!\bin;%PATH%"
set "JAVA_HOME=!JAVA_HOME!"

:RUN
echo [INFO] Launching install script...
powershell -ExecutionPolicy Bypass -Command "& '%~dp0install.ps1'"
echo.
echo [INFO] Install finished. Press any key to start the application...
pause >nul
powershell -ExecutionPolicy Bypass -Command "& '%~dp0start.ps1'"
pause
endlocal