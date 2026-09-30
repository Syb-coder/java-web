# Java Web 项目集

> Java Web 课程设计与实践项目集合，集中收录多个可独立运行的 Spring Boot 应用，覆盖图书管理、交易平台、内容社区、学习平台等常见业务场景。

## 项目简介

本仓库用于整理和维护多个 Java Web 示例项目。每个 `java-*` 目录都是一个相对独立的 Maven 项目，拥有自己的 `pom.xml`、源码、配置和启动脚本，适合学习 Spring Boot 分层架构、数据库建模、表单校验和前后端协作。

## 项目目录

| 目录 | 说明 |
| --- | --- |
| `java-1` | 校园图书借阅管理网站（JDK 21、Spring Boot 4.1、H2，默认端口 8089） |
| `java-2` ~ `java-12` | 其他独立的 Java Web 课程设计与业务案例，具体功能以各目录源码和文档为准 |
| `images/` | 系统架构、模块、流程及数据库设计图 |
| `PROJECTS.md` | 项目登记表 |

部分目录包含项目论文、设计文档或演示资源，便于了解需求和实现思路。

## 技术栈

- Java 21（各项目以自身 `pom.xml` 为准）
- Spring Boot 4.1
- Spring MVC / JPA / Validation
- Maven Wrapper（`mvnw` / `mvnw.cmd`）
- H2 或 MySQL
- Thymeleaf、HTML、CSS、JavaScript（按项目配置）

## 快速开始

进入任意项目目录后运行：

```bash
cd java-1
# Linux / macOS
./mvnw spring-boot:run
# Windows
mvnw.cmd spring-boot:run
```

也可以先打包再运行：

```bash
./mvnw clean package
java -jar target/*.jar
```

首次运行前请阅读对应目录的配置文件和项目说明。不同项目的端口、数据库、默认账号和启动类可能不同，不能假定所有项目使用相同配置。

## 开发建议

1. 使用 JDK 21 和 IntelliJ IDEA 导入目标目录。
2. 以目标目录中的 `pom.xml` 为准安装依赖，不要在仓库根目录执行跨项目构建。
3. 数据库相关配置请先检查 `src/main/resources` 下的 `application*.yml` 或 `application*.properties`。
4. 不要将真实密码、密钥、生产数据库配置或运行日志提交到仓库。

## 相关文档

- [项目登记表](PROJECTS.md)
- [架构图](images/architecture.png)
- [模块图](images/modules.png)
- [借阅流程图](images/borrow_flow.png)
- [数据库 ER 图](images/er_diagram.png)

## 免责声明

本仓库主要用于学习、课程设计和技术交流。运行具体项目时请遵守项目依赖的第三方服务、素材及数据的许可要求。使用前请自行审查代码和配置的安全性。
