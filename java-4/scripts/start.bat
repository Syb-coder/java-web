@echo off
setlocal enabledelayedexpansion
for %%d in (
    "C:\Program Files\Eclipse Adoptium\jdk-21*"
    "C:\Program Files\Java\jdk-21*"
    "C:\Program Files\Microsoft\jdk-21*"
    "C:\010\java"
    "C:\010\jdk*"
    "%LOCALAPPDATA%\Programs\Eclipse Adoptium\jdk-21*"
) do (
    if exist "%%~d\bin\java.exe" (
        set "JAVA_HOME=%%~d"
        goto :FOUND_JDK
    )
)
echo [ERROR] JDK 17+ not found.
echo Please install JDK 21+ from: https://adoptium.net/temurin/releases/?version=21
pause
exit /b 1
:FOUND_JDK
echo [INFO] JDK found at !JAVA_HOME!
set "PATH=!JAVA_HOME!\bin;%PATH%"
set "JAVA_HOME=!JAVA_HOME!"
:RUN
echo [INFO] Starting application...
powershell -ExecutionPolicy Bypass -Command "& '%~dp0start.ps1'"
pause
endlocal
