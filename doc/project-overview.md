# Phantoms Backend 项目说明

> 本文按当前源码和构建配置整理。第三方接口、部署地址、定时任务和数据库配置可能随环境变化；以源码、环境变量和实际部署配置为准。

## 1. 项目简介

`phantoms-backend` 是一个围绕《最终幻想 XIV》（Final Fantasy XIV，简称 FF14）玩家社区构建的 Java 后端服务。

它主要负责为社区前端提供 HTTP API，同时连接 FF14 相关数据服务、石之家（Rising Stones）、QQ 机器人、数据库和缓存服务，支持游戏信息查询、社区消息转发、房屋与招募信息提醒等功能。

这个项目本质上是一个综合型 Spring Boot 后端，不是独立的桌面应用。项目可以通过 Docker 构建并以 Java 服务的形式运行。

## 2. 主要功能

### 2.1 FF14 数据查询

- 对接 FFXIV API，提供角色、游戏资料等相关数据查询。
- 提供 FF14 新闻及相关信息接口。
- 为前端页面提供 XIVAPI、石之家等数据查询能力。

### 2.2 石之家账号与玩家数据

- 对接石之家 API。
- 支持石之家登录流程及登录状态检查。
- 管理 DaoYu key 等登录凭据相关配置。
- 获取玩家活跃状态和房屋信息。
- 定时检查长期未登录且拥有房屋的玩家，并发送提醒。
- 支持每日签到等定时任务。

### 2.3 房屋出售提醒

- 定时获取 FF14 房屋出售数据。
- 筛选符合条件的房屋信息。
- 将可购买的房屋信息推送到指定 QQ 群或频道。

### 2.4 招募信息提醒

- 从 Littlenightmare 获取 FF14 招募信息。
- 将招募数据保存到数据库。
- 对符合条件的招募信息发送 QQ 群提醒。
- 使用缓存避免短时间内重复发送相同消息。

### 2.5 QQ 群消息与在线聊天室

- 通过 NapCat QQ 获取和发送 QQ 消息。
- 对接 OneBot 接口。
- 使用 WebSocket 实现实时聊天记录推送。
- 支持通过网页参与 QQ 聊天。
- 缓存 QQ 用户头像、昵称等信息，减少重复请求。

### 2.6 数据存储与同步

- 使用 PostgreSQL 作为主数据源。
- 使用 MySQL 作为备份数据源。
- 定时将主数据库数据同步或备份到备份数据库。
- 使用 Redis 缓存外部接口数据、消息状态和临时配置。
- 对接 D1、LeanCloud、Supabase 等外部数据服务。

### 2.7 邮件、健康检查和管理接口

- 通过 SMTP 发送邮件。
- 提供健康检查接口和定时健康检查日志。
- 提供账号、系统配置、房屋通知等管理页面。
- 提供 Swagger/OpenAPI 接口文档支持。

## 3. 技术栈

| 类型 | 技术 |
| --- | --- |
| 编程语言 | Java 21 |
| 应用框架 | Spring Boot 3.1.4 |
| Web 接口 | Spring MVC |
| 实时通信 | Spring WebSocket、STOMP |
| ORM / 数据访问 | Spring Data JPA、MyBatis-Plus |
| 主数据库 | PostgreSQL |
| 备份数据库 | MySQL |
| 缓存 | Redis、Jedis |
| 模板和页面 | Thymeleaf、静态 HTML |
| API 文档 | SpringDoc OpenAPI |
| 邮件 | Spring Boot Mail、SMTP |
| HTTP 客户端 | Apache HttpClient、OkHttp |
| JSON 处理 | Jackson、Fastjson |
| 二维码 | ZXing |
| 构建工具 | Maven |
| 部署方式 | Docker |
| 运行环境 | Amazon Corretto 21 |

## 4. 项目结构

```text
phantoms-backend/
├── src/main/java/com/phantoms/phantomsbackend/
│   ├── controller/       HTTP 接口控制器
│   ├── service/          业务服务接口
│   ├── service/impl/     业务服务实现
│   ├── service/scheduler/定时任务
│   ├── repository/       数据库访问接口
│   ├── pojo/              实体类和数据传输对象
│   ├── common/            通用工具、客户端和辅助类
│   ├── config/             Spring 和应用配置
│   └── exception/          全局异常处理
├── src/main/resources/
│   ├── static/             静态管理页面
│   ├── templates/           Thymeleaf 模板
│   ├── mappers/             MyBatis 映射文件
│   ├── application.yml      应用配置
│   └── log4j2.xml            日志配置
├── src/test/                测试代码和 WebSocket 测试页面
├── doc/                     项目文档、流程图和 API 示例
├── lib/                     本地依赖包
├── pom.xml                  Maven 项目配置
└── Dockerfile               Docker 多阶段构建配置
```

## 5. 主要接口模块

项目中的控制器按业务划分，常见模块包括：

- `XIVAPIController`：FF14 游戏数据查询。
- `RisingStonesController`：石之家相关数据接口。
- `RisingStonesSigninController`：石之家登录或签到相关接口。
- `RecruitmentController`：招募信息接口。
- `HousingNotifyController`：房屋出售通知配置与接口。
- `OneBotController`：OneBot / QQ 机器人接口。
- `WebSocketController`：实时通信接口。
- `MessageController`：消息相关接口。
- `EmailController`：邮件发送接口。
- `ConfigController`：系统配置接口。
- `PingController`：服务状态和连通性检查。

具体接口路径和请求参数应以代码及项目中的 Postman 集合为准。

## 6. 定时任务

项目包含多个后台定时任务，用于执行不适合由用户请求直接触发的工作，例如：

- FF14 新闻定时推送。
- FF14 房屋出售数据检查。
- 招募数据抓取和提醒。
- 石之家玩家不活跃提醒。
- 每日签到。
- DaoYu key 或石之家登录状态监控。
- PostgreSQL 到 MySQL 的数据同步。
- 服务健康检查。

定时任务通常位于：

```text
src/main/java/com/phantoms/phantomsbackend/service/scheduler/
```

## 7. 外部依赖

完整运行通常需要根据环境配置以下服务或账号：

- PostgreSQL 数据库。
- MySQL 数据库，用于备份或兼容相关业务。
- Redis 服务。
- FF14 / XIVAPI 数据服务。
- 石之家账号或登录相关配置。
- NapCat QQ 和 OneBot HTTP 服务。
- Littlenightmare API。
- SMTP 邮件服务器。
- D1、LeanCloud 或 Supabase 服务。

这些服务的地址、账号和密钥应通过环境变量或本地配置提供，不应将真实凭据提交到代码仓库。

## 8. 本地运行

### 8.1 环境要求

- JDK 21。
- Maven 3.9 或更高版本。
- 可访问的 PostgreSQL、MySQL 和 Redis 服务。部分功能还需要外部 API、QQ/OneBot、SMTP 或石之家登录状态。
- 已配置项目所需的环境变量。

### 8.2 Maven 构建

在项目根目录执行：

```bash
mvn clean package
```

跳过测试构建：

```bash
mvn clean package -DskipTests
```

构建成功后，通常会生成：

```text
target/phantoms-backend-1.0-SNAPSHOT.jar
```

### 8.3 启动应用

```bash
java -jar target/phantoms-backend-1.0-SNAPSHOT.jar
```

`src/main/resources/application.yml` 当前配置应用端口为 `8081`。Dockerfile 的 `EXPOSE 8080` 与该配置不一致，`EXPOSE` 只是镜像元数据，不会自动改变 Spring Boot 监听端口；部署时应明确设置平台端口和 `server.port`，避免依赖该不一致配置。

## 9. Docker 部署

项目提供了多阶段 Dockerfile：

1. 使用 Maven 和 Amazon Corretto 21 构建 Spring Boot JAR。
2. 使用 Amazon Corretto 21 Alpine 镜像运行应用。
3. 安装中文字体和通用字体，支持二维码、图片或文本渲染相关功能。
4. Dockerfile 元数据声明 `8080`，但当前 Spring 配置默认监听 `8081`，部署时必须统一二者。

构建镜像：

```bash
docker build -t phantoms-backend .
```

运行容器时，需要注入数据库、Redis、第三方 API 和邮件服务等必要配置。

## 10. 相关文档

- `README.md`：项目功能列表和 Git LFS 维护说明。
- `doc/how-to-login.md`：登录相关说明。
- `doc/daoyu_login_flow.md`：石之家登录流程。
- `doc/risingStones API samples.md`：石之家 API 示例。
- `doc/ffxiv-signin-helper.postman_collection.json`：接口调试用 Postman 集合。
- `doc/Phantoms.jpg`、`doc/Phantoms.drawio`：系统架构图。
- `doc/TODO.txt`：历史任务和待办记录。

旧版主页和新版 Vue 前端分别位于同级目录 `FFXIV_Phantoms_MainPage/` 和 `ffxiv_phantoms_mainpage_vue/`，并非本 Maven 项目的子模块。

## 11. 注意事项

- `.env`、配置文件和数据库备份中可能包含敏感信息，使用和分享项目时应检查是否存在真实密钥、Cookie、Token 或账号信息。
- 石之家、QQ 机器人和其他第三方接口依赖外部登录状态，相关功能可能因 Cookie、key 过期或第三方接口变化而失效。
- 当前源码中叨鱼登录状态监控为每 2 小时执行一次；DaoYu key 缓存、房屋通知和新闻相关任务多为每 5 分钟，数据同步为每 10 分钟，健康检查为每 1 分钟。具体以 `@Scheduled` 注解为准，注释中的旧周期可能不准确。
- 招募抓取等功能可能受到 Cloudflare 或第三方服务访问限制。
- 邮件发送是否可用取决于部署环境到 SMTP 服务器的网络连通性。
- 项目中的 `src/archive/` 主要保存历史代码，不应默认视为当前生产逻辑。

## 12. 一句话总结

这是一个为 FF14 玩家社区提供数据查询、QQ 群机器人、实时聊天室、房屋和招募提醒、账号管理以及数据同步能力的综合型 Spring Boot 后端服务。
