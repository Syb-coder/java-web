@echo off
REM java-12 Novel Forum - Windows launcher (pure ASCII to avoid GBK/UTF-8 issues)
REM Detects JDK, then launches PowerShell start script
setlocal enabledelayedexpansion

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
pause
exit /b 1

:FOUND_JDK
echo [INFO] JDK 21 found at !JAVA_HOME!
set "PATH=!JAVA_HOME!\bin;%PATH%"
set "JAVA_HOME=!JAVA_HOME!"

:RUN
echo [INFO] Starting application...
powershell -ExecutionPolicy Bypass -Command "& '%~dp0start.ps1' %*"
pause
endlocal
