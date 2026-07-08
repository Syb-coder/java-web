@echo off
REM 大学生闲置二手物品交易网站启动脚本（Windows）
REM 使用 Maven Wrapper 编译并以 spring-boot:run 启动
cd /d "%~dp0"
call mvnw.cmd spring-boot:run
