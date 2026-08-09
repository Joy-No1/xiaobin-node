# xiaobin-admin — XXL-Job 调度中心

> 内嵌官方 `xxl-job-admin 3.4.2` 源码（包名保持 `com.xxl.job.admin`，jakarta 版，兼容 Spring Boot 3.3.7）。
> 与 `xiaobin-job-executor`（执行器）配合：执行器注册到本调度中心，由调度中心统一触发任务。

## 启动前准备

### 1. 数据库

执行 `docs/init-job.sql`（建 `xiaobin-job` 库 + 8 张 `xxl_job_*` 表 + 默认管理员）。

### 2. Nacos 数据源配置 ⭐

在 Nacos `xiaobin` 命名空间新建配置 `xiaobin-admin.yaml`（group=`DEFAULT_GROUP`）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/xiaobin-job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
    username: root
    password: 你的数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      minimum-idle: 10
      maximum-pool-size: 30
      auto-commit: true
      idle-timeout: 30000
      pool-name: HikariCP
      max-lifetime: 900000
      connection-timeout: 10000
      connection-test-query: SELECT 1
      validation-timeout: 1000
```

> 该配置由 `xiaobin-admin/src/main/resources/application.yml` 中的
> `spring.config.import: nacos:xiaobin-admin.yaml?group=DEFAULT_GROUP&namespace=xiaobin` 自动拉取。
> 数据库名是 `xiaobin-job`（不是官方默认的 `xxl_job`）。

## 启动

```bash
# 在 IDE 中运行 XxlJobAdminApplication（包 com.xxl.job.admin）
# 或打包运行：
mvn -pl xiaobin-admin -am package -DskipTests
java -jar xiaobin-admin/target/xiaobin-admin-0.0.1-SNAPSHOT.jar
```

- 访问地址：`http://127.0.0.1:8088/xxl-job-admin`
- 默认账号：`admin / 123456`

## 配置说明

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `server.port` | `8088` | 调度中心端口（与执行器 `xxl.job.admin.addresses` 一致） |
| `server.servlet.context-path` | `/xxl-job-admin` | 访问前缀 |
| `xxl.job.accessToken` | `default_token` | **必须与执行器一致**，否则注册/回调失败 |
| `xxl.job.i18n` | `zh_CN` | 界面语言 |
| `spring.mail.*` | 占位 | 任务失败邮件告警，如不使用可忽略 |

## 与执行器联调

`xiaobin-job-executor` 的 `application.yml` 已配置：

```yaml
xxl:
  job:
    admin:
      addresses: http://127.0.0.1:8088/xxl-job-admin   # 指向本调度中心
    executor:
      appname: xiaobin-job
      port: 9999
    accessToken: default_token                          # 与调度中心一致
```

启动顺序：数据库 → Nacos → `xiaobin-admin` → `xiaobin-job-executor`。
执行器启动后会自动注册到调度中心（执行器管理页面可见 `xiaobin-job`），即可在调度中心创建/触发任务。

## 模块结构

```
xiaobin-admin/
├── pom.xml                        # web/freemarker/mail/actuator/mybatis/mysql/xxl-job-core/nacos-config
└── src/main/
    ├── java/com/xxl/job/admin/    # 官方 xxl-job-admin 源码（business + framework 结构）
    └── resources/
        ├── application.yml        # 本地配置（端口/context-path/freemarker/xxl.job.*），数据源来自 Nacos
        ├── mapper/**/*Mapper.xml  # 调度中心 MyBatis SQL
        ├── templates/             # Freemarker 前端页面
        ├── static/                # 前端静态资源
        └── i18n/                  # 国际化
```

> 注意：调度中心独立使用 `mybatis-spring-boot-starter`（不是项目其它模块的 MyBatis-Plus），
> 且不依赖 `xiaobin-common`，避免共享的 MyBatis-Plus/拦截器配置影响调度中心。
