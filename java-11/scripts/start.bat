@echo off
chcp 65001 >nul 2>&1
echo ========================================
echo   java-11 - ACG Discussion Website
echo ========================================
echo.
echo Starting, please wait...
echo If this is the first run, Maven may download
echo dependencies which can take several minutes.
echo.
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start.ps1"
echo.
echo ----------------------------------------
echo Script ended. Press any key to close.
echo ----------------------------------------
pause >nul
